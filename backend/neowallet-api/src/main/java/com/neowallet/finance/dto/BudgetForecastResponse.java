package com.neowallet.finance.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record BudgetForecastResponse(
    String budgetId,
    String period,
    String currency,
    Forecast forecast,
    List<CategoryForecast> categories,
    String calculatedAt
) {
    @Builder
    public record Forecast(
        BigDecimal projectedSpending,
        BigDecimal projectedRemaining,
        BigDecimal projectedUtilizationPercentage,
        String projectedStatus,
        String confidence,
        int basedOnHistoricalMonths
    ) {}

    @Builder
    public record CategoryForecast(
        String category,
        BigDecimal limit,
        BigDecimal projectedSpending,
        BigDecimal projectedRemaining,
        BigDecimal projectedUtilizationPercentage
    ) {}
}
