package com.neowallet.finance.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record TransactionResponse(
    String transactionId,
    String userId,
    String familyId,
    String familyMemberId,
    String type,
    String category,
    BigDecimal amount,
    String currency,
    String description,
    String transactionDate,
    String status,
    Boolean isRecurring,
    String source,
    String createdAt,
    String updatedAt
) {
}
