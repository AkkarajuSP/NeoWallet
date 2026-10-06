-- NeoWallet database baseline.
-- No business tables are created yet; this migration only verifies Flyway connectivity.

CREATE SCHEMA IF NOT EXISTS neowallet;

CREATE TABLE IF NOT EXISTS neowallet.migration_baseline (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
