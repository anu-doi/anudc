/**
 * alter_user_table_20120517.sql
 * 
 * Australian National University Data Commons
 * 
 * This script adds the user_type column to the users table
 * 
 * 
 * Version	Date		Developer				Description
 * 0.1		17/05/2012	Genevieve Turner (GT)	Initial
 */

CREATE TABLE users_type (
	id	bigserial	not null
	,type_name	varchar(255)	not null
	,PRIMARY KEY (id)
);

INSERT INTO users_type (type_name)
VALUES ('ANU User');

INSERT INTO users_type (type_name)
VALUES ('Registered User');

ALTER TABLE users
ADD user_type bigint;

UPDATE users
SET user_type = 1
WHERE username = password;

UPDATE users
SET user_type = 2
WHERE user_type IS NULL;

ALTER TABLE users ADD CONSTRAINT fk_users_1 FOREIGN KEY (user_type) REFERENCES users_type (id);
ALTER TABLE users ALTER COLUMN user_type SET NOT NULL;