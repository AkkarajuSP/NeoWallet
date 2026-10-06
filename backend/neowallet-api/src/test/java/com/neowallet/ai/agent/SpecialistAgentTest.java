package com.neowallet.ai.agent;

import com.neowallet.ai.dto.AIRequest;
import com.neowallet.ai.dto.AIResponseType;
import com.neowallet.ai.dto.IntentType;
import com.neowallet.ai.dto.ToolExecutionRecord;
import com.neowallet.ai.policy.AIPermission;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SpecialistAgentTest {

    private AIRequest request(String message) {
        return new AIRequest(UUID.randomUUID(), null, "s1", message, List.of(), Map.of());
    }

    private ToolExecutionRecord record(String toolName) {
        return new ToolExecutionRecord(toolName, true, true, "{\"tool\": \"" + toolName + "\"}", Map.of("tool", toolName));
    }

    @Test
    void financialAnalysisAgent_buildsSpendingAnalysisPrompt() {
        FinancialAnalysisAgent agent = new FinancialAnalysisAgent();
        String prompt = agent.buildPrompt(request("What did I spend this month?"), List.of(record("getFinancialOverview")), IntentType.FINANCIAL_OVERVIEW, AIPermission.READ_ONLY);

        assertTrue(prompt.contains("Neo Financial Analysis Agent"));
        assertTrue(prompt.contains("Do not calculate or invent financial numbers"));
        assertTrue(prompt.contains("getFinancialOverview"));
        assertEquals(AIResponseType.FACT, agent.resolveResponseType(IntentType.FINANCIAL_OVERVIEW, AIPermission.READ_ONLY, "You spent..."));
        assertEquals(AIResponseType.ANALYSIS, agent.resolveResponseType(IntentType.TRANSACTION_ANALYSIS, AIPermission.READ_ONLY, "Your spending..."));
    }

    @Test
    void budgetAgent_buildsUtilizationPrompt() {
        BudgetAgent agent = new BudgetAgent();
        String prompt = agent.buildPrompt(request("How much of my budget is left?"), List.of(record("getBudget")), IntentType.BUDGET_ANALYSIS, AIPermission.READ_ONLY);

        assertTrue(prompt.contains("Neo Budget Agent"));
        assertTrue(prompt.contains("You cannot modify budgets"));
        assertTrue(prompt.contains("getBudget"));
        assertEquals(AIResponseType.ANALYSIS, agent.resolveResponseType(IntentType.BUDGET_ANALYSIS, AIPermission.READ_ONLY, "You have used..."));
        assertEquals(AIResponseType.RECOMMENDATION, agent.resolveResponseType(IntentType.BUDGET_ANALYSIS, AIPermission.RECOMMENDATION, "I recommend..."));
    }

    @Test
    void savingsAgent_buildsGoalProgressPrompt() {
        SavingsAgent agent = new SavingsAgent();
        String prompt = agent.buildPrompt(request("How are my savings goals?"), List.of(record("getSavingsGoals")), IntentType.SAVINGS_ANALYSIS, AIPermission.READ_ONLY);

        assertTrue(prompt.contains("Neo Savings Agent"));
        assertTrue(prompt.contains("You cannot modify savings goals"));
        assertTrue(prompt.contains("getSavingsGoals"));
        assertEquals(AIResponseType.ANALYSIS, agent.resolveResponseType(IntentType.SAVINGS_ANALYSIS, AIPermission.READ_ONLY, "Your goals..."));
        assertEquals(AIResponseType.RECOMMENDATION, agent.resolveResponseType(IntentType.SAVINGS_ANALYSIS, AIPermission.RECOMMENDATION, "I recommend saving..."));
    }

    @Test
    void billAgent_buildsUpcomingBillsPrompt() {
        BillAgent agent = new BillAgent();
        String prompt = agent.buildPrompt(request("What bills are due?"), List.of(record("getBills")), IntentType.BILL_ANALYSIS, AIPermission.READ_ONLY);

        assertTrue(prompt.contains("Neo Bill Agent"));
        assertTrue(prompt.contains("You cannot pay"));
        assertTrue(prompt.contains("getBills"));
        assertEquals(AIResponseType.ANALYSIS, agent.resolveResponseType(IntentType.BILL_ANALYSIS, AIPermission.READ_ONLY, "Upcoming bills..."));
        assertEquals(AIResponseType.RECOMMENDATION, agent.resolveResponseType(IntentType.BILL_ANALYSIS, AIPermission.RECOMMENDATION, "I recommend..."));
    }

    @Test
    void financialHealthAgent_buildsScoreExplanationPrompt() {
        FinancialHealthAgent agent = new FinancialHealthAgent();
        String prompt = agent.buildPrompt(request("Why did my health score change?"), List.of(record("getFinancialHealth")), IntentType.FINANCIAL_HEALTH_EXPLANATION, AIPermission.READ_ONLY);

        assertTrue(prompt.contains("Neo Financial Health Agent"));
        assertTrue(prompt.contains("must NOT calculate"));
        assertTrue(prompt.contains("getFinancialHealth"));
        assertEquals(AIResponseType.ANALYSIS, agent.resolveResponseType(IntentType.FINANCIAL_HEALTH_EXPLANATION, AIPermission.READ_ONLY, "Your score changed..."));
        assertEquals(AIResponseType.FACT, agent.resolveResponseType(IntentType.FINANCIAL_HEALTH_QUERY, AIPermission.READ_ONLY, "Your score is 75."));
    }

    @Test
    void conciergeAgent_buildsGeneralQuestionPrompt() {
        ConciergeAgent agent = new ConciergeAgent();
        String prompt = agent.buildPrompt(request("What is Neo?"), List.of(), IntentType.GREETING, AIPermission.READ_ONLY);

        assertTrue(prompt.contains("Neo"));
        assertTrue(prompt.contains("read-only financial concierge"));
        assertEquals(AIResponseType.ANALYSIS, agent.resolveResponseType(IntentType.GREETING, AIPermission.READ_ONLY, "Hello!"));
        assertEquals(AIResponseType.RECOMMENDATION, agent.resolveResponseType(IntentType.RECOMMENDATION_REQUEST, AIPermission.RECOMMENDATION, "I recommend..."));
    }

    @Test
    void allAgents_refuseBlankText() {
        Agent[] agents = { new FinancialAnalysisAgent(), new BudgetAgent(), new SavingsAgent(), new BillAgent(), new FinancialHealthAgent(), new ConciergeAgent() };
        for (Agent agent : agents) {
            assertEquals(AIResponseType.INSUFFICIENT_DATA, agent.resolveResponseType(IntentType.GENERAL_FINANCIAL_QUESTION, AIPermission.READ_ONLY, ""), agent.name());
        }
    }
}
