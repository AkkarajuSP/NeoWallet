package com.neowallet.provider;

/**
 * Provider abstraction for AI orchestration.
 * Implementations are replaceable without changing business logic.
 */
public interface AIProvider {

    /**
     * Sends a prompt to the AI provider and returns the generated text.
     * No direct database access is allowed in this layer.
     */
    String generate(String prompt, AIContext context);

    /**
     * Validates the provider is reachable.
     */
    boolean health();

}
