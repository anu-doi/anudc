
insert into users_type (type_name) values
('ANU User');

insert into users_type (type_name) values
('Registered User');

select setval('users_type_id_seq', (SELECT MAX(id) FROM users_type));

insert into domains (id, domain_name) values
(1, 'Administrators');

insert into groups (id, group_name) values
(1, 'Administrators');

select setval('groups_id_seq', (SELECT MAX(id) FROM groups));

insert into acl_sid (id, principal, sid) values
(1,'0','ROLE_ADMIN');

select setval('acl_sid_id_seq', (SELECT MAX(id) FROM acl_sid));

insert into acl_class (id, class) values
(1,'au.edu.anu.datacommons.connection.db.model.Domains');
insert into acl_class (id, class) values
(2,'au.edu.anu.datacommons.connection.db.model.Groups');
insert into acl_class (id, class) values
(3,'au.edu.anu.datacommons.connection.db.model.FedoraObject');

select setval('acl_class_id_seq', (SELECT MAX(id) FROM acl_class));

insert into acl_object_identity (id, object_id_class, object_id_identity, owner_sid, parent_object, entries_inheriting) values
(1,1,1,1,null,'1');
insert into acl_object_identity (id, object_id_class, object_id_identity, owner_sid, parent_object, entries_inheriting) values
(2,2,1,1,1,'1');

select setval('acl_object_identity_id_seq', (SELECT MAX(id) FROM acl_object_identity));

INSERT INTO publish_location (code, name, execute_class, requires) VALUES
('ANU', 'Australian National University','au.edu.anu.datacommons.publish.ANUPublish', null);

INSERT INTO publish_location (code, name, execute_class, requires) VALUES
('ANDS', 'Australian National Data Service','au.edu.anu.datacommons.publish.ANDSPublish',1);

select setval('publish_location_id_seq', (SELECT MAX(id) FROM publish_location));
