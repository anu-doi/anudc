/**
 * 20121109_update_audit_tables.sql
 * 
 * Australian National University Data Commons
 * 
 * This script adds tables to show the link types
 * 
 * Version	Date		Developer				Description
 * 0.1		09/11/2012	Genevieve Turner (GT)	Initial
 */

alter table audit_object
add column rid bigint;

alter table audit_access
add column rid bigint;