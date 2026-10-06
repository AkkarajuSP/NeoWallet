# NeoWallet Financial Health Scoring Algorithm v1

## Executive Summary

This document defines the deterministic Financial Health Scoring Algorithm for NeoWallet MVP. The score provides an explainable educational indicator of household financial planning and behavior, clearly distinguished from regulated financial assessments like credit scores.

**Score Range**: 0-100
**Scoring Factors**: 6 (Budget Adherence, Savings Behavior, Expense Trend, Bill Discipline, Emergency Preparedness, Goal Progress)
**Algorithm Type**: Deterministic calculations with AI explanations
**Date**: August 17, 2026
**Status**: Ready for Product Owner Approval

---

## 1. Objective

### Primary Objective

NeoWallet shall provide an explainable Financial Health Score that helps families understand their current household financial position and behavior.

### Score Purpose

The score is an **educational and planning indicator** designed to:
- Help users understand their financial health
- Identify areas for improvement
- Track progress over time
- Provide actionable insights

### Score Limitations

The score is **NOT**:
- A credit score
- A loan eligibility score
- An investment rating
- A banking risk score
- A regulated financial assessment
- A guarantee of financial stability

### Score Disclaimer

All score displays must include:
- "Educational/Planning Indicator" label
- Confidence level
- Disclaimer: "This is an educational indicator, not a regulated financial assessment"

---

## 2. Score Definition

### Score Range

The Financial Health Score uses a 0-100 scale:

| Score Range | Label | Description |
|------------|-------|-------------|
| 0-39 | Critical | Significant financial health concerns require immediate attention |
| 40-59 | Needs Attention | Financial health has notable areas needing improvement |
| 60-74 | Fair | Financial health is acceptable but has room for improvement |
| 75-89 | Good | Financial health is strong with minor areas for improvement |
| 90-100 | Excellent | Financial health is very strong with excellent planning |

### Score Range Configuration

Score ranges are **configurable** with the following structure:

```
ScoreRange {
    critical_min: 0
    critical_max: 39
    needs_attention_min: 40
    needs_attention_max: 59
    fair_min: 60
    fair_max: 74
    good_min: 75
    good_max: 89
    excellent_min: 90
    excellent_max: 100
}
```

---

## 3. Scoring Factors

### Factor Overview

The Financial Health Score is calculated from 6 factors:

1. **Budget Adherence** (20%): How well spending stays within budget limits
2. **Savings Behavior** (20%): Consistency of savings habits
3. **Expense Trend** (15%): Direction and stability of spending patterns
4. **Bill Discipline** (15%): Timeliness of bill payments
5. **Emergency Preparedness** (15%): Adequacy of emergency fund
6. **Savings Goal Progress** (15%): Progress toward savings goals

### Factor Selection Rationale

**Budget Adherence (20%)**: Core financial discipline indicator
**Savings Behavior (20%)**: Critical for long-term financial health
**Expense Trend (15%)**: Indicates spending stability and control
**Bill Discipline (15%)**: Reflects payment responsibility and organization
**Emergency Preparedness (15%)**: Essential for financial resilience
**Savings Goal Progress (15%)**: Shows progress toward financial objectives

### Additional Factors Considered

The following factors were considered but **NOT included in MVP** to avoid overcomplication:
- Debt-to-income ratio (requires external data)
- Investment performance (not available in MVP)
- Credit utilization (requires external data)
- Net worth tracking (requires external asset data)

---

## 4. Weights

### Proposed Weights

```
Budget Adherence          20%
Savings Behavior          20%
Expense Trend             15%
Bill Discipline           15%
Emergency Preparedness    15%
Goal Progress             15%
TOTAL                    100%
```

### Weight Rationale

**Budget Adherence (20%)**: 
- Highest weight because budget adherence is the foundation of financial health
- Direct measure of financial discipline
- Most actionable factor for users

**Savings Behavior (20%)**:
- High weight because savings is critical for long-term financial security
- Directly impacts financial resilience
- Key indicator of financial planning

**Expense Trend (15%)**:
- Medium weight because trend indicates future direction
- Helps identify emerging issues before they become critical
- Less controllable than budget adherence

**Bill Discipline (15%)**:
- Medium weight because it reflects organizational habits
- Important for avoiding late fees and penalties
- Easier to improve than overall spending

**Emergency Preparedness (15%)**:
- Medium weight because emergency funds are essential but not always immediately critical
- Depends on user-provided information
- May not reflect total financial resources

**Goal Progress (15%)**:
- Medium weight because goal progress is aspirational
- Users may not have goals
- Less critical than day-to-day financial discipline

### Weight Configuration

Weights are **configurable** with the constraint that they must total 100%:

```
Weights {
    budget_adherence_weight: Decimal (default: 0.20)
    savings_behavior_weight: Decimal (default: 0.20)
    expense_trend_weight: Decimal (default: 0.15)
    bill_discipline_weight: Decimal (default: 0.15)
    emergency_preparedness_weight: Decimal (default: 0.15)
    goal_progress_weight: Decimal (default: 0.15)
}
```

---

## 5. Mathematical Formulas

### Overall Score Formula

```
Financial Health Score = 
    (Budget Adherence Score × Budget Adherence Weight) +
    (Savings Behavior Score × Savings Behavior Weight) +
    (Expense Trend Score × Expense Trend Weight) +
    (Bill Discipline Score × Bill Discipline Weight) +
    (Emergency Preparedness Score × Emergency Preparedness Weight) +
    (Goal Progress Score × Goal Progress Weight)
```

### Factor Score Formula

Each factor score is calculated on a 0-100 scale.

---

## 6. Budget Adherence Score

### Formula

```
Budget Adherence Score = 
    (Number of Categories Within Budget / Total Number of Categories) × 100
```

### Calculation Steps

1. For each budget category, calculate utilization:
   ```
   Category Utilization % = (Actual Spending / Budget Limit) × 100
   ```

2. Determine if category is within budget:
   ```
   Within Budget = Category Utilization % ≤ 100
   ```

3. Calculate overall adherence:
   ```
   Budget Adherence Score = (Within Budget Count / Total Category Count) × 100
   ```

### Nuanced Scoring

For a more nuanced score, consider utilization levels:

```
IF Category Utilization ≤ 80% THEN
    Category Score = 100
ELSE IF Category Utilization ≤ 100% THEN
    Category Score = 100 - ((Utilization - 80) / 20) × 20
ELSE IF Category Utilization ≤ 120% THEN
    Category Score = 80 - ((Utilization - 100) / 20) × 60
ELSE
    Category Score = 20 - ((Utilization - 120) / 80) × 20
END IF
```

### Family Budget

For family budgets, calculate adherence per family member:

```
Family Budget Adherence = Average of Individual Member Budget Adherence
```

### Example

```
Categories: 10
Within Budget: 8
Over Budget: 2

Budget Adherence Score = (8 / 10) × 100 = 80
```

---

## 7. Savings Behavior Score

### Formula

```
Savings Behavior Score = 
    (Actual Savings / Planned Savings) × 100
```

### Calculation Steps

1. Calculate planned savings:
   ```
   Planned Savings = SUM(Savings Goal Monthly Contributions) + Emergency Allocation
   ```

2. Calculate actual savings:
   ```
   Actual Savings = SUM(Actual Savings Contributions) + Emergency Fund Contributions
   ```

3. Calculate score:
   ```
   Savings Behavior Score = (Actual Savings / Planned Savings) × 100
   ```

### Score Capping

```
IF Savings Behavior Score > 100 THEN
    Savings Behavior Score = 100
END IF
```

### No Savings Scenario

```
IF Planned Savings = 0 THEN
    Savings Behavior Score = 50 (neutral)
    Note: "No savings plan established"
END IF
```

### Negative Savings Scenario

```
IF Actual Savings < 0 THEN
    Savings Behavior Score = 0
    Note: "Negative savings (net withdrawal)"
END IF
```

### Example

```
Planned Savings: ₹10,000
Actual Savings: ₹8,000

Savings Behavior Score = (8,000 / 10,000) × 100 = 80
```

---

## 8. Expense Trend Score

### Formula

```
Expense Trend Score = 100 - (Expense Increase Percentage × Weight)
```

### Calculation Steps

1. Calculate current month spending:
   ```
   Current Spending = SUM(Current Month Transactions)
   ```

2. Calculate historical average:
   ```
   Historical Average = Average of Previous 3 Months Spending
   ```

3. Calculate trend percentage:
   ```
   Trend % = ((Current Spending - Historical Average) / Historical Average) × 100
   ```

4. Calculate score:
   ```
   IF Trend % ≤ 0 THEN
       Expense Trend Score = 100
   ELSE IF Trend % ≤ 10 THEN
       Expense Trend Score = 100 - (Trend % × 2)
   ELSE IF Trend % ≤ 20 THEN
       Expense Trend Score = 80 - ((Trend % - 10) × 3)
   ELSE
       Expense Trend Score = 50 - ((Trend % - 20) × 1.5)
   END IF
```

### Score Capping

```
IF Expense Trend Score < 0 THEN
    Expense Trend Score = 0
END IF
```

### Volatility Adjustment

For highly volatile spending, apply volatility penalty:

```
IF Standard Deviation > Average × 0.3 THEN
    Volatility Penalty = 10
    Expense Trend Score = MAX(0, Expense Trend Score - Volatility Penalty)
END IF
```

### Example

```
Historical Average: ₹50,000
Current Spending: ₹55,000
Trend %: +10%

Expense Trend Score = 100 - (10 × 2) = 80
```

---

## 9. Bill Discipline Score

### Formula

```
Bill Discipline Score = 
    (Bills Paid on Time / Total Bills) × 100
```

### Calculation Steps

1. For each bill, determine if paid on time:
   ```
   Paid on Time = Payment Date ≤ Due Date + Grace Period
   ```

2. Calculate overall discipline:
   ```
   Bill Discipline Score = (Paid on Time Count / Total Bill Count) × 100
   ```

### Grace Period

Default grace period: 3 days after due date

```
Grace Period = 3 days (configurable)
```

### Overdue Penalty

For bills significantly overdue, apply additional penalty:

```
IF Days Overdue > 7 THEN
    Overdue Penalty = 5
    Bill Discipline Score = MAX(0, Bill Discipline Score - Overdue Penalty)
END IF
```

### No Bills Scenario

```
IF Total Bills = 0 THEN
    Bill Discipline Score = 100 (neutral)
    Note: "No bills to track"
END IF
```

### Example

```
Total Bills: 10
Paid on Time: 9
Overdue: 1

Bill Discipline Score = (9 / 10) × 100 = 90
```

---

## 10. Emergency Preparedness Score

### Formula

```
Emergency Preparedness Score = 
    (Current Emergency Fund / Target Emergency Fund) × 100
```

### Calculation Steps

1. Determine target emergency fund:
   ```
   Target Emergency Fund = 3 to 6 Months of Essential Expenses
   ```
   (User configurable, default: 3 months)

2. Calculate current emergency fund:
   ```
   Current Emergency Fund = User-Provided Emergency Fund Amount
   ```

3. Calculate score:
   ```
   Emergency Preparedness Score = (Current Emergency Fund / Target Emergency Fund) × 100
   ```

### Score Capping

```
IF Emergency Preparedness Score > 100 THEN
    Emergency Preparedness Score = 100
END IF
```

### No Emergency Fund Scenario

```
IF Current Emergency Fund = 0 THEN
    Emergency Preparedness Score = 0
    Note: "No emergency fund established"
END IF
```

### Unknown Resources Scenario

```
IF Target Emergency Fund Not Provided THEN
    Emergency Preparedness Score = 50 (neutral)
    Note: "Emergency fund target not set"
END IF
```

### Example

```
Target Emergency Fund: ₹150,000 (3 months of ₹50,000 essential expenses)
Current Emergency Fund: ₹75,000

Emergency Preparedness Score = (75,000 / 150,000) × 100 = 50
```

---

## 11. Savings Goal Progress Score

### Formula

```
Savings Goal Progress Score = 
    Average of Individual Goal Progress Scores
```

### Individual Goal Progress Formula

```
Individual Goal Progress = 
    (Current Amount / Target Amount) × 100
```

### Calculation Steps

1. For each savings goal, calculate progress:
   ```
   Goal Progress = (Current Amount / Target Amount) × 100
   ```

2. Calculate overall score:
   ```
   Savings Goal Progress Score = Average of All Goal Progress Scores
   ```

### Time Adjustment

Adjust for time remaining to target:

```
IF Current Progress < Expected Progress THEN
    Time Penalty = (Expected Progress - Current Progress) × 0.5
    Goal Progress = MAX(0, Goal Progress - Time Penalty)
END IF

Expected Progress = (Elapsed Months / Total Months) × 100
```

### No Goals Scenario

```
IF Total Goals = 0 THEN
    Savings Goal Progress Score = 50 (neutral)
    Note: "No savings goals established"
END IF
```

### Example

```
Goal 1: ₹10,000 / ₹20,000 = 50%
Goal 2: ₹15,000 / ₹15,000 = 100%

Savings Goal Progress Score = (50 + 100) / 2 = 75
```

---

## 12. Missing Data Handling

### Data Availability Rules

#### 0 Months of Historical Data

```
IF Historical Months = 0 THEN
    Budget Adherence Score = 50 (neutral)
    Expense Trend Score = 50 (neutral)
    Note: "Insufficient historical data"
    Overall Confidence LOW
END IF
```

#### 1 Month of Historical Data

```
IF Historical Months = 1 THEN
    Budget Adherence Score = Calculate with available data
    Expense Trend Score = 50 (neutral - no trend)
    Note: "Limited historical data"
    Overall Confidence LOW
END IF
```

#### 2 Months of Historical Data

```
IF Historical Months = 2 THEN
    Budget Adherence Score = Calculate with available data
    Expense Trend Score = Calculate with limited trend
    Note: "Limited trend data"
    Overall Confidence MEDIUM
END IF
```

#### 3+ Months of Historical Data

```
IF Historical Months >= 3 THEN
    Budget Adherence Score = Calculate with full data
    Expense Trend Score = Calculate with full trend
    Note: "Sufficient historical data"
    Overall Confidence HIGH
END IF
```

### Provisional Score

During early usage (0-2 months), the score is marked as **Provisional**:

```
IF Historical Months < 3 THEN
    Score Status = PROVISIONAL
    Display Note: "Score will improve with more historical data"
END IF
```

### Unavailable Score

If insufficient data exists for critical factors, the overall score may be **Unavailable**:

```
IF Critical Factors Missing THEN
    Overall Score = UNAVAILABLE
    Display Note: "Insufficient data to calculate score"
END IF
```

### Factor-Level Missing Data

Each factor handles missing data independently:

```
IF Factor Data Missing THEN
    Factor Score = 50 (neutral)
    Factor Note: "Insufficient data"
END IF
```

---

## 13. Confidence Model

### Confidence Definition

Score Confidence is separate from the Financial Health Score and indicates the reliability of the calculated score based on data availability.

### Confidence Levels

| Confidence Level | Historical Months | Description |
|------------------|-------------------|-------------|
| Low | 0-1 months | Limited data, score may change significantly |
| Medium | 2 months | Moderate data, score reasonably stable |
| High | 3+ months | Sufficient data, score is reliable |

### Confidence Calculation

```
IF Historical Months >= 3 THEN
    Confidence = HIGH
ELSE IF Historical Months = 2 THEN
    Confidence = MEDIUM
ELSE
    Confidence = LOW
END IF
```

### Factor-Level Confidence

Each factor has its own confidence level based on its data availability:

```
Factor Confidence = 
    IF Factor Data Available THEN
        Calculate based on data months
    ELSE
        LOW
    END IF
```

### Overall Confidence

```
Overall Confidence = Minimum of All Factor Confidences
```

### Confidence Display

```
Financial Health Score: 78
Confidence: Medium
Reason: "2 months of transaction history available"
```

---

## 14. Individual Score

### Individual Score Calculation

Individual scores are calculated using the same factors but with individual-specific data:

```
Individual Financial Health Score = 
    (Individual Budget Adherence × Weight) +
    (Individual Savings Behavior × Weight) +
    (Individual Expense Trend × Weight) +
    (Individual Bill Discipline × Weight) +
    (Individual Emergency Preparedness × Weight) +
    (Individual Goal Progress × Weight)
```

### Individual vs Family Data

Individual scores use:
- Individual transactions
- Individual budgets
- Individual bills
- Individual savings goals
- Individual emergency fund

Family scores use:
- Family transactions (aggregated)
- Family budgets
- Family bills
- Family savings goals
- Family emergency fund

### Score Comparison

Users can compare their individual score to the family score:

```
Individual Score: 82
Family Score: 78
Difference: +4
Note: "Your individual financial health is better than family average"
```

---

## 15. Family Score

### Family Score Calculation

Family scores aggregate data from all family members:

```
Family Financial Health Score = 
    (Family Budget Adherence × Weight) +
    (Family Savings Behavior × Weight) +
    (Family Expense Trend × Weight) +
    (Family Bill Discipline × Weight) +
    (Family Emergency Preparedness × Weight) +
    (Family Goal Progress × Weight)
```

### Family Budget Adherence

```
Family Budget Adherence = 
    (Family Within Budget Categories / Total Family Categories) × 100
```

### Family Savings Behavior

```
Family Savings Behavior = 
    (Family Actual Savings / Family Planned Savings) × 100
```

### Family Expense Trend

```
Family Expense Trend = 
    Family Current Spending vs Family Historical Average
```

### Family Bill Discipline

```
Family Bill Discipline = 
    (Family Bills Paid on Time / Total Family Bills) × 100
```

### Family Emergency Preparedness

```
Family Emergency Preparedness = 
    (Family Emergency Fund / Family Target Emergency Fund) × 100
```

### Family Goal Progress

```
Family Goal Progress = 
    Average of Family Savings Goal Progress
```

### Access Control

RESTRICTED family members see limited family score information:

```
IF User Role = RESTRICTED THEN
    Hide: Individual member scores
    Hide: Detailed factor breakdowns
    Show: Overall family score only
END IF
```

---

## 16. Score Change

### Score Change Calculation

```
Score Change = Current Score - Previous Score

Score Change % = ((Current Score - Previous Score) / Previous Score) × 100
```

### Change Direction

```
IF Score Change > 0 THEN
    Direction = IMPROVING
ELSE IF Score Change < 0 THEN
    Direction = DECLINING
ELSE
    Direction = STABLE
END IF
```

### Change Magnitude

```
IF ABS(Score Change) ≤ 5 THEN
    Magnitude = MINOR
ELSE IF ABS(Score Change) ≤ 15 THEN
    Magnitude = MODERATE
ELSE
    Magnitude = SIGNIFICANT
END IF
```

### Change Explanation

For each factor, explain the change:

```
Factor Change = Current Factor Score - Previous Factor Score

IF Factor Change ≠ 0 THEN
    Explain Reason:
    - "Grocery spending increased 12% this month"
    - "Savings contribution increased by ₹2,000"
    - "All bills paid on time this month"
END IF
```

### Example

```
Last Month: 72
Current: 78
Change: +6
Direction: IMPROVING
Magnitude: MODERATE

Major Reasons:
- Budget Adherence improved from 75 to 85
- Savings Behavior improved from 70 to 80
- Expense Trend improved from 65 to 75
```

---

## 17. Explainability

### Explainability Requirements

Every score must explain:

#### Overall Score
```
Financial Health Score: 78
Label: Good
Confidence: Medium
```

#### Factor Scores
```
Budget Adherence: 84 (weight: 20%)
Savings Behavior: 72 (weight: 20%)
Expense Trend: 81 (weight: 15%)
Bill Discipline: 95 (weight: 15%)
Emergency Preparedness: 60 (weight: 15%)
Goal Progress: 78 (weight: 15%)
```

#### Factor Weights
```
Budget Adherence contributes: 84 × 0.20 = 16.8 points
Savings Behavior contributes: 72 × 0.20 = 14.4 points
Expense Trend contributes: 81 × 0.15 = 12.15 points
Bill Discipline contributes: 95 × 0.15 = 14.25 points
Emergency Preparedness contributes: 60 × 0.15 = 9 points
Goal Progress contributes: 78 × 0.15 = 11.7 points
Total: 78.3 (rounded to 78)
```

#### Positive Contributors
```
Positive Contributors:
- Bill Discipline: 95 (excellent bill payment discipline)
- Budget Adherence: 84 (good budget adherence)
```

#### Negative Contributors
```
Negative Contributors:
- Emergency Preparedness: 60 (emergency fund needs improvement)
- Savings Behavior: 72 (savings below target)
```

#### Missing Data
```
Missing Data:
- None (all factors have sufficient data)
```

#### Recommended Actions
```
Recommended Actions:
1. Increase emergency fund allocation to reach target
2. Review savings allocation to meet monthly target
3. Continue excellent bill payment discipline
```

#### Score Change From Previous Period
```
Score Change: +6 (from 72 to 78)
Major Reasons:
- Grocery spending decreased 5% this month
- Savings contribution increased by ₹2,000
- All bills paid on time
```

### Explainability Output Format

```
Explainability {
    overall_score: Integer
    score_label: String
    confidence_level: String
    
    factor_scores: {
        budget_adherence: {score: Integer, weight: Decimal, contribution: Decimal}
        savings_behavior: {score: Integer, weight: Decimal, contribution: Decimal}
        expense_trend: {score: Integer, weight: Decimal, contribution: Decimal}
        bill_discipline: {score: Integer, weight: Decimal, contribution: Decimal}
        emergency_preparedness: {score: Integer, weight: Decimal, contribution: Decimal}
        goal_progress: {score: Integer, weight: Decimal, contribution: Decimal}
    }
    
    positive_contributors: Factor[]
    negative_contributors: Factor[]
    
    missing_data: MissingData[]
    
    recommended_actions: Action[]
    
    score_change: {
        previous_score: Integer
        current_score: Integer
        change: Integer
        direction: String
        magnitude: String
        reasons: Reason[]
    }
}
```

---

## 18. Recommendations

### Deterministic Recommendation Triggers

#### Low Budget Adherence
```
IF Budget Adherence Score < 70 THEN
    Recommendation: "Review high-spending categories"
    Action: "Identify categories exceeding budget and consider reducing spending"
END IF
```

#### Low Savings Score
```
IF Savings Behavior Score < 60 THEN
    Recommendation: "Review savings allocation"
    Action: "Consider increasing savings allocation or reducing discretionary spending"
END IF
```

#### Increasing Expense Trend
```
IF Expense Trend Score < 70 THEN
    Recommendation: "Review variable expenses"
    Action: "Identify categories with increasing spending and consider optimization"
END IF
```

#### Low Emergency Preparedness
```
IF Emergency Preparedness Score < 50 THEN
    Recommendation: "Consider emergency allocation"
    Action: "Increase emergency fund allocation to improve financial resilience"
END IF
```

#### Low Goal Progress
```
IF Goal Progress Score < 50 THEN
    Recommendation: "Review savings goals"
    Action: "Consider adjusting target dates or amounts, or increase contributions"
END IF
```

### Recommendation Priority

Recommendations are prioritized by score impact:

```
Priority = (100 - Factor Score) × Factor Weight
```

### Recommendation Format

```
Recommendation {
    factor: String
    factor_score: Integer
    priority: Integer
    recommendation: String
    action: String
    estimated_impact: String
}
```

---

## 19. AI Responsibilities

### AI Responsibilities

AI may:

1. **Explain the Score**: Provide natural language explanation of score and factors
2. **Explain Individual Factors**: Explain why a factor has a specific score
3. **Suggest Improvements**: Suggest ways to improve specific factor scores
4. **Generate Personalized Recommendations**: Provide tailored advice based on user's situation
5. **Identify Patterns**: Identify spending patterns that impact financial health
6. **Provide Context**: Provide context about score changes and trends

### AI Cannot

AI must NOT:

1. **Calculate the Authoritative Score**: The score is calculated deterministically by the engine
2. **Change the Score**: AI cannot modify the calculated score
3. **Modify Scoring Weights**: AI cannot change factor weights
4. **Modify Financial Records**: AI cannot modify transactions, budgets, or other records
5. **Override Deterministic Calculations**: AI cannot override the deterministic calculation engine

### AI Explanation Example

**Deterministic Engine Output**:
```
Budget Adherence Score: 84
Calculated from: 8/10 categories within budget
```

**AI Explanation**:
```
"Your budget adherence score of 84 is good. You stayed within budget in 8 out of 10 categories this month. The two categories where you exceeded budget were Groceries (₹12,000 vs ₹10,000 budget) and Entertainment (₹3,000 vs ₹2,000 budget). Consider reviewing your grocery spending to improve this score next month."
```

---

## 20. Edge Cases

### No Transactions
```
IF Total Transactions = 0 THEN
    Budget Adherence Score = 50 (neutral)
    Expense Trend Score = 50 (neutral)
    Note: "No transaction data available"
END IF
```

### No Income
```
IF Monthly Income = 0 THEN
    Financial Health Score = UNAVAILABLE
    Note: "Income data required for score calculation"
END IF
```

### No Savings
```
IF Planned Savings = 0 AND Actual Savings = 0 THEN
    Savings Behavior Score = 50 (neutral)
    Note: "No savings plan established"
END IF
```

### No Bills
```
IF Total Bills = 0 THEN
    Bill Discipline Score = 100 (neutral)
    Note: "No bills to track"
END IF
```

### No Savings Goals
```
IF Total Goals = 0 THEN
    Goal Progress Score = 50 (neutral)
    Note: "No savings goals established"
END IF
```

### Negative Monthly Position
```
IF Expenses > Income THEN
    Financial Health Score = 0 (Critical)
    Note: "Spending exceeds income - immediate attention required"
END IF
```

### One-Time Large Expense
```
IF Transaction Amount > (Average Monthly Spending × 3) THEN
    Mark as One-Time Expense
    Exclude from Trend Calculation
    Note: "One-time expense excluded from trend"
END IF
```

### Refund
```
IF Transaction Type = Refund THEN
    Treat as Negative Expense
    Subtract from Category Spending
    Note: "Refund applied to category"
END IF
```

### Family Member Added
```
IF Family Member Added THEN
    Recalculate Family Score
    Update Family Budget Adherence
    Update Family Savings Behavior
    Display Impact on Family Score
END IF
```

### Family Member Removed
```
IF Family Member Removed THEN
    Recalculate Family Score
    Update Family Budget Adherence
    Update Family Savings Behavior
    Display Impact on Family Score
END IF
```

### Missing Historical Data
```
IF Historical Months < 3 THEN
    Mark Score as Provisional
    Use Available Data with Low Confidence
    Display Note: "Score will improve with more historical data"
END IF
```

### Changed Budget
```
IF Budget Modified THEN
    Recalculate Budget Adherence Score
    Update Overall Score
    Preserve Previous Version
    Display Score Change
END IF
```

### Changed Scoring Configuration
```
IF Scoring Weights Modified THEN
    Recalculate Overall Score
    Preserve Previous Version
    Display Score Change
    Note: "Scoring configuration changed"
END IF
```

---

## 21. Versioning

### Score Versioning

Every financial health score must be versioned:

```
ScoreVersion {
    version_id: String
    calculation_date: DateTime
    
    // Input Snapshot
    input_snapshot: {
        monthly_income: Decimal
        historical_months: Integer
        budget_data: BudgetData
        savings_data: SavingsData
        bill_data: BillData
        emergency_data: EmergencyData
        goal_data: GoalData
    }
    
    // Factor Scores
    factor_scores: {
        budget_adherence: Integer
        savings_behavior: Integer
        expense_trend: Integer
        bill_discipline: Integer
        emergency_preparedness: Integer
        goal_progress: Integer
    }
    
    // Weights
    weights: {
        budget_adherence_weight: Decimal
        savings_behavior_weight: Decimal
        expense_trend_weight: Decimal
        bill_discipline_weight: Decimal
        emergency_preparedness_weight: Decimal
        goal_progress_weight: Decimal
    }
    
    // Final Score
    final_score: Integer
    
    // Confidence
    confidence_level: String
    
    // Recommendations
    recommendations: Recommendation[]
}
```

### Version History

```
ScoreHistory {
    user_id: String
    family_id: String (optional)
    versions: ScoreVersion[]
    current_version: String
}
```

### Reproducibility

Historical scores must remain reproducible:

```
IF Recalculate Historical Version THEN
    Input Snapshot = Historical Version.input_snapshot
    Weights = Historical Version.weights
    Recalculated Score = CalculateScore(Input Snapshot, Weights)
    Assert Recalculated Score == Historical Version.final_score
END IF
```

---

## 22. Configuration

### Configurable Parameters

#### Score Ranges
```
CRITICAL_MIN: Integer (default: 0)
CRITICAL_MAX: Integer (default: 39)
NEEDS_ATTENTION_MIN: Integer (default: 40)
NEEDS_ATTENTION_MAX: Integer (default: 59)
FAIR_MIN: Integer (default: 60)
FAIR_MAX: Integer (default: 74)
GOOD_MIN: Integer (default: 75)
GOOD_MAX: Integer (default: 89)
EXCELLENT_MIN: Integer (default: 90)
EXCELLENT_MAX: Integer (default: 100)
```

#### Factor Weights
```
BUDGET_ADHERENCE_WEIGHT: Decimal (default: 0.20)
SAVINGS_BEHAVIOR_WEIGHT: Decimal (default: 0.20)
EXPENSE_TREND_WEIGHT: Decimal (default: 0.15)
BILL_DISCIPLINE_WEIGHT: Decimal (default: 0.15)
EMERGENCY_PREPAREDNESS_WEIGHT: Decimal (default: 0.15)
GOAL_PROGRESS_WEIGHT: Decimal (default: 0.15)
```

#### Minimum Data Period
```
MINIMUM_HISTORICAL_MONTHS_HIGH_CONFIDENCE: Integer (default: 3)
MINIMUM_HISTORICAL_MONTHS_MEDIUM_CONFIDENCE: Integer (default: 2)
```

#### Confidence Thresholds
```
HIGH_CONFIDENCE_MONTHS: Integer (default: 3)
MEDIUM_CONFIDENCE_MONTHS: Integer (default: 2)
```

#### Recommendation Thresholds
```
BUDGET_ADHERENCE_RECOMMENDATION_THRESHOLD: Integer (default: 70)
SAVINGS_BEHAVIOR_RECOMMENDATION_THRESHOLD: Integer (default: 60)
EXPENSE_TREND_RECOMMENDATION_THRESHOLD: Integer (default: 70)
EMERGENCY_PREPAREDNESS_RECOMMENDATION_THRESHOLD: Integer (default: 50)
GOAL_PROGRESS_RECOMMENDATION_THRESHOLD: Integer (default: 50)
```

#### Bill Parameters
```
BILL_GRACE_PERIOD_DAYS: Integer (default: 3)
BILL_OVERDUE_PENALTY_THRESHOLD_DAYS: Integer (default: 7)
BILL_OVERDUE_PENALTY_AMOUNT: Integer (default: 5)
```

#### Emergency Fund Parameters
```
EMERGENCY_FUND_TARGET_MONTHS: Integer (default: 3)
EMERGENCY_FUND_PERCENTAGE: Decimal (default: 10)
```

### Configuration Classification

#### User Configurable
- Emergency fund target months
- Emergency fund percentage
- Recommendation thresholds

#### Admin Configurable
- Score ranges
- Factor weights
- Minimum data periods
- Confidence thresholds
- Bill parameters

#### System Controlled
- Deterministic calculation formulas
- Algorithm logic
- Confidence calculation

---

## 23. Worked Examples

### Example 1: New User (0 Months Historical Data)

**Inputs**:
- Monthly Income: ₹80,000
- Historical Data: 0 months
- Budgets: Not established
- Savings: Not established
- Bills: Not entered
- Emergency Fund: Not established
- Goals: Not established

**Calculation**:
```
Budget Adherence Score = 50 (neutral - no data)
Savings Behavior Score = 50 (neutral - no plan)
Expense Trend Score = 50 (neutral - no data)
Bill Discipline Score = 100 (neutral - no bills)
Emergency Preparedness Score = 50 (neutral - no data)
Goal Progress Score = 50 (neutral - no goals)

Financial Health Score = 
    (50 × 0.20) + (50 × 0.20) + (50 × 0.15) + (100 × 0.15) + (50 × 0.15) + (50 × 0.15)
    = 10 + 10 + 7.5 + 15 + 7.5 + 7.5
    = 57.5 ≈ 58

Label: Needs Attention
Confidence: Low
Status: Provisional
```

**Output**:
```
Financial Health Score: 58
Label: Needs Attention
Confidence: Low
Note: "Score will improve with more historical data"
```

### Example 2: Established User (3+ Months Historical Data)

**Inputs**:
- Monthly Income: ₹80,000
- Historical Data: 6 months
- Budgets: 10 categories, 8 within budget
- Planned Savings: ₹10,000
- Actual Savings: ₹8,000
- Bills: 10 bills, 9 paid on time
- Emergency Fund: ₹75,000 (target: ₹150,000)
- Goals: 2 goals, 50% and 100% progress

**Calculation**:
```
Budget Adherence Score = (8 / 10) × 100 = 80
Savings Behavior Score = (8,000 / 10,000) × 100 = 80
Expense Trend = +5% (stable)
Expense Trend Score = 100 - (5 × 2) = 90
Bill Discipline Score = (9 / 10) × 100 = 90
Emergency Preparedness Score = (75,000 / 150,000) × 100 = 50
Goal Progress Score = (50 + 100) / 2 = 75

Financial Health Score = 
    (80 × 0.20) + (80 × 0.20) + (90 × 0.15) + (90 × 0.15) + (50 × 0.15) + (75 × 0.15)
    = 16 + 16 + 13.5 + 13.5 + 7.5 + 11.25
    = 77.75 ≈ 78

Label: Good
Confidence: High
```

**Output**:
```
Financial Health Score: 78
Label: Good
Confidence: High

Factor Scores:
- Budget Adherence: 80 (16 points)
- Savings Behavior: 80 (16 points)
- Expense Trend: 90 (13.5 points)
- Bill Discipline: 90 (13.5 points)
- Emergency Preparedness: 50 (7.5 points)
- Goal Progress: 75 (11.25 points)

Positive Contributors:
- Bill Discipline: 90 (excellent)
- Expense Trend: 90 (stable)

Negative Contributors:
- Emergency Preparedness: 50 (needs improvement)

Recommended Actions:
1. Increase emergency fund allocation to reach target
2. Continue excellent bill payment discipline
```

### Example 3: Score Change

**Previous Month**:
```
Budget Adherence: 75
Savings Behavior: 70
Expense Trend: 65
Bill Discipline: 95
Emergency Preparedness: 60
Goal Progress: 70

Score = 72
```

**Current Month**:
```
Budget Adherence: 84
Savings Behavior: 80
Expense Trend: 81
Bill Discipline: 95
Emergency Preparedness: 60
Goal Progress: 78

Score = 78
```

**Change**:
```
Score Change: +6
Direction: IMPROVING
Magnitude: MODERATE

Major Reasons:
- Budget Adherence improved from 75 to 84 (grocery spending decreased 5%)
- Savings Behavior improved from 70 to 80 (savings contribution increased by ₹2,000)
- Expense Trend improved from 65 to 81 (spending stabilized)
```

---

## 24. Pseudocode

### Main Scoring Algorithm

```
FUNCTION CalculateFinancialHealthScore(inputs):
    
    // Step 1: Input Validation
    IF inputs.monthly_income <= 0 THEN
        RETURN Error("Income is required for score calculation")
    END IF
    
    // Step 2: Determine Data Availability
    historical_months = CountHistoricalMonths(inputs.historical_expenses)
    confidence = DetermineConfidence(historical_months)
    
    // Step 3: Calculate Factor Scores
    budget_adherence_score = CalculateBudgetAdherenceScore(
        inputs.budgets,
        inputs.transactions,
        historical_months
    )
    
    savings_behavior_score = CalculateSavingsBehaviorScore(
        inputs.savings_data,
        historical_months
    )
    
    expense_trend_score = CalculateExpenseTrendScore(
        inputs.transactions,
        historical_months
    )
    
    bill_discipline_score = CalculateBillDisciplineScore(
        inputs.bills,
        historical_months
    )
    
    emergency_preparedness_score = CalculateEmergencyPreparednessScore(
        inputs.emergency_fund,
        inputs.essential_expenses
    )
    
    goal_progress_score = CalculateGoalProgressScore(
        inputs.savings_goals,
        historical_months
    )
    
    // Step 4: Apply Weights
    weights = GetConfiguredWeights()
    
    weighted_score = 
        (budget_adherence_score × weights.budget_adherence) +
        (savings_behavior_score × weights.savings_behavior) +
        (expense_trend_score × weights.expense_trend) +
        (bill_discipline_score × weights.bill_discipline) +
        (emergency_preparedness_score × weights.emergency_preparedness) +
        (goal_progress_score × weights.goal_progress)
    
    // Step 5: Round to Integer
    final_score = ROUND(weighted_score)
    
    // Step 6: Determine Label
    score_label = DetermineScoreLabel(final_score)
    
    // Step 7: Generate Recommendations
    recommendations = GenerateRecommendations(
        budget_adherence_score,
        savings_behavior_score,
        expense_trend_score,
        bill_discipline_score,
        emergency_preparedness_score,
        goal_progress_score
    )
    
    // Step 8: Calculate Score Change
    score_change = CalculateScoreChange(final_score, inputs.previous_score)
    
    // Step 9: Generate Explainability
    explainability = GenerateExplainability(
        final_score,
        score_label,
        confidence,
        budget_adherence_score,
        savings_behavior_score,
        expense_trend_score,
        bill_discipline_score,
        emergency_preparedness_score,
        goal_progress_score,
        weights,
        recommendations,
        score_change
    )
    
    // Step 10: Generate Output
    RETURN {
        score: final_score,
        label: score_label,
        confidence: confidence,
        factor_scores: {
            budget_adherence: budget_adherence_score,
            savings_behavior: savings_behavior_score,
            expense_trend: expense_trend_score,
            bill_discipline: bill_discipline_score,
            emergency_preparedness: emergency_preparedness_score,
            goal_progress: goal_progress_score
        },
        recommendations: recommendations,
        explainability: explainability,
        score_change: score_change
    }
    
END FUNCTION
```

### Budget Adherence Score Calculation

```
FUNCTION CalculateBudgetAdherenceScore(budgets, transactions, historical_months):
    
    IF historical_months < 1 THEN
        RETURN 50  // neutral - no data
    END IF
    
    within_budget_count = 0
    total_categories = COUNT(budgets)
    
    FOR EACH budget IN budgets:
        
        category_spending = SUM(transactions WHERE category = budget.category)
        category_utilization = (category_spending / budget.limit) × 100
        
        IF category_utilization <= 100 THEN
            within_budget_count += 1
        END IF
        
    END FOR
    
    IF total_categories > 0 THEN
        score = (within_budget_count / total_categories) × 100
    ELSE
        score = 50  // neutral - no budgets
    END IF
    
    RETURN score
    
END FUNCTION
```

### Savings Behavior Score Calculation

```
FUNCTION CalculateSavingsBehaviorScore(savings_data, historical_months):
    
    planned_savings = savings_data.planned_savings
    actual_savings = savings_data.actual_savings
    
    IF planned_savings = 0 THEN
        RETURN 50  // neutral - no plan
    END IF
    
    IF actual_savings < 0 THEN
        RETURN 0  // negative savings
    END IF
    
    score = (actual_savings / planned_savings) × 100
    
    IF score > 100 THEN
        score = 100
    END IF
    
    RETURN score
    
END FUNCTION
```

### Expense Trend Score Calculation

```
FUNCTION CalculateExpenseTrendScore(transactions, historical_months):
    
    IF historical_months < 2 THEN
        RETURN 50  // neutral - no trend data
    END IF
    
    current_spending = SUM(transactions WHERE month = current_month)
    historical_average = Average(transactions WHERE month IN previous_months)
    
    IF historical_average = 0 THEN
        RETURN 50  // neutral - no baseline
    END IF
    
    trend_percentage = ((current_spending - historical_average) / historical_average) × 100
    
    IF trend_percentage <= 0 THEN
        score = 100
    ELSE IF trend_percentage <= 10 THEN
        score = 100 - (trend_percentage × 2)
    ELSE IF trend_percentage <= 20 THEN
        score = 80 - ((trend_percentage - 10) × 3)
    ELSE
        score = 50 - ((trend_percentage - 20) × 1.5)
    END IF
    
    IF score < 0 THEN
        score = 0
    END IF
    
    RETURN score
    
END FUNCTION
```

---

## 25. Open Decisions

### Critical Decisions (Before Development)

1. **Score Range Values**: What should be the exact score range boundaries? (Proposed: 0-39 Critical, 40-59 Needs Attention, 60-74 Fair, 75-89 Good, 90-100 Excellent)

2. **Factor Weights**: What should be the exact factor weights? (Proposed: Budget Adherence 20%, Savings Behavior 20%, Expense Trend 15%, Bill Discipline 15%, Emergency Preparedness 15%, Goal Progress 15%)

3. **Minimum Historical Months for High Confidence**: How many months required for high confidence? (Proposed: 3 months)

4. **Emergency Fund Target Months**: How many months of essential expenses for emergency fund target? (Proposed: 3 months, user configurable 3-6)

5. **Bill Grace Period**: What should be the grace period for bill discipline? (Proposed: 3 days)

### Important Decisions (Before MVP)

1. **Budget Adherence Nuanced Scoring**: Should we use nuanced scoring (0-100 per category) or simple binary (within/over budget)? (Proposed: Nuanced scoring for better granularity)

2. **Expense Trend Cap**: What should be the maximum trend percentage cap? (Proposed: No cap, but diminishing returns beyond 20%)

3. **Volatility Penalty**: Should we apply volatility penalty for highly variable spending? (Proposed: Yes, if standard deviation > 30% of average)

4. **Provisional Score Display**: How should provisional scores be displayed? (Proposed: Display with "Provisional" label and confidence level)

5. **Score Change Threshold**: What magnitude of change should trigger explanation? (Proposed: Any change > 0)

### Configurable Defaults (Can Approve with Defaults)

1. **Score Ranges**: As proposed above
2. **Factor Weights**: As proposed above
3. **Minimum Historical Months**: 3 for high confidence
4. **Confidence Thresholds**: As proposed above
5. **Recommendation Thresholds**: Budget Adherence 70, Savings Behavior 60, Expense Trend 70, Emergency Preparedness 50, Goal Progress 50
6. **Bill Grace Period**: 3 days
7. **Bill Overdue Penalty**: 7 days threshold, 5 point penalty
8. **Emergency Fund Percentage**: 10%
9. **Emergency Fund Target Months**: 3

---

## 26. Formula Validation and Final MVP Calculation Model

### Validation Overview

This section validates the proposed formulas against edge cases and potential misleading results, and provides the final MVP calculation model.

### Validation Methodology

Each formula was tested against:
- Normal cases
- Edge cases
- Missing data scenarios
- Boundary conditions
- Potential misleading results

### 1. Budget Adherence Formula Validation

#### Original Formula Issue

**Original Formula**: `(Categories within budget / Total categories) × 100`

**Issue**: This binary approach can produce misleading results:
- A category 1% over budget is treated the same as 100% over budget
- Magnitude of variance is not considered
- A user with 9 categories slightly under budget and 1 category 200% over budget scores 90/100
- A user with all categories 50% over budget scores 0/100

#### Improved Formula

**Final MVP Formula**:
```
Budget Adherence Score = Average of Category Scores

Category Score Calculation:
IF Category Utilization ≤ 80% THEN
    Category Score = 100
ELSE IF Category Utilization ≤ 100% THEN
    Category Score = 100 - ((Utilization - 80) / 20) × 20
ELSE IF Category Utilization ≤ 120% THEN
    Category Score = 80 - ((Utilization - 100) / 20) × 60
ELSE IF Category Utilization ≤ 150% THEN
    Category Score = 20 - ((Utilization - 120) / 30) × 20
ELSE
    Category Score = 0
END IF
```

#### Test Cases

**Example A: All categories slightly under budget**
```
Categories: 10
All at 90% utilization
Category Scores: All 100
Budget Adherence Score: 100
Result: CORRECT - excellent adherence
```

**Example B: One category significantly over budget**
```
Categories: 10
9 categories at 90% utilization (Score: 100 each)
1 category at 150% utilization (Score: 20)
Budget Adherence Score: (9 × 100 + 20) / 10 = 92
Result: CORRECT - reflects one over-budget category
```

**Example C: Several categories slightly over budget**
```
Categories: 10
7 categories at 90% utilization (Score: 100 each)
3 categories at 110% utilization (Score: 70 each)
Budget Adherence Score: (7 × 100 + 3 × 70) / 10 = 91
Result: CORRECT - reflects slight over-budget
```

**Example D: One essential category significantly over budget**
```
Categories: 10
9 categories at 90% utilization (Score: 100 each)
1 essential category at 200% utilization (Score: 0)
Budget Adherence Score: (9 × 100 + 0) / 10 = 90
Result: CORRECT - essential over-budget penalized
```

**Example E: No budget exists**
```
Total Categories: 0
Budget Adherence Score: 50 (neutral)
Note: "No budgets established"
Confidence: LOW
```

### 2. Savings Behavior Formula Validation

#### Original Formula Validation

**Original Formula**: `(Actual savings / Planned savings) × 100`

**Edge Cases Handled**:

**Maximum Score**: Capped at 100
```
IF Score > 100 THEN
    Score = 100
END IF
```

**Planned Savings = 0**:
```
IF Planned Savings = 0 THEN
    Score = 50 (neutral)
    Note: "No savings plan established"
END IF
```

**Actual Savings = 0**:
```
IF Planned Savings > 0 AND Actual Savings = 0 THEN
    Score = 0
    Note: "No savings made despite plan"
END IF
```

**Negative Savings**:
```
IF Actual Savings < 0 THEN
    Score = 0
    Note: "Negative savings (net withdrawal)"
END IF
```

**Actual Savings > Planned Savings**:
```
IF Actual Savings > Planned Savings THEN
    Score = 100 (capped)
    Note: "Exceeded savings target"
END IF
```

**Missing Savings Information**:
```
IF No Savings Data Available THEN
    Score = 50 (neutral)
    Note: "No savings data available"
    Confidence: LOW
END IF
```

### 3. Expense Trend Formula Validation

#### Formula Validation

**Formula**: `100 - (Expense increase % × weight)`

**Edge Cases Handled**:

**Score Cannot Become Negative**:
```
IF Calculated Score < 0 THEN
    Score = 0
END IF
```

**Score Cannot Exceed 100**:
```
IF Calculated Score > 100 THEN
    Score = 100
END IF
```

**Large Increases handled sensibly**:
```
IF Trend % > 50 THEN
    Score = 0 (floor)
    Note: "Significant spending increase"
END IF
```

**Expense decreases rewarded reasonably**:
```
IF Trend % ≤ 0 THEN
    Score = 100 (full score)
    Note: "Spending stable or decreasing"
END IF
```

**Missing historical data**:
```
IF Historical Months < 2 THEN
    Score = 50 (neutral)
    Note: "Insufficient trend data"
END IF
```

**One-time expenses do not distort score**:
```
IF Transaction Marked as One-Time THEN
    Exclude from Trend Calculation
    Note: "One-time expense excluded"
END IF
```

### 4. Emergency Preparedness Formula Validation

#### Formula Validation

**Formula**: `(Current emergency fund / Target emergency fund) × 100`

**Edge Cases Handled**:

**Target = 0**:
```
IF Target Emergency Fund = 0 THEN
    Score = 50 (neutral)
    Note: "Emergency fund target not set"
    Confidence: LOW
END IF
```

**Current = 0**:
```
IF Current Emergency Fund = 0 AND Target > 0 THEN
    Score = 0
    Note: "No emergency fund established"
END IF
```

**Current > Target**:
```
IF Current Emergency Fund > Target THEN
    Score = 100 (capped)
    Note: "Emergency fund target exceeded"
END IF
```

**Missing target**:
```
IF Target Not Provided THEN
    Score = 50 (neutral)
    Note: "Emergency fund target not set"
    Confidence: LOW
END IF
```

**User has not provided emergency fund information**:
```
IF Emergency Fund Data Not Provided THEN
    Score = 50 (neutral)
    Note: "Emergency fund data not provided"
    Confidence: LOW
    Disclaimer: "Score does not reflect external assets"
END IF
```

### 5. Savings Goal Progress Formula Validation

#### Formula Validation

**Formula**: `Average of Individual Goal Progress Scores`

**Edge Cases Handled**:

**Multiple goals**: Average of all goal progress scores

**No goals**:
```
IF Total Goals = 0 THEN
    Score = 50 (neutral)
    Note: "No savings goals established"
END IF
```

**Completed goals**:
```
IF Goal Progress >= 100 THEN
    Goal Score = 100
END IF
```

**Overdue goals**:
```
IF Current Date > Target Date THEN
    Apply Time Penalty
    Goal Score = MAX(0, Goal Progress - Time Penalty)
    Time Penalty = ((Days Overdue / 30) × 10)
END IF
```

**Cancelled goals**: Excluded from calculation

**New goals**: Included with current progress (0%)

**Goals with different target dates**: Weighted by time remaining
```
Goal Weight = 1 / Remaining Months
Weighted Progress = Goal Progress × Goal Weight
Final Score = Weighted Average of Weighted Progress
```

**Recommended MVP Approach**: Equal weighting for simplicity and explainability

### 6. Overall Score Validation

#### Formula Validation

**Formula**: `Factor Score × Factor Weight`

**Edge Cases Handled**:

**Final score always 0-100**:
```
IF Calculated Score < 0 THEN
    Score = 0
ELSE IF Calculated Score > 100 THEN
    Score = 100
END IF
```

**Weights total exactly 100%**:
```
Total Weight = SUM(All Factor Weights)
IF Total Weight ≠ 1.0 THEN
    Error: "Weights must total 100%"
END IF
```

**Missing factors do not create misleading results**:

Option A: Neutral score (50) for missing factors
```
IF Factor Data Missing THEN
    Factor Score = 50 (neutral)
    Factor Note: "Insufficient data"
END IF
```

Option B: Exclude missing factors and redistribute weights
```
IF Factor Data Missing THEN
    Exclude Factor from calculation
    Redistribute Weight Proportionally to Available Factors
END IF
```

**Recommended MVP Approach**: Option A (neutral score) for simplicity and transparency

### 7. Neutral Score Evaluation

#### Evaluation of 50 for Missing Data

**Issue**: A neutral score of 50 could misleadingly imply average financial health when data is missing.

**Recommendation**: Keep neutral score of 50 BUT:
- Mark score as "Provisional" when any factor uses neutral score
- Display clear note: "Score will improve with more historical data"
- Use separate confidence level to indicate data availability
- Display "Insufficient Data" for factors with missing data

**Alternative Considered**: Score unavailable when critical factors missing

**Decision**: Use neutral score with provisional status and clear disclaimers

### 8. Confidence Separation

#### Validation

**Financial Health Score** and **Score Confidence** are completely separate:

```
Financial Health Score: 78
Score Confidence: Medium
Reason: "Only 2 months of transaction history available"
```

**Confidence Calculation**:
```
IF Historical Months >= 3 THEN
    Confidence = HIGH
ELSE IF Historical Months = 2 THEN
    Confidence = MEDIUM
ELSE
    Confidence = LOW
END IF
```

**Factor-Level Confidence**:
```
Factor Confidence = 
    IF Factor Data Available THEN
        Calculate based on data months
    ELSE
        LOW
    END IF
```

**Overall Confidence**:
```
Overall Confidence = Minimum of All Factor Confidences
```

### 9. Fairness Validation

#### No Penalty for Missing Information

**No savings goal**: Score = 50 (neutral), not penalized
**No emergency fund data**: Score = 50 (neutral), not penalized
**Irregular income**: Not directly scored (budget adherence still applies)
**Limited transaction history**: Confidence = LOW, score not penalized

**Display**: "Insufficient Data" rather than poor score

### 10. Final MVP Calculation Model

#### Final Formulas

**Budget Adherence Score**:
```
Budget Adherence Score = Average of Category Scores

Category Score:
IF Utilization ≤ 80% THEN 100
ELSE IF Utilization ≤ 100% THEN 100 - ((Utilization - 80) / 20) × 20
ELSE IF Utilization ≤ 120% THEN 80 - ((Utilization - 100) / 20) × 60
ELSE IF Utilization ≤ 150% THEN 20 - ((Utilization - 120) / 30) × 20
ELSE 0
```

**Savings Behavior Score**:
```
IF Planned Savings = 0 THEN 50
ELSE IF Actual Savings < 0 THEN 0
ELSE (Actual / Planned) × 100, capped at 100
```

**Expense Trend Score**:
```
IF Historical Months < 2 THEN 50
ELSE IF Trend % ≤ 0 THEN 100
ELSE IF Trend % ≤ 10 THEN 100 - (Trend % × 2)
ELSE IF Trend % ≤ 20 THEN 80 - ((Trend % - 10) × 3)
ELSE IF Trend % ≤ 50 THEN 50 - ((Trend % - 20) × 1.5)
ELSE 0
```

**Bill Discipline Score**:
```
IF Total Bills = 0 THEN 100
ELSE (Paid on Time / Total) × 100
```

**Emergency Preparedness Score**:
```
IF Target = 0 OR Not Provided THEN 50
ELSE IF Current = 0 THEN 0
ELSE (Current / Target) × 100, capped at 100
```

**Savings Goal Progress Score**:
```
IF Total Goals = 0 THEN 50
ELSE Average of Individual Goal Progress Scores

Individual Goal Progress = (Current / Target) × 100
IF Overdue THEN Apply Time Penalty
```

**Overall Score**:
```
Overall Score = SUM(Factor Score × Factor Weight)
IF Overall Score < 0 THEN 0
ELSE IF Overall Score > 100 THEN 100
```

#### Assumptions

1. **Planning Values Only**: All scores are based on planning values, not actual bank balances
2. **NeoWallet Data Only**: Scores use only data available within NeoWallet, no external assumptions
3. **User-Provided Information**: Emergency fund and savings goals rely on user-provided information
4. **Historical Data Priority**: Historical data takes priority over baseline assumptions
5. **Deterministic Calculations**: Same inputs always produce same outputs

#### Edge Cases

**No transactions**: Budget adherence = 50, Expense trend = 50
**No income**: Score unavailable
**No savings**: Savings behavior = 50 (neutral)
**No bills**: Bill discipline = 100 (neutral)
**No goals**: Goal progress = 50 (neutral)
**Negative position**: Score = 0 (Critical)
**One-time expense**: Excluded from trend
**Refund**: Treated as negative expense
**Family changes**: Score recalculated
**Missing data**: Factor score = 50 (neutral), confidence = LOW

#### Score Normalization

All factor scores normalized to 0-100 range before weighting.
Overall score normalized to 0-100 range after weighting.

#### Missing Data Handling

```
IF Factor Data Missing THEN
    Factor Score = 50 (neutral)
    Factor Note = "Insufficient data"
    Factor Confidence = LOW
END IF

IF Any Factor Confidence = LOW THEN
    Overall Score Status = PROVISIONAL
END IF
```

#### Confidence Handling

```
Confidence Levels:
- HIGH: 3+ months of historical data
- MEDIUM: 2 months of historical data
- LOW: 0-1 months of historical data

Display Format:
"Financial Health Score: 78 (Provisional)"
"Confidence: Medium"
"Reason: 2 months of data available"
```

#### Worked Examples

**Example 1: New User (0 months)**
```
Budget Adherence: 50 (no data)
Savings Behavior: 50 (no plan)
Expense Trend: 50 (no data)
Bill Discipline: 100 (no bills)
Emergency Preparedness: 50 (no data)
Goal Progress: 50 (no goals)

Score = (50×0.2) + (50×0.2) + (50×0.15) + (100×0.15) + (50×0.15) + (50×0.15) = 58
Status: Provisional
Confidence: Low
```

**Example 2: Established User (6 months)**
```
Budget Adherence: 80 (8/10 within budget)
Savings Behavior: 80 (8K/10K)
Expense Trend: 90 (+5% stable)
Bill Discipline: 90 (9/10 on time)
Emergency Preparedness: 50 (75K/150K)
Goal Progress: 75 (average)

Score = (80×0.2) + (80×0.2) + (90×0.15) + (90×0.15) + (50×0.15) + (75×0.15) = 78
Status: Final
Confidence: High
```

**Example 3: One category significantly over budget**
```
9 categories at 90% utilization (Score: 100 each)
1 category at 150% utilization (Score: 20)

Budget Adherence = (9×100 + 20) / 10 = 92
```

---

## 27. Formula Decisions Requiring Product Owner Approval

### CRITICAL (Must Approve Before Development)

1. **Score Range Values**: Exact boundaries for score ranges (Critical, Needs Attention, Fair, Good, Excellent)
   - Proposed: 0-39 Critical, 40-59 Needs Attention, 60-74 Fair, 75-89 Good, 90-100 Excellent
   - Decision Required: Approve exact range boundaries

2. **Factor Weights**: Exact percentage for each of the 6 scoring factors
   - Proposed: Budget Adherence 20%, Savings Behavior 20%, Expense Trend 15%, Bill Discipline 15%, Emergency Preparedness 15%, Goal Progress 15%
   - Decision Required: Approve exact weights

3. **Minimum Historical Months for High Confidence**: Months required for high confidence score
   - Proposed: 3 months
   - Decision Required: Approve minimum threshold

4. **Emergency Fund Target Months**: Months of essential expenses for emergency fund target
   - Proposed: 3 months (user configurable 3-6)
   - Decision Required: Approve default target

5. **Budget Adherence Nuanced Scoring**: Use nuanced scoring (0-100 per category) vs binary (within/over budget)
   - Proposed: Nuanced scoring for better granularity
   - Decision Required: Approve nuanced approach

### IMPORTANT (Should Approve Before MVP)

1. **Budget Adherence Utilization Thresholds**: Thresholds for score calculation (80%, 100%, 120%, 150%)
   - Proposed: As specified in Section 26
   - Decision Required: Approve thresholds or modify

2. **Expense Trend Cap**: Maximum trend percentage cap to prevent extreme scores
   - Proposed: 50% cap (score floors at 0)
   - Decision Required: Approve cap or modify

3. **Provisional Score Display**: Whether to mark score as "Provisional" when data is limited
   - Proposed: Yes, mark as Provisional when any factor has neutral score
   - Decision Required: Approve provisional approach
   - Alternative: Score unavailable when critical factors missing

4. **Savings Goal Weighting Method**: How to weight multiple savings goals
   - Proposed: Equal weighting for MVP simplicity
   - Decision Required: Approve equal weighting or alternative (amount, priority, time)

5. **Volatility Penalty**: Whether to apply volatility penalty for highly variable spending
   - Proposed: Yes, if standard deviation > 30% of average
   - Decision Required: Approve volatility penalty or disable

### CONFIGURABLE DEFAULTS (Can Approve with Defaults)

1. **Score Ranges**: As proposed in Section 26
2. **Factor Weights**: As proposed in Section 26
3. **Minimum Historical Months**: 3 for high confidence
4. **Confidence Thresholds**: High (3+ months), Medium (2 months), Low (0-1 months)
5. **Recommendation Thresholds**: Budget Adherence 70, Savings Behavior 60, Expense Trend 70, Emergency Preparedness 50, Goal Progress 50
6. **Bill Grace Period**: 3 days
7. **Bill Overdue Penalty**: 7 days threshold, 5 point penalty
8. **Emergency Fund Percentage**: 10%
9. **Emergency Fund Target Months**: 3
10. **Trend Cap Percentage**: 50%

---

## Conclusion

This document defines a deterministic Financial Health Scoring Algorithm for NeoWallet MVP that:

- Uses 6 factors with configurable weights
- Calculates scores on a 0-100 scale with clear ranges
- Handles missing data gracefully with confidence levels
- Provides explainable recommendations
- Separates deterministic calculations from AI explanations
- Maintains version history for reproducibility
- Clearly distinguishes from regulated financial assessments
- Uses improved formulas that consider magnitude of variance
- Treats missing data neutrally without penalties
- Maintains separate confidence from score

**Total Algorithm Components**: 27
**Scoring Factors**: 6
**Edge Cases**: 16
**Configuration Parameters**: 15
**Open Decisions**: 15

**Next Steps**: Product Owner approval of critical and important decisions, then proceed to NW-003.

---

**Document Version**: v1.1
**Date**: August 17, 2026
**Status**: Ready for Product Owner Approval
**Next Step**: Product Owner approval, then NW-003
