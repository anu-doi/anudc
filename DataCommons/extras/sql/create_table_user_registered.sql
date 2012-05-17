/**
 * create_table_user_registered.sql
 * 
 * Australian National University Data Commons
 * 
 * This script is to perform updates to the users table in which the id column is added
 * and used as the primary key
 * 
 * 
 * Version	Date		Developer				Description
 * 0.1		16/05/2012	Genevieve Turner (GT)	Initial
 */

CREATE TABLE user_registered (
	id bigint	not null
	,last_name	varchar(40)	not null
	,given_name	varchar(40)	not null
	,PRIMARY KEY (id)
	,CONSTRAINT fk_user_registered_1 FOREIGN KEY(id) REFERENCES users(id)
)