package com.neowallet.ai.dto;

import java.util.Map;

/**
 * Record of a tool that was executed to satisfy an AI request.
 */
public record ToolExecutionRecord(
        String toolName,
        boolean allowed,
        boolean executed,
        String resultSummary,
        Map<String, Object> metadata
) {
    public ToolExecutionRecord {
        if (metadata == null) metadata = Map.of();
    }
}
