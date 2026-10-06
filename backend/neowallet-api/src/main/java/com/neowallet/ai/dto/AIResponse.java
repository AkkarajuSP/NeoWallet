package com.neowallet.ai.dto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Represents a structured response from the Neo AI Concierge.
 */
public record AIResponse(
        String responseId,
        UUID userId,
        UUID familyId,
        AIResponseType type,
        String text,
        List<ToolExecutionRecord> toolExecutions,
        List<String> citedFacts,
        Map<String, Object> metadata,
        boolean refusal
) {
    public AIResponse {
        if (toolExecutions == null) toolExecutions = List.of();
        if (citedFacts == null) citedFacts = List.of();
        if (metadata == null) metadata = Map.of();
    }
}
