update template
set name = 'Collection'
	, description = 'datasets, catalogues, indexes, collections, registries  and repositories'
where template_pid = 'tmplt:1';

update template
set name = 'Activity'
	, description = 'projects and courses'
where template_pid = 'tmplt:2';

update template
set name = 'Services'
	, description = 'A variety of service types'
where template_pid = 'tmplt:3';

update template
set name = 'Party'
	, description = 'groups and administrative positions'
where template_pid = 'tmplt:4';

update template
set name = 'Person'
	, description = 'metadata about people'
where template_pid = 'tmplt:5';

update template
set name = 'PAMBU Author'
	, description = 'PAMBU Author'
where template_pid = 'tmplt:6';

update template
set name = 'PAMBU Collection'
	, description = 'PAMBU record'
where template_pid = 'tmplt:7';

update template
set name = 'LTERN Collection'
	, description = 'LTERN datasets'
where template_pid = 'tmplt:8';
