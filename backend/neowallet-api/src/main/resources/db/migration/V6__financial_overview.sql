-- NeoWallet financial overview and categories schema.
-- All monetary columns are NUMERIC(15,2). These are planning/management values only.

CREATE TABLE IF NOT EXISTS neowallet.categories (
    category_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    parent_category_id UUID REFERENCES neowallet.categories(category_id) ON DELETE SET NULL,
    category_type VARCHAR(20) NOT NULL CHECK (category_type IN ('ESSENTIAL', 'VARIABLE', 'DISCRETIONARY', 'INCOME', 'SAVINGS', 'EMERGENCY')),
    priority VARCHAR(20) NOT NULL DEFAULT 'VARIABLE' CHECK (priority IN ('ESSENTIAL', 'VARIABLE', 'DISCRETIONARY')),
    icon VARCHAR(50),
    color VARCHAR(7),
    is_system BOOLEAN NOT NULL DEFAULT TRUE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_by UUID REFERENCES neowallet.users(user_id) ON DELETE SET NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (name, parent_category_id)
);

CREATE INDEX IF NOT EXISTS idx_categories_parent_id ON neowallet.categories(parent_category_id);
CREATE INDEX IF NOT EXISTS idx_categories_type ON neowallet.categories(category_type);
CREATE INDEX IF NOT EXISTS idx_categories_priority ON neowallet.categories(priority);

CREATE TABLE IF NOT EXISTS neowallet.financial_overviews (
    overview_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES neowallet.users(user_id) ON DELETE CASCADE,
    family_id UUID REFERENCES neowallet.families(family_id) ON DELETE SET NULL,
    planning_income NUMERIC(15, 2) NOT NULL DEFAULT 0,
    mandatory_commitments NUMERIC(15, 2) NOT NULL DEFAULT 0,
    essential_allocation NUMERIC(15, 2) NOT NULL DEFAULT 0,
    variable_allocation NUMERIC(15, 2) NOT NULL DEFAULT 0,
    savings_allocation NUMERIC(15, 2) NOT NULL DEFAULT 0,
    emergency_allocation NUMERIC(15, 2) NOT NULL DEFAULT 0,
    discretionary_planning NUMERIC(15, 2) NOT NULL DEFAULT 0,
    committed_amount NUMERIC(15, 2) NOT NULL DEFAULT 0,
    pending_payments NUMERIC(15, 2) NOT NULL DEFAULT 0,
    actual_transactions NUMERIC(15, 2) NOT NULL DEFAULT 0,
    available_financial_capacity NUMERIC(15, 2) NOT NULL DEFAULT 0,
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    period VARCHAR(7) NOT NULL,
    calculated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CHECK (user_id IS NOT NULL OR family_id IS NOT NULL),
    UNIQUE (user_id, period),
    UNIQUE (family_id, period)
);

CREATE INDEX IF NOT EXISTS idx_financial_overviews_user_id ON neowallet.financial_overviews(user_id);
CREATE INDEX IF NOT EXISTS idx_financial_overviews_family_id ON neowallet.financial_overviews(family_id);
CREATE INDEX IF NOT EXISTS idx_financial_overviews_period ON neowallet.financial_overviews(period);

CREATE TABLE IF NOT EXISTS neowallet.financial_periods (
    period_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    overview_id UUID NOT NULL REFERENCES neowallet.financial_overviews(overview_id) ON DELETE CASCADE,
    period_start DATE NOT NULL,
    period_end DATE NOT NULL,
    period_type VARCHAR(20) NOT NULL CHECK (period_type IN ('MONTHLY', 'WEEKLY', 'CUSTOM')),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_financial_periods_overview_id ON neowallet.financial_periods(overview_id);
CREATE INDEX IF NOT EXISTS idx_financial_periods_period ON neowallet.financial_periods(period_start, period_end);

CREATE TABLE IF NOT EXISTS neowallet.financial_allocations (
    allocation_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    overview_id UUID NOT NULL REFERENCES neowallet.financial_overviews(overview_id) ON DELETE CASCADE,
    category_id UUID REFERENCES neowallet.categories(category_id) ON DELETE SET NULL,
    category_name VARCHAR(100) NOT NULL,
    allocated_amount NUMERIC(15, 2) NOT NULL DEFAULT 0,
    actual_amount NUMERIC(15, 2) NOT NULL DEFAULT 0,
    utilization_percentage NUMERIC(5, 2) NOT NULL DEFAULT 0,
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_financial_allocations_overview_id ON neowallet.financial_allocations(overview_id);
CREATE INDEX IF NOT EXISTS idx_financial_allocations_category_id ON neowallet.financial_allocations(category_id);

-- Seed default essential/variable/planning categories used for household budget allocation.
INSERT INTO neowallet.categories (category_id, name, category_type, priority, is_system, is_active) VALUES
    (gen_random_uuid(), 'Housing', 'ESSENTIAL', 'ESSENTIAL', TRUE, TRUE),
    (gen_random_uuid(), 'Groceries', 'ESSENTIAL', 'ESSENTIAL', TRUE, TRUE),
    (gen_random_uuid(), 'Electricity', 'ESSENTIAL', 'ESSENTIAL', TRUE, TRUE),
    (gen_random_uuid(), 'Gas', 'ESSENTIAL', 'ESSENTIAL', TRUE, TRUE),
    (gen_random_uuid(), 'Internet', 'ESSENTIAL', 'ESSENTIAL', TRUE, TRUE),
    (gen_random_uuid(), 'Mobile', 'ESSENTIAL', 'ESSENTIAL', TRUE, TRUE),
    (gen_random_uuid(), 'DTH', 'ESSENTIAL', 'ESSENTIAL', TRUE, TRUE),
    (gen_random_uuid(), 'Water', 'ESSENTIAL', 'ESSENTIAL', TRUE, TRUE),
    (gen_random_uuid(), 'Transportation', 'ESSENTIAL', 'ESSENTIAL', TRUE, TRUE),
    (gen_random_uuid(), 'Education', 'ESSENTIAL', 'ESSENTIAL', TRUE, TRUE),
    (gen_random_uuid(), 'Healthcare', 'ESSENTIAL', 'ESSENTIAL', TRUE, TRUE),
    (gen_random_uuid(), 'Insurance', 'ESSENTIAL', 'ESSENTIAL', TRUE, TRUE),
    (gen_random_uuid(), 'Entertainment', 'VARIABLE', 'VARIABLE', TRUE, TRUE),
    (gen_random_uuid(), 'Subscriptions', 'VARIABLE', 'VARIABLE', TRUE, TRUE),
    (gen_random_uuid(), 'Personal', 'VARIABLE', 'VARIABLE', TRUE, TRUE),
    (gen_random_uuid(), 'Family', 'VARIABLE', 'VARIABLE', TRUE, TRUE),
    (gen_random_uuid(), 'Savings', 'SAVINGS', 'VARIABLE', TRUE, TRUE),
    (gen_random_uuid(), 'Emergency', 'EMERGENCY', 'VARIABLE', TRUE, TRUE),
    (gen_random_uuid(), 'Other', 'DISCRETIONARY', 'DISCRETIONARY', TRUE, TRUE)
ON CONFLICT (name, parent_category_id) DO NOTHING;
