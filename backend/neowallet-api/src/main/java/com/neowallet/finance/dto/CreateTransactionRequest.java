package com.neowallet.finance.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CreateTransactionRequest(
    @NotBlank String type,
    @NotBlank String category,
    @NotNull BigDecimal amount,
    String currency,
    String description,
    String transactionDate,
    String status,
    Boolean isRecurring,
    String familyMemberId
) {
}
