/**
 * 20120620_create_audit_table.sql
 * 
 * Australian National University Data Commons
 * 
 * This script performas actions to create audit tables
 * 
 * Version	Date		Developer				Description
 * 0.1		20/06/2012	Genevieve Turner (GT)	Initial
 */

CREATE TABLE audit_object (
	id			bigserial	NOT NULL
	,log_date	timestamp	NOT NULL
	,log_type	varchar(16)	NOT NULL
	,object_id	bigint		NOT NULL
	,user_id	bigint		NOT NULL
	,before		text		NULL
	,after		text		NULL
	,PRIMARY KEY (id)
	,CONSTRAINT fk_audit_object FOREIGN KEY (object_id) REFERENCES fedora_object (id)
	,CONSTRAINT fk_audit_user FOREIGN KEY (user_id) REFERENCES users (id)
);