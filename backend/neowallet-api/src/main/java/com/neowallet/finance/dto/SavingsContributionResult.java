package com.neowallet.finance.dto;

import lombok.Builder;

@Builder
public record SavingsContributionResult(
    SavingsContributionResponse contribution,
    SavingsGoalResponse goal
) {}
