package com.neowallet.ai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neowallet.ai.agent.*;
import com.neowallet.ai.dto.AIRequest;
import com.neowallet.ai.dto.AIResponse;
import com.neowallet.ai.dto.AIResponseType;
import com.neowallet.ai.dto.IntentType;
import com.neowallet.ai.policy.AIPermission;
import com.neowallet.ai.policy.AIPolicyDecision;
import com.neowallet.ai.tool.NeowalletTool;
import com.neowallet.ai.tool.ToolContext;
import com.neowallet.ai.tool.ToolRegistry;
import com.neowallet.provider.AIContext;
import com.neowallet.provider.AIProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AIOrchestratorTest {

    private AIOrchestrator orchestrator;
    private AIProvider aiProvider;
    private ToolRegistry toolRegistry;

    @BeforeEach
    void setUp() {
        aiProvider = mock(AIProvider.class);
        toolRegistry = mock(ToolRegistry.class);
        orchestrator = new AIOrchestrator(aiProvider, toolRegistry, new ObjectMapper(), List.of(
                new ConciergeAgent(),
                new FinancialAnalysisAgent(),
                new BudgetAgent(),
                new SavingsAgent(),
                new BillAgent(),
                new FinancialHealthAgent()
        ));
        ReflectionTestUtils.setField(orchestrator, "maxContextSize", 10);
        ReflectionTestUtils.setField(orchestrator, "maxResponseTokens", 500);
        ReflectionTestUtils.setField(orchestrator, "requestTimeoutMs", 10000L);
        ReflectionTestUtils.setField(orchestrator, "rateLimit", 60);
        ReflectionTestUtils.setField(orchestrator, "conversationLimit", 50);
        ReflectionTestUtils.setField(orchestrator, "promptVersion", "1.0");
        ReflectionTestUtils.setField(orchestrator, "toolVersion", "1.0");
    }

    private NeowalletTool stubTool(String name, Map<String, Object> result) {
        NeowalletTool tool = mock(NeowalletTool.class);
        when(tool.name()).thenReturn(name);
        when(tool.execute(any(ToolContext.class), anyMap())).thenReturn(result);
        return tool;
    }

    @Test
    void aiProviderFailure_returnsErrorResponse() {
        AIRequest request = new AIRequest(UUID.randomUUID(), null, "s1", "What is my budget?", List.of(), Map.of());
        AIPolicyDecision decision = new AIPolicyDecision(true, AIPermission.READ_ONLY, "ok",
                List.of("getBudget"), List.of());

        NeowalletTool getBudget = stubTool("getBudget", Map.of("amount", 1000));
        when(toolRegistry.get("getBudget")).thenReturn(Optional.of(getBudget));
        when(aiProvider.generate(anyString(), any(AIContext.class)))
                .thenThrow(new RuntimeException("provider down"));

        AIResponse response = orchestrator.process(request, decision, new ToolContext(request.userId(), null, "OWNER"), IntentType.BUDGET_ANALYSIS);

        assertEquals(AIResponseType.FACT, response.type());
        assertTrue(response.text().toLowerCase().contains("unable"));
        assertNotNull(response.metadata().get("providerError"));
    }

    @Test
    void insufficientDataRequest_returnsInsufficientData() {
        AIRequest request = new AIRequest(UUID.randomUUID(), null, "s1", "What is the weather?", List.of(), Map.of());
        AIPolicyDecision decision = new AIPolicyDecision(true, AIPermission.READ_ONLY, "ok",
                List.of(), List.of());

        AIResponse response = orchestrator.process(request, decision, new ToolContext(request.userId(), null, "OWNER"), IntentType.UNSUPPORTED_REQUEST);

        assertEquals(AIResponseType.INSUFFICIENT_DATA, response.type());
        assertFalse(response.refusal());
    }

    @Test
    void recommendationRequestIsAllowed() {
        AIRequest request = new AIRequest(UUID.randomUUID(), null, "s1", "What can we improve?", List.of(), Map.of());
        AIPolicyDecision decision = new AIPolicyDecision(true, AIPermission.RECOMMENDATION, "ok",
                List.of("getFinancialHealth"), List.of());

        NeowalletTool getFinancialHealth = stubTool("getFinancialHealth", Map.of("score", 75));
        when(toolRegistry.get("getFinancialHealth")).thenReturn(Optional.of(getFinancialHealth));
        when(aiProvider.generate(anyString(), any(AIContext.class))).thenReturn("I recommend saving more.");

        AIResponse response = orchestrator.process(request, decision, new ToolContext(request.userId(), null, "OWNER"), IntentType.RECOMMENDATION_REQUEST);

        assertEquals(AIResponseType.RECOMMENDATION, response.type());
    }

    @Test
    void emptyBudgetData_returnsInsufficientData() {
        AIRequest request = new AIRequest(UUID.randomUUID(), null, "s1", "What is my budget?", List.of(), Map.of());
        AIPolicyDecision decision = new AIPolicyDecision(true, AIPermission.READ_ONLY, "ok",
                List.of("getBudget"), List.of());

        when(toolRegistry.get("getBudget")).thenReturn(Optional.empty());

        AIResponse response = orchestrator.process(request, decision, new ToolContext(request.userId(), null, "OWNER"), IntentType.BUDGET_ANALYSIS);

        assertEquals(AIResponseType.INSUFFICIENT_DATA, response.type());
        assertFalse(response.refusal());
    }

    @Test
    void emptySavingsData_returnsInsufficientData() {
        AIRequest request = new AIRequest(UUID.randomUUID(), null, "s1", "How is my savings goal?", List.of(), Map.of());
        AIPolicyDecision decision = new AIPolicyDecision(true, AIPermission.READ_ONLY, "ok",
                List.of("getSavingsGoals"), List.of());

        when(toolRegistry.get("getSavingsGoals")).thenReturn(Optional.empty());

        AIResponse response = orchestrator.process(request, decision, new ToolContext(request.userId(), null, "OWNER"), IntentType.SAVINGS_ANALYSIS);

        assertEquals(AIResponseType.INSUFFICIENT_DATA, response.type());
        assertFalse(response.refusal());
    }

    @Test
    void emptyTransactionData_returnsInsufficientData() {
        AIRequest request = new AIRequest(UUID.randomUUID(), null, "s1", "What did we spend?", List.of(), Map.of());
        AIPolicyDecision decision = new AIPolicyDecision(true, AIPermission.READ_ONLY, "ok",
                List.of("getTransactions"), List.of());

        NeowalletTool tool = mock(NeowalletTool.class);
        when(tool.name()).thenReturn("getTransactions");
        when(tool.execute(any(ToolContext.class), anyMap())).thenReturn(Map.of());
        when(toolRegistry.get("getTransactions")).thenReturn(Optional.of(tool));

        AIResponse response = orchestrator.process(request, decision, new ToolContext(request.userId(), null, "OWNER"), IntentType.TRANSACTION_ANALYSIS);

        assertEquals(AIResponseType.INSUFFICIENT_DATA, response.type());
    }

    @Test
    void emptyBillData_returnsInsufficientData() {
        AIRequest request = new AIRequest(UUID.randomUUID(), null, "s1", "What bills are due?", List.of(), Map.of());
        AIPolicyDecision decision = new AIPolicyDecision(true, AIPermission.READ_ONLY, "ok",
                List.of("getBills"), List.of());

        NeowalletTool tool = mock(NeowalletTool.class);
        when(tool.name()).thenReturn("getBills");
        when(tool.execute(any(ToolContext.class), anyMap())).thenReturn(Map.of());
        when(toolRegistry.get("getBills")).thenReturn(Optional.of(tool));

        AIResponse response = orchestrator.process(request, decision, new ToolContext(request.userId(), null, "OWNER"), IntentType.BILL_ANALYSIS);

        assertEquals(AIResponseType.INSUFFICIENT_DATA, response.type());
    }

    @Test
    void emptyFinancialHealthData_returnsInsufficientData() {
        AIRequest request = new AIRequest(UUID.randomUUID(), null, "s1", "What is our health score?", List.of(), Map.of());
        AIPolicyDecision decision = new AIPolicyDecision(true, AIPermission.READ_ONLY, "ok",
                List.of("getFinancialHealth"), List.of());

        when(toolRegistry.get("getFinancialHealth")).thenReturn(Optional.empty());

        AIResponse response = orchestrator.process(request, decision, new ToolContext(request.userId(), null, "OWNER"), IntentType.FINANCIAL_HEALTH_EXPLANATION);

        assertEquals(AIResponseType.INSUFFICIENT_DATA, response.type());
    }
}
