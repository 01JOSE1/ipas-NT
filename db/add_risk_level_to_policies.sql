-- Add risk_level column to policies table
ALTER TABLE policies
ADD COLUMN risk_level VARCHAR(50) NULL;

-- Optional: set default value for existing rows (uncomment if desired)
-- UPDATE policies SET risk_level = 'MEDIO' WHERE risk_level IS NULL;

-- Rollback statement (to remove the column)
-- ALTER TABLE policies DROP COLUMN risk_level;
