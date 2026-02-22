CREATE INDEX IF NOT EXISTS idx_user_entity_username_lower 
ON user_entity (LOWER(username));

COMMENT ON INDEX idx_user_entity_username_lower IS 
    'Index for case-insensitive username search using LIKE operator';