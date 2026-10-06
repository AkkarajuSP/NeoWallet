package com.neowallet.finance.dto;

import java.math.BigDecimal;
import java.util.List;

public record CreateBudgetRequest(
    String name,
    String period,
    String currency,
    List<CreateBudgetCategoryRequest> categories
) {
    public record CreateBudgetCategoryRequest(
        String category,
        BigDecimal limit,
        String priority
    ) {}
}
