package com.neowallet.finance.engine;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class FinancialHealthCalculationEngine {

    public static final String ALGORITHM_VERSION = "1.1";

    private static final BigDecimal WEIGHT_BUDGET_ADHERENCE = new BigDecimal("0.20");
    private static final BigDecimal WEIGHT_SAVINGS_BEHAVIOR = new BigDecimal("0.20");
    private static final BigDecimal WEIGHT_EXPENSE_TREND = new BigDecimal("0.15");
    private static final BigDecimal WEIGHT_BILL_DISCIPLINE = new BigDecimal("0.15");
    private static final BigDecimal WEIGHT_EMERGENCY_PREPAREDNESS = new BigDecimal("0.15");
    private static final BigDecimal WEIGHT_GOAL_PROGRESS = new BigDecimal("0.15");

    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");
    private static final BigDecimal EIGHTY = new BigDecimal("80");
    private static final BigDecimal ONE_TWENTY = new BigDecimal("120");
    private static final BigDecimal ONE_FIFTY = new BigDecimal("150");

    public FinancialHealthResult calculate(FinancialHealthInput input) {
        if (input.monthlyIncome().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Income is required for score calculation");
        }

        int budgetAdherence = calculateBudgetAdherence(input);
        int savingsBehavior = calculateSavingsBehavior(input);
        int expenseTrend = calculateExpenseTrend(input);
        int billDiscipline = calculateBillDiscipline(input);
        int emergencyPreparedness = calculateEmergencyPreparedness(input);
        int goalProgress = calculateGoalProgress(input);

        String confidence = determineOverallConfidence(input, budgetAdherence, savingsBehavior, expenseTrend, billDiscipline, emergencyPreparedness, goalProgress);
        String status = determineStatus(confidence);

        List<FinancialHealthResult.FactorResult> factors = new ArrayList<>();
        factors.add(buildFactor("budgetAdherence", budgetAdherence, WEIGHT_BUDGET_ADHERENCE));
        factors.add(buildFactor("savingsBehavior", savingsBehavior, WEIGHT_SAVINGS_BEHAVIOR));
        factors.add(buildFactor("expenseTrend", expenseTrend, WEIGHT_EXPENSE_TREND));
        factors.add(buildFactor("billDiscipline", billDiscipline, WEIGHT_BILL_DISCIPLINE));
        factors.add(buildFactor("emergencyPreparedness", emergencyPreparedness, WEIGHT_EMERGENCY_PREPAREDNESS));
        factors.add(buildFactor("goalProgress", goalProgress, WEIGHT_GOAL_PROGRESS));

        BigDecimal weighted = factors.stream()
                .map(f -> f.contribution())
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(0, RoundingMode.HALF_UP);

        int overallScore = weighted.intValue();
        overallScore = Math.max(0, Math.min(100, overallScore));

        String scoreLabel = determineLabel(overallScore);

        ScoreChangeModel scoreChangeModel = calculateScoreChange(input.previousScore(), overallScore);

        List<FinancialHealthResult.Contributor> positive = buildPositiveContributors(factors);
        List<FinancialHealthResult.Contributor> negative = buildNegativeContributors(factors);
        List<FinancialHealthResult.RecommendedAction> recommendations = buildRecommendations(factors);

        FinancialHealthResult.ScoreChange scoreChange = new FinancialHealthResult.ScoreChange(
                scoreChangeModel.previousScore,
                scoreChangeModel.currentScore,
                scoreChangeModel.change,
                scoreChangeModel.direction,
                scoreChangeModel.magnitude,
                scoreChangeModel.reasons
        );

        return new FinancialHealthResult(
                overallScore,
                scoreLabel,
                confidence,
                status,
                factors,
                positive,
                negative,
                recommendations,
                scoreChange,
                Instant.now(),
                ALGORITHM_VERSION,
                buildNotes(input)
        );
    }

    private int calculateBudgetAdherence(FinancialHealthInput input) {
        if (input.historicalMonths() < 1 || input.budgetCategories().isEmpty()) {
            return 50;
        }

        BigDecimal total = BigDecimal.ZERO;
        for (FinancialHealthInput.BudgetCategoryInput category : input.budgetCategories()) {
            BigDecimal limit = category.limit();
            BigDecimal spent = category.spent();
            if (limit == null || limit.compareTo(BigDecimal.ZERO) <= 0) {
                total = total.add(BigDecimal.valueOf(50));
                continue;
            }
            BigDecimal utilization = spent == null ? BigDecimal.ZERO : spent;
            BigDecimal percentage = utilization.multiply(ONE_HUNDRED).divide(limit, 4, RoundingMode.HALF_UP);
            total = total.add(calculateCategoryScore(percentage));
        }

        return total.divide(BigDecimal.valueOf(input.budgetCategories().size()), 0, RoundingMode.HALF_UP).intValue();
    }

    private BigDecimal calculateCategoryScore(BigDecimal utilization) {
        BigDecimal u = utilization;
        if (u.compareTo(EIGHTY) <= 0) {
            return ONE_HUNDRED;
        } else if (u.compareTo(ONE_HUNDRED) <= 0) {
            return ONE_HUNDRED.subtract(u.subtract(EIGHTY).divide(new BigDecimal("20"), 4, RoundingMode.HALF_UP).multiply(new BigDecimal("20")));
        } else if (u.compareTo(ONE_TWENTY) <= 0) {
            return new BigDecimal("80").subtract(u.subtract(ONE_HUNDRED).divide(new BigDecimal("20"), 4, RoundingMode.HALF_UP).multiply(new BigDecimal("60")));
        } else if (u.compareTo(ONE_FIFTY) <= 0) {
            return new BigDecimal("20").subtract(u.subtract(ONE_TWENTY).divide(new BigDecimal("30"), 4, RoundingMode.HALF_UP).multiply(new BigDecimal("20")));
        }
        return BigDecimal.ZERO;
    }

    private int calculateSavingsBehavior(FinancialHealthInput input) {
        if (input.plannedSavings().compareTo(BigDecimal.ZERO) == 0) {
            return 50;
        }
        if (input.actualSavings().compareTo(BigDecimal.ZERO) < 0) {
            return 0;
        }
        BigDecimal score = input.actualSavings().multiply(ONE_HUNDRED).divide(input.plannedSavings(), 4, RoundingMode.HALF_UP);
        if (score.compareTo(ONE_HUNDRED) > 0) {
            score = ONE_HUNDRED;
        }
        return score.setScale(0, RoundingMode.HALF_UP).intValue();
    }

    private int calculateExpenseTrend(FinancialHealthInput input) {
        if (input.historicalMonths() < 2 || input.monthlySpendings().size() < 2) {
            return 50;
        }

        List<FinancialHealthInput.MonthlySpendingInput> spendings = input.monthlySpendings();
        BigDecimal current = spendings.get(spendings.size() - 1).total();

        BigDecimal sum = BigDecimal.ZERO;
        for (int i = 0; i < spendings.size() - 1; i++) {
            sum = sum.add(spendings.get(i).total() == null ? BigDecimal.ZERO : spendings.get(i).total());
        }
        BigDecimal count = BigDecimal.valueOf(spendings.size() - 1);
        if (count.compareTo(BigDecimal.ZERO) == 0) {
            return 50;
        }
        BigDecimal average = sum.divide(count, 4, RoundingMode.HALF_UP);
        if (average.compareTo(BigDecimal.ZERO) == 0) {
            return 50;
        }

        BigDecimal trend = current.subtract(average).multiply(ONE_HUNDRED).divide(average, 4, RoundingMode.HALF_UP);
        double t = trend.doubleValue();

        BigDecimal score;
        if (t <= 0) {
            score = ONE_HUNDRED;
        } else if (t <= 10) {
            score = ONE_HUNDRED.subtract(BigDecimal.valueOf(t).multiply(new BigDecimal("2")));
        } else if (t <= 20) {
            score = new BigDecimal("80").subtract(BigDecimal.valueOf(t - 10).multiply(new BigDecimal("3")));
        } else if (t <= 50) {
            score = new BigDecimal("50").subtract(BigDecimal.valueOf(t - 20).multiply(new BigDecimal("1.5")));
        } else {
            score = BigDecimal.ZERO;
        }

        if (score.compareTo(BigDecimal.ZERO) < 0) {
            score = BigDecimal.ZERO;
        } else if (score.compareTo(ONE_HUNDRED) > 0) {
            score = ONE_HUNDRED;
        }

        return score.setScale(0, RoundingMode.HALF_UP).intValue();
    }

    private int calculateBillDiscipline(FinancialHealthInput input) {
        if (input.bills().isEmpty()) {
            return 100;
        }

        int paidOnTime = 0;
        boolean hasSevereOverdue = false;
        LocalDate now = LocalDate.now();

        for (FinancialHealthInput.BillInput bill : input.bills()) {
            if ("PAID".equals(bill.status()) && bill.paidDate() != null) {
                if (!bill.paidDate().isAfter(bill.dueDate().plusDays(3))) {
                    paidOnTime++;
                }
            } else if ("PENDING".equals(bill.status()) && !now.isAfter(bill.dueDate().plusDays(3))) {
                paidOnTime++;
            } else if ("OVERDUE".equals(bill.status()) && bill.dueDate() != null) {
                long days = ChronoUnit.DAYS.between(bill.dueDate(), now);
                if (days > 7) {
                    hasSevereOverdue = true;
                }
            }
        }

        BigDecimal score = BigDecimal.valueOf(paidOnTime).multiply(ONE_HUNDRED)
                .divide(BigDecimal.valueOf(input.bills().size()), 4, RoundingMode.HALF_UP);

        if (hasSevereOverdue) {
            score = score.subtract(new BigDecimal("5"));
        }

        if (score.compareTo(BigDecimal.ZERO) < 0) {
            score = BigDecimal.ZERO;
        }
        return score.setScale(0, RoundingMode.HALF_UP).intValue();
    }

    private int calculateEmergencyPreparedness(FinancialHealthInput input) {
        if (input.emergencyTarget().compareTo(BigDecimal.ZERO) <= 0) {
            return 50;
        }
        if (input.emergencyCurrent().compareTo(BigDecimal.ZERO) <= 0) {
            return 0;
        }
        BigDecimal score = input.emergencyCurrent().multiply(ONE_HUNDRED).divide(input.emergencyTarget(), 4, RoundingMode.HALF_UP);
        if (score.compareTo(ONE_HUNDRED) > 0) {
            score = ONE_HUNDRED;
        }
        return score.setScale(0, RoundingMode.HALF_UP).intValue();
    }

    private int calculateGoalProgress(FinancialHealthInput input) {
        if (input.savingsGoals().isEmpty()) {
            return 50;
        }

        BigDecimal total = BigDecimal.ZERO;
        LocalDate now = LocalDate.now();

        for (FinancialHealthInput.SavingsGoalInput goal : input.savingsGoals()) {
            if (goal.target() == null || goal.target().compareTo(BigDecimal.ZERO) == 0) {
                total = total.add(BigDecimal.valueOf(50));
                continue;
            }
            BigDecimal raw = goal.current().multiply(ONE_HUNDRED).divide(goal.target(), 4, RoundingMode.HALF_UP);
            if (raw.compareTo(ONE_HUNDRED) > 0) {
                raw = ONE_HUNDRED;
            }
            if (goal.targetDate() != null && now.isAfter(goal.targetDate())) {
                long daysOverdue = ChronoUnit.DAYS.between(goal.targetDate(), now);
                BigDecimal penalty = BigDecimal.valueOf(daysOverdue / 30.0 * 10.0).setScale(4, RoundingMode.HALF_UP);
                raw = raw.subtract(penalty);
            }
            if (raw.compareTo(BigDecimal.ZERO) < 0) {
                raw = BigDecimal.ZERO;
            }
            total = total.add(raw);
        }

        return total.divide(BigDecimal.valueOf(input.savingsGoals().size()), 0, RoundingMode.HALF_UP).intValue();
    }

    private String determineOverallConfidence(FinancialHealthInput input, int... scores) {
        boolean hasMissing = false;
        if (input.historicalMonths() < 1 || input.budgetCategories().isEmpty()) hasMissing = true;
        if (input.plannedSavings().compareTo(BigDecimal.ZERO) == 0 && input.actualSavings().compareTo(BigDecimal.ZERO) == 0) hasMissing = true;
        if (input.historicalMonths() < 2) hasMissing = true;
        if (input.emergencyTarget().compareTo(BigDecimal.ZERO) <= 0) hasMissing = true;
        if (input.savingsGoals().isEmpty()) hasMissing = true;

        if (input.historicalMonths() >= 3 && !hasMissing) {
            return "HIGH";
        } else if (input.historicalMonths() == 2) {
            return hasMissing ? "LOW" : "MEDIUM";
        }
        return "LOW";
    }

    private String determineStatus(String confidence) {
        return "HIGH".equals(confidence) ? "FINAL" : "PROVISIONAL";
    }

    private String determineLabel(int score) {
        if (score >= 90) return "EXCELLENT";
        if (score >= 75) return "GOOD";
        if (score >= 60) return "FAIR";
        if (score >= 40) return "NEEDS_ATTENTION";
        return "CRITICAL";
    }

    private FinancialHealthResult.FactorResult buildFactor(String name, int score, BigDecimal weight) {
        BigDecimal contribution = BigDecimal.valueOf(score).multiply(weight).setScale(2, RoundingMode.HALF_UP);
        return new FinancialHealthResult.FactorResult(name, score, weight, contribution, "HIGH", null);
    }

    private ScoreChangeModel calculateScoreChange(Integer previousScore, int currentScore) {
        if (previousScore == null || previousScore == 0) {
            return new ScoreChangeModel(null, currentScore, null, "STABLE", "MINOR", List.of("No previous score available"));
        }

        int change = currentScore - previousScore;
        String direction = change > 0 ? "IMPROVING" : change < 0 ? "DECLINING" : "STABLE";
        int abs = Math.abs(change);
        String magnitude = abs <= 5 ? "MINOR" : abs <= 15 ? "MODERATE" : "SIGNIFICANT";

        return new ScoreChangeModel(previousScore, currentScore, change, direction, magnitude, List.of());
    }

    private List<FinancialHealthResult.Contributor> buildPositiveContributors(List<FinancialHealthResult.FactorResult> factors) {
        return factors.stream()
                .filter(f -> f.score() >= 75)
                .map(f -> new FinancialHealthResult.Contributor(f.name(), f.score(), getFactorPraise(f.name())))
                .toList();
    }

    private List<FinancialHealthResult.Contributor> buildNegativeContributors(List<FinancialHealthResult.FactorResult> factors) {
        return factors.stream()
                .filter(f -> f.score() < 60)
                .map(f -> new FinancialHealthResult.Contributor(f.name(), f.score(), getFactorConcern(f.name())))
                .toList();
    }

    private List<FinancialHealthResult.RecommendedAction> buildRecommendations(List<FinancialHealthResult.FactorResult> factors) {
        List<FinancialHealthResult.RecommendedAction> actions = new ArrayList<>();
        for (FinancialHealthResult.FactorResult factor : factors) {
            if (factor.score() < getRecommendationThreshold(factor.name())) {
                actions.add(new FinancialHealthResult.RecommendedAction(
                        factor.name(),
                        getRecommendationText(factor.name()),
                        getPriority(factor),
                        "Up to " + (getRecommendationThreshold(factor.name()) - factor.score()) + " points"
                ));
            }
        }
        return actions.stream()
                .sorted(Comparator.comparing(a -> priorityRank(a.priority())))
                .toList();
    }

    private int getRecommendationThreshold(String factor) {
        return switch (factor) {
            case "budgetAdherence" -> 70;
            case "savingsBehavior" -> 60;
            case "expenseTrend" -> 70;
            case "emergencyPreparedness" -> 50;
            case "goalProgress" -> 50;
            default -> 50;
        };
    }

    private String getRecommendationText(String factor) {
        return switch (factor) {
            case "budgetAdherence" -> "Review high-spending categories";
            case "savingsBehavior" -> "Review savings allocation";
            case "expenseTrend" -> "Review variable expenses";
            case "emergencyPreparedness" -> "Consider emergency allocation";
            case "goalProgress" -> "Review savings goals";
            default -> "Review " + factor;
        };
    }

    private String getPriority(FinancialHealthResult.FactorResult factor) {
        int impact = (100 - factor.score()) * factor.weight().multiply(ONE_HUNDRED).intValue();
        if (impact > 800) return "HIGH";
        if (impact > 400) return "MEDIUM";
        return "LOW";
    }

    private int priorityRank(String priority) {
        return switch (priority) {
            case "HIGH" -> 0;
            case "MEDIUM" -> 1;
            default -> 2;
        };
    }

    private String getFactorPraise(String factor) {
        return switch (factor) {
            case "budgetAdherence" -> "Good budget adherence";
            case "savingsBehavior" -> "Savings on track";
            case "expenseTrend" -> "Spending stable";
            case "billDiscipline" -> "Excellent bill payment discipline";
            case "emergencyPreparedness" -> "Emergency fund improving";
            case "goalProgress" -> "Savings goals progressing well";
            default -> factor + " is strong";
        };
    }

    private String getFactorConcern(String factor) {
        return switch (factor) {
            case "budgetAdherence" -> "Budget adherence needs improvement";
            case "savingsBehavior" -> "Savings below target";
            case "expenseTrend" -> "Spending trend increasing";
            case "billDiscipline" -> "Bill payment discipline needs attention";
            case "emergencyPreparedness" -> "Emergency fund needs improvement";
            case "goalProgress" -> "Savings goals behind target";
            default -> factor + " needs attention";
        };
    }

    private List<String> buildNotes(FinancialHealthInput input) {
        List<String> notes = new ArrayList<>();
        if (input.historicalMonths() < 3) {
            notes.add("Score will improve with more historical data");
        }
        if (input.budgetCategories().isEmpty()) {
            notes.add("No budgets established");
        }
        if (input.plannedSavings().compareTo(BigDecimal.ZERO) == 0 && input.actualSavings().compareTo(BigDecimal.ZERO) == 0) {
            notes.add("No savings plan established");
        }
        if (input.bills().isEmpty()) {
            notes.add("No bills to track");
        }
        if (input.savingsGoals().isEmpty()) {
            notes.add("No savings goals established");
        }
        if (input.emergencyTarget().compareTo(BigDecimal.ZERO) <= 0) {
            notes.add("Emergency fund target not set");
        }
        return notes;
    }

    private record ScoreChangeModel(Integer previousScore, Integer currentScore, Integer change, String direction, String magnitude, List<String> reasons) {}
}
