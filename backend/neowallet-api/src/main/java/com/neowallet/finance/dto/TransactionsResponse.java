package com.neowallet.finance.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.util.List;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record TransactionsResponse(
    List<TransactionResponse> transactions,
    PaginationDto pagination
) {

    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record PaginationDto(
        int page,
        int limit,
        long total,
        int totalPages
    ) {
    }
}
