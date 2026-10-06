# NeoWallet ER Model v1

## Executive Summary

This document defines the complete Entity-Relationship (ER) model for NeoWallet MVP using Mermaid syntax. The model covers all 35 tables across 14 domains with their relationships, cardinality, primary keys, and foreign keys.

**Total Tables**: 35
**Total Relationships**: 40+
**Date**: August 18, 2026
**Status**: Ready for Product Owner Approval

---

## 1. Complete ER Model

```mermaid
erDiagram
    %% Identity Domain
    users ||--o{ user_credentials : "has"
    users ||--o{ user_sessions : "has"
    users ||--o{ user_devices : "has"
    users ||--o{ otp_challenges : "has"
    
    %% User Domain
    users ||--|| user_profiles : "has"
    users ||--|| user_preferences : "has"
    users ||--o{ user_addresses : "has"
    
    %% Family Domain
    users ||--o{ families : "owns"
    families ||--o{ family_members : "has"
    users ||--o{ family_members : "joins"
    families ||--o{ family_invitations : "sends"
    users ||--o{ family_invitations : "invites"
    
    %% User Profile to Family
    user_profiles }o--|| families : "belongs to"
    
    %% Financial Overview Domain
    users ||--o{ financial_overviews : "has"
    families ||--o{ financial_overviews : "has"
    financial_overviews ||--o{ financial_periods : "has"
    financial_overviews ||--o{ financial_allocations : "has"
    
    %% Transaction Domain
    users ||--o{ transactions : "has"
    families ||--o{ transactions : "belongs to"
    family_members ||--o{ transactions : "records"
    categories ||--o{ transactions : "categorizes"
    
    %% Category Domain
    categories ||--o{ categories : "parent of"
    
    %% Budget Domain
    users ||--o{ budgets : "has"
    families ||--o{ budgets : "has"
    budgets ||--o{ budget_categories : "has"
    categories ||--o{ budget_categories : "categorizes"
    budgets ||--o{ budget_periods : "has"
    budgets ||--o{ budget_recommendations : "has"
    users ||--o{ budget_recommendations : "receives"
    families ||--o{ budget_recommendations : "receives"
    
    %% Allocation Domain
    users ||--o{ allocation_recommendations : "receives"
    families ||--o{ allocation_recommendations : "receives"
    allocation_recommendations ||--o{ allocation_categories : "has"
    categories ||--o{ allocation_categories : "categorizes"
    allocation_recommendations ||--o{ allocation_versions : "has"
    allocation_recommendations ||--o{ allocation_approvals : "has"
    users ||--o{ allocation_approvals : "approves"
    
    %% Savings Domain
    users ||--o{ savings_goals : "has"
    families ||--o{ savings_goals : "has"
    savings_goals ||--o{ savings_contributions : "has"
    users ||--o{ savings_contributions : "contributes"
    savings_goals ||--o{ savings_progress : "has"
    
    %% Bill Domain
    users ||--o{ bills : "has"
    families ||--o{ bills : "belongs to"
    family_members ||--o{ bills : "assigned to"
    bills ||--o{ bill_status_history : "has"
    users ||--o{ bill_status_history : "changes"
    
    %% Notification Domain
    users ||--o{ notifications : "receives"
    notifications ||--o{ notification_deliveries : "delivered via"
    users ||--|| notification_preferences : "has"
    
    %% Financial Health Domain
    users ||--o{ financial_health_scores : "has"
    families ||--o{ financial_health_scores : "has"
    financial_health_scores ||--o{ financial_health_factors : "has"
    financial_health_scores ||--o{ financial_health_calculations : "has"
    
    %% AI Domain
    users ||--o{ ai_conversations : "has"
    families ||--o{ ai_conversations : "has"
    ai_conversations ||--o{ ai_messages : "contains"
    ai_conversations ||--o{ ai_tool_invocations : "invokes"
    ai_messages ||--o{ ai_tool_invocations : "triggers"
    ai_conversations ||--o{ ai_recommendations : "generates"
    ai_messages ||--o{ ai_recommendations : "contains"
    
    %% Audit Domain
    users ||--o{ audit_logs : "performs"
    
    %% Configuration Domain
    users ||--o{ user_configurations : "has"
```

---

## 2. Identity Domain ER Model

```mermaid
erDiagram
    users {
        UUID user_id PK
        VARCHAR email UK
        VARCHAR phone_number UK
        VARCHAR password_hash
        VARCHAR first_name
        VARCHAR last_name
        VARCHAR account_status
        BOOLEAN email_verified
        BOOLEAN phone_verified
        INTEGER failed_login_attempts
        TIMESTAMP locked_until
        TIMESTAMP created_at
        TIMESTAMP updated_at
        TIMESTAMP deleted_at
    }
    
    user_credentials {
        UUID credential_id PK
        UUID user_id FK
        VARCHAR credential_type
        VARCHAR credential_value
        BOOLEAN is_active
        TIMESTAMP expires_at
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }
    
    user_sessions {
        UUID session_id PK
        UUID user_id FK
        UUID device_id FK
        VARCHAR refresh_token_hash
        VARCHAR ip_address
        TEXT user_agent
        TIMESTAMP last_active_at
        TIMESTAMP expires_at
        TIMESTAMP revoked_at
        TIMESTAMP created_at
    }
    
    user_devices {
        UUID device_id PK
        UUID user_id FK
        VARCHAR device_name
        VARCHAR device_type
        VARCHAR device_token
        VARCHAR platform
        VARCHAR os_version
        VARCHAR app_version
        TIMESTAMP last_active_at
        BOOLEAN is_active
        TIMESTAMP created_at
    }
    
    otp_challenges {
        UUID otp_id PK
        UUID user_id FK
        VARCHAR email
        VARCHAR phone_number
        VARCHAR otp_hash
        VARCHAR purpose
        TIMESTAMP expires_at
        INTEGER attempts
        TIMESTAMP verified_at
        TIMESTAMP created_at
    }
    
    users ||--o{ user_credentials : "has"
    users ||--o{ user_sessions : "has"
    user_sessions }o--|| user_devices : "uses"
    users ||--o{ user_devices : "has"
    users ||--o{ otp_challenges : "has"
```

---

## 3. User Domain ER Model

```mermaid
erDiagram
    users {
        UUID user_id PK
        VARCHAR email UK
        VARCHAR password_hash
        VARCHAR first_name
        VARCHAR last_name
        VARCHAR account_status
        TIMESTAMP created_at
        TIMESTAMP updated_at
        TIMESTAMP deleted_at
    }
    
    user_profiles {
        UUID profile_id PK
        UUID user_id FK,UK
        VARCHAR profile_image_url
        DATE date_of_birth
        VARCHAR timezone
        VARCHAR locale
        VARCHAR currency
        UUID family_id FK
        VARCHAR family_role
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }
    
    user_preferences {
        UUID preference_id PK
        UUID user_id FK,UK
        BOOLEAN budget_alerts_enabled
        INTEGER budget_alert_threshold_percentage
        BOOLEAN bill_reminders_enabled
        INTEGER[] bill_reminder_days
        BOOLEAN savings_updates_enabled
        VARCHAR savings_updates_frequency
        BOOLEAN financial_health_updates_enabled
        VARCHAR financial_health_updates_frequency
        VARCHAR ai_response_style
        VARCHAR ai_language
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }
    
    user_addresses {
        UUID address_id PK
        UUID user_id FK
        VARCHAR address_type
        VARCHAR address_line1
        VARCHAR address_line2
        VARCHAR city
        VARCHAR state
        VARCHAR postal_code
        VARCHAR country_code
        BOOLEAN is_default
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }
    
    users ||--|| user_profiles : "has"
    users ||--|| user_preferences : "has"
    users ||--o{ user_addresses : "has"
```

---

## 4. Family Domain ER Model

```mermaid
erDiagram
    users {
        UUID user_id PK
        VARCHAR email
        VARCHAR first_name
        VARCHAR last_name
    }
    
    families {
        UUID family_id PK
        VARCHAR name
        VARCHAR currency
        UUID owner_id FK
        INTEGER member_count
        TIMESTAMP created_at
        TIMESTAMP updated_at
        TIMESTAMP deleted_at
    }
    
    family_members {
        UUID member_id PK
        UUID family_id FK
        UUID user_id FK
        VARCHAR role
        TIMESTAMP joined_at
        TIMESTAMP left_at
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }
    
    family_invitations {
        UUID invitation_id PK
        UUID family_id FK
        UUID inviter_id FK
        VARCHAR email
        VARCHAR role
        VARCHAR token UK
        TIMESTAMP expires_at
        TIMESTAMP accepted_at
        TIMESTAMP rejected_at
        TIMESTAMP created_at
    }
    
    family_roles {
        VARCHAR role_id PK
        VARCHAR role_name
        TEXT description
        JSONB permissions
        TIMESTAMP created_at
    }
    
    users ||--o{ families : "owns"
    families ||--o{ family_members : "has"
    users ||--o{ family_members : "joins"
    families ||--o{ family_invitations : "sends"
    users ||--o{ family_invitations : "invites"
    family_members }o--|| family_roles : "has"
```

---

## 5. Financial Overview Domain ER Model

```mermaid
erDiagram
    users {
        UUID user_id PK
    }
    
    families {
        UUID family_id PK
    }
    
    financial_overviews {
        UUID overview_id PK
        UUID user_id FK
        UUID family_id FK
        NUMERIC planning_income
        NUMERIC mandatory_commitments
        NUMERIC essential_allocation
        NUMERIC variable_allocation
        NUMERIC savings_allocation
        NUMERIC emergency_allocation
        NUMERIC discretionary_planning
        NUMERIC committed_amount
        NUMERIC pending_payments
        NUMERIC actual_transactions
        NUMERIC available_financial_capacity
        VARCHAR currency
        VARCHAR period
        TIMESTAMP calculated_at
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }
    
    financial_periods {
        UUID period_id PK
        UUID overview_id FK
        DATE period_start
        DATE period_end
        VARCHAR period_type
        BOOLEAN is_active
        TIMESTAMP created_at
    }
    
    financial_allocations {
        UUID allocation_id PK
        UUID overview_id FK
        UUID category_id FK
        VARCHAR category_name
        NUMERIC allocated_amount
        NUMERIC actual_amount
        NUMERIC utilization_percentage
        VARCHAR currency
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }
    
    categories {
        UUID category_id PK
        VARCHAR name
        UUID parent_category_id FK
        VARCHAR category_type
        VARCHAR priority
        VARCHAR icon
        VARCHAR color
        BOOLEAN is_system
        BOOLEAN is_active
        UUID created_by FK
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }
    
    users ||--o{ financial_overviews : "has"
    families ||--o{ financial_overviews : "has"
    financial_overviews ||--o{ financial_periods : "has"
    financial_overviews ||--o{ financial_allocations : "has"
    categories ||--o{ financial_allocations : "categorizes"
    categories ||--o{ categories : "parent of"
```

---

## 6. Transaction Domain ER Model

```mermaid
erDiagram
    users {
        UUID user_id PK
    }
    
    families {
        UUID family_id PK
    }
    
    family_members {
        UUID member_id PK
        UUID user_id FK
        UUID family_id FK
        VARCHAR role
    }
    
    categories {
        UUID category_id PK
        VARCHAR name
        VARCHAR category_type
    }
    
    transactions {
        UUID transaction_id PK
        UUID user_id FK
        UUID family_id FK
        UUID family_member_id FK
        VARCHAR type
        UUID category_id FK
        VARCHAR category_name
        NUMERIC amount
        VARCHAR currency
        TEXT description
        DATE transaction_date
        DATE posted_date
        VARCHAR status
        BOOLEAN is_recurring
        VARCHAR recurring_pattern
        VARCHAR external_reference
        VARCHAR source
        TIMESTAMP created_at
        TIMESTAMP updated_at
        TIMESTAMP deleted_at
    }
    
    users ||--o{ transactions : "has"
    families ||--o{ transactions : "belongs to"
    family_members ||--o{ transactions : "records"
    categories ||--o{ transactions : "categorizes"
```

---

## 7. Budget Domain ER Model

```mermaid
erDiagram
    users {
        UUID user_id PK
    }
    
    families {
        UUID family_id PK
    }
    
    categories {
        UUID category_id PK
        VARCHAR name
        VARCHAR category_type
    }
    
    budgets {
        UUID budget_id PK
        UUID user_id FK
        UUID family_id FK
        VARCHAR name
        VARCHAR period
        VARCHAR currency
        NUMERIC total_limit
        NUMERIC total_spent
        NUMERIC utilization_percentage
        VARCHAR status
        INTEGER version
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }
    
    budget_categories {
        UUID budget_category_id PK
        UUID budget_id FK
        UUID category_id FK
        VARCHAR category_name
        NUMERIC limit_amount
        NUMERIC spent_amount
        NUMERIC utilization_percentage
        VARCHAR priority
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }
    
    budget_periods {
        UUID period_id PK
        UUID budget_id FK
        DATE period_start
        DATE period_end
        VARCHAR period_type
        BOOLEAN is_active
        TIMESTAMP created_at
    }
    
    budget_recommendations {
        UUID recommendation_id PK
        UUID budget_id FK
        UUID user_id FK
        UUID family_id FK
        VARCHAR period
        VARCHAR currency
        JSONB recommended_allocation
        JSONB user_modification
        JSONB final_approved_budget
        VARCHAR status
        VARCHAR confidence
        INTEGER historical_months_used
        TIMESTAMP generated_at
        TIMESTAMP approved_at
    }
    
    users ||--o{ budgets : "has"
    families ||--o{ budgets : "has"
    budgets ||--o{ budget_categories : "has"
    categories ||--o{ budget_categories : "categorizes"
    budgets ||--o{ budget_periods : "has"
    budgets ||--o{ budget_recommendations : "has"
    users ||--o{ budget_recommendations : "receives"
    families ||--o{ budget_recommendations : "receives"
```

---

## 8. Allocation Domain ER Model

```mermaid
erDiagram
    users {
        UUID user_id PK
    }
    
    families {
        UUID family_id PK
    }
    
    categories {
        UUID category_id PK
        VARCHAR name
        VARCHAR category_type
    }
    
    allocation_recommendations {
        UUID recommendation_id PK
        UUID user_id FK
        UUID family_id FK
        VARCHAR period
        VARCHAR currency
        JSONB system_recommendation
        JSONB user_modification
        JSONB final_approved_allocation
        VARCHAR status
        VARCHAR confidence
        INTEGER historical_months_used
        TIMESTAMP generated_at
        TIMESTAMP approved_at
    }
    
    allocation_categories {
        UUID allocation_category_id PK
        UUID recommendation_id FK
        UUID category_id FK
        VARCHAR category_name
        NUMERIC recommended_amount
        NUMERIC modified_amount
        NUMERIC final_amount
        VARCHAR priority
        TIMESTAMP created_at
    }
    
    allocation_versions {
        UUID version_id PK
        UUID recommendation_id FK
        INTEGER version_number
        JSONB allocation_data
        TEXT change_reason
        TIMESTAMP created_at
    }
    
    allocation_approvals {
        UUID approval_id PK
        UUID recommendation_id FK
        UUID user_id FK
        VARCHAR action
        JSONB approval_data
        TIMESTAMP created_at
    }
    
    users ||--o{ allocation_recommendations : "receives"
    families ||--o{ allocation_recommendations : "receives"
    allocation_recommendations ||--o{ allocation_categories : "has"
    categories ||--o{ allocation_categories : "categorizes"
    allocation_recommendations ||--o{ allocation_versions : "has"
    allocation_recommendations ||--o{ allocation_approvals : "has"
    users ||--o{ allocation_approvals : "approves"
```

---

## 9. Savings Domain ER Model

```mermaid
erDiagram
    users {
        UUID user_id PK
    }
    
    families {
        UUID family_id PK
    }
    
    savings_goals {
        UUID goal_id PK
        UUID user_id FK
        UUID family_id FK
        VARCHAR name
        NUMERIC target_amount
        NUMERIC current_amount
        DATE target_date
        VARCHAR currency
        VARCHAR priority
        VARCHAR category
        VARCHAR status
        NUMERIC progress_percentage
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }
    
    savings_contributions {
        UUID contribution_id PK
        UUID goal_id FK
        UUID user_id FK
        NUMERIC amount
        VARCHAR currency
        DATE contribution_date
        TEXT notes
        TIMESTAMP created_at
    }
    
    savings_progress {
        UUID progress_id PK
        UUID goal_id FK
        NUMERIC current_amount
        NUMERIC progress_percentage
        NUMERIC remaining_amount
        INTEGER months_remaining
        NUMERIC required_monthly_contribution
        NUMERIC actual_monthly_contribution
        BOOLEAN on_track
        VARCHAR status
        TIMESTAMP calculated_at
    }
    
    users ||--o{ savings_goals : "has"
    families ||--o{ savings_goals : "has"
    savings_goals ||--o{ savings_contributions : "has"
    users ||--o{ savings_contributions : "contributes"
    savings_goals ||--o{ savings_progress : "has"
```

---

## 10. Bill Domain ER Model

```mermaid
erDiagram
    users {
        UUID user_id PK
    }
    
    families {
        UUID family_id PK
    }
    
    family_members {
        UUID member_id PK
        UUID user_id FK
        UUID family_id FK
        VARCHAR role
    }
    
    bills {
        UUID bill_id PK
        UUID user_id FK
        UUID family_id FK
        UUID family_member_id FK
        VARCHAR name
        NUMERIC amount
        VARCHAR currency
        DATE due_date
        VARCHAR category
        BOOLEAN is_recurring
        VARCHAR recurring_period
        VARCHAR vendor
        TEXT notes
        VARCHAR status
        DATE paid_date
        VARCHAR payment_method
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }
    
    bill_categories {
        UUID category_id PK
        VARCHAR name
        TEXT description
        VARCHAR icon
        VARCHAR color
        BOOLEAN is_system
        TIMESTAMP created_at
    }
    
    bill_status_history {
        UUID history_id PK
        UUID bill_id FK
        VARCHAR old_status
        VARCHAR new_status
        UUID changed_by FK
        TIMESTAMP changed_at
        TEXT notes
    }
    
    users ||--o{ bills : "has"
    families ||--o{ bills : "belongs to"
    family_members ||--o{ bills : "assigned to"
    bills ||--o{ bill_status_history : "has"
    users ||--o{ bill_status_history : "changes"
```

---

## 11. Notification Domain ER Model

```mermaid
erDiagram
    users {
        UUID user_id PK
    }
    
    notifications {
        UUID notification_id PK
        UUID user_id FK
        VARCHAR type
        VARCHAR title
        TEXT message
        JSONB data
        BOOLEAN is_read
        TIMESTAMP read_at
        TIMESTAMP created_at
    }
    
    notification_deliveries {
        UUID delivery_id PK
        UUID notification_id FK
        VARCHAR channel
        VARCHAR status
        TIMESTAMP queued_at
        TIMESTAMP sent_at
        TIMESTAMP delivered_at
        TIMESTAMP failed_at
        TEXT error_message
        VARCHAR provider_reference
    }
    
    notification_preferences {
        UUID preference_id PK
        UUID user_id FK,UK
        BOOLEAN budget_alerts_enabled
        INTEGER budget_alert_threshold_percentage
        BOOLEAN bill_reminders_enabled
        INTEGER[] bill_reminder_days
        BOOLEAN savings_updates_enabled
        VARCHAR savings_updates_frequency
        BOOLEAN financial_health_updates_enabled
        VARCHAR financial_health_updates_frequency
        BOOLEAN push_enabled
        BOOLEAN email_enabled
        BOOLEAN sms_enabled
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }
    
    users ||--o{ notifications : "receives"
    notifications ||--o{ notification_deliveries : "delivered via"
    users ||--|| notification_preferences : "has"
```

---

## 12. Financial Health Domain ER Model

```mermaid
erDiagram
    users {
        UUID user_id PK
    }
    
    families {
        UUID family_id PK
    }
    
    financial_health_scores {
        UUID score_id PK
        UUID user_id FK
        UUID family_id FK
        INTEGER overall_score
        VARCHAR score_label
        VARCHAR confidence
        VARCHAR status
        TIMESTAMP calculated_at
    }
    
    financial_health_factors {
        UUID factor_id PK
        UUID score_id FK
        VARCHAR factor_name
        INTEGER factor_score
        NUMERIC weight
        NUMERIC contribution
        TIMESTAMP created_at
    }
    
    financial_health_calculations {
        UUID calculation_id PK
        UUID score_id FK
        VARCHAR calculation_version
        JSONB input_snapshot
        JSONB factor_scores
        JSONB weights
        TIMESTAMP calculated_at
    }
    
    users ||--o{ financial_health_scores : "has"
    families ||--o{ financial_health_scores : "has"
    financial_health_scores ||--o{ financial_health_factors : "has"
    financial_health_scores ||--o{ financial_health_calculations : "has"
```

---

## 13. AI Domain ER Model

```mermaid
erDiagram
    users {
        UUID user_id PK
    }
    
    families {
        UUID family_id PK
    }
    
    ai_conversations {
        UUID conversation_id PK
        UUID user_id FK
        UUID family_id FK
        VARCHAR title
        INTEGER message_count
        TIMESTAMP last_message_at
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }
    
    ai_messages {
        UUID message_id PK
        UUID conversation_id FK
        VARCHAR role
        TEXT content
        VARCHAR message_type
        VARCHAR response_type
        VARCHAR confidence
        VARCHAR model_provider
        VARCHAR model_version
        VARCHAR prompt_version
        TIMESTAMP created_at
    }
    
    ai_tool_invocations {
        UUID invocation_id PK
        UUID conversation_id FK
        UUID message_id FK
        VARCHAR tool_name
        JSONB tool_parameters
        VARCHAR tool_result_reference
        VARCHAR status
        TIMESTAMP executed_at
        TEXT error_message
    }
    
    ai_recommendations {
        UUID recommendation_id PK
        UUID conversation_id FK
        UUID message_id FK
        VARCHAR recommendation_type
        JSONB recommendation_data
        VARCHAR user_action
        TIMESTAMP created_at
    }
    
    users ||--o{ ai_conversations : "has"
    families ||--o{ ai_conversations : "has"
    ai_conversations ||--o{ ai_messages : "contains"
    ai_conversations ||--o{ ai_tool_invocations : "invokes"
    ai_messages ||--o{ ai_tool_invocations : "triggers"
    ai_conversations ||--o{ ai_recommendations : "generates"
    ai_messages ||--o{ ai_recommendations : "contains"
```

---

## 14. Audit Domain ER Model

```mermaid
erDiagram
    users {
        UUID user_id PK
    }
    
    audit_logs {
        UUID audit_id PK
        UUID actor_id FK
        VARCHAR actor_type
        VARCHAR action
        VARCHAR resource_type
        UUID resource_id
        UUID request_id
        UUID correlation_id
        VARCHAR result
        JSONB metadata
        TIMESTAMP created_at
    }
    
    users ||--o{ audit_logs : "performs"
```

---

## 15. Configuration Domain ER Model

```mermaid
erDiagram
    users {
        UUID user_id PK
    }
    
    system_configurations {
        UUID config_id PK
        VARCHAR config_key UK
        TEXT config_value
        VARCHAR config_type
        TEXT description
        BOOLEAN is_sensitive
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }
    
    user_configurations {
        UUID config_id PK
        UUID user_id FK
        VARCHAR config_key
        TEXT config_value
        VARCHAR config_type
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }
    
    users ||--o{ user_configurations : "has"
```

---

## 16. Relationship Summary

### 16.1 Cardinality Legend

- `||--||`: One-to-One (1:1)
- `||--o{`: One-to-Many (1:N)
- `}o--||`: Many-to-One (N:1)
- `}o--o{`: Many-to-Many (N:N)

### 16.2 Relationship Count by Domain

| Domain | Relationships |
|--------|---------------|
| Identity | 4 |
| User | 3 |
| Family | 5 |
| Financial Overview | 6 |
| Transaction | 4 |
| Budget | 7 |
| Allocation | 6 |
| Savings | 5 |
| Bill | 5 |
| Notification | 3 |
| Financial Health | 3 |
| AI | 6 |
| Audit | 1 |
| Configuration | 1 |
| **TOTAL** | **59** |

---

## 17. Key Relationships

### 17.1 User to Family

- `users` → `families` (1:N): User can own multiple families
- `users` → `family_members` (1:N): User can be member of multiple families
- `families` → `family_members` (1:N): Family has multiple members
- `user_profiles.family_id` → `families` (N:1): User profile belongs to one family

### 17.2 Financial Data Ownership

- All financial tables have `user_id` or `family_id`
- Server-side authorization required for cross-family access
- Family membership checked before data access

### 17.3 AI Security

- `ai_conversations` → `ai_messages` (1:N): Conversation has messages
- `ai_messages` → `ai_tool_invocations` (1:N): Messages trigger tool invocations
- AI has NO direct database access
- AI accesses data only through authorized application tools/APIs

### 17.4 Audit Trail

- `audit_logs` → `users` (N:1): Audit logs track user actions
- Audit logs are immutable (no UPDATE, no DELETE)
- ON DELETE RESTRICT prevents deletion of users with audit logs

---

## 18. Conclusion

The NeoWallet ER model defines 35 tables across 14 domains with 59 relationships. The model emphasizes:

- **Financial Safety**: No custodial wallet balance tables
- **Planning vs Actual**: Clear separation of planning data and actual transactions
- **Family Isolation**: Server-side authorization with family membership
- **Audit Trail**: Immutable audit logs for sensitive operations
- **AI Security**: AI has no direct database access

**Next Steps**:
1. Create Data Dictionary
2. Create Traceability Matrix
3. Create Validation Report
