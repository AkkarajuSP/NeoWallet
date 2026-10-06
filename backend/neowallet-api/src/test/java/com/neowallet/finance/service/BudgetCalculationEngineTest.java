package com.neowallet.finance.service;

import com.neowallet.finance.dto.BudgetRecommendationResponse;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BudgetCalculationEngineTest {

    private final BudgetCalculationEngine engine = new BudgetCalculationEngine();

    @Test
    void newUserZeroHistoricalData() {
        BudgetCalculationEngine.CalculationInput input = new BudgetCalculationEngine.CalculationInput(
            new BigDecimal("80000"),
            Collections.emptyList(),
            new BigDecimal("0.05"),
            new BigDecimal("0.05")
        );

        BudgetRecommendationResponse.RecommendedAllocation allocation = engine.calculateAllocation(input);

        assertThat(allocation.planningIncome()).isEqualByComparingTo(new BigDecimal("80000.00"));
        assertThat(allocation.essentialAllocation().setScale(0)).isEqualByComparingTo(new BigDecimal("68000"));
        assertThat(allocation.discretionaryPlanning()).isEqualByComparingTo(BigDecimal.ZERO.setScale(2));

        List<BudgetRecommendationResponse.RecommendedCategory> categories = engine.calculateCategories(input);
        assertThat(categories).hasSize(10);
        assertThat(categories).anyMatch(c -> "Groceries".equals(c.category()) && c.recommendedLimit().compareTo(new BigDecimal("12000.00")) == 0);
    }

    @Test
    void historicalDataThreePlusMonths() {
        List<BudgetCalculationEngine.CategoryHistory> histories = Arrays.asList(
            new BudgetCalculationEngine.CategoryHistory("Groceries", Arrays.asList(new BigDecimal("8140"), new BigDecimal("10000"), new BigDecimal("10500"), new BigDecimal("11340"))),
            new BudgetCalculationEngine.CategoryHistory("Housing", Arrays.asList(new BigDecimal("22000"), new BigDecimal("22000"), new BigDecimal("22000"), new BigDecimal("22000"))),
            new BudgetCalculationEngine.CategoryHistory("Utilities", Arrays.asList(new BigDecimal("7000"), new BigDecimal("7200"), new BigDecimal("7400"), new BigDecimal("7600"))),
            new BudgetCalculationEngine.CategoryHistory("Transportation", Arrays.asList(new BigDecimal("6000"), new BigDecimal("6000"), new BigDecimal("6000"), new BigDecimal("5880")))
        );

        BudgetCalculationEngine.CalculationInput input = new BudgetCalculationEngine.CalculationInput(
            new BigDecimal("80000"),
            histories,
            new BigDecimal("0.10"),
            new BigDecimal("0.05")
        );

        List<BudgetRecommendationResponse.RecommendedCategory> categories = engine.calculateCategories(input);
        BudgetRecommendationResponse.RecommendedCategory groceries = categories.stream().filter(c -> "Groceries".equals(c.category())).findFirst().orElseThrow();
        assertThat(groceries.recommendedLimit()).isEqualTo(new BigDecimal("11340.00"));
    }

    @Test
    void noHistoricalDataDeficit() {
        BudgetCalculationEngine.CalculationInput input = new BudgetCalculationEngine.CalculationInput(
            new BigDecimal("50000"),
            Collections.emptyList(),
            new BigDecimal("0.10"),
            new BigDecimal("0.05")
        );

        BudgetRecommendationResponse.RecommendedAllocation allocation = engine.calculateAllocation(input);
        assertThat(allocation.discretionaryPlanning()).isEqualTo(new BigDecimal("0.00"));
    }

    @Test
    void determineConfidenceAndStatus() {
        assertThat(engine.determineConfidence(3)).isEqualTo("HIGH");
        assertThat(engine.determineConfidence(2)).isEqualTo("MEDIUM");
        assertThat(engine.determineConfidence(0)).isEqualTo("LOW");

        assertThat(engine.determineStatus(new BigDecimal("79"), 80, 100)).isEqualTo("WITHIN_BUDGET");
        assertThat(engine.determineStatus(new BigDecimal("80"), 80, 100)).isEqualTo("NEAR_LIMIT");
        assertThat(engine.determineStatus(new BigDecimal("100"), 80, 100)).isEqualTo("OVER_BUDGET");
    }
}
