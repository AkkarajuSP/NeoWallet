package com.neowallet.ai.service;

import com.neowallet.ai.dto.AIRequest;
import com.neowallet.ai.dto.AIResponse;
import com.neowallet.ai.dto.AIResponseType;
import com.neowallet.ai.dto.Conversation;
import com.neowallet.ai.dto.IntentType;
import com.neowallet.ai.dto.Message;
import com.neowallet.ai.dto.ToolExecutionRecord;
import com.neowallet.ai.policy.AIPermission;
import com.neowallet.ai.policy.AIPolicyDecision;
import com.neowallet.ai.policy.PolicyEngine;
import com.neowallet.ai.tool.ToolContext;
import com.neowallet.identity.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Neo Concierge service: the single entry point for user AI requests.
 * It establishes context, classifies intent, enforces policy, and orchestrates a response.
 */
@Service
@RequiredArgsConstructor
public class NeoConciergeService {

    private final IntentClassifier intentClassifier;
    private final PolicyEngine policyEngine;
    private final AIOrchestrator aiOrchestrator;
    private final ConversationService conversationService;
    private final AuditService auditService;

    public AIResponse chat(UUID userId, UUID familyId, String message, String sessionId, String role) {
        Conversation conversation = conversationService.getOrCreate(userId, familyId, sessionId);

        Message userMessage = new Message(
                UUID.randomUUID().toString(),
                userId,
                Message.ROLE_USER,
                message,
                Instant.now(),
                null
        );
        conversationService.addMessage(conversation, userMessage);

        AIRequest request = new AIRequest(userId, familyId, sessionId, message,
                conversation.messages(),
                Map.of("role", String.valueOf(role)));

        IntentType intent = intentClassifier.classify(message, conversation.messages());
        List<String> requestedTools = intentClassifier.extractRequestedTools(message, intent);

        AIPolicyDecision decision = policyEngine.evaluate(message, intent, requestedTools, role);

        ToolContext toolContext = new ToolContext(userId, familyId, role);

        AIResponse response;
        if (!decision.allowed()) {
            response = new AIResponse(
                    UUID.randomUUID().toString(),
                    userId,
                    familyId,
                    AIResponseType.REFUSAL,
                    "I cannot help with that. " + decision.reason(),
                    List.of(),
                    List.of(),
                    Map.of("reason", decision.reason(), "intent", intent.name()),
                    true
            );
        } else {
            response = aiOrchestrator.process(request, decision, toolContext, intent);
        }

        Message neoMessage = new Message(
                response.responseId(),
                userId,
                Message.ROLE_NEO,
                response.text(),
                Instant.now(),
                response.type()
        );
        conversationService.addMessage(conversation, neoMessage);

        auditAIRequest(userId, familyId, message, intent, requestedTools, decision, response);

        return response;
    }

    private void auditAIRequest(UUID userId, UUID familyId, String message, IntentType intent,
                                List<String> requestedTools, AIPolicyDecision decision, AIResponse response) {
        try {
            String actorType = familyId != null ? "FAMILY_MEMBER" : "USER";
            StringBuilder metadata = new StringBuilder();
            metadata.append("familyId=").append(familyId).append(";");
            metadata.append("intent=").append(intent.name()).append(";");
            metadata.append("requestedTools=").append(requestedTools).append(";");
            metadata.append("allowed=").append(decision.allowed()).append(";");
            metadata.append("allowedTools=").append(decision.allowedTools()).append(";");
            metadata.append("deniedTools=").append(decision.deniedTools()).append(";");
            metadata.append("permission=").append(decision.permission().name()).append(";");
            metadata.append("responseType=").append(response.type().name()).append(";");
            metadata.append("refusal=").append(response.refusal()).append(";");
            metadata.append("executedTools=").append(response.toolExecutions().stream()
                    .filter(ToolExecutionRecord::executed)
                    .map(ToolExecutionRecord::toolName)
                    .toList()).append(";");
            metadata.append("promptVersion=").append(response.metadata().getOrDefault("promptVersion", "unknown")).append(";");
            metadata.append("toolVersion=").append(response.metadata().getOrDefault("toolVersion", "unknown")).append(";");
            metadata.append("timestamp=").append(Instant.now()).append(";");
            metadata.append("result=").append(response.refusal() ? "REFUSED" : "SUCCESS").append(";");

            auditService.record(userId, actorType, "AI_REQUEST_PROCESSED", "AI",
                    userId, response.refusal() ? "REFUSED" : "SUCCESS", metadata.toString());
        } catch (Exception e) {
            // Audit must not break the user experience.
        }
    }
}
