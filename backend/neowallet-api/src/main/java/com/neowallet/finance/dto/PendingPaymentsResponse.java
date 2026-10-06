package com.neowallet.finance.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PendingPaymentsResponse(
    List<PendingPaymentDto> pendingPayments,
    BigDecimal totalPending,
    String currency,
    String period
) {

    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record PendingPaymentDto(
        String paymentId,
        String type,
        BigDecimal amount,
        String dueDate,
        String currency
    ) {
    }
}
