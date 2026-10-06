package com.neowallet.ai.tool;

import java.util.UUID;

/**
 * Context passed to a tool to enforce authorization and family scope.
 */
public record ToolContext(
        UUID userId,
        UUID familyId,
        String role
) {
}
