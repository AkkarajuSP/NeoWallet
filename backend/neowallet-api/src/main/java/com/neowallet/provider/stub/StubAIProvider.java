package com.neowallet.provider.stub;

import com.neowallet.provider.AIContext;
import com.neowallet.provider.AIProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Stub AI provider for local development.
 * Can be replaced by a real provider without changing business logic.
 */
@Component
@ConditionalOnProperty(prefix = "neowallet.ai", name = "provider", havingValue = "stub", matchIfMissing = true)
public class StubAIProvider implements AIProvider {

    @Override
    public String generate(String prompt, AIContext context) {
        return "Stub AI response for prompt: " + prompt;
    }

    @Override
    public boolean health() {
        return true;
    }

}
