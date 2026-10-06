package com.neowallet.ai.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * A single message within a Neo AI conversation.
 */
public record Message(
        String messageId,
        UUID userId,
        String role,
        String content,
        Instant timestamp,
        AIResponseType type
) {
    public Message {
        if (timestamp == null) timestamp = Instant.now();
    }

    public static final String ROLE_USER = "USER";
    public static final String ROLE_NEO = "NEO";
    public static final String ROLE_SYSTEM = "SYSTEM";
}
