package com.neowallet.ai.dto;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * An in-memory conversation foundation for Neo AI.
 * Persistence is intentionally kept minimal for the foundation phase.
 */
public class Conversation {

    private final String conversationId;
    private final UUID userId;
    private final UUID familyId;
    private final List<Message> messages;
    private Instant startedAt;
    private Instant updatedAt;

    public Conversation(UUID userId, UUID familyId) {
        this.conversationId = UUID.randomUUID().toString();
        this.userId = userId;
        this.familyId = familyId;
        this.messages = new ArrayList<>();
        this.startedAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public String conversationId() { return conversationId; }
    public UUID userId() { return userId; }
    public UUID familyId() { return familyId; }
    public List<Message> messages() { return List.copyOf(messages); }
    public Instant startedAt() { return startedAt; }
    public Instant updatedAt() { return updatedAt; }

    public void addMessage(Message message) {
        messages.add(message);
        this.updatedAt = Instant.now();
    }
}
