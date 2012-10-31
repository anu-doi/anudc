insert into report (report_name, report_template, sub_report)
values ('Publish Location Report','report_tmplt.jasper','subRpt_locationPublished.jasper');

insert into report_param (id, seq_num, param_name, request_param, default_value)
select report.id
	,1
	,'param1'
	,'location'
	,'ANDS'
from report
where report_name = 'Publish Location Report';

insert into report (report_name, report_template, sub_report)
values ('Records with Digital Object Identifiers','report_tmplt.jasper','subRpt_doi.jasper');
