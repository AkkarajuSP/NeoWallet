package com.neowallet.finance.service;

import com.neowallet.finance.dto.*;
import com.neowallet.finance.entity.FinancialAllocation;
import com.neowallet.finance.entity.FinancialOverview;
import com.neowallet.finance.repository.FinancialAllocationRepository;
import com.neowallet.finance.repository.FinancialOverviewRepository;
import com.neowallet.identity.repository.FamilyMemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FinancialOverviewService {

    private static final String DISCLAIMER = "This is a planning/management representation, not actual funds held by NeoWallet";

    private final FinancialOverviewRepository financialOverviewRepository;
    private final FinancialAllocationRepository financialAllocationRepository;
    private final FamilyMemberRepository familyMemberRepository;

    @Transactional(readOnly = true)
    public FinancialOverviewResponse getOverview(UUID userId, Optional<UUID> familyId) {
        var overview = resolveOverview(userId, familyId);
        return mapToOverviewResponse(overview);
    }

    @Transactional(readOnly = true)
    public FinancialSummaryResponse getSummary(UUID userId, Optional<UUID> familyId) {
        var overview = resolveOverview(userId, familyId);
        return mapToSummaryResponse(overview);
    }

    @Transactional(readOnly = true)
    public AllocationsResponse getAllocations(UUID userId, Optional<UUID> familyId) {
        var overview = resolveOverview(userId, familyId);
        var allocations = financialAllocationRepository.findByOverview_OverviewId(overview.getOverviewId());
        return mapToAllocationsResponse(overview, allocations);
    }

    @Transactional(readOnly = true)
    public CommitmentsResponse getCommitments(UUID userId, Optional<UUID> familyId) {
        var overview = resolveOverview(userId, familyId);
        return CommitmentsResponse.builder()
            .commitments(Collections.emptyList())
            .totalCommitments(overview.getMandatoryCommitments())
            .currency(overview.getCurrency())
            .period(overview.getPeriod())
            .build();
    }

    @Transactional(readOnly = true)
    public PendingPaymentsResponse getPendingPayments(UUID userId, Optional<UUID> familyId) {
        var overview = resolveOverview(userId, familyId);
        return PendingPaymentsResponse.builder()
            .pendingPayments(Collections.emptyList())
            .totalPending(overview.getPendingPayments())
            .currency(overview.getCurrency())
            .period(overview.getPeriod())
            .build();
    }

    @Transactional(readOnly = true)
    public CapacityResponse getCapacity(UUID userId, Optional<UUID> familyId) {
        var overview = resolveOverview(userId, familyId);
        return CapacityResponse.builder()
            .availableFinancialCapacity(overview.getAvailableFinancialCapacity())
            .currency(overview.getCurrency())
            .period(overview.getPeriod())
            .disclaimer(DISCLAIMER)
            .build();
    }

    private FinancialOverview resolveOverview(UUID userId, Optional<UUID> familyId) {
        familyId.ifPresent(fid -> ensureFamilyMember(userId, fid));

        String period = currentPeriod();
        Optional<FinancialOverview> overview;

        if (familyId.isPresent()) {
            overview = financialOverviewRepository.findByFamily_FamilyIdAndPeriod(familyId.get(), period);
        } else {
            overview = financialOverviewRepository.findByUser_UserIdAndPeriod(userId, period);
        }

        return overview.orElseThrow(() -> new EntityNotFoundException("No financial data available"));
    }

    private void ensureFamilyMember(UUID userId, UUID familyId) {
        if (!familyMemberRepository.existsByFamily_FamilyIdAndUser_UserIdAndLeftAtIsNull(familyId, userId)) {
            throw new AccessDeniedException("Not a family member");
        }
    }

    private String currentPeriod() {
        return YearMonth.now().toString();
    }

    private FinancialOverviewResponse mapToOverviewResponse(FinancialOverview overview) {
        return FinancialOverviewResponse.builder()
            .overviewId(overview.getOverviewId().toString())
            .userId(overview.getUser() != null ? overview.getUser().getUserId().toString() : null)
            .familyId(overview.getFamily() != null ? overview.getFamily().getFamilyId().toString() : null)
            .planningIncome(overview.getPlanningIncome())
            .mandatoryCommitments(overview.getMandatoryCommitments())
            .essentialAllocation(overview.getEssentialAllocation())
            .savingsAllocation(overview.getSavingsAllocation())
            .emergencyAllocation(overview.getEmergencyAllocation())
            .discretionaryPlanning(overview.getDiscretionaryPlanning())
            .committedAmount(overview.getCommittedAmount())
            .pendingPayments(overview.getPendingPayments())
            .actualTransactions(overview.getActualTransactions())
            .availableFinancialCapacity(overview.getAvailableFinancialCapacity())
            .currency(overview.getCurrency())
            .period(overview.getPeriod())
            .calculatedAt(overview.getCalculatedAt().toString())
            .disclaimer(DISCLAIMER)
            .build();
    }

    private FinancialSummaryResponse mapToSummaryResponse(FinancialOverview overview) {
        BigDecimal totalIncome = overview.getPlanningIncome();
        BigDecimal totalExpenses = overview.getCommittedAmount().add(overview.getActualTransactions());
        BigDecimal totalSavings = overview.getSavingsAllocation().add(overview.getEmergencyAllocation());
        BigDecimal availableCapacity = overview.getAvailableFinancialCapacity();

        BigDecimal totalAllocation = overview.getEssentialAllocation()
            .add(overview.getVariableAllocation())
            .add(overview.getSavingsAllocation())
            .add(overview.getEmergencyAllocation())
            .add(overview.getDiscretionaryPlanning());

        BigDecimal budgetAdherence = totalAllocation.compareTo(BigDecimal.ZERO) == 0
            ? BigDecimal.ZERO
            : overview.getActualTransactions()
                .multiply(BigDecimal.valueOf(100))
                .divide(totalAllocation, 2, RoundingMode.HALF_UP);

        return FinancialSummaryResponse.builder()
            .totalIncome(totalIncome)
            .totalExpenses(totalExpenses)
            .totalSavings(totalSavings)
            .availableCapacity(availableCapacity)
            .budgetAdherence(budgetAdherence)
            .currency(overview.getCurrency())
            .period(overview.getPeriod())
            .build();
    }

    private AllocationsResponse mapToAllocationsResponse(FinancialOverview overview, List<FinancialAllocation> allocations) {
        List<AllocationsResponse.AllocationDto> dtos = allocations.stream()
            .map(a -> AllocationsResponse.AllocationDto.builder()
                .category(a.getCategoryName())
                .allocatedAmount(a.getAllocatedAmount())
                .actualAmount(a.getActualAmount())
                .utilizationPercentage(a.getUtilizationPercentage())
                .currency(a.getCurrency())
                .build())
            .collect(Collectors.toList());

        return AllocationsResponse.builder()
            .allocations(dtos)
            .currency(overview.getCurrency())
            .period(overview.getPeriod())
            .build();
    }
}
