/**
 * 3_add_data.sql
 * 
 * Australian National University Data Commons
 * 
 * Inserts a few default records in to the database.
 * 
 * Version	Date		Developer			Description
 * 0.1		13/03/2012	Genevieve Turner	Initial build
 */

INSERT INTO access_user VALUES (1, 'u5125986');
INSERT INTO access_user VALUES (2, 'u4465201');
INSERT INTO access_group VALUES (1, 'admin', 1);
INSERT INTO user_group VALUES (1,1);
INSERT INTO user_group VALUES (2,1);