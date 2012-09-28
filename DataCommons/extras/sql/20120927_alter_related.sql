/**
 * 20120927_alter_related.sql
 * 
 * Australian National University Data Commons
 * 
 * This script adds tables to show the link types
 * 
 * Version	Date		Developer				Description
 * 0.1		27/09/2012	Genevieve Turner (GT)	Initial
 */

alter table link_type add column reverse varchar(60);

update link_type
set reverse = 'hasPart'
where code = 'isPartOf';

update link_type
set reverse = 'isPartOf'
where code = 'hasPart';

update link_type
set reverse = 'isOutputOf'
where code = 'hasOutput';

update link_type
set reverse = 'isManagerOf'
where code = 'isManagedBy';

update link_type
set reverse = 'isOwnerOf'
where code = 'isOwnedBy';

update link_type
set reverse = 'isParticipantIn'
where code = 'hasParticipant';

update link_type
set reverse = 'hasAssociationWith'
where code = 'hasAssociationWith';

update link_type
set reverse = 'isDescribedBy'
where code = 'describes';

update link_type
set reverse = 'describes'
where code = 'isDescribedBy';

update link_type
set reverse = 'isLocationFor'
where code = 'isLocatedIn';

update link_type
set reverse = 'isLocatedIn'
where code = 'isLocationFor';

update link_type
set reverse = 'hasDerivedCollection'
where code = 'isDerivedFrom';

update link_type
set reverse = 'isDerivedFrom'
where code = 'hasDerivedCollection';

update link_type
set reverse = 'isCollectorOf'
where code = 'hasCollector';

update link_type
set reverse = 'enriches'
where code = 'isEnrichedBy';

update link_type
set reverse = 'hasOutput'
where code = 'isOutputOf';

update link_type
set reverse = 'isSupportedBy'
where code = 'supports';

update link_type
set reverse = 'makesAvailable'
where code = 'isAvailableThrough';

update link_type
set reverse = 'produces'
where code = 'isProducedBy';

update link_type
set reverse = 'presents'
where code = 'isPresentedBy';

update link_type
set reverse = 'operatesOn'
where code = 'isOperatedOnBy';

update link_type
set reverse = 'addsValueTo'
where code = 'hasValueAddedBy';

update link_type
set reverse = 'isMemberOf'
where code = 'hasMember';

update link_type
set reverse = 'hasMember'
where code = 'isMemberOf';

update link_type
set reverse = 'isFunderOf'
where code = 'isFundedBy';

update link_type
set reverse = 'isFundedBy'
where code = 'isFunderOf';

update link_type
set reverse = 'isEnrichedBy'
where code = 'enriches';

update link_type
set reverse = 'hasCollector'
where code = 'isCollectorOf';

update link_type
set reverse = 'hasParticipant'
where code = 'isParticipantIn';

update link_type
set reverse = 'isManagedBy'
where code = 'isManagerOf';

update link_type
set reverse = 'isOwnedBy'
where code = 'isOwnerOf';

update link_type
set reverse = 'supports'
where code = 'isSupportedBy';

update link_type
set reverse = 'isAvailableThrough'
where code = 'makesAvailable';

update link_type
set reverse = 'isProducedBy'
where code = 'produces';

update link_type
set reverse = 'isPresentedBy'
where code = 'presents';

update link_type
set reverse = 'isOperatedOnBy'
where code = 'operatesOn';

update link_type
set reverse = 'hasValueAddedBy'
where code = 'addsValueTo';