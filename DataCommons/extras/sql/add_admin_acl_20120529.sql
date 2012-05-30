INSERT INTO acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
SELECT acl_object_identity.id
	, 1
	, acl_sid.id
	, 16
	, '1'
	, '1'
	, '1'
FROM acl_object_identity, acl_sid
WHERE acl_sid.sid = 'ROLE_ADMIN'
AND acl_object_identity.parent_object is null;
