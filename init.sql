-- Create schemas
CREATE SCHEMA IF NOT EXISTS iam_service;
CREATE SCHEMA IF NOT EXISTS user_service;
CREATE SCHEMA IF NOT EXISTS notification_service;
CREATE SCHEMA IF NOT EXISTS config_service;

ALTER TABLE iam_service.tbl_user 
ADD COLUMN IF NOT EXISTS created_date timestamp,
ADD COLUMN IF NOT EXISTS updated_date timestamp,
ADD COLUMN IF NOT EXISTS user_create varchar(255),
ADD COLUMN IF NOT EXISTS user_update varchar(255);
-- Set search path
SET search_path TO iam_service, user_service, notification_service;