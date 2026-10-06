package com.neowallet.finance.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record AllocationsResponse(
    List<AllocationDto> allocations,
    String currency,
    String period
) {

    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record AllocationDto(
        String category,
        BigDecimal allocatedAmount,
        BigDecimal actualAmount,
        BigDecimal utilizationPercentage,
        String currency
    ) {
    }
}
