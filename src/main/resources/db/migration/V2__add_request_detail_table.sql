CREATE TABLE request_details (
    -- base --
    request_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    -- fields --
    json_payload JSONB,

    PRIMARY KEY (request_id),
    FOREIGN KEY (request_id) REFERENCES requests (id) ON DELETE CASCADE
);