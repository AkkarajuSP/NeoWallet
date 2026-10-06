package com.neowallet.ai.tool.impl;

import com.neowallet.ai.tool.NeowalletTool;
import com.neowallet.ai.tool.ToolContext;
import com.neowallet.finance.dto.SavingsGoalListResponse;
import com.neowallet.finance.service.SavingsGoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

/**
 * Read-only tool that retrieves the user's savings goals.
 */
@Component
@RequiredArgsConstructor
public class GetSavingsGoalsTool implements NeowalletTool {

    private final SavingsGoalService savingsGoalService;

    @Override
    public String name() {
        return "getSavingsGoals";
    }

    @Override
    public String description() {
        return "Retrieves the user's savings goals and progress.";
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }

    @Override
    public Map<String, Object> execute(ToolContext context, Map<String, String> parameters) {
        int page = Integer.parseInt(parameters.getOrDefault("page", "0"));
        int size = Integer.parseInt(parameters.getOrDefault("size", "10"));
        SavingsGoalListResponse goals = savingsGoalService.listSavingsGoals(context.userId(), Optional.ofNullable(context.familyId()),
                Optional.empty(), Optional.empty(), page, size, "createdAt,desc");
        return Map.of(
                "tool", name(),
                "goals", goals.goals(),
                "totalCount", goals.pagination().totalCount()
        );
    }
}
