-- Apply once to the existing Aiven app_user table before starting the updated app.
-- The shared schema has user_role/user_status/registration_no but is missing
-- columns required by the orthoptist validation entities.

ALTER TABLE app_user
    ADD COLUMN role VARCHAR(50) NOT NULL DEFAULT '',
    ADD COLUMN status VARCHAR(50) NOT NULL DEFAULT '',
    ADD COLUMN license_number VARCHAR(50) NULL,
    ADD COLUMN profile_completed BOOLEAN NOT NULL DEFAULT FALSE;

UPDATE app_user
SET role = CAST(user_role AS CHAR),
    status = CAST(user_status AS CHAR),
    license_number = COALESCE(NULLIF(license_number, ''), registration_no),
    profile_completed = CASE
        WHEN user_role = 'ORTHOPTIST'
            AND COALESCE(TRIM(registration_no), '') <> ''
            AND COALESCE(TRIM(phone), '') <> ''
            AND COALESCE(TRIM(branch), '') <> ''
        THEN TRUE
        ELSE FALSE
    END;

ALTER TABLE app_user
    MODIFY COLUMN role VARCHAR(50) NOT NULL,
    MODIFY COLUMN status VARCHAR(50) NOT NULL;
