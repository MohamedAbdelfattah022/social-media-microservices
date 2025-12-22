CREATE TABLE user_profiles
(
    id                  VARCHAR(36) NOT NULL, -- keycloak UUID
    first_name          VARCHAR(255),
    last_name           VARCHAR(255),
    bio                 TEXT,
    profile_picture_url VARCHAR(255),
    created_at          TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_user_profiles PRIMARY KEY (id)
);

ALTER TABLE outbox_events
ALTER
COLUMN aggregate_id TYPE VARCHAR(36) USING aggregate_id::VARCHAR(36);