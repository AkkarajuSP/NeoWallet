-- NeoWallet identity and authentication schema.
-- No financial or business tables are created yet.

CREATE TABLE IF NOT EXISTS neowallet.users (
    user_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20),
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    account_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    phone_verified BOOLEAN NOT NULL DEFAULT FALSE,
    failed_login_attempts INTEGER NOT NULL DEFAULT 0,
    locked_until TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT uq_users_email UNIQUE (email),
    CONSTRAINT uq_users_phone UNIQUE (phone_number),
    CONSTRAINT chk_users_account_status CHECK (account_status IN ('PENDING', 'ACTIVE', 'SUSPENDED', 'LOCKED', 'DELETED'))
);

CREATE INDEX IF NOT EXISTS idx_users_email ON neowallet.users(email);
CREATE INDEX IF NOT EXISTS idx_users_status ON neowallet.users(account_status);

CREATE TABLE IF NOT EXISTS neowallet.user_credentials (
    credential_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES neowallet.users(user_id) ON DELETE CASCADE,
    credential_type VARCHAR(20) NOT NULL,
    credential_value VARCHAR(255) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    expires_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_user_credentials_type CHECK (credential_type IN ('PASSWORD', 'SOCIAL', 'OTP'))
);

CREATE INDEX IF NOT EXISTS idx_user_credentials_user_id ON neowallet.user_credentials(user_id);
CREATE INDEX IF NOT EXISTS idx_user_credentials_type ON neowallet.user_credentials(credential_type);

CREATE TABLE IF NOT EXISTS neowallet.user_devices (
    device_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES neowallet.users(user_id) ON DELETE CASCADE,
    device_name VARCHAR(100),
    device_type VARCHAR(20) NOT NULL,
    device_token VARCHAR(500),
    platform VARCHAR(20),
    os_version VARCHAR(50),
    app_version VARCHAR(20),
    last_active_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_user_devices_type CHECK (device_type IN ('ANDROID', 'IOS', 'WEB'))
);

CREATE INDEX IF NOT EXISTS idx_user_devices_user_id ON neowallet.user_devices(user_id);
CREATE INDEX IF NOT EXISTS idx_user_devices_type ON neowallet.user_devices(device_type);
CREATE INDEX IF NOT EXISTS idx_user_devices_token ON neowallet.user_devices(device_token);

CREATE TABLE IF NOT EXISTS neowallet.user_sessions (
    session_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES neowallet.users(user_id) ON DELETE CASCADE,
    device_id UUID REFERENCES neowallet.user_devices(device_id) ON DELETE SET NULL,
    refresh_token_hash VARCHAR(255) NOT NULL,
    previous_refresh_token_hash VARCHAR(255),
    ip_address VARCHAR(45),
    user_agent TEXT,
    last_active_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    revoked_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_user_sessions_refresh_token_hash UNIQUE (refresh_token_hash)
);

CREATE INDEX IF NOT EXISTS idx_user_sessions_user_id ON neowallet.user_sessions(user_id);
CREATE INDEX IF NOT EXISTS idx_user_sessions_device_id ON neowallet.user_sessions(device_id);
CREATE INDEX IF NOT EXISTS idx_user_sessions_expires_at ON neowallet.user_sessions(expires_at);
CREATE INDEX IF NOT EXISTS idx_user_sessions_prev_hash ON neowallet.user_sessions(previous_refresh_token_hash);

CREATE TABLE IF NOT EXISTS neowallet.otp_challenges (
    otp_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES neowallet.users(user_id) ON DELETE CASCADE,
    email VARCHAR(255),
    phone_number VARCHAR(20),
    otp_hash VARCHAR(255) NOT NULL,
    purpose VARCHAR(20) NOT NULL,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    attempts INTEGER NOT NULL DEFAULT 0,
    resend_count INTEGER NOT NULL DEFAULT 0,
    verified_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_otp_challenges_purpose CHECK (purpose IN ('REGISTRATION', 'LOGIN', 'RESET'))
);

CREATE INDEX IF NOT EXISTS idx_otp_challenges_user_id ON neowallet.otp_challenges(user_id);
CREATE INDEX IF NOT EXISTS idx_otp_challenges_email ON neowallet.otp_challenges(email);
CREATE INDEX IF NOT EXISTS idx_otp_challenges_phone ON neowallet.otp_challenges(phone_number);
CREATE INDEX IF NOT EXISTS idx_otp_challenges_expires_at ON neowallet.otp_challenges(expires_at);

CREATE TABLE IF NOT EXISTS neowallet.audit_logs (
    audit_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    actor_id UUID REFERENCES neowallet.users(user_id) ON DELETE RESTRICT,
    actor_type VARCHAR(20) NOT NULL DEFAULT 'USER',
    action VARCHAR(50) NOT NULL,
    resource_type VARCHAR(50) NOT NULL,
    resource_id UUID,
    request_id UUID,
    correlation_id UUID,
    result VARCHAR(20) NOT NULL,
    metadata TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_audit_logs_actor_type CHECK (actor_type IN ('USER', 'SYSTEM', 'ADMIN')),
    CONSTRAINT chk_audit_logs_result CHECK (result IN ('SUCCESS', 'FAILURE', 'PARTIAL'))
);

CREATE INDEX IF NOT EXISTS idx_audit_logs_actor_id ON neowallet.audit_logs(actor_id);
CREATE INDEX IF NOT EXISTS idx_audit_logs_action ON neowallet.audit_logs(action);
CREATE INDEX IF NOT EXISTS idx_audit_logs_resource_type ON neowallet.audit_logs(resource_type);
CREATE INDEX IF NOT EXISTS idx_audit_logs_resource_id ON neowallet.audit_logs(resource_id);
CREATE INDEX IF NOT EXISTS idx_audit_logs_created_at ON neowallet.audit_logs(created_at);
