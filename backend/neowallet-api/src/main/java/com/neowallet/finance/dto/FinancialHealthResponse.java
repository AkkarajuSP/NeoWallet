package com.neowallet.finance.dto;

import java.time.Instant;

public record FinancialHealthResponse(
        String healthScoreId,
        String userId,
        String familyId,
        Integer overallScore,
        String scoreLabel,
        String confidence,
        String status,
        Instant calculatedAt,
        String disclaimer
) {
}
