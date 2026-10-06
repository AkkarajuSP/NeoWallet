package com.neowallet.identity.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class SessionResponse {

    private UUID sessionId;
    private UUID deviceId;
    private String deviceName;
    private Instant lastActiveAt;
    private Instant createdAt;
    private boolean current;

}
