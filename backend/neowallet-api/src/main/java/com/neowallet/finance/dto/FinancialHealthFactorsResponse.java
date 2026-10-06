package com.neowallet.finance.dto;

import java.time.Instant;
import java.util.Map;

public record FinancialHealthFactorsResponse(
        String healthScoreId,
        Map<String, FactorScoreDto> factorScores,
        Instant calculatedAt
) {
}
