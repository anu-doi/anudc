insert into groups (id, group_name) values
(1, 'Administrators');

insert into acl_sid (id, principal, sid) values
(1,'0','ROLE_ADMIN');

insert into acl_class (id, class) values
(1,'au.edu.anu.datacommons.connection.db.model.Domains'),
(2,'au.edu.anu.datacommons.connection.db.model.Groups'),
(3,'au.edu.anu.datacommons.connection.db.model.FedoraObject');
