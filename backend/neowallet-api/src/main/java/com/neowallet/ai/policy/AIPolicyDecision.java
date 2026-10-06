package com.neowallet.ai.policy;

import java.util.List;

/**
 * Result of a policy evaluation for a Neo AI request.
 */
public record AIPolicyDecision(
        boolean allowed,
        AIPermission permission,
        String reason,
        List<String> allowedTools,
        List<String> deniedTools
) {
    public AIPolicyDecision {
        if (allowedTools == null) allowedTools = List.of();
        if (deniedTools == null) deniedTools = List.of();
    }
}
