CREATE TABLE IF NOT EXISTS neowallet.budgets (
    budget_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES neowallet.users(user_id) ON DELETE CASCADE,
    family_id UUID REFERENCES neowallet.families(family_id) ON DELETE SET NULL,
    name VARCHAR(100) NOT NULL,
    period VARCHAR(7) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    total_limit NUMERIC(15, 2) NOT NULL,
    total_spent NUMERIC(15, 2) NOT NULL DEFAULT 0,
    utilization_percentage NUMERIC(5, 2) NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    version INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CHECK (user_id IS NOT NULL OR family_id IS NOT NULL),
    CHECK (status IN ('ACTIVE', 'ARCHIVED', 'DELETED')),
    CONSTRAINT uq_budgets_user_period_name UNIQUE (user_id, period, name),
    CONSTRAINT uq_budgets_family_period_name UNIQUE (family_id, period, name)
);

CREATE INDEX IF NOT EXISTS idx_budgets_user_id ON neowallet.budgets(user_id);
CREATE INDEX IF NOT EXISTS idx_budgets_family_id ON neowallet.budgets(family_id);
CREATE INDEX IF NOT EXISTS idx_budgets_period ON neowallet.budgets(period);
CREATE INDEX IF NOT EXISTS idx_budgets_status ON neowallet.budgets(status);

CREATE TABLE IF NOT EXISTS neowallet.budget_categories (
    budget_category_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    budget_id UUID NOT NULL REFERENCES neowallet.budgets(budget_id) ON DELETE CASCADE,
    category_id UUID REFERENCES neowallet.categories(category_id) ON DELETE SET NULL,
    category_name VARCHAR(100) NOT NULL,
    limit_amount NUMERIC(15, 2) NOT NULL,
    spent_amount NUMERIC(15, 2) NOT NULL DEFAULT 0,
    utilization_percentage NUMERIC(5, 2) NOT NULL DEFAULT 0,
    priority VARCHAR(20) NOT NULL DEFAULT 'VARIABLE',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CHECK (priority IN ('ESSENTIAL', 'VARIABLE', 'DISCRETIONARY')),
    CHECK (limit_amount > 0)
);

CREATE INDEX IF NOT EXISTS idx_budget_categories_budget_id ON neowallet.budget_categories(budget_id);
CREATE INDEX IF NOT EXISTS idx_budget_categories_category_id ON neowallet.budget_categories(category_id);
CREATE INDEX IF NOT EXISTS idx_budget_categories_priority ON neowallet.budget_categories(priority);

CREATE TABLE IF NOT EXISTS neowallet.budget_periods (
    period_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    budget_id UUID NOT NULL REFERENCES neowallet.budgets(budget_id) ON DELETE CASCADE,
    period_start DATE NOT NULL,
    period_end DATE NOT NULL,
    period_type VARCHAR(20) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CHECK (period_type IN ('MONTHLY', 'WEEKLY', 'CUSTOM'))
);

CREATE INDEX IF NOT EXISTS idx_budget_periods_budget_id ON neowallet.budget_periods(budget_id);
CREATE INDEX IF NOT EXISTS idx_budget_periods_period ON neowallet.budget_periods(period_start, period_end);

CREATE TABLE IF NOT EXISTS neowallet.budget_recommendations (
    recommendation_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    budget_id UUID REFERENCES neowallet.budgets(budget_id) ON DELETE SET NULL,
    user_id UUID NOT NULL REFERENCES neowallet.users(user_id) ON DELETE CASCADE,
    family_id UUID REFERENCES neowallet.families(family_id) ON DELETE SET NULL,
    period VARCHAR(7) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    recommended_allocation JSONB NOT NULL,
    user_modification JSONB,
    final_approved_budget JSONB,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    confidence VARCHAR(10) NOT NULL DEFAULT 'MEDIUM',
    historical_months_used INTEGER NOT NULL DEFAULT 0,
    generated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    approved_at TIMESTAMP WITH TIME ZONE,
    CHECK (status IN ('PENDING', 'VIEWED', 'MODIFIED', 'APPROVED', 'REJECTED', 'SUPERSEDED')),
    CHECK (confidence IN ('HIGH', 'MEDIUM', 'LOW'))
);

CREATE INDEX IF NOT EXISTS idx_budget_recommendations_budget_id ON neowallet.budget_recommendations(budget_id);
CREATE INDEX IF NOT EXISTS idx_budget_recommendations_user_id ON neowallet.budget_recommendations(user_id);
CREATE INDEX IF NOT EXISTS idx_budget_recommendations_family_id ON neowallet.budget_recommendations(family_id);
CREATE INDEX IF NOT EXISTS idx_budget_recommendations_period ON neowallet.budget_recommendations(period);
CREATE INDEX IF NOT EXISTS idx_budget_recommendations_status ON neowallet.budget_recommendations(status);
