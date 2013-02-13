/**
 * Updates table fedora_object by adding a new column is_files_public with boolean datatype and NOT NULL constraint after existing values have been set to false.
 */
ALTER TABLE fedora_object
	ADD COLUMN is_files_public boolean;

UPDATE fedora_object
	SET is_files_public='false';

ALTER TABLE fedora_object
	ALTER COLUMN is_files_public SET NOT NULL;
	