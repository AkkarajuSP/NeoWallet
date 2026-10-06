package com.neowallet.ai.tool.impl;

import com.neowallet.ai.tool.NeowalletTool;
import com.neowallet.ai.tool.ToolContext;
import com.neowallet.finance.dto.FinancialHealthResponse;
import com.neowallet.finance.service.FinancialHealthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

/**
 * Read-only tool that retrieves the user's financial health score.
 */
@Component
@RequiredArgsConstructor
public class GetFinancialHealthTool implements NeowalletTool {

    private final FinancialHealthService financialHealthService;

    @Override
    public String name() {
        return "getFinancialHealth";
    }

    @Override
    public String description() {
        return "Retrieves the current financial health score, confidence, and factor breakdown.";
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }

    @Override
    public Map<String, Object> execute(ToolContext context, Map<String, String> parameters) {
        FinancialHealthResponse health = financialHealthService.getFinancialHealth(context.userId(), Optional.ofNullable(context.familyId()));
        return Map.of(
                "tool", name(),
                "overallScore", health.overallScore(),
                "scoreLabel", health.scoreLabel(),
                "confidence", health.confidence(),
                "status", health.status(),
                "calculatedAt", health.calculatedAt()
        );
    }
}
