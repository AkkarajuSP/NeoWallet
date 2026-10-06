package com.neowallet.ai.tool.impl;

import com.neowallet.ai.tool.NeowalletTool;
import com.neowallet.ai.tool.ToolContext;
import com.neowallet.finance.dto.FinancialOverviewResponse;
import com.neowallet.finance.service.FinancialOverviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

/**
 * Read-only tool that retrieves the user's financial overview.
 */
@Component
@RequiredArgsConstructor
public class GetFinancialOverviewTool implements NeowalletTool {

    private final FinancialOverviewService financialOverviewService;

    @Override
    public String name() {
        return "getFinancialOverview";
    }

    @Override
    public String description() {
        return "Retrieves the user's financial overview including income, expenses, and balances.";
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }

    @Override
    public Map<String, Object> execute(ToolContext context, Map<String, String> parameters) {
        FinancialOverviewResponse overview = financialOverviewService.getOverview(context.userId(), Optional.ofNullable(context.familyId()));
        return Map.of(
                "tool", name(),
                "planningIncome", overview.planningIncome(),
                "mandatoryCommitments", overview.mandatoryCommitments(),
                "essentialAllocation", overview.essentialAllocation(),
                "savingsAllocation", overview.savingsAllocation(),
                "emergencyAllocation", overview.emergencyAllocation(),
                "discretionaryPlanning", overview.discretionaryPlanning(),
                "availableFinancialCapacity", overview.availableFinancialCapacity(),
                "calculatedAt", overview.calculatedAt()
        );
    }
}
