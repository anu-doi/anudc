ALTER TABLE report_auto ADD COLUMN format text;

UPDATE report_auto SET format = 'pdf';
