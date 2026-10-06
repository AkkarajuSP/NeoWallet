package com.neowallet.finance.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record SavingsContributionResponse(
    String contributionId,
    String goalId,
    String userId,
    BigDecimal amount,
    String currency,
    LocalDate contributionDate,
    String notes,
    String createdAt
) {}
