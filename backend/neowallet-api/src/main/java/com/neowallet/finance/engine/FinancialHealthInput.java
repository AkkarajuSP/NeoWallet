package com.neowallet.finance.engine;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public record FinancialHealthInput(
        BigDecimal monthlyIncome,
        int historicalMonths,
        List<BudgetCategoryInput> budgetCategories,
        BigDecimal plannedSavings,
        BigDecimal actualSavings,
        List<MonthlySpendingInput> monthlySpendings,
        List<BillInput> bills,
        BigDecimal emergencyTarget,
        BigDecimal emergencyCurrent,
        List<SavingsGoalInput> savingsGoals,
        Integer previousScore,
        boolean isFamily
) {

    public FinancialHealthInput {
        if (monthlyIncome == null) monthlyIncome = BigDecimal.ZERO;
        if (budgetCategories == null) budgetCategories = List.of();
        if (plannedSavings == null) plannedSavings = BigDecimal.ZERO;
        if (actualSavings == null) actualSavings = BigDecimal.ZERO;
        if (monthlySpendings == null) monthlySpendings = List.of();
        if (bills == null) bills = List.of();
        if (emergencyTarget == null) emergencyTarget = BigDecimal.ZERO;
        if (emergencyCurrent == null) emergencyCurrent = BigDecimal.ZERO;
        if (savingsGoals == null) savingsGoals = List.of();
    }

    public record BudgetCategoryInput(String name, BigDecimal limit, BigDecimal spent) {}

    public record MonthlySpendingInput(String month, BigDecimal total) {}

    public record BillInput(LocalDate dueDate, LocalDate paidDate, String status) {}

    public record SavingsGoalInput(BigDecimal current, BigDecimal target, LocalDate targetDate) {}

    public static class Builder {
        private BigDecimal monthlyIncome = BigDecimal.ZERO;
        private int historicalMonths = 0;
        private List<BudgetCategoryInput> budgetCategories = List.of();
        private BigDecimal plannedSavings = BigDecimal.ZERO;
        private BigDecimal actualSavings = BigDecimal.ZERO;
        private List<MonthlySpendingInput> monthlySpendings = List.of();
        private List<BillInput> bills = List.of();
        private BigDecimal emergencyTarget = BigDecimal.ZERO;
        private BigDecimal emergencyCurrent = BigDecimal.ZERO;
        private List<SavingsGoalInput> savingsGoals = List.of();
        private Integer previousScore = null;
        private boolean isFamily = false;

        public Builder monthlyIncome(BigDecimal v) { this.monthlyIncome = v; return this; }
        public Builder historicalMonths(int v) { this.historicalMonths = v; return this; }
        public Builder budgetCategories(List<BudgetCategoryInput> v) { this.budgetCategories = v; return this; }
        public Builder plannedSavings(BigDecimal v) { this.plannedSavings = v; return this; }
        public Builder actualSavings(BigDecimal v) { this.actualSavings = v; return this; }
        public Builder monthlySpendings(List<MonthlySpendingInput> v) { this.monthlySpendings = v; return this; }
        public Builder bills(List<BillInput> v) { this.bills = v; return this; }
        public Builder emergencyTarget(BigDecimal v) { this.emergencyTarget = v; return this; }
        public Builder emergencyCurrent(BigDecimal v) { this.emergencyCurrent = v; return this; }
        public Builder savingsGoals(List<SavingsGoalInput> v) { this.savingsGoals = v; return this; }
        public Builder previousScore(Integer v) { this.previousScore = v; return this; }
        public Builder isFamily(boolean v) { this.isFamily = v; return this; }

        public FinancialHealthInput build() {
            return new FinancialHealthInput(monthlyIncome, historicalMonths, budgetCategories, plannedSavings, actualSavings,
                    monthlySpendings, bills, emergencyTarget, emergencyCurrent, savingsGoals, previousScore, isFamily);
        }
    }
}
