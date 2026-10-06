package com.neowallet.finance.service;

import com.neowallet.finance.dto.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.*;

@Component
public class BudgetCalculationEngine {

    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_UP;
    private static final MathContext MC = new MathContext(16, ROUNDING);

    private static final BigDecimal TREND_CAP = new BigDecimal("0.20");
    private static final BigDecimal OUTLIER_STD_MULTIPLIER = new BigDecimal("2");
    private static final BigDecimal ONE_TIME_MULTIPLIER = new BigDecimal("3");

    private static final Map<String, BigDecimal> DEFAULT_PERCENTAGES = new LinkedHashMap<>();
    private static final Map<String, String> DEFAULT_PRIORITIES = new LinkedHashMap<>();

    static {
        DEFAULT_PERCENTAGES.put("Housing", new BigDecimal("0.30"));
        DEFAULT_PERCENTAGES.put("Groceries", new BigDecimal("0.15"));
        DEFAULT_PERCENTAGES.put("Utilities", new BigDecimal("0.10"));
        DEFAULT_PERCENTAGES.put("Transportation", new BigDecimal("0.10"));
        DEFAULT_PERCENTAGES.put("Healthcare", new BigDecimal("0.05"));
        DEFAULT_PERCENTAGES.put("Insurance", new BigDecimal("0.05"));
        DEFAULT_PERCENTAGES.put("Education", new BigDecimal("0.10"));
        DEFAULT_PERCENTAGES.put("Entertainment", new BigDecimal("0.05"));
        DEFAULT_PERCENTAGES.put("Savings", new BigDecimal("0.05"));
        DEFAULT_PERCENTAGES.put("Emergency", new BigDecimal("0.05"));

        DEFAULT_PRIORITIES.put("Housing", "ESSENTIAL");
        DEFAULT_PRIORITIES.put("Groceries", "ESSENTIAL");
        DEFAULT_PRIORITIES.put("Utilities", "ESSENTIAL");
        DEFAULT_PRIORITIES.put("Transportation", "ESSENTIAL");
        DEFAULT_PRIORITIES.put("Healthcare", "ESSENTIAL");
        DEFAULT_PRIORITIES.put("Insurance", "ESSENTIAL");
        DEFAULT_PRIORITIES.put("Education", "ESSENTIAL");
        DEFAULT_PRIORITIES.put("Entertainment", "VARIABLE");
        DEFAULT_PRIORITIES.put("Savings", "DISCRETIONARY");
        DEFAULT_PRIORITIES.put("Emergency", "DISCRETIONARY");
    }

    public static class CategoryHistory {
        public final String category;
        public final List<BigDecimal> monthlyTotals;

        public CategoryHistory(String category, List<BigDecimal> monthlyTotals) {
            this.category = category;
            this.monthlyTotals = monthlyTotals;
        }
    }

    public static class CalculationInput {
        public final BigDecimal monthlyIncome;
        public final List<CategoryHistory> categoryHistories;
        public final BigDecimal emergencyFundPercentage;
        public final BigDecimal savingsPercentage;

        public CalculationInput(BigDecimal monthlyIncome, List<CategoryHistory> categoryHistories,
                                BigDecimal emergencyFundPercentage, BigDecimal savingsPercentage) {
            this.monthlyIncome = monthlyIncome;
            this.categoryHistories = categoryHistories != null ? categoryHistories : Collections.emptyList();
            this.emergencyFundPercentage = emergencyFundPercentage != null ? emergencyFundPercentage : new BigDecimal("0.10");
            this.savingsPercentage = savingsPercentage != null ? savingsPercentage : new BigDecimal("0.05");
        }
    }

    public BudgetRecommendationResponse.RecommendedAllocation calculateAllocation(CalculationInput input) {
        Map<String, BigDecimal> categoryBudgets = calculateCategoryBudgets(input);

        BigDecimal essential = BigDecimal.ZERO;
        BigDecimal variable = BigDecimal.ZERO;
        BigDecimal savings = categoryBudgets.getOrDefault("Savings", input.monthlyIncome.multiply(input.savingsPercentage, MC));
        BigDecimal emergency = categoryBudgets.getOrDefault("Emergency", input.monthlyIncome.multiply(input.emergencyFundPercentage, MC));

        for (String category : categoryBudgets.keySet()) {
            if ("Savings".equals(category) || "Emergency".equals(category)) {
                continue;
            }
            String priority = DEFAULT_PRIORITIES.getOrDefault(category, "VARIABLE");
            BigDecimal amount = categoryBudgets.get(category);
            if ("ESSENTIAL".equals(priority)) {
                essential = essential.add(amount, MC);
            } else {
                variable = variable.add(amount, MC);
            }
        }

        BigDecimal discretionary = input.monthlyIncome
            .subtract(essential, MC)
            .subtract(variable, MC)
            .subtract(savings, MC)
            .subtract(emergency, MC);

        return BudgetRecommendationResponse.RecommendedAllocation.builder()
            .planningIncome(input.monthlyIncome)
            .essentialAllocation(essential.setScale(SCALE, ROUNDING))
            .variableAllocation(variable.setScale(SCALE, ROUNDING))
            .savingsAllocation(savings.setScale(SCALE, ROUNDING))
            .emergencyAllocation(emergency.setScale(SCALE, ROUNDING))
            .discretionaryPlanning(discretionary.setScale(SCALE, ROUNDING))
            .build();
    }

    public List<BudgetRecommendationResponse.RecommendedCategory> calculateCategories(CalculationInput input) {
        Map<String, BigDecimal> budgets = calculateCategoryBudgets(input);
        List<BudgetRecommendationResponse.RecommendedCategory> result = new ArrayList<>();
        for (String category : budgets.keySet()) {
            BigDecimal historicalAverage = averageForCategory(input, category);
            result.add(BudgetRecommendationResponse.RecommendedCategory.builder()
                .category(category)
                .recommendedLimit(budgets.get(category).setScale(SCALE, ROUNDING))
                .basedOnHistoricalAverage(historicalAverage.setScale(SCALE, ROUNDING))
                .priority(DEFAULT_PRIORITIES.getOrDefault(category, "VARIABLE"))
                .build());
        }
        return result;
    }

    private Map<String, BigDecimal> calculateCategoryBudgets(CalculationInput input) {
        Map<String, BigDecimal> result = new LinkedHashMap<>();

        for (String category : DEFAULT_PERCENTAGES.keySet()) {
            BigDecimal historicalAverage = averageForCategory(input, category);
            BigDecimal historicalMonths = countHistoricalMonths(input, category);
            BigDecimal budget;
            BigDecimal priorityWeight = BigDecimal.ONE;

            if (historicalMonths.compareTo(BigDecimal.valueOf(3)) >= 0) {
                BigDecimal trend = calculateTrend(input, category);
                budget = historicalAverage.multiply(BigDecimal.ONE.add(trend), MC).multiply(priorityWeight, MC);
            } else if (historicalMonths.compareTo(BigDecimal.ZERO) > 0) {
                budget = historicalAverage.multiply(priorityWeight, MC);
            } else {
                budget = input.monthlyIncome.multiply(DEFAULT_PERCENTAGES.get(category), MC);
            }

            result.put(category, budget);
        }

        for (CategoryHistory history : input.categoryHistories) {
            if (!DEFAULT_PERCENTAGES.containsKey(history.category)) {
                BigDecimal historicalAverage = calculateHistoricalAverage(history.monthlyTotals);
                BigDecimal historicalMonths = BigDecimal.valueOf(countNonOutlierMonths(history.monthlyTotals));
                BigDecimal budget;
                if (historicalMonths.compareTo(BigDecimal.valueOf(3)) >= 0) {
                    BigDecimal trend = calculateTrendFromValues(history.monthlyTotals);
                    budget = historicalAverage.multiply(BigDecimal.ONE.add(trend), MC);
                } else if (historicalMonths.compareTo(BigDecimal.ZERO) > 0) {
                    budget = historicalAverage;
                } else {
                    budget = BigDecimal.ZERO;
                }
                result.put(history.category, budget);
            }
        }

        return result;
    }

    private BigDecimal averageForCategory(CalculationInput input, String category) {
        for (CategoryHistory history : input.categoryHistories) {
            if (category.equals(history.category)) {
                return calculateHistoricalAverage(history.monthlyTotals);
            }
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal countHistoricalMonths(CalculationInput input, String category) {
        for (CategoryHistory history : input.categoryHistories) {
            if (category.equals(history.category)) {
                return BigDecimal.valueOf(countNonOutlierMonths(history.monthlyTotals));
            }
        }
        return BigDecimal.ZERO;
    }

    private int countNonOutlierMonths(List<BigDecimal> monthlyTotals) {
        return removeOutliers(monthlyTotals).size();
    }

    private BigDecimal calculateHistoricalAverage(List<BigDecimal> values) {
        List<BigDecimal> filtered = removeOutliers(values);
        filtered = removeOneTimeExpenses(filtered);

        if (filtered.isEmpty()) {
            return BigDecimal.ZERO;
        }

        if (filtered.size() >= 4) {
            return calculateWeightedAverage(filtered);
        }
        return calculateSimpleAverage(filtered);
    }

    private BigDecimal calculateSimpleAverage(List<BigDecimal> values) {
        BigDecimal sum = BigDecimal.ZERO;
        for (BigDecimal v : values) {
            sum = sum.add(v, MC);
        }
        return sum.divide(BigDecimal.valueOf(values.size()), SCALE, ROUNDING);
    }

    private BigDecimal calculateWeightedAverage(List<BigDecimal> values) {
        List<BigDecimal> recent = values.subList(Math.max(0, values.size() - 4), values.size());
        BigDecimal[] weights = {
            new BigDecimal("0.4"), new BigDecimal("0.3"), new BigDecimal("0.2"), new BigDecimal("0.1")
        };
        BigDecimal weightedSum = BigDecimal.ZERO;
        BigDecimal weightSum = BigDecimal.ZERO;
        for (int i = 0; i < recent.size(); i++) {
            int index = recent.size() - 1 - i;
            BigDecimal w = weights[Math.min(i, weights.length - 1)];
            weightedSum = weightedSum.add(recent.get(index).multiply(w, MC), MC);
            weightSum = weightSum.add(w, MC);
        }
        return weightedSum.divide(weightSum, SCALE, ROUNDING);
    }

    private BigDecimal calculateTrend(CalculationInput input, String category) {
        for (CategoryHistory history : input.categoryHistories) {
            if (category.equals(history.category)) {
                return calculateTrendFromValues(history.monthlyTotals);
            }
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal calculateTrendFromValues(List<BigDecimal> values) {
        if (values == null || values.size() < 2) {
            return BigDecimal.ZERO;
        }
        BigDecimal recent = values.get(values.size() - 1);
        BigDecimal previous = values.get(values.size() - 2);
        if (previous.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal trend = recent.subtract(previous, MC).divide(previous, 10, ROUNDING);
        return capTrend(trend);
    }

    private BigDecimal capTrend(BigDecimal trend) {
        if (trend.compareTo(TREND_CAP) > 0) return TREND_CAP;
        if (trend.compareTo(TREND_CAP.negate()) < 0) return TREND_CAP.negate();
        return trend;
    }

    private List<BigDecimal> removeOutliers(List<BigDecimal> values) {
        if (values == null || values.size() < 4) {
            return values == null ? Collections.emptyList() : new ArrayList<>(values);
        }
        BigDecimal mean = calculateSimpleAverage(values);
        BigDecimal variance = BigDecimal.ZERO;
        for (BigDecimal v : values) {
            BigDecimal diff = v.subtract(mean, MC);
            variance = variance.add(diff.multiply(diff, MC), MC);
        }
        BigDecimal stdDev = variance.divide(BigDecimal.valueOf(values.size()), 10, ROUNDING).sqrt(MC);
        BigDecimal upper = mean.add(stdDev.multiply(OUTLIER_STD_MULTIPLIER, MC), MC);
        BigDecimal lower = mean.subtract(stdDev.multiply(OUTLIER_STD_MULTIPLIER, MC), MC);
        List<BigDecimal> result = new ArrayList<>();
        for (BigDecimal v : values) {
            if (v.compareTo(lower) >= 0 && v.compareTo(upper) <= 0) {
                result.add(v);
            }
        }
        return result;
    }

    private List<BigDecimal> removeOneTimeExpenses(List<BigDecimal> values) {
        if (values == null || values.isEmpty()) return Collections.emptyList();
        BigDecimal average = calculateSimpleAverage(values);
        List<BigDecimal> result = new ArrayList<>();
        for (BigDecimal v : values) {
            if (average.compareTo(BigDecimal.ZERO) == 0 || v.compareTo(average.multiply(ONE_TIME_MULTIPLIER, MC)) <= 0) {
                result.add(v);
            }
        }
        return result;
    }

    public BudgetForecastResponse forecast(BudgetResponse budget, List<CategoryHistory> categoryHistories) {
        int historicalMonths = 0;
        for (CategoryHistory h : categoryHistories) {
            historicalMonths = Math.max(historicalMonths, h.monthlyTotals.size());
        }
        String confidence = determineConfidence(historicalMonths);

        List<BudgetForecastResponse.CategoryForecast> categoryForecasts = new ArrayList<>();
        BigDecimal projectedTotal = BigDecimal.ZERO;
        for (BudgetCategoryDto cat : budget.categories()) {
            BigDecimal historicalAverage = BigDecimal.ZERO;
            BigDecimal trend = BigDecimal.ZERO;
            for (CategoryHistory h : categoryHistories) {
                if (cat.category().equals(h.category)) {
                    historicalAverage = calculateHistoricalAverage(h.monthlyTotals);
                    if (h.monthlyTotals.size() >= 2) {
                        trend = calculateTrendFromValues(h.monthlyTotals);
                    }
                    break;
                }
            }
            BigDecimal projectedSpending = historicalAverage.multiply(BigDecimal.ONE.add(trend), MC);
            if (historicalAverage.compareTo(BigDecimal.ZERO) == 0) {
                projectedSpending = cat.spent() != null ? cat.spent() : BigDecimal.ZERO;
            }
            BigDecimal remaining = cat.limit().subtract(projectedSpending, MC);
            BigDecimal util = cat.limit().compareTo(BigDecimal.ZERO) == 0
                ? BigDecimal.ZERO
                : projectedSpending.multiply(BigDecimal.valueOf(100), MC).divide(cat.limit(), SCALE, ROUNDING);
            categoryForecasts.add(BudgetForecastResponse.CategoryForecast.builder()
                .category(cat.category())
                .limit(cat.limit())
                .projectedSpending(projectedSpending.setScale(SCALE, ROUNDING))
                .projectedRemaining(remaining.setScale(SCALE, ROUNDING))
                .projectedUtilizationPercentage(util.setScale(SCALE, ROUNDING))
                .build());
            projectedTotal = projectedTotal.add(projectedSpending, MC);
        }

        BigDecimal remaining = budget.totalLimit().subtract(projectedTotal, MC);
        BigDecimal util = budget.totalLimit().compareTo(BigDecimal.ZERO) == 0
            ? BigDecimal.ZERO
            : projectedTotal.multiply(BigDecimal.valueOf(100), MC).divide(budget.totalLimit(), SCALE, ROUNDING);

        return BudgetForecastResponse.builder()
            .budgetId(budget.budgetId())
            .period(budget.period())
            .currency(budget.currency())
            .forecast(BudgetForecastResponse.Forecast.builder()
                .projectedSpending(projectedTotal.setScale(SCALE, ROUNDING))
                .projectedRemaining(remaining.setScale(SCALE, ROUNDING))
                .projectedUtilizationPercentage(util.setScale(SCALE, ROUNDING))
                .projectedStatus(determineStatus(util, 80, 100))
                .confidence(confidence)
                .basedOnHistoricalMonths(historicalMonths)
                .build())
            .categories(categoryForecasts)
            .calculatedAt(java.time.Instant.now().toString())
            .build();
    }

    public String determineConfidence(int months) {
        if (months >= 3) return "HIGH";
        if (months == 2) return "MEDIUM";
        return "LOW";
    }

    public String determineStatus(BigDecimal utilization, int warning, int exceeded) {
        if (utilization.compareTo(BigDecimal.valueOf(exceeded)) >= 0) return "OVER_BUDGET";
        if (utilization.compareTo(BigDecimal.valueOf(warning)) >= 0) return "NEAR_LIMIT";
        return "WITHIN_BUDGET";
    }
}
