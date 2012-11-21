/**
 * 20121120_create_iris_publish.sql
 * 
 * Australian National University Data Commons
 * 
 * Creates table 'publish_iris' and inserts a row for into 'publish_location' for IRIS.
 */

INSERT INTO publish_location (code, name, execute_class)
VALUES ('IRIS', 'Incorporated Research Institutions for Seismology', 'au.edu.anu.datacommons.publish.IRISPublish');

CREATE TABLE publish_iris (
	pid				varchar(64)	not null
	,publish_date	timestamp	not null
	,status			varchar(20)	not null
	,iris_network	varchar(20)
	,PRIMARY KEY (pid, publish_date)
);
