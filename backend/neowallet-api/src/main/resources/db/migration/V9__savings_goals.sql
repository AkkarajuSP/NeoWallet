CREATE TABLE IF NOT EXISTS neowallet.savings_goals (
    goal_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES neowallet.users(user_id) ON DELETE CASCADE,
    family_id UUID REFERENCES neowallet.families(family_id) ON DELETE SET NULL,
    name VARCHAR(100) NOT NULL,
    target_amount NUMERIC(15, 2) NOT NULL,
    current_amount NUMERIC(15, 2) NOT NULL DEFAULT 0,
    target_date DATE NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    category VARCHAR(50),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    progress_percentage NUMERIC(5, 2) NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_savings_goals_status CHECK (status IN ('ACTIVE', 'COMPLETED', 'CANCELLED')),
    CONSTRAINT chk_savings_goals_priority CHECK (priority IN ('HIGH', 'MEDIUM', 'LOW')),
    CONSTRAINT chk_savings_goals_user_or_family CHECK (user_id IS NOT NULL OR family_id IS NOT NULL),
    CONSTRAINT chk_savings_goals_target_positive CHECK (target_amount > 0)
);

CREATE INDEX IF NOT EXISTS idx_savings_goals_user_id ON neowallet.savings_goals(user_id);
CREATE INDEX IF NOT EXISTS idx_savings_goals_family_id ON neowallet.savings_goals(family_id);
CREATE INDEX IF NOT EXISTS idx_savings_goals_status ON neowallet.savings_goals(status);
CREATE INDEX IF NOT EXISTS idx_savings_goals_priority ON neowallet.savings_goals(priority);
CREATE INDEX IF NOT EXISTS idx_savings_goals_target_date ON neowallet.savings_goals(target_date);

CREATE TABLE IF NOT EXISTS neowallet.savings_contributions (
    contribution_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    goal_id UUID NOT NULL REFERENCES neowallet.savings_goals(goal_id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES neowallet.users(user_id) ON DELETE CASCADE,
    amount NUMERIC(15, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    contribution_date DATE NOT NULL,
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_savings_contributions_positive CHECK (amount > 0)
);

CREATE INDEX IF NOT EXISTS idx_savings_contributions_goal_id ON neowallet.savings_contributions(goal_id);
CREATE INDEX IF NOT EXISTS idx_savings_contributions_user_id ON neowallet.savings_contributions(user_id);
CREATE INDEX IF NOT EXISTS idx_savings_contributions_contribution_date ON neowallet.savings_contributions(contribution_date);

CREATE TABLE IF NOT EXISTS neowallet.savings_progress (
    progress_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    goal_id UUID NOT NULL REFERENCES neowallet.savings_goals(goal_id) ON DELETE CASCADE,
    current_amount NUMERIC(15, 2) NOT NULL,
    progress_percentage NUMERIC(5, 2) NOT NULL,
    remaining_amount NUMERIC(15, 2) NOT NULL,
    months_remaining INTEGER,
    required_monthly_contribution NUMERIC(15, 2),
    actual_monthly_contribution NUMERIC(15, 2),
    on_track BOOLEAN,
    status VARCHAR(20),
    calculated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_savings_progress_status CHECK (status IN ('ON_TRACK', 'BEHIND', 'AHEAD'))
);

CREATE INDEX IF NOT EXISTS idx_savings_progress_goal_id ON neowallet.savings_progress(goal_id);
CREATE INDEX IF NOT EXISTS idx_savings_progress_calculated_at ON neowallet.savings_progress(calculated_at);
