drop table if exists report_auto_param;
drop table if exists report_auto;

CREATE TABLE report_auto (
	id			bigserial	not null
	,report_id	bigint		not null
	,email		varchar(255)
	,cron		text		not null
	,PRIMARY KEY (id)
	,CONSTRAINT fk_report_auto_param_1 FOREIGN KEY (report_id) REFERENCES report (id)
);

CREATE TABLE report_auto_param (
	id			bigint	not null
	,seq_num	int		not null
	,param		text	not null
	,param_val	text	not null
	,PRIMARY KEY (id, seq_num)
	,CONSTRAINT fk_report_auto_param_1 FOREIGN KEY (id) REFERENCES report_auto (id)
);