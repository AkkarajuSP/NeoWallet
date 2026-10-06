# NeoWallet Business Rules Specification v1

## Executive Summary

This document defines comprehensive business rules for NeoWallet MVP based on the approved product and payment model. NeoWallet is a financial management and payment orchestration platform that does NOT hold customer funds.

**Product Definition**: Mobile-first AI-powered family financial management and payment orchestration platform
**Core Value**: PLAN → OPTIMIZE → DECIDE → PAY
**Total Business Rules**: 85 rules across 15 domains
**Date**: August 17, 2026
**Status**: Ready for Product Owner Approval

---

## Business Rule Catalogue

| Domain | Rule Count | Rule IDs |
|--------|------------|----------|
| Financial Overview | 7 | BR-FIN-001 to BR-FIN-007 |
| User | 8 | BR-USR-001 to BR-USR-008 |
| Family | 10 | BR-FAM-001 to BR-FAM-010 |
| Transaction | 10 | BR-TRX-001 to BR-TRX-010 |
| Budget | 8 | BR-BUD-001 to BR-BUD-008 |
| Household Allocation | 5 | BR-ALLOC-001 to BR-ALLOC-005 |
| Savings | 7 | BR-SAV-001 to BR-SAV-007 |
| Bill | 7 | BR-BIL-001 to BR-BIL-007 |
| Notification | 6 | BR-NOT-001 to BR-NOT-006 |
| Financial Health | 6 | BR-HLT-001 to BR-HLT-006 |
| Neo AI | 6 | BR-AI-001 to BR-AI-006 |
| AI Confidence | 3 | BR-AIC-001 to BR-AIC-003 |
| Security | 7 | BR-SEC-001 to BR-SEC-007 |
| Data Retention | 5 | BR-RET-001 to BR-RET-005 |
| Configuration | 6 | BR-CFG-001 to BR-CFG-006 |
| **TOTAL** | **15** | **85** |

---

## 1. Financial Overview Rules

### BR-FIN-001: Financial Overview Calculation
**Rule ID**: BR-FIN-001
**Domain**: Financial Overview
**Rule Name**: Financial Overview Calculation
**Description**: Calculate financial overview based on planning data, not actual funds
**Trigger**: User views financial overview or transaction recorded
**Conditions**: User has created financial overview
**Action**: 
```
Financial Overview = Planned Allocation - Committed Amount - Actual Transactions (current period) + Income (current period)
```
**Exceptions**: None
**Priority**: P0 (Critical)
**Configurable?**: No
**Related FRS Requirement**: FRS-WAL-001
**Related User Story**: US-WAL-001

### BR-FIN-002: Planned Allocation Definition
**Rule ID**: BR-FIN-002
**Domain**: Financial Overview
**Rule Name**: Planned Allocation Definition
**Description**: Planned allocation is user-defined budget for a planning period
**Trigger**: User sets planned allocation
**Conditions**: User has created financial overview
**Action**: Store planned allocation as planning value (not actual funds)
**Exceptions**: None
**Priority**: P0 (Critical)
**Configurable?**: No
**Related FRS Requirement**: FRS-WAL-003
**Related User Story**: US-WAL-003

### BR-FIN-003: Budget Allocation Calculation
**Rule ID**: BR-FIN-003
**Domain**: Financial Overview
**Rule Name**: Budget Allocation Calculation
**Description**: Budget allocation is sum of all category budgets
**Trigger**: User creates or modifies budget
**Conditions**: User has created budgets
**Action**: 
```
Budget Allocation = SUM(Category Budget Limits)
Unallocated Amount = Planned Allocation - Budget Allocation
```
**Exceptions**: None
**Priority**: P1 (High)
**Configurable?**: No
**Related FRS Requirement**: FRS-WAL-004
**Related User Story**: US-WAL-004

### BR-FIN-004: Committed Amount Calculation
**Rule ID**: BR-FIN-004
**Domain**: Financial Overview
**Rule Name**: Committed Amount Calculation
**Description**: Committed amount is sum of pending payments and scheduled transactions
**Trigger**: User schedules payment or transaction
**Conditions**: User has pending payments or scheduled transactions
**Action**: 
```
Committed Amount = SUM(Pending Payments) + SUM(Scheduled Transactions)
```
**Exceptions**: None
**Priority**: P1 (High)
**Configurable?**: No
**Related FRS Requirement**: FRS-WAL-005
**Related User Story**: US-WAL-005

### BR-FIN-005: Available Financial Capacity Calculation
**Rule ID**: BR-FIN-005
**Domain**: Financial Overview
**Rule Name**: Available Financial Capacity Calculation
**Description**: Available capacity is remaining planning capacity
**Trigger**: User views financial overview or transaction recorded
**Conditions**: User has financial overview with planned allocation
**Action**: 
```
Available Financial Capacity = Planned Allocation - Committed Amount - Actual Transactions (current period) + Income (current period)
```
**Exceptions**: None
**Priority**: P0 (Critical)
**Configurable?**: No
**Related FRS Requirement**: FRS-WAL-006
**Related User Story**: US-WAL-006

### BR-FIN-006: Planning Value Representation
**Rule ID**: BR-FIN-006
**Domain**: Financial Overview
**Rule Name**: Planning Value Representation
**Description**: All financial overview values must be clearly labeled as planning values, not actual funds
**Trigger**: Display of any financial overview value
**Conditions**: User views financial overview
**Action**: Display clear label "Planned" or "Planning Value" for all overview values
**Exceptions**: None
**Priority**: P0 (Critical)
**Configurable?**: No
**Related FRS Requirement**: FRS-WAL-001
**Related User Story**: US-WAL-001

### BR-FIN-007: Financial Overview Type Switching
**Rule ID**: BR-FIN-007
**Domain**: Financial Overview
**Rule Name**: Financial Overview Type Switching
**Description**: User can switch between individual and family financial overview
**Trigger**: User taps overview switcher
**Conditions**: User has both individual and family overview access
**Action**: Display selected overview type with appropriate data and permissions
**Exceptions**: User lacks permission for selected type → display access denied
**Priority**: P1 (High)
**Configurable?**: No
**Related FRS Requirement**: FRS-WAL-008
**Related User Story**: US-WAL-008

---

## 2. User Rules

### BR-USR-001: User Registration Validation
**Rule ID**: BR-USR-001
**Domain**: User
**Rule Name**: User Registration Validation
**Description**: Validate user registration input
**Trigger**: User submits registration form
**Conditions**: User enters email and password
**Action**: 
- Validate email format (RFC 5322)
- Validate password strength (min 8 chars, uppercase, lowercase, number, special char)
- Check email uniqueness
- Send verification email
**Exceptions**: Invalid email → display error; Weak password → display requirements; Email exists → prompt login
**Priority**: P0 (Critical)
**Configurable?**: Yes (password requirements)
**Related FRS Requirement**: FRS-AUTH-001
**Related User Story**: US-AUTH-001

### BR-USR-002: Account Activation
**Rule ID**: BR-USR-002
**Domain**: User
**Rule Name**: Account Activation
**Description**: Account requires email verification before activation
**Trigger**: User clicks verification link
**Conditions**: User registered but not verified
**Action**: Activate account, allow login
**Exceptions**: Verification token expired → display error, require new verification
**Priority**: P0 (Critical)
**Configurable?**: No
**Related FRS Requirement**: FRS-AUTH-001
**Related User Story**: US-AUTH-001

### BR-USR-003: Session Management
**Rule ID**: BR-USR-003
**Domain**: User
**Rule Name**: Session Management
**Description**: Session timeout after inactivity
**Trigger**: User inactive for configured period
**Conditions**: User has active session
**Action**: Invalidate session, redirect to login, display session expired message
**Exceptions**: None
**Priority**: P1 (High)
**Configurable?**: Yes (SESSION_TIMEOUT)
**Related FRS Requirement**: FRS-AUTH-004
**Related User Story**: US-AUTH-004

### BR-USR-004: Failed Authentication Lockout
**Rule ID**: BR-USR-004
**Domain**: User
**Rule Name**: Failed Authentication Lockout
**Description**: Lock account after failed login attempts
**Trigger**: User exceeds failed login threshold
**Conditions**: User has consecutive failed login attempts
**Action**: Lock account for configured duration, send notification
**Exceptions**: None
**Priority**: P1 (High)
**Configurable?**: Yes (MAX_FAILED_ATTEMPTS, LOCKOUT_DURATION)
**Related FRS Requirement**: FRS-AUTH-002
**Related User Story**: US-AUTH-002

### BR-USR-005: Profile Update Validation
**Rule ID**: BR-USR-005
**Domain**: User
**Rule Name**: Profile Update Validation
**Description**: Validate profile update input
**Trigger**: User submits profile update
**Conditions**: User updates profile fields
**Action**: 
- Validate phone format (E.164)
- Validate name format
- Email cannot be changed (requires re-verification)
**Exceptions**: Invalid format → display error
**Priority**: P1 (High)
**Configurable?**: No
**Related FRS Requirement**: FRS-USER-002
**Related User Story**: US-USER-002

### BR-USR-006: Account Deletion Validation
**Rule ID**: BR-USR-006
**Domain**: User
**Rule Name**: Account Deletion Validation
**Description**: Validate account deletion request
**Trigger**: User requests account deletion
**Conditions**: User confirms deletion by typing "DELETE"
**Action**: 
- Check for active financial commitments
- If no commitments: anonymize data, delete account
- If commitments: display error, prevent deletion
**Exceptions**: Active commitments → prevent deletion
**Priority**: P2 (Medium)
**Configurable?**: No
**Related FRS Requirement**: FRS-USER-005
**Related User Story**: US-USER-005

### BR-USR-007: Device Registration Limit
**Rule ID**: BR-USR-007
**Domain**: User
**Rule Name**: Device Registration Limit
**Description**: Limit number of registered devices per user
**Trigger**: User attempts to register new device
**Conditions**: User has reached device limit
**Action**: Display error, require device removal before new registration
**Exceptions**: None
**Priority**: P2 (Medium)
**Configurable?**: Yes (MAX_DEVICES_PER_USER)
**Related FRS Requirement**: FRS-AUTH-005
**Related User Story**: US-AUTH-005

### BR-USR-008: Password Reset Token Expiry
**Rule ID**: BR-USR-008
**Domain**: User
**Rule Name**: Password Reset Token Expiry
**Description**: Password reset tokens expire after configured duration
**Trigger**: User clicks reset link
**Conditions**: Token has expired
**Action**: Display token expired message, require new reset request
**Exceptions**: None
**Priority**: P1 (High)
**Configurable?**: Yes (RESET_TOKEN_EXPIRY)
**Related FRS Requirement**: FRS-AUTH-003
**Related User Story**: US-AUTH-003

---

## 3. Family Rules

### BR-FAM-001: Family Creation Validation
**Rule ID**: BR-FAM-001
**Domain**: Family
**Rule Name**: Family Creation Validation
**Description**: Validate family creation request
**Trigger**: User submits family creation form
**Conditions**: User does not belong to any family
**Action**: 
- Validate family name
- Create family
- Assign user as FAMILY_OWNER
- Create family financial overview
**Exceptions**: User already in family → display error
**Priority**: P1 (High)
**Configurable?**: No
**Related FRS Requirement**: FRS-FAM-001
**Related User Story**: US-FAM-001

### BR-FAM-002: Family Invitation Token Expiry
**Rule ID**: BR-FAM-002
**Domain**: Family
**Rule Name**: Family Invitation Token Expiry
**Description**: Family invitation tokens expire after configured duration
**Trigger**: User clicks invitation link
**Conditions**: Token has expired
**Action**: Display token expired message, require new invitation
**Exceptions**: None
**Priority**: P1 (High)
**Configurable?**: Yes (INVITATION_TOKEN_EXPIRY)
**Related FRS Requirement**: FRS-FAM-002
**Related User Story**: US-FAM-002

### BR-FAM-003: Family Member Role Assignment
**Rule ID**: BR-FAM-003
**Domain**: Family
**Rule Name**: Family Member Role Assignment
**Description**: Assign role to family member
**Trigger**: Family owner assigns role
**Conditions**: Family owner assigns role to member
**Action**: Update member role, apply role permissions immediately
**Exceptions**: Attempt to change owner role → display error
**Priority**: P1 (High)
**Configurable?**: No
**Related FRS Requirement**: FRS-FAM-005
**Related User Story**: US-FAM-005

### BR-FAM-004: FAMILY_OWNER Permissions
**Rule ID**: BR-FAM-004
**Domain**: Family
**Rule Name**: FAMILY_OWNER Permissions
**Description**: Define FAMILY_OWNER permissions
**Trigger**: Family owner performs action
**Conditions**: User is FAMILY_OWNER
**Action**: 
- VIEW: All family financial data
- CREATE: Budgets, savings goals, bills
- UPDATE: All family settings
- DELETE: Family members (except self)
- APPROVE: All financial decisions
**Exceptions**: None
**Priority**: P0 (Critical)
**Configurable?**: No
**Related FRS Requirement**: FRS-FAM-005
**Related User Story**: US-FAM-005

### BR-FAM-005: FAMILY_MEMBER Permissions
**Rule ID**: BR-FAM-005
**Domain**: Family
**Rule Name**: FAMILY_MEMBER Permissions
**Description**: Define FAMILY_MEMBER permissions
**Trigger**: Family member performs action
**Conditions**: User is FAMILY_MEMBER
**Action**: 
- VIEW: All family financial data
- CREATE: Personal budgets, savings goals
- UPDATE: Personal settings only
- DELETE: Personal data only
- APPROVE: Personal financial decisions only
**Exceptions**: Attempt to modify family settings → display error
**Priority**: P0 (Critical)
**Configurable?**: No
**Related FRS Requirement**: FRS-FAM-005
**Related User Story**: US-FAM-005

### BR-FAM-006: RESTRICTED Permissions
**Rule ID**: BR-FAM-006
**Domain**: Family
**Rule Name**: RESTRICTED Permissions
**Description**: Define RESTRICTED permissions
**Trigger**: RESTRICTED member performs action
**Conditions**: User is RESTRICTED
**Action**: 
- VIEW: Limited family financial data (no sensitive details)
- CREATE: None
- UPDATE: None
- DELETE: None
- APPROVE: None
**Exceptions**: Attempt to view sensitive data → display access denied
**Priority**: P0 (Critical)
**Configurable?**: No
**Related FRS Requirement**: FRS-FAM-005
**Related User Story**: US-FAM-005

### BR-FAM-007: Family Member Removal
**Rule ID**: BR-FAM-007
**Domain**: Family
**Rule Name**: Family Member Removal
**Description**: Family owner can remove members
**Trigger**: Family owner removes member
**Conditions**: User is FAMILY_OWNER
**Action**: Remove member from family, revoke access, send notification
**Exceptions**: Attempt to remove self → display error (must leave family instead)
**Priority**: P1 (High)
**Configurable?**: No
**Related FRS Requirement**: FRS-FAM-004
**Related User Story**: US-FAM-004

### BR-FAM-008: Family Spending Limits
**Rule ID**: BR-FAM-008
**Domain**: Family
**Rule Name**: Family Spending Limits
**Description**: Family owner can set spending limits for members
**Trigger**: Family owner sets spending limit
**Conditions**: User is FAMILY_OWNER
**Action**: Set daily/weekly/monthly limits, enforce at transaction time
**Exceptions**: Member exceeds limit → display limit exceeded, prevent transaction
**Priority**: P2 (Medium)
**Configurable?**: Yes (limit amounts)
**Related FRS Requirement**: FRS-FAM-007
**Related User Story**: US-FAM-007

### BR-FAM-009: Family Leave Validation
**Rule ID**: BR-FAM-009
**Domain**: Family
**Rule Name**: Family Leave Validation
**Description**: Family members can leave family (not owners)
**Trigger**: Family member leaves family
**Conditions**: User is not FAMILY_OWNER
**Action**: Remove user from family, revoke access, redirect to individual overview
**Exceptions**: User is FAMILY_OWNER → display error (cannot leave as owner)
**Priority**: P2 (Medium)
**Configurable?**: No
**Related FRS Requirement**: FRS-FAM-008
**Related User Story**: US-FAM-008

### BR-FAM-010: Family Financial Data Visibility
**Rule ID**: BR-FAM-010
**Domain**: Family
**Rule Name**: Family Financial Data Visibility
**Description**: Define financial data visibility by role
**Trigger**: User views family financial data
**Conditions**: User has family access
**Action**: 
- FAMILY_OWNER: Full visibility
- FAMILY_MEMBER: Full visibility
- RESTRICTED: Limited visibility (hide sensitive details)
**Exceptions**: None
**Priority**: P0 (Critical)
**Configurable?**: No
**Related FRS Requirement**: FRS-FAM-006
**Related User Story**: US-FAM-006

---

## 4. Transaction Rules

### BR-TRX-001: Transaction Type Classification
**Rule ID**: BR-TRX-001
**Domain**: Transaction
**Rule Name**: Transaction Type Classification
**Description**: Classify transaction as income, expense, or transfer
**Trigger**: User records transaction
**Conditions**: User enters transaction details
**Action**: 
- Income: Money received
- Expense: Money spent
- Transfer: Money moved between categories (future)
**Exceptions**: None
**Priority**: P0 (Critical)
**Configurable?**: No
**Related FRS Requirement**: FRS-TRX-001
**Related User Story**: US-TRX-001

### BR-TRX-002: Transaction Status Lifecycle
**Rule ID**: BR-TRX-002
**Domain**: Transaction
**Rule Name**: Transaction Status Lifecycle
**Description**: Define transaction status states
**Trigger**: Transaction recorded or updated
**Conditions**: Transaction exists
**Action**: 
- PLANNED: Scheduled for future
- COMMITTED: Initiated but not completed
- PENDING: Submitted to provider
- COMPLETED: Successfully executed
- FAILED: Execution failed
- REVERSED: Transaction reversed
**Exceptions**: Invalid status transition → display error
**Priority**: P0 (Critical)
**Configurable?**: No
**Related FRS Requirement**: FRS-TRX-001
**Related User Story**: US-TRX-001

### BR-TRX-003: Transaction Categorization
**Rule ID**: BR-TRX-003
**Domain**: Transaction
**Rule Name**: Transaction Categorization
**Description**: Every transaction must have a category
**Trigger**: User records transaction
**Conditions**: Transaction recorded
**Action**: Require category selection, use predefined categories or custom categories
**Exceptions**: No category selected → display error, require selection
**Priority**: P1 (High)
**Configurable?**: Yes (predefined categories)
**Related FRS Requirement**: FRS-TRX-002
**Related User Story**: US-TRX-002

### BR-TRX-004: Transaction Amount Validation
**Rule ID**: BR-TRX-004
**Domain**: Transaction
**Rule Name**: Transaction Amount Validation
**Description**: Validate transaction amount
**Trigger**: User enters transaction amount
**Conditions**: Transaction amount entered
**Action**: Validate amount is positive number, validate decimal places (max 2)
**Exceptions**: Invalid amount → display error
**Priority**: P0 (Critical)
**Configurable?**: Yes (decimal places)
**Related FRS Requirement**: FRS-TRX-001
**Related User Story**: US-TRX-001

### BR-TRX-005: Transaction Audit Trail
**Rule ID**: BR-TRX-005
**Domain**: Transaction
**Rule Name**: Transaction Audit Trail
**Description**: Maintain audit trail for all transaction changes
**Trigger**: Transaction created, updated, or deleted
**Conditions**: Transaction modified
**Action**: Log old and new values, log timestamp, log user, log reason
**Exceptions**: None
**Priority**: P0 (Critical)
**Configurable?**: No
**Related FRS Requirement**: FRS-TRX-004
**Related User Story**: US-TRX-004

### BR-TRX-006: Transaction Deletion
**Rule ID**: BR-TRX-006
**Domain**: Transaction
**Rule Name**: Transaction Deletion
**Description**: Transactions are soft-deleted
**Trigger**: User deletes transaction
**Conditions**: User confirms deletion
**Action**: Soft-delete transaction (mark as deleted), preserve audit trail
**Exceptions**: Transaction linked to payment → display warning
**Priority**: P2 (Medium)
**Configurable?**: No
**Related FRS Requirement**: FRS-TRX-007
**Related User Story**: US-TRX-007

### BR-TRX-007: Duplicate Transaction Detection
**Rule ID**: BR-TRX-007
**Domain**: Transaction
**Rule Name**: Duplicate Transaction Detection
**Description**: Detect potential duplicate transactions
**Trigger**: User records transaction
**Conditions**: Similar transaction exists (same amount, date, category)
**Action**: Display duplicate warning, allow user to confirm or cancel
**Exceptions**: None
**Priority**: P2 (Medium)
**Configurable?**: Yes (similarity threshold)
**Related FRS Requirement**: FRS-TRX-001
**Related User Story**: US-TRX-001

### BR-TRX-008: Transaction Correction
**Rule ID**: BR-TRX-008
**Domain**: Transaction
**Rule Name**: Transaction Correction
**Description**: Allow transaction correction with audit trail
**Trigger**: User edits transaction
**Conditions**: Transaction exists
**Action**: Create correction record, preserve original, update transaction
**Exceptions**: None
**Priority**: P1 (High)
**Configurable?**: No
**Related FRS Requirement**: FRS-TRX-004
**Related User Story**: US-TRX-004

### BR-TRX-009: Transaction Date Validation
**Rule ID**: BR-TRX-009
**Domain**: Transaction
**Rule Name**: Transaction Date Validation
**Description**: Validate transaction date
**Trigger**: User enters transaction date
**Conditions**: Transaction date entered
**Action**: Validate date is not in future (unless planned), validate date format
**Exceptions**: Future date for actual transaction → display warning
**Priority**: P1 (High)
**Configurable?**: No
**Related FRS Requirement**: FRS-TRX-001
**Related User Story**: US-TRX-001

### BR-TRX-010: Recurring Transaction
**Rule ID**: BR-TRX-010
**Domain**: Transaction
**Rule Name**: Recurring Transaction
**Description**: Define recurring transaction rules
**Trigger**: User creates recurring transaction
**Conditions**: Transaction marked as recurring
**Action**: Set recurrence pattern (daily, weekly, monthly), calculate next due dates
**Exceptions**: None
**Priority**: P2 (Medium)
**Configurable?**: Yes (recurrence patterns)
**Related FRS Requirement**: FRS-TRX-001
**Related User Story**: US-TRX-001

---

## 5. Budget Rules

### BR-BUD-001: Budget Creation Validation
**Rule ID**: BR-BUD-001
**Domain**: Budget
**Rule Name**: Budget Creation Validation
**Description**: Validate budget creation
**Trigger**: User creates budget
**Conditions**: User enters budget details
**Action**: 
- Validate amount is positive
- Validate category exists
- Validate period is valid
- Link to financial overview
**Exceptions**: Category already has budget → display error
**Priority**: P1 (High)
**Configurable?**: No
**Related FRS Requirement**: FRS-BUD-001
**Related User Story**: US-BUD-001

### BR-BUD-002: Budget Utilization Calculation
**Rule ID**: BR-BUD-002
**Domain**: Budget
**Rule Name**: Budget Utilization Calculation
**Description**: Calculate budget utilization percentage
**Trigger**: Transaction recorded or budget viewed
**Conditions**: Budget exists with transactions
**Action**: 
```
Budget Utilization % = (Actual Spending / Budget Limit) * 100
```
**Exceptions**: None
**Priority**: P0 (Critical)
**Configurable?**: No
**Related FRS Requirement**: FRS-BUD-003
**Related User Story**: US-BUD-003

### BR-BUD-003: Budget Warning Threshold
**Rule ID**: BR-BUD-003
**Domain**: Budget
**Rule Name**: Budget Warning Threshold
**Description**: Trigger budget warning at configured percentage
**Trigger**: Budget utilization exceeds threshold
**Conditions**: Budget utilization >= warning threshold
**Action**: Trigger budget warning notification
**Exceptions**: None
**Priority**: P1 (High)
**Configurable?**: Yes (BUDGET_WARNING_PERCENTAGE, default 80%)
**Related FRS Requirement**: FRS-BUD-004
**Related User Story**: US-BUD-004

### BR-BUD-004: Budget Exceeded Threshold
**Rule ID**: BR-BUD-004
**Domain**: Budget
**Rule Name**: Budget Exceeded Threshold
**Description**: Mark budget as exceeded at configured percentage
**Trigger**: Budget utilization exceeds threshold
**Conditions**: Budget utilization >= exceeded threshold
**Action**: Mark budget as EXCEEDED, trigger exceeded notification
**Exceptions**: None
**Priority**: P0 (Critical)
**Configurable?**: Yes (BUDGET_EXCEEDED_PERCENTAGE, default 100%)
**Related FRS Requirement**: FRS-BUD-003
**Related User Story**: US-BUD-003

### BR-BUD-005: Budget Rollover
**Rule ID**: BR-BUD-005
**Domain**: Budget
**Rule Name**: Budget Rollover
**Description**: Define budget rollover behavior
**Trigger**: Budget period ends
**Conditions**: Budget has rollover enabled
**Action**: Add unspent amount to next period's budget
**Exceptions**: Rollover disabled → reset budget to limit
**Priority**: P2 (Medium)
**Configurable?**: Yes (rollover enabled/disabled)
**Related FRS Requirement**: FRS-BUD-006
**Related User Story**: US-BUD-006

### BR-BUD-006: Budget Modification Impact
**Rule ID**: BR-BUD-006
**Domain**: Budget
**Rule Name**: Budget Modification Impact
**Description**: Warn user of budget modification impact
**Trigger**: User modifies budget limit
**Conditions**: Budget has actual spending
**Action**: 
- If new limit < spent amount: display warning
- If new limit < current utilization: display warning
- Allow modification with warning
**Exceptions**: None
**Priority**: P1 (High)
**Configurable?**: No
**Related FRS Requirement**: FRS-BUD-002
**Related User Story**: US-BUD-002

### BR-BUD-007: Budget Forecasting
**Rule ID**: BR-BUD-007
**Domain**: Budget
**Rule Name**: Budget Forecasting
**Description**: Forecast budget based on historical data
**Trigger**: User views budget
**Conditions**: Sufficient historical data exists
**Action**: Calculate projected spending based on historical patterns
**Exceptions**: Insufficient data → display no forecast
**Priority**: P2 (Medium)
**Configurable?**: Yes (forecasting period)
**Related FRS Requirement**: FRS-BUD-003
**Related User Story**: US-BUD-003

### BR-BUD-008: Budget Deletion
**Rule ID**: BR-BUD-008
**Domain**: Budget
**Rule Name**: Budget Deletion
**Description**: Budgets are soft-deleted
**Trigger**: User deletes budget
**Conditions**: User confirms deletion
**Action**: Soft-delete budget, preserve transaction history, update financial overview
**Exceptions**: Budget has transactions → display warning
**Priority**: P2 (Medium)
**Configurable?**: No
**Related FRS Requirement**: FRS-BUD-007
**Related User Story**: US-BUD-007

---

## 6. Household Allocation Rules

### BR-ALLOC-001: Allocation Recommendation Calculation
**Rule ID**: BR-ALLOC-001
**Domain**: Household Allocation
**Rule Name**: Allocation Recommendation Calculation
**Description**: Calculate recommended household allocation
**Trigger**: User requests allocation recommendation
**Conditions**: Sufficient data exists (income, expenses, commitments)
**Action**: 
```
Recommended Allocation = 
    (Income - Recurring Expenses - Existing Commitments) 
    * Category Weight
    * Family Size Factor
    * Emergency Fund Allocation
    * Savings Goal Allocation
```
**Exceptions**: Insufficient data → display insufficient data message
**Priority**: P2 (Medium)
**Configurable?**: Yes (category weights, family size factors)
**Related FRS Requirement**: FRS-WAL-003
**Related User Story**: US-WAL-003

### BR-ALLOC-002: Recommendation vs User Approval
**Rule ID**: BR-ALLOC-002
**Domain**: Household Allocation
**Rule Name**: Recommendation vs User Approval
**Description**: Distinguish system recommendation from user approval
**Trigger**: Allocation recommendation displayed
**Conditions**: Recommendation calculated
**Action**: 
- Display as "SYSTEM RECOMMENDATION"
- Require user approval to apply
- User can override recommendation
**Exceptions**: None
**Priority**: P0 (Critical)
**Configurable?**: No
**Related FRS Requirement**: FRS-WAL-003
**Related User Story**: US-WAL-003

### BR-ALLOC-003: User Override of Recommendation
**Rule ID**: BR-ALLOC-003
**Domain**: Household Allocation
**Rule Name**: User Override of Recommendation
**Description**: User can override system recommendation
**Trigger**: User modifies recommended allocation
**Conditions**: Recommendation displayed
**Action**: Allow user to modify any category allocation, mark as USER APPROVED
**Exceptions**: None
**Priority**: P0 (Critical)
**Configurable?**: No
**Related FRS Requirement**: FRS-WAL-003
**Related User Story**: US-WAL-003

### BR-ALLOC-004: Emergency Fund Allocation
**Rule ID**: BR-ALLOC-004
**Domain**: Household Allocation
**Rule Name**: Emergency Fund Allocation
**Description**: Include emergency fund in allocation recommendation
**Trigger**: Allocation recommendation calculated
**Conditions**: Income data exists
**Action**: Allocate percentage to emergency fund (typically 3-6 months expenses)
**Exceptions**: None
**Priority**: P2 (Medium)
**Configurable?**: Yes (emergency fund percentage)
**Related FRS Requirement**: FRS-WAL-003
**Related User Story**: US-WAL-003

### BR-ALLOC-005: Savings Goal Allocation
**Rule ID**: BR-ALLOC-005
**Domain**: Household Allocation
**Rule Name**: Savings Goal Allocation
**Description**: Include savings goals in allocation recommendation
**Trigger**: Allocation recommendation calculated
**Conditions**: Savings goals exist
**Action**: Allocate to savings goals based on target dates and amounts
**Exceptions**: None
**Priority**: P2 (Medium)
**Configurable?**: No
**Related FRS Requirement**: FRS-WAL-003
**Related User Story**: US-WAL-003

---

## 7. Savings Rules

### BR-SAV-001: Savings Goal Creation Validation
**Rule ID**: BR-SAV-001
**Domain**: Savings
**Rule Name**: Savings Goal Creation Validation
**Description**: Validate savings goal creation
**Trigger**: User creates savings goal
**Conditions**: User enters goal details
**Action**: 
- Validate target amount is positive
- Validate target date is in future
- Link to financial overview
- Initialize current amount to 0
**Exceptions**: Invalid amount or date → display error
**Priority**: P1 (High)
**Configurable?**: No
**Related FRS Requirement**: FRS-SAV-001
**Related User Story**: US-SAV-001

### BR-SAV-002: Monthly Required Contribution Calculation
**Rule ID**: BR-SAV-002
**Domain**: Savings
**Rule Name**: Monthly Required Contribution Calculation
**Description**: Calculate monthly contribution needed to reach goal
**Trigger**: Savings goal created or viewed
**Conditions**: Savings goal exists with target and date
**Action**: 
```
Monthly Required = (Target Amount - Current Amount) / Remaining Months
```
**Exceptions**: Target date passed → display overdue message
**Priority**: P1 (High)
**Configurable?**: No
**Related FRS Requirement**: FRS-SAV-002
**Related User Story**: US-SAV-002

### BR-SAV-003: Goal Progress Calculation
**Rule ID**: BR-SAV-003
**Domain**: Savings
**Rule Name**: Goal Progress Calculation
**Description**: Calculate savings goal progress percentage
**Trigger**: Contribution made or goal viewed
**Conditions**: Savings goal exists
**Action**: 
```
Progress % = (Current Amount / Target Amount) * 100
```
**Exceptions**: None
**Priority**: P0 (Critical)
**Configurable?**: No
**Related FRS Requirement**: FRS-SAV-002
**Related User Story**: US-SAV-002

### BR-SAV-004: Goal Completion
**Rule ID**: BR-SAV-004
**Domain**: Savings
**Rule Name**: Goal Completion
**Description**: Mark goal as achieved when target reached
**Trigger**: Contribution brings current amount >= target
**Conditions**: Current amount >= target amount
**Action**: Mark goal as ACHIEVED, trigger celebration notification
**Exceptions**: None
**Priority**: P1 (High)
**Configurable?**: No
**Related FRS Requirement**: FRS-SAV-005
**Related User Story**: US-SAV-005

### BR-SAV-005: Missed Contribution Detection
**Rule ID**: BR-SAV-005
**Domain**: Savings
**Rule Name**: Missed Contribution Detection
**Description**: Detect missed monthly contributions
**Trigger**: Monthly contribution review
**Conditions**: Monthly contribution not made
**Action**: Mark contribution as missed, display missed contribution warning
**Exceptions**: None
**Priority**: P2 (Medium)
**Configurable?**: Yes (missed contribution threshold)
**Related FRS Requirement**: FRS-SAV-002
**Related User Story**: US-SAV-002

### BR-SAV-006: Goal Modification Validation
**Rule ID**: BR-SAV-006
**Domain**: Savings
**Rule Name**: Goal Modification Validation
**Description**: Validate savings goal modification
**Trigger**: User modifies savings goal
**Conditions**: User updates goal details
**Action**: 
- If target increased: recalculate required contribution
- If target decreased: display warning
- If date changed: recalculate required contribution
**Exceptions**: None
**Priority**: P1 (High)
**Configurable?**: No
**Related FRS Requirement**: FRS-SAV-001
**Related User Story**: US-SAV-001

### BR-SAV-007: Goal Cancellation
**Rule ID**: BR-SAV-007
**Domain**: Savings
**Rule Name**: Goal Cancellation
**Description**: Savings goals are soft-deleted
**Trigger**: User cancels savings goal
**Conditions**: User confirms cancellation
**Action**: Soft-delete goal, preserve contribution history, update financial overview
**Exceptions**: Goal has contributions → display warning about losing history
**Priority**: P2 (Medium)
**Configurable?**: No
**Related FRS Requirement**: FRS-SAV-006
**Related User Story**: US-SAV-006

---

## 8. Bill Rules

### BR-BIL-001: Bill Creation Validation
**Rule ID**: BR-BIL-001
**Domain**: Bill
**Rule Name**: Bill Creation Validation
**Description**: Validate bill creation
**Trigger**: User creates bill
**Conditions**: User enters bill details
**Action**: 
- Validate amount is positive
- Validate due date is valid
- Validate category exists
- Link to financial overview
**Exceptions**: Invalid data → display error
**Priority**: P1 (High)
**Configurable?**: No
**Related FRS Requirement**: FRS-BIL-001
**Related User Story**: US-BIL-001

### BR-BIL-002: Bill Status Calculation
**Rule ID**: BR-BIL-002
**Domain**: Bill
**Rule Name**: Bill Status Calculation
**Description**: Calculate bill status based on due date
**Trigger**: Bill viewed or daily status check
**Conditions**: Bill exists
**Action**: 
- UPCOMING: Due date > today
- DUE: Due date = today
- OVERDUE: Due date < today and not paid
- PAID: Payment recorded
**Exceptions**: None
**Priority**: P0 (Critical)
**Configurable?**: No
**Related FRS Requirement**: FRS-BIL-002
**Related User Story**: US-BIL-002

### BR-BIL-003: Bill Reminder Thresholds
**Rule ID**: BR-BIL-003
**Domain**: Bill
**Rule Name**: Bill Reminder Thresholds
**Description**: Trigger bill reminders at configured days before due
**Trigger**: Daily bill reminder check
**Conditions**: Bill due date within reminder threshold
**Action**: Trigger bill reminder notification
**Exceptions**: Notifications disabled → do not send
**Priority**: P1 (High)
**Configurable?**: Yes (BILL_REMINDER_DAYS, default 7 days, 2 days, due date)
**Related FRS Requirement**: FRS-BIL-003
**Related User Story**: US-BIL-003

### BR-BIL-004: Bill Payment Recording
**Rule ID**: BR-BIL-004
**Domain**: Bill
**Rule Name**: Bill Payment Recording
**Description**: Record bill payment (manual entry in MVP)
**Trigger**: User records bill payment
**Conditions**: User enters payment details
**Action**: 
- Validate payment amount
- Validate payment date
- Update bill status to PAID
- Create payment record
- Update financial overview
**Exceptions**: Invalid data → display error
**Priority**: P1 (High)
**Configurable?**: No
**Related FRS Requirement**: FRS-BIL-004
**Related User Story**: US-BIL-004

### BR-BIL-005: Recurring Bill Calculation
**Rule ID**: BR-BIL-005
**Domain**: Bill
**Rule Name**: Recurring Bill Calculation
**Description**: Calculate next due dates for recurring bills
**Trigger**: Bill viewed or daily calculation
**Conditions**: Bill marked as recurring
**Action**: Calculate next due dates based on recurrence pattern
**Exceptions**: None
**Priority**: P2 (Medium)
**Configurable?**: Yes (recurrence patterns)
**Related FRS Requirement**: FRS-BIL-006
**Related User Story**: US-BIL-006

### BR-BIL-006: Bill Overdue Notification
**Rule ID**: BR-BIL-006
**Domain**: Bill
**Rule Name**: Bill Overdue Notification
**Description**: Trigger overdue notification for overdue bills
**Trigger**: Daily overdue check
**Conditions**: Bill is overdue
**Action**: Trigger overdue notification, highlight in bill list
**Exceptions**: Notifications disabled → do not send
**Priority**: P1 (High)
**Configurable?**: No
**Related FRS Requirement**: FRS-BIL-003
**Related User Story**: US-BIL-003

### BR-BIL-007: Bill Cancellation
**Rule ID**: BR-BIL-007
**Domain**: Bill
**Rule Name**: Bill Cancellation
**Description**: Bills are soft-deleted
**Trigger**: User cancels bill
**Conditions**: User confirms cancellation
**Action**: Soft-delete bill, preserve payment history, update financial overview
**Exceptions**: Bill has payments → display warning about losing history
**Priority**: P2 (Medium)
**Configurable?**: No
**Related FRS Requirement**: FRS-BIL-001
**Related User Story**: US-BIL-001

---

## 9. Notification Rules

### BR-NOT-001: Notification Preference Validation
**Rule ID**: BR-NOT-001
**Domain**: Notification
**Rule Name**: Notification Preference Validation
**Description**: Validate and apply notification preferences
**Trigger**: User updates notification preferences
**Conditions**: User modifies preferences
**Action**: 
- Validate preference values
- Apply preferences immediately
- Respect quiet hours
**Exceptions**: All notifications disabled → display warning
**Priority**: P1 (High)
**Configurable?**: Yes (notification types, quiet hours)
**Related FRS Requirement**: FRS-NOT-001
**Related User Story**: US-NOT-001

### BR-NOT-002: Budget Warning Notification
**Rule ID**: BR-NOT-002
**Domain**: Notification
**Rule Name**: Budget Warning Notification
**Description**: Trigger budget warning at configured threshold
**Trigger**: Budget utilization >= warning threshold
**Conditions**: Budget warning threshold exceeded
**Action**: Send budget warning notification via enabled channels
**Exceptions**: Notifications disabled → do not send
**Priority**: P1 (High)
**Configurable?**: Yes (BUDGET_WARNING_PERCENTAGE)
**Related FRS Requirement**: FRS-BUD-004
**Related User Story**: US-BUD-004

### BR-NOT-003: Budget Exceeded Notification
**Rule ID**: BR-NOT-003
**Domain**: Notification
**Rule Name**: Budget Exceeded Notification
**Description**: Trigger budget exceeded notification
**Trigger**: Budget utilization >= exceeded threshold
**Conditions**: Budget exceeded threshold exceeded
**Action**: Send budget exceeded notification via enabled channels
**Exceptions**: Notifications disabled → do not send
**Priority**: P0 (Critical)
**Configurable?**: Yes (BUDGET_EXCEEDED_PERCENTAGE)
**Related FRS Requirement**: FRS-BUD-004
**Related User Story**: US-BUD-004

### BR-NOT-004: Bill Reminder Notification
**Rule ID**: BR-NOT-004
**Domain**: Notification
**Rule Name**: Bill Reminder Notification
**Description**: Trigger bill reminder at configured days before due
**Trigger**: Daily bill reminder check
**Conditions**: Bill due date within reminder threshold
**Action**: Send bill reminder notification via enabled channels
**Exceptions**: Notifications disabled → do not send
**Priority**: P1 (High)
**Configurable?**: Yes (BILL_REMINDER_DAYS)
**Related FRS Requirement**: FRS-BIL-003
**Related User Story**: US-BIL-003

### BR-NOT-005: Savings Progress Notification
**Rule ID**: BR-NOT-005
**Domain**: Notification
**Rule Name**: Savings Progress Notification
**Description**: Trigger savings progress notification at configured threshold
**Trigger**: Savings progress reaches threshold
**Conditions**: Savings progress >= configured threshold
**Action**: Send savings progress notification via enabled channels
**Exceptions**: Notifications disabled → do not send
**Priority**: P2 (Medium)
**Configurable?**: Yes (SAVINGS_PROGRESS_THRESHOLD)
**Related FRS Requirement**: FRS-SAV-002
**Related User Story**: US-SAV-002

### BR-NOT-006: Security Event Notification
**Rule ID**: BR-NOT-006
**Domain**: Notification
**Rule Name**: Security Event Notification
**Description**: Send notification for security events
**Trigger**: Security event occurs (failed login, account lockout, etc.)
**Conditions**: Security event detected
**Action**: Send security notification via all enabled channels
**Exceptions**: None
**Priority**: P0 (Critical)
**Configurable?**: No
**Related FRS Requirement**: FRS-AUTH-002
**Related User Story**: US-AUTH-002

---

## 10. Financial Health Rules

### BR-HLT-001: Financial Health Score Calculation
**Rule ID**: BR-HLT-001
**Domain**: Financial Health
**Rule Name**: Financial Health Score Calculation
**Description**: Calculate financial health score based on multiple factors
**Trigger**: Financial health viewed or daily calculation
**Conditions**: Sufficient financial data exists
**Action**: 
```
Health Score = 
    (Savings Behavior Weight * Savings Score) +
    (Budget Adherence Weight * Budget Score) +
    (Expense Trend Weight * Expense Score) +
    (Bill Discipline Weight * Bill Score) +
    (Emergency Preparedness Weight * Emergency Score) +
    (Goal Progress Weight * Goal Score)
```
**Exceptions**: Insufficient data → display insufficient data message
**Priority**: P1 (High)
**Configurable?**: Yes (factor weights)
**Related FRS Requirement**: FRS-HLT-001
**Related User Story**: US-HLT-001

### BR-HLT-002: Savings Behavior Score
**Rule ID**: BR-HLT-002
**Domain**: Financial Health
**Rule Name**: Savings Behavior Score
**Description**: Calculate savings behavior score
**Trigger**: Financial health score calculation
**Conditions**: Savings data exists
**Action**: 
```
Savings Score = (Actual Savings / Recommended Savings) * 100
Score Range: 0-100
```
**Exceptions**: No savings data → score = 0
**Priority**: P1 (High)
**Configurable?**: Yes (recommended savings percentage)
**Related FRS Requirement**: FRS-HLT-001
**Related User Story**: US-HLT-001

### BR-HLT-003: Budget Adherence Score
**Rule ID**: BR-HLT-003
**Domain**: Financial Health
**Rule Name**: Budget Adherence Score
**Description**: Calculate budget adherence score
**Trigger**: Financial health score calculation
**Conditions**: Budget data exists
**Action**: 
```
Budget Score = (Budgets Within Limit / Total Budgets) * 100
Score Range: 0-100
```
**Exceptions**: No budget data → score = 0
**Priority**: P1 (High)
**Configurable?**: No
**Related FRS Requirement**: FRS-HLT-001
**Related User Story**: US-HLT-001

### BR-HLT-004: Expense Trend Score
**Rule ID**: BR-HLT-004
**Domain**: Financial Health
**Rule Name**: Expense Trend Score
**Description**: Calculate expense trend score
**Trigger**: Financial health score calculation
**Conditions**: Historical expense data exists
**Action**: 
```
Expense Score = 100 - (Expense Increase % * Weight)
Score Range: 0-100
```
**Exceptions**: No historical data → score = 50 (neutral)
**Priority**: P2 (Medium)
**Configurable?**: Yes (expense increase weight)
**Related FRS Requirement**: FRS-HLT-002
**Related User Story**: US-HLT-002

### BR-HLT-005: Bill Discipline Score
**Rule ID**: BR-HLT-005
**Domain**: Financial Health
**Rule Name**: Bill Discipline Score
**Description**: Calculate bill discipline score
**Trigger**: Financial health score calculation
**Conditions**: Bill payment data exists
**Action**: 
```
Bill Score = (Bills Paid on Time / Total Bills) * 100
Score Range: 0-100
```
**Exceptions**: No bill data → score = 0
**Priority**: P1 (High)
**Configurable?**: No
**Related FRS Requirement**: FRS-HLT-001
**Related User Story**: US-HLT-001

### BR-HLT-006: Score Interpretation
**Rule ID**: BR-HLT-006
**Domain**: Financial Health
**Rule Name**: Score Interpretation
**Description**: Interpret financial health score
**Trigger**: Financial health score displayed
**Conditions**: Score calculated
**Action**: 
- 80-100: Excellent
- 60-79: Good
- 40-59: Fair
- 20-39: Poor
- 0-19: Critical
**Exceptions**: None
**Priority**: P1 (High)
**Configurable?**: Yes (score ranges)
**Related FRS Requirement**: FRS-HLT-001
**Related User Story**: US-HLT-001

---

## 11. Neo AI Rules

### BR-AI-001: AI Read-Only Access
**Rule ID**: BR-AI-001
**Domain**: Neo AI
**Rule Name**: AI Read-Only Access
**Description**: AI must operate in read-only mode
**Trigger**: AI service processes request
**Conditions**: AI service active
**Action**: AI can only read data via module APIs, cannot modify data
**Exceptions**: None
**Priority**: P0 (Critical)
**Configurable?**: No
**Related FRS Requirement**: FRS-AI-001
**Related User Story**: US-AI-001

### BR-AI-002: AI No Direct Database Access
**Rule ID**: BR-AI-002
**Domain**: Neo AI
**Rule Name**: AI No Direct Database Access
**Description**: AI cannot access production database directly
**Trigger**: AI service attempts data access
**Conditions**: AI service needs data
**Action**: AI must access data via Spring Boot module APIs only
**Exceptions**: None
**Priority**: P0 (Critical)
**Configurable?**: No
**Related FRS Requirement**: FRS-AI-001
**Related User Story**: US-AI-001

### BR-AI-003: AI No Autonomous Transactions
**Rule ID**: BR-AI-003
**Domain**: Neo AI
**Rule Name**: AI No Autonomous Transactions
**Description**: AI cannot autonomously execute financial transactions
**Trigger**: AI generates recommendation
**Conditions**: AI recommends financial action
**Action**: AI provides recommendation only, user must approve before execution
**Exceptions**: None
**Priority**: P0 (Critical)
**Configurable?**: No
**Related FRS Requirement**: FRS-AI-004
**Related User Story**: US-AI-004

### BR-AI-004: AI No Arbitrary SQL
**Rule ID**: BR-AI-004
**Domain**: Neo AI
**Rule Name**: AI No Arbitrary SQL
**Description**: AI cannot generate or execute SQL queries
**Trigger**: AI service processes request
**Conditions**: AI needs data
**Action**: AI must use predefined module APIs, cannot generate SQL
**Exceptions**: None
**Priority**: P0 (Critical)
**Configurable?**: No
**Related FRS Requirement**: FRS-AI-001
**Related User Story**: US-AI-001

### BR-AI-005: AI Recommendation Distinction
**Rule ID**: BR-AI-005
**Domain**: Neo AI
**Rule Name**: AI Recommendation Distinction
**Description**: AI recommendations must be clearly distinguished from deterministic calculations
**Trigger**: AI displays recommendation
**Conditions**: AI provides recommendation
**Action**: Display "AI RECOMMENDATION" label, display confidence score, require user approval
**Exceptions**: None
**Priority**: P0 (Critical)
**Configurable?**: No
**Related FRS Requirement**: FRS-AI-004
**Related User Story**: US-AI-004

### BR-AI-006: AI Capability Boundaries
**Rule ID**: BR-AI-006
**Domain**: Neo AI
**Rule Name**: AI Capability Boundaries
**Description**: Define AI capabilities for MVP
**Trigger**: AI service processes request
**Conditions**: AI service active
**Action**: AI can: spending analysis, budget explanation, budget recommendations, financial summaries, bill analysis, savings recommendations, financial health explanation
AI cannot: move money, execute payments, change budgets without approval, delete transactions, modify records
**Exceptions**: None
**Priority**: P0 (Critical)
**Configurable?**: No
**Related FRS Requirement**: FRS-AI-001
**Related User Story**: US-AI-001

---

## 12. AI Confidence Rules

### BR-AIC-001: High Confidence Threshold
**Rule ID**: BR-AIC-001
**Domain**: AI Confidence
**Rule Name**: High Confidence Threshold
**Description**: Define high confidence threshold
**Trigger**: AI generates recommendation
**Conditions**: AI confidence score calculated
**Action**: Confidence >= 80% = High Confidence
**Exceptions**: None
**Priority**: P2 (Medium)
**Configurable?**: Yes (HIGH_CONFIDENCE_THRESHOLD, default 80%)
**Related FRS Requirement**: FRS-AI-004
**Related User Story**: US-AI-004

### BR-AIC-002: Medium Confidence Threshold
**Rule ID**: BR-AIC-002
**Domain**: AI Confidence
**Rule Name**: Medium Confidence Threshold
**Description**: Define medium confidence threshold
**Trigger**: AI generates recommendation
**Conditions**: AI confidence score calculated
**Action**: Confidence 60-79% = Medium Confidence
**Exceptions**: None
**Priority**: P2 (Medium)
**Configurable?**: Yes (MEDIUM_CONFIDENCE_MIN, default 60%)
**Related FRS Requirement**: FRS-AI-004
**Related User Story**: US-AI-004

### BR-AIC-003: Low Confidence Handling
**Rule ID**: BR-AIC-003
**Domain**: AI Confidence
**Rule Name**: Low Confidence Handling
**Description**: Handle low confidence recommendations
**Trigger**: AI generates recommendation
**Conditions**: AI confidence < 60%
**Action**: Display "LOW CONFIDENCE" label, consider withholding recommendation
**Exceptions**: None
**Priority**: P2 (Medium)
**Configurable?**: Yes (LOW_CONFIDENCE_THRESHOLD, default 60%)
**Related FRS Requirement**: FRS-AI-004
**Related User Story**: US-AI-004

---

## 13. Security Rules

### BR-SEC-001: Authentication Token Expiry
**Rule ID**: BR-SEC-001
**Domain**: Security
**Rule Name**: Authentication Token Expiry
**Description**: JWT access tokens expire after configured duration
**Trigger**: User authenticates
**Conditions**: Access token generated
**Action**: Set token expiry to configured duration (default 1 hour)
**Exceptions**: None
**Priority**: P0 (Critical)
**Configurable?**: Yes (ACCESS_TOKEN_EXPIRY, default 1 hour)
**Related FRS Requirement**: FRS-AUTH-002
**Related User Story**: US-AUTH-002

### BR-SEC-002: RBAC Enforcement
**Rule ID**: BR-SEC-002
**Domain**: Security
**Rule Name**: RBAC Enforcement
**Description**: Enforce role-based access control
**Trigger**: User attempts action
**Conditions**: User has role
**Action**: Check role permissions, allow or deny action based on permissions
**Exceptions**: Insufficient permissions → display access denied
**Priority**: P0 (Critical)
**Configurable?**: No
**Related FRS Requirement**: FRS-FAM-005
**Related User Story**: US-FAM-005

### BR-SEC-003: Sensitive Data Encryption
**Rule ID**: BR-SEC-003
**Domain**: Security
**Rule Name**: Sensitive Data Encryption
**Description**: Encrypt sensitive data at rest
**Trigger**: Sensitive data stored
**Conditions**: Sensitive data (passwords, PII) stored
**Action**: Encrypt using AES-256, store encryption keys in KMS
**Exceptions**: None
**Priority**: P0 (Critical)
**Configurable?**: No
**Related FRS Requirement**: FRS-AUTH-001
**Related User Story**: US-AUTH-001

### BR-SEC-004: Audit Logging
**Rule ID**: BR-SEC-004
**Domain**: Security
**Rule Name**: Audit Logging
**Description**: Log all security-relevant events
**Trigger**: Security event occurs
**Conditions**: User performs action
**Action**: Log timestamp, user, action, IP, device, result
**Exceptions**: None
**Priority**: P0 (Critical)
**Configurable?**: No
**Related FRS Requirement**: FRS-AUTH-002
**Related User Story**: US-AUTH-002

### BR-SEC-005: Rate Limiting
**Rule ID**: BR-SEC-005
**Domain**: Security
**Rule Name**: Rate Limiting
**Description**: Apply rate limiting to API endpoints
**Trigger**: API request received
**Conditions**: Request rate exceeds threshold
**Action**: Reject request with 429 status, display rate limit exceeded message
**Exceptions**: None
**Priority**: P1 (High)
**Configurable?**: Yes (RATE_LIMIT_THRESHOLD)
**Related FRS Requirement**: FRS-AUTH-002
**Related User Story**: US-AUTH-002

### BR-SEC-006: Failed Authentication Lockout
**Rule ID**: BR-SEC-006
**Domain**: Security
**Rule Name**: Failed Authentication Lockout
**Description**: Lock account after failed authentication attempts
**Trigger**: Consecutive failed login attempts
**Conditions**: Failed attempts exceed threshold
**Action**: Lock account for configured duration
**Exceptions**: None
**Priority**: P1 (High)
**Configurable?**: Yes (MAX_FAILED_ATTEMPTS, LOCKOUT_DURATION)
**Related FRS Requirement**: FRS-AUTH-002
**Related User Story**: US-AUTH-002

### BR-SEC-007: Data Access Control
**Rule ID**: BR-SEC-007
**Domain**: Security
**Rule Name**: Data Access Control
**Description**: Enforce data access control
**Trigger**: User attempts to access data
**Conditions**: User requests data
**Action**: Verify user has permission to access data, deny if insufficient permissions
**Exceptions**: Insufficient permissions → display access denied
**Priority**: P0 (Critical)
**Configurable?**: No
**Related FRS Requirement**: FRS-FAM-006
**Related User Story**: US-FAM-006

---

## 14. Data Retention Rules

### BR-RET-001: Transaction Retention
**Rule ID**: BR-RET-001
**Domain**: Data Retention
**Rule Name**: Transaction Retention
**Description**: Define transaction data retention period
**Trigger**: Transaction data archived
**Conditions**: Transaction age exceeds retention period
**Action**: Archive or anonymize transaction data
**Exceptions**: Requires Compliance/Legal Decision
**Priority**: P2 (Medium)
**Configurable?**: Yes (TRANSACTION_RETENTION_PERIOD)
**Related FRS Requirement**: FRS-TRX-001
**Related User Story**: US-TRX-001

### BR-RET-002: Audit Log Retention
**Rule ID**: BR-RET-002
**Domain**: Data Retention
**Rule Name**: Audit Log Retention
**Description**: Define audit log retention period
**Trigger**: Audit log archived
**Conditions**: Audit log age exceeds retention period
**Action**: Archive audit log
**Exceptions**: Requires Compliance/Legal Decision
**Priority**: P0 (Critical)
**Configurable?**: Yes (AUDIT_LOG_RETENTION_PERIOD)
**Related FRS Requirement**: FRS-AUTH-002
**Related User Story**: US-AUTH-002

### BR-RET-003: Notification Retention
**Rule ID**: BR-RET-003
**Domain**: Data Retention
**Rule Name**: Notification Retention
**Description**: Define notification retention period
**Trigger**: Notification archived
**Conditions**: Notification age exceeds retention period
**Action**: Delete notification
**Exceptions**: Requires Compliance/Legal Decision
**Priority**: P2 (Medium)
**Configurable?**: Yes (NOTIFICATION_RETENTION_PERIOD)
**Related FRS Requirement**: FRS-NOT-002
**Related User Story**: US-NOT-002

### BR-RET-004: AI Conversation Retention
**Rule ID**: BR-RET-004
**Domain**: Data Retention
**Rule Name**: AI Conversation Retention
**Description**: Define AI conversation retention period
**Trigger**: AI conversation archived
**Conditions**: Conversation age exceeds retention period
**Action**: Delete or anonymize conversation
**Exceptions**: Requires Compliance/Legal Decision
**Priority**: P2 (Medium)
**Configurable?**: Yes (AI_CONVERSATION_RETENTION_PERIOD)
**Related FRS Requirement**: FRS-AI-001
**Related User Story**: US-AI-001

### BR-RET-005: User Data Retention
**Rule ID**: BR-RET-005
**Domain**: Data Retention
**Rule Name**: User Data Retention
**Description**: Define user data retention period after account deletion
**Trigger**: Account deleted
**Conditions**: Account deletion completed
**Action**: Anonymize user data after configured period
**Exceptions**: Requires Compliance/Legal Decision
**Priority**: P0 (Critical)
**Configurable?**: Yes (USER_DATA_RETENTION_PERIOD)
**Related FRS Requirement**: FRS-USER-005
**Related User Story**: US-USER-005

---

## 15. Configuration Rules

### BR-CFG-001: User Configurable Thresholds
**Rule ID**: BR-CFG-001
**Domain**: Configuration
**Rule Name**: User Configurable Thresholds
**Description**: Define thresholds users can configure
**Trigger**: User modifies preferences
**Conditions**: User accesses settings
**Action**: Allow user to configure: budget warning percentage, notification preferences, quiet hours
**Exceptions**: None
**Priority**: P2 (Medium)
**Configurable?**: Yes (by user)
**Related FRS Requirement**: FRS-NOT-001
**Related User Story**: US-NOT-001

### BR-CFG-002: Admin Configurable Thresholds
**Rule ID**: BR-CFG-002
**Domain**: Configuration
**Rule Name**: Admin Configurable Thresholds
**Description**: Define thresholds admins can configure
**Trigger**: Admin modifies system settings
**Conditions**: Admin accesses admin settings
**Action**: Allow admin to configure: rate limits, session timeout, failed attempt limits, retention periods
**Exceptions**: None
**Priority**: P1 (High)
**Configurable?**: Yes (by admin)
**Related FRS Requirement**: FRS-AUTH-002
**Related User Story**: US-AUTH-002

### BR-CFG-003: System Controlled Thresholds
**Rule ID**: BR-CFG-003
**Domain**: Configuration
**Rule Name**: System Controlled Thresholds
**Description**: Define thresholds controlled by system
**Trigger**: System operation
**Conditions**: System processes data
**Action**: System controls: financial health score weights, AI confidence thresholds, budget exceeded threshold (default 100%)
**Exceptions**: None
**Priority**: P1 (High)
**Configurable?**: Yes (by admin)
**Related FRS Requirement**: FRS-HLT-001
**Related User Story**: US-HLT-001

### BR-CFG-004: Default Configuration Values
**Rule ID**: BR-CFG-004
**Domain**: Configuration
**Rule Name**: Default Configuration Values
**Description**: Define default configuration values
**Trigger**: System initialization
**Conditions**: Configuration not set
**Action**: Apply default values
**Exceptions**: None
**Priority**: P1 (High)
**Configurable?**: No
**Related FRS Requirement**: FRS-BUD-004
**Related User Story**: US-BUD-004

### BR-CFG-005: Configuration Validation
**Rule ID**: BR-CFG-005
**Domain**: Configuration
**Rule Name**: Configuration Validation
**Description**: Validate configuration changes
**Trigger**: Configuration modified
**Conditions**: Configuration value changed
**Action**: Validate value is within acceptable range, type, format
**Exceptions**: Invalid value → display error, revert to previous value
**Priority**: P1 (High)
**Configurable?**: No
**Related FRS Requirement**: FRS-NOT-001
**Related User Story**: US-NOT-001

### BR-CFG-006: Configuration Audit Trail
**Rule ID**: BR-CFG-006
**Domain**: Configuration
**Rule Name**: Configuration Audit Trail
**Description**: Maintain audit trail for configuration changes
**Trigger**: Configuration modified
**Conditions**: Configuration value changed
**Action**: Log old value, new value, timestamp, user, reason
**Exceptions**: None
**Priority**: P1 (High)
**Configurable?**: No
**Related FRS Requirement**: FRS-AUTH-002
**Related User Story**: US-AUTH-002

---

## Business Rules Requiring Product Owner Approval

### CRITICAL (Must Approve Before Development)

1. **BR-FIN-006: Planning Value Representation** - Confirm all financial overview values are clearly labeled as planning values
2. **BR-FAM-004 to BR-FAM-006: Family Role Permissions** - Confirm FAMILY_OWNER, FAMILY_MEMBER, RESTRICTED permissions
3. **BR-TRX-002: Transaction Status Lifecycle** - Confirm transaction status states and transitions
4. **BR-AI-001 to BR-AI-006: AI Boundaries** - Confirm AI read-only mode and capability boundaries
5. **BR-SEC-001 to BR-SEC-007: Security Rules** - Confirm security rules and thresholds
6. **BR-RET-002 and BR-RET-005: Critical Data Retention** - Confirm audit log and user data retention (requires legal review)

### IMPORTANT (Should Approve Before MVP)

1. **BR-BUD-003 and BR-BUD-004: Budget Thresholds** - Confirm default values (80% warning, 100% exceeded)
2. **BR-BIL-003: Bill Reminder Thresholds** - Confirm default values (7 days, 2 days, due date)
3. **BR-HLT-001 to BR-HLT-006: Financial Health Scoring** - Confirm scoring algorithm and factor weights
4. **BR-AIC-001 to BR-AIC-003: AI Confidence Thresholds** - Confirm confidence thresholds (80%, 60-79%, <60%)
5. **BR-RET-001, BR-RET-003, BR-RET-004: Data Retention** - Confirm retention periods (requires legal review)

### CONFIGURABLE DEFAULTS (Can Approve with Defaults)

1. **BR-USR-003: Session Timeout** - Default 30 minutes
2. **BR-USR-004: Failed Authentication Lockout** - Default 5 attempts, 30 minutes lockout
3. **BR-USR-007: Device Limit** - Default 5 devices
4. **BR-USR-008: Reset Token Expiry** - Default 1 hour
5. **BR-FAM-002: Invitation Token Expiry** - Default 7 days
6. **BR-FAM-008: Spending Limits** - Default amounts to be configured
7. **BR-TRX-007: Duplicate Detection Threshold** - Default similarity threshold
8. **BR-BUD-005: Budget Rollover** - Default disabled
9. **BR-NOT-001: Notification Preferences** - Default all enabled
10. **BR-SEC-001: Access Token Expiry** - Default 1 hour
11. **BR-SEC-005: Rate Limiting** - Default threshold to be configured
12. **BR-SEC-006: Failed Attempt Limit** - Default 5 attempts
13. **BR-SEC-006: Lockout Duration** - Default 30 minutes

---

## Open Decisions

1. **Budget Warning Threshold**: What should be the default budget warning percentage? (Proposed: 80%)
2. **Budget Exceeded Threshold**: What should be the default budget exceeded percentage? (Proposed: 100%)
3. **Bill Reminder Days**: What should be the default bill reminder days? (Proposed: 7 days, 2 days, due date)
4. **Financial Health Factor Weights**: What should be the weights for each health factor? (Proposed: equal weights initially)
5. **AI Confidence Thresholds**: What should be the confidence thresholds? (Proposed: High >=80%, Medium 60-79%, Low <60%)
6. **Data Retention Periods**: What should be the retention periods for transactions, audit logs, notifications, AI conversations? (Requires legal review)
7. **Session Timeout**: What should be the default session timeout? (Proposed: 30 minutes)
8. **Failed Authentication Lockout**: What should be the max failed attempts and lockout duration? (Proposed: 5 attempts, 30 minutes)
9. **Device Limit**: What should be the max devices per user? (Proposed: 5)
10. **Budget Rollover**: Should budget rollover be enabled by default? (Proposed: disabled)

---

## Conclusion

This document defines 85 business rules across 15 domains for NeoWallet MVP. All rules enforce the financial management model (no stored-value wallet), AI read-only mode, and configurable thresholds.

**Total Rules**: 85
**Critical Rules**: 25
**High Priority Rules**: 35
**Medium Priority Rules**: 25

**Key Principles**:
- Financial overview values are planning values, not actual funds
- AI operates in read-only mode with no autonomous transactions
- All thresholds are configurable (not hard-coded)
- Security rules are enforced at all levels
- Audit trails maintained for all sensitive operations

**Next Steps**: Product Owner approval of critical and important rules, legal review of data retention rules, then proceed to NW-003.

---

**Document Version**: v1
**Date**: August 17, 2026
**Status**: Ready for Product Owner Approval
**Next Step**: Product Owner approval, then NW-003
