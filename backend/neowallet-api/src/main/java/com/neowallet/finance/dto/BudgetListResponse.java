package com.neowallet.finance.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record BudgetListResponse(
    List<BudgetResponse> budgets,
    PaginationDto pagination
) {
    @Builder
    public record PaginationDto(
        int page,
        int limit,
        int totalCount,
        int pageCount
    ) {}
}
