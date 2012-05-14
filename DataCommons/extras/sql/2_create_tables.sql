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

-- Table: collection_requests

-- DROP TABLE collection_requests;

CREATE TABLE collection_requests
(
  id bigserial NOT NULL,
  pid character varying(255) NOT NULL,
  requestor_id bigint NOT NULL,
  requestor_ip character varying(255),
  "timestamp" timestamp without time zone NOT NULL,
  dropbox_id bigint,
  CONSTRAINT collection_requests_pkey PRIMARY KEY (id )
  )
;
ALTER TABLE collection_requests
  OWNER TO dcuser;
  
-- Table: collection_dropboxes

-- DROP TABLE collection_dropboxes;

CREATE TABLE collection_dropboxes
(
  id bigserial NOT NULL,
  access_code bigint NOT NULL,
  access_password character varying(255) NOT NULL,
  active boolean NOT NULL,
  creator_user_id bigint NOT NULL,
  expiry date NOT NULL,
  notifyonpickup boolean,
  created timestamp without time zone NOT NULL,
  request_fk bigint,
  CONSTRAINT collection_dropboxes_pkey PRIMARY KEY (id ),
  CONSTRAINT fk79688dc93ef24101 FOREIGN KEY (request_fk)
      REFERENCES collection_requests (id),
        CONSTRAINT collection_dropboxes_access_code_key UNIQUE (access_code )
)
;
ALTER TABLE collection_dropboxes
  OWNER TO dcuser;

  
-- Table: collection_dropbox_access_logs

-- DROP TABLE collection_dropbox_access_logs;

CREATE TABLE collection_dropbox_access_logs
(
  id bigserial NOT NULL,
  ip_address character varying(255) NOT NULL,
  "timestamp" timestamp without time zone NOT NULL,
  dropbox_fk bigint NOT NULL,
  CONSTRAINT collection_dropbox_access_logs_pkey PRIMARY KEY (id ),
  CONSTRAINT fk66fa0146be4a5de1 FOREIGN KEY (dropbox_fk)
      REFERENCES collection_dropboxes (id)
      )
;
ALTER TABLE collection_dropbox_access_logs
  OWNER TO dcuser;

  
-- Table: question_bank

-- DROP TABLE question_bank;

CREATE TABLE question_bank
(
  id bigserial NOT NULL,
  question character varying(255) NOT NULL,
  CONSTRAINT question_bank_pkey PRIMARY KEY (id ),
  CONSTRAINT question_bank_question_key UNIQUE (question )
)
;
ALTER TABLE question_bank
  OWNER TO dcuser;



-- Table: collection_request_answers

-- DROP TABLE collection_request_answers;

CREATE TABLE collection_request_answers
(
  id bigserial NOT NULL,
  answer character varying(255) NOT NULL,
  request_fk bigint NOT NULL,
  question_fk bigint NOT NULL,
  CONSTRAINT collection_request_answers_pkey PRIMARY KEY (id ),
  CONSTRAINT fk71965b243ef24101 FOREIGN KEY (request_fk)
      REFERENCES collection_requests (id),
        CONSTRAINT fk71965b24b5c126c9 FOREIGN KEY (question_fk)
      REFERENCES question_bank (id)
      )
;
ALTER TABLE collection_request_answers
  OWNER TO dcuser;

-- Table: collection_request_items

-- DROP TABLE collection_request_items;

CREATE TABLE collection_request_items
(
  id bigserial NOT NULL,
  item character varying(255) NOT NULL,
  request_fk bigint NOT NULL,
  CONSTRAINT collection_request_items_pkey PRIMARY KEY (id ),
  CONSTRAINT fk67ed6a2f3ef24101 FOREIGN KEY (request_fk)
      REFERENCES collection_requests (id)
      )
;
ALTER TABLE collection_request_items
  OWNER TO dcuser;

-- Table: collection_request_status

-- DROP TABLE collection_request_status;

CREATE TABLE collection_request_status
(
  id bigserial NOT NULL,
  reason character varying(255) NOT NULL,
  status integer NOT NULL,
  "timestamp" timestamp without time zone NOT NULL,
  user_id bigint NOT NULL,
  request_fk bigint,
  CONSTRAINT collection_request_status_pkey PRIMARY KEY (id ),
  CONSTRAINT fka6ce9b633ef24101 FOREIGN KEY (request_fk)
      REFERENCES collection_requests (id)
      )
;
ALTER TABLE collection_request_status
  OWNER TO dcuser;


-- Table: question_map

-- DROP TABLE question_map;

CREATE TABLE question_map
(
  id bigserial NOT NULL,
  pid character varying(255) NOT NULL,
  question_fk bigint,
  CONSTRAINT question_map_pkey PRIMARY KEY (id ),
  CONSTRAINT fkf5c2ad83b5c126c9 FOREIGN KEY (question_fk)
      REFERENCES question_bank (id),
        CONSTRAINT question_map_pid_question_fk_key UNIQUE (pid , question_fk )
)
;
ALTER TABLE question_map
  OWNER TO dcuser;

