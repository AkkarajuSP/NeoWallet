package com.neowallet.finance.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record BudgetUtilizationResponse(
    String budgetId,
    String period,
    String currency,
    List<CategoryUtilization> categories,
    BigDecimal totalLimit,
    BigDecimal totalSpent,
    BigDecimal totalRemaining,
    BigDecimal overallUtilizationPercentage,
    String overallStatus,
    String calculatedAt
) {
    @Builder
    public record CategoryUtilization(
        String category,
        BigDecimal limit,
        BigDecimal spent,
        BigDecimal remaining,
        BigDecimal utilizationPercentage,
        String status
    ) {}
}
