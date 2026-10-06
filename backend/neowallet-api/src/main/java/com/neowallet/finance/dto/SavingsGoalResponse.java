package com.neowallet.finance.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record SavingsGoalResponse(
    String goalId,
    String userId,
    String familyId,
    String name,
    BigDecimal targetAmount,
    BigDecimal currentAmount,
    BigDecimal progressPercentage,
    LocalDate targetDate,
    String currency,
    String priority,
    String category,
    String status,
    String createdAt,
    String updatedAt
) {}
