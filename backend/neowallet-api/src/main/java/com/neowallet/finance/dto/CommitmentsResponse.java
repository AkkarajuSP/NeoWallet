package com.neowallet.finance.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CommitmentsResponse(
    List<CommitmentDto> commitments,
    BigDecimal totalCommitments,
    String currency,
    String period
) {

    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record CommitmentDto(
        String type,
        String name,
        BigDecimal amount,
        String dueDate,
        String currency
    ) {
    }
}
