package com.neowallet.finance.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record BudgetRecommendationResponse(
    String recommendationId,
    String userId,
    String familyId,
    String period,
    String currency,
    RecommendedAllocation recommendedAllocation,
    List<RecommendedCategory> categories,
    String confidence,
    int historicalMonthsUsed,
    String generatedAt,
    String disclaimer
) {
    @Builder
    public record RecommendedAllocation(
        BigDecimal planningIncome,
        BigDecimal essentialAllocation,
        BigDecimal variableAllocation,
        BigDecimal savingsAllocation,
        BigDecimal emergencyAllocation,
        BigDecimal discretionaryPlanning
    ) {}

    @Builder
    public record RecommendedCategory(
        String category,
        BigDecimal recommendedLimit,
        BigDecimal basedOnHistoricalAverage,
        String priority
    ) {}
}
