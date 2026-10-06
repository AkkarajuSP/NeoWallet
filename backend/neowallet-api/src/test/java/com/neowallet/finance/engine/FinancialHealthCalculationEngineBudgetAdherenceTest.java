package com.neowallet.finance.engine;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FinancialHealthCalculationEngineBudgetAdherenceTest {

    private final FinancialHealthCalculationEngine engine = new FinancialHealthCalculationEngine();

    @Test
    void allSlightlyUnderBudget_scoreIs100() {
        List<FinancialHealthInput.BudgetCategoryInput> categories = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            categories.add(new FinancialHealthInput.BudgetCategoryInput("Cat" + i, new BigDecimal("100"), new BigDecimal("80")));
        }
        FinancialHealthInput input = baseBuilder().historicalMonths(6).budgetCategories(categories).build();
        assertEquals(100, findFactor(engine.calculate(input), "budgetAdherence").score(), "All categories at 80% utilization yield a category score of 100");
    }

    @Test
    void oneSlightlyOverBudget_scoreReflectsMagnitude() {
        List<FinancialHealthInput.BudgetCategoryInput> categories = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            categories.add(new FinancialHealthInput.BudgetCategoryInput("Under" + i, new BigDecimal("100"), new BigDecimal("80")));
        }
        categories.add(new FinancialHealthInput.BudgetCategoryInput("SlightlyOver", new BigDecimal("100"), new BigDecimal("110")));
        FinancialHealthInput input = baseBuilder().historicalMonths(6).budgetCategories(categories).build();

        FinancialHealthResult.FactorResult factor = findFactor(engine.calculate(input), "budgetAdherence");
        // 9 × 100 + 50 (at 110% utilization) = 950 / 10 = 95
        assertEquals(95, factor.score(), "Slightly over-budget category reduces score by a magnitude-based amount");
    }

    @Test
    void oneSignificantlyOverBudget_scoreReflectsMagnitude() {
        List<FinancialHealthInput.BudgetCategoryInput> categories = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            categories.add(new FinancialHealthInput.BudgetCategoryInput("Under" + i, new BigDecimal("100"), new BigDecimal("80")));
        }
        categories.add(new FinancialHealthInput.BudgetCategoryInput("SignificantlyOver", new BigDecimal("100"), new BigDecimal("150")));
        FinancialHealthInput input = baseBuilder().historicalMonths(6).budgetCategories(categories).build();

        FinancialHealthResult.FactorResult factor = findFactor(engine.calculate(input), "budgetAdherence");
        // 9 × 100 + 0 (at 150% utilization) = 900 / 10 = 90
        assertEquals(90, factor.score());
    }

    @Test
    void severalSlightlyOverBudget_scoreReflectsMagnitude() {
        List<FinancialHealthInput.BudgetCategoryInput> categories = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            categories.add(new FinancialHealthInput.BudgetCategoryInput("Under" + i, new BigDecimal("100"), new BigDecimal("80")));
        }
        for (int i = 0; i < 3; i++) {
            categories.add(new FinancialHealthInput.BudgetCategoryInput("SlightlyOver" + i, new BigDecimal("100"), new BigDecimal("110")));
        }
        FinancialHealthInput input = baseBuilder().historicalMonths(6).budgetCategories(categories).build();

        FinancialHealthResult.FactorResult factor = findFactor(engine.calculate(input), "budgetAdherence");
        // 7 × 100 + 3 × 50 = 850 / 10 = 85
        assertEquals(85, factor.score());
    }

    @Test
    void oneEssentialSignificantlyOverBudget_scoreReflectsMagnitude() {
        List<FinancialHealthInput.BudgetCategoryInput> categories = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            categories.add(new FinancialHealthInput.BudgetCategoryInput("Under" + i, new BigDecimal("100"), new BigDecimal("80")));
        }
        categories.add(new FinancialHealthInput.BudgetCategoryInput("EssentialOver", new BigDecimal("100"), new BigDecimal("200")));
        FinancialHealthInput input = baseBuilder().historicalMonths(6).budgetCategories(categories).build();

        FinancialHealthResult.FactorResult factor = findFactor(engine.calculate(input), "budgetAdherence");
        // 9 × 100 + 0 (at 200% utilization) = 900 / 10 = 90
        assertEquals(90, factor.score());
    }

    @Test
    void noBudget_scoreIsNeutral() {
        FinancialHealthInput input = baseBuilder().historicalMonths(6).budgetCategories(List.of()).build();
        FinancialHealthResult.FactorResult factor = findFactor(engine.calculate(input), "budgetAdherence");
        assertEquals(50, factor.score());
    }

    @Test
    void utilization0_percent_scoreIs100() {
        FinancialHealthInput input = baseBuilder()
                .historicalMonths(6)
                .budgetCategories(List.of(new FinancialHealthInput.BudgetCategoryInput("A", new BigDecimal("100"), BigDecimal.ZERO)))
                .build();
        FinancialHealthResult.FactorResult factor = findFactor(engine.calculate(input), "budgetAdherence");
        assertEquals(100, factor.score());
    }

    @Test
    void utilization80_percent_scoreIs100() {
        FinancialHealthInput input = baseBuilder()
                .historicalMonths(6)
                .budgetCategories(List.of(new FinancialHealthInput.BudgetCategoryInput("A", new BigDecimal("100"), new BigDecimal("80"))))
                .build();
        FinancialHealthResult.FactorResult factor = findFactor(engine.calculate(input), "budgetAdherence");
        assertEquals(100, factor.score());
    }

    @Test
    void utilization100_percent_scoreIs80() {
        FinancialHealthInput input = baseBuilder()
                .historicalMonths(6)
                .budgetCategories(List.of(new FinancialHealthInput.BudgetCategoryInput("A", new BigDecimal("100"), new BigDecimal("100"))))
                .build();
        FinancialHealthResult.FactorResult factor = findFactor(engine.calculate(input), "budgetAdherence");
        assertEquals(80, factor.score());
    }

    @Test
    void utilization120_percent_scoreIs20() {
        FinancialHealthInput input = baseBuilder()
                .historicalMonths(6)
                .budgetCategories(List.of(new FinancialHealthInput.BudgetCategoryInput("A", new BigDecimal("100"), new BigDecimal("120"))))
                .build();
        FinancialHealthResult.FactorResult factor = findFactor(engine.calculate(input), "budgetAdherence");
        assertEquals(20, factor.score());
    }

    @Test
    void utilization150_percent_scoreIs0() {
        FinancialHealthInput input = baseBuilder()
                .historicalMonths(6)
                .budgetCategories(List.of(new FinancialHealthInput.BudgetCategoryInput("A", new BigDecimal("100"), new BigDecimal("150"))))
                .build();
        FinancialHealthResult.FactorResult factor = findFactor(engine.calculate(input), "budgetAdherence");
        assertEquals(0, factor.score());
    }

    @Test
    void utilizationAbove150_percent_scoreIs0() {
        FinancialHealthInput input = baseBuilder()
                .historicalMonths(6)
                .budgetCategories(List.of(new FinancialHealthInput.BudgetCategoryInput("A", new BigDecimal("100"), new BigDecimal("250"))))
                .build();
        FinancialHealthResult.FactorResult factor = findFactor(engine.calculate(input), "budgetAdherence");
        assertEquals(0, factor.score());
    }

    @Test
    void factorAndOverallScoreRemainWithinBounds() {
        FinancialHealthInput input = baseBuilder().build();
        FinancialHealthResult result = engine.calculate(input);
        assertTrue(result.overallScore() >= 0 && result.overallScore() <= 100, "Overall score out of bounds: " + result.overallScore());
        for (FinancialHealthResult.FactorResult f : result.factors()) {
            assertTrue(f.score() >= 0 && f.score() <= 100, "Factor " + f.name() + " out of bounds: " + f.score());
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

    private FinancialHealthResult.FactorResult findFactor(FinancialHealthResult result, String name) {
        return result.factors().stream().filter(f -> f.name().equals(name)).findFirst().orElseThrow();
    }
}
