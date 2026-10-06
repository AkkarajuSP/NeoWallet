# NeoWallet Data Retention Matrix v1

## Executive Summary

This document defines the data retention matrix for NeoWallet MVP, mapping all data entities to retention periods and identifying areas requiring compliance/legal decision.

**IMPORTANT DISCLAIMER**: This document does not invent statutory retention periods. Where retention is legally uncertain, it is marked "Compliance Decision Required."

**Date**: August 18, 2026
**Status**: Ready for Product Owner Approval

---

## 1. Retention Matrix Overview

### 1.1 Data Categories

| Category | Entities | Retention Period | Status |
|----------|----------|-----------------|--------|
| User Data | users, user_profiles, user_preferences, user_addresses | 7 years after deletion | Business Rule |
| Authentication Records | user_credentials, user_sessions, user_devices, otp_challenges | 7 years after deletion | Business Rule |
| Transactions | transactions | 7 years | Business Rule |
| Budgets | budgets, budget_categories, budget_periods, budget_recommendations | 7 years | Business Rule |
| Allocations | allocation_recommendations, allocation_categories, allocation_versions, allocation_approvals | 7 years | Business Rule |
| Savings | savings_goals, savings_contributions, savings_progress | 7 years after completion | Business Rule |
| Bills | bills, bill_categories, bill_status_history | 7 years | Business Rule |
| Notifications | notifications, notification_preferences, notification_deliveries | 90 days | Compliance Decision Required |
| AI Conversations | ai_conversations, ai_messages, ai_tool_invocations, ai_recommendations | 1 year | Compliance Decision Required |
| Audit Logs | audit_logs | 7 years | Business Rule |
| Security Logs | application logs, security logs | 90 days | Compliance Decision Required |
| Consent Records | user_preferences (consent fields) | 7 years | Business Rule |

### 1.2 Retention Status Legend

- **Business Rule**: Retention period defined in business rules
- **Compliance Decision Required**: Retention period requires legal/compliance review
- **Not Currently Identified**: Retention period not currently identified

---

## 2. User Data Retention

### 2.1 Users

**Entity**: users

**Data**:
- User ID (UUID)
- Email address
- Phone number (optional)
- Account status
- Created timestamp
- Updated timestamp
- Deleted timestamp (soft delete)

**Retention Period**: 7 years after deletion

**Legal Basis**: Business Rule BR-RET-001

**Deletion Process**:
1. User requests account deletion
2. System soft-deletes user account (deleted_at timestamp)
3. User data anonymized after 7 years
4. Audit logs retained for 7 years

**Professional Review Required**: No (business rule defined)

### 2.2 User Profiles

**Entity**: user_profiles

**Data**:
- User ID (UUID)
- First name
- Last name
- Date of birth (optional)
- Timezone
- Locale
- Currency
- Family ID (optional)
- Address ID (optional)
- Created timestamp
- Updated timestamp

**Retention Period**: 7 years after deletion

**Legal Basis**: Business Rule BR-RET-001

**Deletion Process**:
1. User requests account deletion
2. System soft-deletes user profile (deleted_at timestamp)
3. User profile data anonymized after 7 years
4. Audit logs retained for 7 years

**Professional Review Required**: No (business rule defined)

### 2.3 User Preferences

**Entity**: user_preferences

**Data**:
- User ID (UUID)
- Notification preferences
- App preferences
- Privacy preferences
- Consent records
- Created timestamp
- Updated timestamp

**Retention Period**: 7 years after deletion

**Legal Basis**: Business Rule BR-RET-001

**Deletion Process**:
1. User requests account deletion
2. System soft-deletes user preferences (deleted_at timestamp)
3. User preferences data anonymized after 7 years
4. Audit logs retained for 7 years

**Professional Review Required**: No (business rule defined)

### 2.4 User Addresses

**Entity**: user_addresses

**Data**:
- Address ID (UUID)
- User ID (UUID)
- Address line 1
- Address line 2 (optional)
- City
- State
- Postal code
- Country
- Address type
- Created timestamp
- Updated timestamp

**Retention Period**: 7 years after deletion

**Legal Basis**: Business Rule BR-RET-001

**Deletion Process**:
1. User requests account deletion
2. System soft-deletes user address (deleted_at timestamp)
3. User address data anonymized after 7 years
4. Audit logs retained for 7 years

**Professional Review Required**: No (business rule defined)

---

## 3. Authentication Records Retention

### 3.1 User Credentials

**Entity**: user_credentials

**Data**:
- Credential ID (UUID)
- User ID (UUID)
- Credential type
- Credential value (hashed)
- Created timestamp
- Updated timestamp

**Retention Period**: 7 years after deletion

**Legal Basis**: Business Rule BR-RET-001

**Deletion Process**:
1. User requests account deletion
2. System deletes user credentials
3. Audit logs retained for 7 years

**Professional Review Required**: No (business rule defined)

### 3.2 User Sessions

**Entity**: user_sessions

**Data**:
- Session ID (UUID)
- User ID (UUID)
- Device ID (UUID)
- Refresh token hash
- IP address
- User agent
- Last active timestamp
- Expiration timestamp
- Revoked timestamp
- Created timestamp

**Retention Period**: 7 years after deletion

**Legal Basis**: Business Rule BR-RET-001

**Deletion Process**:
1. User requests account deletion
2. System deletes user sessions
3. Audit logs retained for 7 years

**Professional Review Required**: No (business rule defined)

### 3.3 User Devices

**Entity**: user_devices

**Data**:
- Device ID (UUID)
- User ID (UUID)
- Device token
- Device type
- Platform
- OS version
- App version
- Device fingerprint
- Created timestamp
- Updated timestamp

**Retention Period**: 7 years after deletion

**Legal Basis**: Business Rule BR-RET-001

**Deletion Process**:
1. User requests account deletion
2. System deletes user devices
3. Audit logs retained for 7 years

**Professional Review Required**: No (business rule defined)

### 3.4 OTP Challenges

**Entity**: otp_challenges

**Data**:
- OTP ID (UUID)
- User ID (UUID)
- OTP type
- OTP hash (SHA-256)
- Expiration timestamp
- Attempts
- Created timestamp

**Retention Period**: 7 years after deletion

**Legal Basis**: Business Rule BR-RET-001

**Deletion Process**:
1. User requests account deletion
2. System deletes OTP challenges
3. Audit logs retained for 7 years

**Professional Review Required**: No (business rule defined)

---

## 4. Transactions Retention

### 4.1 Transactions

**Entity**: transactions

**Data**:
- Transaction ID (UUID)
- User ID (UUID)
- Family ID (optional)
- Category ID
- Type
- Amount
- Currency
- Transaction date
- Posted date (optional)
- Description
- Status
- Source
- Family member ID (optional)
- Is recurring
- Recurring pattern (optional)
- External reference (optional)
- Created timestamp
- Updated timestamp

**Retention Period**: 7 years

**Legal Basis**: Business Rule BR-RET-002

**Deletion Process**:
- Transactions are NOT soft-deleted
- Transactions are retained for 7 years
- After 7 years, transactions can be archived or deleted
- Audit logs retained for 7 years

**Professional Review Required**: No (business rule defined)

---

## 5. Budgets Retention

### 5.1 Budgets

**Entity**: budgets

**Data**:
- Budget ID (UUID)
- User ID (UUID) or Family ID (UUID)
- Name
- Period
- Version
- Status
- Created timestamp
- Updated timestamp

**Retention Period**: 7 years

**Legal Basis**: Business Rule BR-RET-003

**Deletion Process**:
- Budgets are soft-deleted (status = DELETED)
- Budgets retained for 7 years
- After 7 years, budgets can be archived or deleted
- Audit logs retained for 7 years

**Professional Review Required**: No (business rule defined)

### 5.2 Budget Categories

**Entity**: budget_categories

**Data**:
- Budget category ID (UUID)
- Budget ID (UUID)
- Category ID
- Planned amount
- Currency
- Created timestamp
- Updated timestamp

**Retention Period**: 7 years

**Legal Basis**: Business Rule BR-RET-003

**Deletion Process**:
 budget categories retained for 7 years
- After 7 years, budget categories can be archived or deleted
- Audit logs retained for 7 years

**Professional Review Required**: No (business rule defined)

### 5.3 Budget Periods

**Entity**: budget_periods

**Data**:
- Budget period ID (UUID)
- Budget ID (UUID)
- Period
- Status
- Created timestamp
- Updated timestamp

**Retention Period**: 7 years

**Legal Basis**: Business Rule BR-RET-003

**Deletion Process**:
- Budget periods retained for 7 years
- After 7 years, budget periods can be archived or deleted
- Audit logs retained for 7 years

**Professional Review Required**: No (business rule defined)

### 5.4 Budget Recommendations

**Entity**: budget_recommendations

**Data**:
- Recommendation ID (UUID)
- Budget ID (UUID)
- Recommended allocation
- User modification
- Final approved budget
- Status
- Created timestamp
- Updated timestamp

**Retention Period**: 7 years

**Legal Basis**: Business Rule BR-RET-003

**Deletion Process**:
- Budget recommendations retained for 7 years
- After 7 years, budget recommendations can be archived or deleted
- Audit logs retained for 7 years

**Professional Review Required**: No (business rule defined)

---

## 6. Allocations Retention

### 6.1 Allocation Recommendations

**Entity**: allocation_recommendations

**Data**:
- Recommendation ID (UUID)
- User ID (UUID) or Family ID (UUID)
- Period
- System recommendation
- User modification
- Final approved allocation
- Status
- Historical months used
- Created timestamp
- Updated timestamp

**Retention Period**: 7 years

**Legal Basis**: Business Rule BR-RET-003

**Deletion Process**:
- Allocation recommendations retained for 7 years
- After 7 years, allocation recommendations can be archived or deleted
- Audit logs retained for 7 years

**Professional Review Required**: No (business rule defined)

### 6.2 Allocation Categories

**Entity**: allocation_categories

**Data**:
- Allocation category ID (UUID)
- Recommendation ID (UUID)
- Category ID
- Recommended amount
- Currency
- Priority
- Created timestamp
- Updated timestamp

**Retention Period**: 7 years

**Legal Basis**: Business Rule BR-RET-003

**Deletion Process**:
- Allocation categories retained for 7 years
- After 7 years, allocation categories can be archived or deleted
- Audit logs retained for 7 years

**Professional Review Required**: No (business rule defined)

### 6.3 Allocation Versions

**Entity**: allocation_versions

**Data**:
- Version ID (UUID)
- Recommendation ID (UUID)
- Version number
- Allocation data
- Change reason
- Created timestamp

**Retention Period**: 7 years

**Legal Basis**: Business Rule BR-RET-003

**Deletion Process**:
- Allocation versions retained for 7 years
- After 7 years, allocation versions can be archived or deleted
- Audit logs retained for 7 years

**Professional Review Required**: No (business rule defined)

### 6.4 Allocation Approvals

**Entity**: allocation_approvals

**Data**:
- Approval ID (UUID)
- Recommendation ID (UUID)
- User ID (UUID)
- Approval status
- Approval timestamp
- Created timestamp

**Retention Period**: 7 years

**Legal Basis**: Business Rule BR-RET-003

**Deletion Process**:
- Allocation approvals retained for 7 years
- After 7 years, allocation approvals can be archived or deleted
- Audit logs retained for 7 years

**Professional Review Required**: No (business rule defined)

---

## 7. Savings Retention

### 7.1 Savings Goals

**Entity**: savings_goals

**Data**:
- Goal ID (UUID)
- User ID (UUID) or Family ID (UUID)
- Name
- Target amount
- Currency
- Target date
- Current amount
- Progress percentage
- Priority
- Status
- Created timestamp
- Updated timestamp

**Retention Period**: 7 years after completion

**Legal Basis**: Business Rule BR-RET-004

**Deletion Process**:
- Savings goals are soft-deleted (status = CANCELLED)
- Savings goals retained for 7 years after completion
- After 7 years, savings goals can be archived or deleted
- Audit logs retained for 7 years

**Professional Review Required**: No (business rule defined)

### 7.2 Savings Contributions

**Entity**: savings_contributions

**Data**:
- Contribution ID (UUID)
- Goal ID (UUID)
- User ID (UUID)
- Amount
- Currency
- Contribution date
- Created timestamp

**Retention Period**: 7 years after completion

**Legal Basis**: Business Rule BR-RET-004

**Deletion Process**:
- Savings contributions retained for 7 years after goal completion
- After 7 years, savings contributions can be archived or deleted
- Audit logs retained for 7 years

**Professional Review Required**: No (business rule defined)

### 7.3 Savings Progress

**Entity**: savings_progress

**Data**:
- Progress ID (UUID)
- Goal ID (UUID)
- Period
- Current amount
- Required monthly contribution
- Created timestamp

**Retention Period**: 7 years after completion

**Legal Basis**: Business Rule BR-RET-004

**Deletion Process**:
- Savings progress retained for 7 years after goal completion
- After 7 years, savings progress can be archived or deleted
- Audit logs retained for 7 years

**Professional Review Required**: No (business rule defined)

---

## 8. Bills Retention

### 8.1 Bills

**Entity**: bills

**Data**:
- Bill ID (UUID)
- User ID (UUID)
- Family ID (optional)
- Category ID
- Vendor name
- Expected amount
- Currency
- Due date
- Recurring
- Recurring period (optional)
- Status
- Paid date (optional)
- Payment method (optional)
- Created timestamp
- Updated timestamp

**Retention Period**: 7 years

**Legal Basis**: Business Rule BR-RET-005

**Deletion Process**:
- Bills are soft-deleted (status = DELETED)
- Bills retained for 7 years
- After 7 years, bills can be archived or deleted
- Audit logs retained for 7 years

**Professional Review Required**: No (business rule defined)

### 8.2 Bill Categories

**Entity**: bill_categories

**Data**:
- Bill category ID (UUID)
- Category ID
- Category name
- Created timestamp

**Retention Period**: 7 years

**Legal Basis**: Business Rule BR-RET-005

**Deletion Process**:
- Bill categories retained for 7 years
- After 7 years, bill categories can be archived or deleted
- Audit logs retained for 7 years

**Professional Review Required**: No (business rule defined)

### 8.3 Bill Status History

**Entity**: bill_status_history

**Data**:
- Status history ID (UUID)
- Bill ID (UUID)
- Status
- Changed timestamp
- Changed by (user ID or system)

**Retention Period**: 7 years

**Legal Basis**: Business Rule BR-RET-005

**Deletion Process**:
- Bill status history retained for 7 years
- After 7 years, bill status history can be archived or deleted
- Audit logs retained for 7 years

**Professional Review Required**: No (business rule defined)

---

## 9. Notifications Retention

### 9.1 Notifications

**Entity**: notifications

**Data**:
- Notification ID (UUID)
- User ID (UUID)
- Type
- Title
- Message
- Priority
- Is read
- Read timestamp
- Created timestamp

**Retention Period**: 90 days

**Legal Basis**: Compliance Decision Required

**Deletion Process**:
- Notifications retained for 90 days
- After 90 days, notifications can be archived or deleted
- Audit logs retained for 7 years

**Professional Review Required**: Yes (compliance decision required)

### 9.2 Notification Preferences

**Entity**: notification_preferences

**Data**:
- Preference ID (UUID)
- User ID (UUID)
- Push enabled
- Email enabled
- SMS enabled
- WhatsApp enabled
- Created timestamp
- Updated timestamp

**Retention Period**: 90 days

**Legal Basis**: Compliance Decision Required

**Deletion Process**:
- Notification preferences retained for 90 days
- After 90 days, notification preferences can be archived or deleted
- Audit logs retained for 7 years

**Professional Review Required**: Yes (compliance decision required)

### 9.3 Notification Deliveries

**Entity**: notification_deliveries

**Data**:
- Delivery ID (UUID)
- Notification ID (UUID)
- Channel
- Status
- Queued timestamp
- Sent timestamp
- Delivered timestamp
- Failed timestamp
- Error message
- Created timestamp

**Retention Period**: 90 days

**Legal Basis**: Compliance Decision Required

**Deletion Process**:
- Notification deliveries retained for 90 days
- After 90 days, notification deliveries can be archived or deleted
- Audit logs retained for 7 years

**Professional Review Required**: Yes (compliance decision required)

---

## 10. AI Conversations Retention

### 10.1 AI Conversations

**Entity**: ai_conversations

**Data**:
- Conversation ID (UUID)
- User ID (UUID)
- Family ID (optional)
- Title
- Status
- Created timestamp
- Updated timestamp

**Retention Period**: 1 year

**Legal Basis**: Compliance Decision Required

**Deletion Process**:
- AI conversations are soft-deleted
- AI conversations retained for 1 year
- After 1 year, AI conversations can be archived or deleted
- User can delete AI conversations at any time
- Audit logs retained for 7 years

**Professional Review Required**: Yes (compliance decision required)

### 10.2 AI Messages

**Entity**: ai_messages

**Data**:
- Message ID (UUID)
- Conversation ID (UUID)
- Role
- Message type
- Content
- Response type
- Model provider
- Model version
- Prompt version
- Confidence
- Tool result reference (optional)
- Created timestamp

**Retention Period**: 1 year

**Legal Basis**: Compliance Decision Required

**Deletion Process**:
- AI messages retained for 1 year
- After 1 year, AI messages can be archived or deleted
- User can delete AI conversations at any time
- Audit logs retained for 7 years

**Professional Review Required**: Yes (compliance decision required)

### 10.3 AI Tool Invocations

**Entity**: ai_tool_invocations

**Data**:
- Tool invocation ID (UUID)
- Message ID (UUID)
- Tool name
- Tool parameters
- Tool result
- Executed timestamp
- Created timestamp

**Retention Period**: 1 year

**Legal Basis**: Compliance Decision Required

**Deletion Process**:
- AI tool invocations retained for 1 year
- After 1 year, AI tool invocations can be archived or deleted
- Audit logs retained for 7 years

**Professional Review Required**: Yes (compliance decision required)

### 10.4 AI Recommendations

**Entity**: ai_recommendations

**Data**:
- Recommendation ID (UUID)
- Conversation ID (UUID)
- Recommendation type
- Recommendation data
- User action
- Action timestamp
- Created timestamp

**Retention Period**: 1 year

**Legal Basis**: Compliance Decision Required

**Deletion Process**:
- AI recommendations retained for 1 year
- After 1 year, AI recommendations can be archived or deleted
- Audit logs retained for 7 years

**Professional Review Required**: Yes (compliance decision required)

---

## 11. Audit Logs Retention

### 11.1 Audit Logs

**Entity**: audit_logs

**Data**:
- Audit ID (UUID)
- Actor ID
- Actor type
- Action
- Resource type
- Resource ID
- Request ID
- Correlation ID
- Result
- Metadata
- Created timestamp

**Retention Period**: 7 years

**Legal Basis**: Business Rule BR-RET-001

**Deletion Process**:
- Audit logs are immutable (no UPDATE, no DELETE)
- Audit logs retained for 7 years
- After 7 years, audit logs can be archived
- Audit logs cannot be deleted (regulatory requirement)

**Professional Review Required**: No (business rule defined)

---

## 12. Security Logs Retention

### 12.1 Application Logs

**Data**:
- Application logs
- Error logs
- Performance logs
- Security logs

**Retention Period**: 90 days

**Legal Basis**: Compliance Decision Required

**Deletion Process**:
- Application logs retained for 90 days
- After 90 days, application logs can be archived or deleted
- No sensitive data in logs
- Log masking for PII and financial data

**Professional Review Required**: Yes (compliance decision required)

### 12.2 Security Logs

**Data**:
- Security event logs
- Failed login attempts
- Authorization failures
- Cross-family access attempts
- AI tool abuse
- Prompt injection attempts

**Retention Period**: 90 days

**Legal Basis**: Compliance Decision Required

**Deletion Process**:
- Security logs retained for 90 days
- After 90 days, security logs can be archived or deleted
- No sensitive data in logs
- Log masking for PII and financial data

**Professional Review Required**: Yes (compliance decision required)

---

## 13. Consent Records Retention

### 13.1 Consent Records

**Entity**: user_preferences (consent fields)

**Data**:
- User ID (UUID)
- Data processing consent
- AI insights consent
- Communication consent
- Marketing consent
- Consent timestamp
- Consent version

**Retention Period**: 7 years

**Legal Basis**: Business Rule BR-RET-001

**Deletion Process**:
- Consent records retained for 7 years after deletion
- After 7 years, consent records can be archived or deleted
- Audit logs retained for 7 years

**Professional Review Required**: No (business rule defined)

---

## 14. Compliance Decision Required Summary

### 14.1 Areas Requiring Compliance Decision

| Data Category | Entity | Current Retention | Required Action |
|---------------|--------|-------------------|----------------|
| Notifications | notifications | 90 days | Compliance Decision Required |
| Notifications | notification_preferences | 90 days | Compliance Decision Required |
| Notifications | notification_deliveries | 90 days | Compliance Decision Required |
| AI Conversations | ai_conversations | 1 year | Compliance Decision Required |
| AI Conversations | ai_messages | 1 year | Compliance Decision Required |
| AI Conversations | ai_tool_invocations | 1 year | Compliance Decision Required |
| AI Conversations | ai_recommendations | 1 year | Compliance Decision Required |
| Security Logs | application logs | 90 days | Compliance Decision Required |
| Security Logs | security logs | 90 days | Compliance Decision Required |

### 14.2 Professional Review Required

**Professional Review Required**: Yes (all areas marked "Compliance Decision Required")

**Review Areas**:
- Notifications retention period (90 days)
- AI conversations retention period (1 year)
- Security logs retention period (90 days)

---

## 15. Legal Hold

### 15.1 Legal Hold Process

**Legal Hold Trigger**:
- Legal hold request from legal team
- Litigation notice
- Regulatory investigation
- Audit request

**Legal Hold Process**:
1. Legal hold places data on hold
2. Data cannot be deleted during legal hold
3. Legal hold tracked in system
4. Legal hold released when authorized

**Legal Hold Tracking**:
- Legal hold ID
- Legal hold reason
- Legal hold start date
- Legal hold end date
- Legal hold status
- Legal hold authorized by

---

## 16. Data Deletion Process

### 16.1 User-Requested Deletion

**Deletion Request**:
1. User submits account deletion request
2. System confirms deletion request
3. System soft-deletes user account (deleted_at timestamp)
4. System deletes sensitive data immediately
5. System anonymizes data after retention period
6. System retains audit logs for 7 years

**Deletion Timeline**:
- Immediate: Soft-delete user account
- Immediate: Delete sensitive data (passwords, tokens)
- 7 years: Anonymize user data
- 7 years: Archive or delete user data
- 7 years: Archive audit logs

### 16.2 Automatic Deletion

**Automatic Deletion**:
- Notifications: Deleted after 90 days
- AI conversations: Deleted after 1 year
- Security logs: Deleted after 90 days
- Application logs: Deleted after 90 days

### 16.3 Archive Deletion

**Archive Deletion**:
- Transactions: Archived or deleted after 7 years
- Budgets: Archived or deleted after 7 years
- Allocations: Archived or deleted after 7 years
- Savings: Archived or deleted after 7 years after completion
- Bills: Archived or deleted after 7 years
- Audit logs: Archived after 7 years (cannot be deleted)

---

## 17. Conclusion

The NeoWallet Data Retention Matrix defines retention periods for all data entities. Most retention periods are defined in business rules. Areas requiring compliance/legal decision are identified.

**IMPORTANT DISCLAIMER**: This document does not invent statutory retention periods. Where retention is legally uncertain, it is marked "Compliance Decision Required."

**Next Steps**:
1. Create Compliance Traceability
2. Create Compliance Gap Register
3. Create Regulatory Review Register
4. Create Compliance Validation Report
