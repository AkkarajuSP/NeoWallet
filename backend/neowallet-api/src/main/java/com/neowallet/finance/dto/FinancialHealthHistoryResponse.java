package com.neowallet.finance.dto;

import java.util.List;

public record FinancialHealthHistoryResponse(
        List<FinancialHealthResponse> history,
        PaginationDto pagination
) {
    public record PaginationDto(int page, int limit, long total, int totalPages) {}
}
