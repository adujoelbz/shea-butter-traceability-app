-- Add missing columns to batch_photos table
ALTER TABLE batch_photos
    ADD COLUMN IF NOT EXISTS file_type VARCHAR(100),
    ADD COLUMN IF NOT EXISTS file_size BIGINT;