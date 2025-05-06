/*******************************************************************************
 * Australian National University Data Commons
 * Copyright (C) 2013  The Australian National University
 * 
 * This file is part of Australian National University Data Commons.
 * 
 * Australian National University Data Commons is free software: you
 * can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package au.edu.anu.datacommons.doi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.datacite.schema.kernel_4.DateType;
import org.datacite.schema.kernel_4.DescriptionType;
import org.datacite.schema.kernel_4.NameType;
import org.datacite.schema.kernel_4.Resource;
import org.datacite.schema.kernel_4.Resource.AlternateIdentifiers;
import org.datacite.schema.kernel_4.Resource.Contributors;
import org.datacite.schema.kernel_4.Resource.Creators;
import org.datacite.schema.kernel_4.Resource.Creators.Creator;
import org.datacite.schema.kernel_4.Resource.Creators.Creator.CreatorName;
import org.datacite.schema.kernel_4.Resource.Dates;
import org.datacite.schema.kernel_4.Resource.Dates.Date;
import org.datacite.schema.kernel_4.Resource.Descriptions;
import org.datacite.schema.kernel_4.Resource.Descriptions.Description;
import org.datacite.schema.kernel_4.Resource.Formats;
import org.datacite.schema.kernel_4.Resource.GeoLocations;
import org.datacite.schema.kernel_4.Resource.GeoLocations.GeoLocation;
import org.datacite.schema.kernel_4.Resource.Identifier;
import org.datacite.schema.kernel_4.Resource.Publisher;
import org.datacite.schema.kernel_4.Resource.RelatedIdentifiers;
import org.datacite.schema.kernel_4.Resource.ResourceType;
import org.datacite.schema.kernel_4.Resource.RightsList;
import org.datacite.schema.kernel_4.Resource.RightsList.Rights;
import org.datacite.schema.kernel_4.Resource.Sizes;
import org.datacite.schema.kernel_4.Resource.Subjects;
import org.datacite.schema.kernel_4.Resource.Subjects.Subject;
import org.datacite.schema.kernel_4.Resource.Titles;
import org.datacite.schema.kernel_4.Resource.Titles.Title;
import org.datacite.schema.kernel_4.TitleType;

import au.edu.anu.datacommons.xml.data.Data;
import au.edu.anu.datacommons.xml.data.DataItem;

/**
 * This class accepts a Data item and creates a new {@link Resource} object with the details of the record. The Resource
 * object can then be marshalled into XML as per DataCite's schema used in DOI requests.
 * 
 * @author Rahul Khanna
 * 
 */
public class DoiResourceAdapter {
	private Data sourceData;
	private Resource doiResource = new Resource();
	
	private static final Map<String, LicenceTypeForDOI> licenceTypes;
	
	static {
		Map<String, LicenceTypeForDOI> aMap = new HashMap<String, LicenceTypeForDOI>();
		aMap.put("CC-BY", new LicenceTypeForDOI("CC BY","http://creativecommons.org/licenses/by/3.0/au/deed.en","Creative Commons Attribution 3.0 International"));
		aMap.put("CC-BY-4_0", new LicenceTypeForDOI("CC BY","http://creativecommons.org/licenses/by/4.0/","Creative Commons Attribution 4.0 International"));
		aMap.put("CC BY-SA", new LicenceTypeForDOI("CC BY-SA","http://creativecommons.org/licenses/by-sa/3.0/au/deed.en","Creative Commons Share-Alike 3.0 International"));
		aMap.put("CC BY-SA-4_0", new LicenceTypeForDOI("CC BY-SA","http://creativecommons.org/licenses/by-sa/4.0/","Creative Commons Share-Alike 4.0 International"));
		aMap.put("CC BY-ND", new LicenceTypeForDOI("CC BY-ND","http://creativecommons.org/licenses/by-nd/3.0/au/deed.en","Creative Commons No Derivatives 3.0 International"));
		aMap.put("CC BY-ND-4_0", new LicenceTypeForDOI("CC BY-ND","http://creativecommons.org/licenses/by-nd/4.0/","Creative Commons No Derivatives 4.0 International"));
		aMap.put("CC BY-NC", new LicenceTypeForDOI("CC BY-NC","http://creativecommons.org/licenses/by-nc/3.0/au/deed.en","Creative Commons Non Commercial 3.0 International"));
		aMap.put("CC BY-NC-4_0", new LicenceTypeForDOI("CC BY-NC","http://creativecommons.org/licenses/by-nc/4.0/","Creative Commons Non Commercial 4.0 International"));
		aMap.put("CC BY-NC-SA", new LicenceTypeForDOI("CC BY-NC-SA","http://creativecommons.org/licenses/by-nc-sa/3.0/au/deed.en","Creative Commons Non Commercial-Share Alike 3.0 International"));
		aMap.put("CC BY-NC-SA-4_0", new LicenceTypeForDOI("CC BY-NC-SA","http://creativecommons.org/licenses/by-nc-sa/4.0/","Creative Commons Non Commercial-Share Alike 4.0 International"));
		aMap.put("CC BY-NC-ND", new LicenceTypeForDOI("CC BY-NC-ND","http://creativecommons.org/licenses/by-nc-nd/3.0/au/deed.en","Creative Commons Non Commercial-No Dervatives 3.0 International"));
		aMap.put("CC BY-NC-ND-4_0", new LicenceTypeForDOI("CC BY-NC-ND","http://creativecommons.org/licenses/by-nc-nd/4.0/","reative Commons Non Commercial-No Dervatives 4.0 International"));
		aMap.put("GPL", new LicenceTypeForDOI(null,"http://www.gnu.org/licenses/gpl.html","GNU General Public License"));
		aMap.put("AusGoalRestrictive", new LicenceTypeForDOI(null,null,"AUSGoal Restrictive"));
		aMap.put("NoLicence", new LicenceTypeForDOI(null,null,"No Licence"));
		aMap.put("Unknown/Other", new LicenceTypeForDOI(null,null,"Other"));
		licenceTypes = Collections.unmodifiableMap(aMap);
	}
//	
//	private static final Map<String, LicenceTypeForDOI> licenceTypes = new HashMap<String, LicenceTypeForDOI>() {
//		
//	};

	/**
	 * Constructor specifying the Data object to adapt into a Resource object.
	 * 
	 * @param sourceData
	 *            Data object to read metadata values of a record from.
	 * @throws DoiException
	 *             when unable to generate a Resource object.
	 * 
	 */
	public DoiResourceAdapter(Data sourceData) {
		this.sourceData = sourceData;
	}

	/**
	 * Returns the generated Resource object containing values read from the Data object for use in DOI requests.
	 * 
	 * @return Resource object containing metadata values of a record
	 * @throws DoiException
	 */
	public Resource createDoiResource() throws DoiException {
		generateResource();
		return this.doiResource;
	}

	/**
	 * Generates a Resource object from a specified Data object.
	 * 
	 * @param itemData
	 *            Data object to read metadata values of a record from.
	 * 
	 * @throws DoiException
	 *             When at least one creator and one title, a publisher and publication year has not been specified in
	 *             the record.
	 */
	private void generateResource() throws DoiException {
		// Mandatory fields.
		
		Creators creators = getCreators();
		if (creators == null) {
			throw new DoiException(
					"No creators provided. Creators, titles, collection type, publisher and publication year are required for a DOI to be minted.");
		}
		doiResource.setCreators(creators);

		Titles titles = getTitles();
		if (titles == null) {
			throw new DoiException(
					"No titles provided. Creators, titles, collection type, publisher and publication year are required for a DOI to be minted.");
		}
		doiResource.setTitles(titles);

		Publisher publisher = getPublisher();
		if (publisher == null) {
			throw new DoiException(
					"No publisher provided. Creators, titles, collection type, publisher and publication year are required for a DOI to be minted.");
		}
		doiResource.setPublisher(publisher);

		String publicationYear = getPublicationYear();
		if (publicationYear == null) {
			throw new DoiException(
					"No publication year provided. Creators, collection type, titles, publisher and publication year are required for a DOI to be minted.");
		}
		doiResource.setPublicationYear(publicationYear);

		ResourceType resourceType = getResourceType();
		if (resourceType == null) {
			throw new DoiException("No resource sub type provided. Creators, collection type, titles, publisher and publication year are required for a DOI to be minted.");
		}
		
		// Optional fields.
		doiResource.setSubjects(getSubjects());
		doiResource.setContributors(getContributors());
		doiResource.setDates(getDates());
		doiResource.setLanguage(getLanguage());
		doiResource.setResourceType(getResourceType());
		doiResource.setIdentifier(getIdentifier());
		doiResource.setAlternateIdentifiers(getAlternateIdentifiers());
		doiResource.setRelatedIdentifiers(getRelatedIdentifiers());
		doiResource.setSizes(getSizes());
		doiResource.setFormats(getFormats());
		doiResource.setVersion(getVersion());
		doiResource.setRightsList(getRights());
		doiResource.setDescriptions(getDescriptions());
		doiResource.setGeoLocations(getGeoLocations());
	}

	/**
	 * Returns a Creators object consisting of a list of Creator objects created from the specified Data object.
	 * 
	 * @param itemData
	 *            Data object containing a record's metadata.
	 * @return Creators object containing a List of Creator objects
	 */
	private Creators getCreators() {
		Creators creators = null;
		List<Creator> list = new ArrayList<Creator>();
		List<DataItem> dataItems = sourceData.getElementByName("citCreator");
		for (DataItem item : dataItems) {
			if (item.getChildValues() != null && item.getChildValues().size() > 0) {
				Creator c = new Creator();
				String creatorGiven = null;
				String creatorSurname = null;
				for (DataItem childItem : item.getChildValues()) {
					if ("citCreatorGiven".equals(childItem.getName())) {
						creatorGiven = childItem.getValue();
					}
					else if ("citCreatorSurname".equals(childItem.getName())) {
						creatorSurname = childItem.getValue();
					}
				}
				if (creatorGiven != null && creatorSurname != null) {
					CreatorName creatorName = new CreatorName();
					creatorName.setNameType(NameType.PERSONAL);
					String name = creatorSurname + ", " + creatorGiven;
					creatorName.setValue(name);
					c.setCreatorName(creatorName);
				}
				else if (creatorGiven != null) {
					CreatorName name = new CreatorName();
					name.setNameType(NameType.ORGANIZATIONAL);
					name.setValue(creatorGiven);
					c.setCreatorName(name);
				}
				else if (creatorSurname != null) {
					CreatorName name = new CreatorName();
					name.setNameType(NameType.ORGANIZATIONAL);
					name.setValue(creatorSurname);
					c.setCreatorName(name);
				}
				list.add(c);
			}
		}
		if (!list.isEmpty()) {
			creators = new Creators();
			creators.getCreator().addAll(list);
		}
		return creators;
	}

	/**
	 * Returns a Titles object consisting of a list of Title objects from the specified Data object.
	 * 
	 * @param itemData
	 *            Data object to read title details from
	 * @return Titles object containing a List of Title objects.
	 */
	private Titles getTitles() {
		Titles titles = null;
		List<Title> list = new ArrayList<Title>();
		String titleStr = getValueOfFirstElementByNameFromSource("name");
		if (titleStr != null) {
			Title t = new Title();
			t.setValue(titleStr);
			list.add(t);
		}
		String altTitleStr = getValueOfFirstElementByNameFromSource("altName");
		if (altTitleStr != null) {
			Title t = new Title();
			t.setValue(altTitleStr);
			t.setTitleType(TitleType.ALTERNATIVE_TITLE);
			list.add(t);
		}
		if (list.size() > 0) {
			titles = new Titles();
			titles.getTitle().addAll(list);
		}
		return titles;
	}

	/**
	 * Gets the publisher of the record from the Data object.
	 * 
	 * @param itemData
	 *            Data object from which the publisher value will be read
	 * @return Name of Publisher as String
	 */
	private Publisher getPublisher() {
		Publisher publisher = new Publisher();
		publisher.setValue(getValueOfFirstElementByNameFromSource("citationPublisher"));
		return publisher;
	}

	/**
	 * Gets the year of publication from the Data Object.
	 * 
	 * @param itemData
	 *            Data object from which the year of publication will be read
	 * @return Year of publication as String
	 */
	private String getPublicationYear() {
		return getValueOfFirstElementByNameFromSource("citationYear");
	}

	private Subjects getSubjects() {
		Subjects subjects = new Subjects();
		
		// TODO Implement.
		List<DataItem> anzforSubjects = sourceData.getElementByName("anzforSubject");
		for (DataItem item : anzforSubjects) {
			Subject subject = new Subject();
			subject.setSubjectScheme("ANZSRC Fields of Research");
			subject.setSchemeURI("https://www.abs.gov.au/statistics/classifications/australian-and-new-zealand-standard-research-classification-anzsrc");
			subject.setClassificationCode(item.getValue());
			subject.setValue(item.getDescription());
			subjects.getSubject().add(subject);
		}

		List<DataItem> keywords = sourceData.getElementByName("locSubject");
		for (DataItem item : keywords) {
			Subject subject = new Subject();
			subject.setValue(item.getValue());
			subjects.getSubject().add(subject);
		}
		
		if (subjects.getSubject().size() > 0) {
			return subjects;
		}
		return null;
	}

	private Contributors getContributors() {
		// TODO Implement.
		return null;
	}

	private Dates getDates() {
		Dates dates = new Dates();
		List<DataItem> coverageDates = sourceData.getElementByName("coverageDates");
		for (DataItem coverageDate : coverageDates) {
			List<DataItem> dateFroms = coverageDate.getChildElementByName("dateFrom");
			String dateFrom = dateFroms.get(0).getValue();
			List<DataItem> dateTos = coverageDate.getChildElementByName("dateTo");
			String dateTo = dateTos.get(0).getValue();
			Date date = new Date();
			date.setDateType(DateType.COVERAGE);
			date.setDateInformation(dateFrom+"/"+dateTo);
			dates.getDate().add(date);
		}
		if (dates.getDate().size() > 0) {
			return dates;
		}
		return null;
	}

	/**
	 * Gets the language of the metadata.
	 * 
	 * @return Language as String
	 */
	private String getLanguage() {
		return getValueOfFirstElementByNameFromSource("metaLang");
	}

	/**
	 * Maps the resource to one allowed by DataCite schema. Since DataCommons only recognises collections or datasets,
	 * only one of those two values will be returned.
	 * 
	 * @return ResourceType object
	 */
	private ResourceType getResourceType() {
		//TODO fix this so that the correct resource type generals are selected
		ResourceType resType = null;
		DataItem subTypeDI = this.sourceData.getFirstElementByName("subType");
		String subType = subTypeDI.getValue().toLowerCase();
		String subTypeDescription = subTypeDI.getDescription();
		if (subType.equals("dataset")) {
			resType = new ResourceType();
			resType.setResourceTypeGeneral(org.datacite.schema.kernel_4.ResourceType.DATASET);
			resType.setValue("Dataset");
		} else if (subType.equals("collection")) {
			resType = new ResourceType();
			resType.setResourceTypeGeneral(org.datacite.schema.kernel_4.ResourceType.COLLECTION);
			resType.setValue("Collection");
		}
		else if (subType.equals("software")) {
			resType = new ResourceType();
			resType.setResourceTypeGeneral(org.datacite.schema.kernel_4.ResourceType.SOFTWARE);
			resType.setValue("Software");
		}
		else {
			resType = new ResourceType();
			resType.setResourceTypeGeneral(org.datacite.schema.kernel_4.ResourceType.OTHER);
			if (subTypeDescription != null) {
				resType.setValue(subTypeDescription);
			}
			else {
				resType.setValue(subType);
			}
		}
		return resType;
	}

	/**
	 * Gets the DOI identifier if one exists
	 * 
	 * @return Identifier Object
	 */
	private Identifier getIdentifier() {
		Identifier identifier = null;
		String doiStr = getValueOfFirstElementByNameFromSource("doi");
		if (doiStr != null && doiStr.length() > 0) {
			identifier = new Identifier();
			identifier.setValue(doiStr);
			identifier.setIdentifierType("DOI");
		}
		return identifier;
	}

	private AlternateIdentifiers getAlternateIdentifiers() {
		// TODO Implement
		return null;
	}

	private RelatedIdentifiers getRelatedIdentifiers() {
		// TODO Implement
		return null;
	}

	private Sizes getSizes() {
		Sizes sizes = new Sizes();
		DataItem dataSize = sourceData.getFirstElementByName("dataSize");
		if (dataSize != null) {
			sizes.getSize().add(dataSize.getValue());
		}
		
		DataItem dataExtent = sourceData.getFirstElementByName("dataExtent");
		if (dataSize != null) {
			sizes.getSize().add(dataExtent.getValue() + " files");
		}
		
		if (sizes.getSize().size() > 0) {
			return sizes;
		}
		return null;
	}

	private Formats getFormats() {
		// TODO Implement
		return null;
	}

	private String getVersion() {
		// TODO Implement
		return null;
	}

	private RightsList getRights() {
		RightsList rightsList = new RightsList();

		DataItem licenceTypeDataItem = sourceData.getFirstElementByName("licenceType");
		String value = licenceTypeDataItem.getValue();
		LicenceTypeForDOI licenceType =  licenceTypes.get(value);
		if (licenceType != null) {
			rightsList.getRights().add(licenceType.getRight());
			return rightsList;
		}
		DataItem licence = sourceData.getFirstElementByName("licence");
		if(licence != null) {
			Rights right = new Rights();
			right.setValue(licence.getValue());
			rightsList.getRights().add(right);
		}
		DataItem accessRightsType = sourceData.getFirstElementByName("accessRightsType");
		if(accessRightsType != null) {
			Rights right = new Rights();
			right.setValue(accessRightsType.getDescription());
			rightsList.getRights().add(right);
		}
		DataItem accessRights = sourceData.getFirstElementByName("accessRights");
		if(accessRights != null) {
			Rights right = new Rights();
			right.setValue(accessRights.getValue());
			rightsList.getRights().add(right);
		}
		if (rightsList.getRights().size() > 0) {
			return rightsList;
		}
		return null;
	}

	/**
	 * Gets the brief and full descriptions, if exists.
	 * 
	 * @return Descriptions object
	 */
	private Descriptions getDescriptions() {
		Descriptions descriptions = null;
		List<Description> list = new ArrayList<Description>();
		String bDesc = getValueOfFirstElementByNameFromSource("briefDesc");
		if (bDesc != null) {
			Description d = new Description();
			d.getContent().add(bDesc);
			d.setDescriptionType(DescriptionType.OTHER);
			list.add(d);
		}
		String fDesc = getValueOfFirstElementByNameFromSource("fullDesc");
		if (fDesc != null) {
			Description d = new Description();
			d.getContent().add(fDesc);
			d.setDescriptionType(DescriptionType.ABSTRACT);
			list.add(d);
		}
		if (list.size() > 0) {
			descriptions = new Descriptions();
			descriptions.getDescription().addAll(list);
		}
		return descriptions;
	}
	
	private GeoLocations getGeoLocations() {
//		GeoLocations geoLocations = new GeoLocations();
//		GeoLocation geoLocation = new GeoLocation();
//		GeoLocation geoLocation = geoLocations.getGeoLocation().get(0);
//		sourceData.getElementByName("");
//		geoLocation.getGeoLocationPlaceOrGeoLocationPointOrGeoLocationBox()
		
		return null;
	}

	/**
	 * Gets the value of the first element found with the specified name.
	 * 
	 * @param elementName
	 *            Element whose value is requested
	 * @return value as String
	 */
	private String getValueOfFirstElementByNameFromSource(String elementName) {
		String value = null;
		DataItem di = this.sourceData.getFirstElementByName(elementName);
		if (di != null && di.getValue().length() > 0) {
			value = di.getValue();
		}
		return value;
	}
}
