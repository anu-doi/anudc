/*
 * Adds a column to the access_logs table to log user-agent strings from HTTP requests.
 */
ALTER TABLE access_logs
	ADD COLUMN user_agent character varying(255);