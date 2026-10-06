package com.neowallet.ai.dto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Represents a user request to the Neo AI Concierge.
 */
public record AIRequest(
        UUID userId,
        UUID familyId,
        String sessionId,
        String message,
        List<Message> conversationHistory,
        Map<String, String> metadata
) {
    public AIRequest(UUID userId, String message) {
        this(userId, null, null, message, List.of(), Map.of());
    }

    public AIRequest(UUID userId, UUID familyId, String message) {
        this(userId, familyId, null, message, List.of(), Map.of());
    }
}
