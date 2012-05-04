/**
 * 1_create_database.sql
 * 
 * Australian National University Data Commons
 * 
 * Creates a database (note this must be run as a super user e.g. postgres)
 * 
 * Version	Date		Developer			Description
 * 0.1		13/03/2012	Genevieve Turner	Initial build
 */

CREATE USER dcuser WITH PASSWORD 'dcpassword';
CREATE DATABASE datacommonsdb WITH ENCODING='UTF8' OWNER dcuser;
