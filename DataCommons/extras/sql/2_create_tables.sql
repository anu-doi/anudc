/**
 * 2_create_tables.sql
 * 
 * Australian National University Data Commons
 * 
 * Creates tables for the data commons project
 * 
 * Version	Date		Developer			Description
 * 0.1		13/03/2012	Genevieve Turner	Initial build
 */

CREATE TABLE access_user (
	user_id		bigint 		NOT NULL
	,user_uid	varchar(15) NOT NULL
	,PRIMARY KEY (user_id)
);

CREATE TABLE access_group (
	group_id	bigint			NOT NULL
	,group_name	varchar(255)	NOT NULL
	,group_owner	bigint		NOT NULL
	,PRIMARY KEY (group_id)
	,FOREIGN KEY (group_owner) REFERENCES access_user(user_id)
);

CREATE TABLE user_group (
	user_id		bigint				NOT NULL
	,group_id	bigint				NOT NULL
	,PRIMARY KEY (user_id, group_id)
	,FOREIGN KEY (user_id) REFERENCES access_user(user_id)
	,FOREIGN KEY (group_id) REFERENCES access_group(group_id)
);