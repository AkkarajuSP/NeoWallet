package com.neowallet.finance.engine;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record FinancialHealthResult(
        Integer overallScore,
        String scoreLabel,
        String confidence,
        String status,
        List<FactorResult> factors,
        List<Contributor> positiveContributors,
        List<Contributor> negativeContributors,
        List<RecommendedAction> recommendedActions,
        ScoreChange scoreChange,
        Instant calculatedAt,
        String algorithmVersion,
        List<String> notes
) {

    public record FactorResult(
            String name,
            Integer score,
            BigDecimal weight,
            BigDecimal contribution,
            String confidence,
            String note
    ) {}

    public record Contributor(String factor, Integer score, String reason) {}

    public record RecommendedAction(String factor, String action, String priority, String estimatedImpact) {}

    public record ScoreChange(
            Integer previousScore,
            Integer currentScore,
            Integer change,
            String direction,
            String magnitude,
            List<String> reasons
    ) {}
}
