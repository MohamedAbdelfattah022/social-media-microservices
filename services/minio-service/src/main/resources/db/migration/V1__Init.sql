CREATE TABLE IF NOT EXISTS file_metadata (
    id UUID PRIMARY KEY,
    original_filename VARCHAR(255) NOT NULL,
    stored_filename VARCHAR(255) NOT NULL UNIQUE,
    file_size BIGINT NOT NULL,
    content_type VARCHAR(100),
    uploaded_at TIMESTAMP,
    bucket_name VARCHAR(100) NOT NULL
);

CREATE INDEX idx_file_metadata_stored_filename ON file_metadata(stored_filename);
CREATE INDEX idx_file_metadata_uploaded_at ON file_metadata(uploaded_at);
