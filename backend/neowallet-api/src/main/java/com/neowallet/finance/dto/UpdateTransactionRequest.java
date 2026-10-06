package com.neowallet.finance.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record UpdateTransactionRequest(
    String category,
    BigDecimal amount,
    String description,
    String transactionDate,
    String status,
    Boolean isRecurring
) {
}
