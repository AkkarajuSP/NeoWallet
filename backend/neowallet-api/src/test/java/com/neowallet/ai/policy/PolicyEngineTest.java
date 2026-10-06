package com.neowallet.ai.policy;

import com.neowallet.ai.dto.IntentType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PolicyEngineTest {

    private final PolicyEngine engine = new PolicyEngine();

    @Test
    void financialQuery_isAllowed_readOnly() {
        AIPolicyDecision decision = engine.evaluate("how much do we have", IntentType.FINANCIAL_QUERY, List.of("getFinancialOverview"), "MEMBER");
        assertTrue(decision.allowed());
        assertEquals(AIPermission.READ_ONLY, decision.permission());
        assertTrue(decision.allowedTools().contains("getFinancialOverview"));
    }

    @Test
    void recommendationRequest_isAllowed_recommendation() {
        AIPolicyDecision decision = engine.evaluate("what should we improve", IntentType.RECOMMENDATION_REQUEST, List.of("getFinancialHealth"), "MEMBER");
        assertTrue(decision.allowed());
        assertEquals(AIPermission.RECOMMENDATION, decision.permission());
    }

    @Test
    void financialExecution_isDenied() {
        AIPolicyDecision decision = engine.evaluate("pay my bill", IntentType.DENIED_EXECUTION, List.of("payBill"), "OWNER");
        assertFalse(decision.allowed());
        assertEquals(AIPermission.FINANCIAL_EXECUTION, decision.permission());
    }

    @Test
    void restrictedUser_cannotAccessNonReadTool() {
        AIPolicyDecision decision = engine.evaluate("what is our budget", IntentType.FINANCIAL_QUERY, List.of("getBudget", "payBill"), "RESTRICTED");
        assertFalse(decision.allowed());
    }

    @Test
    void crossFamilyIntent_denied() {
        AIPolicyDecision decision = engine.evaluate("transfer money", IntentType.DENIED_EXECUTION, List.of("transfer"), "OWNER");
        assertFalse(decision.allowed());
    }
}
