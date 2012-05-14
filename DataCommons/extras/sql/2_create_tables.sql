create table users(
	username varchar(50) not null primary key,
	password varchar(50) not null,
	enabled boolean not null
);

create table authorities (
	username varchar(50) not null,
	authority varchar(50) not null,
	constraint fk_authorities_users foreign key(username) references users(username)
);
create unique index ix_auth_username on authorities (username,authority);

CREATE TABLE acl_sid (
	id	bigserial				NOT NULL
	,principal	boolean			NOT NULL
	,sid		varchar(100)	NOT NULL
	,PRIMARY KEY(id)
	,CONSTRAINT unique_uk_1 UNIQUE(sid, principal)
);

CREATE TABLE acl_class (
	id		bigserial		NOT NULL
	,class	varchar(255)	NOT NULL
	,PRIMARY KEY (id)
	,CONSTRAINT unique_uk_2 UNIQUE(class)
);

CREATE TABLE acl_object_identity (
	id					bigserial	NOT NULL
	,object_id_class	bigint		NOT NULL
	,object_id_identity	bigint		NOT NULL
	,owner_sid			bigint
	,parent_object		bigint
	,entries_inheriting	boolean		NOT NULL
	,PRIMARY KEY (id)
	,CONSTRAINT unique_uk_3 UNIQUE(object_id_class, object_id_identity)
	,CONSTRAINT foreign_fk_1 FOREIGN KEY(parent_object) REFERENCES acl_object_identity(id)
	,CONSTRAINT foreign_fk_2 FOREIGN KEY(object_id_class) REFERENCES acl_class(id)
	,CONSTRAINT foreign_fk_3 FOREIGN KEY(owner_sid) REFERENCES acl_sid(id)
);

CREATE TABLE acl_entry (
	id						bigserial	NOT NULL
	,acl_object_identity	bigint		NOT NULL
	,ace_order				int			NOT NULL
	,sid					bigint		NOT NULL
	,mask					integer		NOT NULL
	,granting				boolean		NOT NULL
	,audit_success			boolean		NOT NULL
	,audit_failure			boolean		NOT NULL
	,PRIMARY KEY (id)
	,CONSTRAINT unique_uk_4 UNIQUE(acl_object_identity, ace_order)
	,CONSTRAINT foreign_fk_4 FOREIGN KEY (acl_object_identity) REFERENCES acl_object_identity(id)
	,CONSTRAINT foreign_fk_5 FOREIGN KEY (sid) REFERENCES acl_sid(id)
);

CREATE TABLE groups (
	id			bigserial NOT NULL
	,group_name	varchar(255)
	,PRIMARY KEY (id)
);

CREATE TABLE domains (
	id				bigserial NOT NULL
	,domain_name	varchar(255)
	,PRIMARY KEY (id)
);

CREATE TABLE fedora_object (
	id			bigserial	NOT NULL
	,pid		varchar(64)	NOT NULL
	,group_id	bigint		NOT NULL
	,published	boolean		NOT NULL
	,PRIMARY KEY (id)
	,CONSTRAINT foreign_fk_6 FOREIGN KEY (group_id) REFERENCES groups (id)
);

CREATE TABLE publish_location (
	id				bigserial		NOT NULL
	,code			varchar(20)		NOT NULL
	,name			varchar(255)	NOT NULL
	,execute_class	varchar(255)	NOT NULL
	,requires		bigint			NULL
	,PRIMARY KEY (id)
	,UNIQUE (code)
);