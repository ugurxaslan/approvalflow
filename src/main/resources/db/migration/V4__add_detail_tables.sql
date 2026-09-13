CREATE TABLE leave_request_details(
    --base--
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    --base2--
    request_id BIGINT NOT NULL,
    --fields--
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,

    CONSTRAINT pk_leave_request_details PRIMARY KEY (request_id),
    CONSTRAINT fk_leave_request_details_on_requests FOREIGN KEY (request_id) REFERENCES requests (id) ON DELETE CASCADE
);

CREATE TABLE salary_advance_request_details (
    --base--
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    --base2--
    request_id BIGINT NOT NULL,
    --fields--
    amount DECIMAL(19, 2) NOT NULL,

    CONSTRAINT pk_salary_advance_request_details PRIMARY KEY (request_id),
    CONSTRAINT fk_salary_advance_request_details_on_requests FOREIGN KEY (request_id) REFERENCES requests (id) ON DELETE CASCADE
);