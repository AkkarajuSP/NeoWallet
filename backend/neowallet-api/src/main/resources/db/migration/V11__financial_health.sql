CREATE TABLE IF NOT EXISTS neowallet.financial_health_scores (
    score_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES neowallet.users(user_id) ON DELETE CASCADE,
    family_id UUID REFERENCES neowallet.families(family_id) ON DELETE SET NULL,
    overall_score INTEGER NOT NULL,
    score_label VARCHAR(20) NOT NULL,
    confidence VARCHAR(10) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'FINAL',
    calculated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_financial_health_scores_user_id FOREIGN KEY (user_id) REFERENCES neowallet.users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_financial_health_scores_family_id FOREIGN KEY (family_id) REFERENCES neowallet.families(family_id) ON DELETE SET NULL,
    CONSTRAINT chk_financial_health_scores_overall_score CHECK (overall_score BETWEEN 0 AND 100),
    CONSTRAINT chk_financial_health_scores_score_label CHECK (score_label IN ('CRITICAL', 'NEEDS_ATTENTION', 'FAIR', 'GOOD', 'EXCELLENT')),
    CONSTRAINT chk_financial_health_scores_confidence CHECK (confidence IN ('HIGH', 'MEDIUM', 'LOW')),
    CONSTRAINT chk_financial_health_scores_status CHECK (status IN ('FINAL', 'PROVISIONAL')),
    CONSTRAINT chk_financial_health_scores_user_or_family CHECK (user_id IS NOT NULL OR family_id IS NOT NULL)
);

CREATE INDEX IF NOT EXISTS idx_financial_health_scores_user_id ON neowallet.financial_health_scores(user_id);
CREATE INDEX IF NOT EXISTS idx_financial_health_scores_family_id ON neowallet.financial_health_scores(family_id);
CREATE INDEX IF NOT EXISTS idx_financial_health_scores_calculated_at ON neowallet.financial_health_scores(calculated_at);

CREATE UNIQUE INDEX IF NOT EXISTS uq_financial_health_scores_user_calculated_at
    ON neowallet.financial_health_scores(user_id, calculated_at) WHERE user_id IS NOT NULL;

CREATE UNIQUE INDEX IF NOT EXISTS uq_financial_health_scores_family_calculated_at
    ON neowallet.financial_health_scores(family_id, calculated_at) WHERE family_id IS NOT NULL;

CREATE TABLE IF NOT EXISTS neowallet.financial_health_factors (
    factor_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    score_id UUID NOT NULL REFERENCES neowallet.financial_health_scores(score_id) ON DELETE CASCADE,
    factor_name VARCHAR(50) NOT NULL,
    factor_score INTEGER NOT NULL,
    weight NUMERIC(5, 2) NOT NULL,
    contribution NUMERIC(5, 2) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_financial_health_factors_score_id FOREIGN KEY (score_id) REFERENCES neowallet.financial_health_scores(score_id) ON DELETE CASCADE,
    CONSTRAINT chk_financial_health_factors_factor_score CHECK (factor_score BETWEEN 0 AND 100),
    CONSTRAINT chk_financial_health_factors_weight CHECK (weight BETWEEN 0 AND 1)
);

CREATE INDEX IF NOT EXISTS idx_financial_health_factors_score_id ON neowallet.financial_health_factors(score_id);
CREATE INDEX IF NOT EXISTS idx_financial_health_factors_factor_name ON neowallet.financial_health_factors(factor_name);

CREATE TABLE IF NOT EXISTS neowallet.financial_health_calculations (
    calculation_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    score_id UUID NOT NULL REFERENCES neowallet.financial_health_scores(score_id) ON DELETE CASCADE,
    calculation_version VARCHAR(20) NOT NULL,
    input_snapshot JSONB NOT NULL,
    factor_scores JSONB NOT NULL,
    weights JSONB NOT NULL,
    calculated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_financial_health_calculations_score_id FOREIGN KEY (score_id) REFERENCES neowallet.financial_health_scores(score_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_financial_health_calculations_score_id ON neowallet.financial_health_calculations(score_id);
CREATE INDEX IF NOT EXISTS idx_financial_health_calculations_version ON neowallet.financial_health_calculations(calculation_version);
