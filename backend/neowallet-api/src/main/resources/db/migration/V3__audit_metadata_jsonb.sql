DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'neowallet'
          AND table_name = 'audit_logs'
          AND column_name = 'metadata'
          AND data_type = 'text'
    ) THEN
        UPDATE neowallet.audit_logs
        SET metadata = NULL
        WHERE metadata IS NOT NULL
          AND metadata = '';

        ALTER TABLE neowallet.audit_logs
        ALTER COLUMN metadata TYPE JSONB USING metadata::jsonb;
    END IF;
END $$;
