/**
 * 20120625_create_select_code_table.sql
 * 
 * Australian National University Data Commons
 * 
 * This script is to perform updates to the users table in which the id column is added
 * and used as the primary key
 * 
 * 
 * Version	Date		Developer				Description
 * 0.1		25/06/2012	Genevieve Turner (GT)	Initial
 */

alter table collection_requests
add column object_fk bigint;

alter table collection_requests add constraint fk_request_object FOREIGN KEY (object_fk) REFERENCES fedora_object (id);

update collection_requests
set object_fk = fedora_object.id
from fedora_object
where fedora_object.pid = collection_requests.pid;

