# NeoWallet Budget Allocation Algorithm v1

## Executive Summary

This document defines the deterministic algorithms and business logic for NeoWallet household budget planning and allocation. The algorithm provides planning recommendations based on historical data, user inputs, and configurable parameters, while clearly distinguishing planning values from actual funds.

**Core Value**: PLAN → OPTIMIZE → DECIDE → PAY
**Fundamental Principle**: All allocation values are planning values, not actual bank balances
**Algorithm Type**: Deterministic calculations with AI explanations (not AI-driven decisions)
**Date**: August 17, 2026
**Status**: Ready for Product Owner Approval

---

## 1. Objectives

### Primary Objectives

1. **Provide Planning Guidance**: Help families plan their household financial allocation
2. **Use Historical Data**: Leverage historical spending patterns where available
3. **Respect User Priorities**: Allow users to override system recommendations
4. **Maintain Transparency**: Clearly explain all recommendations and calculations
5. **Handle Edge Cases**: Gracefully handle missing data, insufficient income, and edge cases
6. **Separate Planning from Execution**: All values are planning values, not actual funds
7. **Enable Explainability**: Provide clear rationale for all recommendations

### Non-Objectives

- Do not make autonomous financial decisions
- Do not access actual bank account balances
- Do not execute payments or transfers
- Do not replace user judgment
- Do not claim universal applicability of recommendations

---

## 2. Inputs

### Input Classification

#### MANDATORY INPUT
Inputs required for basic allocation calculation:
- **Monthly Income**: User-provided monthly household income
- **Family Size**: Number of family members
- **Number of Adults**: Number of adult family members
- **Number of Children**: Number of child family members

#### OPTIONAL INPUT
Inputs that improve recommendation quality but are not required:
- **Other Recurring Income**: Additional income sources
- **Existing Budgets**: Current budget allocations
- **User-Defined Priorities**: User-specified category priorities

#### USER-PROVIDED INPUT
Inputs explicitly provided by the user:
- **Monthly Income**: User-entered income amount
- **Family Composition**: User-entered family size and composition
- **Debt Obligations**: User-provided debt information (voluntary)
- **Savings Goals**: User-defined savings targets
- **User-Defined Priorities**: User-specified category priorities
- **Emergency Fund Preference**: User-specified emergency fund amount/percentage

#### SYSTEM-DERIVED INPUT
Inputs calculated by the system from transaction data:
- **Historical Expenses**: Calculated from transaction history
- **Recurring Expenses**: Identified from transaction patterns
- **Seasonal Spending Patterns**: Derived from historical data (if sufficient)
- **Previous Month Actual Spending**: Calculated from transaction records
- **Average Spending**: Calculated from historical data
- **Spending Trend**: Calculated from historical data

#### AI-RECOMMENDED INPUT
Inputs suggested by AI (user must approve):
- **Category Priorities**: AI-suggested category importance
- **Optimization Suggestions**: AI-suggested allocation improvements
- **Pattern Insights**: AI-identified spending patterns

### Input Data Structure

```
Input {
    // Mandatory
    monthly_income: Decimal
    family_size: Integer
    number_of_adults: Integer
    number_of_children: Integer
    
    // Optional
    other_recurring_income: Decimal[]
    existing_budgets: Budget[]
    user_defined_priorities: CategoryPriority[]
    
    // User-Provided
    debt_obligations: DebtObligation[]
    savings_goals: SavingsGoal[]
    emergency_fund_preference: EmergencyFundPreference
    
    // System-Derived
    historical_expenses: HistoricalExpense[]
    recurring_expenses: RecurringExpense[]
    seasonal_spending_patterns: SeasonalPattern[]
    previous_month_actual_spending: MonthlySpending
    average_spending: CategoryAverage[]
    spending_trend: CategoryTrend[]
    
    // AI-Recommended
    ai_category_priorities: CategoryPriority[]
    ai_optimization_suggestions: OptimizationSuggestion[]
}
```

---

## 3. Expense Categories

### Default Household Categories

#### Essential Categories
1. **Housing**: Rent, mortgage, property taxes, maintenance
2. **Groceries**: Food and household supplies
3. **Electricity**: Electricity bills
4. **Gas**: Gas bills (cooking/heating)
5. **Internet**: Internet service
6. **Mobile**: Mobile phone plans
7. **DTH**: Direct-to-home TV services
8. **Water**: Water bills
9. **Transportation**: Fuel, public transport, vehicle maintenance
10. **Education**: Tuition, school fees, educational materials
11. **Healthcare**: Medical expenses, insurance premiums
12. **Insurance**: Life, health, property insurance

#### Variable Categories
13. **Entertainment**: Movies, dining out, hobbies
14. **Subscriptions**: Streaming services, magazines, memberships
15. **Personal**: Personal care, clothing, miscellaneous
16. **Family**: Family activities, gifts, celebrations

#### Planning Categories
17. **Savings**: General savings allocation
18. **Emergency**: Emergency fund allocation
19. **Other**: Miscellaneous expenses not covered elsewhere

### Category Configuration

Categories are **configurable** with the following properties:

```
Category {
    id: String
    name: String
    type: CategoryType (ESSENTIAL, VARIABLE, PLANNING)
    is_mandatory: Boolean
    default_percentage: Decimal (optional)
    minimum_allocation: Decimal (optional)
    user_can_delete: Boolean
    user_can_modify: Boolean
}
```

### Category Priority

Categories can be prioritized by the user:

```
CategoryPriority {
    category_id: String
    priority: Integer (1 = highest, 10 = lowest)
    weight: Decimal (for allocation calculation)
}
```

---

## 4. Business Rules

### Core Business Rules

#### BR-ALLOC-001: Planning Value Representation
**Rule**: All allocation values must be clearly labeled as planning values
**Enforcement**: Display "Planned" label for all allocation values
**Exception**: None

#### BR-ALLOC-002: Historical Data Priority
**Rule**: Historical data takes priority over baseline assumptions where available
**Enforcement**: Use historical averages when 3+ months of data exists
**Exception**: Insufficient historical data → use baseline assumptions

#### BR-ALLOC-003: User Override Authority
**Rule**: User can override any system recommendation
**Enforcement**: Preserve original recommendation alongside user modification
**Exception**: None

#### BR-ALLOC-004: Deficit Transparency
**Rule**: System must show deficit when expenses exceed income
**Enforcement**: Display planning deficit amount, do not artificially balance
**Exception**: None

#### BR-ALLOC-005: Savings Goal Priority
**Rule**: Savings goals influence allocation but do not override essential expenses
**Enforcement**: Calculate required contribution, show affordability warning if not affordable
**Exception**: None

#### BR-ALLOC-006: Emergency Fund Configurability
**Rule**: Emergency fund allocation is configurable by user
**Enforcement**: Use user preference or default percentage
**Exception**: None

#### BR-ALLOC-007: Deterministic Calculation
**Rule**: Core calculations must be deterministic and reproducible
**Enforcement**: Same inputs must produce same outputs
**Exception**: None

#### BR-ALLOC-008: AI Explanation Only
**Rule**: AI provides explanations, not calculations
**Enforcement**: AI explains deterministic calculation results
**Exception**: None

---

## 5. Mathematical Formulas

### Core Allocation Formula

```
Available Planning Income = Monthly Income + Other Recurring Income

Mandatory Commitments = Debt Obligations + Recurring Essential Expenses

Essential Allocation = Housing + Groceries + Utilities + Transportation + Healthcare + Insurance

Savings Allocation = SUM(Savings Goal Monthly Contributions)

Emergency Allocation = Emergency Fund Preference (percentage of Available Planning Income)

Discretionary Planning Amount = 
    Available Planning Income 
    - Mandatory Commitments 
    - Essential Allocation 
    - Savings Allocation 
    - Emergency Allocation
```

### Category Budget Formula

```
Recommended Category Budget = 
    Historical Average 
    × (1 + Trend Percentage) 
    × Category Priority Weight
    × Inflation Adjustment (if applicable)
```

### Budget Utilization Formula

```
Budget Utilization % = (Actual Spending / Budget Limit) × 100
```

### Savings Goal Formula

```
Required Monthly Contribution = 
    (Target Amount - Current Amount) / Remaining Months
```

### Financial Health Score Formula

```
Health Score = 
    (Savings Behavior Weight × Savings Score) +
    (Budget Adherence Weight × Budget Score) +
    (Expense Trend Weight × Expense Score) +
    (Bill Discipline Weight × Bill Score) +
    (Emergency Preparedness Weight × Emergency Score) +
    (Goal Progress Weight × Goal Score)
```

---

## 6. Allocation Algorithm

### Algorithm Overview

The allocation algorithm follows these steps:

1. **Input Validation**: Validate all inputs
2. **Data Availability Check**: Determine historical data availability
3. **Baseline Calculation**: Calculate baseline allocation based on data availability
4. **Historical Analysis**: Analyze historical spending patterns
5. **Essential Allocation**: Calculate essential expense allocation
6. **Savings Allocation**: Calculate savings goal allocation
7. **Emergency Allocation**: Calculate emergency fund allocation
8. **Discretionary Allocation**: Calculate discretionary allocation
9. **Category Distribution**: Distribute discretionary allocation to categories
10. **Validation**: Validate allocation (no negative values, no deficit unless shown)
11. **Recommendation Generation**: Generate budget recommendations
12. **User Override**: Apply user modifications
13. **Final Output**: Generate final allocation output

### Minimum Data Rule

#### 0 Months of Historical Data
- Use configurable baseline assumptions
- Ask user for category priorities
- Use default category percentages
- Mark all recommendations as "Low Confidence"

#### 1 Month of Historical Data
- Use available actual data
- Apply low confidence weighting
- Use default percentages for missing categories
- Mark recommendations as "Low Confidence"

#### 2 Months of Historical Data
- Use average of 2 months
- Apply limited trend calculation
- Use default percentages for missing categories
- Mark recommendations as "Medium Confidence"

#### 3+ Months of Historical Data
- Use historical average
- Calculate spending trend
- Identify seasonal patterns (if sufficient data)
- Apply outlier detection
- Mark recommendations as "High Confidence"

### Pseudocode

```
FUNCTION CalculateAllocation(inputs):
    // Step 1: Input Validation
    IF inputs.monthly_income <= 0 THEN
        RETURN Error("Income must be positive")
    
    // Step 2: Data Availability Check
    historical_months = CountHistoricalMonths(inputs.historical_expenses)
    
    // Step 3: Baseline Calculation
    baseline_allocation = CalculateBaselineAllocation(inputs, historical_months)
    
    // Step 4: Historical Analysis
    historical_analysis = AnalyzeHistoricalSpending(inputs.historical_expenses, historical_months)
    
    // Step 5: Essential Allocation
    essential_allocation = CalculateEssentialAllocation(inputs, historical_analysis)
    
    // Step 6: Savings Allocation
    savings_allocation = CalculateSavingsAllocation(inputs.savings_goals)
    
    // Step 7: Emergency Allocation
    emergency_allocation = CalculateEmergencyAllocation(inputs.emergency_fund_preference, inputs.monthly_income)
    
    // Step 8: Discretionary Allocation
    discretionary_amount = CalculateDiscretionaryAmount(
        inputs.monthly_income,
        inputs.debt_obligations,
        essential_allocation,
        savings_allocation,
        emergency_allocation
    )
    
    // Step 9: Category Distribution
    category_budgets = DistributeToCategories(
        discretionary_amount,
        historical_analysis,
        inputs.user_defined_priorities
    )
    
    // Step 10: Validation
    allocation = ValidateAllocation(
        inputs.monthly_income,
        essential_allocation,
        savings_allocation,
        emergency_allocation,
        category_budgets
    )
    
    // Step 11: Recommendation Generation
    recommendations = GenerateRecommendations(allocation, historical_analysis)
    
    // Step 12: User Override
    final_allocation = ApplyUserOverride(recommendations, inputs.user_modifications)
    
    // Step 13: Final Output
    RETURN GenerateOutput(final_allocation, historical_analysis, recommendations)
END FUNCTION
```

---

## 7. Historical Spending Method

### Average Calculation

#### Simple Average
```
Simple Average = SUM(Monthly Spending) / Number of Months
```

#### Weighted Average
```
Weighted Average = 
    (Recent Month × 0.4) + 
    (Previous Month × 0.3) + 
    (2 Months Ago × 0.2) + 
    (3 Months Ago × 0.1)
```

#### Median
```
Median = Middle value of sorted monthly spending
```

### Trend Calculation

#### Percentage Trend
```
Trend % = ((Current Month - Previous Month) / Previous Month) × 100
```

#### Linear Trend
```
Linear Trend = Slope of linear regression on monthly spending
```

### Outlier Handling

#### Standard Deviation Method
```
Mean = Average of monthly spending
StdDev = Standard Deviation of monthly spending
Outlier Threshold = Mean ± (2 × StdDev)

IF Monthly Spending > Outlier Threshold THEN
    Exclude from average calculation
END IF
```

#### IQR Method
```
Q1 = 25th percentile
Q3 = 75th percentile
IQR = Q3 - Q1
Outlier Threshold = Q1 - (1.5 × IQR) to Q3 + (1.5 × IQR)

IF Monthly Spending < Lower Threshold OR Monthly Spending > Upper Threshold THEN
    Exclude from average calculation
END IF
```

### Missing Data Handling

#### Interpolation
```
IF Month Data Missing THEN
    Interpolated Value = (Previous Month + Next Month) / 2
END IF
```

#### Forward Fill
```
IF Month Data Missing THEN
    Use Previous Month Value
END IF
```

### One-Time Expenses

#### Identification
```
IF Expense Occurs Only Once in 12 Months THEN
    Mark as One-Time Expense
    Exclude from Recurring Calculation
END IF
```

### Recurring Expenses

#### Pattern Detection
```
IF Expense Occurs in Same Category Within ±7 Days Each Month THEN
    Mark as Recurring Expense
    Include in Baseline Calculation
END IF
```

### Seasonal Expenses

#### Seasonal Detection
```
IF Expense Pattern Repeats Annually THEN
    Mark as Seasonal Expense
    Use Seasonal Average for Current Month
END IF
```

---

## 8. Savings Algorithm

### Required Monthly Contribution Calculation

```
Required Monthly Contribution = 
    (Target Amount - Current Amount) / Remaining Months

Remaining Months = 
    MAX(1, Target Date - Current Date in Months)
```

### Affordability Check

```
IF Required Monthly Contribution > Available Discretionary Amount THEN
    Mark as "Not Affordable"
    Generate Alternatives
END IF
```

### Alternatives Generation

#### Option 1: Extend Target Date
```
New Remaining Months = 
    (Target Amount - Current Amount) / Available Discretionary Amount
New Target Date = Current Date + New Remaining Months
```

#### Option 2: Reduce Target Amount
```
New Target Amount = 
    Current Amount + (Available Discretionary Amount × Remaining Months)
```

#### Option 3: Reduce Discretionary Allocation
```
Required Discretionary Reduction = 
    Required Monthly Contribution - Available Discretionary Amount
```

#### Option 4: Keep Current Plan
```
Continue with current contribution rate
Accept that goal may not be met on time
```

### Multiple Goals Priority

#### Priority by Target Date
```
Sort Goals by Target Date (soonest first)
Allocate Available Savings to Goals in Priority Order
```

#### Priority by Amount
```
Sort Goals by Target Amount (smallest first)
Allocate Available Savings to Goals in Priority Order
```

---

## 9. Emergency Allocation

### Configurable Emergency Fund Mechanism

#### Percentage-Based Allocation
```
Emergency Allocation = 
    Available Planning Income × Emergency Fund Percentage

Default Emergency Fund Percentage = 10% (configurable)
```

#### Fixed Amount Allocation
```
Emergency Allocation = 
    User-Specified Fixed Amount
```

#### Target-Based Allocation
```
Emergency Fund Target = 3 to 6 Months of Essential Expenses
Monthly Contribution = 
    (Emergency Fund Target - Current Emergency Fund) / Target Months
```

### User Override

```
IF User Specifies Emergency Fund Preference THEN
    Use User Preference
ELSE
    Use Default Percentage (10%)
END IF
```

### Emergency Fund Status

```
IF Current Emergency Fund >= Target THEN
    Status = "Fully Funded"
ELSE IF Current Emergency Fund >= (Target × 0.5) THEN
    Status = "Partially Funded"
ELSE
    Status = "Underfunded"
END IF
```

---

## 10. Forecasting

### MVP Forecasting (Deterministic)

#### Expected Month-End Spending
```
Expected Month-End Spending = 
    Current Month Spending + 
    (Average Daily Spending × Remaining Days)
```

#### Expected Category Spending
```
Expected Category Spending = 
    Historical Category Average × (1 + Trend Percentage)
```

#### Expected Budget Variance
```
Expected Budget Variance = 
    Expected Category Spending - Category Budget
```

### Forecast Uncertainty

#### Confidence Level
```
IF Historical Months >= 3 THEN
    Confidence = "High"
ELSE IF Historical Months = 2 THEN
    Confidence = "Medium"
ELSE
    Confidence = "Low"
END IF
```

#### Variance Range
```
Variance Range = ± (Standard Deviation × 2)
```

### Forecast Display

```
Forecast {
    expected_month_end_spending: Decimal
    expected_category_spending: Map<Category, Decimal>
    expected_budget_variance: Map<Category, Decimal>
    confidence_level: String
    variance_range: Decimal
    uncertainty_note: String
}
```

---

## 11. Budget Alerts

### Alert Thresholds (Configurable Defaults)

#### Budget Warning Threshold
```
Default: 80% of budget limit
Configurable: BUDGET_WARNING_PERCENTAGE

IF Budget Utilization >= BUDGET_WARNING_PERCENTAGE THEN
    Trigger Warning Alert
END IF
```

#### Budget Exceeded Threshold
```
Default: 100% of budget limit
Configurable: BUDGET_EXCEEDED_PERCENTAGE

IF Budget Utilization >= BUDGET_EXCEEDED_PERCENTAGE THEN
    Trigger Exceeded Alert
END IF
```

### Alert Behavior

#### Nearly Exhausted
```
IF Budget Utilization >= 90% AND Budget Utilization < 100% THEN
    Alert Type: "Nearly Exhausted"
    Message: "Budget nearly exhausted. Consider reducing spending."
END IF
```

#### Budget Exceeded
```
IF Budget Utilization >= 100% THEN
    Alert Type: "Budget Exceeded"
    Message: "Budget exceeded. Review spending."
END IF
```

#### Predicted Overspending
```
IF Expected Budget Variance > 0 THEN
    Alert Type: "Predicted Overspending"
    Message: "Expected to exceed budget by ₹{Variance Amount}"
END IF
```

---

## 12. AI vs Deterministic Responsibilities

### Deterministic Calculation Engine Responsibilities

The deterministic calculation engine is responsible for:

1. **All Mathematical Calculations**: All numerical calculations
2. **Historical Data Analysis**: Average, trend, outlier detection
3. **Budget Recommendations**: Generate budget amounts based on formulas
4. **Allocation Calculations**: Calculate essential, savings, emergency, discretionary allocation
5. **Forecasting**: Calculate expected spending and variance
6. **Validation**: Validate all calculations and inputs
7. **Deficit Detection**: Identify and report planning deficits

### AI Responsibilities

AI is responsible for:

1. **Explanation**: Explain the calculated numbers in natural language
2. **Pattern Identification**: Identify spending patterns and anomalies
3. **Recommendation Alternatives**: Suggest alternative allocation strategies
4. **Insight Generation**: Provide insights into financial behavior
5. **Optimization Suggestions**: Suggest ways to optimize spending
6. **Contextual Advice**: Provide contextual financial advice

### AI Cannot

AI must NOT:

1. **Replace Calculations**: AI cannot override deterministic calculation results
2. **Make Financial Decisions**: AI cannot autonomously make financial decisions
3. **Modify Budgets**: AI cannot modify budgets without user approval
4. **Access Database**: AI cannot access production database directly
5. **Execute Transactions**: AI cannot execute any financial transactions

### Separation Example

**Deterministic Engine Output**:
```
Groceries Budget: ₹11,340
Calculated from: Historical Average (₹10,500) × (1 + Trend 8%)
```

**AI Explanation**:
```
"Based on your historical grocery spending of ₹10,500 and an 8% upward trend,
we recommend a budget of ₹11,340. This accounts for the recent increase
in grocery prices. You could potentially save ₹500 by reducing discretionary
spending on entertainment."
```

---

## 13. Edge Cases

### Missing Income
```
IF Monthly Income = 0 OR Not Provided THEN
    RETURN Error("Income is required for allocation calculation")
    Prompt User to Enter Income
END IF
```

### Missing Expenses
```
IF Historical Expenses Not Available THEN
    Use Baseline Assumptions
    Mark Recommendations as "Low Confidence"
    Prompt User to Enter Estimated Expenses
END IF
```

### Very High One-Time Expense
```
IF Transaction Amount > (Average Monthly Spending × 3) THEN
    Mark as One-Time Expense
    Exclude from Recurring Calculation
    Display Note: "Excluded as one-time expense"
END IF
```

### Duplicate Transactions
```
IF Duplicate Transaction Detected THEN
    Flag for Review
    Exclude from Average Calculation
    Display Warning: "Duplicate transaction detected"
END IF
```

### Refunds
```
IF Transaction Type = Refund THEN
    Treat as Negative Expense
    Subtract from Category Spending
    Display Note: "Refund applied"
END IF
```

### Negative Transactions
```
IF Transaction Amount < 0 THEN
    Validate Transaction Type (Refund, Correction)
    Apply Appropriate Handling
END IF
```

### Mid-Month Budget Creation
```
IF Budget Created Mid-Month THEN
    Pro-rate Budget Limit for Remaining Days
    Calculate Utilization Based on Pro-rated Limit
    Display Note: "Pro-rated for mid-month creation"
END IF
```

### User Changes Income
```
IF User Changes Income THEN
    Recalculate Allocation
    Generate New Recommendations
    Preserve Previous Version
    Display Comparison
END IF
```

### User Changes Savings Goal
```
IF User Changes Savings Goal THEN
    Recalculate Required Monthly Contribution
    Check Affordability
    Generate Alternatives if Not Affordable
    Preserve Previous Version
END IF
```

### Family Member Added
```
IF Family Member Added THEN
    Update Family Size
    Recalculate Allocation
    Adjust Category Budgets Based on Family Size
    Display Impact
END IF
```

### Family Member Removed
```
IF Family Member Removed THEN
    Update Family Size
    Recalculate Allocation
    Adjust Category Budgets Based on Family Size
    Display Impact
END IF
```

### New Category
```
IF User Adds New Category THEN
    Add to Category List
    Allocate from Discretionary Amount
    Update Allocation
    Display Impact
END IF
```

### Deleted Category
```
IF User Deletes Category THEN
    Remove from Category List
    Reallocate Budget to Other Categories or Unallocated
    Update Allocation
    Display Impact
END IF
```

### Month-End Rollover
```
IF Month Ends THEN
    Calculate Budget Utilization
    Carry Forward Unspent Amount (if rollover enabled)
    Reset Budget to Limit (if rollover disabled)
    Generate Month-End Report
END IF
```

---

## 14. Examples

### Example 1: New User (0 Months Historical Data)

**Inputs**:
- Monthly Income: ₹80,000
- Family Size: 4 (2 adults, 2 children)
- Historical Data: 0 months

**Calculation**:
```
Available Planning Income = ₹80,000

Baseline Allocation (using default percentages):
- Housing: 30% = ₹24,000
- Groceries: 15% = ₹12,000
- Utilities: 10% = ₹8,000
- Transportation: 10% = ₹8,000
- Healthcare: 5% = ₹4,000
- Insurance: 5% = ₹4,000
- Education: 10% = ₹8,000
- Entertainment: 5% = ₹4,000
- Savings: 5% = ₹4,000
- Emergency: 5% = ₹4,000

Total Essential + Planning = ₹80,000
Discretionary = ₹0

Confidence: Low
```

**Output**:
```
Recommendation: Baseline allocation based on default percentages
Note: "Low confidence - no historical data available"
Action: Ask user to provide category priorities
```

### Example 2: Historical Data Available (3+ Months)

**Inputs**:
- Monthly Income: ₹80,000
- Family Size: 4
- Historical Data: 6 months
- Historical Grocery Average: ₹10,500
- Grocery Trend: +8%

**Calculation**:
```
Available Planning Income = ₹80,000

Historical Analysis:
- Groceries: ₹10,500 average, +8% trend
- Housing: ₹22,000 average, 0% trend
- Utilities: ₹7,500 average, +5% trend
- Transportation: ₹6,000 average, -2% trend

Recommended Allocation:
- Groceries: ₹10,500 × 1.08 = ₹11,340
- Housing: ₹22,000 × 1.00 = ₹22,000
- Utilities: ₹7,500 × 1.05 = ₹7,875
- Transportation: ₹6,000 × 0.98 = ₹5,880

Total Essential: ₹47,095
Savings (user goal): ₹10,000
Emergency (10%): ₹8,000
Discretionary: ₹80,000 - ₹47,095 - ₹10,000 - ₹8,000 = ₹14,905

Confidence: High
```

**Output**:
```
Groceries Budget: ₹11,340
Explanation: "Based on 6-month average of ₹10,500 and 8% upward trend"
Confidence: High
```

### Example 3: Savings Goal Not Affordable

**Inputs**:
- Monthly Income: ₹50,000
- Essential Expenses: ₹45,000
- Savings Goal: ₹120,000 in 12 months (₹10,000/month)
- Emergency Allocation: ₹5,000 (10%)

**Calculation**:
```
Available Planning Income = ₹50,000
Essential Expenses = ₹45,000
Emergency Allocation = ₹5,000
Available for Savings = ₹50,000 - ₹45,000 - ₹5,000 = ₹0

Required Monthly Contribution = ₹10,000
Available for Savings = ₹0

Status: Not Affordable
```

**Alternatives**:
1. Extend Target Date: 24 months → ₹5,000/month (still not affordable)
2. Reduce Target Amount: ₹60,000 → ₹5,000/month (still not affordable)
3. Reduce Emergency Allocation: ₹2,500 → ₹2,500/month (affordable)
4. Keep Current Plan: Goal will not be met

**Output**:
```
Savings Goal: Not Affordable
Required: ₹10,000/month
Available: ₹0/month
Alternatives Provided
User Decision Required
```

### Example 4: Planning Deficit

**Inputs**:
- Monthly Income: ₹50,000
- Mandatory Commitments: ₹30,000
- Essential Expenses: ₹30,000

**Calculation**:
```
Available Planning Income = ₹50,000
Mandatory Commitments = ₹30,000
Essential Expenses = ₹30,000
Total Required = ₹60,000

Planning Deficit = ₹60,000 - ₹50,000 = ₹10,000
```

**Output**:
```
Planning Deficit: ₹10,000
Income: ₹50,000
Required: ₹60,000
Recommendations:
1. Increase Income
2. Reduce Essential Expenses
3. Seek Debt Relief
4. Review Mandatory Commitments
```

---

## 15. Pseudocode

### Main Allocation Algorithm

```
FUNCTION CalculateHouseholdAllocation(inputs):
    
    // Step 1: Input Validation
    IF inputs.monthly_income <= 0 THEN
        RETURN Error("Income must be positive")
    END IF
    
    // Step 2: Determine Historical Data Availability
    historical_months = CountHistoricalMonths(inputs.historical_expenses)
    
    // Step 3: Calculate Available Planning Income
    available_income = inputs.monthly_income
    IF inputs.other_recurring_income EXISTS THEN
        available_income += SUM(inputs.other_recurring_income)
    END IF
    
    // Step 4: Calculate Mandatory Commitments
    mandatory_commitments = 0
    IF inputs.debt_obligations EXISTS THEN
        mandatory_commitments += SUM(inputs.debt_obligations.monthly_payment)
    END IF
    
    // Step 5: Calculate Essential Allocation
    essential_allocation = CalculateEssentialAllocation(
        inputs,
        historical_months,
        mandatory_commitments
    )
    
    // Step 6: Calculate Savings Allocation
    savings_allocation = CalculateSavingsAllocation(
        inputs.savings_goals,
        available_income - mandatory_commitments - essential_allocation.total
    )
    
    // Step 7: Calculate Emergency Allocation
    emergency_allocation = CalculateEmergencyAllocation(
        inputs.emergency_fund_preference,
        available_income
    )
    
    // Step 8: Calculate Discretionary Amount
    discretionary_amount = 
        available_income 
        - mandatory_commitments 
        - essential_allocation.total 
        - savings_allocation.total 
        - emergency_allocation
    
    // Step 9: Check for Deficit
    IF discretionary_amount < 0 THEN
        RETURN GenerateDeficitOutput(
            available_income,
            mandatory_commitments,
            essential_allocation.total,
            savings_allocation.total,
            emergency_allocation,
            discretionary_amount
        )
    END IF
    
    // Step 10: Distribute to Categories
    category_budgets = DistributeToCategories(
        discretionary_amount,
        inputs.historical_expenses,
        historical_months,
        inputs.user_defined_priorities
    )
    
    // Step 11: Generate Recommendations
    recommendations = GenerateRecommendations(
        essential_allocation,
        savings_allocation,
        emergency_allocation,
        category_budgets,
        historical_months
    )
    
    // Step 12: Apply User Override
    final_allocation = ApplyUserOverride(
        recommendations,
        inputs.user_modifications
    )
    
    // Step 13: Generate Output
    RETURN GenerateOutput(
        final_allocation,
        historical_months,
        recommendations
    )
    
END FUNCTION
```

### Essential Allocation Calculation

```
FUNCTION CalculateEssentialAllocation(inputs, historical_months, mandatory_commitments):
    
    essential_allocation = {}
    total_essential = 0
    
    essential_categories = [
        "Housing", "Groceries", "Electricity", "Gas", "Internet",
        "Mobile", "DTH", "Water", "Transportation", "Education",
        "Healthcare", "Insurance"
    ]
    
    FOR EACH category IN essential_categories:
        
        IF historical_months >= 3 THEN
            // Use historical data
            category_average = CalculateHistoricalAverage(
                inputs.historical_expenses,
                category
            )
            category_trend = CalculateTrend(
                inputs.historical_expenses,
                category
            )
            category_allocation = category_average × (1 + category_trend)
            
        ELSE IF historical_months >= 1 THEN
            // Use available data with low confidence
            category_average = CalculateHistoricalAverage(
                inputs.historical_expenses,
                category
            )
            category_allocation = category_average
            
        ELSE
            // Use baseline assumption
            category_allocation = 
                inputs.monthly_income × GetDefaultPercentage(category)
        END IF
        
        essential_allocation[category] = category_allocation
        total_essential += category_allocation
        
    END FOR
    
    RETURN {
        categories: essential_allocation,
        total: total_essential
    }
    
END FUNCTION
```

### Historical Average Calculation

```
FUNCTION CalculateHistoricalAverage(historical_expenses, category):
    
    category_expenses = FilterByCategory(historical_expenses, category)
    
    // Remove outliers
    filtered_expenses = RemoveOutliers(category_expenses)
    
    // Remove one-time expenses
    filtered_expenses = RemoveOneTimeExpenses(filtered_expenses)
    
    // Calculate weighted average
    IF COUNT(filtered_expenses) >= 3 THEN
        weighted_average = CalculateWeightedAverage(filtered_expenses)
    ELSE
        weighted_average = CalculateSimpleAverage(filtered_expenses)
    END IF
    
    RETURN weighted_average
    
END FUNCTION
```

### Trend Calculation

```
FUNCTION CalculateTrend(historical_expenses, category):
    
    category_expenses = FilterByCategory(historical_expenses, category)
    
    IF COUNT(category_expenses) < 2 THEN
        RETURN 0  // No trend data
    END IF
    
    recent_month = category_expenses[LAST_INDEX]
    previous_month = category_expenses[LAST_INDEX - 1]
    
    trend_percentage = 
        ((recent_month - previous_month) / previous_month) × 100
    
    // Cap trend at ±20% to prevent extreme recommendations
    IF trend_percentage > 20 THEN
        trend_percentage = 20
    ELSE IF trend_percentage < -20 THEN
        trend_percentage = -20
    END IF
    
    RETURN trend_percentage
    
END FUNCTION
```

### Savings Allocation Calculation

```
FUNCTION CalculateSavingsAllocation(savings_goals, available_amount):
    
    savings_allocation = {}
    total_savings = 0
    
    IF savings_goals EXISTS THEN
        
        // Sort by target date (soonest first)
        sorted_goals = SortByTargetDate(savings_goals)
        
        FOR EACH goal IN sorted_goals:
            
            remaining_months = MAX(1, goal.target_date - current_date_in_months)
            required_contribution = 
                (goal.target_amount - goal.current_amount) / remaining_months
            
            IF total_savings + required_contribution <= available_amount THEN
                savings_allocation[goal.id] = required_contribution
                total_savings += required_contribution
            ELSE
                // Not affordable
                savings_allocation[goal.id] = 0
                goal.affordable = FALSE
            END IF
            
        END FOR
        
    END IF
    
    RETURN {
        goals: savings_allocation,
        total: total_savings,
        affordable_goals: COUNT(goals WHERE affordable = TRUE)
    }
    
END FUNCTION
```

---

## 16. Output Model

### Complete Output Structure

```
AllocationOutput {
    // Planning Income
    planning_income: Decimal
    other_recurring_income: Decimal[]
    total_available_income: Decimal
    
    // Mandatory Commitments
    mandatory_commitments: Decimal
    debt_obligations: DebtObligation[]
    
    // Essential Allocation
    essential_allocation: {
        housing: Decimal
        groceries: Decimal
        electricity: Decimal
        gas: Decimal
        internet: Decimal
        mobile: Decimal
        dth: Decimal
        water: Decimal
        transportation: Decimal
        education: Decimal
        healthcare: Decimal
        insurance: Decimal
        total: Decimal
    }
    
    // Savings Allocation
    savings_allocation: {
        goals: Map<GoalID, Decimal>
        total: Decimal
        affordable_goals: Integer
        not_affordable_goals: Integer
    }
    
    // Emergency Allocation
    emergency_allocation: Decimal
    emergency_fund_percentage: Decimal
    
    // Discretionary Allocation
    discretionary_amount: Decimal
    
    // Category Budgets
    category_budgets: Map<Category, Decimal>
    
    // Total Planned
    total_planned: Decimal
    
    // Unallocated Amount
    unallocated_amount: Decimal
    
    // Deficit Amount
    deficit_amount: Decimal
    
    // Forecast
    forecast: {
        expected_month_end_spending: Decimal
        expected_category_spending: Map<Category, Decimal>
        expected_budget_variance: Map<Category, Decimal>
        confidence_level: String
        variance_range: Decimal
    }
    
    // Warnings
    warnings: Warning[]
    
    // Recommendations
    recommendations: Recommendation[]
    
    // Historical Data
    historical_data: {
        months_available: Integer
        confidence_level: String
        category_averages: Map<Category, Decimal>
        category_trends: Map<Category, Decimal>
    }
    
    // Versioning
    version: {
        recommendation_version: String
        generated_date: DateTime
        input_snapshot: InputSnapshot
        system_recommendation: Allocation
        user_changes: UserChange[]
        final_approved_budget: Allocation
    }
    
    // Explainability
    explainability: {
        calculation_method: String
        inputs_used: Input[]
        historical_data_used: HistoricalData[]
        rules_applied: Rule[]
        ai_involved: Boolean
        user_changes: UserChange[]
    }
}
```

---

## 17. Explainability

### Explainability Requirements

For every recommendation, provide:

#### Why Was This Amount Recommended?
```
Explanation: "Based on 6-month historical average of ₹10,500 and 8% upward trend"
```

#### Which Inputs Were Used?
```
Inputs Used:
- Monthly Income: ₹80,000
- Historical Groceries: ₹10,500 (6-month average)
- Grocery Trend: +8%
- Family Size: 4
```

#### Which Historical Data Was Used?
```
Historical Data Used:
- 6 months of grocery spending data
- Average: ₹10,500
- Trend: +8%
- Outliers Removed: 1 transaction (₹25,000 - one-time expense)
```

#### Which Rules Were Applied?
```
Rules Applied:
- BR-ALLOC-002: Historical Data Priority
- BR-ALLOC-003: User Override Authority
- BR-ALLOC-007: Deterministic Calculation
```

#### Was AI Involved?
```
AI Involved: Yes
AI Role: Explanation and pattern identification
Calculation: Deterministic engine
```

#### What Did the User Change?
```
User Changes:
- Original Recommendation: ₹11,340
- User Modified: ₹12,000
- Reason: "User preference for higher grocery budget"
```

### Explainability Output Format

```
Explainability {
    category: String
    recommended_amount: Decimal
    calculation_method: String
    inputs_used: Input[]
    historical_data_used: HistoricalData[]
    rules_applied: Rule[]
    ai_involved: Boolean
    ai_explanation: String
    user_changes: UserChange[]
    final_amount: Decimal
    confidence_level: String
}
```

---

## 18. Versioning

### Budget Recommendation Versioning

Every budget recommendation must be versioned:

```
BudgetVersion {
    version_id: String
    generated_date: DateTime
    generated_by: String (SYSTEM or USER)
    
    // Input Snapshot
    input_snapshot: {
        monthly_income: Decimal
        family_size: Integer
        historical_months: Integer
        savings_goals: SavingsGoal[]
    }
    
    // System Recommendation
    system_recommendation: Allocation
    
    // User Changes
    user_changes: UserChange[]
    
    // Final Approved Budget
    final_approved_budget: Allocation
    
    // Status
    status: String (DRAFT, APPROVED, ACTIVE, ARCHIVED)
}
```

### Version History

```
VersionHistory {
    budget_id: String
    versions: BudgetVersion[]
    current_version: String
}
```

### Change Tracking

```
UserChange {
    change_id: String
    category: String
    old_value: Decimal
    new_value: Decimal
    reason: String
    changed_by: String
    changed_date: DateTime
}
```

---

## 19. Configuration Parameters

### Configurable Parameters

#### Budget Thresholds
```
BUDGET_WARNING_PERCENTAGE: Decimal (default: 80%)
BUDGET_EXCEEDED_PERCENTAGE: Decimal (default: 100%)
```

#### Historical Data
```
MINIMUM_HISTORICAL_MONTHS: Integer (default: 3)
OUTLIER_THRESHOLD: Decimal (default: 2.0 standard deviations)
TREND_CAP_PERCENTAGE: Decimal (default: 20%)
```

#### Emergency Fund
```
EMERGENCY_FUND_PERCENTAGE: Decimal (default: 10%)
EMERGENCY_FUND_TARGET_MONTHS: Integer (default: 3 to 6)
```

#### Savings
```
DEFAULT_SAVINGS_PERCENTAGE: Decimal (default: 5%)
MINIMUM_SAVINGS_GOAL_AMOUNT: Decimal (default: ₹1,000)
```

#### Confidence Levels
```
HIGH_CONFIDENCE_MONTHS: Integer (default: 3)
MEDIUM_CONFIDENCE_MONTHS: Integer (default: 2)
LOW_CONFIDENCE_MONTHS: Integer (default: 0 or 1)
```

#### AI Confidence
```
HIGH_CONFIDENCE_THRESHOLD: Decimal (default: 80%)
MEDIUM_CONFIDENCE_MIN: Decimal (default: 60%)
LOW_CONFIDENCE_THRESHOLD: Decimal (default: 60%)
```

### Configuration Classification

#### User Configurable
- Budget warning percentage
- Budget exceeded percentage
- Notification preferences
- Emergency fund percentage
- Category priorities

#### Admin Configurable
- Default category percentages
- Historical data thresholds
- Confidence level thresholds
- AI confidence thresholds

#### System Controlled
- Financial health score weights
- Deterministic calculation formulas
- Algorithm logic

---

## 20. Open Decisions

### Critical Decisions (Before Development)

1. **Default Category Percentages**: What should be the default percentage for each category? (Proposed: Housing 30%, Groceries 15%, etc.)

2. **Emergency Fund Percentage**: What should be the default emergency fund percentage? (Proposed: 10%)

3. **Emergency Fund Target Months**: How many months of essential expenses for emergency fund target? (Proposed: 3-6 months, user configurable)

4. **Minimum Historical Months**: What is the minimum months required for high confidence recommendations? (Proposed: 3 months)

5. **Trend Cap Percentage**: What should be the maximum trend percentage allowed in recommendations? (Proposed: ±20%)

6. **Outlier Detection Method**: Which outlier detection method to use? (Proposed: Standard Deviation with 2.0 threshold)

7. **Weighted Average Weights**: What weights to use for weighted average? (Proposed: Recent 40%, Previous 30%, 2 Months Ago 20%, 3 Months Ago 10%)

8. **Savings Goal Priority**: How to prioritize multiple savings goals? (Proposed: By target date, then by amount)

9. **Budget Rollover**: Should unspent budget roll over to next month? (Proposed: Configurable, default disabled)

10. **AI Confidence Thresholds**: What should be the AI confidence thresholds? (Proposed: High ≥80%, Medium 60-79%, Low <60%)

### Important Decisions (Before MVP)

1. **Forecasting Method**: Which forecasting method to use for MVP? (Proposed: Deterministic linear extrapolation)

2. **Seasonal Pattern Detection**: How many months of data required for seasonal pattern detection? (Proposed: 12 months, deferred to post-MVP)

3. **Category Customization**: Should users be able to add custom categories in MVP? (Proposed: Yes, configurable)

4. **Family Size Impact**: How does family size impact category allocations? (Proposed: Per-capita adjustment for variable categories)

5. **Inflation Adjustment**: Should inflation adjustment be applied to historical averages? (Proposed: Configurable, default disabled for MVP)

### Configurable Defaults (Can Approve with Defaults)

1. **Budget Warning Threshold**: 80%
2. **Budget Exceeded Threshold**: 100%
3. **Bill Reminder Days**: 7 days, 2 days, due date
4. **Session Timeout**: 30 minutes
5. **Failed Authentication Lockout**: 5 attempts, 30 minutes
6. **Device Limit**: 5 devices
7. **Default Category Percentages**: As proposed above
8. **Emergency Fund Percentage**: 10%
9. **Default Savings Percentage**: 5%
10. **Trend Cap Percentage**: ±20%

---

## Conclusion

This document defines a deterministic budget allocation algorithm for NeoWallet MVP that:

- Uses historical data where available
- Respects user priorities and overrides
- Clearly distinguishes planning values from actual funds
- Provides explainable recommendations
- Handles edge cases gracefully
- Separates deterministic calculations from AI explanations
- Maintains version history for all recommendations

**Total Algorithm Components**: 20
**Core Formulas**: 7
**Edge Cases**: 16
**Configuration Parameters**: 10
**Open Decisions**: 10

**Next Steps**: Product Owner approval of critical and important decisions, then proceed to NW-003.

---

**Document Version**: v1
**Date**: August 17, 2026
**Status**: Ready for Product Owner Approval
**Next Step**: Product Owner approval, then NW-003
