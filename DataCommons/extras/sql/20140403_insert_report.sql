INSERT INTO report (id, report_name, report_template, sub_report) 
VALUES (9, 'Record Report', 'report_tmplt.jasper', 'subRpt_created.jasper');

INSERT INTO report_param (id, seq_num, param_name, request_param)
VALUES (9, 1, 'param1', 'groupId');
