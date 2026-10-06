package com.neowallet.ai.agent;

import com.neowallet.ai.dto.AIRequest;
import com.neowallet.ai.dto.AIResponseType;
import com.neowallet.ai.dto.IntentType;
import com.neowallet.ai.dto.ToolExecutionRecord;
import com.neowallet.ai.policy.AIPermission;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Default Neo Concierge agent for general financial questions and greetings.
 */
@Component
public class ConciergeAgent implements Agent {

    @Override
    public boolean supports(IntentType intent) {
        return intent == IntentType.GREETING
                || intent == IntentType.GENERAL_FINANCIAL_QUESTION
                || intent == IntentType.RECOMMENDATION_REQUEST
                || intent == IntentType.UNKNOWN
                || intent == IntentType.INSUFFICIENT_DATA
                || intent == IntentType.UNSUPPORTED_REQUEST;
    }

    @Override
    public int priority() {
        return 0;
    }

    @Override
    public String name() {
        return "NeoConcierge";
    }

    @Override
    public String buildPrompt(AIRequest request, List<ToolExecutionRecord> records, IntentType intent, AIPermission permission) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are Neo, a helpful, read-only financial concierge.\n");
        sb.append("You can UNDERSTAND, ANALYZE, EXPLAIN, and RECOMMEND, but you cannot perform financial actions.\n");
        sb.append("If you mention a number, it must come from the authoritative data below.\n\n");

        appendAuthoritativeData(sb, records);
        appendConversation(sb, request);

        sb.append("User intent: ").append(intent.name()).append("\n");
        sb.append("Allowed permission level: ").append(permission.name()).append("\n");
        sb.append("User message: ").append(request.message()).append("\n");
        sb.append("\nProvide a helpful, safe response.");
        return sb.toString();
    }

    @Override
    public AIResponseType resolveResponseType(IntentType intent, AIPermission permission, String aiText) {
        if (aiText == null || aiText.isBlank()) {
            return AIResponseType.INSUFFICIENT_DATA;
        }
        if (intent == IntentType.RECOMMENDATION_REQUEST && permission == AIPermission.RECOMMENDATION) {
            return AIResponseType.RECOMMENDATION;
        }
        if (intent == IntentType.GREETING) {
            return AIResponseType.ANALYSIS;
        }
        if (intent == IntentType.INSUFFICIENT_DATA || intent == IntentType.UNSUPPORTED_REQUEST || intent == IntentType.UNKNOWN) {
            return AIResponseType.INSUFFICIENT_DATA;
        }
        if (intent == IntentType.GENERAL_FINANCIAL_QUESTION) {
            return AIResponseType.ANALYSIS;
        }
        return AIResponseType.ANALYSIS;
    }

    void appendAuthoritativeData(StringBuilder sb, List<ToolExecutionRecord> records) {
        sb.append("Authoritative data (use these numbers only):\n");
        if (records.isEmpty()) {
            sb.append("- No relevant data was retrieved.\n");
        } else {
            for (ToolExecutionRecord record : records) {
                sb.append("- ").append(record.toolName()).append(": ").append(record.resultSummary()).append("\n");
            }
        }
        sb.append("\n");
    }

    void appendConversation(StringBuilder sb, AIRequest request) {
        List<com.neowallet.ai.dto.Message> history = request.conversationHistory();
        if (!history.isEmpty()) {
            int start = Math.max(0, history.size() - 10);
            sb.append("Conversation context (most recent ").append(history.size() - start).append(" messages):\n");
            for (int i = start; i < history.size(); i++) {
                com.neowallet.ai.dto.Message m = history.get(i);
                sb.append(m.role().toUpperCase()).append(": ").append(m.content()).append("\n");
            }
            sb.append("\n");
        }
    }
}
