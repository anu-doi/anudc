/**
 * 20120913_alter_fedora_object.sql
 * 
 * Australian National University Data Commons
 * 
 * This script adds the template to the fedora object.
 * 
 * Version	Date		Developer				Description
 * 0.1		13/09/2012	Genevieve Turner (GT)	Initial
 */

ALTER TABLE fedora_object
ADD tmplt_id text;