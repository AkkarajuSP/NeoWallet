-- NeoWallet Bill domain (NW-003.9).
-- MVP bills are manual-entry / planning records only. "Mark as Paid" records status; NeoWallet does NOT execute payment.

CREATE TABLE IF NOT EXISTS neowallet.bills (
    bill_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES neowallet.users(user_id) ON DELETE CASCADE,
    family_id UUID REFERENCES neowallet.families(family_id) ON DELETE SET NULL,
    family_member_id UUID REFERENCES neowallet.family_members(member_id) ON DELETE SET NULL,
    name VARCHAR(100) NOT NULL,
    amount NUMERIC(15, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    due_date DATE NOT NULL,
    category VARCHAR(50),
    is_recurring BOOLEAN DEFAULT FALSE,
    recurring_period VARCHAR(20),
    vendor VARCHAR(100),
    notes TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    paid_date DATE,
    payment_method VARCHAR(50),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT chk_bills_recurring_period CHECK (recurring_period IN ('MONTHLY', 'WEEKLY', 'YEARLY', NULL)),
    CONSTRAINT chk_bills_status CHECK (status IN ('PENDING', 'PAID', 'OVERDUE'))
);

CREATE INDEX IF NOT EXISTS idx_bills_user_id ON neowallet.bills(user_id);
CREATE INDEX IF NOT EXISTS idx_bills_family_id ON neowallet.bills(family_id);
CREATE INDEX IF NOT EXISTS idx_bills_family_member_id ON neowallet.bills(family_member_id);
CREATE INDEX IF NOT EXISTS idx_bills_status ON neowallet.bills(status);
CREATE INDEX IF NOT EXISTS idx_bills_due_date ON neowallet.bills(due_date);
CREATE INDEX IF NOT EXISTS idx_bills_is_recurring ON neowallet.bills(is_recurring);
CREATE INDEX IF NOT EXISTS idx_bills_deleted_at ON neowallet.bills(deleted_at);

CREATE TABLE IF NOT EXISTS neowallet.bill_categories (
    category_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    description TEXT,
    icon VARCHAR(50),
    color VARCHAR(7),
    is_system BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_bill_categories_name ON neowallet.bill_categories(name);

CREATE TABLE IF NOT EXISTS neowallet.bill_status_history (
    history_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    bill_id UUID NOT NULL REFERENCES neowallet.bills(bill_id) ON DELETE CASCADE,
    old_status VARCHAR(20),
    new_status VARCHAR(20) NOT NULL,
    changed_by UUID NOT NULL REFERENCES neowallet.users(user_id) ON DELETE CASCADE,
    changed_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    notes TEXT
);

CREATE INDEX IF NOT EXISTS idx_bill_status_history_bill_id ON neowallet.bill_status_history(bill_id);
CREATE INDEX IF NOT EXISTS idx_bill_status_history_changed_at ON neowallet.bill_status_history(changed_at);
