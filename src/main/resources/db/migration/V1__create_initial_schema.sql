-- 1. User Table --
CREATE TABLE users (
    --base--
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    --fields--
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    department_type VARCHAR(50) NOT NULL,
    PRIMARY KEY(id)
);

-- 2. User Roles Table --
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id, role),--bir role 2 kez eklenemez--
    CONSTRAINT fk_user_roles_users FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 3. Requests Table --
CREATE TABLE requests (
    --base--
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    --fields--
    request_type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    --relations--
    requested_by_id BIGINT NOT NULL,
    PRIMARY KEY(id),
    CONSTRAINT fk_requests_users FOREIGN KEY (requested_by_id) REFERENCES users(id)
);

--4. Approval Steps Table --
CREATE TABLE approval_steps(
    --base--
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    --fields--
    step_order INTEGER NOT NULL,
    comment VARCHAR(255),
    action_date TIMESTAMP,
    status VARCHAR(50) NOT NULL,
    required_role VARCHAR(50) NOT NULL,
    --relations--
    request_id BIGINT NOT NULL,
    assigned_approver_id BIGINT,
    PRIMARY KEY(id),
    CONSTRAINT fk_approval_steps_requests FOREIGN KEY (request_id) REFERENCES requests(id),
    CONSTRAINT fk_approval_steps_users FOREIGN KEY (assigned_approver_id) REFERENCES users(id)
);