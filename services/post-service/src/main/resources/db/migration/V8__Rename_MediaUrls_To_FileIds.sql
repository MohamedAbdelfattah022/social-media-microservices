-- Rename post_media_urls table to post_file_ids and change column type
ALTER TABLE post_media_urls RENAME TO post_file_ids;
ALTER TABLE post_file_ids RENAME COLUMN media_url TO file_id;

-- Drop existing data (VARCHAR URLs cannot be converted to UUID)
DELETE FROM post_file_ids;

-- Change column type to UUID
ALTER TABLE post_file_ids ALTER COLUMN file_id TYPE UUID USING file_id::uuid;

-- Add index for faster lookups
CREATE INDEX idx_post_file_ids_file_id ON post_file_ids(file_id);
