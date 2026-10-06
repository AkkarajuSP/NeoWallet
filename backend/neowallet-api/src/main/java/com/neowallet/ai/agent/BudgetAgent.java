package com.neowallet.ai.agent;

import com.neowallet.ai.dto.AIRequest;
import com.neowallet.ai.dto.AIResponseType;
import com.neowallet.ai.dto.IntentType;
import com.neowallet.ai.dto.ToolExecutionRecord;
import com.neowallet.ai.policy.AIPermission;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Read-only budget specialist.
 * Explains budgets, utilization, variance, forecast, and recommendations.
 * Never modifies budgets.
 */
@Component
public class BudgetAgent extends ConciergeAgent {

    @Override
    public boolean supports(IntentType intent) {
        return intent == IntentType.BUDGET_ANALYSIS
                || intent == IntentType.BUDGET_HELP;
    }

    @Override
    public int priority() {
        return 10;
    }

    @Override
    public String name() {
        return "BudgetAgent";
    }

    @Override
    public String buildPrompt(AIRequest request, List<ToolExecutionRecord> records, IntentType intent, AIPermission permission) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are the Neo Budget Agent.\n");
        sb.append("Role: UNDERSTAND, ANALYZE, EXPLAIN, RECOMMEND.\n");
        sb.append("You cannot modify budgets or allocations.\n");
        sb.append("Use only the numbers in the authoritative data below.\n");
        sb.append("Keep recommendations advisory and explainable.\n\n");

        appendAuthoritativeData(sb, records);
        appendConversation(sb, request);

        sb.append("User intent: ").append(intent.name()).append("\n");
        sb.append("Allowed permission level: ").append(permission.name()).append("\n");
        sb.append("User message: ").append(request.message()).append("\n");
        sb.append("\nProvide a budget explanation or recommendation. Do not state that you changed anything.");
        return sb.toString();
    }

    @Override
    public AIResponseType resolveResponseType(IntentType intent, AIPermission permission, String aiText) {
        if (aiText == null || aiText.isBlank()) {
            return AIResponseType.INSUFFICIENT_DATA;
        }
        if (permission == AIPermission.RECOMMENDATION && aiText.toLowerCase().contains("recommend")) {
            return AIResponseType.RECOMMENDATION;
        }
        return AIResponseType.ANALYSIS;
    }
}
