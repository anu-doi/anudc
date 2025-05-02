update template_attribute
set tooltip = 'Please choose the access privilege group for the collection, if Access Privileges field is empty for you, please contact <a href="mailto:repository.admin@anu.edu.au">repository.admin@anu.edu.au</a> to organise your access privileges'
where name = 'ownerGroup'
and tooltip = 'Please choose the access privilege group for the collection';
