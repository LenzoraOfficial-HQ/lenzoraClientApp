-- Apply this additive migration to the application schema before deployment.
-- Entity auditing records the authenticated actor alongside existing timestamps.

ALTER TABLE app_user
    ADD COLUMN created_by VARCHAR(255) NULL,
    ADD COLUMN last_modified_by VARCHAR(255) NULL;

ALTER TABLE password_reset_tokens
    ADD COLUMN created_by VARCHAR(255) NULL,
    ADD COLUMN last_modified_by VARCHAR(255) NULL,
    ADD COLUMN updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6);

ALTER TABLE app_order
    ADD COLUMN created_by VARCHAR(255) NULL,
    ADD COLUMN last_modified_by VARCHAR(255) NULL;

ALTER TABLE order_item
    ADD COLUMN created_by VARCHAR(255) NULL,
    ADD COLUMN last_modified_by VARCHAR(255) NULL;

ALTER TABLE prescription
    ADD COLUMN created_by VARCHAR(255) NULL,
    ADD COLUMN last_modified_by VARCHAR(255) NULL;

ALTER TABLE frame
    ADD COLUMN created_by VARCHAR(255) NULL,
    ADD COLUMN last_modified_by VARCHAR(255) NULL;

ALTER TABLE lens_option
    ADD COLUMN created_by VARCHAR(255) NULL,
    ADD COLUMN last_modified_by VARCHAR(255) NULL;

ALTER TABLE supplier
    ADD COLUMN created_by VARCHAR(255) NULL,
    ADD COLUMN last_modified_by VARCHAR(255) NULL;

ALTER TABLE audit_logs
    ADD COLUMN username VARCHAR(100) NULL;
