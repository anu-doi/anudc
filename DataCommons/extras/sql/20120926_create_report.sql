CREATE TABLE report (
	id					bigserial		not null
	, report_name		varchar(50)		not null
	, report_template	varchar(100)	not null
	, sub_report		varchar(100)
	, PRIMARY KEY (id)
);

CREATE TABLE report_param (
	id				bigint
	, seq_num		int
	, param_name	varchar(50)
	, request_param	varchar(50)
	, default_value	varchar(255)
	, PRIMARY KEY (id, seq_num)
	, CONSTRAINT fk_report_params_1 FOREIGN KEY (id) REFERENCES report (id)
);

INSERT INTO report (report_name, report_template, sub_report)
VALUES ('All Report', 'report_tmplt.jasper', 'subRpt_all.jasper');

INSERT INTO report_param (id, seq_num, param_name, request_param)
SELECT	report.id as id
	,1 as seq_num
	, 'pid' as param_name
	, 'pid' as request_param
FROM report
WHERE report_name = 'All Report';

INSERT INTO report (report_name, report_template, sub_report)
VALUES ('Modified Report', 'report_tmplt.jasper', 'subRpt_update_filter.jasper');

INSERT INTO report_param (id, seq_num, param_name, request_param)
SELECT	report.id as id
	,1 as seq_num
	, 'pid' as param_name
	, 'pid' as request_param
FROM report
WHERE report_name = 'Modified Report';

INSERT INTO report_param (id, seq_num, param_name, default_value)
SELECT	report.id as id
	,2 as seq_num
	, 'param1' as param_name
	, 'MODIFIED' as default_value
FROM report
WHERE report_name = 'Modified Report';

INSERT INTO report (report_name, report_template, sub_report)
VALUES ('Publish Report', 'report_tmplt.jasper', 'subRpt_update_filter.jasper');

INSERT INTO report_param (id, seq_num, param_name, request_param)
SELECT	report.id as id
	,1 as seq_num
	, 'pid' as param_name
	, 'pid' as request_param
FROM report
WHERE report_name = 'Publish Report';

INSERT INTO report_param (id, seq_num, param_name, default_value)
SELECT	report.id as id
	,2 as seq_num
	, 'param1' as param_name
	, 'PUBLISH' as default_value
FROM report
WHERE report_name = 'Publish Report';

INSERT INTO report (report_name, report_template, sub_report)
VALUES ('Logs Report', 'report_tmplt.jasper', 'subRpt_logs.jasper');

INSERT INTO report_param (id, seq_num, param_name, request_param)
SELECT	report.id as id
	,1 as seq_num
	, 'pid' as param_name
	, 'pid' as request_param
FROM report
WHERE report_name = 'Logs Report';

INSERT INTO report (report_name, report_template, sub_report)
VALUES ('Dropbox Report', 'report_tmplt.jasper', 'subRpt_dropbox.jasper');

INSERT INTO report_param (id, seq_num, param_name, request_param)
SELECT	report.id as id
	,1 as seq_num
	, 'pid' as param_name
	, 'pid' as request_param
FROM report
WHERE report_name = 'Dropbox Report';
