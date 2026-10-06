-- NeoWallet transaction records schema.
-- All monetary columns are NUMERIC(15,2). These are actual financial records only.

CREATE TABLE IF NOT EXISTS neowallet.transactions (
    transaction_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES neowallet.users(user_id) ON DELETE CASCADE,
    family_id UUID REFERENCES neowallet.families(family_id) ON DELETE SET NULL,
    family_member_id UUID REFERENCES neowallet.family_members(member_id) ON DELETE SET NULL,
    type VARCHAR(20) NOT NULL CHECK (type IN ('INCOME', 'EXPENSE', 'REFUND', 'ADJUSTMENT', 'TRANSFER_RECORD')),
    category_id UUID REFERENCES neowallet.categories(category_id) ON DELETE SET NULL,
    category_name VARCHAR(100) NOT NULL,
    amount NUMERIC(15, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    description TEXT,
    transaction_date DATE NOT NULL,
    posted_date DATE,
    status VARCHAR(20) NOT NULL DEFAULT 'PLANNED' CHECK (status IN ('PLANNED', 'COMMITTED', 'PENDING', 'COMPLETED', 'FAILED', 'REVERSED')),
    is_recurring BOOLEAN NOT NULL DEFAULT FALSE,
    recurring_pattern VARCHAR(50),
    external_reference VARCHAR(100),
    source VARCHAR(20) NOT NULL DEFAULT 'MANUAL' CHECK (source IN ('MANUAL', 'IMPORTED', 'PROVIDER')),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX IF NOT EXISTS idx_transactions_user_id ON neowallet.transactions(user_id);
CREATE INDEX IF NOT EXISTS idx_transactions_family_id ON neowallet.transactions(family_id);
CREATE INDEX IF NOT EXISTS idx_transactions_family_member_id ON neowallet.transactions(family_member_id);
CREATE INDEX IF NOT EXISTS idx_transactions_type ON neowallet.transactions(type);
CREATE INDEX IF NOT EXISTS idx_transactions_category_id ON neowallet.transactions(category_id);
CREATE INDEX IF NOT EXISTS idx_transactions_transaction_date ON neowallet.transactions(transaction_date);
CREATE INDEX IF NOT EXISTS idx_transactions_status ON neowallet.transactions(status);
CREATE INDEX IF NOT EXISTS idx_transactions_is_recurring ON neowallet.transactions(is_recurring);
