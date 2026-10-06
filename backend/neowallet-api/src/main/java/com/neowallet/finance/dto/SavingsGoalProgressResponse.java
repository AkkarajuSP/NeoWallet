package com.neowallet.finance.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record SavingsGoalProgressResponse(
    String goalId,
    BigDecimal currentAmount,
    BigDecimal targetAmount,
    BigDecimal progressPercentage,
    BigDecimal remainingAmount,
    Integer monthsRemaining,
    BigDecimal requiredMonthlyContribution,
    BigDecimal actualMonthlyContribution,
    Boolean onTrack,
    String status,
    String calculatedAt
) {}
