insert into domains (id, domain_name) values
(1, 'Administrators');

insert into groups (id, group_name) values
(1, 'Administrators');

insert into acl_sid (id, principal, sid) values
(1,'0','ROLE_ADMIN');

insert into acl_class (id, class) values
(1,'au.edu.anu.datacommons.connection.db.model.Domains');
insert into acl_class (id, class) values
(2,'au.edu.anu.datacommons.connection.db.model.Groups');
insert into acl_class (id, class) values
(3,'au.edu.anu.datacommons.connection.db.model.FedoraObject');

insert into acl_object_identity (id, object_id_class, object_id_identity, owner_sid, parent_object, entries_inheriting) values
(1,1,1,1,null,'1');
insert into acl_object_identity (id, object_id_class, object_id_identity, owner_sid, parent_object, entries_inheriting) values
(2,2,1,1,1,'1');
