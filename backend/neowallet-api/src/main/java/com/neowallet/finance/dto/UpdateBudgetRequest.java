package com.neowallet.finance.dto;

import java.math.BigDecimal;
import java.util.List;

public record UpdateBudgetRequest(
    String name,
    List<UpdateBudgetCategoryRequest> categories
) {
    public record UpdateBudgetCategoryRequest(
        String category,
        BigDecimal limit,
        String priority
    ) {}
}
