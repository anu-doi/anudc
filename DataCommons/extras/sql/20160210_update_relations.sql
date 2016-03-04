delete from link_relation
using link_type
where link_relation.category1 = 'Activity'
and link_relation.category2 = 'Party'
and link_type.code = 'isOwnedBy'
and link_relation.link_type_id = link_type.id;

delete from link_relation
using link_type
where link_relation.category1 = 'Activity'
and link_relation.category2 = 'Activity'
and link_type.code = 'isFundedBy'
and link_relation.link_type_id = link_type.id;

insert into link_type (code, description, reverse) values ('isPrincipalInvestigatorOf','Is Principal Investigator Of','hasPrincipalInvestigator');
insert into link_type (code, description, reverse) values ('hasPrincipalInvestigator','Has Principal Investigator','isPrincipalInvestigatorOf');


insert into link_relation (category1, category2, link_type_id)
select 'Activity' as category1
	, 'Party' as category2
	, link_type.id
from link_type
where link_type.code = 'hasPrincipalInvestigator';

insert into link_relation (category1, category2, link_type_id)
select 'Collection' as category1
	, 'Party' as category2
	, link_type.id
from link_type
where link_type.code = 'hasPrincipalInvestigator';

insert into link_relation (category1, category2, link_type_id)
select 'Party' as category1
	, 'Activity' as category2
	, link_type.id
from link_type
where link_type.code = 'isPrincipalInvestigatorOf';

insert into link_relation (category1, category2, link_type_id)
select 'Party' as category1
	, 'Collection' as category2
	, link_type.id
from link_type
where link_type.code = 'isPrincipalInvestigatorOf';