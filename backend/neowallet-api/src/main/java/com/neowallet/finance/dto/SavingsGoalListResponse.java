package com.neowallet.finance.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record SavingsGoalListResponse(
    List<SavingsGoalResponse> goals,
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
