-- 1. Drop old name/username column if it exists
ALTER TABLE admins
    DROP COLUMN IF EXISTS name;

-- 2. Add first_name and last_name
ALTER TABLE admins
    ADD COLUMN first_name VARCHAR(50) NOT NULL DEFAULT 'Admin',
    ADD COLUMN last_name VARCHAR(50) NOT NULL DEFAULT 'User';

-- 3. Add role column
ALTER TABLE admins
    ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'STAFF';