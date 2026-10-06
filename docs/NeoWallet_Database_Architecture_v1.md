# NeoWallet Database Architecture v1

## Executive Summary

This document defines the complete logical and physical database design for NeoWallet MVP. NeoWallet uses a single PostgreSQL database for the modular monolith architecture, with logical domain ownership for 14 core domains.

**Database**: PostgreSQL 15+
**Architecture**: Single database for modular monolith
**Total Tables**: 35 tables across 14 domains
**Date**: August 18, 2026
**Status**: Ready for Product Owner Approval

---

## 1. Database Principles

### 1.1 Single Database Strategy

NeoWallet MVP uses **ONE PostgreSQL database** for the entire modular monolith.

**Rationale**:
- Simplifies deployment and operations for MVP
- Enables cross-domain queries for financial calculations
- Reduces transaction complexity
- Aligns with modular monolith architecture
- Can be split later if needed

**Anti-Patterns to Avoid**:
- Do NOT create one database per service
- Do NOT introduce microservice database-per-service architecture for MVP
- Do NOT use separate databases for each module

### 1.2 Logical Domain Ownership

The database is logically organized by domain ownership, but physically stored in a single database.

**Schema Organization**:
- All tables use a single `public` schema
- Tables are named with domain prefixes for clarity
- Example: `users`, `families`, `transactions`, `budgets`

### 1.3 Financial Data Principle

**CRITICAL**: NeoWallet does NOT hold customer funds.

**Database Design Implications**:
- No custodial wallet balance table
- No stored-value wallet table
- No payment execution tables
- Financial overview tables store PLANNING DATA only
- Transaction tables record ACTUAL TRANSACTIONS (external or manual)
- All monetary values use NUMERIC/DECIMAL types

---

## 2. Core Domains

### 2.1 Domain Overview

| Domain | Table Count | Ownership | Purpose |
|--------|-------------|-----------|---------|
| Identity | 5 | Authentication Module | User authentication and session management |
| Users | 3 | User Module | User profile and preferences |
| Families | 4 | Family Module | Family management and membership |
| Financial Overview | 3 | Financial Overview Module | Planning and allocation data |
| Transactions | 1 | Transaction Module | Transaction records |
| Categories | 1 | Transaction Module | Transaction categorization |
| Budgets | 4 | Budget Module | Budget planning and tracking |
| Allocations | 4 | Allocation Module | Allocation recommendations |
| Savings Goals | 3 | Savings Module | Savings goal tracking |
| Bills | 3 | Bill Module | Bill management |
| Notifications | 3 | Notification Module | Notification delivery |
| Financial Health | 3 | Financial Health Module | Financial health scoring |
| AI | 4 | AI Module | AI conversation and tool invocation |
| Audit | 1 | Audit Module | Immutable audit records |
| Configuration | 2 | Configuration Module | System configuration |
| **TOTAL** | **35** | **14 Modules** | **Complete MVP** |

---

## 3. Identity Domain

### 3.1 Domain Purpose

The Identity domain manages user authentication, credentials, sessions, devices, and OTP verification challenges.

**Tables**:
- `users` - User accounts
- `user_credentials` - Password and authentication credentials
- `user_sessions` - Active user sessions
- `user_devices` - Registered devices
- `otp_challenges` - OTP verification challenges

### 3.2 Users Table

**Table**: `users`

**Purpose**: Core user account information

**Columns**:
```sql
CREATE TABLE users (
    user_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) NOT NULL UNIQUE,
    phone_number VARCHAR(20) UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    account_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    email_verified BOOLEAN DEFAULT FALSE,
    phone_verified BOOLEAN DEFAULT FALSE,
    failed_login_attempts INTEGER DEFAULT 0,
    locked_until TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITH TIME ZONE
);
```

**Constraints**:
- PRIMARY KEY: `user_id`
- UNIQUE: `email`
- UNIQUE: `phone_number`
- CHECK: `account_status IN ('ACTIVE', 'SUSPENDED', 'LOCKED', 'DELETED')`
- NOT NULL: `email`, `password_hash`, `first_name`, `last_name`, `account_status`

**Indexes**:
- `idx_users_email` on `email`
- `idx_users_status` on `account_status`

**Security**:
- `password_hash` stores bcrypt hash, never plaintext
- `failed_login_attempts` tracks login failures for lockout
- `locked_until` supports temporary account lockout

**Soft Delete**:
- `deleted_at` for soft delete
- Account status set to 'DELETED' when soft deleted

### 3.3 User Credentials Table

**Table**: `user_credentials`

**Purpose**: Store authentication credentials separately from user profile

**Columns**:
```sql
CREATE TABLE user_credentials (
    credential_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    credential_type VARCHAR(20) NOT NULL,
    credential_value VARCHAR(255) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    expires_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

**Constraints**:
- PRIMARY KEY: `credential_id`
- FOREIGN KEY: `user_id` → `users(user_id)` ON DELETE CASCADE
- CHECK: `credential_type IN ('PASSWORD', 'SOCIAL', 'OTP')`
- NOT NULL: `user_id`, `credential_type`, `credential_value`

**Indexes**:
- `idx_user_credentials_user_id` on `user_id`
- `idx_user_credentials_type` on `credential_type`

**Security**:
- `credential_value` stores hashed credentials
- Supports multiple credential types (password, social, OTP)
- Credentials can be expired with `expires_at`

### 3.4 User Sessions Table

**Table**: `user_sessions`

**Purpose**: Track active user sessions for JWT token management

**Columns**:
```sql
CREATE TABLE user_sessions (
    session_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    device_id UUID REFERENCES user_devices(device_id) ON DELETE SET NULL,
    refresh_token_hash VARCHAR(255) NOT NULL,
    ip_address VARCHAR(45),
    user_agent TEXT,
    last_active_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    revoked_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

**Constraints**:
- PRIMARY KEY: `session_id`
- FOREIGN KEY: `user_id` → `users(user_id)` ON DELETE CASCADE
- FOREIGN KEY: `device_id` → `user_devices(device_id)` ON DELETE SET NULL
- NOT NULL: `user_id`, `refresh_token_hash`, `expires_at`

**Indexes**:
- `idx_user_sessions_user_id` on `user_id`
- `idx_user_sessions_device_id` on `device_id`
- `idx_user_sessions_expires_at` on `expires_at`

**Session Management**:
- `refresh_token_hash` stores hashed refresh token
- `expires_at` tracks session expiration
- `revoked_at` supports session revocation
- `last_active_at` tracks session activity

### 3.5 User Devices Table

**Table**: `user_devices`

**Purpose**: Track user devices for push notifications and session management

**Columns**:
```sql
CREATE TABLE user_devices (
    device_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    device_name VARCHAR(100),
    device_type VARCHAR(20) NOT NULL,
    device_token VARCHAR(500),
    platform VARCHAR(20),
    os_version VARCHAR(50),
    app_version VARCHAR(20),
    last_active_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

**Constraints**:
- PRIMARY KEY: `device_id`
- FOREIGN KEY: `user_id` → `users(user_id)` ON DELETE CASCADE
- CHECK: `device_type IN ('ANDROID', 'IOS', 'WEB')`
- NOT NULL: `user_id`, `device_type`

**Indexes**:
- `idx_user_devices_user_id` on `user_id`
- `idx_user_devices_type` on `device_type`
- `idx_user_devices_token` on `device_token`

**Push Notifications**:
- `device_token` stores push notification token
- `device_type` identifies platform (Android, iOS, Web)
- `is_active` supports device deactivation

### 3.6 OTP Challenges Table

**Table**: `otp_challenges`

**Purpose**: Store OTP verification challenges for registration, login, and password reset

**Columns**:
```sql
CREATE TABLE otp_challenges (
    otp_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(user_id) ON DELETE CASCADE,
    email VARCHAR(255),
    phone_number VARCHAR(20),
    otp_hash VARCHAR(255) NOT NULL,
    purpose VARCHAR(20) NOT NULL,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    attempts INTEGER DEFAULT 0,
    verified_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

**Constraints**:
- PRIMARY KEY: `otp_id`
- FOREIGN KEY: `user_id` → `users(user_id)` ON DELETE CASCADE
- CHECK: `purpose IN ('REGISTRATION', 'LOGIN', 'RESET')`
- NOT NULL: `otp_hash`, `purpose`, `expires_at`

**Indexes**:
- `idx_otp_challenges_user_id` on `user_id`
- `idx_otp_challenges_email` on `email`
- `idx_otp_challenges_phone` on `phone_number`
- `idx_otp_challenges_expires_at` on `expires_at`

**Security**:
- `otp_hash` stores hashed OTP, never plaintext
- `attempts` tracks OTP retry attempts
- `expires_at` enforces OTP expiration (10 minutes configurable)
- `verified_at` tracks successful verification

---

## 4. User Domain

### 4.1 Domain Purpose

The User domain manages user profiles, preferences, and additional user data.

**Tables**:
- `user_profiles` - Extended user profile information
- `user_preferences` - User notification and app preferences
- `user_addresses` - User addresses (optional for future)

### 4.2 User Profiles Table

**Table**: `user_profiles`

**Purpose**: Extended user profile information

**Columns**:
```sql
CREATE TABLE user_profiles (
    profile_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE REFERENCES users(user_id) ON DELETE CASCADE,
    profile_image_url VARCHAR(500),
    date_of_birth DATE,
    timezone VARCHAR(50) DEFAULT 'UTC',
    locale VARCHAR(10) DEFAULT 'en-US',
    currency VARCHAR(3) DEFAULT 'USD',
    family_id UUID REFERENCES families(family_id) ON DELETE SET NULL,
    family_role VARCHAR(20),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

**Constraints**:
- PRIMARY KEY: `profile_id`
- UNIQUE: `user_id`
- FOREIGN KEY: `user_id` → `users(user_id)` ON DELETE CASCADE
- FOREIGN KEY: `family_id` → `families(family_id)` ON DELETE SET NULL
- CHECK: `family_role IN ('OWNER', 'MEMBER', 'RESTRICTED', NULL)`
- NOT NULL: `user_id`

**Indexes**:
- `idx_user_profiles_user_id` on `user_id`
- `idx_user_profiles_family_id` on `family_id`

**Profile Data**:
- `profile_image_url` stores profile picture URL
- `timezone` defaults to UTC
- `locale` defaults to en-US
- `currency` defaults to USD
- `family_id` and `family_role` for family membership

### 4.3 User Preferences Table

**Table**: `user_preferences`

**Purpose**: User notification and app preferences

**Columns**:
```sql
CREATE TABLE user_preferences (
    preference_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE REFERENCES users(user_id) ON DELETE CASCADE,
    budget_alerts_enabled BOOLEAN DEFAULT TRUE,
    budget_alert_threshold_percentage INTEGER DEFAULT 80,
    bill_reminders_enabled BOOLEAN DEFAULT TRUE,
    bill_reminder_days INTEGER[] DEFAULT ARRAY[3, 7],
    savings_updates_enabled BOOLEAN DEFAULT TRUE,
    savings_updates_frequency VARCHAR(10) DEFAULT 'WEEKLY',
    financial_health_updates_enabled BOOLEAN DEFAULT TRUE,
    financial_health_updates_frequency VARCHAR(10) DEFAULT 'MONTHLY',
    ai_response_style VARCHAR(10) DEFAULT 'CONCISE',
    ai_language VARCHAR(10) DEFAULT 'en',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

**Constraints**:
- PRIMARY KEY: `preference_id`
- UNIQUE: `user_id`
- FOREIGN KEY: `user_id` → `users(user_id)` ON DELETE CASCADE
- CHECK: `budget_alert_threshold_percentage BETWEEN 0 AND 100`
- CHECK: `savings_updates_frequency IN ('DAILY', 'WEEKLY', 'MONTHLY')`
- CHECK: `financial_health_updates_frequency IN ('DAILY', 'WEEKLY', 'MONTHLY')`
- CHECK: `ai_response_style IN ('CONCISE', 'DETAILED')`
- NOT NULL: `user_id`

**Indexes**:
- `idx_user_preferences_user_id` on `user_id`

**Preferences**:
- Budget alerts with configurable threshold (default 80%)
- Bill reminders with configurable days (default 3 and 7 days before due)
- Savings updates with configurable frequency
- Financial health updates with configurable frequency
- AI response style (concise or detailed)
- AI language preference

### 4.4 User Addresses Table

**Table**: `user_addresses`

**Purpose**: User addresses for future payment provider integration

**Columns**:
```sql
CREATE TABLE user_addresses (
    address_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    address_type VARCHAR(20) NOT NULL,
    address_line1 VARCHAR(255) NOT NULL,
    address_line2 VARCHAR(255),
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100),
    postal_code VARCHAR(20) NOT NULL,
    country_code VARCHAR(2) NOT NULL,
    is_default BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

**Constraints**:
- PRIMARY KEY: `address_id`
- FOREIGN KEY: `user_id` → `users(user_id)` ON DELETE CASCADE
- CHECK: `address_type IN ('HOME', 'WORK', 'BILLING', 'SHIPPING')`
- NOT NULL: `user_id`, `address_type`, `address_line1`, `city`, `postal_code`, `country_code`

**Indexes**:
- `idx_user_addresses_user_id` on `user_id`
- `idx_user_addresses_type` on `address_type`

**Note**: This table is reserved for future payment provider integration.

---

## 5. Family Domain

### 5.1 Domain Purpose

The Family domain manages family creation, membership, invitations, and roles.

**Tables**:
- `families` - Family accounts
- `family_members` - Family membership
- `family_invitations` - Family invitations
- `family_roles` - Family role definitions (reference data)

### 5.2 Families Table

**Table**: `families`

**Purpose**: Family accounts for collaborative financial management

**Columns**:
```sql
CREATE TABLE families (
    family_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    owner_id UUID NOT NULL REFERENCES users(user_id) ON DELETE RESTRICT,
    member_count INTEGER DEFAULT 1,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITH TIME ZONE
);
```

**Constraints**:
- PRIMARY KEY: `family_id`
- FOREIGN KEY: `owner_id` → `users(user_id)` ON DELETE RESTRICT
- NOT NULL: `name`, `currency`, `owner_id`

**Indexes**:
- `idx_families_owner_id` on `owner_id`
- `idx_families_name` on `name`

**Family Ownership**:
- `owner_id` references the family owner
- ON DELETE RESTRICT prevents deletion of family with owner
- `member_count` tracks total family members
- Soft delete with `deleted_at`

### 5.3 Family Members Table

**Table**: `family_members`

**Purpose**: Family membership with roles

**Columns**:
```sql
CREATE TABLE family_members (
    member_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    family_id UUID NOT NULL REFERENCES families(family_id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    role VARCHAR(20) NOT NULL DEFAULT 'MEMBER',
    joined_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    left_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(family_id, user_id)
);
```

**Constraints**:
- PRIMARY KEY: `member_id`
- FOREIGN KEY: `family_id` → `families(family_id)` ON DELETE CASCADE
- FOREIGN KEY: `user_id` → `users(user_id)` ON DELETE CASCADE
- UNIQUE: `(family_id, user_id)`
- CHECK: `role IN ('OWNER', 'MEMBER', 'RESTRICTED')`
- NOT NULL: `family_id`, `user_id`, `role`

**Indexes**:
- `idx_family_members_family_id` on `family_id`
- `idx_family_members_user_id` on `user_id`
- `idx_family_members_role` on `role`

**Family Roles**:
- OWNER: Full family management
- MEMBER: View and edit family data
- RESTRICTED: Limited family data access
- `joined_at` tracks when user joined family
- `left_at` tracks when user left family

### 5.4 Family Invitations Table

**Table**: `family_invitations`

**Purpose**: Family member invitations

**Columns**:
```sql
CREATE TABLE family_invitations (
    invitation_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    family_id UUID NOT NULL REFERENCES families(family_id) ON DELETE CASCADE,
    inviter_id UUID NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    email VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'MEMBER',
    token VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    accepted_at TIMESTAMP WITH TIME ZONE,
    rejected_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

**Constraints**:
- PRIMARY KEY: `invitation_id`
- FOREIGN KEY: `family_id` → `families(family_id)` ON DELETE CASCADE
- FOREIGN KEY: `inviter_id` → `users(user_id)` ON DELETE CASCADE
- UNIQUE: `token`
- CHECK: `role IN ('MEMBER', 'RESTRICTED')`
- NOT NULL: `family_id`, `inviter_id`, `email`, `role`, `token`, `expires_at`

**Indexes**:
- `idx_family_invitations_family_id` on `family_id`
- `idx_family_invitations_email` on `email`
- `idx_family_invitations_token` on `token`
- `idx_family_invitations_expires_at` on `expires_at`

**Invitation Lifecycle**:
- `token` unique invitation token
- `expires_at` invitation expiration (7 days configurable)
- `accepted_at` tracks acceptance
- `rejected_at` tracks rejection

### 5.5 Family Roles Table

**Table**: `family_roles`

**Purpose**: Reference data for family roles

**Columns**:
```sql
CREATE TABLE family_roles (
    role_id VARCHAR(20) PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL,
    description TEXT,
    permissions JSONB,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

**Constraints**:
- PRIMARY KEY: `role_id`
- NOT NULL: `role_name`

**Seed Data**:
```sql
INSERT INTO family_roles (role_id, role_name, description, permissions) VALUES
('OWNER', 'Family Owner', 'Full family management privileges', '{"VIEW": true, "CREATE": true, "UPDATE": true, "DELETE": true, "INVITE": true, "APPROVE": true, "MANAGE_MEMBERS": true}'),
('MEMBER', 'Family Member', 'View and edit family data', '{"VIEW": true, "CREATE": true, "UPDATE": true, "DELETE": false, "INVITE": false, "APPROVE": false, "MANAGE_MEMBERS": false}'),
('RESTRICTED', 'Restricted Member', 'Limited family data access', '{"VIEW": true, "CREATE": false, "UPDATE": false, "DELETE": false, "INVITE": false, "APPROVE": false, "MANAGE_MEMBERS": false}');
```

---

## 6. Financial Overview Domain

### 6.1 Domain Purpose

The Financial Overview domain stores planning and allocation data. **CRITICAL**: These are planning values, NOT actual funds held by NeoWallet.

**Tables**:
- `financial_overviews` - Financial overview containers
- `financial_periods` - Planning periods
- `financial_allocations` - Category allocations

### 6.2 Financial Overviews Table

**Table**: `financial_overviews`

**Purpose**: Financial overview containers for users and families

**Columns**:
```sql
CREATE TABLE financial_overviews (
    overview_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(user_id) ON DELETE CASCADE,
    family_id UUID REFERENCES families(family_id) ON DELETE SET NULL,
    planning_income NUMERIC(15, 2) NOT NULL,
    mandatory_commitments NUMERIC(15, 2) DEFAULT 0,
    essential_allocation NUMERIC(15, 2) DEFAULT 0,
    variable_allocation NUMERIC(15, 2) DEFAULT 0,
    savings_allocation NUMERIC(15, 2) DEFAULT 0,
    emergency_allocation NUMERIC(15, 2) DEFAULT 0,
    discretionary_planning NUMERIC(15, 2) DEFAULT 0,
    committed_amount NUMERIC(15, 2) DEFAULT 0,
    pending_payments NUMERIC(15, 2) DEFAULT 0,
    actual_transactions NUMERIC(15, 2) DEFAULT 0,
    available_financial_capacity NUMERIC(15, 2) DEFAULT 0,
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    period VARCHAR(7) NOT NULL,
    calculated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CHECK (user_id IS NOT NULL OR family_id IS NOT NULL)
);
```

**Constraints**:
- PRIMARY KEY: `overview_id`
- FOREIGN KEY: `user_id` → `users(user_id)` ON DELETE CASCADE
- FOREIGN KEY: `family_id` → `families(family_id)` ON DELETE SET NULL
- CHECK: `(user_id IS NOT NULL OR family_id IS NOT NULL)`
- NOT NULL: `planning_income`, `currency`, `period`

**Indexes**:
- `idx_financial_overviews_user_id` on `user_id`
- `idx_financial_overviews_family_id` on `family_id`
- `idx_financial_overviews_period` on `period`
- UNIQUE: `(user_id, period)` where user_id is not null
- UNIQUE: `(family_id, period)` where family_id is not null

**Planning Values**:
- `planning_income`: User-defined monthly income (planning value)
- `mandatory_commitments`: Sum of mandatory bills/loans
- `essential_allocation`: Essential category budget
- `variable_allocation`: Variable category budget
- `savings_allocation`: Savings goal allocation
- `emergency_allocation`: Emergency fund allocation
- `discretionary_planning`: Discretionary spending allocation
- `committed_amount`: Sum of pending payments
- `pending_payments`: Pending bill payments
- `actual_transactions`: Actual transaction total (current period)
- `available_financial_capacity`: Planning income - committed - actual

**DISCLAIMER**: All values are planning/management representations, NOT actual NeoWallet-held funds.

### 6.3 Financial Periods Table

**Table**: `financial_periods`

**Purpose**: Planning periods for financial calculations

**Columns**:
```sql
CREATE TABLE financial_periods (
    period_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    overview_id UUID NOT NULL REFERENCES financial_overviews(overview_id) ON DELETE CASCADE,
    period_start DATE NOT NULL,
    period_end DATE NOT NULL,
    period_type VARCHAR(20) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

**Constraints**:
- PRIMARY KEY: `period_id`
- FOREIGN KEY: `overview_id` → `financial_overviews(overview_id)` ON DELETE CASCADE
- CHECK: `period_type IN ('MONTHLY', 'WEEKLY', 'CUSTOM')`
- NOT NULL: `overview_id`, `period_start`, `period_end`, `period_type`

**Indexes**:
- `idx_financial_periods_overview_id` on `overview_id`
- `idx_financial_periods_period` on `(period_start, period_end)`

### 6.4 Financial Allocations Table

**Table**: `financial_allocations`

**Purpose**: Category-level financial allocations

**Columns**:
```sql
CREATE TABLE financial_allocations (
    allocation_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    overview_id UUID NOT NULL REFERENCES financial_overviews(overview_id) ON DELETE CASCADE,
    category_id UUID REFERENCES categories(category_id) ON DELETE SET NULL,
    category_name VARCHAR(100) NOT NULL,
    allocated_amount NUMERIC(15, 2) NOT NULL,
    actual_amount NUMERIC(15, 2) DEFAULT 0,
    utilization_percentage NUMERIC(5, 2) DEFAULT 0,
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

**Constraints**:
- PRIMARY KEY: `allocation_id`
- FOREIGN KEY: `overview_id` → `financial_overviews(overview_id)` ON DELETE CASCADE
- FOREIGN KEY: `category_id` → `categories(category_id)` ON DELETE SET NULL
- NOT NULL: `overview_id`, `category_name`, `allocated_amount`, `currency`

**Indexes**:
- `idx_financial_allocations_overview_id` on `overview_id`
- `idx_financial_allocations_category_id` on `category_id`

---

## 7. Transaction Domain

### 7.1 Domain Purpose

The Transaction domain records all financial transactions (income, expenses, refunds, adjustments, transfer records).

**Tables**:
- `transactions` - Transaction records

### 7.2 Transactions Table

**Table**: `transactions`

**Purpose**: Transaction records for income, expenses, refunds, adjustments, and transfer records

**Columns**:
```sql
CREATE TABLE transactions (
    transaction_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    family_id UUID REFERENCES families(family_id) ON DELETE SET NULL,
    family_member_id UUID REFERENCES family_members(member_id) ON DELETE SET NULL,
    type VARCHAR(20) NOT NULL,
    category_id UUID REFERENCES categories(category_id) ON DELETE SET NULL,
    category_name VARCHAR(100) NOT NULL,
    amount NUMERIC(15, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    description TEXT,
    transaction_date DATE NOT NULL,
    posted_date DATE,
    status VARCHAR(20) NOT NULL DEFAULT 'PLANNED',
    is_recurring BOOLEAN DEFAULT FALSE,
    recurring_pattern VARCHAR(50),
    external_reference VARCHAR(100),
    source VARCHAR(20) DEFAULT 'MANUAL',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITH TIME ZONE
);
```

**Constraints**:
- PRIMARY KEY: `transaction_id`
- FOREIGN KEY: `user_id` → `users(user_id)` ON DELETE CASCADE
- FOREIGN KEY: `family_id` → `families(family_id)` ON DELETE SET NULL
- FOREIGN KEY: `family_member_id` → `family_members(member_id)` ON DELETE SET NULL
- FOREIGN KEY: `category_id` → `categories(category_id)` ON DELETE SET NULL
- CHECK: `type IN ('INCOME', 'EXPENSE', 'REFUND', 'ADJUSTMENT', 'TRANSFER_RECORD')`
- CHECK: `status IN ('PLANNED', 'COMMITTED', 'PENDING', 'COMPLETED', 'FAILED', 'REVERSED')`
- CHECK: `source IN ('MANUAL', 'IMPORTED', 'PROVIDER')`
- NOT NULL: `user_id`, `type`, `category_name`, `amount`, `currency`, `transaction_date`, `status`

**Indexes**:
- `idx_transactions_user_id` on `user_id`
- `idx_transactions_family_id` on `family_id`
- `idx_transactions_family_member_id` on `family_member_id`
- `idx_transactions_type` on `type`
- `idx_transactions_category_id` on `category_id`
- `idx_transactions_transaction_date` on `transaction_date`
- `idx_transactions_status` on `status`
- `idx_transactions_is_recurring` on `is_recurring`

**Transaction Types**:
- INCOME: Money received
- EXPENSE: Money spent
- REFUND: Money returned
- ADJUSTMENT: Manual adjustment
- TRANSFER_RECORD: Transfer between accounts (record only)

**Transaction Statuses**:
- PLANNED: Planned transaction
- COMMITTED: Committed to be executed
- PENDING: Pending execution
- COMPLETED: Successfully executed
- FAILED: Execution failed
- REVERSED: Transaction reversed

**Money Storage**:
- `amount` uses NUMERIC(15, 2) for precise decimal storage
- Never use FLOAT or DOUBLE for money
- Currency code stored separately (ISO 4217)

**Soft Delete**:
- `deleted_at` for soft delete
- Financial records should generally NOT be soft deleted (see section 19)

---

## 8. Category Domain

### 8.1 Domain Purpose

The Category domain provides configurable transaction categories.

**Tables**:
- `categories` - Transaction categories

### 8.2 Categories Table

**Table**: `categories`

**Purpose**: Configurable transaction categories

**Columns**:
```sql
CREATE TABLE categories (
    category_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    parent_category_id UUID REFERENCES categories(category_id) ON DELETE SET NULL,
    category_type VARCHAR(20) NOT NULL,
    priority VARCHAR(20) DEFAULT 'VARIABLE',
    icon VARCHAR(50),
    color VARCHAR(7),
    is_system BOOLEAN DEFAULT TRUE,
    is_active BOOLEAN DEFAULT TRUE,
    created_by UUID REFERENCES users(user_id) ON DELETE SET NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(name, parent_category_id)
);
```

**Constraints**:
- PRIMARY KEY: `category_id`
- FOREIGN KEY: `parent_category_id` → `categories(category_id)` ON DELETE SET NULL
- FOREIGN KEY: `created_by` → `users(user_id)` ON DELETE SET NULL
- CHECK: `category_type IN ('ESSENTIAL', 'VARIABLE', 'DISCRETIONARY', 'INCOME', 'SAVINGS', 'EMERGENCY')`
- CHECK: `priority IN ('ESSENTIAL', 'VARIABLE', 'DISCRETIONARY')`
- UNIQUE: `(name, parent_category_id)`
- NOT NULL: `name`, `category_type`

**Indexes**:
- `idx_categories_parent_id` on `parent_category_id`
- `idx_categories_type` on `category_type`
- `idx_categories_priority` on `priority`
- `idx_categories_is_system` on `is_system`
- `idx_categories_is_active` on `is_active`

**Default Categories** (from Budget Allocation Algorithm):
- Housing
- Groceries
- Utilities
- Transportation
- Education
- Healthcare
- Insurance
- Entertainment
- Subscriptions
- Personal
- Family
- Savings
- Emergency
- Other

**Category Types**:
- ESSENTIAL: Essential expenses
- VARIABLE: Variable expenses
- DISCRETIONARY: Discretionary spending
- INCOME: Income categories
- SAVINGS: Savings categories
- EMERGENCY: Emergency fund

---

## 9. Budget Domain

### 9.1 Domain Purpose

The Budget domain manages budget planning, tracking, and recommendations.

**Tables**:
- `budgets` - Budget containers
- `budget_categories` - Category-level budgets
- `budget_periods` - Budget periods
- `budget_recommendations` - Budget recommendations

### 9.2 Budgets Table

**Table**: `budgets`

**Purpose**: Budget containers for users and families

**Columns**:
```sql
CREATE TABLE budgets (
    budget_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(user_id) ON DELETE CASCADE,
    family_id UUID REFERENCES families(family_id) ON DELETE SET NULL,
    name VARCHAR(100) NOT NULL,
    period VARCHAR(7) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    total_limit NUMERIC(15, 2) NOT NULL,
    total_spent NUMERIC(15, 2) DEFAULT 0,
    utilization_percentage NUMERIC(5, 2) DEFAULT 0,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    version INTEGER DEFAULT 1,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CHECK (user_id IS NOT NULL OR family_id IS NOT NULL)
);
```

**Constraints**:
- PRIMARY KEY: `budget_id`
- FOREIGN KEY: `user_id` → `users(user_id)` ON DELETE CASCADE
- FOREIGN KEY: `family_id` → `families(family_id)` ON DELETE SET NULL
- CHECK: `status IN ('ACTIVE', 'ARCHIVED', 'DELETED')`
- CHECK: `(user_id IS NOT NULL OR family_id IS NOT NULL)`
- NOT NULL: `name`, `period`, `currency`, `total_limit`

**Indexes**:
- `idx_budgets_user_id` on `user_id`
- `idx_budgets_family_id` on `family_id`
- `idx_budgets_period` on `period`
- `idx_budgets_status` on `status`
- UNIQUE: `(user_id, period, name)` where user_id is not null
- UNIQUE: `(family_id, period, name)` where family_id is not null

**Budget Versioning**:
- `version` tracks budget versions
- Budgets can be archived but not deleted (soft delete)

### 9.3 Budget Categories Table

**Table**: `budget_categories`

**Purpose**: Category-level budget limits

**Columns**:
```sql
CREATE TABLE budget_categories (
    budget_category_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    budget_id UUID NOT NULL REFERENCES budgets(budget_id) ON DELETE CASCADE,
    category_id UUID REFERENCES categories(category_id) ON DELETE SET NULL,
    category_name VARCHAR(100) NOT NULL,
    limit_amount NUMERIC(15, 2) NOT NULL,
    spent_amount NUMERIC(15, 2) DEFAULT 0,
    utilization_percentage NUMERIC(5, 2) DEFAULT 0,
    priority VARCHAR(20) DEFAULT 'VARIABLE',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

**Constraints**:
- PRIMARY KEY: `budget_category_id`
- FOREIGN KEY: `budget_id` → `budgets(budget_id)` ON DELETE CASCADE
- FOREIGN KEY: `category_id` → `categories(category_id)` ON DELETE SET NULL
- CHECK: `priority IN ('ESSENTIAL', 'VARIABLE', 'DISCRETIONARY')`
- NOT NULL: `budget_id`, `category_name`, `limit_amount`

**Indexes**:
- `idx_budget_categories_budget_id` on `budget_id`
- `idx_budget_categories_category_id` on `category_id`
- `idx_budget_categories_priority` on `priority`

### 9.4 Budget Periods Table

**Table**: `budget_periods`

**Purpose**: Budget period definitions

**Columns**:
```sql
CREATE TABLE budget_periods (
    period_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    budget_id UUID NOT NULL REFERENCES budgets(budget_id) ON DELETE CASCADE,
    period_start DATE NOT NULL,
    period_end DATE NOT NULL,
    period_type VARCHAR(20) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

**Constraints**:
- PRIMARY KEY: `period_id`
- FOREIGN KEY: `budget_id` → `budgets(budget_id)` ON DELETE CASCADE
- CHECK: `period_type IN ('MONTHLY', 'WEEKLY', 'CUSTOM')`
- NOT NULL: `budget_id`, `period_start`, `period_end`, `period_type`

**Indexes**:
- `idx_budget_periods_budget_id` on `budget_id`
- `idx_budget_periods_period` on `(period_start, period_end)`

### 9.5 Budget Recommendations Table

**Table**: `budget_recommendations`

**Purpose**: Budget recommendations from system

**Columns**:
```sql
CREATE TABLE budget_recommendations (
    recommendation_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    budget_id UUID REFERENCES budgets(budget_id) ON DELETE SET NULL,
    user_id UUID NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    family_id UUID REFERENCES families(family_id) ON DELETE SET NULL,
    period VARCHAR(7) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    recommended_allocation JSONB NOT NULL,
    user_modification JSONB,
    final_approved_budget JSONB,
    status VARCHAR(20) DEFAULT 'PENDING',
    confidence VARCHAR(10) DEFAULT 'MEDIUM',
    historical_months_used INTEGER DEFAULT 0,
    generated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    approved_at TIMESTAMP WITH TIME ZONE
);
```

**Constraints**:
- PRIMARY KEY: `recommendation_id`
- FOREIGN KEY: `budget_id` → `budgets(budget_id)` ON DELETE SET NULL
- FOREIGN KEY: `user_id` → `users(user_id)` ON DELETE CASCADE
- FOREIGN KEY: `family_id` → `families(family_id)` ON DELETE SET NULL
- CHECK: `status IN ('PENDING', 'VIEWED', 'MODIFIED', 'APPROVED', 'REJECTED', 'SUPERSEDED')`
- CHECK: `confidence IN ('HIGH', 'MEDIUM', 'LOW')`
- NOT NULL: `user_id`, `period`, `currency`, `recommended_allocation`

**Indexes**:
- `idx_budget_recommendations_budget_id` on `budget_id`
- `idx_budget_recommendations_user_id` on `user_id`
- `idx_budget_recommendations_family_id` on `family_id`
- `idx_budget_recommendations_period` on `period`
- `idx_budget_recommendations_status` on `status`

**Recommendation Lifecycle**:
- PENDING: Generated, not yet viewed
- VIEWED: User has viewed
- MODIFIED: User has modified
- APPROVED: User has approved
- REJECTED: User has rejected
- SUPERSEDED: Superseded by newer recommendation

**Preserved Data**:
- `recommended_allocation`: System recommendation
- `user_modification`: User modifications
- `final_approved_budget`: Final approved budget

---

## 10. Allocation Domain

### 10.1 Domain Purpose

The Allocation domain manages allocation recommendations for household budget planning.

**Tables**:
- `allocation_recommendations` - Allocation recommendations
- `allocation_categories` - Category-level allocations
- `allocation_versions` - Allocation version history
- `allocation_approvals` - Allocation approval records

### 10.2 Allocation Recommendations Table

**Table**: `allocation_recommendations`

**Purpose**: Allocation recommendations for household budget planning

**Columns**:
```sql
CREATE TABLE allocation_recommendations (
    recommendation_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    family_id UUID REFERENCES families(family_id) ON DELETE SET NULL,
    period VARCHAR(7) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    system_recommendation JSONB NOT NULL,
    user_modification JSONB,
    final_approved_allocation JSONB,
    status VARCHAR(20) DEFAULT 'PENDING',
    confidence VARCHAR(10) DEFAULT 'MEDIUM',
    historical_months_used INTEGER DEFAULT 0,
    generated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    approved_at TIMESTAMP WITH TIME ZONE
);
```

**Constraints**:
- PRIMARY KEY: `recommendation_id`
- FOREIGN KEY: `user_id` → `users(user_id)` ON DELETE CASCADE
- FOREIGN KEY: `family_id` → `families(family_id)` ON DELETE SET NULL
- CHECK: `status IN ('PENDING', 'VIEWED', 'MODIFIED', 'APPROVED', 'REJECTED', 'SUPERSEDED')`
- CHECK: `confidence IN ('HIGH', 'MEDIUM', 'LOW')`
- NOT NULL: `user_id`, `period`, `currency`, `system_recommendation`

**Indexes**:
- `idx_allocation_recommendations_user_id` on `user_id`
- `idx_allocation_recommendations_family_id` on `family_id`
- `idx_allocation_recommendations_period` on `period`
- `idx_allocation_recommendations_status` on `status`

**Preserved Lifecycle**:
- `system_recommendation`: Original system recommendation
- `user_modification`: User modifications
- `final_approved_allocation`: Final approved allocation

### 10.3 Allocation Categories Table

**Table**: `allocation_categories`

**Purpose**: Category-level allocation details

**Columns**:
```sql
CREATE TABLE allocation_categories (
    allocation_category_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    recommendation_id UUID NOT NULL REFERENCES allocation_recommendations(recommendation_id) ON DELETE CASCADE,
    category_id UUID REFERENCES categories(category_id) ON DELETE SET NULL,
    category_name VARCHAR(100) NOT NULL,
    recommended_amount NUMERIC(15, 2) NOT NULL,
    modified_amount NUMERIC(15, 2),
    final_amount NUMERIC(15, 2),
    priority VARCHAR(20) DEFAULT 'VARIABLE',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

**Constraints**:
- PRIMARY KEY: `allocation_category_id`
- FOREIGN KEY: `recommendation_id` → `allocation_recommendations(recommendation_id)` ON DELETE CASCADE
- FOREIGN KEY: `category_id` → `categories(category_id)` ON DELETE SET NULL
- CHECK: `priority IN ('ESSENTIAL', 'VARIABLE', 'DISCRETIONARY')`
- NOT NULL: `recommendation_id`, `category_name`, `recommended_amount`

**Indexes**:
- `idx_allocation_categories_recommendation_id` on `recommendation_id`
- `idx_allocation_categories_category_id` on `category_id`

### 10.4 Allocation Versions Table

**Table**: `allocation_versions`

**Purpose**: Allocation version history

**Columns**:
```sql
CREATE TABLE allocation_versions (
    version_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    recommendation_id UUID NOT NULL REFERENCES allocation_recommendations(recommendation_id) ON DELETE CASCADE,
    version_number INTEGER NOT NULL,
    allocation_data JSONB NOT NULL,
    change_reason TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

**Constraints**:
- PRIMARY KEY: `version_id`
- FOREIGN KEY: `recommendation_id` → `allocation_recommendations(recommendation_id)` ON DELETE CASCADE
- NOT NULL: `recommendation_id`, `version_number`, `allocation_data`

**Indexes**:
- `idx_allocation_versions_recommendation_id` on `recommendation_id`
- `idx_allocation_versions_version_number` on `version_number`

### 10.5 Allocation Approvals Table

**Table**: `allocation_approvals`

**Purpose**: Allocation approval records

**Columns**:
```sql
CREATE TABLE allocation_approvals (
    approval_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    recommendation_id UUID NOT NULL REFERENCES allocation_recommendations(recommendation_id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    action VARCHAR(20) NOT NULL,
    approval_data JSONB,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

**Constraints**:
- PRIMARY KEY: `approval_id`
- FOREIGN KEY: `recommendation_id` → `allocation_recommendations(recommendation_id)` ON DELETE CASCADE
- FOREIGN KEY: `user_id` → `users(user_id)` ON DELETE CASCADE
- CHECK: `action IN ('APPROVE', 'MODIFY', 'REJECT')`
- NOT NULL: `recommendation_id`, `user_id`, `action`

**Indexes**:
- `idx_allocation_approvals_recommendation_id` on `recommendation_id`
- `idx_allocation_approvals_user_id` on `user_id`

---

## 11. Savings Domain

### 11.1 Domain Purpose

The Savings domain manages savings goals and progress tracking. **CRITICAL**: These are planning/tracking values, NOT actual NeoWallet-held funds.

**Tables**:
- `savings_goals` - Savings goals
- `savings_contributions` - Savings goal contributions
- `savings_progress` - Savings goal progress snapshots

### 11.2 Savings Goals Table

**Table**: `savings_goals`

**Purpose**: Savings goals for users and families

**Columns**:
```sql
CREATE TABLE savings_goals (
    goal_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(user_id) ON DELETE CASCADE,
    family_id UUID REFERENCES families(family_id) ON DELETE SET NULL,
    name VARCHAR(100) NOT NULL,
    target_amount NUMERIC(15, 2) NOT NULL,
    current_amount NUMERIC(15, 2) DEFAULT 0,
    target_date DATE NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    priority VARCHAR(20) DEFAULT 'MEDIUM',
    category VARCHAR(50),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    progress_percentage NUMERIC(5, 2) DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CHECK (user_id IS NOT NULL OR family_id IS NOT NULL)
);
```

**Constraints**:
- PRIMARY KEY: `goal_id`
- FOREIGN KEY: `user_id` → `users(user_id)` ON DELETE CASCADE
- FOREIGN KEY: `family_id` → `families(family_id)` ON DELETE SET NULL
- CHECK: `priority IN ('HIGH', 'MEDIUM', 'LOW')`
- CHECK: `status IN ('ACTIVE', 'COMPLETED', 'CANCELLED')`
- CHECK: `(user_id IS NOT NULL OR family_id IS NOT NULL)`
- NOT NULL: `name`, `target_amount`, `target_date`, `currency`

**Indexes**:
- `idx_savings_goals_user_id` on `user_id`
- `idx_savings_goals_family_id` on `family_id`
- `idx_savings_goals_status` on `status`
- `idx_savings_goals_priority` on `priority`
- `idx_savings_goals_target_date` on `target_date`

**DISCLAIMER**: `current_amount` represents tracked progress, NOT actual NeoWallet-held funds.

### 11.3 Savings Contributions Table

**Table**: `savings_contributions`

**Purpose**: Savings goal contribution records

**Columns**:
```sql
CREATE TABLE savings_contributions (
    contribution_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    goal_id UUID NOT NULL REFERENCES savings_goals(goal_id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    amount NUMERIC(15, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    contribution_date DATE NOT NULL,
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

**Constraints**:
- PRIMARY KEY: `contribution_id`
- FOREIGN KEY: `goal_id` → `savings_goals(goal_id)` ON DELETE CASCADE
- FOREIGN KEY: `user_id` → `users(user_id)` ON DELETE CASCADE
- NOT NULL: `goal_id`, `user_id`, `amount`, `currency`, `contribution_date`

**Indexes**:
- `idx_savings_contributions_goal_id` on `goal_id`
- `idx_savings_contributions_user_id` on `user_id`
- `idx_savings_contributions_contribution_date` on `contribution_date`

**DISCLAIMER**: Contributions represent planned/recorded savings, NOT actual NeoWallet-held funds.

### 11.4 Savings Progress Table

**Table**: `savings_progress`

**Purpose**: Savings goal progress snapshots

**Columns**:
```sql
CREATE TABLE savings_progress (
    progress_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    goal_id UUID NOT NULL REFERENCES savings_goals(goal_id) ON DELETE CASCADE,
    current_amount NUMERIC(15, 2) NOT NULL,
    progress_percentage NUMERIC(5, 2) NOT NULL,
    remaining_amount NUMERIC(15, 2) NOT NULL,
    months_remaining INTEGER,
    required_monthly_contribution NUMERIC(15, 2),
    actual_monthly_contribution NUMERIC(15, 2),
    on_track BOOLEAN,
    status VARCHAR(20),
    calculated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

**Constraints**:
- PRIMARY KEY: `progress_id`
- FOREIGN KEY: `goal_id` → `savings_goals(goal_id)` ON DELETE CASCADE
- CHECK: `status IN ('ON_TRACK', 'BEHIND', 'AHEAD')`
- NOT NULL: `goal_id`, `current_amount`, `progress_percentage`, `remaining_amount`

**Indexes**:
- `idx_savings_progress_goal_id` on `goal_id`
- `idx_savings_progress_calculated_at` on `calculated_at`

---

## 12. Bill Domain

### 12.1 Domain Purpose

The Bill domain manages bill tracking and payment status recording. **CRITICAL**: MVP bills are manually entered/planned. "Mark Paid" only records status, does NOT execute payment.

**Tables**:
- `bills` - Bill records
- `bill_categories` - Bill categories
- `bill_status_history` - Bill status history

### 12.2 Bills Table

**Table**: `bills`

**Purpose**: Bill records for tracking and reminders

**Columns**:
```sql
CREATE TABLE bills (
    bill_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    family_id UUID REFERENCES families(family_id) ON DELETE SET NULL,
    family_member_id UUID REFERENCES family_members(member_id) ON DELETE SET NULL,
    name VARCHAR(100) NOT NULL,
    amount NUMERIC(15, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    due_date DATE NOT NULL,
    category VARCHAR(50),
    is_recurring BOOLEAN DEFAULT FALSE,
    recurring_period VARCHAR(20),
    vendor VARCHAR(100),
    notes TEXT,
    status VARCHAR(20) DEFAULT 'PENDING',
    paid_date DATE,
    payment_method VARCHAR(50),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

**Constraints**:
- PRIMARY KEY: `bill_id`
- FOREIGN KEY: `user_id` → `users(user_id)` ON DELETE CASCADE
- FOREIGN KEY: `family_id` → `families(family_id)` ON DELETE SET NULL
- FOREIGN KEY: `family_member_id` → `family_members(member_id)` ON DELETE SET NULL
- CHECK: `recurring_period IN ('MONTHLY', 'WEEKLY', 'YEARLY', NULL)`
- CHECK: `status IN ('PENDING', 'PAID', 'OVERDUE')`
- NOT NULL: `user_id`, `name`, `amount`, `currency`, `due_date`

**Indexes**:
- `idx_bills_user_id` on `user_id`
- `idx_bills_family_id` on `family_id`
- `idx_bills_family_member_id` on `family_member_id`
- `idx_bills_status` on `status`
- `idx_bills_due_date` on `due_date`
- `idx_bills_is_recurring` on `is_recurring`

**Bill Status**:
- PENDING: Not yet paid
- PAID: User has marked as paid
- OVERDUE: Past due date and not paid

**CRITICAL**: `paid_date` and `payment_method` only record user's declaration of payment. NeoWallet does NOT execute payment.

### 12.3 Bill Categories Table

**Table**: `bill_categories`

**Purpose**: Bill category reference data

**Columns**:
```sql
CREATE TABLE bill_categories (
    category_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    description TEXT,
    icon VARCHAR(50),
    color VARCHAR(7),
    is_system BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

**Constraints**:
- PRIMARY KEY: `category_id`
- NOT NULL: `name`

**Indexes**:
- `idx_bill_categories_name` on `name`

### 12.4 Bill Status History Table

**Table**: `bill_status_history`

**Purpose**: Bill status change history

**Columns**:
```sql
CREATE TABLE bill_status_history (
    history_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    bill_id UUID NOT NULL REFERENCES bills(bill_id) ON DELETE CASCADE,
    old_status VARCHAR(20),
    new_status VARCHAR(20) NOT NULL,
    changed_by UUID NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    changed_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    notes TEXT
);
```

**Constraints**:
- PRIMARY KEY: `history_id`
- FOREIGN KEY: `bill_id` → `bills(bill_id)` ON DELETE CASCADE
- FOREIGN KEY: `changed_by` → `users(user_id)` ON DELETE CASCADE
- NOT NULL: `bill_id`, `new_status`, `changed_by`, `changed_at`

**Indexes**:
- `idx_bill_status_history_bill_id` on `bill_id`
- `idx_bill_status_history_changed_at` on `changed_at`

---

## 13. Notification Domain

### 13.1 Domain Purpose

The Notification domain manages notification delivery and tracking.

**Tables**:
- `notifications` - Notification records
- `notification_preferences` - User notification preferences
- `notification_deliveries` - Notification delivery tracking

### 13.2 Notifications Table

**Table**: `notifications`

**Purpose**: Notification records for users

**Columns**:
```sql
CREATE TABLE notifications (
    notification_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    type VARCHAR(50) NOT NULL,
    title VARCHAR(200) NOT NULL,
    message TEXT NOT NULL,
    data JSONB,
    is_read BOOLEAN DEFAULT FALSE,
    read_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

**Constraints**:
- PRIMARY KEY: `notification_id`
- FOREIGN KEY: `user_id` → `users(user_id)` ON DELETE CASCADE
- CHECK: `type IN ('BUDGET_ALERT', 'BILL_REMINDER', 'SAVINGS_UPDATE', 'FINANCIAL_HEALTH_UPDATE', 'SYSTEM')`
- NOT NULL: `user_id`, `type`, `title`, `message`

**Indexes**:
- `idx_notifications_user_id` on `user_id`
- `idx_notifications_type` on `type`
- `idx_notifications_is_read` on `is_read`
- `idx_notifications_created_at` on `created_at`

### 13.3 Notification Preferences Table

**Table**: `notification_preferences`

**Purpose**: User notification preferences (separate from user_preferences for extensibility)

**Columns**:
```sql
CREATE TABLE notification_preferences (
    preference_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE REFERENCES users(user_id) ON DELETE CASCADE,
    budget_alerts_enabled BOOLEAN DEFAULT TRUE,
    budget_alert_threshold_percentage INTEGER DEFAULT 80,
    bill_reminders_enabled BOOLEAN DEFAULT TRUE,
    bill_reminder_days INTEGER[] DEFAULT ARRAY[3, 7],
    savings_updates_enabled BOOLEAN DEFAULT TRUE,
    savings_updates_frequency VARCHAR(10) DEFAULT 'WEEKLY',
    financial_health_updates_enabled BOOLEAN DEFAULT TRUE,
    financial_health_updates_frequency VARCHAR(10) DEFAULT 'MONTHLY',
    push_enabled BOOLEAN DEFAULT TRUE,
    email_enabled BOOLEAN DEFAULT FALSE,
    sms_enabled BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

**Constraints**:
- PRIMARY KEY: `preference_id`
- UNIQUE: `user_id`
- FOREIGN KEY: `user_id` → `users(user_id)` ON DELETE CASCADE
- CHECK: `budget_alert_threshold_percentage BETWEEN 0 AND 100`
- CHECK: `savings_updates_frequency IN ('DAILY', 'WEEKLY', 'MONTHLY')`
- CHECK: `financial_health_updates_frequency IN ('DAILY', 'WEEKLY', 'MONTHLY')`
- NOT NULL: `user_id`

**Indexes**:
- `idx_notification_preferences_user_id` on `user_id`

### 13.4 Notification Deliveries Table

**Table**: `notification_deliveries`

**Purpose**: Notification delivery tracking

**Columns**:
```sql
CREATE TABLE notification_deliveries (
    delivery_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    notification_id UUID NOT NULL REFERENCES notifications(notification_id) ON DELETE CASCADE,
    channel VARCHAR(20) NOT NULL,
    status VARCHAR(20) DEFAULT 'QUEUED',
    queued_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    sent_at TIMESTAMP WITH TIME ZONE,
    delivered_at TIMESTAMP WITH TIME ZONE,
    failed_at TIMESTAMP WITH TIME ZONE,
    error_message TEXT,
    provider_reference VARCHAR(100)
);
```

**Constraints**:
- PRIMARY KEY: `delivery_id`
- FOREIGN KEY: `notification_id` → `notifications(notification_id)` ON DELETE CASCADE
- CHECK: `channel IN ('PUSH', 'EMAIL', 'SMS')`
- CHECK: `status IN ('QUEUED', 'SENT', 'DELIVERED', 'FAILED')`
- NOT NULL: `notification_id`, `channel`, `status`, `queued_at`

**Indexes**:
- `idx_notification_deliveries_notification_id` on `notification_id`
- `idx_notification_deliveries_channel` on `channel`
- `idx_notification_deliveries_status` on `status`

---

## 14. Financial Health Domain

### 14.1 Domain Purpose

The Financial Health domain stores financial health scores and factor calculations. **CRITICAL**: Score is calculated by deterministic engine, NOT AI.

**Tables**:
- `financial_health_scores` - Financial health scores
- `financial_health_factors` - Factor scores
- `financial_health_calculations` - Calculation snapshots

### 14.2 Financial Health Scores Table

**Table**: `financial_health_scores`

**Purpose**: Financial health scores for users and families

**Columns**:
```sql
CREATE TABLE financial_health_scores (
    score_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(user_id) ON DELETE CASCADE,
    family_id UUID REFERENCES families(family_id) ON DELETE SET NULL,
    overall_score INTEGER NOT NULL,
    score_label VARCHAR(20) NOT NULL,
    confidence VARCHAR(10) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'FINAL',
    calculated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CHECK (user_id IS NOT NULL OR family_id IS NOT NULL)
);
```

**Constraints**:
- PRIMARY KEY: `score_id`
- FOREIGN KEY: `user_id` → `users(user_id)` ON DELETE CASCADE
- FOREIGN KEY: `family_id` → `families(family_id)` ON DELETE SET NULL
- CHECK: `overall_score BETWEEN 0 AND 100`
- CHECK: `score_label IN ('CRITICAL', 'NEEDS_ATTENTION', 'FAIR', 'GOOD', 'EXCELLENT')`
- CHECK: `confidence IN ('HIGH', 'MEDIUM', 'LOW')`
- CHECK: `status IN ('FINAL', 'PROVISIONAL')`
- CHECK: `(user_id IS NOT NULL OR family_id IS NOT NULL)`
- NOT NULL: `overall_score`, `score_label`, `confidence`, `status`

**Indexes**:
- `idx_financial_health_scores_user_id` on `user_id`
- `idx_financial_health_scores_family_id` on `family_id`
- `idx_financial_health_scores_calculated_at` on `calculated_at`
- UNIQUE: `(user_id, calculated_at)` where user_id is not null
- UNIQUE: `(family_id, calculated_at)` where family_id is not null

**Score Labels**:
- 0-39: CRITICAL
- 40-59: NEEDS_ATTENTION
- 60-74: FAIR
- 75-89: GOOD
- 90-100: EXCELLENT

### 14.3 Financial Health Factors Table

**Table**: `financial_health_factors`

**Purpose**: Factor-level scores for financial health

**Columns**:
```sql
CREATE TABLE financial_health_factors (
    factor_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    score_id UUID NOT NULL REFERENCES financial_health_scores(score_id) ON DELETE CASCADE,
    factor_name VARCHAR(50) NOT NULL,
    factor_score INTEGER NOT NULL,
    weight NUMERIC(5, 2) NOT NULL,
    contribution NUMERIC(5, 2) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

**Constraints**:
- PRIMARY KEY: `factor_id`
- FOREIGN KEY: `score_id` → `financial_health_scores(score_id)` ON DELETE CASCADE
- CHECK: `factor_score BETWEEN 0 AND 100`
- CHECK: `weight BETWEEN 0 AND 1`
- NOT NULL: `score_id`, `factor_name`, `factor_score`, `weight`, `contribution`

**Indexes**:
- `idx_financial_health_factors_score_id` on `score_id`
- `idx_financial_health_factors_factor_name` on `factor_name`

**Factors**:
- budget_adherence (20%)
- savings_behavior (20%)
- expense_trend (15%)
- bill_discipline (15%)
- emergency_preparedness (15%)
- goal_progress (15%)

### 14.4 Financial Health Calculations Table

**Table**: `financial_health_calculations`

**Purpose**: Calculation snapshots for reproducibility

**Columns**:
```sql
CREATE TABLE financial_health_calculations (
    calculation_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    score_id UUID NOT NULL REFERENCES financial_health_scores(score_id) ON DELETE CASCADE,
    calculation_version VARCHAR(20) NOT NULL,
    input_snapshot JSONB NOT NULL,
    factor_scores JSONB NOT NULL,
    weights JSONB NOT NULL,
    calculated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

**Constraints**:
- PRIMARY KEY: `calculation_id`
- FOREIGN KEY: `score_id` → `financial_health_scores(score_id)` ON DELETE CASCADE
- NOT NULL: `score_id`, `calculation_version`, `input_snapshot`, `factor_scores`, `weights`

**Indexes**:
- `idx_financial_health_calculations_score_id` on `score_id`
- `idx_financial_health_calculations_version` on `calculation_version`

**Reproducibility**:
- `input_snapshot`: Input data snapshot
- `factor_scores`: Factor scores
- `weights`: Factor weights
- Historical scores must be reproducible

---

## 15. AI Domain

### 15.1 Domain Purpose

The AI domain stores AI conversations, messages, and tool invocations. **CRITICAL**: AI has NO direct database access. AI accesses data only through authorized application tools/APIs.

**Tables**:
- `ai_conversations` - AI conversations
- `ai_messages` - AI conversation messages
- `ai_tool_invocations` - AI tool invocations
- `ai_recommendations` - AI recommendations

### 15.2 AI Conversations Table

**Table**: `ai_conversations`

**Purpose**: AI conversation containers

**Columns**:
```sql
CREATE TABLE ai_conversations (
    conversation_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    family_id UUID REFERENCES families(family_id) ON DELETE SET NULL,
    title VARCHAR(200),
    message_count INTEGER DEFAULT 0,
    last_message_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

**Constraints**:
- PRIMARY KEY: `conversation_id`
- FOREIGN KEY: `user_id` → `users(user_id)` ON DELETE CASCADE
- FOREIGN KEY: `family_id` → `families(family_id)` ON DELETE SET NULL
- NOT NULL: `user_id`

**Indexes**:
- `idx_ai_conversations_user_id` on `user_id`
- `idx_ai_conversations_family_id` on `family_id`
- `idx_ai_conversations_updated_at` on `updated_at`

### 15.3 AI Messages Table

**Table**: `ai_messages`

**Purpose**: AI conversation messages

**Columns**:
```sql
CREATE TABLE ai_messages (
    message_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    conversation_id UUID NOT NULL REFERENCES ai_conversations(conversation_id) ON DELETE CASCADE,
    role VARCHAR(20) NOT NULL,
    content TEXT NOT NULL,
    message_type VARCHAR(20),
    response_type VARCHAR(20),
    confidence VARCHAR(10),
    model_provider VARCHAR(50),
    model_version VARCHAR(50),
    prompt_version VARCHAR(50),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

**Constraints**:
- PRIMARY KEY: `message_id`
- FOREIGN KEY: `conversation_id` → `ai_conversations(conversation_id)` ON DELETE CASCADE
- CHECK: `role IN ('USER', 'AI')`
- CHECK: `message_type IN ('FACT', 'ANALYSIS', 'RECOMMENDATION', 'WARNING', 'INSUFFICIENT_DATA', 'REFUSAL', NULL)`
- CHECK: `response_type IN ('FACT', 'ANALYSIS', 'RECOMMENDATION', 'WARNING', 'INSUFFICIENT_DATA', 'REFUSAL', NULL)`
- CHECK: `confidence IN ('HIGH', 'MEDIUM', 'LOW', NULL)`
- NOT NULL: `conversation_id`, `role`, `content`

**Indexes**:
- `idx_ai_messages_conversation_id` on `conversation_id`
- `idx_ai_messages_role` on `role`
- `idx_ai_messages_created_at` on `created_at`

### 15.4 AI Tool Invocations Table

**Table**: `ai_tool_invocations`

**Purpose**: AI tool invocation records for audit

**Columns**:
```sql
CREATE TABLE ai_tool_invocations (
    invocation_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    conversation_id UUID REFERENCES ai_conversations(conversation_id) ON DELETE SET NULL,
    message_id UUID REFERENCES ai_messages(message_id) ON DELETE SET NULL,
    tool_name VARCHAR(100) NOT NULL,
    tool_parameters JSONB,
    tool_result_reference VARCHAR(100),
    status VARCHAR(20) NOT NULL,
    executed_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    error_message TEXT
);
```

**Constraints**:
- PRIMARY KEY: `invocation_id`
- FOREIGN KEY: `conversation_id` → `ai_conversations(conversation_id)` ON DELETE SET NULL
- FOREIGN KEY: `message_id` → `ai_messages(message_id)` ON DELETE SET NULL
- CHECK: `status IN ('SUCCESS', 'FAILURE', 'TIMEOUT')`
- NOT NULL: `tool_name`, `status`, `executed_at`

**Indexes**:
- `idx_ai_tool_invocations_conversation_id` on `conversation_id`
- `idx_ai_tool_invocations_message_id` on `message_id`
- `idx_ai_tool_invocations_tool_name` on `tool_name`
- `idx_ai_tool_invocations_executed_at` on `executed_at`

**AI Tool Security**:
- AI tools are read-only and authorized
- Tool invocations are auditable
- AI does NOT have direct database access
- AI accesses data only through authorized application tools/APIs

### 15.5 AI Recommendations Table

**Table**: `ai_recommendations`

**Purpose**: AI-generated recommendations

**Columns**:
```sql
CREATE TABLE ai_recommendations (
    recommendation_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    conversation_id UUID REFERENCES ai_conversations(conversation_id) ON DELETE SET NULL,
    message_id UUID REFERENCES ai_messages(message_id) ON DELETE SET NULL,
    recommendation_type VARCHAR(50) NOT NULL,
    recommendation_data JSONB NOT NULL,
    user_action VARCHAR(20),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

**Constraints**:
- PRIMARY KEY: `recommendation_id`
- FOREIGN KEY: `conversation_id` → `ai_conversations(conversation_id)` ON DELETE SET NULL
- FOREIGN KEY: `message_id` → `ai_messages(message_id)` ON DELETE SET NULL
- CHECK: `user_action IN ('ACCEPTED', 'REJECTED', 'IGNORED', NULL)`
- NOT NULL: `recommendation_type`, `recommendation_data`

**Indexes**:
- `idx_ai_recommendations_conversation_id` on `conversation_id`
- `idx_ai_recommendations_message_id` on `message_id`
- `idx_ai_recommendations_type` on `recommendation_type`

---

## 16. Audit Domain

### 16.1 Domain Purpose

The Audit domain stores immutable audit records for sensitive operations.

**Tables**:
- `audit_logs` - Immutable audit records

### 16.2 Audit Logs Table

**Table**: `audit_logs`

**Purpose**: Immutable audit records for sensitive operations

**Columns**:
```sql
CREATE TABLE audit_logs (
    audit_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    actor_id UUID NOT NULL REFERENCES users(user_id) ON DELETE RESTRICT,
    actor_type VARCHAR(20) NOT NULL DEFAULT 'USER',
    action VARCHAR(50) NOT NULL,
    resource_type VARCHAR(50) NOT NULL,
    resource_id UUID,
    request_id UUID,
    correlation_id UUID,
    result VARCHAR(20) NOT NULL,
    metadata JSONB,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

**Constraints**:
- PRIMARY KEY: `audit_id`
- FOREIGN KEY: `actor_id` → `users(user_id)` ON DELETE RESTRICT
- CHECK: `actor_type IN ('USER', 'SYSTEM', 'ADMIN')`
- CHECK: `result IN ('SUCCESS', 'FAILURE', 'PARTIAL')`
- NOT NULL: `actor_id`, `action`, `resource_type`, `result`

**Indexes**:
- `idx_audit_logs_actor_id` on `actor_id`
- `idx_audit_logs_action` on `action`
- `idx_audit_logs_resource_type` on `resource_type`
- `idx_audit_logs_resource_id` on `resource_id`
- `idx_audit_logs_created_at` on `created_at`

**Audit Events**:
- Authentication (login, logout, password change)
- Authorization changes (role changes)
- Family membership (invite, accept, remove)
- Financial records (transaction create/update/delete)
- Budget changes (create, update, delete)
- Savings changes (create, update, delete)
- Bill status changes (mark paid)
- AI tool invocation
- Security events
- Configuration changes

**Immutable**:
- Audit logs are immutable (no UPDATE, no DELETE)
- ON DELETE RESTRICT prevents deletion of users with audit logs

---

## 17. Configuration Domain

### 17.1 Domain Purpose

The Configuration domain stores system configuration parameters.

**Tables**:
- `system_configurations` - System-wide configuration
- `user_configurations` - User-specific configuration

### 17.2 System Configurations Table

**Table**: `system_configurations`

**Purpose**: System-wide configuration parameters

**Columns**:
```sql
CREATE TABLE system_configurations (
    config_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    config_key VARCHAR(100) NOT NULL UNIQUE,
    config_value TEXT NOT NULL,
    config_type VARCHAR(20) NOT NULL,
    description TEXT,
    is_sensitive BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

**Constraints**:
- PRIMARY KEY: `config_id`
- UNIQUE: `config_key`
- CHECK: `config_type IN ('STRING', 'INTEGER', 'BOOLEAN', 'JSON', 'DECIMAL')`
- NOT NULL: `config_key`, `config_value`, `config_type`

**Indexes**:
- `idx_system_configurations_key` on `config_key`
- `idx_system_configurations_type` on `config_type`

**Configuration Categories**:
- Budget thresholds
- Bill reminders
- AI thresholds
- Financial health thresholds
- Session settings
- Notification thresholds

### 17.3 User Configurations Table

**Table**: `user_configurations`

**Purpose**: User-specific configuration parameters

**Columns**:
```sql
CREATE TABLE user_configurations (
    config_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    config_key VARCHAR(100) NOT NULL,
    config_value TEXT NOT NULL,
    config_type VARCHAR(20) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, config_key)
);
```

**Constraints**:
- PRIMARY KEY: `config_id`
- FOREIGN KEY: `user_id` → `users(user_id)` ON DELETE CASCADE
- UNIQUE: `(user_id, config_key)`
- CHECK: `config_type IN ('STRING', 'INTEGER', 'BOOLEAN', 'JSON', 'DECIMAL')`
- NOT NULL: `user_id`, `config_key`, `config_value`, `config_type`

**Indexes**:
- `idx_user_configurations_user_id` on `user_id`
- `idx_user_configurations_key` on `config_key`

---

## 18. Constraints and Indexes

### 18.1 Constraint Summary

**Primary Keys**: All tables use UUID primary keys with `gen_random_uuid()` default

**Foreign Keys**: All foreign keys have appropriate ON DELETE behavior:
- CASCADE: Child records deleted when parent deleted
- SET NULL: Child records set to NULL when parent deleted
- RESTRICT: Prevents deletion when child records exist

**Unique Constraints**: Enforced for business rules (email, phone, etc.)

**Check Constraints**: Enforce data validity (status enums, ranges, etc.)

**Not Null Constraints**: Enforce required fields

### 18.2 Index Summary

**Performance Indexes**:
- User ID indexes on all user-owned tables
- Family ID indexes on all family-owned tables
- Date indexes on time-based queries (transaction_date, due_date, etc.)
- Status indexes on status-based queries
- Type indexes on type-based queries

**Avoid Excessive Indexing**: Indexes added based on actual API access patterns from OpenAPI specification

---

## 19. Soft Delete Strategy

### 19.1 Soft Delete Rules

**Soft Delete Appropriate**:
- Users (with account status)
- Families (with deleted_at)
- Budgets (with status)
- Savings Goals (with status)
- AI Conversations (user-owned data)

**Hard Delete Required**:
- Transactions (financial records must be preserved)
- Audit Logs (immutable by design)
- Bill Status History (immutable by design)
- Financial Health Calculations (historical reproducibility)

**Rationale**:
- Financial records must be preserved for audit and compliance
- Audit logs must be immutable
- Planning data can be soft deleted
- User data can be soft deleted (with retention policy)

---

## 20. Money and Currency

### 20.1 Monetary Storage Rules

**Use NUMERIC/DECIMAL**:
- All monetary columns use `NUMERIC(15, 2)`
- Precision: 15 digits
- Scale: 2 decimal places
- Supports amounts up to 999,999,999,999.99

**Never Use FLOAT/DOUBLE**:
- Floating point types have precision issues
- Can cause rounding errors
- Not suitable for financial calculations

**Currency Storage**:
- Currency code stored separately (ISO 4217)
- Default currency: USD
- Supports multi-currency in future

**Examples**:
```sql
amount NUMERIC(15, 2) NOT NULL
currency VARCHAR(3) NOT NULL DEFAULT 'USD'
```

---

## 21. Time Handling

### 21.1 Timestamp Standardization

**UTC Storage**:
- All timestamps stored in UTC
- Use `TIMESTAMP WITH TIME ZONE`
- Default: `CURRENT_TIMESTAMP`

**User Timezone**:
- User timezone stored separately in `user_profiles.timezone`
- Default: UTC
- Conversion rules documented in API specification

**Date Fields**:
- Budget periods: YYYY-MM format
- Bill due dates: DATE type
- Transaction dates: DATE type
- Savings target dates: DATE type

---

## 22. Multi-Tenancy / Family Isolation

### 22.1 Family Data Isolation

**Every financial record must have ownership**:
- Transactions: `user_id` and optional `family_id`
- Budgets: `user_id` or `family_id`
- Savings Goals: `user_id` or `family_id`
- Bills: `user_id` and optional `family_id`

**Server-Side Authorization**:
- Authorization enforced in application layer
- Family membership checked before access
- Role-based access control (OWNER, MEMBER, RESTRICTED)

**Cross-Family Access Prevention**:
- Queries always filter by user_id or family_id
- No cross-family data access
- Server-side validation required

---

## 23. Data Retention

### 23.1 Retention Mapping

Based on Business Rules (BR-RET-001 to BR-RET-005):

| Entity | Retention Period | Status |
|--------|------------------|--------|
| User Accounts | 7 years after deletion | BR-RET-001 |
| Transactions | 7 years | BR-RET-002 |
| Budgets | 7 years | BR-RET-003 |
| Savings Goals | 7 years after completion | BR-RET-004 |
| Bills | 7 years | BR-RET-005 |
| Audit Logs | 7 years | BR-RET-001 |
| AI Conversations | 1 year | Compliance Decision Required |
| Notifications | 90 days | Compliance Decision Required |

**Compliance Decision Required**:
- AI Conversations: Legal retention uncertain
- Notifications: Legal retention uncertain

---

## 24. Optimistic Concurrency

### 24.1 Concurrency Strategy

**Version-based Optimistic Locking**:
- Budgets: `version` column
- Allocations: Version history table
- Family membership: `updated_at` timestamp
- User profile: `updated_at` timestamp

**Implementation**:
- Application layer checks version/timestamp before update
- If version/timestamp mismatch, return conflict error
- Client must re-fetch and retry

---

## 25. Data Encryption

### 25.1 Sensitive Fields

**Application-Level Encryption**:
- `users.password_hash`: Bcrypt hash (not encryption)
- `user_credentials.credential_value`: Hashed credentials
- `otp_challenges.otp_hash`: Hashed OTP

**Database Encryption**:
- PostgreSQL TDE (Transparent Data Encryption) at rest
- SSL/TLS for data in transit

**Never Store in Plaintext**:
- Passwords
- Raw OTPs
- Payment credentials
- Sensitive provider secrets

---

## 26. Migration Strategy

### 26.1 Initial Schema Order

**Migration Sequencing**:
1. Core tables (users, categories, family_roles)
2. Identity tables (user_credentials, user_sessions, user_devices, otp_challenges)
3. User tables (user_profiles, user_preferences, user_addresses)
4. Family tables (families, family_members, family_invitations)
5. Financial tables (financial_overviews, financial_periods, financial_allocations)
6. Transaction tables (transactions)
7. Budget tables (budgets, budget_categories, budget_periods, budget_recommendations)
8. Allocation tables (allocation_recommendations, allocation_categories, allocation_versions, allocation_approvals)
9. Savings tables (savings_goals, savings_contributions, savings_progress)
10. Bill tables (bills, bill_categories, bill_status_history)
11. Notification tables (notifications, notification_preferences, notification_deliveries)
12. Financial Health tables (financial_health_scores, financial_health_factors, financial_health_calculations)
13. AI tables (ai_conversations, ai_messages, ai_tool_invocations, ai_recommendations)
14. Audit tables (audit_logs)
15. Configuration tables (system_configurations, user_configurations)

### 26.2 Reference Data Strategy

**Seed Data Required**:
- Family roles (OWNER, MEMBER, RESTRICTED)
- Default categories (Housing, Groceries, Utilities, etc.)
- System configurations (thresholds, defaults)

### 26.3 Future Migration Principles

**Flyway Versioning**:
- Use Flyway for schema migrations
- Versioned migration files (V1__create_schema.sql, V2__add_column.sql, etc.)
- Repeatable migrations for reference data
- Baseline migration for existing databases

---

## 27. Scalability Considerations

### 27.1 Future Scalability

**Design for Future Growth**:
- Multiple families: Already supported via family_id
- Large transaction history: Indexes on transaction_date, user_id
- More currencies: Currency code column already supports multi-currency
- External financial providers: External_reference column in transactions
- Payment providers: Reserved for future integration
- Additional AI agents: AI tables support multiple providers/models

**No Redesign Required**:
- Schema designed to accommodate future growth
- Extensible JSONB columns for flexible data
- Version columns for data evolution
- Soft delete for data retention

---

## 28. Data Integrity

### 28.1 Financial Integrity

**Transaction Integrity**:
- Transactions use NUMERIC(15, 2) for precision
- No floating point for money
- Status tracking for transaction lifecycle
- Audit logging for all financial changes

**Budget Integrity**:
- Budget versioning for history
- Soft delete (archived) for budgets
- Budget recommendations preserve system/user/final versions

**Allocation Integrity**:
- Complete lifecycle preservation (generated, viewed, modified, approved, superseded)
- Version history for reproducibility
- Approval records for audit trail

**Savings Integrity**:
- Contribution records for tracking
- Progress snapshots for historical analysis
- Disclaimer: Planning values, not actual funds

---

## 29. Conclusion

The NeoWallet database architecture is designed for the MVP modular monolith with a single PostgreSQL database. The design emphasizes:

- **Financial Safety**: No custodial wallet balance tables
- **Planning vs Actual**: Clear distinction between planning data and actual transactions
- **Money Precision**: NUMERIC/DECIMAL for all monetary values
- **Family Isolation**: Server-side authorization with family membership
- **Audit Trail**: Immutable audit logs for sensitive operations
- **AI Security**: AI has no direct database access
- **Scalability**: Design supports future growth without redesign

**Total Tables**: 35 tables across 14 domains
**Database**: PostgreSQL 15+
**Architecture**: Single database for modular monolith

**Next Steps**:
1. Create Data Dictionary
2. Create ER Model
3. Create Traceability Matrix
4. Create Validation Report
