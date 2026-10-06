package com.neowallet.provider;

import lombok.Builder;
import lombok.Value;

import java.util.Map;

/**
 * Context passed to AI provider to enforce grounding and authorization.
 */
@Value
@Builder
public class AIContext {

    String userId;
    String familyId;
    String sessionId;
    Map<String, String> authorizedTools;
    Integer maxResponseTokens;
    Long requestTimeoutMs;

}
