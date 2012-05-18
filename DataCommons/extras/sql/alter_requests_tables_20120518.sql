/**
 * alter_requests_tables_20120518.sql
 * 
 * Australian National University Data Commons
 * 
 * This script performs actions on the request related tables to update columns
 * 
 * 
 * Version	Date		Developer				Description
 * 0.1		18/05/2012	Genevieve Turner (GT)	Initial
 */

-- Alter the collection_dropboxes table
alter table collection_dropboxes add column creator_fk bigint not null;
update collection_dropboxes set creator_fk = creator_user_id;
alter table collection_dropboxes add CONSTRAINT fk79688dc969bf5f07 FOREIGN KEY (creator_fk) REFERENCES users (id);
alter table collection_dropboxes drop column creator_user_id;

-- Alter the collection_request_status table
alter table collection_request_status add column user_fk bigint not null;
update collection_request_status set user_fk = user_id;
alter table collection_request_status add CONSTRAINT fka6ce9b63ec35b08 FOREIGN KEY (user_fk) REFERENCES users (id);
alter table collection_request_status drop column user_id;

-- Alter the collection_requests table
alter table collection_requests add column requestor_fk bigint not null;
update collection_requests set requestor_fk = requestor_id;
alter table collection_requests add CONSTRAINT fk71967445661730c1 FOREIGN KEY (requestor_fk) REFERENCES users (id);
alter table collection_requests drop column requestor_id;

-- Alter the question_bank table
alter table question_bank rename column question to question_text;
