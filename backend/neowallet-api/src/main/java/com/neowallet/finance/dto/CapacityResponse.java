package com.neowallet.finance.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CapacityResponse(
    BigDecimal availableFinancialCapacity,
    String currency,
    String period,
    String disclaimer
) {
}
