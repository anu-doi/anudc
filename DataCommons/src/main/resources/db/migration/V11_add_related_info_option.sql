insert into select_code (select_name, code, description, deprecated) values 
	('collectionPartyType', 'isManagedBy', 'Is Managed By', false)
	,('collectionPartyType', 'isOwnedBy', 'Is Owned By', false)
	,('collectionPartyType', 'hasAssociationWith', 'Has Association With', false)
	,('collectionPartyType', 'hasCollector', 'Has Collector', false)
	,('collectionPartyType', 'isEnrichedBy', 'Is Enriched By', false)
	,('collectionPartyType', 'hasPrincipalInvestigator', 'Has Principal Investigator', false);

insert into select_code (select_name, code, description, deprecated) values 
	('partyIdType', 'abn', 'Australian Business Number', false)
	,('partyIdType', 'AU-ANL:PEAU', 'National Library of Australia identifier', false)
	,('partyIdType', 'fundref', 'Open Funder Registry', false)
	,('partyIdType', 'orcid', 'Open Researcher and Contributor Identifier', false)
	,('partyIdType', 'researcherID', 'Web of Science ResearcherID', false)
	,('partyIdType', 'ror', 'Research Organization Registry identifier', false)
	,('partyIdType', 'handle', 'Handle System Identifier', false)
	,('partyIdType', 'purl', 'Persistent Uniform Resource Locator', false)
	,('partyIdType', 'uri', 'Uniform Resource Identifier', false)
	,('partyIdType', 'urn', 'Uniform Resource Name', false);

insert into template_attribute (id, template_id, name, field_type_id, label, tooltip, multivalued, required, select_code, max_length, template_tab_id, form_order, display_order, hidden, extra) values
(243,1,'externalParty',5,'Related external people and organisations','If appropriate, include identifiers for related external people and organisations ie. ROR',false,false,null,null,2, 47, 47, null, null);

insert into template_attribute_column (template_attribute_id, name, label, field_type_id, select_code, column_order) values
	(243,'partyIdType', 'Identifier type', 3, 'partyIdType', 1)
	,(243,'partyIdValue', 'Identifier value', 1, null, 2)
	,(243,'partyTitle', 'Title/Name', 1, null, 3)
	,(243,'partyRelationType', 'Relationship type', 3, 'collectionPartyType', 4);
