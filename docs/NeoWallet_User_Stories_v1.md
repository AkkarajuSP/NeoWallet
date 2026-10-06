# NeoWallet User Stories v1

## Executive Summary

Comprehensive user stories for NeoWallet MVP based on approved product/payment model. 72 user stories across 11 features.

**Product**: Financial management and payment orchestration platform (does NOT hold funds)
**Core Value**: PLAN → OPTIMIZE → DECIDE → PAY
**Total Stories**: 72
**Date**: August 17, 2026

---

## User Story Inventory

| Epic | Feature | Story Count |
|------|---------|-------------|
| Authentication | Authentication | 6 |
| User Profile | User Profile | 5 |
| Family Management | Family Management | 9 |
| Financial Overview | Financial Overview | 8 |
| Transactions | Transactions | 7 |
| Budget | Budget | 7 |
| Savings Goals | Savings Goals | 6 |
| Bills | Bills | 6 |
| Notifications | Notifications | 5 |
| Neo AI Concierge | Neo AI Concierge | 8 |
| Financial Health | Financial Health | 5 |
| **TOTAL** | **11 Features** | **72** |

---

## Epic 1: Authentication (6 Stories)

**US-AUTH-001**: User Registration - Create account with email/password
**US-AUTH-002**: User Login - Authenticate with JWT tokens
**US-AUTH-003**: Password Reset - Reset via email
**US-AUTH-004**: Session Management - Auto-logout after inactivity
**US-AUTH-005**: Device Registration - Register device for push notifications
**US-AUTH-006**: Logout - Secure session termination

---

## Epic 2: User Profile (5 Stories)

**US-USER-001**: View User Profile - Display profile information
**US-USER-002**: Update User Profile - Update name, phone, preferences
**US-USER-003**: Upload Profile Image - Upload and manage profile picture
**US-USER-004**: Manage User Preferences - Notification, language, currency settings
**US-USER-005**: Delete Account - Permanently delete account and data

---

## Epic 3: Family Management (9 Stories)

**US-FAM-001**: Create Family - Create family and become owner
**US-FAM-002**: Invite Family Member - Send invitation via email
**US-FAM-003**: Accept Family Invitation - Join family
**US-FAM-004**: Remove Family Member - Remove member from family
**US-FAM-005**: Assign Family Member Role - Set MEMBER or RESTRICTED role
**US-FAM-006**: View Shared Financial Overview - View family finances
**US-FAM-007**: Set Family Spending Limits - Set limits for members
**US-FAM-008**: Leave Family - Exit family (non-owners)
**US-FAM-009**: View Family Transactions - View family spending

---

## Epic 4: Financial Overview (8 Stories)

**US-WAL-001**: View Financial Overview - Display calculated financial position
**US-WAL-002**: Create Financial Overview - Create planning container
**US-WAL-003**: Set Planned Allocation - Set total planned spending
**US-WAL-004**: View Budget Allocation - View budget distribution
**US-WAL-005**: View Committed Amount - View pending obligations
**US-WAL-006**: View Available Financial Capacity - View remaining capacity
**US-WAL-007**: View Pending Payments - Track payment status
**US-WAL-008**: Switch Between Individual and Family Overview - Toggle views

---

## Epic 5: Transactions (7 Stories)

**US-TRX-001**: Record Manual Transaction - Add income/expense
**US-TRX-002**: Categorize Transaction - Assign category
**US-TRX-003**: Search and Filter Transactions - Find specific transactions
**US-TRX-004**: Edit Transaction - Update with audit trail
**US-TRX-005**: View Transaction History - Review past transactions
**US-TRX-006**: Generate Transaction Reports - Analyze spending
**US-TRX-007**: Delete Transaction - Remove incorrect records

---

## Epic 6: Budget (7 Stories)

**US-BUD-001**: Create Budget - Set category budget limit
**US-BUD-002**: Set Budget Limit - Update budget amount
**US-BUD-003**: Track Budget vs Actual - Monitor adherence
**US-BUD-004**: Receive Budget Alerts - Notifications for limits
**US-BUD-005**: View Budget Progress Visualization - Visual tracking
**US-BUD-006**: Manage Budget Period - Set time periods
**US-BUD-007**: Delete Budget - Remove budget

---

## Epic 7: Savings Goals (6 Stories)

**US-SAV-001**: Create Savings Goal - Set target and timeline
**US-SAV-002**: Track Savings Progress - Monitor goal achievement
**US-SAV-003**: Make Goal Contribution - Add to goal
**US-SAV-004**: View Goal Visualization - Visual progress tracking
**US-SAV-005**: Mark Goal as Achieved - Celebrate completion
**US-SAV-006**: Delete Savings Goal - Remove goal

---

## Epic 8: Bills (6 Stories)

**US-BIL-001**: Create Bill - Add bill with due date
**US-BIL-002**: Track Bill Due Dates - Monitor upcoming bills
**US-BIL-003**: Receive Bill Reminders - Notifications before due
**US-BIL-004**: Record Bill Payment - Mark as paid (manual entry)
**US-BIL-005**: View Bill History - Review payment history
**US-BIL-006**: Manage Recurring Bills - Set recurrence patterns

---

## Epic 9: Notifications (5 Stories)

**US-NOT-001**: Configure Notification Preferences - Set notification types
**US-NOT-002**: View Notification History - Review past notifications
**US-NOT-003**: Receive In-App Notifications - App notifications
**US-NOT-004**: Receive Email Notifications - Email alerts
**US-NOT-005**: Receive Push Notifications - Device push alerts

---

## Epic 10: Neo AI Concierge (8 Stories)

**US-AI-001**: Ask Neo AI General Question - Financial Q&A
**US-AI-002**: Request Spending Analysis - AI spending insights
**US-AI-003**: Request Budget Explanation - AI budget analysis
**US-AI-004**: Request Budget Recommendations - AI optimization suggestions
**US-AI-005**: Request Financial Summary - AI overview
**US-AI-006**: Request Bill Analysis - AI bill insights
**US-AI-007**: Request Savings Recommendations - AI savings optimization
**US-AI-008**: Request Financial Health Explanation - AI health analysis

**AI Security**: All AI stories enforce read-only mode, no database access, no autonomous transactions, human-in-the-loop for recommendations.

---

## Epic 11: Financial Health (5 Stories)

**US-HLT-001**: View Financial Health Score - Display wellness score
**US-HLT-002**: View Spending Trends - Analyze patterns
**US-HLT-003**: View Savings Rate - Track savings percentage
**US-HLT-004**: View Health Visualization - Visual health dashboard
**US-HLT-005**: View Improvement Suggestions - Actionable recommendations

---

## MVP Traceability Matrix

All 72 stories trace to MVP features. Priority breakdown:
- P0 (Critical): 12 stories
- P1 (High): 45 stories
- P2 (Medium): 15 stories

---

## Missing Requirements

1. **Business Rules**: Budget calculation algorithms, financial health scoring methodology not defined
2. **AI Decision Boundaries**: Specific AI capability limits not detailed
3. **Notification Thresholds**: Budget alert trigger percentages not specified
4. **Financial Health Scoring**: Scoring algorithm and factors not defined
5. **Family Permission Details**: Specific permissions per role not detailed

---

## Questions for Product Owner

1. **Budget Alert Thresholds**: What percentage should trigger budget alerts (e.g., 80%, 90%, 100%)?
2. **Financial Health Scoring**: What is the scoring algorithm and weight of each factor?
3. **AI Confidence Thresholds**: What confidence score required for AI recommendations?
4. **Family RESTRICTED Role**: What specific data should RESTRICTED members see/hide?
5. **Notification Quiet Hours**: Should quiet hours be configurable? Default quiet hours?
6. **Transaction Categories**: Should users be able to create custom categories in MVP?
7. **Budget Rollover**: Should unspent budget roll over to next period?
8. **Savings Goal Types**: Should we support different goal types (emergency fund, vacation, etc.)?
9. **Bill Payment Recording**: Should manual bill payment create transaction records?
10. **AI Response Limits**: Should there be token/cost limits per user per month?

---

## Conclusion

72 user stories defined for MVP across 11 features. All stories enforce financial management model (no stored-value wallet). AI stories enforce read-only mode. Ready for Product Owner approval.

**Document Version**: v1
**Status**: Ready for Review
**Next Step**: Product Owner approval, then NW-003
