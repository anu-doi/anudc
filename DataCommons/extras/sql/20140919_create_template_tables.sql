--drop table if exists template_publish_location;
--drop table if exists template;

create table template (
	id	bigserial
	, template_pid	varchar(64)
	, name	varchar(100)
	, description text
	, PRIMARY KEY (id)
);

insert into template (template_pid, name, description) values ('tmplt:1', 'Collection Template', 'For recording metadata about Datasets, Catalogues, Indexes, Collections, Registries, and Repositories');
insert into template (template_pid, name, description) values ('tmplt:2', 'Activity Template', 'Template recording metadata about activities such as projects and courses');
insert into template (template_pid, name, description) values ('tmplt:3', 'Service Template', 'Template for recording metadata about services');
insert into template (template_pid, name, description) values ('tmplt:4', 'Party Template', 'Template for recording metadata about groups and administrative positions');
insert into template (template_pid, name, description) values ('tmplt:5', 'Person Template', 'Template for recording metadata about people');
insert into template (template_pid, name, description) values ('tmplt:6', 'PAMBU Author Template', 'Template for recording PAMBU Author metadata');
insert into template (template_pid, name, description) values ('tmplt:7', 'PAMBU Collection Template ', 'Template for recording PAMBU Record metadata');


create table template_publish_location (
	template_id		bigint
	, location_id	bigint
	, PRIMARY KEY (template_id, location_id)
	, CONSTRAINT fk_template_publish_location_1 FOREIGN KEY (template_id) REFERENCES template (id)
	, CONSTRAINT fk_template_publish_location_2 FOREIGN KEY (location_id) REFERENCES publish_location (id)
);

insert into template_publish_location (template_id, location_id)
select template.id
	, publish_location.id
from template, publish_location
where publish_location.code = 'PAMBU'
and template.template_pid in ('tmplt:6', 'tmplt:7');

insert into template_publish_location (template_id, location_id)
select template.id
	, publish_location.id
from template, publish_location
where publish_location.code = 'IRIS'
and template.template_pid = 'tmplt:1';

insert into template_publish_location (template_id, location_id)
select template.id
	, publish_location.id
from template, publish_location
where publish_location.code = 'ANU'
and template.template_pid in ('tmplt:1', 'tmplt:2', 'tmplt:3', 'tmplt:4', 'tmplt:5', 'tmplt:6', 'tmplt:7');

insert into template_publish_location (template_id, location_id)
select template.id
	, publish_location.id
from template, publish_location
where publish_location.code = 'ANDS'
and template.template_pid in ('tmplt:1', 'tmplt:2', 'tmplt:3', 'tmplt:4', 'tmplt:5', 'tmplt:7');

insert into acl_class (id, class) values (4, 'au.edu.anu.datacommons.data.db.model.PublishLocation');
insert into acl_class (id, class) values (5, 'au.edu.anu.datacommons.data.db.model.Template');

insert into acl_object_identity (object_id_class, object_id_identity, owner_sid, entries_inheriting)
select 4
	, publish_location.id
	, acl_sid.id
	, '1'
from publish_location, acl_sid
where acl_sid.sid = 'ROLE_ADMIN';

insert into acl_object_identity (object_id_class, object_id_identity, owner_sid, entries_inheriting)
select 5
	, template.id
	, acl_sid.id
	, '1'
from template, acl_sid
where acl_sid.sid = 'ROLE_ADMIN';

insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
select acl_object_identity.id
	, 0
	, acl_sid.id
	, 2
	, '1'
	, '0'
	, '0'
from acl_object_identity, acl_sid
where acl_sid.sid = 'ROLE_ADMIN'
and acl_object_identity.object_id_class = 5;

insert into acl_sid (sid, principal) values ('ROLE_ANU_USER','0');

insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
select acl_object_identity.id as acl_object_identity
	, 1 as ace_order
	, acl_sid.id
	, 2 as mask
	, '1' as granting
	, '0' as audit_success
	, '0' as audit_failure
from template, acl_object_identity, acl_sid
where template.template_pid in ('tmplt:1', 'tmplt:2', 'tmplt:3', 'tmplt:4', 'tmplt:5')
and acl_object_identity.object_id_class = 5
and acl_object_identity.object_id_identity = template.id
and acl_sid.sid = 'ROLE_ANU_USER';

insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
select acl_object_identity.id as acl_object_identity
	, 0 as ace_order
	, acl_sid.id
	, 64 as mask
	, '1' as granting
	, '0' as audit_success
	, '0' as audit_failure
from publish_location, acl_object_identity, acl_sid
where acl_object_identity.object_id_class = 4
and acl_object_identity.object_id_identity = publish_location.id
and acl_sid.sid = 'ROLE_ADMIN';

insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
select acl_object_identity.id as acl_object_identity
	, 1 as ace_order
	, acl_sid.id
	, 64 as mask
	, '1' as granting
	, '0' as audit_success
	, '0' as audit_failure
from publish_location, acl_object_identity, acl_sid
where publish_location.code in ('ANU', 'ANDS')
and acl_object_identity.object_id_class = 4
and acl_object_identity.object_id_identity = publish_location.id
and acl_sid.sid = 'ROLE_ANU_USER';
