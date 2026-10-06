package com.neowallet.finance.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record FinancialOverviewResponse(
    String overviewId,
    String userId,
    String familyId,
    BigDecimal planningIncome,
    BigDecimal mandatoryCommitments,
    BigDecimal essentialAllocation,
    BigDecimal savingsAllocation,
    BigDecimal emergencyAllocation,
    BigDecimal discretionaryPlanning,
    BigDecimal committedAmount,
    BigDecimal pendingPayments,
    BigDecimal actualTransactions,
    BigDecimal availableFinancialCapacity,
    String currency,
    String period,
    String calculatedAt,
    String disclaimer
) {
}
