package com.neowallet.ai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neowallet.ai.agent.Agent;
import com.neowallet.ai.dto.AIRequest;
import com.neowallet.ai.dto.AIResponse;
import com.neowallet.ai.dto.AIResponseType;
import com.neowallet.ai.dto.IntentType;
import com.neowallet.ai.dto.ToolExecutionRecord;
import com.neowallet.ai.policy.AIPermission;
import com.neowallet.ai.policy.AIPolicyDecision;
import com.neowallet.ai.tool.NeowalletTool;
import com.neowallet.ai.tool.ToolContext;
import com.neowallet.ai.tool.ToolRegistry;
import com.neowallet.provider.AIContext;
import com.neowallet.provider.AIProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Executes the approved tool pipeline and generates a response through the AI provider.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AIOrchestrator {

    private final AIProvider aiProvider;
    private final ToolRegistry toolRegistry;
    private final ObjectMapper objectMapper;
    private final List<Agent> agents;

    @Value("${neowallet.ai.max-context-size:10}")
    private int maxContextSize;

    @Value("${neowallet.ai.max-response-tokens:500}")
    private int maxResponseTokens;

    @Value("${neowallet.ai.request-timeout-ms:10000}")
    private long requestTimeoutMs;

    @Value("${neowallet.ai.rate-limit:60}")
    private int rateLimit;

    @Value("${neowallet.ai.conversation-limit:50}")
    private int conversationLimit;

    @Value("${neowallet.ai.prompt-version:1.0}")
    private String promptVersion;

    @Value("${neowallet.ai.tool-version:1.0}")
    private String toolVersion;

    public AIResponse process(AIRequest request, AIPolicyDecision decision, ToolContext context, IntentType intent) {
        if (!decision.allowed()) {
            return refusal(request.userId(), request.familyId(), decision.reason());
        }

        List<ToolExecutionRecord> records = new ArrayList<>();
        List<String> citedFacts = new ArrayList<>();

        for (String toolName : decision.allowedTools()) {
            toolRegistry.get(toolName).ifPresentOrElse(
                    tool -> executeTool(tool, context, records, citedFacts),
                    () -> records.add(new ToolExecutionRecord(toolName, false, false, "Tool not in registry", Map.of()))
            );
        }

        if (records.isEmpty() && (intent == IntentType.UNSUPPORTED_REQUEST || intent == IntentType.INSUFFICIENT_DATA)) {
            return insufficientData(request, "I don't have the information needed to answer that.");
        }

        if (requiresData(intent) && !hasUsableData(records)) {
            return insufficientData(request, "I don't have the required financial data to answer that.");
        }

        Agent agent = selectAgent(intent);
        String prompt = agent.buildPrompt(request, records, intent, decision.permission());
        AIContext aiContext = AIContext.builder()
                .userId(String.valueOf(request.userId()))
                .familyId(request.familyId() != null ? request.familyId().toString() : null)
                .sessionId(request.sessionId())
                .authorizedTools(Map.of("allowed", String.valueOf(decision.allowedTools())))
                .maxResponseTokens(maxResponseTokens)
                .build();

        String aiText;
        try {
            aiText = aiProvider.generate(prompt, aiContext);
        } catch (Exception e) {
            log.warn("AI provider failed", e);
            return new AIResponse(
                    UUID.randomUUID().toString(),
                    request.userId(),
                    request.familyId(),
                    AIResponseType.FACT,
                    "I was unable to generate a response right now. Please try again.",
                    records,
                    citedFacts,
                    Map.of("providerError", e.getMessage(), "agent", agent.name()),
                    false
            );
        }

        AIResponseType type = agent.resolveResponseType(intent, decision.permission(), aiText);
        return new AIResponse(
                UUID.randomUUID().toString(),
                request.userId(),
                request.familyId(),
                type,
                aiText,
                records,
                citedFacts,
                Map.of(
                        "permission", decision.permission().name(),
                        "intent", intent.name(),
                        "agent", agent.name(),
                        "promptVersion", promptVersion,
                        "toolVersion", toolVersion,
                        "maxResponseTokens", maxResponseTokens
                ),
                false
        );
    }

    private void executeTool(NeowalletTool tool, ToolContext context, List<ToolExecutionRecord> records, List<String> citedFacts) {
        try {
            Map<String, Object> result = tool.execute(context, Map.of());
            String summary = summarize(result);
            records.add(new ToolExecutionRecord(tool.name(), true, true, summary, result));
            citedFacts.add(tool.name() + " = " + summary);
        } catch (Exception e) {
            log.warn("Tool execution failed: {}", tool.name(), e);
            records.add(new ToolExecutionRecord(tool.name(), true, false, "Execution failed: " + e.getMessage(), Map.of()));
        }
    }

    private String summarize(Map<String, Object> result) {
        try {
            return objectMapper.writeValueAsString(result);
        } catch (Exception e) {
            return String.valueOf(result);
        }
    }

    private Agent selectAgent(IntentType intent) {
        return agents.stream()
                .filter(a -> a.supports(intent))
                .max(java.util.Comparator.comparingInt(Agent::priority))
                .orElseThrow(() -> new IllegalStateException("No agent found for intent " + intent));
    }

    private boolean requiresData(IntentType intent) {
        return intent != IntentType.GREETING
                && intent != IntentType.INSUFFICIENT_DATA
                && intent != IntentType.UNSUPPORTED_REQUEST
                && intent != IntentType.UNKNOWN;
    }

    private boolean hasUsableData(List<ToolExecutionRecord> records) {
        if (records == null || records.isEmpty()) {
            return false;
        }
        return records.stream().anyMatch(this::isUsableRecord);
    }

    private boolean isUsableRecord(ToolExecutionRecord record) {
        if (record == null) return false;
        if (!record.allowed() || !record.executed()) return false;
        if (record.metadata() == null || record.metadata().isEmpty()) return false;
        String summary = record.resultSummary();
        if (summary == null || summary.isBlank()) return false;
        String lower = summary.toLowerCase();
        if ("null".equals(lower) || "[]".equals(summary.trim()) || "{}".equals(summary.trim())) return false;
        return !lower.contains("tool not in registry") && !lower.contains("execution failed");
    }

    private AIResponse refusal(UUID userId, UUID familyId, String reason) {
        return new AIResponse(
                UUID.randomUUID().toString(),
                userId,
                familyId,
                AIResponseType.REFUSAL,
                "I cannot help with that. " + reason,
                List.of(),
                List.of(),
                Map.of("reason", reason, "promptVersion", promptVersion, "toolVersion", toolVersion),
                true
        );
    }

    private AIResponse insufficientData(AIRequest request, String reason) {
        return new AIResponse(
                UUID.randomUUID().toString(),
                request.userId(),
                request.familyId(),
                AIResponseType.INSUFFICIENT_DATA,
                reason,
                List.of(),
                List.of(),
                Map.of("reason", reason, "promptVersion", promptVersion, "toolVersion", toolVersion),
                false
        );
    }
}
