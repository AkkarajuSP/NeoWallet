package com.neowallet.ai.tool;

import java.util.Map;

/**
 * Contract for a read-only tool available to Neo AI.
 * Tools never perform financial writes.
 */
public interface NeowalletTool {

    String name();

    String description();

    boolean isReadOnly();

    Map<String, Object> execute(ToolContext context, Map<String, String> parameters);
}
