-- NeoWallet family membership and invitation schema (NW-003.4).

CREATE TABLE IF NOT EXISTS neowallet.family_members (
    member_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    family_id UUID NOT NULL REFERENCES neowallet.families(family_id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES neowallet.users(user_id) ON DELETE CASCADE,
    role VARCHAR(20) NOT NULL DEFAULT 'MEMBER',
    joined_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    left_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_family_members_role CHECK (role IN ('OWNER', 'MEMBER', 'RESTRICTED')),
    CONSTRAINT uq_family_members_family_user UNIQUE (family_id, user_id)
);

CREATE INDEX IF NOT EXISTS idx_family_members_family_id ON neowallet.family_members(family_id);
CREATE INDEX IF NOT EXISTS idx_family_members_user_id ON neowallet.family_members(user_id);
CREATE INDEX IF NOT EXISTS idx_family_members_role ON neowallet.family_members(role);

CREATE TABLE IF NOT EXISTS neowallet.family_invitations (
    invitation_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    family_id UUID NOT NULL REFERENCES neowallet.families(family_id) ON DELETE CASCADE,
    inviter_id UUID NOT NULL REFERENCES neowallet.users(user_id) ON DELETE CASCADE,
    email VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'MEMBER',
    token VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    accepted_at TIMESTAMP WITH TIME ZONE,
    rejected_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_family_invitations_role CHECK (role IN ('MEMBER', 'RESTRICTED')),
    CONSTRAINT uq_family_invitations_token UNIQUE (token)
);

CREATE INDEX IF NOT EXISTS idx_family_invitations_family_id ON neowallet.family_invitations(family_id);
CREATE INDEX IF NOT EXISTS idx_family_invitations_email ON neowallet.family_invitations(email);
CREATE INDEX IF NOT EXISTS idx_family_invitations_token ON neowallet.family_invitations(token);
CREATE INDEX IF NOT EXISTS idx_family_invitations_expires_at ON neowallet.family_invitations(expires_at);
