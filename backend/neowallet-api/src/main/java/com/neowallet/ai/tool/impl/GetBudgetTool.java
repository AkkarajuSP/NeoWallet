package com.neowallet.ai.tool.impl;

import com.neowallet.ai.tool.NeowalletTool;
import com.neowallet.ai.tool.ToolContext;
import com.neowallet.finance.dto.BudgetListResponse;
import com.neowallet.finance.service.BudgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

/**
 * Read-only tool that retrieves the user's budgets.
 */
@Component
@RequiredArgsConstructor
public class GetBudgetTool implements NeowalletTool {

    private final BudgetService budgetService;

    @Override
    public String name() {
        return "getBudget";
    }

    @Override
    public String description() {
        return "Retrieves the user's active budgets and utilization.";
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }

    @Override
    public Map<String, Object> execute(ToolContext context, Map<String, String> parameters) {
        int page = Integer.parseInt(parameters.getOrDefault("page", "0"));
        int size = Integer.parseInt(parameters.getOrDefault("size", "10"));
        BudgetListResponse budget = budgetService.listBudgets(context.userId(), Optional.ofNullable(context.familyId()), Optional.empty(), page, size, "createdAt,desc");
        return Map.of(
                "tool", name(),
                "budgets", budget.budgets(),
                "totalCount", budget.pagination().totalCount()
        );
    }
}
