/**
 * 20120821_update_user_tables.sql
 * 
 * Australian National University Data Commons
 * 
 * This script adds columns to the user_registerd table and creates the user_request_pwd table
 * 
 * Version	Date		Developer				Description
 * 0.1		27/08/2012	Genevieve Turner (GT)	Initial
 */

ALTER TABLE user_registered
ADD institution	text;

ALTER TABLE user_registered
ADD phone text;

ALTER TABLE user_registered
ADD address text;

CREATE TABLE user_request_pwd (
	id				bigserial	not null
	,user_id		bigint		not null
	,request_date	timestamp	not null
	,ip_address		varchar(30)
	,link_id		text		not null
	,used			boolean
	,PRIMARY KEY (id)
	,CONSTRAINT fk_user_request_pwd_1 FOREIGN KEY (user_id) REFERENCES users (id)
);
