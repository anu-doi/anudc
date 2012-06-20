update acl_class
set class = 'au.edu.anu.datacommons.data.db.model.Domains'
where id = 1;

update acl_class
set class = 'au.edu.anu.datacommons.data.db.model.Groups'
where id = 2;

update acl_class
set class = 'au.edu.anu.datacommons.data.db.model.FedoraObject'
where id = 3;