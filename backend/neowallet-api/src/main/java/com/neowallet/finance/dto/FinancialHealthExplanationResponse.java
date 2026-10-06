package com.neowallet.finance.dto;

import java.time.Instant;
import java.util.List;

public record FinancialHealthExplanationResponse(
        String healthScoreId,
        Integer overallScore,
        String scoreLabel,
        String explanation,
        List<ContributorDto> positiveContributors,
        List<ContributorDto> negativeContributors,
        List<RecommendedActionDto> recommendedActions,
        ScoreChangeDto scoreChange,
        Instant calculatedAt
) {

    public record ContributorDto(String factor, Integer score, String reason) {}

    public record RecommendedActionDto(String action, String priority) {}

    public record ScoreChangeDto(
            Integer previousScore,
            Integer currentScore,
            Integer change,
            String direction,
            String magnitude,
            List<String> reasons
    ) {}
}
