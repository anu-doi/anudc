/*
Removes the not null constraint from access_logs.user_fk so requests from anonymous users can be logged for public files.
*/
ALTER TABLE access_logs ALTER COLUMN user_fk DROP NOT NULL;