create table question_bank_option (
	id	bigserial
	, question_id	bigint
	, option_value	text
);

alter table question_map add column seq_num int;

update question_map
set seq_num = v_question_map.id
from 
(
	select row_number() over (partition by domain_fk, group_fk, pid) as rn, id
	from question_map
) as v_question_map
where question_map.id = v_question_map.id;

alter table collection_request_answers add column answer_order int;

insert into question_bank (question_text) values 
	('What is your intended use of data?')
	, ('Please provide a brief summary of your proposed research and the intended use of this  data.')
	, ('How will the analysis method be made available?')
	, ('Will security and access controls be in place for this data?')
	, ('If Yes, please provide the details for the security and access measures to be put in place:')
	, ('Will any reuse of this data be made publicy available?')
	, ('If No, please provide your reasons for not making this data publicly available:')
	, ('Will a copy of the results be provided to the data owner?')
	, ('If No, please provide your reasons for not providing a copy of the results to the data owner:')
	, ('Will this data be used for commercial or financial gain?')
	, ('If Yes, please provide the details for how this  data will be used for commercial or financial gain:')
	, ('Requirements on conditions of use')
	, ('If Other, please specify')
	;

insert into question_bank_option (question_id, option_value) 
select id as question_id, unnest(array['Pure Research', 'Government Research', 'Teaching Purposed', 'Research Consultany', 'Commercial Research', 'Thesis or coursework', 'Other (please specify)']) as list_value
from question_bank qb
where qb.question_text = 'What is your intended use of data?';

insert into question_bank_option (question_id, option_value) 
select id as question_id, unnest(array['Yes', 'No']) as list_value
from question_bank qb
where qb.question_text in ('Will security and access controls be in place for this data?','Will any reuse of this data be made publicy available?','Will a copy of the results be provided to the data owner?','Will this data be used for commercial or financial gain?');

delete from question_map
using domains d
where d.domain_name = 'The Australian National University'
and d.id = question_map.domain_fk;

insert into question_map (question_fk, domain_fk, required, seq_num)
select qb.id, d.id, '1', 1
from question_bank qb
	, domains d
where d.domain_name = 'The Australian National University'
and qb.question_text in 
	('What is your intended use of data?');
	
insert into question_map (question_fk, domain_fk, required, seq_num)
select qb.id, d.id, '0', 2
from question_bank qb
	, domains d
where d.domain_name = 'The Australian National University'
and qb.question_text in 
	('If Other, please specify');
	
insert into question_map (question_fk, domain_fk, required, seq_num)
select qb.id, d.id, '1', 3
from question_bank qb
	, domains d
where d.domain_name = 'The Australian National University'
and qb.question_text in 
	('Please provide a brief summary of your proposed research and the intended use of this  data.');
	
insert into question_map (question_fk, domain_fk, required, seq_num)
select qb.id, d.id, '1', 4
from question_bank qb
	, domains d
where d.domain_name = 'The Australian National University'
and qb.question_text in 
	('How will the analysis method be made available?');
	
insert into question_map (question_fk, domain_fk, required, seq_num)
select qb.id, d.id, '1', 5
from question_bank qb
	, domains d
where d.domain_name = 'The Australian National University'
and qb.question_text in 
	('Will security and access controls be in place for this data?');
	
insert into question_map (question_fk, domain_fk, required, seq_num)
select qb.id, d.id, '0', 6
from question_bank qb
	, domains d
where d.domain_name = 'The Australian National University'
and qb.question_text in 
	('If Yes, please provide the details for the security and access measures to be put in place:');
	
insert into question_map (question_fk, domain_fk, required, seq_num)
select qb.id, d.id, '1', 7
from question_bank qb
	, domains d
where d.domain_name = 'The Australian National University'
and qb.question_text in 
	('Will any reuse of this data be made publicy available?');
	
insert into question_map (question_fk, domain_fk, required, seq_num)
select qb.id, d.id, '0', 8
from question_bank qb
	, domains d
where d.domain_name = 'The Australian National University'
and qb.question_text in 
	('If No, please provide your reasons for not making this data publicly available:');
	
insert into question_map (question_fk, domain_fk, required, seq_num)
select qb.id, d.id, '1', 9
from question_bank qb
	, domains d
where d.domain_name = 'The Australian National University'
and qb.question_text in 
	('Will a copy of the results be provided to the data owner?');
	
insert into question_map (question_fk, domain_fk, required, seq_num)
select qb.id, d.id, '0', 10
from question_bank qb
	, domains d
where d.domain_name = 'The Australian National University'
and qb.question_text in 
	('If No, please provide your reasons for not providing a copy of the results to the data owner:');
	
insert into question_map (question_fk, domain_fk, required, seq_num)
select qb.id, d.id, '1', 11
from question_bank qb
	, domains d
where d.domain_name = 'The Australian National University'
and qb.question_text in 
	('Will this data be used for commercial or financial gain?');
	
insert into question_map (question_fk, domain_fk, required, seq_num)
select qb.id, d.id, '0', 12
from question_bank qb
	, domains d
where d.domain_name = 'The Australian National University'
and qb.question_text in 
	('If Yes, please provide the details for how this  data will be used for commercial or financial gain:');
	
insert into question_map (question_fk, domain_fk, required, seq_num)
select qb.id, d.id, '1', 13
from question_bank qb
	, domains d
where d.domain_name = 'The Australian National University'
and qb.question_text in 
	('Requirements on conditions of use');




