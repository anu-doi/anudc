alter table question_map add column group_fk bigint, add column domain_fk bigint, add column required boolean;
update question_map set required = '1';
alter table question_map alter column required set not null;

alter table question_map add constraint question_map_uq_1 UNIQUE (pid, group_fk, domain_fk, question_fk);
alter table question_map add constraint question_map_fk_1 FOREIGN KEY (group_fk) REFERENCES groups (id);
alter table question_map add constraint question_map_fk_2 FOREIGN KEY (domain_fk) REFERENCES domains (id);

alter table question_map drop constraint fkf5c2ad83b5c126c9;

alter table question_map alter column pid drop not null;

insert into domains (domain_name) values ('The Australian National University');

insert into acl_object_identity (object_id_class, object_id_identity, owner_sid, entries_inheriting)
select 1 as object_id_class
	,domains.id as object_id_identity
	,acl_sid.id as owner_sid
	,'1' as entries_inheriting
from domains, acl_sid
where domains.domain_name = 'The Australian National University'
and acl_sid.sid = 'ROLE_ADMIN';

update acl_object_identity
set parent_object = anu_obj.id
from domains, acl_object_identity anu_obj
where domains.domain_name = 'The Australian National University'
and anu_obj.object_id_class = 1
and anu_obj.object_id_identity = domains.id
and acl_object_identity.object_id_class = 1
and anu_obj.id != acl_object_identity.id
and acl_object_identity.parent_object is null;


-- Add the ANU questions
insert into question_bank (question_text) values ('Why do you wish access to this data');
insert into question_bank (question_text) values ('Do you agree to fully acknowledge this data in any publication');
insert into question_bank (question_text) values ('What is the intended use for this data');
insert into question_bank (question_text) values ('What security and access controls will be in place for the downloaded data');
insert into question_bank (question_text) values ('Will the data be used for commercial purposes or financial gain');
insert into question_bank (question_text) values ('Will the data be used in a commercial/open source product');
insert into question_bank (question_text) values ('Will the results of any re-use of this data be publicly available');
insert into question_bank (question_text) values ('Where will the results for the reanalysis of this data be made available');
insert into question_bank (question_text) values ('Will a copy of the results be made available to the data owner');
insert into question_bank (question_text) values ('Will a copy of the analysis method be made publicly available');
insert into question_bank (question_text) values ('Where will the analysis method be made available');

insert into question_map (question_fk, domain_fk, required)
select question_bank.id as question_fk
	, domains.id as domain_fk
	, '1' as required
from domains, question_bank
where domain_name = 'The Australian National University'
and question_bank.question_text in ('Why do you wish access to this data'
		, 'Do you agree to fully acknowledge this data in any publication'
		, 'What is the intended use for this data'
		, 'What security and access controls will be in place for the downloaded data'
		, 'Will the data be used for commercial purposes or financial gain');
		
insert into question_map (question_fk, domain_fk, required)
select question_bank.id as question_fk
	, domains.id as domain_fk
	, '0' as required
from domains, question_bank
where domain_name = 'The Australian National University'
and question_bank.question_text in ('Will the data be used in a commercial/open source product'
		,'Will the results of any re-use of this data be publicly available'
		,'Where will the results for the reanalysis of this data be made available'
		,'Will a copy of the results be made available to the data owner'
		,'Will a copy of the analysis method be made publicly available'
		,'Where will the analysis method be made available');

-- Add the PAMBU questions
insert into question_bank (question_text) values ('What is the PMB number of the collection you are asking about?');
insert into question_bank (question_text) values ('Please state your enquiry - let us know as much detail as possible');
insert into question_bank (question_text) values ('Please describe how you intend using this collection');
insert into question_bank (question_text) values ('Please state the country where you live');

insert into question_map (question_fk, domain_fk, required)
select question_bank.id as question_fk
	, domains.id as domain_fk
	, '1' as required
from domains, question_bank
where domain_name = 'Pacific Manuscripts Bureau'
and question_bank.question_text in ('What is the PMB number of the collection you are asking about?'
	, 'Please state your enquiry - let us know as much detail as possible'
	, 'Please describe how you intend using this collection'
	, 'Please state the country where you live');