insert into template (id, template_pid, name, description) 
select 8, 'tmplt:8', 'LTERN Collection Template', 'LTERN Collection template for recording Datasets'
where not exists (select 1 from template where id = 8);


create table entity_type (
	id	bigserial
	, name	text
	, primary key (id)
);

insert into entity_type (name) values 
	('collection')
	,('activity')
	,('service')
	,('party');


alter table template add column entity_type_id bigint;
alter table template add foreign key (entity_type_id) references entity_type (id);

update template
set entity_type_id = entity_type.id
from entity_type
where lower(template.name) like '%collection%'
and entity_type.name = 'collection';

update template
set entity_type_id = entity_type.id
from entity_type
where lower(template.name) like '%activity%'
and entity_type.name = 'activity';

update template
set entity_type_id = entity_type.id
from entity_type
where lower(template.name) like '%service%'
and entity_type.name = 'service';

update template
set entity_type_id = entity_type.id
from entity_type
where lower(template.name) like '%party%'
and entity_type.name = 'party';

update template
set entity_type_id = entity_type.id
from entity_type
where lower(template.name) like '%person%'
and entity_type.name = 'party';

update template
set entity_type_id = entity_type.id
from entity_type
where lower(template.name) like '%author%'
and entity_type.name = 'party';

create table field_type (
	id	bigserial
	, name	text
	, primary key (id)
);

insert into field_type (id, name) values 
	(1, 'TextField')
	,(2, 'TextArea')
	,(3, 'ComboBox')
	,(4, 'RadioButton')
	,(5, 'Table')
	,(6, 'Group')
	,(7, 'Date')
	;
	
select setval('field_type_id_seq', (SELECT MAX(id) FROM field_type));

insert into select_code (select_name, code, description) values 
	('collection_sub_type','dataset','Dataset')
	,('collection_sub_type','catalogueOrIndex','Catalogue or Index')
	,('collection_sub_type','collection','Collection')
	,('collection_sub_type','registry','Registry')
	,('collection_sub_type','repository','Repository')
	,('collection_sub_type','classificationScheme','Classification Scheme')
	,('collection_sub_type','software','Software')
	,('language','en','English')
	,('language','zh','Chinese')
	,('accessRightsType','open','Open')
	,('accessRightsType','conditional','Conditional')
	,('accessRightsType','restricted','Restricted')
	,('identifierType','ark','ARK Persistent Identifier Scheme')
	,('identifierType','doi','Digital Object Identifier')
	,('identifierType','ean13','International Article Number')
	,('identifierType','eissn','Electronic International Standard Serial Number')
	,('identifierType','handle','HANDLE System Identifier')
	,('identifierType','infouri','''info'' URI scheme')
	,('identifierType','local','identifier unique within a local context')
	,('identifierType','purl','Persistent Uniform Resource Locator')
	,('identifierType','uri','Uniform Resource Identifier')
	,('identifierType','issn','International Standard Serial Number')
	,('identifierType','isbn','International Standard Book Number')
	,('identifierType','istc','International Standard Text Code http://www.istc-international.org/html')
	,('identifierType','upc','Universal Product Code')
	,('identifierType','urn','Uniform Resource Name')
	,('coordinate_type','text','Text')
	,('coordinate_type','gmlKmlPolyCoords','KML long/lat co-ordinates from GML defining a polygon')
	,('coordinate_type','gpx','the GPS Exchange Format')
	,('coordinate_type','iso31661','ISO 3166-1 Country Codes')
	,('coordinate_type','iso31662','ISO 3166-2 Country Subdivision Codes')
	,('coordinate_type','iso31663','ISO 3166-3 Deleted Country Codes')
	,('coordinate_type','iso19139dcmiBox','DCMI Box notation conformant with iso 19139')
	,('coordinate_type','kmlPolyCoords','Keyhole Markup Language co-ordinates defining a polygon')
	,('coordinate_type','dcmiPoint','Spatial Location Information in DCMI Point Notation')
	,('anztoaSubject','ed','Experimental development')
	,('anztoaSubject','ar','Applied Research')
	,('anztoaSubject','sbr','Strategic basic research')
	,('anztoaSubject','pbr','Pure basic research')
	,('dataMgmtPlan','yes','Yes')
	,('dataMgmtPlan','no','No')
	,('activity_sub_type','project','Project')
	,('activity_sub_type','program','Program')
	,('activity_sub_type','course','Course')
	,('activity_sub_type','award','Award')
	,('activity_sub_type','event','Event')
	,('activity_sub_type','grant','Grant')
	,('service_sub_type','create','Create')
	,('service_sub_type','generate','Generate')
	,('service_sub_type','annotate','Annotate')
	,('service_sub_type','report','Report')
	,('service_sub_type','transform','Transform')
	,('service_sub_type','assemble','Assemble')
	,('service_sub_type','harvest-oaipmh','OAI-PMH Harvest')
	,('service_sub_type','search-http','Search service over HTTP')
	,('service_sub_type','search-opensearch','OpenSearch search')
	,('service_sub_type','search-sru','SRU Search')
	,('service_sub_type','search-srw','SRW Search')
	,('service_sub_type','search-z3950','z39.50 search')
	,('service_sub_type','syndicate-atom','ATOM syndication')
	,('service_sub_type','syndicate-rss','RSS feed')
	,('service_sub_type','store','Store')
	,('group_sub_type','group','Group')
	,('group_sub_type','administrativePosition','Administrative Position')
	,('taxonomyType','Species','Species')
	,('delivery_method','offline', 'Offline')
	,('delivery_method','software', 'Software')
	,('delivery_method','webservice', 'Web Service')
	,('delivery_method','workflow', 'Workflow')
	;

create table template_attribute (
	id	bigserial
	, template_id	bigint
	, name	text
	, field_type_id	bigint
	, label	text
	, tooltip	text
	, multivalued	boolean
	, required	boolean
	, select_code	text
	, max_length	bigint
	, template_tab_id	bigint
	, form_order	int
	, display_order	int
	, hidden	boolean
	, extra	text
	, primary key (id)
	, constraint fk_template_attribute_1 foreign key (field_type_id) references field_type (id)
);

create table template_attribute_column (
	id	bigserial
	, template_attribute_id	bigint
	, name	text
	, label	text
	, field_type_id	bigint
	, select_code	text
	, extra	text
	, column_order	int
	, primary key (id)
	, constraint fk_template_attribute_column_1 foreign key (template_attribute_id) references template_attribute (id)
);

create table template_tab (
	id	bigserial
	, template_id	bigint
	, name	text
	, label	text
	, tab_order	int
	, tooltip	text
	, primary key (id)
	, constraint fk_template_tab_column_1 foreign key (template_id) references template (id)
);

-- insert collection attributes
insert into template_tab (id, template_id, name, label, tab_order, tooltip) values
	(1, 1, 'general', 'General', 1, null)
	, (2, 1, 'description', 'Description', 3, null)
	, (3, 1, 'coverage', 'Coverage', 2, 'Dates and Locations relating to collection')
	, (4, 1, 'people','People',4,'Information about contact(s)')
	, (5, 1, 'subject', 'Subject', 5, null)
	, (6, 1, 'rights', 'Rights', 6, null)
	, (7, 1, 'management', 'Management', 7, null)
	;

insert into template_attribute (id, template_id, name, label, field_type_id, select_code, multivalued, required, template_tab_id, form_order, display_order, max_length, extra,tooltip) values
	(1,1,'type','Type','1',null,'0','0','1',1,1,null,null,null)
	,(2,1,'name','Title','1',null,'0','1',1,2,2,'255',null,'This is the title of the data collection. It needs to be unique, i.e. do not use a title that is identical to an existing publication that is related to and/or underpinned by the data.<br/><br/>Titles that are descriptive of the actual data are best. Try to include key distinctive characteristics that would provide information for potential users to determine if the data might be useful to them. These may include information specific to the entities studied, survey data, observations, images collected, location, time, and temporal or spatial coverage.<br/><br/>Examples:<br/>Net levels of greenhouse gas emission and sources in the New South Wales Hunter Valley, 1990 - 1998.')
	,(3,1,'abbrName','Brief Title','1',null,'0','0',1,3,3,'255',null,'Please include a brief title if your title is very long')
	,(4,1,'altName','Alternate Title','1',null,'0','0',1,4,4,'255',null,'If there is an alternative title for your data collection, please include it here.')
	,(5,1,'subType','Collection Type','4','collection_sub_type','0','1',1,5,5,null,null,'Catalogue or Index - a collection of descriptions describing the content of one or more collective works at the item level.<br/>Collection - compiled content created as separate and independent works and assembled into a collective whole<br/>Registry - a collection of registry objects compiled to support the business of a given community<br/>Repository - a collection of physical or digital objects compiled for information and documentation purposes and/or for storage and safekeeping<br/>Dataset - a collection of physical or digital objects generated by research activities.<br/>Classification Scheme - A list or arrangement of terms used in a particular context e.g. ontologies, thesauri<br/>Software - One or more items that represent a software product')
	,(6,1,'ownerGroup','Access Privileges','6',null,'0','1',1,6,6,null,null,'Please choose the access privilege group for the collection')
	,(7,1,'doi','DOI - Digital Object Identifier','1',null,'0','0',1,7,7,null,null,'The Digital Object Identifier for this record.Please note that if this does not already exist a DOI will be created when the record is published')
	,(8,1,'websiteAddress','Website Address','1',null,'1','0',1,8,8,null,null,'Websites at which the collections data is held')
	,(9,1,'metaLang','Metadata Language','3','language','0','0',1,9,9,null,null,'Please select the language that you are using to describe this data')
	,(10,1,'dataLang','Data Language','3','language','0','0',1,10,10,null,null,'Please select the language the data is in')
	,(11,1,'significanceStatement','Significance Statement','2',null,'0','0',2,11,11,null,null,'What is significant about this dataset. For example:<br/><br/>The first Australian full talking film.')
	,(12,1,'briefDesc','Brief Description','2',null,'0','0',2,12,12,null,null,'A brief summary about the data')
	,(13,1,'fullDesc','Full Description','2',null,'0','1',2,13,13,null,null,'The description should be as rich as possible. If applicable, include: the scope; details of what is being studied or recorded; methodologies used, information about any instruments that were used to produce and/or collect the data; relevant standards used; conditions under which the study or research occured or the data was collected, etc<br/>If there were any problems or other issues with methods used to produce and/or collect the data, please include those as well.')
	,(14,1,'email','Contact Email','1',null,'1','1',4,14,14,null,'email',null)
	,(15,1,'postalAddress','Contact Address','2',null,'0','0',4,15,15,null,null,null)
	,(16,1,'phone','Contact Phone Number','1',null,'1','0',4,16,16,null,null,null)
	,(17,1,'fax','Contact Fax Number','1',null,'1','0',4,17,17,null,null,null)
	,(18,1,'principalInvestigator','Principal Investigator','1',null,'0','0',4,18,18,null,null,'Please record the name of the principal investigator')
	,(19,1,'supervisor','Supervisors','1',null,'1','0',4,19,19,null,null,'Please record the name of the person identified as supervisor in relation to the creation of the data. (Note that this may be  the same as the Primary Contact.)')
	,(20,1,'collaborator','Collaborators','1',null,'1','0',4,20,20,null,null,'Please record the names of any other collaborators in the research project which created the data.')
	,(21,1,'anzforSubject','Fields of Research','3','anzforSubject','1','1',5,21,21,null,null,'Include Fields of Research terms and corresponding classification codes to be associated with the person being described. The Fields of Research Classfication can be found at the following URL.<br/><br/><a href="https://www.abs.gov.au/Ausstats/abs@.nsf/Latestproducts/6BB427AB9696C225CA2574180004463E?opendocument" class="text-link">https://www.abs.gov.au/Ausstats/abs@.nsf/Latestproducts/6BB427AB9696C225CA2574180004463E?opendocument</a>')
	,(22,1,'anzseoSubject','Socio-Economic Objective','3','anzseoSubject','1','0',5,22,22,null,null,'Include terms and corresponding classification codes to be associated with the resource being described. Information on the Socio-Economic Objectives Classification Codes can be found at the following URL.<br/><br/><a href="https://www.abs.gov.au/Ausstats/abs@.nsf/Latestproducts/CF7ADB06FA2DFD69CA2574180004CB82?opendocument" class="text-link">https://www.abs.gov.au/Ausstats/abs@.nsf/Latestproducts/CF7ADB06FA2DFD69CA2574180004CB82?opendocument</a>')
	,(23,1,'locSubject','Keywords','1',null,'1','0',5,23,23,null,null,'Record keywords describing the activity. 5-10 keywords will normally be sufficient. The keywords must be specific enough for researchers with similar interests to find your activity.<br/><br/>If you need assistance in selecting keywords, there are many thesauri that may be helpful. Please use one appropriate to your discipline. If you are unable to identify a discipline specific classification scheme, you may wish to use the Library of Congress Classification scheme available at <a href="http://www.loc.gov/catdir/cpso/lcco/" class="text-link">http://www.loc.gov/catdir/cpso/lcco/</a>')
	,(24,1,'anztoaSubject','Type of Research Activity','4','anztoaSubject','0','0',5,24,24,null,null,'If appropriate, assign a research activity classficiation. There are four set values that can be assigned. Please choose one.')
	,(25,1,'coverageDates','Date Coverage','5',null,'0','0',3,25,25,null,null,'If appropriate, please indicate a date range relevant to the content of the material described.<br/>1990 - 2005<br/>2011-05-29 - 2011-12-31<br/>2012-06-21T11:49:53+10:00 - 2012-07-02T15:21+10:00')
	,(26,1,'coverageDateText','Time Period','1',null,'1','0',3,26,26,null,null,'If appropriate, please describe the time period during which the data was collected, observations were made, images were created or the time period that the resource is linked to intellectually or thematically. Examples include:<br/>18th Century<br/>World War II')
	,(27,1,'coverageArea','Geospatial Location','5',null,'0','0',3,27,27,null,null,'If appropriate, please include geospatial location information relevant to the research dataset/collection. This information may describe a geographical area where the data was collected, a place which is the subject of the collection or a location which is the focus of an activity e.g. co-ordinates or place name.')
	,(28,1,'createdDate','Date of data creation','1',null,'0','0',2,28,28,null,'date','The date/year the data was created.  It may be the date the information was created before an embargo period. For example:<br/><br/>1987<br/>2005-06<br/>2009-01-24')
	,(29,1,'citationYear','Year of data publication','1',null,'0','0',2,29,29,null,'date','The year the data was first published for citation purposes. If this field is not populated when publishing it will default to the year it is published in ANU Data Commons')
	,(30,1,'citCreator','Creator(s) for Citation','5',null,'0','0',2,30,30,null,null,'The creator(s) of the data for citation purposes. This field indicates who should be considered named in a citation.  Defaults to ''The Australian National University''. For example:<br/><br/>T Irino; R Tada<br/>Geofon operator<br/>Michael Denhard')
	,(31,1,'citationPublisher','Publisher for Citation','1',null,'0','0',2,31,31,null,null,'The publisher of the data.  Defaults to ''The Australian National University Data Commons''. For example:<br/><br/>Australian Data Archive<br/>Incorporated Research Institutions for Seismology')
	,(32,1,'publication','Publications','5',null,'0','0',2,32,32,null,null,'If appropriate, please include information about any publications that relate to the collection/dataset<br/>For example:<br/>Identifier Type: International Standard Serial Number<br/>Identifier Value: 0278-7393<br/>Publication Reference: Heathcote, A. (2003) Item recognition memory and the ROC. Journal of Experimental Psychology: Learning, Memory and Cognition, 29, 1210-1230.')
	,(33,1,'relatedWebsites','Related Websites','5',null,'0','0',2,33,33,null,null,'If appropriate, please include a URL and Notes for any websites that relate to the resource being described. For example:<br/><br/>URL: http://anusf.anu.edu.au/index.php<br/><br/>Title: ANU Supercomputing Facility')
	,(34,1,'externalId','Other Related Identifiers','1',null,'1','0',2,34,34,null,null,'Identifiers for systems external to ANU Data Commons')
	,(35,1,'accessRights','Access Rights','2',null,'0','0',6,35,35,null,null,'Enter a statement about access (or access conditions) to the resource. This will include access restrictions or embargoes based on privacy, security or other policies.<br/><br/>Examples:<br/>Contact Chief Investigator to negotiate access to the data<br/><br/>Embargoes until 1 year after publication of the research<br/><br/>Open Access allowed (see rights held in and over resource below)')
	,(36,1,'accessRightsType','Access Rights Type','3','accessRightsType','0','0',6,36,36,null,null,'Open - Data is publicly accessible online<br/><br/>Conditional - Data is publicly accessible to certain conditions. For example:<ul><li>a fee applies</li><li>the data is only accessible at a specific physical location</li></ul><br/>Restricted - Data access is limited. For example:<ul><li>following an embargo period</li><li>to a particular group of users</li><li>where formal permission is granted</li></ul>')
	,(37,1,'rightsStatement','Rights held in and over the data','2',null,'0','0',6,37,37,null,null,'Include information on copyright, licences and other intellectual property rights.<br/><br/>Examples:<br/>This dataset is made available under the Public Domain Dedication and Licence v1.0. Full text can be found at http://www.opendatacommons.org/licences/pddl/1.0/<br/><br/>Creative Commons Licence (CC BY or CC BY-SA or CC BY-ND or CC BY-NC or CC BY-NC-SA or CC BY-NC-ND) is assigned to this data. Details of the licence can be found at http://creativecommons.org.au/licences.<br/><br/>The (name) licence that controls this data is available at (URL)')
	,(38,1,'licenceType','Licence Type','3','licenceType','0','0',6,38,38,null,null,'Further information on the licence types can be found at http://www.ausgoal.gov.au/the-ausgoal-licence-suite<br/><br/><strong>Attribution</strong> - This licence lets others distribute, remix, tweak, and build upon your work, even commercially, as long as they credit you for the original creation. This is the most accomodating of licences offered. Recommended for maximum dissemination and use of licenced materials.<br/><strong>Attribution-shareAlike</strong> - This licence lets others remix, tweak and build upon your work even for commercial purposes, as long as they credit you and licence their new creations under identical terms. This licence is often compared to ''copyleft'' free and open source software licences. All new works based on yours will carry the same licence, so any derivatives will also allow commercial use. This is the licence used by Wikipedia, and is recommended for materials that would benefit from incorporating content from Wikipedia and similarly licenced projects.<br/><strong>Attribution-NoDerivatives</strong> - This licence allows for redistribution, commercial and non-commercial, as long as it is passed along unchanged and in whole, with credit to you.<br/><strong>Attribution-NonCommercial</strong> - This licence lets other remix, tweak and build upon your work non-commercially, and although their new works must also acknowledge you and be non-commercial, they don''t have to licence their derivative works on the same terms.<br/><strong>Attribution-NonCommercial-ShareAlike</strong> - This licence lets others remix, tweak, and build upon your work non-commercially, as long as they credit you and licence their new creations under the identical terms.<br/><strong>Attribution-NonCommercial-NoDerivatives</strong> - This licence is the most restrictive of AusGoal''s six main licences, only allowing others to download your works and share them with others as long as they credit you, but they can''t change them in any way or use them commercially.<br/><strong>General Public Licenc</strong> - The GNU General Public Licence is a free, copyleft licence for software and other kinds of works. Further information can be found at http://www.gnu.org/copyleft/gpl.html<br/><strong>AusGoal Restrictive Licence</strong> - This licence has been developed specifically for mateiral that may contain personal or other confidential information. It may also be used for other reasons, including material to be licenced under some form of limiting or restrictive condiation (e.g. a time limit on use, or payment arrangements others than an initial once-only fee).')
	,(39,1,'licence','Licence','2',null,'0','0',6,39,39,null,null,'Licence Information')
	,(40,1,'dataLocation','Data Location','2',null,'0','0',7,40,40,null,null,'Include where the data is located<br/><br/>Example:<br/>This data is located at (URL or physical location)')
	,(41,1,'embargoDate','Embargo Date','1',null,'0','0',7,41,41,null,'date','If this collection is under embargo, please provide the embargo lift date<br/>Example:<br/>2020-01-01')
	,(42,1,'dataRetention','Retention Period','1',null,'0','0',7,42,42,null,null,'Include the period of time that the data must be retained. This must be in line with institutional/funding body or legistlative retention<br/><br/>Examples:<br/>7 years<br/><br/>Until 31st December 2016<br/><br/>Indefinately')
	,(43,1,'disposalDate','Disposal Date','1',null,'0','0',7,43,43,null,null,'The identified disposal date for this data is (date)')
	,(44,1,'dataExtent','Extent or Quantity','1',null,'0','0',7,44,44,null,null,'Enter the number of files that comprise the research dataset/collection')
	,(45,1,'dataSize','Data Size','1',null,'0','0',7,45,45,null,null,'Record the file size of the data in KB, MB, GB, TB, etc. If you are unsure, please consult your IT support person.')
	,(46,1,'dataMgmtPlan','Data Management Plan','4','dataMgmtPlan','0','0',7,46,46,null,null,'Indicate if there is a data management plan for this data. Information on data management plans is found at http://libguides.anu.edu.au/datamanagement/')
	;
	
	
insert into template_attribute_column (template_attribute_id, name, label, field_type_id, select_code, column_order) values 
	(21,'dateFrom','Date From',7,null,1)
	,(21,'dateTo','Date To',7,null,2)
	,(25,'dateFrom','Date From',7,null,1)
	,(25,'dateTo','Date To',7,null,2)
	,(27,'covAreaType','Location Type',3,'coordinate_type',1)
	,(27,'covAreaValue','Location Value',1,null,2)
	,(30,'citCreatorGiven','Given Name',1,null,1)
	,(30,'citCreatorSurname','Surname',1,null,2)
	,(32,'pubType','Identifier Type',3,'identifierType',1)
	,(32,'pubValue','Identifier Value',1,null,2)
	,(32,'pubTitle','Publication Title',2,null,3)
	,(32,'pubNotes','Publication Reference',2,null,4)
	,(33,'relatedWebURL','URL',1,null,1)
	,(33,'relatedWebTitle','Title',1,null,2)
	;

-- insert activity attributes
insert into template_tab (id, template_id, name, label, tab_order, tooltip) values
	(8, 2, 'general', 'General', 1, null)
	, (9, 2, 'description', 'Description', 2, null)
	, (10, 2, 'contact','Contact',3,'Information about contact(s)')
	, (11, 2, 'subject', 'Subject', 4, null)
	, (12, 2, 'related', 'Related Information', 5, null)
	;
	
insert into template_attribute (id, template_id, name, label, field_type_id, select_code, multivalued, required, template_tab_id, form_order, display_order, max_length, extra,tooltip) values
	(47,2,'type','Type','1',null,'0','0',8,1,1,null,null,null)
	,(48,2,'subType','Activity Type','3','activity_sub_type','0','1',8,2,2,null,'required',null)
	,(49,2,'ownerGroup','Access Privileges','6',null,'0','1',8,3,3,null,'required','Please choose the access privilege group for the activity')
	,(50,2,'name','Title','1',null,'0','1',8,4,4,'80','required','Enter the name of the research project')
	,(51,2,'abbrName','Abbreviated Title','1',null,'0','0',8,5,5,'255',null,'Please include a brief title if your title is very long')
	,(52,2,'altName','Alternate Title','1',null,'0','0',8,6,6,'255',null,'If there is an alternative title for your activity, please include it here')
	,(53,2,'arcNumber','ARC Grant Number','1',null,'0','0',8,7,7,null,null,null)
	,(54,2,'fundingBody','Funding Body','1',null,'1','0',8,8,8,null,null,'Enter the name of the research funding body(ies) if applicable.')
	,(55,2,'existenceStart','Date activity began','1',null,'0','0',8,9,9,null,'date','Date/Year the project/program/course/award/event began')
	,(56,2,'existenceEnd','Date the activity ended','1',null,'0','0',8,10,10,null,'date','Date/Year the project/program/course/award/event ended')
	,(57,2,'briefDesc','Brief Description','2',null,'0','0',9,11,11,'4000',null,'A brief summary about the project')
	,(58,2,'fullDesc','Full Description','2',null,'0','0',9,12,12,'4000','needed','The description should be as rich as possible. If applicable, include: the scope; details of what is being studied or recorded; methodologies used, information about any instruments that were used to produce and/or collect the data; relevant standards used; conditions under which the study or research occured or the data was collected, etc<br/>If there were any problems or other issues with methods used to produce and/or collect the data, please include those as well.')
	,(59,2,'email','Contact Email','1',null,'1','0',10,13,13,null,'needed email',null)
	,(60,2,'postalAddress','Contact Address','2',null,'0','0',10,14,14,null,null,null)
	,(61,2,'phone','Contact Phone Number','1',null,'1','0',10,15,15,null,null,null)
	,(62,2,'fax','Contact Fax Number','1',null,'1','0',10,16,16,null,null,null)
	,(63,2,'websiteAddress','Website Address','1',null,'1','0',10,17,17,null,null,null)
	,(64,2,'anzforSubject','Fields of Research','3','anzforSubject','1','0',11,18,18,null,'needed','Include Fields of Research terms and corresponding classification codes to be associated with the person being described. The Fields of Research Classfication can be found at the following URL.<br/><br/><a href="https://www.abs.gov.au/Ausstats/abs@.nsf/Latestproducts/6BB427AB9696C225CA2574180004463E?opendocument" class="text-link">https://www.abs.gov.au/Ausstats/abs@.nsf/Latestproducts/6BB427AB9696C225CA2574180004463E?opendocument</a>')
	,(65,2,'anzseoSubject','Socio-Economic Objective','3','anzseoSubject','1','0',11,19,19,null,null,'Include terms and corresponding classification codes to be associated with the resource being described. Information on the Socio-Economic Objectives Classification Codes can be found at the following URL.<br/><br/><a href="https://www.abs.gov.au/Ausstats/abs@.nsf/Latestproducts/CF7ADB06FA2DFD69CA2574180004CB82?opendocument" class="text-link">https://www.abs.gov.au/Ausstats/abs@.nsf/Latestproducts/CF7ADB06FA2DFD69CA2574180004CB82?opendocument</a>')
	,(66,2,'locSubject','Keywords','1',null,'1','0',11,20,20,null,null,'Record keywords describing the activity. 5-10 keywords will normally be sufficient. The keywords must be specific enough for researchers with similar interests to find your activity.<br/><br/>If you need assistance in selecting keywords, there are many thesauri that may be helpful. Please use one appropriate to your discipline. If you are unable to identify a discipline specific classification scheme, you may wish to use the Library of Congress Classification scheme available at <a href="http://www.loc.gov/catdir/cpso/lcco/" class="text-link">http://www.loc.gov/catdir/cpso/lcco/</a>')
	,(67,2,'anztoaSubject','Type of Research Activity','3','anztoaSubject','0','0',11,21,21,null,null,'If appropriate, assign a research activity classficiation. There are four set values that can be assigned. Please choose one.')
	,(68,2,'publication','Publications','5',null,'0','0',12,22,22,null,null,'If appropriate, please include information about any publications that relate to the activity/project<br/>For example:<br/>Identifier Type: International Standard Serial Number<br/>Identifier Value: 0278-7393<br/>Publication Reference: Heathcote, A. (2003) Item recognition memory and the ROC. Journal of Experimental Psychology: Learning, Memory and Cognition, 29, 1210-1230.')
	,(69,2,'relatedWebsites','Related Websites','5',null,'0','0',12,23,23,null,null,'If appropriate, please include a URL and Notes for any websites that relate to the resourrce being described. For example:<br/><br/>URL: http://anusf.anu.edu.au/index.php<br/><br/>Title: ANU Supercomputing Facility')
	,(70,2,'externalId','Other Related Identifiers','1',null,'1','0',12,24,24,null,null,'Identifiers for systems external to ANU Data Commons')
	;
	
insert into template_attribute_column (template_attribute_id, name, label, field_type_id, select_code, column_order) values 
	(68,'pubType','Identifier Type',3,'identifierType',1)
	,(68,'pubValue','Identifier Value',1,null,2)
	,(68,'pubTitle','Publication Title',2,null,3)
	,(69,'relatedWebURL','URL',1,null,1)
	,(69,'relatedWebTitle','Title',1,null,2)
	;

-- insert service template
insert into template_tab (id, template_id, name, label, tab_order, tooltip) values
	(13, 3, 'general', 'General', 1, null)
	, (14, 3, 'description', 'Description', 2, null)
	, (15, 3, 'contact','Contact',3,'Information about contact(s)')
	, (16, 3, 'subject', 'Subject', 4, null)
	, (17, 3, 'other', 'Other', 5, null)
	;

insert into template_attribute (id, template_id, name, label, field_type_id, select_code, multivalued, required, template_tab_id, form_order, display_order, max_length, extra,tooltip) values
	(71,3,'type','Type','1',null,'0','0',13,1,1,null,null,null)
	,(72,3,'subType','Service Type','3','service_sub_type','0','1',13,2,2,null,'required','Create: produces a new data object representing existing phenomona in the world, including physical reality and user input. An instrument creates data<br/>Generate: procues a new data object out of mathematical formulae and parameters, rather than capturing and representing existing data in the world. A simulator generates data. (The simulation is the generated data.) A random number generator generates data.<br/>Report: Presents existing data in ta summary form. A visualisation reports on data.<br/>Annotate: Links an annotation to a data object, or part thereof.<br/>Transform: Changes a data object into a new data object, with a distinct format. An analysis tool creates a new data object out of data (either raw data, or other analyses).<br/>Assemble: Builds a new data object instance composed of exsiting data objects. A survey generation tool creates a survey form out of user input and templates.<br/>OAI-PMH Harvest: Open Archives Initiative Protocol for Metadata Harvesting. See also http://www.openarchives.org/<br/>Search service over HTTP. RFC2626.<br/>OpenSearch search - a collection of technologies that allow publishing of search results in a format suitable for syndication and aggregation.<br/>SRU search: SRU is a standard XML-focused search protocol for Internet search queries based on Z39.50 semantics.<br/>z39.50 search - the International Standard, ISO 23950: Information Retrieval (Z39.50): Application Service Definition and Protocol Specification, (also ANSI/NISO Z39.50). The standard specifies a client/server-based protocol for searching and retrieving information from remove databases.<br/>ATOM syndication - an XML-based Web content and metadata syndication format. http://tools.ietf.org/html/rfc4287<br/>RSS feed - a family of web feed formats that are specified using XML.')
	,(73,3,'ownerGroup','Access Privileges','6',null,'0','1',13,3,3,null,'required','Please choose the access privilege group for the service')
	,(74,3,'name','Title','1',null,'0','1',13,4,4,'80','required','Enter the name of the service<br/><br/>For example:<br/>The Australian National University Supercomputer Facility')
	,(75,3,'abbrName','Abbreviated Title','1',null,'0','0',13,5,5,'255',null,'If there is an abbreviated name used for the service include it here<br/><br/>For example:<br/>ANUSF')
	,(76,3,'altName','Alternate Title','1',null,'0','0',13,6,6,'255',null,'If there is an alternative title for the service, please include it here')
	,(77,3,'existenceStart','Date the service was started','1',null,'0','0',13,7,7,null,'date',null)
	,(78,3,'existenceEnd','Date the service was stopped','1',null,'0','0',13,8,8,null,'date',null)
	,(79,3,'briefDesc','Brief Description','2',null,'0','0',14,9,9,'4000',null,'A brief summary about the service')
	,(80,3,'fullDesc','Full Description','2',null,'0','0',14,10,10,'4000','needed','A description that is as rich as possible.')
	,(81,3,'email','Contact Email','1',null,'1','0',15,11,11,null,'needed email',null)
	,(82,3,'postalAddress','Contact Address','2',null,'0','0',15,12,12,null,null,null)
	,(83,3,'phone','Contact Phone Number','1',null,'1','0',15,13,13,null,null,null)
	,(84,3,'fax','Contact Fax Number','1',null,'1','0',15,14,14,null,null,null)
	,(85,3,'deliveryMethod','Delivery Method','3','delivery_method','0','0',17,15,15,null,'needed','Web service: according to the W3C, ''a software system designed to support interoperable machine-to-machine interaction over a network. It has an interface described in a machine-processable format''.<br/>Software: all services provided by software other than as web services; users interact with these through a user interface or on a local system. This includese Unix applications, PC/Mac applications, and software access through a browser.<br/>Offline service: a service not provided through computers or the internet. Instruments such as beamlines and microscopes are normally modelled as offline services.<br/>Workflow: a service that orchestrates other services. Kepler workflows, which script how various instruments and computational tools interact to deliver an output, are an example of a workflow.')
	,(86,3,'anzforSubject','Fields of Research','3','anzforSubject','1','0',16,16,16,null,null,'Include Fields of Research terms and corresponding classification codes to be associated with the person being described. The Fields of Research Classfication can be found at the following URL.<br/><br/><a href="https://www.abs.gov.au/Ausstats/abs@.nsf/Latestproducts/6BB427AB9696C225CA2574180004463E?opendocument" class="text-link">https://www.abs.gov.au/Ausstats/abs@.nsf/Latestproducts/6BB427AB9696C225CA2574180004463E?opendocument</a>')
	,(87,3,'anzseoSubject','Socio-Economic Objective','3','anzseoSubject','1','0',16,17,17,null,null,'Include terms and corresponding classification codes to be associated with the resource being described. Information on the Socio-Economic Objectives Classification Codes can be found at the following URL.<br/><br/><a href="https://www.abs.gov.au/Ausstats/abs@.nsf/Latestproducts/CF7ADB06FA2DFD69CA2574180004CB82?opendocument" class="text-link">https://www.abs.gov.au/Ausstats/abs@.nsf/Latestproducts/CF7ADB06FA2DFD69CA2574180004CB82?opendocument</a>')
	,(88,3,'locSubject','Keywords','1',null,'1','0',16,18,18,null,null,'Record keywords describing the activity. 5-10 keywords will normally be sufficient. The keywords must be specific enough for researchers with similar interests to find your activity.<br/><br/>If you need assistance in selecting keywords, there are many thesauri that may be helpful. Please use one appropriate to your discipline. If you are unable to identify a discipline specific classification scheme, you may wish to use the Library of Congress Classification scheme available at <a href="http://www.loc.gov/catdir/cpso/lcco/" class="text-link">http://www.loc.gov/catdir/cpso/lcco/</a>')
	,(89,3,'websiteAddress','Access URL','1',null,'1','0',17,19,19,null,null,'The url to access the service.')
	,(90,3,'relatedURL','Service Website','1',null,'0','0',17,20,20,null,null,'A link to a website associated with information about the service')
	,(91,3,'accessPolicy','Access Policy Website','1',null,'0','0',17,21,21,null,null,'A link to a website that contains the access policy for the service')
	;


-- insert party template
insert into template_tab (id, template_id, name, label, tab_order, tooltip) values
	(18, 4, 'general', 'General', 1, null)
	, (19, 4, 'description', 'Description', 2, null)
	, (20, 4, 'contact','Contact',3,'Information about contact(s)')
	, (21, 4, 'subject', 'Subject', 4, null)
	;
	
insert into template_attribute (id, template_id, name, label, field_type_id, select_code, multivalued, required, template_tab_id, form_order, display_order, max_length, extra,tooltip) values
	(92,4,'type','Type','1',null,'0','0',18,1,1,null,null,null)
	,(93,4,'subType','Party Type','3','group_sub_type','0','1',18,2,2,null,'required',null)
	,(94,4,'ownerGroup','Access Privileges','6',null,'0','1',18,3,3,null,'required','Please choose the access privilege group')
	,(95,4,'name','Title','1',null,'0','1',18,4,4,'80','required','This is the title of the group/position.')
	,(96,4,'abbrName','Brief Title','1',null,'0','0',18,5,5,'255',null,'Please include a brief title if your title is very long')
	,(97,4,'altName','Alternate Title','1',null,'0','0',18,6,6,'255',null,'If there is an alternative title for the group/position, please include it here.')
	,(98,4,'nlaIdentifier','NLA Identifier','1',null,'0','0',18,7,7,null,null,'Please include the groups National Library of Australia Identifier if it exists.  You can find these at the Trove website http://trove.nla.gov.au/')
	,(99,4,'existenceStart','Date the group/position was formed','1',null,'0','0',18,8,8,null,'date',null)
	,(100,4,'existenceEnd','Date the group/position was dissolved','1',null,'0','0',18,9,9,null,'date',null)
	,(101,4,'briefDesc','Brief Description','2',null,'0','0',19,10,10,'4000',null,'A brief description about the group/position')
	,(102,4,'fullDesc','Full Description','2',null,'0','0',19,11,11,'4000','needed','A longer description of the group/position. This may include a history of the group/position.')
	,(103,4,'email','Contact Email','1',null,'1','0',20,12,12,null,'needed email',null)
	,(104,4,'postalAddress','Contact Address','2',null,'0','0',20,13,13,null,null,null)
	,(105,4,'phone','Contact Phone Number','1',null,'1','0',20,14,14,null,null,null)
	,(106,4,'fax','Contact Fax Number','1',null,'1','0',20,15,15,null,null,null)
	,(107,4,'websiteAddress','Website Address','1',null,'1','0',20,16,16,null,null,null)
	,(108,4,'anzforSubject','Fields of Research','3','anzforSubject','1','0',21,17,17,null,'needed','Include Fields of Research terms and corresponding classification codes to be associated with the person being described. The Fields of Research Classfication can be found at the following URL.<br/><br/><a href="https://www.abs.gov.au/Ausstats/abs@.nsf/Latestproducts/6BB427AB9696C225CA2574180004463E?opendocument" class="text-link">https://www.abs.gov.au/Ausstats/abs@.nsf/Latestproducts/6BB427AB9696C225CA2574180004463E?opendocument</a>')
	,(109,4,'anzseoSubject','Socio-Economic Objective','3','anzseoSubject','1','0',21,18,18,null,null,'Include terms and corresponding classification codes to be associated with the resource being described. Information on the Socio-Economic Objectives Classification Codes can be found at the following URL.<br/><br/><a href="https://www.abs.gov.au/Ausstats/abs@.nsf/Latestproducts/CF7ADB06FA2DFD69CA2574180004CB82?opendocument" class="text-link">https://www.abs.gov.au/Ausstats/abs@.nsf/Latestproducts/CF7ADB06FA2DFD69CA2574180004CB82?opendocument</a>')
	,(110,4,'locSubject','Keywords','1',null,'1','0',21,19,19,null,null,'Record keywords describing the activity. 5-10 keywords will normally be sufficient. The keywords must be specific enough for researchers with similar interests to find your activity.<br/><br/>If you need assistance in selecting keywords, there are many thesauri that may be helpful. Please use one appropriate to your discipline. If you are unable to identify a discipline specific classification scheme, you may wish to use the Library of Congress Classification scheme available at <a href="http://www.loc.gov/catdir/cpso/lcco/" class="text-link">http://www.loc.gov/catdir/cpso/lcco/</a>')
	;

-- insert person template
insert into template_tab (id, template_id, name, label, tab_order, tooltip) values
	(22, 5, 'general', 'General', 1, null)
	, (23, 5, 'description', 'Description', 2, null)
	, (24, 5, 'contact','Contact',3,'Information about contact(s)')
	, (25, 5, 'subject', 'Subject', 4, null)
	;
	
insert into template_attribute (id, template_id, name, label, field_type_id, select_code, multivalued, required, template_tab_id, form_order, display_order, max_length, extra,tooltip) values
	(111,5,'type','Type','1',null,'0','0',22,1,1,null,null,null)
	,(112,5,'subType','Party Type','1',null,'0','0',22,2,2,null,null,null)
	,(113,5,'ownerGroup','Access Privileges','6',null,'0','0',22,3,3,null,'required','Please choose the access privilege group')
	,(114,5,'title','Title','1',null,'0','0',22,4,4,null,null,null)
	,(115,5,'givenName','Given Name','1',null,'0','0',22,5,5,null,null,null)
	,(116,5,'lastName','Surname','1',null,'0','0',22,6,6,null,'required',null)
	,(117,5,'Suffixes','suffix','1',null,'0','0',22,7,7,null,null,null)
	,(118,5,'altTitle','Alternate Title','1',null,'0','0',22,8,8,null,null,null)
	,(119,5,'altGivenName','Alternate Given Name','1',null,'0','0',22,9,9,null,null,null)
	,(120,5,'altLastName','Alternate Surname','1',null,'0','0',22,10,10,null,null,null)
	,(121,5,'altSuffix','Alternate Suffixes','1',null,'0','0',22,11,11,null,null,null)
	,(122,5,'abbrTitle','Abbreviated Title','1',null,'0','0',22,12,12,null,null,null)
	,(123,5,'abbrGivenName','Abbreviated Given Name','1',null,'0','0',22,13,13,null,null,null)
	,(124,5,'abbrLastName','Abbreviated Surname','1',null,'0','0',22,14,14,null,null,null)
	,(125,5,'abbrSuffix','Abbreviated Suffixes','1',null,'0','0',22,15,15,null,null,null)
	,(126,5,'uid','Uni ID','1',null,'0','0',22,16,16,null,null,null)
	,(127,5,'orcid','ORCID','1',null,'0','0',22,17,17,null,null,'Please include the individuals ORCID if it exists.')
	,(128,5,'nlaIdentifier','NLA Identifier','1',null,'0','0',22,18,18,null,null,'Please include the individuals National Library of Australia Identifier if it exists.  You can find these at the Trove website http://trove.nla.gov.au/')
	,(129,5,'existenceStart','Birth Year/Date','1',null,'0','0',22,19,19,null,'date',null)
	,(130,5,'existenceEnd','Death Year/Date','1',null,'0','0',22,20,20,null,'date',null)
	,(131,5,'briefDesc','Brief Description','2',null,'0','0',23,21,21,'4000',null,'A brief description about the individual, this can include what work is being performed for The Australian National University')
	,(132,5,'fullDesc','Full Description','2',null,'0','0',23,22,22,'4000','needed','A longer description of the individual. This may include a biography of the person.')
	,(133,5,'email','Email Address','1',null,'1','0',24,23,23,null,'needed email',null)
	,(134,5,'postalAddress','Postal Address','2',null,'0','0',24,24,24,null,null,null)
	,(135,5,'phone','Phone Number','1',null,'1','0',24,25,25,null,null,null)
	,(136,5,'fax','Fax Number','1',null,'1','0',24,26,26,null,null,null)
	,(137,5,'websiteAddress','Website Address','1',null,'1','0',24,27,27,null,null,null)
	,(138,5,'anzforSubject','Fields of Research','3','anzforSubject','1','0',25,28,28,null,'needed','Include Fields of Research terms and corresponding classification codes to be associated with the person being described. The Fields of Research Classfication can be found at the following URL.<br/><br/><a href="https://www.abs.gov.au/Ausstats/abs@.nsf/Latestproducts/6BB427AB9696C225CA2574180004463E?opendocument" class="text-link">https://www.abs.gov.au/Ausstats/abs@.nsf/Latestproducts/6BB427AB9696C225CA2574180004463E?opendocument</a>')
	,(139,5,'anzseoSubject','Socio-Economic Objective','3','anzseoSubject','1','0',25,29,29,null,null,'Include terms and corresponding classification codes to be associated with the resource being described. Information on the Socio-Economic Objectives Classification Codes can be found at the following URL.<br/><br/><a href="https://www.abs.gov.au/Ausstats/abs@.nsf/Latestproducts/CF7ADB06FA2DFD69CA2574180004CB82?opendocument" class="text-link">https://www.abs.gov.au/Ausstats/abs@.nsf/Latestproducts/CF7ADB06FA2DFD69CA2574180004CB82?opendocument</a>')
	,(140,5,'locSubject','Keywords','1',null,'1','0',25,30,30,null,null,'Record keywords describing the activity. 5-10 keywords will normally be sufficient. The keywords must be specific enough for researchers with similar interests to find your activity.<br/><br/>If you need assistance in selecting keywords, there are many thesauri that may be helpful. Please use one appropriate to your discipline. If you are unable to identify a discipline specific classification scheme, you may wish to use the Library of Congress Classification scheme available at <a href="http://www.loc.gov/catdir/cpso/lcco/" class="text-link">http://www.loc.gov/catdir/cpso/lcco/</a>')
	;

-- insert ltern collection template
insert into template_tab (id, template_id, name, label, tab_order, tooltip) values
	(26, 8, 'general', 'General', 1, null)
	, (27, 8, 'description', 'Description', 3, null)
	, (28, 8, 'coverage', 'Coverage', 2, 'Dates and Locations relating to collection')
	, (29, 8, 'people','People',4,'Information about contact(s)')
	, (30, 8, 'subject', 'Subject', 5, null)
	, (31, 8, 'rights', 'Rights', 6, null)
	, (32, 8, 'management', 'Management', 7, null)
	;
	
insert into template_attribute (id, template_id, name, label, field_type_id, select_code, multivalued, required, template_tab_id, form_order, display_order, max_length, extra,tooltip) values
	(141,8,'type','Type','1',null,'0','0',26,1,1,null,null,null)
	,(142,8,'name','Title','1',null,'0','1',26,2,2,'255','required','This is the title of the data collection. It needs to be unique, i.e. do not use a title that is identical to an existing publication that is related to and/or underpinned by the data.<br/><br/>Titles that are descriptive of the actual data are best. Try to include key distinctive characteristics that would provide information for potential users to determine if the data might be useful to them. These may include information specific to the entities studied, survey data, observations, images collected, location, time, and temporal or spatial coverage.<br/><br/>Examples:<br/>Net levels of greenhouse gas emission and sources in the New South Wales Hunter Valley, 1990 - 1998.')
	,(143,8,'abbrName','Brief Title','1',null,'0','0',26,3,3,'255',null,'Please include a brief title if your title is very long')
	,(144,8,'altName','Alternate Title','1',null,'0','0',26,4,4,'255',null,'If there is an alternative title for your data collection, please include it here.')
	,(145,8,'subType','Collection Type','4','collection_sub_type','0','1',26,5,5,null,'required','Catalogue or Index - a collection of descriptions describing the content of one or more collective works at the item level.<br/>Collection - compiled content created as separate and independent works and assembled into a collective whole<br/>Registry - a collection of registry objects compiled to support the business of a given community<br/>Repository - a collection of physical or digital objects compiled for information and documentation purposes and/or for storage and safekeeping<br/>Dataset - a collection of physical or digital objects generated by research activities.<br/>Classification Scheme - A list or arrangement of terms used in a particular context e.g. ontologies, thesauri<br/>Software - One or more items that represent a software product')
	,(146,8,'ownerGroup','Access Privileges','6',null,'0','1',26,6,6,null,'required','Please choose the access privilege group for the collection')
	,(147,8,'doi','DOI - Digital Object Identifier','1',null,'0','0',26,7,7,null,null,'The Digital Object Identifier for this record.Please note that if this does not already exist a DOI will be created when the record is published')
	,(148,8,'websiteAddress','Website Address','1',null,'1','0',26,8,8,null,null,'Websites at which the collections data is held')
	,(149,8,'metaLang','Metadata Language','3','language','0','0',26,9,9,null,null,'Please select the language that you are using to describe this data')
	,(150,8,'dataLang','Data Language','3','language','0','0',26,10,10,null,null,'Please select the language the data is in')
	,(151,8,'significanceStatement','Significance Statement','2',null,'0','0',27,11,11,null,null,'What is significant about this dataset. For example:<br/><br/>The first Australian full talking film.')
	,(152,8,'briefDesc','Brief Description','2',null,'0','0',27,12,12,null,null,'A brief summary about the data')
	,(153,8,'fullDesc','Full Description','2',null,'0','0',27,13,13,null,'needed','The description should be as rich as possible. If applicable, include: the scope; details of what is being studied or recorded; methodologies used, information about any instruments that were used to produce and/or collect the data; relevant standards used; conditions under which the study or research occured or the data was collected, etc<br/>If there were any problems or other issues with methods used to produce and/or collect the data, please include those as well.')
	,(154,8,'methods','Methods','5',null,'0','0',27,14,14,null,null,null)
	,(155,8,'fileContent','File Descriptions','5',null,'0','0',27,15,15,null,null,null)
	,(156,8,'email','Contact Email','1',null,'1','0',29,16,16,null,'needed email',null)
	,(157,8,'postalAddress','Contact Address','2',null,'0','0',29,17,17,null,null,null)
	,(158,8,'phone','Contact Phone Number','1',null,'1','0',29,18,18,null,null,null)
	,(159,8,'fax','Contact Fax Number','1',null,'1','0',29,19,19,null,null,null)
	,(160,8,'principalInvestigator','Principal Investigator','1',null,'0','0',29,20,20,null,null,'Please record the name of the principal investigator')
	,(161,8,'supervisor','Supervisors','1',null,'1','0',29,21,21,null,null,'Please record the name of the person identified as supervisor in relation to the creation of the data. (Note that this may be  the same as the Primary Contact.)')
	,(162,8,'collaborator','Collaborators','1',null,'1','0',29,22,22,null,null,'Please record the names of any other collaborators in the research project which created the data.')
	,(163,8,'anzforSubject','Fields of Research','3','anzforSubject','1','0',30,23,23,null,'needed','Include Fields of Research terms and corresponding classification codes to be associated with the person being described. The Fields of Research Classfication can be found at the following URL.<br/><br/><a href="https://www.abs.gov.au/Ausstats/abs@.nsf/Latestproducts/6BB427AB9696C225CA2574180004463E?opendocument" class="text-link">https://www.abs.gov.au/Ausstats/abs@.nsf/Latestproducts/6BB427AB9696C225CA2574180004463E?opendocument</a>')
	,(164,8,'anzseoSubject','Socio-Economic Objective','3','anzseoSubject','1','0',30,24,24,null,null,'Include terms and corresponding classification codes to be associated with the resource being described. Information on the Socio-Economic Objectives Classification Codes can be found at the following URL.<br/><br/><a href="https://www.abs.gov.au/Ausstats/abs@.nsf/Latestproducts/CF7ADB06FA2DFD69CA2574180004CB82?opendocument" class="text-link">https://www.abs.gov.au/Ausstats/abs@.nsf/Latestproducts/CF7ADB06FA2DFD69CA2574180004CB82?opendocument</a>')
	,(165,8,'locSubject','Keywords','1',null,'1','0',30,25,25,null,null,'Record keywords describing the activity. 5-10 keywords will normally be sufficient. The keywords must be specific enough for researchers with similar interests to find your activity.<br/><br/>If you need assistance in selecting keywords, there are many thesauri that may be helpful. Please use one appropriate to your discipline. If you are unable to identify a discipline specific classification scheme, you may wish to use the Library of Congress Classification scheme available at <a href="http://www.loc.gov/catdir/cpso/lcco/" class="text-link">http://www.loc.gov/catdir/cpso/lcco/</a>')
	,(166,8,'taxonomySubject','Taxonomic Classification','5',null,'0','0',30,26,26,null,null,null)
	,(167,8,'anztoaSubject','Type of Research Activity','4','anztoaSubject','0','0',30,27,27,null,null,'If appropriate, assign a research activity classification. There are four set values that can be assigned. Please choose one.')
	,(168,8,'coverageDates','Date Coverage','5',null,'0','0',28,28,28,null,null,'If appropriate, please indicate a date range relevant to the content of the material described.<br/>1990 - 2005<br/>2011-05-29 - 2011-12-31<br/>2012-06-21T11:49:53+10:00 - 2012-07-02T15:21+10:00')
	,(169,8,'coverageDateText','Time Period','1',null,'1','0',28,29,29,null,null,'If appropriate, please describe the time period during which the data was collected, observations were made, images were created or the time period that the resource is linked to intellectually or thematically. Examples include:<br/>18th Century<br/>World War II')
	,(170,8,'coverageArea','Geospatial Location','5',null,'0','0',28,30,30,null,null,'If appropriate, please include geospatial location information relevant to the research dataset/collection. This information may describe a geographical area where the data was collected, a place which is the subject of the collection or a location which is the focus of an activity e.g. co-ordinates or place name.')
	,(171,8,'createdDate','Date of data creation','1',null,'0','0',27,31,31,null,'date','The date/year the data was created.  It may be the date the information was created before an embargo period. For example:<br/><br/>1987<br/>2005-06<br/>2009-01-24')
	,(172,8,'citationYear','Year of data publication','1',null,'0','0',27,32,32,null,'date','The year the data was first published for citation purposes. If this field is not populated when publishing it will default to the year it is published in ANU Data Commons')
	,(173,8,'citCreator','Creator(s) for Citation','5',null,'0','0',27,33,33,null,null,'The creator(s) of the data for citation purposes. This field indicates who should be considered named in a citation.  Defaults to ''The Australian National University''. For example:<br/><br/>T Irino; R Tada<br/>Geofon operator<br/>Michael Denhard')
	,(174,8,'citationPublisher','Publisher for Citation','1',null,'0','0',27,34,34,null,null,'The publisher of the data.  Defaults to ''The Australian National University Data Commons''. For example:<br/><br/>Australian Data Archive<br/>Incorporated Research Institutions for Seismology')
	,(175,8,'publication','Publications','5',null,'0','0',27,35,35,null,null,'If appropriate, please include information about any publications that relate to the collection/dataset<br/>For example:<br/>Identifier Type: International Standard Serial Number<br/>Identifier Value: 0278-7393<br/>Publication Reference: Heathcote, A. (2003) Item recognition memory and the ROC. Journal of Experimental Psychology: Learning, Memory and Cognition, 29, 1210-1230.')
	,(176,8,'relatedWebsites','Related Websites','5',null,'0','0',27,36,36,null,null,'If appropriate, please include a URL and Notes for any websites that relate to the resource being described. For example:<br/><br/>URL: http://anusf.anu.edu.au/index.php<br/><br/>Title: ANU Supercomputing Facility')
	,(177,8,'externalId','Other Related Identifiers','1',null,'1','0',27,37,37,null,null,'Identifiers for systems external to ANU Data Commons')
	,(178,8,'accessRights','Access Rights','2',null,'0','0',31,38,38,null,null,'Enter a statement about access (or access conditions) to the resource. This will include access restrictions or embargoes based on privacy, security or other policies.<br/><br/>Examples:<br/>Contact Chief Investigator to negotiate access to the data<br/><br/>Embargoes until 1 year after publication of the research<br/><br/>Open Access allowed (see rights held in and over resource below)')
	,(179,8,'accessRightsType','Access Rights Type','3','accessRightsType','0','0',31,39,39,null,null,'Open - Data is publicly accessible online<br/><br/>Conditional - Data is publicly accessible to certain conditions. For example:<br/>· a fee applies<br/>· the data is only accessible at a specific physical location<br/><br/>Restricted - Data access is limited. For example:<br/>· following an embargo period<br/>· to a particular group of users<br/>· where formal permission is granted')
	,(180,8,'rightsStatement','Rights held in and over the data','2',null,'0','0',31,40,40,null,null,'Include information on copyright, licences and other intellectual property rights.<br/><br/>Examples:<br/>This dataset is made available under the Public Domain Dedication and Licence v1.0. Full text can be found at http://www.opendatacommons.org/licences/pddl/1.0/<br/><br/>Creative Commons Licence (CC BY or CC BY-SA or CC BY-ND or CC BY-NC or CC BY-NC-SA or CC BY-NC-ND) is assigned to this data. Details of the licence can be found at http://creativecommons.org.au/licences.<br/><br/>The (name) licence that controls this data is available at (URL)')
	,(181,8,'licenceType','Licence Type','3','licenceType','0','0',31,41,41,null,null,'Further information on the licence types can be found at http://www.ausgoal.gov.au/the-ausgoal-licence-suite<br/><br/>Attribution - This licence lets others distribute, remix, tweak, and build upon your work, even commercially, as long as they credit you for the original creation. This is the most accomodating of licences offered. Recommended for maximum dissemination and use of licenced materials.<br/>Attribution-shareAlike - This licence lets others remix, tweak and build upon your work even for commercial purposes, as long as they credit you and licence their new creations under identical terms. This licence is often compared to ''copyleft'' free and open source software licences. All new works based on yours will carry the same licence, so any derivatives will also allow commercial use. This is the licence used by Wikipedia, and is recommended for materials that would benefit from incorporating content from Wikipedia and similarly licenced projects.<br/>Attribution-NoDerivatives - This licence allows for redistribution, commercial and non-commercial, as long as it is passed along unchanged and in whole, with credit to you.<br/>Attribution-NonCommercial - This licence lets other remix, tweak and build upon your work non-commercially, and although their new works must also acknowledge you and be non-commercial, they don''t have to licence their derivative works on the same terms.<br/>Attribution-NonCommercial-ShareAlike - This licence lets others remix, tweak, and build upon your work non-commercially, as long as they credit you and licence their new creations under the identical terms.<br/>Attribution-NonCommercial-NoDerivatives - This licence is the most restrictive of AusGoal''s six main licences, only allowing others to download your works and share them with others as long as they credit you, but they can''t change them in any way or use them commercially.<br/>General Public Licence - The GNU General Public Licence is a free, copyleft licence for software and other kinds of works. Further information can be found at http://www.gnu.org/copyleft/gpl.html<br/>AusGoal Restrictive Licence - This licence has been developed specifically for mateiral that may contain personal or other confidential information. It may also be used for other reasons, including material to be licenced under some form of limiting or restrictive condiation (e.g. a time limit on use, or payment arrangements others than an initial once-only fee).')
	,(182,8,'licence','Licence','2',null,'0','0',31,42,42,null,null,'Licence Information')
	,(183,8,'dataLocation','Data Location','2',null,'0','0',32,43,43,null,null,'Include where the data is located<br/><br/>Example:<br/>This data is located at (URL or physical location)')
	,(184,8,'embargoDate','Embargo Date','1',null,'0','0',32,44,44,null,'date','If this collection is under embargo, please provide the embargo lift date<br/>Example:<br/>2020-01-01')
	,(185,8,'dataRetention','Retention Period','1',null,'0','0',32,45,45,null,null,'Include the period of time that the data must be retained. This must be in line with institutional/funding body or legistlative retention<br/><br/>Examples:<br/>7 years<br/><br/>Until 31st December 2016<br/><br/>Indefinately')
	,(186,8,'disposalDate','Disposal Date','1',null,'0','0',32,46,46,null,null,'The identified disposal date for this data is (date)')
	,(187,8,'dataExtent','Extent or Quantity','1',null,'0','0',32,47,47,null,null,'Enter the number of files that comprise the research dataset/collection')
	,(188,8,'dataSize','Data Size','1',null,'1','0',32,48,48,null,null,'Record the file size of the data in KB, MB, GB, TB, etc. If you are unsure, please consult your IT support person.')
	,(189,8,'dataMgmtPlan','Data Management Plan','4','dataMgmtPlan','0','0',32,49,49,null,null,'Indicate if there is a data management plan for this data. Information on data management plans is found at http://libguides.anu.edu.au/datamanagement/')
	;

insert into template_attribute_column (template_attribute_id, name, label, field_type_id, select_code, column_order) values 
	(154,'methodNumber','Number',1,null,1)
	,(154,'methodTitle','Title',1,null,2)
	,(154,'methodDescription','Description',2,null,3)
	,(154,'methodInstrument','Instrument',2,null,4)
	,(155,'fileContentName','Name',1,null,1)
	,(155,'fileContentDescription','Description',2,null,2)
	,(166,'taxonomyType','Taxonomy Type',3,null,1)
	,(166,'taxonomyValue','Taxonomy Value',1,null,2)
	,(168,'dateFrom','Date From',7,null,1)
	,(168,'dateTo','Date To',7,null,2)
	,(170,'covAreaType','Location Type',3,'coordinate_type',1)
	,(170,'covAreaValue','Location Value',1,null,2)
	,(173,'citCreatorGiven','Given Name',1,null,1)
	,(173,'citCreatorSurname','Surname',1,null,2)
	,(175,'pubType','Identifier Type',3,'identifierType',1)
	,(175,'pubValue','Identifier Value',1,null,2)
	,(175,'pubTitle','Publication Title',2,null,3)
	,(175,'pubNotes','Publication Reference',2,null,4)
	,(176,'relatedWebURL','URL',1,null,1)
	,(176,'relatedWebTitle','Title',1,null,2)
	;

	
select setval('template_tab_id_seq', (SELECT MAX(id) FROM template_tab));
select setval('template_attribute_id_seq', (SELECT MAX(id) FROM template_attribute));
	
