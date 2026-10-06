package com.neowallet.finance.dto;

import java.math.BigDecimal;

public record FactorScoreDto(
        Integer score,
        BigDecimal weight,
        BigDecimal contribution
) {
}
