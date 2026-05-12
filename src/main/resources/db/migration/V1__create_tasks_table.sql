CREATE TABLE tasks (
    id UUID PRIMARY KEY,
    title VARCHAR(120) NOT NULL,
    description VARCHAR(1000),
    status VARCHAR(40) NOT NULL,
    due_date_time TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT chk_tasks_title_not_blank CHECK (LENGTH(TRIM(title)) > 0),
    CONSTRAINT chk_tasks_status_valid CHECK (status IN ('TODO', 'IN_PROGRESS', 'COMPLETED')),
    CONSTRAINT chk_tasks_updated_at_not_before_created_at CHECK (updated_at >= created_at)
);

CREATE INDEX idx_tasks_status ON tasks(status);
CREATE INDEX idx_tasks_due_date_time ON tasks(due_date_time);