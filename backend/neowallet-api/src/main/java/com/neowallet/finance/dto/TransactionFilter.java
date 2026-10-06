package com.neowallet.finance.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record TransactionFilter(
    String category,
    String type,
    String status,
    String familyMemberId,
    BigDecimal amountFrom,
    BigDecimal amountTo,
    LocalDate dateFrom,
    LocalDate dateTo
) {
}
