package com.neowallet.finance.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neowallet.finance.dto.*;
import com.neowallet.finance.entity.Bill;
import com.neowallet.finance.entity.BillStatusHistory;
import com.neowallet.finance.entity.FinancialOverview;
import com.neowallet.finance.repository.BillRepository;
import com.neowallet.finance.repository.BillStatusHistoryRepository;
import com.neowallet.finance.repository.FinancialOverviewRepository;
import com.neowallet.identity.entity.Family;
import com.neowallet.identity.entity.FamilyMember;
import com.neowallet.identity.entity.User;
import com.neowallet.identity.repository.FamilyMemberRepository;
import com.neowallet.identity.repository.FamilyRepository;
import com.neowallet.identity.repository.UserRepository;
import com.neowallet.identity.service.AuditService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BillService {

    private final BillRepository billRepository;
    private final BillStatusHistoryRepository billStatusHistoryRepository;
    private final FinancialOverviewRepository financialOverviewRepository;
    private final FamilyMemberRepository familyMemberRepository;
    private final FamilyRepository familyRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;
    private final ObjectMapper objectMapper;

    private static final DateTimeFormatter PERIOD_FMT = DateTimeFormatter.ofPattern("yyyy-MM");

    @Transactional
    public BillResponse createBill(UUID userId, Optional<UUID> familyId, CreateBillRequest request) {
        familyId.ifPresent(fid -> ensureFamilyMemberCanCreate(userId, fid));
        validateCreate(request);

        if (existsName(userId, familyId, request.name())) {
            throw new IllegalStateException("Bill name already exists");
        }

        User user = userRepository.getReferenceById(userId);
        Bill bill = new Bill();
        bill.setUser(user);
        bill.setName(request.name().trim());
        bill.setAmount(request.amount().setScale(2, RoundingMode.HALF_UP));
        bill.setCurrency(request.currency() != null ? request.currency().toUpperCase() : "USD");
        bill.setDueDate(request.dueDate());
        bill.setCategory(request.category());
        bill.setIsRecurring(request.isRecurring() != null ? request.isRecurring() : false);
        bill.setRecurringPeriod(request.recurringPeriod() != null ? request.recurringPeriod().toUpperCase() : null);
        bill.setVendor(request.vendor());
        bill.setNotes(request.notes());

        if (familyId.isPresent()) {
            Family family = familyRepository.getReferenceById(familyId.get());
            bill.setFamily(family);
            FamilyMember member = familyMemberRepository
                    .findByFamily_FamilyIdAndUser_UserIdAndLeftAtIsNull(familyId.get(), userId)
                    .orElseThrow(() -> new AccessDeniedException("Not a family member"));
            bill.setFamilyMember(member);
        }

        recomputeStatus(bill);
        bill = billRepository.saveAndFlush(bill);
        recordStatusHistory(bill, null, bill.getStatus(), user);
        adjustPendingPayments(userId, familyId, bill.getPeriod(), bill.getAmount());

        auditService.record(userId, "USER", "BILL_CREATED", "BILL", bill.getBillId(), "SUCCESS",
                toJson(Map.of("name", bill.getName(), "amount", bill.getAmount().toString(),
                        "dueDate", bill.getDueDate().toString())));

        return toResponse(bill);
    }

    @Transactional(readOnly = true)
    public BillListResponse listBills(UUID userId, Optional<UUID> familyId, Optional<String> status,
                                       Optional<String> category, Optional<LocalDate> dueDateFrom,
                                       Optional<LocalDate> dueDateTo, int page, int limit, String sort) {
        familyId.ifPresent(fid -> ensureFamilyMember(userId, fid));

        UUID queryUserId = familyId.isEmpty() ? userId : null;
        UUID queryFamilyId = familyId.orElse(null);

        Pageable pageable = buildPageable(page, limit, sort);
        Page<Bill> pageResult = billRepository.findBills(
                queryUserId, queryFamilyId, status.orElse(null), category.orElse(null),
                dueDateFrom.orElse(null), dueDateTo.orElse(null), pageable);

        List<BillResponse> bills = pageResult.getContent().stream()
                .map(this::toResponse)
                .toList();

        return BillListResponse.builder()
                .bills(bills)
                .pagination(BillListResponse.PaginationDto.builder()
                        .page(page)
                        .limit(limit)
                        .totalCount((int) pageResult.getTotalElements())
                        .pageCount(pageResult.getTotalPages())
                        .build())
                .build();
    }

    @Transactional(readOnly = true)
    public BillResponse getBill(UUID userId, UUID billId, Optional<UUID> familyId) {
        Bill bill = findAndEnsureAccess(userId, billId, familyId);
        return toResponse(bill);
    }

    @Transactional
    public BillResponse updateBill(UUID userId, UUID billId, Optional<UUID> familyId, UpdateBillRequest request) {
        Bill bill = findAndEnsureAccess(userId, billId, familyId);
        ensureWriteAccess(userId, familyId, bill);
        validateUpdate(request);

        String originalPeriod = bill.getPeriod();
        BigDecimal originalAmount = bill.getAmount();
        String originalStatus = bill.getStatus();

        boolean amountChanged = false;
        boolean periodChanged = false;

        if (request.name() != null && !request.name().isBlank()) {
            if (!bill.getName().equals(request.name().trim())
                    && existsName(userId, familyId, request.name().trim())) {
                throw new IllegalStateException("Bill name already exists");
            }
            bill.setName(request.name().trim());
        }
        if (request.amount() != null) {
            BigDecimal newAmount = request.amount().setScale(2, RoundingMode.HALF_UP);
            amountChanged = originalAmount.compareTo(newAmount) != 0;
            bill.setAmount(newAmount);
        }
        if (request.currency() != null) {
            bill.setCurrency(request.currency().toUpperCase());
        }
        if (request.dueDate() != null) {
            String newPeriod = request.dueDate().format(PERIOD_FMT);
            periodChanged = !originalPeriod.equals(newPeriod);
            bill.setDueDate(request.dueDate());
        }
        if (request.category() != null) {
            bill.setCategory(request.category());
        }
        if (request.isRecurring() != null) {
            bill.setIsRecurring(request.isRecurring());
        }
        if (request.recurringPeriod() != null) {
            bill.setRecurringPeriod(request.recurringPeriod().toUpperCase());
        }
        if (request.vendor() != null) {
            bill.setVendor(request.vendor());
        }
        if (request.notes() != null) {
            bill.setNotes(request.notes());
        }

        recomputeStatus(bill);
        bill = billRepository.saveAndFlush(bill);

        if (!bill.getStatus().equals(originalStatus)) {
            recordStatusHistory(bill, originalStatus, bill.getStatus(), bill.getUser());
        }

        if ("PENDING".equals(bill.getStatus()) && (amountChanged || periodChanged)) {
            adjustPendingPayments(userId, familyId, originalPeriod, originalAmount.negate());
            adjustPendingPayments(userId, familyId, bill.getPeriod(), bill.getAmount());
        } else if (amountChanged && "PENDING".equals(bill.getStatus())) {
            adjustPendingPayments(userId, familyId, bill.getPeriod(), bill.getAmount().subtract(originalAmount));
        }

        auditService.record(userId, "USER", "BILL_UPDATED", "BILL", bill.getBillId(), "SUCCESS",
                toJson(Map.of("name", bill.getName(), "amount", bill.getAmount().toString())));

        return toResponse(bill);
    }

    @Transactional
    public void deleteBill(UUID userId, UUID billId, Optional<UUID> familyId) {
        Bill bill = findAndEnsureAccess(userId, billId, familyId);
        ensureWriteAccess(userId, familyId, bill);

        if ("PENDING".equals(bill.getStatus()) || "OVERDUE".equals(bill.getStatus())) {
            adjustPendingPayments(userId, familyId, bill.getPeriod(), bill.getAmount().negate());
        }

        bill.setDeletedAt(Instant.now());
        bill = billRepository.saveAndFlush(bill);

        recordStatusHistory(bill, bill.getStatus(), "CANCELLED", bill.getUser());

        auditService.record(userId, "USER", "BILL_DELETED", "BILL", bill.getBillId(), "SUCCESS",
                toJson(Map.of("name", bill.getName())));
    }

    @Transactional
    public MarkPaidResponse markBillPaid(UUID userId, UUID billId, Optional<UUID> familyId, MarkPaidRequest request) {
        Bill bill = findAndEnsureAccess(userId, billId, familyId);
        ensureWriteAccess(userId, familyId, bill);

        if ("PAID".equals(bill.getStatus())) {
            throw new IllegalStateException("Bill is already paid");
        }

        String originalStatus = bill.getStatus();

        LocalDate paidDate = request != null && request.paidDate() != null
                ? request.paidDate()
                : LocalDate.now();
        if (paidDate.isAfter(LocalDate.now())) {
            throw new ValidationException("Paid date cannot be in the future");
        }

        String paymentMethod = request != null ? request.paymentMethod() : null;
        String notes = request != null ? request.notes() : null;

        bill.setStatus("PAID");
        bill.setPaidDate(paidDate);
        bill.setPaymentMethod(paymentMethod);
        if (notes != null) {
            bill.setNotes(notes);
        }
        bill = billRepository.saveAndFlush(bill);

        recordStatusHistory(bill, originalStatus, "PAID", bill.getUser());
        adjustPendingPayments(userId, familyId, bill.getPeriod(), bill.getAmount().negate());

        auditService.record(userId, "USER", "BILL_PAID", "BILL", bill.getBillId(), "SUCCESS",
                toJson(Map.of("name", bill.getName(), "amount", bill.getAmount().toString(),
                        "paidDate", paidDate.toString(), "paymentMethod", String.valueOf(paymentMethod))));

        return MarkPaidResponse.builder()
                .billId(bill.getBillId().toString())
                .status(bill.getStatus())
                .paidDate(bill.getPaidDate())
                .paymentMethod(bill.getPaymentMethod())
                .notes(bill.getNotes())
                .updatedAt(bill.getUpdatedAt().toString())
                .build();
    }

    private void validateCreate(CreateBillRequest request) {
        if (request.name() == null || request.name().isBlank()) {
            throw new ValidationException("Name is required");
        }
        if (request.name().length() > 100) {
            throw new ValidationException("Name must be 100 characters or less");
        }
        if (request.amount() == null || request.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Amount must be greater than 0");
        }
        if (request.dueDate() == null) {
            throw new ValidationException("Due date is required");
        }
        if (request.currency() != null && request.currency().length() != 3) {
            throw new ValidationException("Currency must be 3 characters");
        }
        if (request.recurringPeriod() != null
                && !List.of("MONTHLY", "WEEKLY", "YEARLY").contains(request.recurringPeriod().toUpperCase())) {
            throw new ValidationException("Recurring period must be MONTHLY, WEEKLY, or YEARLY");
        }
    }

    private void validateUpdate(UpdateBillRequest request) {
        if (request.name() != null && request.name().length() > 100) {
            throw new ValidationException("Name must be 100 characters or less");
        }
        if (request.amount() != null && request.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Amount must be greater than 0");
        }
        if (request.currency() != null && request.currency().length() != 3) {
            throw new ValidationException("Currency must be 3 characters");
        }
        if (request.recurringPeriod() != null
                && !List.of("MONTHLY", "WEEKLY", "YEARLY").contains(request.recurringPeriod().toUpperCase())) {
            throw new ValidationException("Recurring period must be MONTHLY, WEEKLY, or YEARLY");
        }
    }

    private boolean existsName(UUID userId, Optional<UUID> familyId, String name) {
        if (familyId.isPresent()) {
            return billRepository.existsByFamily_FamilyIdAndNameAndDeletedAtIsNull(familyId.get(), name);
        }
        return billRepository.existsByUser_UserIdAndNameAndDeletedAtIsNull(userId, name);
    }

    private Bill findAndEnsureAccess(UUID userId, UUID billId, Optional<UUID> familyId) {
        Bill bill = billRepository.findByBillIdAndDeletedAtIsNull(billId)
                .orElseThrow(() -> new EntityNotFoundException("Bill not found"));

        if (familyId.isPresent()) {
            if (bill.getFamily() == null || !bill.getFamily().getFamilyId().equals(familyId.get())) {
                throw new AccessDeniedException("Not authorized");
            }
            ensureFamilyMember(userId, familyId.get());
        } else {
            if (bill.getUser() == null || !bill.getUser().getUserId().equals(userId)) {
                throw new AccessDeniedException("Not authorized");
            }
        }

        return bill;
    }

    private void ensureWriteAccess(UUID userId, Optional<UUID> familyId, Bill bill) {
        if (familyId.isPresent()) {
            FamilyMember member = familyMemberRepository
                    .findByFamily_FamilyIdAndUser_UserIdAndLeftAtIsNull(familyId.get(), userId)
                    .orElseThrow(() -> new AccessDeniedException("Not authorized"));
            if (!"OWNER".equals(member.getRole())) {
                throw new AccessDeniedException("Not authorized");
            }
        } else {
            if (bill.getUser() == null || !bill.getUser().getUserId().equals(userId)) {
                throw new AccessDeniedException("Not authorized");
            }
        }
    }

    private void ensureFamilyMember(UUID userId, UUID familyId) {
        if (!familyMemberRepository.existsByFamily_FamilyIdAndUser_UserIdAndLeftAtIsNull(familyId, userId)) {
            throw new AccessDeniedException("Not a family member");
        }
    }

    private void ensureFamilyMemberCanCreate(UUID userId, UUID familyId) {
        FamilyMember member = familyMemberRepository
                .findByFamily_FamilyIdAndUser_UserIdAndLeftAtIsNull(familyId, userId)
                .orElseThrow(() -> new AccessDeniedException("Not a family member"));
        if ("RESTRICTED".equals(member.getRole())) {
            throw new AccessDeniedException("Restricted members cannot create bills");
        }
    }

    private Pageable buildPageable(int page, int limit, String sort) {
        String[] parts = sort != null && !sort.isBlank() ? sort.split(":") : new String[]{"dueDate", "asc"};
        String field = parts[0];
        Sort.Direction dir = parts.length > 1 && "desc".equalsIgnoreCase(parts[1])
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        return PageRequest.of(page - 1, limit, Sort.by(dir, field));
    }

    private void recomputeStatus(Bill bill) {
        if ("PAID".equals(bill.getStatus())) {
            return;
        }
        if (bill.getDueDate().isBefore(LocalDate.now())) {
            bill.setStatus("OVERDUE");
        } else {
            bill.setStatus("PENDING");
        }
    }

    private String computeStatus(Bill bill) {
        if ("PAID".equals(bill.getStatus())) {
            return "PAID";
        }
        if (bill.getDueDate().isBefore(LocalDate.now())) {
            return "OVERDUE";
        }
        return "PENDING";
    }

    private void recordStatusHistory(Bill bill, String oldStatus, String newStatus, User changedBy) {
        BillStatusHistory history = new BillStatusHistory();
        history.setBill(bill);
        history.setOldStatus(oldStatus);
        history.setNewStatus(newStatus);
        history.setChangedBy(changedBy);
        billStatusHistoryRepository.save(history);
    }

    private void adjustPendingPayments(UUID userId, Optional<UUID> familyId, String period, BigDecimal delta) {
        FinancialOverview overview = resolveOverview(userId, familyId, period).orElse(null);
        if (overview == null) {
            return;
        }
        overview.setPendingPayments(overview.getPendingPayments().add(delta).setScale(2, RoundingMode.HALF_UP));
        overview.setUpdatedAt(Instant.now());
        financialOverviewRepository.save(overview);
    }

    private Optional<FinancialOverview> resolveOverview(UUID userId, Optional<UUID> familyId, String period) {
        return familyId
                .flatMap(fid -> financialOverviewRepository.findByFamily_FamilyIdAndPeriod(fid, period))
                .or(() -> financialOverviewRepository.findByUser_UserIdAndPeriod(userId, period));
    }

    private BillResponse toResponse(Bill bill) {
        String status = computeStatus(bill);
        return BillResponse.builder()
                .billId(bill.getBillId().toString())
                .userId(bill.getUser() != null ? bill.getUser().getUserId().toString() : null)
                .familyId(bill.getFamily() != null ? bill.getFamily().getFamilyId().toString() : null)
                .name(bill.getName())
                .amount(bill.getAmount())
                .currency(bill.getCurrency())
                .dueDate(bill.getDueDate())
                .category(bill.getCategory())
                .isRecurring(bill.getIsRecurring())
                .recurringPeriod(bill.getRecurringPeriod())
                .vendor(bill.getVendor())
                .notes(bill.getNotes())
                .status(status)
                .paidDate(bill.getPaidDate())
                .paymentMethod(bill.getPaymentMethod())
                .createdAt(bill.getCreatedAt().toString())
                .updatedAt(bill.getUpdatedAt().toString())
                .build();
    }

    private String toJson(Map<String, Object> map) {
        try {
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }
}
