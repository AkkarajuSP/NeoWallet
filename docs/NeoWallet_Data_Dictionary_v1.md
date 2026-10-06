# NeoWallet Data Dictionary v1

## Executive Summary

This document provides a complete data dictionary for NeoWallet MVP, defining every entity, column, data type, constraints, and relationships across all 35 tables.

**Total Tables**: 35
**Total Columns**: 300+
**Date**: August 18, 2026
**Status**: Ready for Product Owner Approval

---

## 1. Identity Domain

### 1.1 users

| Column | Data Type | Nullable | Default | Description | Constraint | PII? | Sensitive? | Index | Relationship |
|--------|-----------|----------|---------|-------------|------------|------|------------|-------|-------------|
| user_id | UUID | NO | gen_random_uuid() | Primary key | PK | NO | NO | - | - |
| email | VARCHAR(255) | NO | - | User email address | UNIQUE | YES | NO | idx_users_email | - |
| phone_number | VARCHAR(20) | YES | - | User phone number | UNIQUE | YES | NO | - | - |
| password_hash | VARCHAR(255) | NO | - | Bcrypt hash of password | - | NO | YES | - | - |
| first_name | VARCHAR(100) | NO | - | User first name | - | YES | NO | - | - |
| last_name | VARCHAR(100) | NO | - | User last name | - | YES | NO | - | - |
| account_status | VARCHAR(20) | NO | 'ACTIVE' | Account status | CHECK: ACTIVE, SUSPENDED, LOCKED, DELETED | NO | NO | idx_users_status | - |
| email_verified | BOOLEAN | NO | FALSE | Email verification status | - | NO | NO | - | - |
| phone_verified | BOOLEAN | NO | FALSE | Phone verification status | - | NO | NO | - | - |
| failed_login_attempts | INTEGER | NO | 0 | Failed login attempt count | - | NO | NO | - | - |
| locked_until | TIMESTAMP WITH TIME ZONE | YES | - | Account lockout expiration | - | NO | NO | - | - |
| created_at | TIMESTAMP WITH TIME ZONE | NO | CURRENT_TIMESTAMP | Record creation timestamp | - | NO | NO | - | - |
| updated_at | TIMESTAMP WITH TIME ZONE | NO | CURRENT_TIMESTAMP | Record update timestamp | - | NO | NO | - | - |
| deleted_at | TIMESTAMP WITH TIME ZONE | YES | - | Soft delete timestamp | - | NO | NO | - | - |

### 1.2 user_credentials

| Column | Data Type | Nullable | Default | Description | Constraint | PII? | Sensitive? | Index | Relationship |
|--------|-----------|----------|---------|-------------|------------|------|------------|-------|-------------|
| credential_id | UUID | NO | gen_random_uuid() | Primary key | PK | NO | NO | - | - |
| user_id | UUID | NO | - | User reference | FK: users(user_id) ON DELETE CASCADE | YES | NO | idx_user_credentials_user_id | users |
| credential_type | VARCHAR(20) | NO | - | Credential type | CHECK: PASSWORD, SOCIAL, OTP | NO | NO | idx_user_credentials_type | - |
| credential_value | VARCHAR(255) | NO | - | Hashed credential value | - | NO | YES | - | - |
| is_active | BOOLEAN | NO | TRUE | Credential active status | - | NO | NO | - | - |
| expires_at | TIMESTAMP WITH TIME ZONE | YES | - | Credential expiration | - | NO | NO | - | - |
| created_at | TIMESTAMP WITH TIME ZONE | NO | CURRENT_TIMESTAMP | Record creation timestamp | - | NO | NO | - | - |
| updated_at | TIMESTAMP WITH TIME ZONE | NO | CURRENT_TIMESTAMP | Record update timestamp | - | NO | NO | - | - |

### 1.3 user_sessions

| Column | Data Type | Nullable | Default | Description | Constraint | PII? | Sensitive? | Index | Relationship |
|--------|-----------|----------|---------|-------------|------------|------|------------|-------|-------------|
| session_id | UUID | NO | gen_random_uuid() | Primary key | PK | NO | NO | - | - |
| user_id | UUID | NO | - | User reference | FK: users(user_id) ON DELETE CASCADE | YES | NO | idx_user_sessions_user_id | users |
| device_id | UUID | YES | - | Device reference | FK: user_devices(device_id) ON DELETE SET NULL | NO | NO | idx_user_sessions_device_id | user_devices |
| refresh_token_hash | VARCHAR(255) | NO | - | Hashed refresh token | - | NO | YES | - | - |
| ip_address | VARCHAR(45) | YES | - | Session IP address | - | YES | NO | - | - |
| user_agent | TEXT | YES | - | Session user agent | - | NO | NO | - | - |
| last_active_at | TIMESTAMP WITH TIME ZONE | NO | CURRENT_TIMESTAMP | Last activity timestamp | - | NO | NO | - | - |
| expires_at | TIMESTAMP WITH TIME ZONE | NO | - | Session expiration | - | NO | NO | idx_user_sessions_expires_at | - |
| revoked_at | TIMESTAMP WITH TIME ZONE | YES | - | Session revocation timestamp | - | NO | NO | - | - |
| created_at | TIMESTAMP WITH TIME ZONE | NO | CURRENT_TIMESTAMP | Record creation timestamp | - | NO | NO | - | - |

### 1.4 user_devices

| Column | Data Type | Nullable | Default | Description | Constraint | PII? | Sensitive? | Index | Relationship |
|--------|-----------|----------|---------|-------------|------------|------|------------|-------|-------------|
| device_id | UUID | NO | gen_random_uuid() | Primary key | PK | NO | NO | - | - |
| user_id | UUID | NO | - | User reference | FK: users(user_id) ON DELETE CASCADE | YES | NO | idx_user_devices_user_id | users |
| device_name | VARCHAR(100) | YES | - | Device name | - | NO | NO | - | - |
| device_type | VARCHAR(20) | NO | - | Device type | CHECK: ANDROID, IOS, WEB | NO | NO | idx_user_devices_type | - |
| device_token | VARCHAR(500) | YES | - | Push notification token | - | NO | YES | idx_user_devices_token | - |
| platform | VARCHAR(20) | YES | - | Platform name | - | NO | NO | - | - |
| os_version | VARCHAR(50) | YES | - | OS version | - | NO | NO | - | - |
| app_version | VARCHAR(20) | YES | - | App version | - | NO | NO | - | - |
| last_active_at | TIMESTAMP WITH TIME ZONE | NO | CURRENT_TIMESTAMP | Last activity timestamp | - | NO | NO | - | - |
| is_active | BOOLEAN | NO | TRUE | Device active status | - | NO | NO | - | - |
| created_at | TIMESTAMP WITH TIME ZONE | NO | CURRENT_TIMESTAMP | Record creation timestamp | - | NO | NO | - | - |

### 1.5 otp_challenges

| Column | Data Type | Nullable | Default | Description | Constraint | PII? | Sensitive? | Index | Relationship |
|--------|-----------|----------|---------|-------------|------------|------|------------|-------|-------------|
| otp_id | UUID | NO | gen_random_uuid() | Primary key | PK | NO | NO | - | - |
| user_id | UUID | YES | - | User reference | FK: users(user_id) ON DELETE CASCADE | YES | NO | idx_otp_challenges_user_id | users |
| email | VARCHAR(255) | YES | - | Email for OTP | - | YES | NO | idx_otp_challenges_email | - |
| phone_number | VARCHAR(20) | YES | - | Phone for OTP | - | YES | NO | idx_otp_challenges_phone | - |
| otp_hash | VARCHAR(255) | NO | - | Hashed OTP code | - | NO | YES | - | - |
| purpose | VARCHAR(20) | NO | - | OTP purpose | CHECK: REGISTRATION, LOGIN, RESET | NO | NO | - | - |
| expires_at | TIMESTAMP WITH TIME ZONE | NO | - | OTP expiration | - | NO | NO | idx_otp_challenges_expires_at | - |
| attempts | INTEGER | NO | 0 | OTP retry attempts | - | NO | NO | - | - |
| verified_at | TIMESTAMP WITH TIME ZONE | YES | - | OTP verification timestamp | - | NO | NO | - | - |
| created_at | TIMESTAMP WITH TIME ZONE | NO | CURRENT_TIMESTAMP | Record creation timestamp | - | NO | NO | - | - |

---

## 2. User Domain

### 2.1 user_profiles

| Column | Data Type | Nullable | Default | Description | Constraint | PII? | Sensitive? | Index | Relationship |
|--------|-----------|----------|---------|-------------|------------|------|------------|-------|-------------|
| profile_id | UUID | NO | gen_random_uuid() | Primary key | PK | NO | NO | - | - |
| user_id | UUID | NO | - | User reference | FK: users(user_id) ON DELETE CASCADE, UNIQUE | YES | NO | idx_user_profiles_user_id | users |
| profile_image_url | VARCHAR(500) | YES | - | Profile image URL | - | NO | NO | - | - |
| date_of_birth | DATE | YES | - | Date of birth | - | YES | NO | - | - |
| timezone | VARCHAR(50) | NO | 'UTC' | User timezone | - | NO | NO | - | - |
| locale | VARCHAR(10) | NO | 'en-US' | User locale | - | NO | NO | - | - |
| currency | VARCHAR(3) | NO | 'USD' | User currency | - | NO | NO | - | - |
| family_id | UUID | YES | - | Family reference | FK: families(family_id) ON DELETE SET NULL | NO | NO | idx_user_profiles_family_id | families |
| family_role | VARCHAR(20) | YES | - | Family role | CHECK: OWNER, MEMBER, RESTRICTED | NO | NO | - | - |
| created_at | TIMESTAMP WITH TIME ZONE | NO | CURRENT_TIMESTAMP | Record creation timestamp | - | NO | NO | - | - |
| updated_at | TIMESTAMP WITH TIME ZONE | NO | CURRENT_TIMESTAMP | Record update timestamp | - | NO | NO | - | - |

### 2.2 user_preferences

| Column | Data Type | Nullable | Default | Description | Constraint | PII? | Sensitive? | Index | Relationship |
|--------|-----------|----------|---------|-------------|------------|------|------------|-------|-------------|
| preference_id | UUID | NO | gen_random_uuid() | Primary key | PK | NO | NO | - | - |
| user_id | UUID | NO | - | User reference | FK: users(user_id) ON DELETE CASCADE, UNIQUE | YES | NO | idx_user_preferences_user_id | users |
| budget_alerts_enabled | BOOLEAN | NO | TRUE | Budget alerts enabled | - | NO | NO | - | - |
| budget_alert_threshold_percentage | INTEGER | NO | 80 | Budget alert threshold | CHECK: 0-100 | NO | NO | - | - |
| bill_reminders_enabled | BOOLEAN | NO | TRUE | Bill reminders enabled | - | NO | NO | - | - |
| bill_reminder_days | INTEGER[] | NO | ARRAY[3, 7] | Bill reminder days | - | NO | NO | - | - |
| savings_updates_enabled | BOOLEAN | NO | TRUE | Savings updates enabled | - | NO | NO | - | - |
| savings_updates_frequency | VARCHAR(10) | NO | 'WEEKLY' | Savings update frequency | CHECK: DAILY, WEEKLY, MONTHLY | NO | NO | - | - |
| financial_health_updates_enabled | BOOLEAN | NO | TRUE | Financial health updates enabled | - | NO | NO | - | - |
| financial_health_updates_frequency | VARCHAR(10) | NO | 'MONTHLY' | Financial health update frequency | CHECK: DAILY, WEEKLY, MONTHLY | NO | NO | - | - |
| ai_response_style | VARCHAR(10) | NO | 'CONCISE' | AI response style | CHECK: CONCISE, DETAILED | NO | NO | - | - |
| ai_language | VARCHAR(10) | NO | 'en' | AI language preference | - | NO | NO | - | - |
| created_at | TIMESTAMP WITH TIME ZONE | NO | CURRENT_TIMESTAMP | Record creation timestamp | - | NO | NO | - | - |
| updated_at | TIMESTAMP WITH TIME ZONE | NO | CURRENT_TIMESTAMP | Record update timestamp | - | NO | NO | - | - |

### 2.3 user_addresses

| Column | Data Type | Nullable | Default | Description | Constraint | PII? | Sensitive? | Index | Relationship |
|--------|-----------|----------|---------|-------------|------------|------|------------|-------|-------------|
| address_id | UUID | NO | gen_random_uuid() | Primary key | PK | NO | NO | - | - |
| user_id | UUID | NO | - | User reference | FK: users(user_id) ON DELETE CASCADE | YES | NO | idx_user_addresses_user_id | users |
| address_type | VARCHAR(20) | NO | - | Address type | CHECK: HOME, WORK, BILLING, SHIPPING | NO | NO | idx_user_addresses_type | - |
| address_line1 | VARCHAR(255) | NO | - | Address line 1 | - | YES | NO | - | - |
| address_line2 | VARCHAR(255) | YES | - | Address line 2 | - | YES | NO | - | - |
| city | VARCHAR(100) | NO | - | City | - | YES | NO | - | - |
| state | VARCHAR(100) | YES | - | State/Province | - | YES | NO | - | - |
| postal_code | VARCHAR(20) | NO | - | Postal code | - | YES | NO | - | - |
| country_code | VARCHAR(2) | NO | - | Country code (ISO 3166-1 alpha-2) | - | NO | NO | - | - |
| is_default | BOOLEAN | NO | FALSE | Default address flag | - | NO | NO | - | - |
| created_at | TIMESTAMP WITH TIME ZONE | NO | CURRENT_TIMESTAMP | Record creation timestamp | - | NO | NO | - | - |
| updated_at | TIMESTAMP WITH TIME ZONE | NO | CURRENT_TIMESTAMP | Record update timestamp | - | NO | NO | - | - |

---

## 3. Family Domain

### 3.1 families

| Column | Data Type | Nullable | Default | Description | Constraint | PII? | Sensitive? | Index | Relationship |
|--------|-----------|----------|---------|-------------|------------|------|------------|-------|-------------|
| family_id | UUID | NO | gen_random_uuid() | Primary key | PK | NO | NO | - | - |
| name | VARCHAR(100) | NO | - | Family name | - | NO | NO | idx_families_name | - |
| currency | VARCHAR(3) | NO | 'USD' | Family currency | - | NO | NO | - | - |
| owner_id | UUID | NO | - | Family owner reference | FK: users(user_id) ON DELETE RESTRICT | YES | NO | idx_families_owner_id | users |
| member_count | INTEGER | NO | 1 | Total member count | - | NO | NO | - | - |
| created_at | TIMESTAMP WITH TIME ZONE | NO | CURRENT_TIMESTAMP | Record creation timestamp | - | NO | NO | - | - |
| updated_at | TIMESTAMP WITH TIME ZONE | NO | CURRENT_TIMESTAMP | Record update timestamp | - | NO | NO | - | - |
| deleted_at | TIMESTAMP WITH TIME ZONE | YES | - | Soft delete timestamp | - | NO | NO | - | - |

### 3.2 family_members

| Column | Data Type | Nullable | Default | Description | Constraint | PII? | Sensitive? | Index | Relationship |
|--------|-----------|----------|---------|-------------|------------|------|------------|-------|-------------|
| member_id | UUID | NO | gen_random_uuid() | Primary key | PK | NO | NO | - | - |
| family_id | UUID | NO | - | Family reference | FK: families(family_id) ON DELETE CASCADE | NO | NO | idx_family_members_family_id | families |
| user_id | UUID | NO | - | User reference | FK: users(user_id) ON DELETE CASCADE | YES | NO | idx_family_members_user_id | users |
| role | VARCHAR(20) | NO | 'MEMBER' | Family role | CHECK: OWNER, MEMBER, RESTRICTED | NO | NO | idx_family_members_role | - |
| joined_at | TIMESTAMP WITH TIME ZONE | NO | CURRENT_TIMESTAMP | Join timestamp | - | NO | NO | - | - |
| left_at | TIMESTAMP WITH TIME ZONE | YES | - | Leave timestamp | - | NO | NO | - | - |
| created_at | TIMESTAMP WITH TIME ZONE | YES | CURRENT_TIMESTAMP | Record creation timestamp | - | NO | NO | - | - |
| updated_at | TIMESTAMP WITH TIME ZONE | YES | CURRENT_TIMESTAMP | Record update timestamp | - | NO | NO | - | - |

**Unique Constraint**: (family_id, user_id)

### 3.3 family_invitations

| Column | Data Type | Nullable | Default | Description | Constraint | PII? | Sensitive? | Index | Relationship |
|--------|-----------|----------|---------|-------------|------------|------|------------|-------|-------------|
| invitation_id | UUID | NO | gen_random_uuid() | Primary key | PK | NO | NO | - | - |
| family_id | UUID | NO | - | Family reference | FK: families(family_id) ON DELETE CASCADE | NO | NO | idx_family_invitations_family_id | families |
| inviter_id | UUID | NO | - | Inviter reference | FK: users(user_id) ON DELETE CASCADE | YES | NO | - | users |
| email | VARCHAR(255) | NO | - | Invitee email | - | YES | NO | idx_family_invitations_email | - |
| role | VARCHAR(20) | NO | 'MEMBER' | Invited role | CHECK: MEMBER, RESTRICTED | NO | NO | - | - |
| token | VARCHAR(255) | NO | - | Invitation token | UNIQUE | NO | YES | idx_family_invitations_token | - |
| expires_at | TIMESTAMP WITH TIME ZONE | NO | - | Invitation expiration | - | NO | NO | idx_family_invitations_expires_at | - |
| accepted_at | TIMESTAMP WITH TIME ZONE | YES | - | Acceptance timestamp | - | NO | NO | - | - |
| rejected_at | TIMESTAMP WITH TIME ZONE | YES | - | Rejection timestamp | - | NO | NO | - | - |
| created_at | TIMESTAMP WITH TIME ZONE | NO | CURRENT_TIMESTAMP | Record creation timestamp | - | NO | NO | - | - |

### 3.4 family_roles

| Column | Data Type | Nullable | Default | Description | Constraint | PII? | Sensitive? | Index | Relationship |
|--------|-----------|----------|---------|-------------|------------|------|------------|-------|-------------|
| role_id | VARCHAR(20) | NO | - | Role ID | PK | NO | NO | - | - |
| role_name | VARCHAR(50) | NO | - | Role name | - | NO | NO | - | - |
| description | TEXT | YES | - | Role description | - | NO | NO | - | - |
| permissions | JSONB | YES | - | Role permissions | - | NO | NO | - | - |
| created_at | TIMESTAMP WITH TIME ZONE | NO | CURRENT_TIMESTAMP | Record creation timestamp | - | NO | NO | - | - |

---

## 4. Financial Overview Domain

### 4.1 financial_overviews

| Column | Data Type | Nullable | Default | Description | Constraint | PII? | Sensitive? | Index | Relationship |
|--------|-----------|----------|---------|-------------|------------|------|------------|-------|-------------|
| overview_id | UUID | NO | gen_random_uuid() | Primary key | PK | NO | NO | - | - |
| user_id | UUID | YES | - | User reference | FK: users(user_id) ON DELETE CASCADE | YES | NO | idx_financial_overviews_user_id | users |
| family_id | UUID | YES | - | Family reference | FK: families(family_id) ON DELETE SET NULL | NO | NO | idx_financial_overviews_family_id | families |
| planning_income | NUMERIC(15, 2) | NO | - | Planning income (planning value) | - | NO | NO | - | - |
| mandatory_commitments | NUMERIC(15, 2) | NO | 0 | Mandatory commitments | - | NO | NO | - | - |
| essential_allocation | NUMERIC(15, 2) | NO | 0 | Essential allocation | - | NO | NO | - | - |
| variable_allocation | NUMERIC(15, 2) | NO | 0 | Variable allocation | - | NO | NO | - | - |
| savings_allocation | NUMERIC(15, 2) | NO | 0 | Savings allocation | - | NO | NO | - | - |
| emergency_allocation | NUMERIC(15, 2) | NO | 0 | Emergency allocation | - | NO | NO | - | - |
| discretionary_planning | NUMERIC(15, 2) | NO | 0 | Discretionary planning | - | NO | NO | - | - |
| committed_amount | NUMERIC(15, 2) | NO | 0 | Committed amount | - | NO | NO | - | - |
| pending_payments | NUMERIC(15, 2) | NO | 0 | Pending payments | - | NO | NO | - | - |
| actual_transactions | NUMERIC(15, 2) | NO | 0 | Actual transactions (current period) | - | NO | NO | - | - |
| available_financial_capacity | NUMERIC(15, 2) | NO | 0 | Available financial capacity | - | NO | NO | - | - |
| currency | VARCHAR(3) | NO | 'USD' | Currency code | - | NO | NO | - | - |
| period | VARCHAR(7) | NO | - | Period (YYYY-MM) | - | NO | NO | idx_financial_overviews_period | - |
| calculated_at | TIMESTAMP WITH TIME ZONE | NO | CURRENT_TIMESTAMP | Calculation timestamp | - | NO | NO | - | - |
| created_at | TIMESTAMP WITH TIME ZONE | NO | CURRENT_TIMESTAMP | Record creation timestamp | - | NO | NO | - | - |
| updated_at | TIMESTAMP WITH TIME ZONE | NO | CURRENT_TIMESTAMP | Record update timestamp | - | NO | NO | - | - |

**Check Constraint**: (user_id IS NOT NULL OR family_id IS NOT NULL)

**Unique Constraints**: 
- (user_id, period) where user_id is not null
- (family_id, period) where family_id is not null

### 4.2 financial_periods

| Column | Data Type | Nullable | Default | Description | Constraint | PII? | Sensitive? | Index | Relationship |
|--------|-----------|----------|---------|-------------|------------|------|------------|-------|-------------|
| period_id | UUID | NO | gen_random_uuid() | Primary key | PK | NO | NO | - | - |
| overview_id | UUID | NO | - | Overview reference | FK: financial_overviews(overview_id) ON DELETE CASCADE | NO | NO | idx_financial_periods_overview_id | financial_overviews |
| period_start | DATE | NO | - | Period start date | - | NO | NO | - | - |
| period_end | DATE | NO | - | Period end date | - | NO | NO | - | - |
| period_type | VARCHAR(20) | NO | - | Period type | CHECK: MONTHLY, WEEKLY, CUSTOM | NO | NO | - | - |
| is_active | BOOLEAN | NO | TRUE | Active period flag | - | NO | NO | - | - |
| created_at | TIMESTAMP WITH TIME ZONE | NO | CURRENT_TIMESTAMP | Record creation timestamp | - | NO | NO | - | - |

### 4.3 financial_allocations

| Column | Data Type | Nullable | Default | Description | Constraint | PII? | Sensitive? | Index | Relationship |
|--------|-----------|----------|---------|-------------|------------|------|------------|-------|-------------|
| allocation_id | UUID | NO | gen_random_uuid() | Primary key | PK | NO | NO | - | - |
| overview_id | UUID | NO | - | Overview reference | FK: financial_overviews(overview_id) ON DELETE CASCADE | NO | NO | idx_financial_allocations_overview_id | financial_overviews |
| category_id | UUID | YES | - | Category reference | FK: categories(category_id) ON DELETE SET NULL | NO | NO | idx_financial_allocations_category_id | categories |
| category_name | VARCHAR(100) | NO | - | Category name | - | NO | NO | - | - |
| allocated_amount | NUMERIC(15, 2) | NO | - | Allocated amount | - | NO | NO | - | - |
| actual_amount | NUMERIC(15, 2) | NO | 0 | Actual amount | - | NO | NO | - | - |
| utilization_percentage | NUMERIC(5, 2) | NO | 0 | Utilization percentage | - | NO | NO | - | - |
| currency | VARCHAR(3) | NO | 'USD' | Currency code | - | NO | NO | - | - |
| created_at | TIMESTAMP WITH TIME ZONE | NO | CURRENT_TIMESTAMP | Record creation timestamp | - | NO | NO | - | - |
| updated_at | TIMESTAMP WITH TIME ZONE | NO | CURRENT_TIMESTAMP | Record update timestamp | - | NO | NO | - | - |

---

## 5. Transaction Domain

### 5.1 transactions

| Column | Data Type | Nullable | Default | Description | Constraint | PII? | Sensitive? | Index | Relationship |
|--------|-----------|----------|---------|-------------|------------|------|------------|-------|-------------|
| transaction_id | UUID | NO | gen_random_uuid() | Primary key | PK | NO | NO | - | - |
| user_id | UUID | NO | - | User reference | FK: users(user_id) ON DELETE CASCADE | YES | NO | idx_transactions_user_id | users |
| family_id | UUID | YES | - | Family reference | FK: families(family_id) ON DELETE SET NULL | NO | NO | idx_transactions_family_id | families |
| family_member_id | UUID | YES | - | Family member reference | FK: family_members(member_id) ON DELETE SET NULL | NO | NO | idx_transactions_family_member_id | family_members |
| type | VARCHAR(20) | NO | - | Transaction type | CHECK: INCOME, EXPENSE, REFUND, ADJUSTMENT, TRANSFER_RECORD | NO | NO | idx_transactions_type | - |
| category_id | UUID | YES | - | Category reference | FK: categories(category_id) ON DELETE SET NULL | NO | NO | idx_transactions_category_id | categories |
| category_name | VARCHAR(100) | NO | - | Category name | - | NO | NO | - | - |
| amount | NUMERIC(15, 2) | NO | - | Transaction amount | - | NO | NO | - | - |
| currency | VARCHAR(3) | NO | 'USD' | Currency code | - | NO | NO | - | - |
| description | TEXT | YES | - | Transaction description | - | NO | NO | - | - |
| transaction_date | DATE | NO | - | Transaction date | - | NO | NO | idx_transactions_transaction_date | - |
| posted_date | DATE | YES | - | Posted date | - | NO | NO | - | - |
| status | VARCHAR(20) | NO | 'PLANNED' | Transaction status | CHECK: PLANNED, COMMITTED, PENDING, COMPLETED, FAILED, REVERSED | NO | NO | idx_transactions_status | - |
| is_recurring | BOOLEAN | NO | FALSE | Recurring flag | - | NO | NO | idx_transactions_is_recurring | - |
| recurring_pattern | VARCHAR(50) | YES | - | Recurring pattern | - | NO | NO | - | - |
| external_reference | VARCHAR(100) | YES | - | External reference | - | NO | NO | - | - |
| source | VARCHAR(20) | NO | 'MANUAL' | Transaction source | CHECK: MANUAL, IMPORTED, PROVIDER | NO | NO | - | - |
| created_at | TIMESTAMP WITH TIME ZONE | NO | CURRENT_TIMESTAMP | Record creation timestamp | - | NO | NO | - | - |
| updated_at | TIMESTAMP WITH TIME ZONE | NO | CURRENT_TIMESTAMP | Record update timestamp | - | NO | NO | - | - |
| deleted_at | TIMESTAMP WITH TIME ZONE | YES | - | Soft delete timestamp | - | NO | NO | - | - |

---

## 6. Category Domain

### 6.1 categories

| Column | Data Type | Nullable | Default | Description | Constraint | PII? | Sensitive? | Index | Relationship |
|--------|-----------|----------|---------|-------------|------------|------|------------|-------|-------------|
| category_id | UUID | NO | gen_random_uuid() | Primary key | PK | NO | NO | - | - |
| name | VARCHAR(100) | NO | - | Category name | - | NO | NO | - | - |
| parent_category_id | UUID | YES | - | Parent category reference | FK: categories(category_id) ON DELETE SET NULL | NO | NO | idx_categories_parent_id | categories |
| category_type | VARCHAR(20) | NO | - | Category type | CHECK: ESSENTIAL, VARIABLE, DISCRETIONARY, INCOME, SAVINGS, EMERGENCY | NO | NO | idx_categories_type | - |
| priority | VARCHAR(20) | NO | 'VARIABLE' | Category priority | CHECK: ESSENTIAL, VARIABLE, DISCRETIONARY | NO | NO | idx_categories_priority | - |
| icon | VARCHAR(50) | YES | - | Category icon | - | NO | NO | - | - |
| color | VARCHAR(7) | YES | - | Category color (hex) | - | NO | NO | - | - |
| is_system | BOOLEAN | NO | TRUE | System category flag | - | NO | NO | idx_categories_is_system | - |
| is_active | BOOLEAN | NO | TRUE | Active category flag | - | NO | NO | idx_categories_is_active | - |
| created_by | UUID | YES | - | Creator reference | FK: users(user_id) ON DELETE SET NULL | YES | NO | - | users |
| created_at | TIMESTAMP WITH TIME ZONE | NO | CURRENT_TIMESTAMP | Record creation timestamp | - | NO | NO | - | - |
| updated_at | TIMESTAMP WITH TIME ZONE | NO | CURRENT_TIMESTAMP | Record update timestamp | - | NO | NO | - | - |

**Unique Constraint**: (name, parent_category_id)

---

## 7. Budget Domain

### 7.1 budgets

| Column | Data Type | Nullable | Default | Description | Constraint | PII? | Sensitive? | Index | Relationship |
|--------|-----------|----------|---------|-------------|------------|------|------------|-------|-------------|
| budget_id | UUID | NO | gen_random_uuid() | Primary key | PK | NO | NO | - | - |
| user_id | UUID | YES | - | User reference | FK: users(user_id) ON DELETE CASCADE | YES | NO | idx_budgets_user_id | users |
| family_id | UUID | YES | - | Family reference | FK: families(family_id) ON DELETE SET NULL | NO | NO | idx_budgets_family_id | families |
| name | VARCHAR(100) | NO | - | Budget name | - | NO | NO | - | - |
| period | VARCHAR(7) | NO | - | Budget period (YYYY-MM) | - | NO | NO | idx_budgets_period | - |
| currency | VARCHAR(3) | NO | 'USD' | Currency code | - | NO | NO | - | - |
| total_limit | NUMERIC(15, 2) | NO | - | Total budget limit | - | NO | NO | - | - |
| total_spent | NUMERIC(15, 2) | NO | 0 | Total spent | - | NO | NO | - | - |
| utilization_percentage | NUMERIC(5, 2) | NO | 0 | Utilization percentage | - | NO | NO | - | - |
| status | VARCHAR(20) | NO | 'ACTIVE' | Budget status | CHECK: ACTIVE, ARCHIVED, DELETED | NO | NO | idx_budgets_status | - |
| version | INTEGER | NO | 1 | Budget version | - | NO | NO | - | - |
| created_at | TIMESTAMP WITH TIME ZONE | NO | CURRENT_TIMESTAMP | Record creation timestamp | - | NO | NO | - | - |
| updated_at | TIMESTAMP WITH TIME ZONE | NO | CURRENT_TIMESTAMP | Record update timestamp | - | NO | NO | - | - |

**Check Constraint**: (user_id IS NOT NULL OR family_id IS NOT NULL)

**Unique Constraints**: 
- (user_id, period, name) where user_id is not null
- (family_id, period, name) where family_id is not null

### 7.2 budget_categories

| Column | Data Type | Nullable | Default | Description | Constraint | PII? | Sensitive? | Index | Relationship |
|--------|-----------|----------|---------|-------------|------------|------|------------|-------|-------------|
| budget_category_id | UUID | NO | gen_random_uuid() | Primary key | PK | NO | NO | - | - |
| budget_id | UUID | NO | - | Budget reference | FK: budgets(budget_id) ON DELETE CASCADE | NO | NO | idx_budget_categories_budget_id | budgets |
| category_id | UUID | YES | - | Category reference | FK: categories(category_id) ON DELETE SET NULL | NO | NO | idx_budget_categories_category_id | categories |
| category_name | VARCHAR(100) | NO | - | Category name | - | NO | NO | - | - |
| limit_amount | NUMERIC(15, 2) | NO | - | Budget limit | - | NO | NO | - | - |
| spent_amount | NUMERIC(15, 2) | NO | 0 | Spent amount | - | NO | NO | - | - |
| utilization_percentage | NUMERIC(5, 2) | NO | 0 | Utilization percentage | - | NO | NO | - | - |
| priority | VARCHAR(20) | NO | 'VARIABLE' | Category priority | CHECK: ESSENTIAL, VARIABLE, DISCRETIONARY | NO | NO | idx_budget_categories_priority | - |
| created_at | TIMESTAMP WITH TIME ZONE | NO | CURRENT_TIMESTAMP | Record creation timestamp | - | NO | NO | - | - |
| updated_at | TIMESTAMP WITH TIME ZONE | NO | CURRENT_TIMESTAMP | Record update timestamp | - | NO | NO | - | - |

### 7.3 budget_periods

| Column | Data Type | Nullable | Default | Description | Constraint | PII? | Sensitive? | Index | Relationship |
|--------|-----------|----------|---------|-------------|------------|------|------------|-------|-------------|
| period_id | UUID | NO | gen_random_uuid() | Primary key | PK | NO | NO | - | - |
| budget_id | UUID | NO | - | Budget reference | FK: budgets(budget_id) ON DELETE CASCADE | NO | NO | idx_budget_periods_budget_id | budgets |
| period_start | DATE | NO | - | Period start date | - | NO | NO | - | - |
| period_end | DATE | NO | - | Period end date | - | NO | NO | - | - |
| period_type | VARCHAR(20) | NO | - | Period type | CHECK: MONTHLY, WEEKLY, CUSTOM | NO | NO | - | - |
| is_active | BOOLEAN | NO | TRUE | Active period flag | - | NO | NO | - | - |
| created_at | TIMESTAMP WITH TIME ZONE | NO | CURRENT_TIMESTAMP | Record creation timestamp | - | NO | NO | - | - |

### 7.4 budget_recommendations

| Column | Data Type | Nullable | Default | Description | Constraint | PII? | Sensitive? | Index | Relationship |
|--------|-----------|----------|---------|-------------|------------|------|------------|-------|-------------|
| recommendation_id | UUID | NO | gen_random_uuid() | Primary key | PK | NO | NO | - | - |
| budget_id | UUID | YES | - | Budget reference | FK: budgets(budget_id) ON DELETE SET NULL | NO | NO | idx_budget_recommendations_budget_id | budgets |
| user_id | UUID | NO | - | User reference | FK: users(user_id) ON DELETE CASCADE | YES | NO | idx_budget_recommendations_user_id | users |
| family_id | UUID | YES | - | Family reference | FK: families(family_id) ON DELETE SET NULL | NO | NO | idx_budget_recommendations_family_id | families |
| period | VARCHAR(7) | NO | - | Recommendation period | - | NO | NO | idx_budget_recommendations_period | - |
| currency | VARCHAR(3) | NO | 'USD' | Currency code | - | NO | NO | - | - |
| recommended_allocation | JSONB | NO | - | System recommendation | - | NO | NO | - | - |
| user_modification | JSONB | YES | - | User modifications | - | NO | NO | - | - |
| final_approved_budget | JSONB | YES | - | Final approved budget | - | NO | NO | - | - |
| status | VARCHAR(20) | NO | 'PENDING' | Recommendation status | CHECK: PENDING, VIEWED, MODIFIED, APPROVED, REJECTED, SUPERSEDED | NO | NO | idx_budget_recommendations_status | - |
| confidence | VARCHAR(10) | NO | 'MEDIUM' | Recommendation confidence | CHECK: HIGH, MEDIUM, LOW | NO | NO | - | - |
| historical_months_used | INTEGER | NO | 0 | Historical months used | - | NO | NO | - | - |
| generated_at | TIMESTAMP WITH TIME ZONE | NO | CURRENT_TIMESTAMP | Generation timestamp | - | NO | NO | - | - |
| approved_at | TIMESTAMP WITH TIME ZONE | YES | - | Approval timestamp | - | NO | NO | - | - |

---

## 8. Allocation Domain

### 8.1 allocation_recommendations

| Column | Data Type | Nullable | Default | Description | Constraint | PII? | Sensitive? | Index | Relationship |
|--------|-----------|----------|---------|-------------|------------|------|------------|-------|-------------|
| recommendation_id | UUID | NO | gen_random_uuid() | Primary key | PK | NO | NO | - | - |
| user_id | UUID | NO | - | User reference | FK: users(user_id) ON DELETE CASCADE | YES | NO | idx_allocation_recommendations_user_id | users |
| family_id | UUID | YES | - | Family reference | FK: families(family_id) ON DELETE SET NULL | NO | NO | idx_allocation_recommendations_family_id | families |
| period | VARCHAR(7) | NO | - | Recommendation period | - | NO | NO | idx_allocation_recommendations_period | - |
| currency | VARCHAR(3) | NO | 'USD' | Currency code | - | NO | NO | - | - |
| system_recommendation | JSONB | NO | - | System recommendation | - | NO | NO | - | - |
| user_modification | JSONB | YES | - | User modifications | - | NO | NO | - | - |
| final_approved_allocation | JSONB | YES | - | Final approved allocation | - | NO | NO | - | - |
| status | VARCHAR(20) | NO | 'PENDING' | Recommendation status | CHECK: PENDING, VIEWED, MODIFIED, APPROVED, REJECTED, SUPERSEDED | NO | NO | idx_allocation_recommendations_status | - |
| confidence | VARCHAR(10) | NO | 'MEDIUM' | Recommendation confidence | CHECK: HIGH, MEDIUM, LOW | NO | NO | - | - |
| historical_months_used | INTEGER | NO | 0 | Historical months used | - | NO | NO | - | - |
| generated_at | TIMESTAMP WITH TIME ZONE | NO | CURRENT_TIMESTAMP | Generation timestamp | - | NO | NO | - | - |
| approved_at | TIMESTAMP WITH TIME ZONE | YES | - | Approval timestamp | - | NO | NO | - | - |

### 8.2 allocation_categories

| Column | Data Type | Nullable | Default | Description | Constraint | PII? | Sensitive? | Index | Relationship |
|--------|-----------|----------|---------|-------------|------------|------|------------|-------|-------------|
| allocation_category_id | UUID | NO | gen_random_uuid() | Primary key | PK | NO | NO | - | - |
| recommendation_id | UUID | NO | - | Recommendation reference | FK: allocation_recommendations(recommendation_id) ON DELETE CASCADE | NO | NO | idx_allocation_categories_recommendation_id | allocation_recommendations |
| category_id | UUID | YES | - | Category reference | FK: categories(category_id) ON DELETE SET NULL | NO | NO | idx_allocation_categories_category_id | categories |
| category_name | VARCHAR(100) | NO | - | Category name | - | NO | NO | - | - |
| recommended_amount | NUMERIC(15, 2) | NO | - | Recommended amount | - | NO | NO | - | - |
| modified_amount | NUMERIC(15, 2) | YES | - | Modified amount | - | NO | NO | - | - |
| final_amount | NUMERIC(15, 2) | YES | - | Final amount | - | NO | NO | - | - |
| priority | VARCHAR(20) | NO | 'VARIABLE' | Category priority | CHECK: ESSENTIAL, VARIABLE, DISCRETIONARY | NO | NO | - | - |
| created_at | TIMESTAMP WITH TIME ZONE | NO | CURRENT_TIMESTAMP | Record creation timestamp | - | NO | NO | - | - |

### 8.3 allocation_versions

| Column | Data Type | Nullable | Default | Description | Constraint | PII? | Sensitive? | Index | Relationship |
|--------|-----------|----------|---------|-------------|------------|------|------------|-------|-------------|
| version_id | UUID | NO | gen_random_uuid() | Primary key | PK | NO | NO | - | - |
| recommendation_id | UUID | NO | - | Recommendation reference | FK: allocation_recommendations(recommendation_id) ON DELETE CASCADE | NO | NO | idx_allocation_versions_recommendation_id | allocation_recommendations |
| version_number | INTEGER | NO | - | Version number | - | NO | NO | idx_allocation_versions_version_number | - |
| allocation_data | JSONB | NO | - | Allocation data snapshot | - | NO | NO | - | - |
| change_reason | TEXT | YES | - | Change reason | - | NO | NO | - | - |
| created_at | TIMESTAMP WITH TIME ZONE | NO | CURRENT_TIMESTAMP | Record creation timestamp | - | NO | NO | - | - |

### 8.4 allocation_approvals

| Column | Data Type | Nullable | Default | Description | Constraint | PII? | Sensitive? | Index | Relationship |
|--------|-----------|----------|---------|-------------|------------|------|------------|-------|-------------|
| approval_id | UUID | NO | gen_random_uuid() | Primary key | PK | NO | NO | - | - |
| recommendation_id | UUID | NO | - | Recommendation reference | FK: allocation_recommendations(recommendation_id) ON DELETE CASCADE | NO | NO | idx_allocation_approvals_recommendation_id | allocation_recommendations |
| user_id | UUID | NO | - | User reference | FK: users(user_id) ON DELETE CASCADE | YES | NO | idx_allocation_approvals_user_id | users |
| action | VARCHAR(20) | NO | - | Approval action | CHECK: APPROVE, MODIFY, REJECT | NO | NO | - | - |
| approval_data | JSONB | YES | - | Approval data | - | NO | NO | - | - |
| created_at | TIMESTAMP WITH TIME ZONE | NO | CURRENT_TIMESTAMP | Record creation timestamp | - | NO | NO | - | - |

---

## 9. Savings Domain

### 9.1 savings_goals

| Column | Data Type | Nullable | Default | Description | Constraint | PII? | Sensitive? | Index | Relationship |
|--------|-----------|----------|---------|-------------|------------|------|------------|-------|-------------|
| goal_id | UUID | NO | gen_random_uuid() | Primary key | PK | NO | NO | - | - |
| user_id | UUID | YES | - | User reference | FK: users(user_id) ON DELETE CASCADE | YES | NO | idx_savings_goals_user_id | users |
| family_id | UUID | YES | - | Family reference | FK: families(family_id) ON DELETE SET NULL | NO | NO | idx_savings_goals_family_id | families |
| name | VARCHAR(100) | NO | - | Goal name | - | NO | NO | - | - |
| target_amount | NUMERIC(15, 2) | NO | - | Target amount | - | NO | NO | - | - |
| current_amount | NUMERIC(15, 2) | NO | 0 | Current amount (planning value) | - | NO | NO | - | - |
| target_date | DATE | NO | - | Target date | - | NO | NO | idx_savings_goals_target_date | - |
| currency | VARCHAR(3) | NO | 'USD' | Currency code | - | NO | NO | - | - |
| priority | VARCHAR(20) | NO | 'MEDIUM' | Goal priority | CHECK: HIGH, MEDIUM, LOW | NO | NO | idx_savings_goals_priority | - |
| category | VARCHAR(50) | YES | - | Goal category | - | NO | NO | - | - |
| status | VARCHAR(20) | NO | 'ACTIVE' | Goal status | CHECK: ACTIVE, COMPLETED, CANCELLED | NO | NO | idx_savings_goals_status | - |
| progress_percentage | NUMERIC(5, 2) | NO | 0 | Progress percentage | - | NO | NO | - | - |
| created_at | TIMESTAMP WITH TIME ZONE | NO | CURRENT_TIMESTAMP | Record creation timestamp | - | NO | NO | - | - |
| updated_at | TIMESTAMP WITH TIME ZONE | NO | CURRENT_TIMESTAMP | Record update timestamp | - | NO | NO | - | - |

**Check Constraint**: (user_id IS NOT NULL OR family_id IS NOT NULL)

### 9.2 savings_contributions

| Column | Data Type | Nullable | Default | Description | Constraint | PII? | Sensitive? | Index | Relationship |
|--------|-----------|----------|---------|-------------|------------|------|------------|-------|-------------|
| contribution_id | UUID | NO | gen_random_uuid() | Primary key | PK | NO | NO | - | - |
| goal_id | UUID | NO | - | Goal reference | FK: savings_goals(goal_id) ON DELETE CASCADE | NO | NO | idx_savings_contributions_goal_id | savings_goals |
| user_id | UUID | NO | - | User reference | FK: users(user_id) ON DELETE CASCADE | YES | NO | idx_savings_contributions_user_id | users |
| amount | NUMERIC(15, 2) | NO | - | Contribution amount | - | NO | NO | - | - |
| currency | VARCHAR(3) | NO | 'USD' | Currency code | - | NO | NO | - | - |
| contribution_date | DATE | NO | - | Contribution date | - | NO | NO | idx_savings_contributions_contribution_date | - |
| notes | TEXT | YES | - | Contribution notes | - | NO | NO | - | - |
| created_at | TIMESTAMP WITH TIME ZONE | NO | CURRENT_TIMESTAMP | Record creation timestamp | - | NO | NO | - | - |

### 9.3 savings_progress

| Column | Data Type | Nullable | Default | Description | Constraint | PII? | Sensitive? | Index | Relationship |
|--------|-----------|----------|---------|-------------|------------|------|------------|-------|-------------|
| progress_id | UUID | NO | gen_random_uuid() | Primary key | PK | NO | NO | - | - |
| goal_id | UUID | NO | - | Goal reference | FK: savings_goals(goal_id) ON DELETE CASCADE | NO | NO | idx_savings_progress_goal_id | savings_goals |
| current_amount | NUMERIC(15, 2) | NO | - | Current amount | - | NO | NO | - | - |
| progress_percentage | NUMERIC(5, 2) | NO | - | Progress percentage | - | NO | NO | - | - |
| remaining_amount | NUMERIC(15, 2) | NO | - | Remaining amount | - | NO | NO | - | - |
| months_remaining | INTEGER | YES | - | Months remaining | - | NO | NO | - | - |
| required_monthly_contribution | NUMERIC(15, 2) | YES | - | Required monthly contribution | - | NO | NO | - | - |
| actual_monthly_contribution | NUMERIC(15, 2) | YES | - | Actual monthly contribution | - | NO | NO | - | - |
| on_track | BOOLEAN | YES | - | On track flag | - | NO | NO | - | - |
| status | VARCHAR(20) | YES | - | Progress status | CHECK: ON_TRACK, BEHIND, AHEAD | NO | NO | - | - |
| calculated_at | TIMESTAMP WITH TIME ZONE | NO | CURRENT_TIMESTAMP | Calculation timestamp | - | NO | NO | idx_savings_progress_calculated_at | - |

---

## 10. Bill Domain

### 10.1 bills

| Column | Data Type | Nullable | Default | Description | Constraint | PII? | Sensitive? | Index | Relationship |
|--------|-----------|----------|---------|-------------|------------|------|------------|-------|-------------|
| bill_id | UUID | NO | gen_random_uuid() | Primary key | PK | NO | NO | - | - |
| user_id | UUID | NO | - | User reference | FK: users(user_id) ON DELETE CASCADE | YES | NO | idx_bills_user_id | users |
| family_id | UUID | YES | - | Family reference | FK: families(family_id) ON DELETE SET NULL | NO | NO | idx_bills_family_id | families |
| family_member_id | UUID | YES | - | Family member reference | FK: family_members(member_id) ON DELETE SET NULL | NO | NO | idx_bills_family_member_id | family_members |
| name | VARCHAR(100) | NO | - | Bill name | - | NO | NO | - | - |
| amount | NUMERIC(15, 2) | NO | - | Bill amount | - | NO | NO | - | - |
| currency | VARCHAR(3) | NO | 'USD' | Currency code | - | NO | NO | - | - |
| due_date | DATE | NO | - | Due date | - | NO | NO | idx_bills_due_date | - |
| category | VARCHAR(50) | YES | - | Bill category | - | NO | NO | - | - |
| is_recurring | BOOLEAN | NO | FALSE | Recurring flag | - | NO | NO | idx_bills_is_recurring | - |
| recurring_period | VARCHAR(20) | YES | - | Recurring period | CHECK: MONTHLY, WEEKLY, YEARLY | NO | NO | - | - |
| vendor | VARCHAR(100) | YES | - | Vendor name | - | NO | NO | - | - |
| notes | TEXT | YES | - | Bill notes | - | NO | NO | - | - |
| status | VARCHAR(20) | NO | 'PENDING' | Bill status | CHECK: PENDING, PAID, OVERDUE | NO | NO | idx_bills_status | - |
| paid_date | DATE | YES | - | Paid date (user declaration) | - | NO | NO | - | - |
| payment_method | VARCHAR(50) | YES | - | Payment method (user declaration) | - | NO | NO | - | - |
| created_at | TIMESTAMP WITH TIME ZONE | NO | CURRENT_TIMESTAMP | Record creation timestamp | - | NO | NO | - | - |
| updated_at | TIMESTAMP WITH TIME ZONE | NO | CURRENT_TIMESTAMP | Record update timestamp | - | NO | NO | - | - |

### 10.2 bill_categories

| Column | Data Type | Nullable | Default | Description | Constraint | PII? | Sensitive? | Index | Relationship |
|--------|-----------|----------|---------|-------------|------------|------|------------|-------|-------------|
| category_id | UUID | NO | gen_random_uuid() | Primary key | PK | NO | NO | - | - |
| name | VARCHAR(100) | NO | - | Category name | - | NO | NO | idx_bill_categories_name | - |
| description | TEXT | YES | - | Category description | - | NO | NO | - | - |
| icon | VARCHAR(50) | YES | - | Category icon | - | NO | NO | - | - |
| color | VARCHAR(7) | YES | - | Category color (hex) | - | NO | NO | - | - |
| is_system | BOOLEAN | NO | TRUE | System category flag | - | NO | NO | - | - |
| created_at | TIMESTAMP WITH TIME ZONE | NO | CURRENT_TIMESTAMP | Record creation timestamp | - | NO | NO | - | - |

### 10.3 bill_status_history

| Column | Data Type | Nullable | Default | Description | Constraint | PII? | Sensitive? | Index | Relationship |
|--------|-----------|----------|---------|-------------|------------|------|------------|-------|-------------|
| history_id | UUID | NO | gen_random_uuid() | Primary key | PK | NO | NO | - | - |
| bill_id | UUID | NO | - | Bill reference | FK: bills(bill_id) ON DELETE CASCADE | NO | NO | idx_bill_status_history_bill_id | bills |
| old_status | VARCHAR(20) | YES | - | Old status | - | NO | NO | - | - |
| new_status | VARCHAR(20) | NO | - | New status | - | NO | NO | - | - |
| changed_by | UUID | NO | - | User reference | FK: users(user_id) ON DELETE CASCADE | YES | NO | - | users |
| changed_at | TIMESTAMP WITH TIME ZONE | NO | CURRENT_TIMESTAMP | Change timestamp | - | NO | NO | idx_bill_status_history_changed_at | - |
| notes | TEXT | YES | - | Change notes | - | NO | NO | - | - |

---

## 11. Notification Domain

### 11.1 notifications

| Column | Data Type | Nullable | Default | Description | Constraint | PII? | Sensitive? | Index | Relationship |
|--------|-----------|----------|---------|-------------|------------|------|------------|-------|-------------|
| notification_id | UUID | NO | gen_random_uuid() | Primary key | PK | NO | NO | - | - |
| user_id | UUID | NO | - | User reference | FK: users(user_id) ON DELETE CASCADE | YES | NO | idx_notifications_user_id | users |
| type | VARCHAR(50) | NO | - | Notification type | CHECK: BUDGET_ALERT, BILL_REMINDER, SAVINGS_UPDATE, FINANCIAL_HEALTH_UPDATE, SYSTEM | NO | NO | idx_notifications_type | - |
| title | VARCHAR(200) | NO | - | Notification title | - | NO | NO | - | - |
| message | TEXT | NO | - | Notification message | - | NO | NO | - | - |
| data | JSONB | YES | - | Notification data | - | NO | NO | - | - |
| is_read | BOOLEAN | NO | FALSE | Read flag | - | NO | NO | idx_notifications_is_read | - |
| read_at | TIMESTAMP WITH TIME ZONE | YES | - | Read timestamp | - | NO | NO | - | - |
| created_at | TIMESTAMP WITH TIME ZONE | NO | CURRENT_TIMESTAMP | Record creation timestamp | - | NO | NO | idx_notifications_created_at | - |

### 11.2 notification_preferences

| Column | Data Type | Nullable | Default | Description | Constraint | PII? | Sensitive? | Index | Relationship |
|--------|-----------|----------|---------|-------------|------------|------|------------|-------|-------------|
| preference_id | UUID | NO | gen_random_uuid() | Primary key | PK | NO | NO | - | - |
| user_id | UUID | NO | - | User reference | FK: users(user_id) ON DELETE CASCADE, UNIQUE | YES | NO | idx_notification_preferences_user_id | users |
| budget_alerts_enabled | BOOLEAN | NO | TRUE | Budget alerts enabled | - | NO | NO | - | - |
| budget_alert_threshold_percentage | INTEGER | NO | 80 | Budget alert threshold | CHECK: 0-100 | NO | NO | - | - |
| bill_reminders_enabled | BOOLEAN | NO | TRUE | Bill reminders enabled | - | NO | NO | - | - |
| bill_reminder_days | INTEGER[] | NO | ARRAY[3, 7] | Bill reminder days | - | NO | NO | - | - |
| savings_updates_enabled | BOOLEAN | NO | TRUE | Savings updates enabled | - | NO | NO | - | - |
| savings_updates_frequency | VARCHAR(10) | NO | 'WEEKLY' | Savings update frequency | CHECK: DAILY, WEEKLY, MONTHLY | NO | NO | - | - |
| financial_health_updates_enabled | BOOLEAN | NO | TRUE | Financial health updates enabled | - | NO | NO | - | - |
| financial_health_updates_frequency | VARCHAR(10) | NO | 'MONTHLY' | Financial health update frequency | CHECK: DAILY, WEEKLY, MONTHLY | NO | NO | - | - |
| push_enabled | BOOLEAN | NO | TRUE | Push notifications enabled | - | NO | NO | - | - |
| email_enabled | BOOLEAN | NO | FALSE | Email notifications enabled | - | NO | NO | - | - |
| sms_enabled | BOOLEAN | NO | FALSE | SMS notifications enabled | - | NO | NO | - | - |
| created_at | TIMESTAMP WITH TIME ZONE | NO | CURRENT_TIMESTAMP | Record creation timestamp | - | NO | NO | - | - |
| updated_at | TIMESTAMP WITH TIME ZONE | NO | CURRENT_TIMESTAMP | Record update timestamp | - | NO | NO | - | - |

### 11.3 notification_deliveries

| Column | Data Type | Nullable | Default | Description | Constraint | PII? | Sensitive? | Index | Relationship |
|--------|-----------|----------|---------|-------------|------------|------|------------|-------|-------------|
| delivery_id | UUID | NO | gen_random_uuid() | Primary key | PK | NO | NO | - | - |
| notification_id | UUID | NO | - | Notification reference | FK: notifications(notification_id) ON DELETE CASCADE | NO | NO | idx_notification_deliveries_notification_id | notifications |
| channel | VARCHAR(20) | NO | - | Delivery channel | CHECK: PUSH, EMAIL, SMS | NO | NO | idx_notification_deliveries_channel | - |
| status | VARCHAR(20) | NO | 'QUEUED' | Delivery status | CHECK: QUEUED, SENT, DELIVERED, FAILED | NO | NO | idx_notification_deliveries_status | - |
| queued_at | TIMESTAMP WITH TIME ZONE | NO | CURRENT_TIMESTAMP | Queued timestamp | - | NO | NO | - | - |
| sent_at | TIMESTAMP WITH TIME ZONE | YES | - | Sent timestamp | - | NO | NO | - | - |
| delivered_at | TIMESTAMP WITH TIME ZONE | YES | - | Delivered timestamp | - | NO | NO | - | - |
| failed_at | TIMESTAMP WITH TIME ZONE | YES | - | Failed timestamp | - | NO | NO | - | - |
| error_message | TEXT | YES | - | Error message | - | NO | NO | - | - |
| provider_reference | VARCHAR(100) | YES | - | Provider reference | - | NO | NO | - | - |

---

## 12. Financial Health Domain

### 12.1 financial_health_scores

| Column | Data Type | Nullable | Default | Description | Constraint | PII? | Sensitive? | Index | Relationship |
|--------|-----------|----------|---------|-------------|------------|------|------------|-------|-------------|
| score_id | UUID | NO | gen_random_uuid() | Primary key | PK | NO | NO | - | - |
| user_id | UUID | YES | - | User reference | FK: users(user_id) ON DELETE CASCADE | YES | NO | idx_financial_health_scores_user_id | users |
| family_id | UUID | YES | - | Family reference | FK: families(family_id) ON DELETE SET NULL | NO | NO | idx_financial_health_scores_family_id | families |
| overall_score | INTEGER | NO | - | Overall score (0-100) | CHECK: 0-100 | NO | NO | - | - |
| score_label | VARCHAR(20) | NO | - | Score label | CHECK: CRITICAL, NEEDS_ATTENTION, FAIR, GOOD, EXCELLENT | NO | NO | - | - |
| confidence | VARCHAR(10) | NO | - | Score confidence | CHECK: HIGH, MEDIUM, LOW | NO | NO | - | - |
| status | VARCHAR(20) | NO | 'FINAL' | Score status | CHECK: FINAL, PROVISIONAL | NO | NO | - | - |
| calculated_at | TIMESTAMP WITH TIME ZONE | NO | CURRENT_TIMESTAMP | Calculation timestamp | - | NO | NO | idx_financial_health_scores_calculated_at | - |

**Check Constraint**: (user_id IS NOT NULL OR family_id IS NOT NULL)

**Unique Constraints**: 
- (user_id, calculated_at) where user_id is not null
- (family_id, calculated_at) where family_id is not null

### 12.2 financial_health_factors

| Column | Data Type | Nullable | Default | Description | Constraint | PII? | Sensitive? | Index | Relationship |
|--------|-----------|----------|---------|-------------|------------|------|------------|-------|-------------|
| factor_id | UUID | NO | gen_random_uuid() | Primary key | PK | NO | NO | - | - |
| score_id | UUID | NO | - | Score reference | FK: financial_health_scores(score_id) ON DELETE CASCADE | NO | NO | idx_financial_health_factors_score_id | financial_health_scores |
| factor_name | VARCHAR(50) | NO | - | Factor name | - | NO | NO | idx_financial_health_factors_factor_name | - |
| factor_score | INTEGER | NO | - | Factor score (0-100) | CHECK: 0-100 | NO | NO | - | - |
| weight | NUMERIC(5, 2) | NO | - | Factor weight | CHECK: 0-1 | NO | NO | - | - |
| contribution | NUMERIC(5, 2) | NO | - | Factor contribution | - | NO | NO | - | - |
| created_at | TIMESTAMP WITH TIME ZONE | NO | CURRENT_TIMESTAMP | Record creation timestamp | - | NO | NO | - | - |

### 12.3 financial_health_calculations

| Column | Data Type | Nullable | Default | Description | Constraint | PII? | Sensitive? | Index | Relationship |
|--------|-----------|----------|---------|-------------|------------|------|------------|-------|-------------|
| calculation_id | UUID | NO | gen_random_uuid() | Primary key | PK | NO | NO | - | - |
| score_id | UUID | NO | - | Score reference | FK: financial_health_scores(score_id) ON DELETE CASCADE | NO | NO | idx_financial_health_calculations_score_id | financial_health_scores |
| calculation_version | VARCHAR(20) | NO | - | Calculation version | - | NO | NO | idx_financial_health_calculations_version | - |
| input_snapshot | JSONB | NO | - | Input data snapshot | - | NO | NO | - | - |
| factor_scores | JSONB | NO | - | Factor scores | - | NO | NO | - | - |
| weights | JSONB | NO | - | Factor weights | - | NO | NO | - | - |
| calculated_at | TIMESTAMP WITH TIME ZONE | NO | CURRENT_TIMESTAMP | Calculation timestamp | - | NO | NO | - | - |

---

## 13. AI Domain

### 13.1 ai_conversations

| Column | Data Type | Nullable | Default | Description | Constraint | PII? | Sensitive? | Index | Relationship |
|--------|-----------|----------|---------|-------------|------------|------|------------|-------|-------------|
| conversation_id | UUID | NO | gen_random_uuid() | Primary key | PK | NO | NO | - | - |
| user_id | UUID | NO | - | User reference | FK: users(user_id) ON DELETE CASCADE | YES | NO | idx_ai_conversations_user_id | users |
| family_id | UUID | YES | - | Family reference | FK: families(family_id) ON DELETE SET NULL | NO | NO | idx_ai_conversations_family_id | families |
| title | VARCHAR(200) | YES | - | Conversation title | - | NO | NO | - | - |
| message_count | INTEGER | NO | 0 | Message count | - | NO | NO | - | - |
| last_message_at | TIMESTAMP WITH TIME ZONE | YES | - | Last message timestamp | - | NO | NO | - | - |
| created_at | TIMESTAMP WITH TIME ZONE | NO | CURRENT_TIMESTAMP | Record creation timestamp | - | NO | NO | - | - |
| updated_at | TIMESTAMP WITH TIME ZONE | NO | CURRENT_TIMESTAMP | Record update timestamp | - | NO | NO | idx_ai_conversations_updated_at | - |

### 13.2 ai_messages

| Column | Data Type | Nullable | Default | Description | Constraint | PII? | Sensitive? | Index | Relationship |
|--------|-----------|----------|---------|-------------|------------|------|------------|-------|-------------|
| message_id | UUID | NO | gen_random_uuid() | Primary key | PK | NO | NO | - | - |
| conversation_id | UUID | NO | - | Conversation reference | FK: ai_conversations(conversation_id) ON DELETE CASCADE | NO | NO | idx_ai_messages_conversation_id | ai_conversations |
| role | VARCHAR(20) | NO | - | Message role | CHECK: USER, AI | NO | NO | idx_ai_messages_role | - |
| content | TEXT | NO | - | Message content | - | NO | NO | - | - |
| message_type | VARCHAR(20) | YES | - | Message type | CHECK: FACT, ANALYSIS, RECOMMENDATION, WARNING, INSUFFICIENT_DATA, REFUSAL | NO | NO | - | - |
| response_type | VARCHAR(20) | YES | - | Response type | CHECK: FACT, ANALYSIS, RECOMMENDATION, WARNING, INSUFFICIENT_DATA, REFUSAL | NO | NO | - | - |
| confidence | VARCHAR(10) | YES | - | Response confidence | CHECK: HIGH, MEDIUM, LOW | NO | NO | - | - |
| model_provider | VARCHAR(50) | YES | - | Model provider | - | NO | NO | - | - |
| model_version | VARCHAR(50) | YES | - | Model version | - | NO | NO | - | - |
| prompt_version | VARCHAR(50) | YES | - | Prompt version | - | NO | NO | - | - |
| created_at | TIMESTAMP WITH TIME ZONE | NO | CURRENT_TIMESTAMP | Record creation timestamp | - | NO | NO | idx_ai_messages_created_at | - |

### 13.3 ai_tool_invocations

| Column | Data Type | Nullable | Default | Description | Constraint | PII? | Sensitive? | Index | Relationship |
|--------|-----------|----------|---------|-------------|------------|------|------------|-------|-------------|
| invocation_id | UUID | NO | gen_random_uuid() | Primary key | PK | NO | NO | - | - |
| conversation_id | UUID | YES | - | Conversation reference | FK: ai_conversations(conversation_id) ON DELETE SET NULL | NO | NO | idx_ai_tool_invocations_conversation_id | ai_conversations |
| message_id | UUID | YES | - | Message reference | FK: ai_messages(message_id) ON DELETE SET NULL | NO | NO | idx_ai_tool_invocations_message_id | ai_messages |
| tool_name | VARCHAR(100) | NO | - | Tool name | - | NO | NO | idx_ai_tool_invocations_tool_name | - |
| tool_parameters | JSONB | YES | - | Tool parameters | - | NO | NO | - | - |
| tool_result_reference | VARCHAR(100) | YES | - | Tool result reference | - | NO | NO | - | - |
| status | VARCHAR(20) | NO | - | Invocation status | CHECK: SUCCESS, FAILURE, TIMEOUT | NO | NO | - | - |
| executed_at | TIMESTAMP WITH TIME ZONE | NO | CURRENT_TIMESTAMP | Execution timestamp | - | NO | NO | idx_ai_tool_invocations_executed_at | - |
| error_message | TEXT | YES | - | Error message | - | NO | NO | - | - |

### 13.4 ai_recommendations

| Column | Data Type | Nullable | Default | Description | Constraint | PII? | Sensitive? | Index | Relationship |
|--------|-----------|----------|---------|-------------|------------|------|------------|-------|-------------|
| recommendation_id | UUID | NO | gen_random_uuid() | Primary key | PK | NO | NO | - | - |
| conversation_id | UUID | YES | - | Conversation reference | FK: ai_conversations(conversation_id) ON DELETE SET NULL | NO | NO | idx_ai_recommendations_conversation_id | ai_conversations |
| message_id | UUID | YES | - | Message reference | FK: ai_messages(message_id) ON DELETE SET NULL | NO | NO | idx_ai_recommendations_message_id | ai_messages |
| recommendation_type | VARCHAR(50) | NO | - | Recommendation type | - | NO | NO | idx_ai_recommendations_type | - |
| recommendation_data | JSONB | NO | - | Recommendation data | - | NO | NO | - | - |
| user_action | VARCHAR(20) | YES | - | User action | CHECK: ACCEPTED, REJECTED, IGNORED | NO | NO | - | - |
| created_at | TIMESTAMP WITH TIME ZONE | NO | CURRENT_TIMESTAMP | Record creation timestamp | - | NO | NO | - | - |

---

## 14. Audit Domain

### 14.1 audit_logs

| Column | Data Type | Nullable | Default | Description | Constraint | PII? | Sensitive? | Index | Relationship |
|--------|-----------|----------|---------|-------------|------------|------|------------|-------|-------------|
| audit_id | UUID | NO | gen_random_uuid() | Primary key | PK | NO | NO | - | - |
| actor_id | UUID | NO | - | Actor reference | FK: users(user_id) ON DELETE RESTRICT | YES | NO | idx_audit_logs_actor_id | users |
| actor_type | VARCHAR(20) | NO | 'USER' | Actor type | CHECK: USER, SYSTEM, ADMIN | NO | NO | - | - |
| action | VARCHAR(50) | NO | - | Audit action | - | NO | NO | idx_audit_logs_action | - |
| resource_type | VARCHAR(50) | NO | - | Resource type | - | NO | NO | idx_audit_logs_resource_type | - |
| resource_id | UUID | YES | - | Resource ID | - | NO | NO | idx_audit_logs_resource_id | - |
| request_id | UUID | YES | - | Request ID | - | NO | NO | - | - |
| correlation_id | UUID | YES | - | Correlation ID | - | NO | NO | - | - |
| result | VARCHAR(20) | NO | - | Audit result | CHECK: SUCCESS, FAILURE, PARTIAL | NO | NO | - | - |
| metadata | JSONB | YES | - | Audit metadata | - | NO | NO | - | - |
| created_at | TIMESTAMP WITH TIME ZONE | NO | CURRENT_TIMESTAMP | Record creation timestamp | - | NO | NO | idx_audit_logs_created_at | - |

---

## 15. Configuration Domain

### 15.1 system_configurations

| Column | Data Type | Nullable | Default | Description | Constraint | PII? | Sensitive? | Index | Relationship |
|--------|-----------|----------|---------|-------------|------------|------|------------|-------|-------------|
| config_id | UUID | NO | gen_random_uuid() | Primary key | PK | NO | NO | - | - |
| config_key | VARCHAR(100) | NO | - | Configuration key | UNIQUE | NO | NO | idx_system_configurations_key | - |
| config_value | TEXT | NO | - | Configuration value | - | NO | NO | - | - |
| config_type | VARCHAR(20) | NO | - | Configuration type | CHECK: STRING, INTEGER, BOOLEAN, JSON, DECIMAL | NO | NO | idx_system_configurations_type | - |
| description | TEXT | YES | - | Configuration description | - | NO | NO | - | - |
| is_sensitive | BOOLEAN | NO | FALSE | Sensitive flag | - | NO | NO | - | - |
| created_at | TIMESTAMP WITH TIME ZONE | NO | CURRENT_TIMESTAMP | Record creation timestamp | - | NO | NO | - | - |
| updated_at | TIMESTAMP WITH TIME ZONE | NO | CURRENT_TIMESTAMP | Record update timestamp | - | NO | NO | - | - |

### 15.2 user_configurations

| Column | Data Type | Nullable | Default | Description | Constraint | PII? | Sensitive? | Index | Relationship |
|--------|-----------|----------|---------|-------------|------------|------|------------|-------|-------------|
| config_id | UUID | NO | gen_random_uuid() | Primary key | PK | NO | NO | - | - |
| user_id | UUID | NO | - | User reference | FK: users(user_id) ON DELETE CASCADE | YES | NO | idx_user_configurations_user_id | users |
| config_key | VARCHAR(100) | NO | - | Configuration key | - | NO | NO | idx_user_configurations_key | - |
| config_value | TEXT | NO | - | Configuration value | - | NO | NO | - | - |
| config_type | VARCHAR(20) | NO | - | Configuration type | CHECK: STRING, INTEGER, BOOLEAN, JSON, DECIMAL | NO | NO | - | - |
| created_at | TIMESTAMP WITH TIME ZONE | NO | CURRENT_TIMESTAMP | Record creation timestamp | - | NO | NO | - | - |
| updated_at | TIMESTAMP WITH TIME ZONE | NO | CURRENT_TIMESTAMP | Record update timestamp | - | NO | NO | - | - |

**Unique Constraint**: (user_id, config_key)

---

## 16. Data Type Summary

### 16.1 Primary Keys
- All tables use `UUID` with `gen_random_uuid()` default
- Ensures globally unique identifiers
- Supports distributed systems

### 16.2 Foreign Keys
- All foreign keys use `UUID`
- Appropriate ON DELETE behavior (CASCADE, SET NULL, RESTRICT)

### 16.3 Monetary Types
- All monetary columns use `NUMERIC(15, 2)`
- Precision: 15 digits, Scale: 2 decimal places
- Supports amounts up to 999,999,999,999.99
- Never use FLOAT or DOUBLE for money

### 16.4 Timestamps
- All timestamps use `TIMESTAMP WITH TIME ZONE`
- Stored in UTC
- Default: `CURRENT_TIMESTAMP`

### 16.5 Boolean Flags
- All boolean columns use `BOOLEAN`
- Default values specified where appropriate

### 16.6 JSONB Columns
- Used for flexible data storage
- Examples: recommendations, allocations, metadata
- Supports query and indexing

### 16.7 Array Columns
- Used for multi-value data
- Examples: bill_reminder_days (INTEGER[])
- PostgreSQL-specific array type

---

## 17. PII and Sensitive Data Summary

### 17.1 PII (Personally Identifiable Information)

| Table | Column | PII? |
|-------|--------|------|
| users | email | YES |
| users | phone_number | YES |
| users | first_name | YES |
| users | last_name | YES |
| user_profiles | date_of_birth | YES |
| user_addresses | address_line1 | YES |
| user_addresses | address_line2 | YES |
| user_addresses | city | YES |
| user_addresses | state | YES |
| user_addresses | postal_code | YES |
| families | name | NO (family name, not personal) |
| audit_logs | actor_id | YES (references user) |

### 17.2 Sensitive Data

| Table | Column | Sensitive? |
|-------|--------|-------------|
| users | password_hash | YES (hashed) |
| user_credentials | credential_value | YES (hashed) |
| otp_challenges | otp_hash | YES (hashed) |
| user_sessions | refresh_token_hash | YES (hashed) |
| user_devices | device_token | YES (push token) |
| family_invitations | token | YES (invitation token) |
| system_configurations | config_value | YES (if is_sensitive = TRUE) |

---

## 18. Index Summary

### 18.1 Performance Indexes

**User-based queries**:
- idx_users_email
- idx_users_status
- idx_user_credentials_user_id
- idx_user_sessions_user_id
- idx_user_devices_user_id
- idx_user_profiles_user_id
- idx_user_preferences_user_id
- idx_user_addresses_user_id
- idx_user_configurations_user_id

**Family-based queries**:
- idx_families_owner_id
- idx_families_name
- idx_family_members_family_id
- idx_family_members_user_id
- idx_family_members_role
- idx_family_invitations_family_id
- idx_user_profiles_family_id

**Date-based queries**:
- idx_transactions_transaction_date
- idx_bills_due_date
- idx_savings_goals_target_date
- idx_savings_contributions_contribution_date
- idx_bill_status_history_changed_at
- idx_audit_logs_created_at

**Status-based queries**:
- idx_users_status
- idx_transactions_status
- idx_bills_status
- idx_budgets_status
- idx_savings_goals_status
- idx_notifications_is_read

### 18.2 Index Count by Domain

| Domain | Index Count |
|--------|-------------|
| Identity | 8 |
| User | 5 |
| Family | 7 |
| Financial Overview | 4 |
| Transaction | 8 |
| Category | 5 |
| Budget | 7 |
| Allocation | 5 |
| Savings | 5 |
| Bill | 5 |
| Notification | 6 |
| Financial Health | 4 |
| AI | 7 |
| Audit | 6 |
| Configuration | 3 |
| **TOTAL** | **85** |

---

## 19. Conclusion

The NeoWallet data dictionary defines 35 tables with 300+ columns across 14 domains. All monetary values use NUMERIC(15, 2) for precision. PII and sensitive data are clearly identified. Indexes are designed based on API access patterns.

**Next Steps**:
1. Create Traceability Matrix
2. Create Validation Report
