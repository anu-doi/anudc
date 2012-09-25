/**
 * 20120924_create_audit_access.sql
 * 
 * Australian National University Data Commons
 * 
 * This script creates the audit access table
 * 
 * Version	Date		Developer				Description
 * 0.1		18/09/2012	Genevieve Turner (GT)	Initial
 */

create table audit_access (
	id				bigserial		not null
	, access_date	timestamp		not null
	, ip_address	varchar(30)		null
	, url			text			not null
	, method		varchar(10)		not null
	, pid			varchar(64)		null
	, username		varchar(255)	null
	, PRIMARY KEY (id)
);
CREATE INDEX ix_audit_access_1 ON audit_access (pid);