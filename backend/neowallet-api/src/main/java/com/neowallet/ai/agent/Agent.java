package com.neowallet.ai.agent;

import com.neowallet.ai.dto.AIRequest;
import com.neowallet.ai.dto.AIResponseType;
import com.neowallet.ai.dto.IntentType;
import com.neowallet.ai.dto.ToolExecutionRecord;
import com.neowallet.ai.policy.AIPermission;

import java.util.List;

/**
 * Specialist agent capability within the Neo AI Orchestrator.
 * Each agent is read-only/recommendation-only and never performs financial writes.
 */
public interface Agent {

    boolean supports(IntentType intent);

    int priority();

    String name();

    String buildPrompt(AIRequest request, List<ToolExecutionRecord> records, IntentType intent, AIPermission permission);

    AIResponseType resolveResponseType(IntentType intent, AIPermission permission, String aiText);
}
