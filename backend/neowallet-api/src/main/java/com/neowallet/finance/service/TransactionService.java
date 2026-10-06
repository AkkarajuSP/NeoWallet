package com.neowallet.finance.service;

import com.neowallet.finance.dto.*;
import com.neowallet.finance.entity.Category;
import com.neowallet.finance.entity.Transaction;
import com.neowallet.finance.repository.CategoryRepository;
import com.neowallet.finance.repository.TransactionRepository;
import com.neowallet.identity.entity.Family;
import com.neowallet.identity.entity.FamilyMember;
import com.neowallet.identity.entity.User;
import com.neowallet.identity.repository.FamilyMemberRepository;
import com.neowallet.identity.repository.FamilyRepository;
import com.neowallet.identity.repository.UserRepository;
import com.neowallet.identity.service.AuditService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private static final List<String> VALID_TYPES = List.of("INCOME", "EXPENSE", "REFUND", "ADJUSTMENT", "TRANSFER_RECORD");
    private static final List<String> VALID_STATUSES = List.of("PLANNED", "COMMITTED", "PENDING", "COMPLETED", "FAILED", "REVERSED");

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final FamilyMemberRepository familyMemberRepository;
    private final FamilyRepository familyRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;
    private final ObjectMapper objectMapper;

    @Transactional
    public TransactionResponse createTransaction(UUID userId, Optional<UUID> familyId, CreateTransactionRequest request) {
        familyId.ifPresent(fid -> ensureFamilyMember(userId, fid));

        User user = findUser(userId);
        validateTransactionType(request.type());
        validateAmount(request.amount());

        String status = request.status() != null ? request.status().toUpperCase() : "PLANNED";
        validateTransactionStatus(status);

        Category category = findOrUseCategory(request.category());
        String currency = request.currency() != null ? request.currency().toUpperCase() : "USD";
        if (!currency.matches("^[A-Z]{3}$")) {
            throw new ValidationException("currency must be a 3-letter ISO code");
        }

        Transaction tx = new Transaction();
        tx.setUser(user);
        familyId.flatMap(familyRepository::findById).ifPresent(tx::setFamily);
        if (request.familyMemberId() != null && !request.familyMemberId().isBlank()) {
            UUID memberId = UUID.fromString(request.familyMemberId());
            FamilyMember member = familyMemberRepository.findById(memberId).orElse(null);
            if (member != null && familyId.isPresent() && member.getFamily().getFamilyId().equals(familyId.get())) {
                tx.setFamilyMember(member);
            }
        }
        tx.setType(request.type().toUpperCase());
        tx.setCategory(category);
        tx.setCategoryName(category != null ? category.getName() : request.category());
        tx.setAmount(request.amount());
        tx.setCurrency(currency);
        tx.setDescription(request.description());
        tx.setTransactionDate(parseDate(request.transactionDate()));
        tx.setStatus(status);
        tx.setIsRecurring(request.isRecurring() != null ? request.isRecurring() : false);
        tx.setSource("MANUAL");

        tx = transactionRepository.saveAndFlush(tx);

        auditService.record(userId, "USER", "TRANSACTION_CREATED", "TRANSACTION", tx.getTransactionId(), "SUCCESS",
            toAuditMetadata(tx));

        return mapToResponse(tx);
    }

    @Transactional(readOnly = true)
    public TransactionResponse getTransaction(UUID userId, UUID transactionId) {
        Transaction tx = findTransaction(transactionId);
        ensureAccess(userId, tx);
        return mapToResponse(tx);
    }

    @Transactional(readOnly = true)
    public TransactionsResponse listTransactions(UUID userId, Optional<UUID> familyId, TransactionFilter filter, int page, int limit, String sort) {
        familyId.ifPresent(fid -> ensureFamilyMember(userId, fid));

        Sort sorting = parseSort(sort);
        Pageable pageable = PageRequest.of(page - 1, limit, sorting);

        Specification<Transaction> spec = ownedBy(userId, familyId)
            .and(notDeleted())
            .and(filterType(filter.type()))
            .and(filterStatus(filter.status()))
            .and(filterCategory(filter.category()))
            .and(filterFamilyMember(filter.familyMemberId()))
            .and(filterAmountBetween(filter.amountFrom(), filter.amountTo()))
            .and(filterDateBetween(filter.dateFrom(), filter.dateTo()));

        Page<Transaction> result = transactionRepository.findAll(spec, pageable);
        List<TransactionResponse> dtos = result.getContent().stream().map(this::mapToResponse).toList();

        return TransactionsResponse.builder()
            .transactions(dtos)
            .pagination(TransactionsResponse.PaginationDto.builder()
                .page(page)
                .limit(limit)
                .total(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .build())
            .build();
    }

    @Transactional
    public TransactionResponse updateTransaction(UUID userId, UUID transactionId, UpdateTransactionRequest request) {
        Transaction tx = findTransaction(transactionId);
        ensureAccess(userId, tx);

        if (tx.isDeleted()) {
            throw new ValidationException("Cannot update deleted transaction");
        }
        if ("COMPLETED".equals(tx.getStatus())) {
            throw new ValidationException("Cannot update completed transaction");
        }

        Transaction original = copyForAudit(tx);

        if (request.category() != null && !request.category().isBlank()) {
            Category category = findOrUseCategory(request.category());
            tx.setCategory(category);
            tx.setCategoryName(category != null ? category.getName() : request.category());
        }
        if (request.amount() != null) {
            validateAmount(request.amount());
            tx.setAmount(request.amount());
        }
        if (request.description() != null) {
            tx.setDescription(request.description());
        }
        if (request.transactionDate() != null && !request.transactionDate().isBlank()) {
            tx.setTransactionDate(parseDate(request.transactionDate()));
        }
        if (request.status() != null && !request.status().isBlank()) {
            validateTransactionStatus(request.status().toUpperCase());
            tx.setStatus(request.status().toUpperCase());
        }
        if (request.isRecurring() != null) {
            tx.setIsRecurring(request.isRecurring());
        }

        tx = transactionRepository.saveAndFlush(tx);

        auditService.record(userId, "USER", "TRANSACTION_UPDATED", "TRANSACTION", tx.getTransactionId(), "SUCCESS",
            toAuditChangeMetadata(original, tx));

        return mapToResponse(tx);
    }

    @Transactional
    public void deleteTransaction(UUID userId, UUID transactionId) {
        Transaction tx = findTransaction(transactionId);
        ensureAccess(userId, tx);

        if (tx.isDeleted()) {
            throw new ValidationException("Transaction already deleted");
        }
        if ("COMPLETED".equals(tx.getStatus())) {
            throw new ValidationException("Cannot delete completed transaction");
        }

        tx.setDeletedAt(Instant.now());
        transactionRepository.saveAndFlush(tx);

        auditService.record(userId, "USER", "TRANSACTION_DELETED", "TRANSACTION", tx.getTransactionId(), "SUCCESS",
            toAuditMetadata(tx));
    }

    private Transaction findTransaction(UUID transactionId) {
        return transactionRepository.findByTransactionIdAndDeletedAtIsNull(transactionId)
            .orElseThrow(() -> new EntityNotFoundException("Transaction not found"));
    }

    private User findUser(UUID userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    private void ensureFamilyMember(UUID userId, UUID familyId) {
        if (!familyMemberRepository.existsByFamily_FamilyIdAndUser_UserIdAndLeftAtIsNull(familyId, userId)) {
            throw new AccessDeniedException("Not a family member");
        }
    }

    private void ensureAccess(UUID userId, Transaction tx) {
        if (tx.getFamily() != null) {
            if (!familyMemberRepository.existsByFamily_FamilyIdAndUser_UserIdAndLeftAtIsNull(tx.getFamily().getFamilyId(), userId)
                && !tx.getUser().getUserId().equals(userId)) {
                throw new AccessDeniedException("Not authorized");
            }
        } else if (!tx.getUser().getUserId().equals(userId)) {
            throw new AccessDeniedException("Not authorized");
        }
    }

    private Category findOrUseCategory(String categoryName) {
        return categoryRepository.findByName(categoryName).orElse(null);
    }

    private void validateTransactionType(String type) {
        if (type == null || !VALID_TYPES.contains(type.toUpperCase())) {
            throw new ValidationException("Invalid transaction type");
        }
    }

    private void validateTransactionStatus(String status) {
        if (status == null || !VALID_STATUSES.contains(status)) {
            throw new ValidationException("Invalid transaction status");
        }
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Amount must be positive");
        }
    }

    private LocalDate parseDate(String date) {
        if (date == null || date.isBlank()) {
            return LocalDate.now();
        }
        return LocalDate.parse(date);
    }

    private Sort parseSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }
        String[] parts = sort.split(":");
        String field = parts[0];
        Sort.Direction direction = parts.length > 1 && "asc".equalsIgnoreCase(parts[1])
            ? Sort.Direction.ASC : Sort.Direction.DESC;
        return Sort.by(direction, field);
    }

    private Specification<Transaction> ownedBy(UUID userId, Optional<UUID> familyId) {
        return (root, query, cb) -> {
            if (familyId.isPresent()) {
                return cb.equal(root.get("family").get("familyId"), familyId.get());
            }
            return cb.equal(root.get("user").get("userId"), userId);
        };
    }

    private Specification<Transaction> notDeleted() {
        return (root, query, cb) -> cb.isNull(root.get("deletedAt"));
    }

    private Specification<Transaction> filterType(String type) {
        return (root, query, cb) -> type == null || type.isBlank() ? cb.conjunction() : cb.equal(root.get("type"), type.toUpperCase());
    }

    private Specification<Transaction> filterStatus(String status) {
        return (root, query, cb) -> status == null || status.isBlank() ? cb.conjunction() : cb.equal(root.get("status"), status.toUpperCase());
    }

    private Specification<Transaction> filterCategory(String category) {
        return (root, query, cb) -> category == null || category.isBlank() ? cb.conjunction() : cb.equal(root.get("categoryName"), category);
    }

    private Specification<Transaction> filterFamilyMember(String familyMemberId) {
        return (root, query, cb) -> familyMemberId == null || familyMemberId.isBlank() ? cb.conjunction() :
            cb.equal(root.get("familyMember").get("memberId"), UUID.fromString(familyMemberId));
    }

    private Specification<Transaction> filterAmountBetween(BigDecimal from, BigDecimal to) {
        return (root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();
            if (from != null) predicates.add(cb.greaterThanOrEqualTo(root.get("amount"), from));
            if (to != null) predicates.add(cb.lessThanOrEqualTo(root.get("amount"), to));
            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }

    private Specification<Transaction> filterDateBetween(LocalDate from, LocalDate to) {
        return (root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();
            if (from != null) predicates.add(cb.greaterThanOrEqualTo(root.get("transactionDate"), from));
            if (to != null) predicates.add(cb.lessThanOrEqualTo(root.get("transactionDate"), to));
            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }

    private Transaction copyForAudit(Transaction tx) {
        Transaction copy = new Transaction();
        copy.setAmount(tx.getAmount());
        copy.setCategoryName(tx.getCategoryName());
        copy.setDescription(tx.getDescription());
        copy.setTransactionDate(tx.getTransactionDate());
        copy.setStatus(tx.getStatus());
        copy.setIsRecurring(tx.getIsRecurring());
        return copy;
    }

    private String toAuditMetadata(Transaction tx) {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("type", tx.getType());
            map.put("amount", tx.getAmount().toString());
            map.put("currency", tx.getCurrency());
            map.put("status", tx.getStatus());
            map.put("category", tx.getCategoryName());
            return objectMapper.writeValueAsString(map);
        } catch (Exception e) {
            return "{}";
        }
    }

    private String toAuditChangeMetadata(Transaction original, Transaction updated) {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("original", Map.of(
                "amount", original.getAmount().toString(),
                "category", original.getCategoryName(),
                "status", original.getStatus()
            ));
            map.put("updated", Map.of(
                "amount", updated.getAmount().toString(),
                "category", updated.getCategoryName(),
                "status", updated.getStatus()
            ));
            return objectMapper.writeValueAsString(map);
        } catch (Exception e) {
            return "{}";
        }
    }

    private TransactionResponse mapToResponse(Transaction tx) {
        return TransactionResponse.builder()
            .transactionId(tx.getTransactionId().toString())
            .userId(tx.getUser() != null ? tx.getUser().getUserId().toString() : null)
            .familyId(tx.getFamily() != null ? tx.getFamily().getFamilyId().toString() : null)
            .familyMemberId(tx.getFamilyMember() != null ? tx.getFamilyMember().getMemberId().toString() : null)
            .type(tx.getType())
            .category(tx.getCategoryName())
            .amount(tx.getAmount())
            .currency(tx.getCurrency())
            .description(tx.getDescription())
            .transactionDate(tx.getTransactionDate() != null ? tx.getTransactionDate().toString() : null)
            .status(tx.getStatus())
            .isRecurring(tx.getIsRecurring())
            .source(tx.getSource())
            .createdAt(tx.getCreatedAt().toString())
            .updatedAt(tx.getUpdatedAt().toString())
            .build();
    }
}
