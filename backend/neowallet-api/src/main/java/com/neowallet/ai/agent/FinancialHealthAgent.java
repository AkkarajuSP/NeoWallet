package com.neowallet.ai.agent;

import com.neowallet.ai.dto.AIRequest;
import com.neowallet.ai.dto.AIResponseType;
import com.neowallet.ai.dto.IntentType;
import com.neowallet.ai.dto.ToolExecutionRecord;
import com.neowallet.ai.policy.AIPermission;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Read-only financial health specialist.
 * Explains the authoritative Financial Health score, factors, and changes.
 * Never calculates, modifies, or overrides the deterministic score.
 */
@Component
public class FinancialHealthAgent extends ConciergeAgent {

    @Override
    public boolean supports(IntentType intent) {
        return intent == IntentType.FINANCIAL_HEALTH_QUERY
                || intent == IntentType.FINANCIAL_HEALTH_EXPLANATION;
    }

    @Override
    public int priority() {
        return 10;
    }

    @Override
    public String name() {
        return "FinancialHealthAgent";
    }

    @Override
    public String buildPrompt(AIRequest request, List<ToolExecutionRecord> records, IntentType intent, AIPermission permission) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are the Neo Financial Health Agent.\n");
        sb.append("Role: UNDERSTAND and EXPLAIN the authoritative Financial Health score.\n");
        sb.append("You must NOT calculate, modify, or override the score, weights, or thresholds.\n");
        sb.append("Use only the numbers and factor data in the authoritative data below.\n");
        sb.append("If factor details are unavailable, say so rather than inventing them.\n\n");

        appendAuthoritativeData(sb, records);
        appendConversation(sb, request);

        sb.append("User intent: ").append(intent.name()).append("\n");
        sb.append("Allowed permission level: ").append(permission.name()).append("\n");
        sb.append("User message: ").append(request.message()).append("\n");
        sb.append("\nExplain the financial health score and factors clearly.");
        return sb.toString();
    }

    @Override
    public AIResponseType resolveResponseType(IntentType intent, AIPermission permission, String aiText) {
        if (aiText == null || aiText.isBlank()) {
            return AIResponseType.INSUFFICIENT_DATA;
        }
        if (intent == IntentType.FINANCIAL_HEALTH_QUERY) {
            return AIResponseType.FACT;
        }
        return AIResponseType.ANALYSIS;
    }
}
