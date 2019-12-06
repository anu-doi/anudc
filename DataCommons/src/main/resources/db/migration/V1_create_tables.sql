create table users_type (
	id	bigserial	not null
	, type_name	varchar(255)	not null
	, primary key (id)
);

create table users (
	id	bigserial	not null
	, username	varchar(50)	not null
	, password	varchar(50)	not null
	, enabled	boolean	not null
	, user_type	bigint	not null
	, primary key (id)
	, constraint fk_users_1 foreign key (user_type) references users_type (id)
);
create unique index ix_user_username on users (username);

create table user_registered (
	id	bigint	not null
	, last_name	varchar(40)
	, given_name	varchar(40)
	, institution	text
	, phone	text
	, address	text
	, primary key (id)
	, constraint fk_user_registered_1 foreign key (id) references users (id)
);

create table authorities (
	username varchar(50)	not null
	, authority	varchar(50)	not null
	, constraint fk_authorities_users foreign key (username) references users(username)
);
create unique index ix_auth_username on authorities (username, authority);

create table acl_sid (
	id	bigserial	not null
	, principal	boolean	not null
	, sid	varchar(100)	not null
	, primary key (id)
	, constraint unique_uk_1 unique(sid, principal)
);

create table acl_class (
	id	bigserial	not null
	, class	varchar(255)	not null
	, primary key (id)
	, constraint unique_uk_2 unique(class)
);

create table acl_object_identity (
	id	bigserial	not null
	, object_id_class	bigint	not null
	, object_id_identity	bigint	not null
	, owner_sid	bigint
	, parent_object	bigint
	, entries_inheriting	boolean	not null
	, primary key (id)
	, constraint unique_uk_3 unique(object_id_class, object_id_identity)
	, constraint foreign_fk_1 foreign key(parent_object) references acl_object_identity(id)
	, constraint foreign_fk_2 foreign key(object_id_class) references acl_class(id)
	, constraint foreign_fk_3 foreign key(owner_sid) references acl_sid(id)
);

CREATE TABLE acl_entry (
	id						bigserial	NOT NULL
	, acl_object_identity	bigint		NOT NULL
	, ace_order				int			NOT NULL
	, sid					bigint		NOT NULL
	, mask					integer		NOT NULL
	, granting				boolean		NOT NULL
	, audit_success			boolean		NOT NULL
	, audit_failure			boolean		NOT NULL
	, PRIMARY KEY (id)
	, CONSTRAINT unique_uk_4 UNIQUE(acl_object_identity, ace_order)
	, CONSTRAINT foreign_fk_4 FOREIGN KEY (acl_object_identity) REFERENCES acl_object_identity(id)
	, CONSTRAINT foreign_fk_5 FOREIGN KEY (sid) REFERENCES acl_sid(id)
);

CREATE TABLE groups (
	id			bigserial NOT NULL
	,group_name	varchar(255)
	,PRIMARY KEY (id)
);

CREATE TABLE domains (
	id				bigserial NOT NULL
	, domain_name	varchar(255)
	, PRIMARY KEY (id)
);

CREATE TABLE fedora_object (
	id			bigserial	NOT NULL
	, pid		varchar(64)	NOT NULL
	, group_id	bigint		NOT NULL
	, published	boolean		NOT NULL
	, tmplt_id	text
	, is_files_public	boolean	not null
	,PRIMARY KEY (id)
	,CONSTRAINT foreign_fk_6 FOREIGN KEY (group_id) REFERENCES groups (id)
);

CREATE TABLE publish_location (
	id				bigserial		NOT NULL
	, code			varchar(20)		NOT NULL
	, name			varchar(255)	NOT NULL
	, execute_class	varchar(255)	NOT NULL
	, requires		bigint			NULL
	, PRIMARY KEY (id)
	, UNIQUE (code)
);

CREATE TABLE published (
	fedora_id				bigint		NOT NULL
	, location_id			bigint		NOT NULL
	, PRIMARY KEY (fedora_id, location_id)
	, CONSTRAINT fk_published_1 foreign key (fedora_id) references fedora_object(id)
	, CONSTRAINT fk_published_2 foreign key (location_id) references publish_location(id)
);

CREATE TABLE publish_iris (
	pid				varchar(64)	not null
	, publish_date	timestamp	not null
	, status		varchar(20)	not null
	, iris_network	varchar(20)
	, PRIMARY KEY (pid, publish_date)
);

CREATE TABLE collection_requests (
  id bigserial NOT NULL
  , pid character varying(255) NOT NULL
  , requestor_ip character varying(255)
  ,  "timestamp" timestamp without time zone NOT NULL
  , dropbox_id bigint
  , requestor_fk bigint NOT NULL
  , object_fk bigint
  , primary key (id)
  , constraint fk_collection_requests_1 foreign key (requestor_fk) references users (id)
  , constraint fk_collection_requests_2 foreign key (object_fk) references fedora_object(id)
);

CREATE TABLE collection_dropboxes (
  id	bigserial	NOT NULL
  , access_code	bigint	NOT NULL
  , access_password	character varying(255)	NOT NULL
  , active	boolean	NOT NULL
  , expiry	date	NOT NULL
  , notifyonpickup	boolean
  , created	timestamp without time zone	NOT NULL
  , request_fk	bigint
  , creator_fk	bigint	NOT NULL
  , primary key (id)
  , constraint fk_collection_dropboxes_1 foreign key (request_fk) references collection_requests(id)
  , constraint fk_collection_dropboxes_2 foreign key (creator_fk) references users(id)
  , unique (access_code)
);

CREATE TABLE collection_dropbox_access_logs (
  id bigserial NOT NULL
  , ip_address character varying(255) NOT NULL
  , "timestamp" timestamp without time zone NOT NULL
  , dropbox_fk bigint NOT NULL
  , primary key (id)
  , constraint fk_collection_dropbox_access_logs_1 foreign key (dropbox_fk) references collection_dropboxes (id)
);

CREATE TABLE question_bank (
  id bigserial NOT NULL
  ,question_text character varying(255) NOT NULL
  , primary key (id)
  , unique (question_text)
);

CREATE TABLE collection_request_answers (
  id bigserial NOT NULL
  , answer character varying(255) NOT NULL
  , request_fk bigint NOT NULL
  , question_fk bigint NOT NULL
  , primary key (id)
  , constraint fk_collection_request_answers_1 foreign key (request_fk) references collection_requests (id)
  , constraint fk_collection_request_answers_2 foreign key (question_fk) references question_bank (id)
);

CREATE TABLE collection_request_items (
  id bigserial NOT NULL
  , item character varying(255) NOT NULL
  , request_fk bigint NOT NULL
  , primary key (id)
  , constraint fk_collection_Request_items_1 foreign key (request_fk) references collection_requests (id)
);

CREATE TABLE collection_request_status (
  id bigserial NOT NULL
  , reason character varying(255) NOT NULL
  , status integer NOT NULL
  , "timestamp" timestamp without time zone NOT NULL
  , request_fk bigint
  , user_fk bigint NOT NULL
  , primary key (id)
  , constraint fk_collection_request_status_1 foreign key (request_fk) references collection_requests (id)
  , constraint fk_collection_request_status_2 foreign key (user_fk) references users (id)
);

CREATE TABLE question_map (
  id bigserial NOT NULL
  , pid character varying(255)
  , question_fk bigint
  , group_fk bigint
  , domain_fk bigint
  , required boolean NOT NULL
  , primary key (id)
  , constraint fk_question_map_1 foreign key (group_fk) references groups (id)
  , constraint fk_question_map_2 foreign key (domain_fk) references domains (id)
  , unique (pid, question_fk)
  , unique (pid, group_fk, domain_fk, question_fk)
);

CREATE TABLE audit_object (
  id bigserial NOT NULL
  , log_date timestamp without time zone NOT NULL
  , log_type character varying(16) NOT NULL
  , object_id bigint NOT NULL
  , user_id bigint NOT NULL
  , before text
  , after text
  , rid bigint
  , primary key (id)
  , constraint fk_audit_object_1 foreign key (object_id) references fedora_object(id)
  , constraint fk_audit_object_2 foreign key (user_id) references users(id)
);

CREATE TABLE select_code (
	select_name		varchar(30)		NOT NULL
	,code			varchar(30)		NOT NULL
	,description	varchar(255)	NOT NULL
	,deprecated		boolean			NULL
	,PRIMARY KEY (select_name, code)
);

CREATE TABLE access_logs (
  id bigserial NOT NULL
  , uri varchar(255) NOT NULL
  , user_fk bigint
  , operation integer NOT NULL
  , "timestamp" timestamp without time zone NOT NULL
  , ip_address	varchar(255) NOT NULL
  , user_agent	varchar(255)
  , primary key (id)
  , constraint fk_access_logs_1 foreign key (user_fk) references users (id)
);

create table review_ready (
	id	bigint	not null
	, date_submitted	timestamp	not null
	, PRIMARY KEY (id)
	, CONSTRAINT fk_review_fedora FOREIGN KEY (id) REFERENCES fedora_object(id)
);

create table review_reject (
	id					bigint		not null
	, date_submitted	timestamp	not null
	, reason			text		not null
	, PRIMARY KEY (id)
	, CONSTRAINT fk_review_fedora FOREIGN KEY (id) REFERENCES fedora_object(id)
);

create table publish_ready (
	id	bigint	not null
	, date_submitted	timestamp	not null
	, PRIMARY KEY (id)
	, CONSTRAINT fk_publish_fedora FOREIGN KEY (id) REFERENCES fedora_object(id)
);

CREATE TABLE user_request_pwd (
	id				bigserial	not null
	, user_id		bigint		not null
	, request_date	timestamp	not null
	, ip_address		varchar(30)
	, link_id		text		not null
	, used			boolean
	, PRIMARY KEY (id)
	, CONSTRAINT fk_user_request_pwd_1 FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE link_type (
	id				bigserial	not null
	, code			varchar(60)	not null
	, description	text		not null
	, reverse	varchar(60)
	, PRIMARY KEY (id)
);

CREATE TABLE link_relation (
	category1		varchar(30)	not null
	, category2		varchar(30)	not null
	, link_type_id	bigint		not null
	, PRIMARY KEY (category1, category2, link_type_id)
	, CONSTRAINT fk_link_relation_1 FOREIGN KEY (link_type_id) REFERENCES link_type (id)
);

create table audit_access (
	id				bigserial		not null
	, access_date	timestamp		not null
	, ip_address	varchar(30)		null
	, url			text			not null
	, method		varchar(10)		not null
	, pid			varchar(64)		null
	, username		varchar(255)	null
	, rid			bigint
	, PRIMARY KEY (id)
);
CREATE INDEX ix_audit_access_1 ON audit_access (pid);

CREATE TABLE report (
	id					bigserial		not null
	, report_name		varchar(50)		not null
	, report_template	varchar(100)	not null
	, sub_report		varchar(100)
	, PRIMARY KEY (id)
);

CREATE TABLE report_param (
	id				bigint
	, seq_num		int
	, param_name	varchar(50)
	, request_param	varchar(50)
	, default_value	varchar(255)
	, PRIMARY KEY (id, seq_num)
	, CONSTRAINT fk_report_params_1 FOREIGN KEY (id) REFERENCES report (id)
);

CREATE TABLE log_external_web_resource
(
  id bigserial NOT NULL
  , pid character varying(255)
  , http_request text NOT NULL
  , request_timestamp timestamp without time zone NOT NULL
  , http_response text
  , response_timestamp timestamp without time zone
  , primary key (id)
);

CREATE TABLE log_webservice
(
  id bigserial NOT NULL
  , function_name character varying(255)
  , requestor_ip character varying(255)
  , http_request text NOT NULL
  , request_timestamp timestamp without time zone
  , http_response text
  , response_timestamp timestamp without time zone
  , primary key (id)
);

CREATE TABLE link_external_pattern (
	id				bigserial		not null
	, object_type	varchar(30)		not null
	, pattern		varchar(255)	not null
	, description	varchar(255)	not null
	, PRIMARY KEY (id)
	, UNIQUE (object_type, pattern)
);

CREATE TABLE report_auto (
	id			bigserial	not null
	, report_id	bigint		not null
	, email		varchar(255)
	, cron		text		not null
	, format	text
	,PRIMARY KEY (id)
	,CONSTRAINT fk_report_auto_1 FOREIGN KEY (report_id) REFERENCES report (id)
);

CREATE TABLE report_auto_param (
	id			bigint	not null
	, seq_num	int		not null
	, param		text	not null
	, param_val	text	not null
	, PRIMARY KEY (id, seq_num)
	, CONSTRAINT fk_report_auto_param_1 FOREIGN KEY (id) REFERENCES report_auto (id)
);

create table template (
	id	bigserial
	, template_pid	varchar(64)
	, name	varchar(100)
	, description text
	, PRIMARY KEY (id)
);

CREATE TABLE template_publish_location
(
  template_id bigint NOT NULL
  ,location_id bigint NOT NULL
  , primary key (template_id, location_id)
  , CONSTRAINT fk_template_publish_location_1 FOREIGN KEY (template_id) REFERENCES template (id)
  , CONSTRAINT fk_template_publish_location_2 FOREIGN KEY (location_id) REFERENCES publish_location (id)
)