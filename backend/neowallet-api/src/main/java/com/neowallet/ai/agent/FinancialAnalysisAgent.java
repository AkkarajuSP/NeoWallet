package com.neowallet.ai.agent;

import com.neowallet.ai.dto.AIRequest;
import com.neowallet.ai.dto.AIResponseType;
import com.neowallet.ai.dto.IntentType;
import com.neowallet.ai.dto.ToolExecutionRecord;
import com.neowallet.ai.policy.AIPermission;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Read-only financial analysis specialist.
 * Provides spending analysis, category analysis, trends, and transaction summaries.
 */
@Component
public class FinancialAnalysisAgent extends ConciergeAgent {

    @Override
    public boolean supports(IntentType intent) {
        return intent == IntentType.FINANCIAL_OVERVIEW
                || intent == IntentType.TRANSACTION_ANALYSIS;
    }

    @Override
    public int priority() {
        return 10;
    }

    @Override
    public String name() {
        return "FinancialAnalysisAgent";
    }

    @Override
    public String buildPrompt(AIRequest request, List<ToolExecutionRecord> records, IntentType intent, AIPermission permission) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are the Neo Financial Analysis Agent.\n");
        sb.append("Role: UNDERSTAND, ANALYZE, EXPLAIN.\n");
        sb.append("You may only use the numbers in the authoritative data below.\n");
        sb.append("Do not calculate or invent financial numbers.\n");
        sb.append("Separate authoritative facts from your analysis.\n\n");

        appendAuthoritativeData(sb, records);
        appendConversation(sb, request);

        sb.append("User intent: ").append(intent.name()).append("\n");
        sb.append("Allowed permission level: ").append(permission.name()).append("\n");
        sb.append("User message: ").append(request.message()).append("\n");
        sb.append("\nProvide a concise financial analysis.");
        return sb.toString();
    }

    @Override
    public AIResponseType resolveResponseType(IntentType intent, AIPermission permission, String aiText) {
        if (aiText == null || aiText.isBlank()) {
            return AIResponseType.INSUFFICIENT_DATA;
        }
        if (intent == IntentType.FINANCIAL_OVERVIEW) {
            return AIResponseType.FACT;
        }
        return AIResponseType.ANALYSIS;
    }
}
