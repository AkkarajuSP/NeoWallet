package com.neowallet.finance.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record BudgetResponse(
    String budgetId,
    String userId,
    String familyId,
    String name,
    String period,
    String currency,
    List<BudgetCategoryDto> categories,
    BigDecimal totalLimit,
    BigDecimal totalSpent,
    BigDecimal utilizationPercentage,
    String createdAt,
    String updatedAt
) {}
