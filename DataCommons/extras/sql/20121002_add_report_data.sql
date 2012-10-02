/**
 * 20121002_add_report_data.sql
 * 
 * Australian National University Data Commons
 * 
 * This script adds name to the report parameters
 * 
 * Version	Date		Developer				Description
 * 0.1		02/10/2012	Genevieve Turner (GT)	Initial
 */

INSERT INTO report_param (id, seq_num, param_name, request_param)
SELECT	report.id as id
	, 2 as seq_num
	, 'name' as param_name
	, 'No Name'
FROM report
WHERE report_name = 'All Report';

INSERT INTO report_param (id, seq_num, param_name, request_param)
SELECT	report.id as id
	, 3 as seq_num
	, 'name' as param_name
	, 'No Name'
FROM report
WHERE report_name = 'Modified Report';

INSERT INTO report_param (id, seq_num, param_name, request_param)
SELECT	report.id as id
	, 3 as seq_num
	, 'name' as param_name
	, 'No Name'
FROM report
WHERE report_name = 'Publish Report';

INSERT INTO report_param (id, seq_num, param_name, request_param)
SELECT	report.id as id
	, 2 as seq_num
	, 'name' as param_name
	, 'No Name'
FROM report
WHERE report_name = 'Logs Report';

INSERT INTO report_param (id, seq_num, param_name, request_param)
SELECT	report.id as id
	, 2 as seq_num
	, 'name' as param_name
	, 'No Name'
FROM report
WHERE report_name = 'Dropbox Report';