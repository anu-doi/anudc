/**
 * insert_for_pambu_20120529.sql
 * 
 * Australian National University Data Commons
 * 
 * This script adds values for PAMBU to the database
 * 
 * Version	Date		Developer				Description
 * 0.1		08/06/2012	Genevieve Turner (GT)	Initial
 */

INSERT INTO domains (domain_name) values ('Pacific Manuscripts Bureau');
INSERT INTO groups (group_name) values ('Pacific Manuscripts Bureau');

INSERT INTO acl_object_identity (object_id_class, object_id_identity, owner_sid, entries_inheriting)
SELECT 1
	, domains.id
	, acl_sid.id
	, '1'
FROM domains, acl_sid
WHERE acl_sid.sid = 'ROLE_ADMIN'
AND domains.domain_name = 'Pacific Manuscripts Bureau';


-- Note this doesn't work, will need to update acl_object_identity for group manually at this point in time

/*
INSERT INTO acl_object_identity (object_id_class, object_id_identity, owner_sid, parent_object, entries_inheriting)
SELECT 2
	, groups.id
	, acl_sid.id
	, aoi2.id
	, '1'
FROM groups, acl_sid, acl_object_identity aoi2, domains
WHERE acl_sid.sid = 'ROLE_ADMIN'
AND groups.group_name = 'Pacific Manuscripts Bureau'
AND aoi2.object_id_identity = domains.id
AND domains.domain_name = 'Pacific Manuscripts Bureau';
*/
UPDATE publish_location
SET execute_class = 'au.edu.anu.datacommons.publish.GenericPublish'
WHERE code = 'ANU';

UPDATE publish_location
SET requires = null;

INSERT INTO publish_location (code, name, execute_class, requires) VALUES
('PAMBU', 'Pacific Manuscripts Bureau','au.edu.anu.datacommons.publish.GenericPublish',1);