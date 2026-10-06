package com.neowallet.finance.engine;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FinancialHealthCalculationEngineTest {

    private final FinancialHealthCalculationEngine engine = new FinancialHealthCalculationEngine();

    @Test
    void newUser_zeroMonths_returnsNeutralProvisional() {
        FinancialHealthInput input = baseBuilder()
                .historicalMonths(0)
                .build();

        FinancialHealthResult result = engine.calculate(input);

        assertEquals(58, result.overallScore());
        assertEquals("NEEDS_ATTENTION", result.scoreLabel());
        assertEquals("LOW", result.confidence());
        assertEquals("PROVISIONAL", result.status());
    }

    @Test
    void establishedUser_sixMonths_returnsGoodHighConfidence() {
        List<FinancialHealthInput.BudgetCategoryInput> categories = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            categories.add(new FinancialHealthInput.BudgetCategoryInput("Cat" + i, new BigDecimal("100"), new BigDecimal("80")));
        }
        categories.add(new FinancialHealthInput.BudgetCategoryInput("Over1", new BigDecimal("100"), new BigDecimal("150")));
        categories.add(new FinancialHealthInput.BudgetCategoryInput("Over2", new BigDecimal("100"), new BigDecimal("150")));

        List<FinancialHealthInput.MonthlySpendingInput> spending = List.of(
                new FinancialHealthInput.MonthlySpendingInput("2026-05", new BigDecimal("5000")),
                new FinancialHealthInput.MonthlySpendingInput("2026-06", new BigDecimal("5000")),
                new FinancialHealthInput.MonthlySpendingInput("2026-07", new BigDecimal("5000")),
                new FinancialHealthInput.MonthlySpendingInput("2026-08", new BigDecimal("5250"))
        );

        List<FinancialHealthInput.SavingsGoalInput> goals = List.of(
                new FinancialHealthInput.SavingsGoalInput(new BigDecimal("10000"), new BigDecimal("20000"), LocalDate.of(2027, 8, 31)),
                new FinancialHealthInput.SavingsGoalInput(new BigDecimal("15000"), new BigDecimal("15000"), LocalDate.of(2027, 8, 31))
        );

        FinancialHealthInput input = baseBuilder()
                .historicalMonths(6)
                .budgetCategories(categories)
                .plannedSavings(new BigDecimal("10000"))
                .actualSavings(new BigDecimal("8000"))
                .monthlySpendings(spending)
                .bills(List.of(
                        new FinancialHealthInput.BillInput(LocalDate.of(2026, 8, 10), LocalDate.of(2026, 8, 10), "PAID"),
                        new FinancialHealthInput.BillInput(LocalDate.of(2026, 8, 15), LocalDate.of(2026, 8, 15), "PAID"),
                        new FinancialHealthInput.BillInput(LocalDate.of(2026, 8, 20), LocalDate.of(2026, 8, 20), "PAID")
                ))
                .emergencyTarget(new BigDecimal("150000"))
                .emergencyCurrent(new BigDecimal("75000"))
                .savingsGoals(goals)
                .build();

        FinancialHealthResult result = engine.calculate(input);

        assertTrue(result.overallScore() >= 70 && result.overallScore() <= 85, "Expected Good score, got " + result.overallScore());
        assertEquals("HIGH", result.confidence());
        assertEquals("FINAL", result.status());
    }

    @Test
    void noBudget_returnsNeutralBudgetAdherence() {
        FinancialHealthInput input = baseBuilder()
                .historicalMonths(6)
                .budgetCategories(List.of())
                .build();

        FinancialHealthResult result = engine.calculate(input);

        FinancialHealthResult.FactorResult budget = findFactor(result, "budgetAdherence");
        assertEquals(50, budget.score());
    }

    @Test
    void significantOverBudget_reducesAdherence() {
        List<FinancialHealthInput.BudgetCategoryInput> categories = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            categories.add(new FinancialHealthInput.BudgetCategoryInput("OK" + i, new BigDecimal("100"), new BigDecimal("80")));
        }
        categories.add(new FinancialHealthInput.BudgetCategoryInput("Bad", new BigDecimal("100"), new BigDecimal("250")));

        FinancialHealthInput input = baseBuilder()
                .historicalMonths(6)
                .budgetCategories(categories)
                .build();

        FinancialHealthResult result = engine.calculate(input);

        FinancialHealthResult.FactorResult budget = findFactor(result, "budgetAdherence");
        assertTrue(budget.score() < 95, "Budget adherence should reflect significant over-budget, got " + budget.score());
    }

    @Test
    void noSavingsPlan_returnsNeutralSavings() {
        FinancialHealthInput input = baseBuilder()
                .historicalMonths(6)
                .plannedSavings(BigDecimal.ZERO)
                .actualSavings(BigDecimal.ZERO)
                .build();

        FinancialHealthResult result = engine.calculate(input);

        FinancialHealthResult.FactorResult savings = findFactor(result, "savingsBehavior");
        assertEquals(50, savings.score());
    }

    @Test
    void negativeSavings_returnsZero() {
        FinancialHealthInput input = baseBuilder()
                .historicalMonths(6)
                .plannedSavings(new BigDecimal("1000"))
                .actualSavings(new BigDecimal("-500"))
                .build();

        FinancialHealthResult result = engine.calculate(input);

        FinancialHealthResult.FactorResult savings = findFactor(result, "savingsBehavior");
        assertEquals(0, savings.score());
    }

    @Test
    void noBills_returnsHundredBillDiscipline() {
        FinancialHealthInput input = baseBuilder()
                .historicalMonths(6)
                .bills(List.of())
                .build();

        FinancialHealthResult result = engine.calculate(input);

        FinancialHealthResult.FactorResult bills = findFactor(result, "billDiscipline");
        assertEquals(100, bills.score());
    }

    @Test
    void noEmergencyTarget_returnsNeutral() {
        FinancialHealthInput input = baseBuilder()
                .historicalMonths(6)
                .emergencyTarget(BigDecimal.ZERO)
                .emergencyCurrent(BigDecimal.ZERO)
                .build();

        FinancialHealthResult result = engine.calculate(input);

        FinancialHealthResult.FactorResult emergency = findFactor(result, "emergencyPreparedness");
        assertEquals(50, emergency.score());
    }

    @Test
    void noSavingsGoals_returnsNeutral() {
        FinancialHealthInput input = baseBuilder()
                .historicalMonths(6)
                .savingsGoals(List.of())
                .build();

        FinancialHealthResult result = engine.calculate(input);

        FinancialHealthResult.FactorResult goals = findFactor(result, "goalProgress");
        assertEquals(50, goals.score());
    }

    @Test
    void zeroHistoricalMonths_lowConfidence() {
        FinancialHealthInput input = baseBuilder()
                .historicalMonths(0)
                .build();

        FinancialHealthResult result = engine.calculate(input);

        assertEquals("LOW", result.confidence());
        assertEquals("PROVISIONAL", result.status());
    }

    @Test
    void oneHistoricalMonth_lowConfidence() {
        FinancialHealthInput input = baseBuilder()
                .historicalMonths(1)
                .build();

        FinancialHealthResult result = engine.calculate(input);

        assertEquals("LOW", result.confidence());
    }

    @Test
    void twoHistoricalMonths_mediumConfidence() {
        FinancialHealthInput input = completeBuilder()
                .historicalMonths(2)
                .build();

        FinancialHealthResult result = engine.calculate(input);

        assertEquals("MEDIUM", result.confidence());
    }

    @Test
    void threeHistoricalMonths_highConfidence() {
        FinancialHealthInput input = completeBuilder()
                .historicalMonths(3)
                .build();

        FinancialHealthResult result = engine.calculate(input);

        assertEquals("HIGH", result.confidence());
    }

    @Test
    void scoreCannotExceedHundred() {
        List<FinancialHealthInput.BudgetCategoryInput> categories = List.of(
                new FinancialHealthInput.BudgetCategoryInput("A", new BigDecimal("100"), new BigDecimal("0"))
        );
        FinancialHealthInput input = baseBuilder()
                .historicalMonths(6)
                .budgetCategories(categories)
                .plannedSavings(new BigDecimal("100"))
                .actualSavings(new BigDecimal("1000"))
                .savingsGoals(List.of(new FinancialHealthInput.SavingsGoalInput(new BigDecimal("1000"), new BigDecimal("100"), LocalDate.of(2027, 1, 1))))
                .build();

        FinancialHealthResult result = engine.calculate(input);

        assertTrue(result.overallScore() <= 100);
    }

    @Test
    void factorScoresInRange() {
        FinancialHealthInput input = baseBuilder().build();

        FinancialHealthResult result = engine.calculate(input);

        for (FinancialHealthResult.FactorResult f : result.factors()) {
            assertTrue(f.score() >= 0 && f.score() <= 100, "Factor " + f.name() + " out of range: " + f.score());
        }
    }

    private FinancialHealthInput.Builder baseBuilder() {
        return new FinancialHealthInput.Builder()
                .monthlyIncome(new BigDecimal("80000"))
                .historicalMonths(0)
                .budgetCategories(List.of())
                .plannedSavings(BigDecimal.ZERO)
                .actualSavings(BigDecimal.ZERO)
                .monthlySpendings(List.of())
                .bills(List.of())
                .emergencyTarget(BigDecimal.ZERO)
                .emergencyCurrent(BigDecimal.ZERO)
                .savingsGoals(List.of())
                .previousScore(null);
    }

    private FinancialHealthInput.Builder completeBuilder() {
        return new FinancialHealthInput.Builder()
                .monthlyIncome(new BigDecimal("80000"))
                .historicalMonths(3)
                .budgetCategories(List.of(new FinancialHealthInput.BudgetCategoryInput("Food", new BigDecimal("100"), new BigDecimal("80"))))
                .plannedSavings(new BigDecimal("1000"))
                .actualSavings(new BigDecimal("1000"))
                .monthlySpendings(List.of(
                        new FinancialHealthInput.MonthlySpendingInput("2026-06", new BigDecimal("5000")),
                        new FinancialHealthInput.MonthlySpendingInput("2026-07", new BigDecimal("5000")),
                        new FinancialHealthInput.MonthlySpendingInput("2026-08", new BigDecimal("5000"))
                ))
                .bills(List.of(new FinancialHealthInput.BillInput(LocalDate.of(2026, 8, 15), LocalDate.of(2026, 8, 15), "PAID")))
                .emergencyTarget(new BigDecimal("150000"))
                .emergencyCurrent(new BigDecimal("75000"))
                .savingsGoals(List.of(new FinancialHealthInput.SavingsGoalInput(new BigDecimal("5000"), new BigDecimal("10000"), LocalDate.of(2027, 1, 1))))
                .previousScore(null);
    }

    private FinancialHealthResult.FactorResult findFactor(FinancialHealthResult result, String name) {
        return result.factors().stream().filter(f -> f.name().equals(name)).findFirst().orElseThrow();
    }
}
