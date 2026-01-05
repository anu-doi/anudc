insert into select_code (select_name, code, description, deprecated) values 
	('collectionCollectionType', 'isPartOf', 'Is Part Of', false)
	,('collectionCollectionType', 'hasPart', 'Has Part', false)
	,('collectionCollectionType', 'hasAssociationWith', 'Has Association With', false)
	,('collectionCollectionType', 'describes', 'Describes', false)
	,('collectionCollectionType', 'isDescribedBy', 'Is Described By', false)
	,('collectionCollectionType', 'isLocatedIn', 'Is Located In', false)
	,('collectionCollectionType', 'isLocationFor', 'Is Location For', false)
	,('collectionCollectionType', 'isDerivedFrom', 'Is Derived From', false)
	,('collectionCollectionType', 'hasDerivedCollection', 'Has Derived Collection', false);

insert into select_code (select_name, code, description, deprecated) values 
	('collectionIdType', 'ark', 'ARK Persistent Identifier Scheme', false)
	,('collectionIdType', 'doi', 'Digital Object Identifier', false)
	,('collectionIdType', 'handle', 'Handle System Identifier', false)
	,('collectionIdType', 'infouri', '''info'' URI scheme', false)
	,('collectionIdType', 'isil', 'International Standard Identifier for Libraries', false)
	,('collectionIdType', 'local', 'identifer unique within a local context', false)
	,('collectionIdType', 'purl', 'Persistent Uniform Resource Locator', false)
	,('collectionIdType', 'uri', 'Uniform Resource Identifier', false)
	,('collectionIdType', 'urn', 'Uniform Resource Name', false);

insert into template_attribute (id, template_id, name, field_type_id, label, tooltip, multivalued, required, select_code, max_length, template_tab_id, form_order, display_order, hidden, extra) values
	(244,1,'externalCollection',5,'Related external collections','If appropriate, include identifiers for related collections and datasets',false,false,null,null,2, 48, 48, null, null);

insert into template_attribute_column (template_attribute_id, name, label, field_type_id, select_code, column_order) values
	(244,'collectionIdType', 'Identifier type', 3, 'collectionIdType', 1)
	,(244,'collectionIdValue', 'Identifier value', 1, null, 2)
	,(244,'collectionTitle', 'Title/Name', 1, null, 3)
	,(244,'collectionRelationType', 'Relationship type', 3, 'collectionCollectionType', 4);
	
insert into select_code (select_name, code, description, deprecated) values 
	('collectionActivityType', 'hasAssociationWith', 'Has Association With', false)
	,('collectionActivityType', 'isOutputOf', 'Is Output Of', false);

insert into select_code (select_name, code, description, deprecated) values 
	('activityIdType', 'arc', 'Australian Research Council identifier', false)
	,('activityIdType', 'local', 'identifer unique within a local context', false)
	,('activityIdType', 'nhmrc', 'National Health and Medical Research Council identifier', false)
	,('activityIdType', 'raid', 'Research Activity Identifier', false)
	,('activityIdType', 'handle', 'Handle System Identifier', false)
	,('activityIdType', 'purl', 'Persistent Uniform Resource Locator', false)
	,('activityIdType', 'uri', 'Uniform Resource Identifier', false)
	,('activityIdType', 'urn', 'Uniform Resource Name', false);

insert into template_attribute (id, template_id, name, field_type_id, label, tooltip, multivalued, required, select_code, max_length, template_tab_id, form_order, display_order, hidden, extra) values
(245,1,'externalActivity',5,'Related external grants and projects','If appropriate, include identifiers for related external grants and projects e.g. ARC and NHMRC identifiers',false,false,null,null,2, 49, 49, null, null);

insert into template_attribute_column (template_attribute_id, name, label, field_type_id, select_code, column_order) values
	(245,'activityIdType', 'Identifier type', 3, 'activityIdType', 1)
	,(245,'activityIdValue', 'Identifier value', 1, null, 2)
	,(245,'activityTitle', 'Title/Name', 1, null, 3)
	,(245,'activityRelationType', 'Relationship type', 3, 'collectionActivityType', 4)

-- Make the person type available and mandatory
insert into select_code (select_name, code, description, deprecated) values 
	('personSubType', 'person', 'Person', false)

update template_attribute
set field_type_id = 4
	, required = '1'
	, select_code = 'personSubType'
where template_id in (5,6)
and name = 'subType';

update template_attribute
set required = '1'
where template_id = 5
and name in ('ownerGroup','lastName');
