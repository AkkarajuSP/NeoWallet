package com.neowallet.ai.service;

import com.neowallet.ai.dto.Conversation;
import com.neowallet.ai.dto.Message;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Foundation conversation store.
 * Uses an in-memory store per the requirement to not implement long-term personal memory.
 */
@Service
public class ConversationService {

    private final Map<String, Conversation> conversations = new ConcurrentHashMap<>();

    public Conversation getOrCreate(UUID userId, UUID familyId, String sessionId) {
        String key = (sessionId == null ? userId.toString() : userId + ":" + sessionId);
        return conversations.computeIfAbsent(key, k -> new Conversation(userId, familyId));
    }

    public void addMessage(Conversation conversation, Message message) {
        conversation.addMessage(message);
    }
}
