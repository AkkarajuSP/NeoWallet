package com.neowallet.finance.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Builder
public record SavingsGoalForecastResponse(
    String goalId,
    ForecastDto forecast,
    List<ScenarioDto> scenarios,
    String calculatedAt
) {
    @Builder
    public record ForecastDto(
        LocalDate projectedCompletionDate,
        BigDecimal projectedCompletionAmount,
        Integer monthsToCompletion,
        String confidence
    ) {}

    @Builder
    public record ScenarioDto(
        BigDecimal monthlyContribution,
        LocalDate projectedCompletionDate,
        Integer monthsToCompletion
    ) {}
}
