/**
 * 20120918_create_related.sql
 * 
 * Australian National University Data Commons
 * 
 * This script adds tables to show the link types
 * 
 * Version	Date		Developer				Description
 * 0.1		18/09/2012	Genevieve Turner (GT)	Initial
 */

DROP TABLE IF EXISTS link_relation;
DROP TABLE IF EXISTS link_type;

CREATE TABLE link_type (
	id				bigserial	not null
	,code			varchar(60)	not null
	,description	text		not null
	,PRIMARY KEY (id)
);

CREATE TABLE link_relation (
	category1		varchar(30)	not null
	,category2		varchar(30)	not null
	,link_type_id	bigint		not null
	,PRIMARY KEY (category1, category2, link_type_id)
	,CONSTRAINT fk_link_relation_1 FOREIGN KEY (link_type_id) REFERENCES link_type (id)
);

INSERT INTO link_type (code, description)
VALUES ('isPartOf','Is Part Of');
INSERT INTO link_type (code, description)
VALUES ('hasPart','Has Part');
INSERT INTO link_type (code, description)
VALUES ('hasOutput','Has Output');
INSERT INTO link_type (code, description)
VALUES ('isManagedBy','Is Managed By');
INSERT INTO link_type (code, description)
VALUES ('isOwnedBy','Is Owned By');
INSERT INTO link_type (code, description)
VALUES ('hasParticipant','Has Participant');
INSERT INTO link_type (code, description)
VALUES ('hasAssociationWith','Has Association With');
INSERT INTO link_type (code, description)
VALUES ('describes','Describes');
INSERT INTO link_type (code, description)
VALUES ('isDescribedBy','Is Described By');
INSERT INTO link_type (code, description)
VALUES ('isLocatedIn','Is Located In');
INSERT INTO link_type (code, description)
VALUES ('isLocationFor','Is Location For');
INSERT INTO link_type (code, description)
VALUES ('isDerivedFrom','Is Derived From');
INSERT INTO link_type (code, description)
VALUES ('hasDerivedCollection','Has Derived Collection');
INSERT INTO link_type (code, description)
VALUES ('hasCollector','Has Collector');
INSERT INTO link_type (code, description)
VALUES ('isEnrichedBy','Is Enriched By');
INSERT INTO link_type (code, description)
VALUES ('isOutputOf','Is Output Of');
INSERT INTO link_type (code, description)
VALUES ('supports','Supports');
INSERT INTO link_type (code, description)
VALUES ('isAvailableThrough','Is Available Through');
INSERT INTO link_type (code, description)
VALUES ('isProducedBy','Is Produced By');
INSERT INTO link_type (code, description)
VALUES ('isPresentedBy','Is Presented By');
INSERT INTO link_type (code, description)
VALUES ('isOperatedOnBy','Is Operated On By');
INSERT INTO link_type (code, description)
VALUES ('hasValueAddedBy','Has Value Added By');
INSERT INTO link_type (code, description)
VALUES ('hasMember','Has Member');
INSERT INTO link_type (code, description)
VALUES ('isMemberOf','Is Member Of');
INSERT INTO link_type (code, description)
VALUES ('isFundedBy','Is Funded By');
INSERT INTO link_type (code, description)
VALUES ('isFunderOf','Is Funder Of');
INSERT INTO link_type (code, description)
VALUES ('enriches','Enriches');
INSERT INTO link_type (code, description)
VALUES ('isCollectorOf','Is Collector Of');
INSERT INTO link_type (code, description)
VALUES ('isParticipantIn','Is Participant In');
INSERT INTO link_type (code, description)
VALUES ('isManagerOf','Is Manager Of');
INSERT INTO link_type (code, description)
VALUES ('isOwnerOf','Is Owner Of');
INSERT INTO link_type (code, description)
VALUES ('isSupportedBy','Is Supported By');
INSERT INTO link_type (code, description)
VALUES ('makesAvailable','Makes Available');
INSERT INTO link_type (code, description)
VALUES ('produces','Produces');
INSERT INTO link_type (code, description)
VALUES ('presents','Presents');
INSERT INTO link_type (code, description)
VALUES ('operatesOn','Operates On');
INSERT INTO link_type (code, description)
VALUES ('addsValueTo','Adds Value To');

-- Activity

INSERT INTO link_relation
SELECT 'Activity' as category1
	,'Activity' as category2
	,link_type.id as link_type_id
FROM link_type
WHERE link_type.code IN ('hasAssociationWith','hasPart','isPartOf','isFundedBy');

INSERT INTO link_relation (category1, category2, link_type_id)
SELECT 'Activity' as category1
	,'Collection' as category2
	,link_type.id as link_type_id
FROM link_type
WHERE link_type.code IN ('hasAssociationWith','hasOutput');

INSERT INTO link_relation (category1, category2, link_type_id)
SELECT 'Activity' as category1
	,'Party' as category2
	,link_type.id as link_type_id
FROM link_type
WHERE link_type.code IN ('hasAssociationWith','isFundedBy','isManagedBy','isOwnedBy','hasParticipant');

INSERT INTO link_relation (category1, category2, link_type_id)
SELECT 'Activity' as category1
	,'Service' as category2
	,link_type.id as link_type_id
FROM link_type
WHERE link_type.code IN ('hasAssociationWith');

-- Collection

INSERT INTO link_relation (category1, category2, link_type_id)
SELECT 'Collection' as category1
	,'Activity' as category2
	,link_type.id as link_type_id
FROM link_type
WHERE link_type.code IN ('hasAssociationWith','isOutputOf');

INSERT INTO link_relation (category1, category2, link_type_id)
SELECT 'Collection' as category1
	,'Collection' as category2
	,link_type.id as link_type_id
FROM link_type
WHERE link_type.code IN ('hasAssociationWith','describes','hasPart','hasAssociationWith',
	'isDescribedBy','isLocatedIn','isLocationFor','isPartOf','isDerivedFrom','hasDerivedCollection');

INSERT INTO link_relation (category1, category2, link_type_id)
SELECT 'Collection' as category1
	,'Party' as category2
	,link_type.id as link_type_id
FROM link_type
WHERE link_type.code IN ('hasAssociationWith','hasCollector','isManagedBy','isOwnedBy','isEnrichedBy');

INSERT INTO link_relation (category1, category2, link_type_id)
SELECT 'Collection' as category1
	,'Service' as category2
	,link_type.id as link_type_id
FROM link_type
WHERE link_type.code IN ('hasAssociationWith','supports','isAvailableThrough','isProducedBy',
	'isPresentedBy','isOperatedOnBy','hasValueAddedBy');

-- Party

INSERT INTO link_relation (category1, category2, link_type_id)
SELECT 'Party' as category1
	,'Activity' as category2
	,link_type.id as link_type_id
FROM link_type
WHERE link_type.code IN ('hasAssociationWith','isParticipantIn','isFundedBy','isFunderOf',
	'isManagedBy','isManagerOf','isOwnedBy','isOwnerOf');

INSERT INTO link_relation (category1, category2, link_type_id)
SELECT 'Party' as category1
	,'Collection' as category2
	,link_type.id as link_type_id
FROM link_type
WHERE link_type.code IN ('hasAssociationWith','enriches','isCollectorOf',
	'isManagedBy','isManagerOf','isOwnedBy','isOwnerOf');

INSERT INTO link_relation (category1, category2, link_type_id)
SELECT 'Party' as category1
	,'Party' as category2
	,link_type.id as link_type_id
FROM link_type
WHERE link_type.code IN ('hasAssociationWith','hasMember','hasPart','isPartOf','isMemberOf','isFundedBy',
	'isFunderOf','isManagedBy','isManagerOf','isOwnedBy','isOwnerOf');

INSERT INTO link_relation (category1, category2, link_type_id)
SELECT 'Party' as category1
	,'Service' as category2
	,link_type.id as link_type_id
FROM link_type
WHERE link_type.code IN ('hasAssociationWith','isManagedBy','isManagerOf','isOwnedBy','isOwnerOf');

-- Service

INSERT INTO link_relation (category1, category2, link_type_id)
SELECT 'Service' as category1
	,'Activity' as category2
	,link_type.id as link_type_id
FROM link_type
WHERE link_type.code IN ('hasAssociationWith');

INSERT INTO link_relation (category1, category2, link_type_id)
SELECT 'Service' as category1
	,'Collection' as category2
	,link_type.id as link_type_id
FROM link_type
WHERE link_type.code IN ('hasAssociationWith','isSupportedBy','makesAvailable','produces','presents',
	'operatesOn','addsValueTo');

INSERT INTO link_relation (category1, category2, link_type_id)
SELECT 'Service' as category1
	,'Party' as category2
	,link_type.id as link_type_id
FROM link_type
WHERE link_type.code IN ('hasAssociationWith','isManagedBy','isOwnedBy');

INSERT INTO link_relation (category1, category2, link_type_id)
SELECT 'Service' as category1
	,'Service' as category2
	,link_type.id as link_type_id
FROM link_type
WHERE link_type.code IN ('hasAssociationWith','hasPart','isPartOf');
