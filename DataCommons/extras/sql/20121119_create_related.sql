/**
 * 20121119_create_related.sql
 * 
 * Australian National University Data Commons
 * 
 * Creates table 'link_external_pattern' which associates object types with patterns of external links.
 */

-- drop table link_external_pattern;

CREATE TABLE link_external_pattern (
	id				bigserial		not null
	, object_type	varchar(30)		not null
	, pattern		varchar(255)	not null
	, description	varchar(255)	not null
	, PRIMARY KEY (id)
	, UNIQUE (object_type, pattern)
);

INSERT INTO link_external_pattern (object_type, pattern, description)
VALUES ('party', 'http://nla.gov.au/nla.party', 'NLA Party Identifier');

INSERT INTO link_external_pattern (object_type, pattern, description)
VALUES ('activity', 'http://purl.org/au-research/grants/arc/', 'ARC Grant');
