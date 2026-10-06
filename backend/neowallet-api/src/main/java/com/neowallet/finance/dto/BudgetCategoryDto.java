package com.neowallet.finance.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record BudgetCategoryDto(
    String category,
    BigDecimal limit,
    BigDecimal spent,
    BigDecimal utilizationPercentage,
    String priority
) {}
