-- NeoWallet user profile and preferences schema.
-- Creates families (required FK target for user_profiles.family_id),
-- user_profiles, and user_preferences per the approved data model.

CREATE TABLE IF NOT EXISTS neowallet.families (
    family_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    owner_id UUID NOT NULL REFERENCES neowallet.users(user_id) ON DELETE RESTRICT,
    member_count INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX IF NOT EXISTS idx_families_owner_id ON neowallet.families(owner_id);

CREATE TABLE IF NOT EXISTS neowallet.user_profiles (
    profile_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE REFERENCES neowallet.users(user_id) ON DELETE CASCADE,
    profile_image_url VARCHAR(500),
    date_of_birth DATE,
    timezone VARCHAR(50) NOT NULL DEFAULT 'UTC',
    locale VARCHAR(10) NOT NULL DEFAULT 'en-US',
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    family_id UUID REFERENCES neowallet.families(family_id) ON DELETE SET NULL,
    family_role VARCHAR(20),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_user_profiles_family_role CHECK (family_role IN ('OWNER', 'MEMBER', 'RESTRICTED')),
    CONSTRAINT uq_user_profiles_user_id UNIQUE (user_id)
);

CREATE INDEX IF NOT EXISTS idx_user_profiles_user_id ON neowallet.user_profiles(user_id);
CREATE INDEX IF NOT EXISTS idx_user_profiles_family_id ON neowallet.user_profiles(family_id);

CREATE TABLE IF NOT EXISTS neowallet.user_preferences (
    preference_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE REFERENCES neowallet.users(user_id) ON DELETE CASCADE,
    budget_alerts_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    budget_alert_threshold_percentage INTEGER NOT NULL DEFAULT 80,
    bill_reminders_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    bill_reminder_days INTEGER[] NOT NULL DEFAULT ARRAY[3, 7]::INTEGER[],
    savings_updates_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    savings_updates_frequency VARCHAR(10) NOT NULL DEFAULT 'WEEKLY',
    financial_health_updates_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    financial_health_updates_frequency VARCHAR(10) NOT NULL DEFAULT 'MONTHLY',
    ai_response_style VARCHAR(10) NOT NULL DEFAULT 'CONCISE',
    ai_language VARCHAR(10) NOT NULL DEFAULT 'en',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_user_preferences_user_id UNIQUE (user_id),
    CONSTRAINT chk_user_preferences_budget_threshold CHECK (budget_alert_threshold_percentage BETWEEN 0 AND 100),
    CONSTRAINT chk_user_preferences_savings_frequency CHECK (savings_updates_frequency IN ('DAILY', 'WEEKLY', 'MONTHLY')),
    CONSTRAINT chk_user_preferences_health_frequency CHECK (financial_health_updates_frequency IN ('DAILY', 'WEEKLY', 'MONTHLY')),
    CONSTRAINT chk_user_preferences_ai_style CHECK (ai_response_style IN ('CONCISE', 'DETAILED'))
);

CREATE INDEX IF NOT EXISTS idx_user_preferences_user_id ON neowallet.user_preferences(user_id);

-- Seed default profile and preferences for existing users created before this migration.
INSERT INTO neowallet.user_profiles (user_id)
SELECT user_id FROM neowallet.users
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO neowallet.user_preferences (user_id)
SELECT user_id FROM neowallet.users
ON CONFLICT (user_id) DO NOTHING;
