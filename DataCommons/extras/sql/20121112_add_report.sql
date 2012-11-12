insert into report (report_name, report_template, sub_report)
values ('Web Service Report','report_tmplt.jasper','subRpt_webService.jasper');

insert into report_param (id, seq_num, param_name, request_param, default_value)
select report.id
	,1
	,'param1'
	,'rid'
	,null
from report
where report_name = 'Web Service Report';
