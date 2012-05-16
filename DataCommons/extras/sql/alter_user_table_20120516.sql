/**
 * alter_user_table_20120516.sql
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

create table users_bkp(
	username varchar(50) not null,
	password varchar(50) not null,
	enabled boolean not null
);

create table authorities_bkp (
	username varchar(50) not null,
	authority varchar(50) not null
);

insert into users_bkp (username, password, enabled)
select username, password, enabled from users;

insert into authorities_bkp (username, authority)
select * from authorities;

drop table authorities;
drop table users;

create table users (
	id			bigserial	not null,
	username	varchar(50)	not null,
	password	varchar(50)	not null,
	enabled		boolean		not null,
	primary key (id)
);
create unique index ix_user_username on users (username);

create table authorities (
	username varchar(50) not null,
	authority varchar(50) not null,
	constraint fk_authorities_users foreign key(username) references users(username)
);

create unique index ix_auth_username on authorities (username,authority);

insert into users (username, password, enabled)
select * from users_bkp;

insert into authorities (username, authority)
select * from authorities_bkp;

drop table users_bkp;
drop table authorities_bkp;
