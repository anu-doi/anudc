package au.edu.anu.datacommons.webservice.bindings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType
public class Collection implements FedoraItem
{
	private String pid;
	private String template;
	private final String type = "collection";

	// General
	private String title;
	private String briefTitle;
	private String altTitle;
	private String subType;
	private String ownerGroup;
	private String metadataLanguage;
	private String dataLanguage;

	// Coverage
	private List<DateCoverage> dateCoverage;
	private List<String> coverageDateTextList;
	private List<GeospatialLocation> geospatialLocations;
	
	// Description
	private String significanceStatement;
	private String briefDesc;
	private String fullDesc;
	private String citationType;
	private String citationText;
	private List<Publication> publications;
	private List<RelatedWebsites> related;

	// People
	private List<String> emails;
	private String contactAddress;
	private List<String> phones;
	private List<String> faxes;
	private List<String> websites;
	private List<String> creators;
	private List<String> supervisors;
	private List<String> collaborators;
	
	// Subject
	private List<String> anzForCodes;
	private List<String> anzSeoCodes;
	private List<String> keywords;
	private String researchType;

	// Rights
	private String accessRights;
	private String rightsStatement;
	private String licenceType;
	private String licence;
	
	// Management
	private String dataLocation;
	private String retentionPeriod;
	private String disposalDate;
	private String dataExtent;
	private String dataSize;
	private Boolean dataMgmtPlan;
	
	// Data files
	private List<Link> fileUrlList;
	
	// General

	@XmlElement(name = "name")
	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}
	
	@XmlElement(name = "abbrName")
	public String getBriefTitle()
	{
		return briefTitle;
	}

	public void setBriefTitle(String briefTitle)
	{
		this.briefTitle = briefTitle;
	}
	
	@XmlElement(name = "altName")
	public String getAltTitle()
	{
		return altTitle;
	}

	public void setAltTitle(String altTitle)
	{
		this.altTitle = altTitle;
	}
	
	@XmlElement(name = "subType")
	public String getSubType()
	{
		return subType;
	}

	public void setSubType(String subType)
	{
		this.subType = subType;
	}

	@XmlElement(name = "ownerGroup")
	public String getOwnerGroup()
	{
		return ownerGroup;
	}

	public void setOwnerGroup(String ownerGroup)
	{
		this.ownerGroup = ownerGroup;
	}

	@XmlElement(name = "metaLang")
	public String getMetadataLanguage()
	{
		return metadataLanguage;
	}

	public void setMetadataLanguage(String metadataLanguage)
	{
		this.metadataLanguage = metadataLanguage;
	}

	@XmlElement(name = "dataLang")
	public String getDataLanguage()
	{
		return dataLanguage;
	}

	public void setDataLanguage(String dataLanguage)
	{
		this.dataLanguage = dataLanguage;
	}
	
	// Coverage

	@XmlElement(name = "coverageDates")
	public List<DateCoverage> getDateCoverage()
	{
		return dateCoverage;
	}

	public void setDateCoverage(List<DateCoverage> dateCoverage)
	{
		this.dateCoverage = dateCoverage;
	}
	
	@XmlElement(name = "coverageDateText")
	public List<String> getCoverageDateTextList()
	{
		return coverageDateTextList;
	}

	public void setCoverageDateTextList(List<String> coverageDateTextList)
	{
		this.coverageDateTextList = coverageDateTextList;
	}
	
	@XmlElement(name = "coverageArea")
	public List<GeospatialLocation> getGeospatialLocations()
	{
		return geospatialLocations;
	}

	public void setGeospatialLocations(List<GeospatialLocation> geospatialLocations)
	{
		this.geospatialLocations = geospatialLocations;
	}
	
	// Description
	
	@XmlElement(name = "significanceStatement")
	public String getSignificanceStatement()
	{
		return significanceStatement;
	}

	public void setSignificanceStatement(String significanceStatement)
	{
		this.significanceStatement = significanceStatement;
	}

	
	@XmlElement(name = "briefDesc")
	public String getBriefDesc()
	{
		return briefDesc;
	}

	public void setBriefDesc(String briefDesc)
	{
		this.briefDesc = briefDesc;
	}

	@XmlElement(name = "fullDesc")
	public String getFullDesc()
	{
		return fullDesc;
	}

	public void setFullDesc(String fullDesc)
	{
		this.fullDesc = fullDesc;
	}
	
	@XmlElement(name = "fullCitationType")
	public String getCitationType()
	{
		return citationType;
	}

	public void setCitationType(String citationType)
	{
		this.citationType = citationType;
	}

	@XmlElement(name = "fullCitation")
	public String getCitationText()
	{
		return citationText;
	}

	public void setCitationText(String citationText)
	{
		this.citationText = citationText;
	}

	@XmlElement(name = "publication")
	public List<Publication> getPublications()
	{
		return publications;
	}
	
	public void setPublications(List<Publication> publications)
	{
		this.publications = publications;
	}

	@XmlElement(name = "relatedWebsites")
	public List<RelatedWebsites> getRelated()
	{
		return related;
	}

	public void setRelated(List<RelatedWebsites> related)
	{
		this.related = related;
	}
	
	// People
	
	@XmlElement(name = "email")
	public List<String> getEmails()
	{
		return emails;
	}

	public void setEmails(List<String> emails)
	{
		this.emails = emails;
	}

	@XmlElement(name = "postalAddress")
	public String getContactAddress()
	{
		return contactAddress;
	}

	public void setContactAddress(String contactAddress)
	{
		this.contactAddress = contactAddress;
	}

	@XmlElement(name = "phone")
	public List<String> getPhones()
	{
		return phones;
	}

	public void setPhones(List<String> phones)
	{
		this.phones = phones;
	}

	@XmlElement(name = "fax")
	public List<String> getFaxes()
	{
		return faxes;
	}

	public void setFaxes(List<String> faxes)
	{
		this.faxes = faxes;
	}

	@XmlElement(name = "websiteAddress")
	public List<String> getWebsites()
	{
		return websites;
	}

	public void setWebsites(List<String> websites)
	{
		this.websites = websites;
	}

	@XmlElement(name = "creator")
	public List<String> getCreators()
	{
		return creators;
	}

	public void setCreators(List<String> creators)
	{
		this.creators = creators;
	}

	@XmlElement(name = "supervisor")
	public List<String> getSupervisors()
	{
		return supervisors;
	}

	public void setSupervisors(List<String> supervisors)
	{
		this.supervisors = supervisors;
	}
	
	@XmlElement(name = "collaborator")
	public List<String> getCollaborators()
	{
		return collaborators;
	}

	public void setCollaborators(List<String> collaborators)
	{
		this.collaborators = collaborators;
	}
	
	// Subject
	
	@XmlElement(name = "anzforSubject")
	public List<String> getAnzForCodes()
	{
		return anzForCodes;
	}

	public void setAnzForCodes(List<String> anzForCodes)
	{
		this.anzForCodes = anzForCodes;
	}

	@XmlElement(name = "anzseoSubject")
	public List<String> getAnzSeoCodes()
	{
		return anzSeoCodes;
	}

	public void setAnzSeoCodes(List<String> anzSeoCodes)
	{
		this.anzSeoCodes = anzSeoCodes;
	}

	@XmlElement(name = "locSubject")
	public List<String> getKeywords()
	{
		return keywords;
	}
	
	public void setKeywords(List<String> keywords)
	{
		this.keywords = keywords;
	}
	
	@XmlElement(name = "anztoaSubject")
	public String getResearchType()
	{
		return researchType;
	}

	public void setResearchType(String researchType)
	{
		this.researchType = researchType;
	}

	// Rights
	
	@XmlElement(name = "accessRights")
	public String getAccessRights()
	{
		return accessRights;
	}
	
	public void setAccessRights(String accessRights)
	{
		this.accessRights = accessRights;
	}
	
	@XmlElement(name = "rightsStatement")
	public String getRightsStatement()
	{
		return rightsStatement;
	}
	
	public void setRightsStatement(String rightsStatement)
	{
		this.rightsStatement = rightsStatement;
	}
	
	@XmlElement(name = "licenceType")
	public String getLicenceType()
	{
		return licenceType;
	}
	
	public void setLicenceType(String licenceType)
	{
		this.licenceType = licenceType;
	}
	
	@XmlElement(name = "licence")
	public String getLicence()
	{
		return licence;
	}
	
	public void setLicence(String licence)
	{
		this.licence = licence;
	}
	
	// Management

	@XmlElement(name = "dataLocation")
	public String getDataLocation()
	{
		return dataLocation;
	}

	public void setDataLocation(String dataLocation)
	{
		this.dataLocation = dataLocation;
	}

	@XmlElement(name = "dataRetention")
	public String getRetentionPeriod()
	{
		return retentionPeriod;
	}

	public void setRetentionPeriod(String retentionPeriod)
	{
		this.retentionPeriod = retentionPeriod;
	}

	@XmlElement(name = "disposalDate")
	public String getDisposalDate()
	{
		return disposalDate;
	}

	public void setDisposalDate(String disposalDate)
	{
		this.disposalDate = disposalDate;
	}

	@XmlElement(name = "dataExtent")
	public String getDataExtent()
	{
		return dataExtent;
	}

	public void setDataExtent(String dataExtent)
	{
		this.dataExtent = dataExtent;
	}

	@XmlElement(name = "dataSize")
	public String getDataSize()
	{
		return dataSize;
	}

	public void setDataSize(String dataSize)
	{
		this.dataSize = dataSize;
	}

	@XmlElement(name = "dataMgmtPlan")
	public Boolean getDataMgmtPlan()
	{
		return dataMgmtPlan;
	}

	public void setDataMgmtPlan(Boolean dataMgmtPlan)
	{
		this.dataMgmtPlan = dataMgmtPlan;
	}
	
	@XmlElement(name = "link")
	public List<Link> getFileUrlList()
	{
		return fileUrlList;
	}

	public void setFileUrlList(List<Link> fileUrlList)
	{
		this.fileUrlList = fileUrlList;
	}

	@Override
	@XmlAttribute(name = "tmplt")
	public String getTemplate()
	{
		return template;
	}
	
	@Override
	public void setTemplate(String template)
	{
		this.template = template;
	}

	@Override
	@XmlElement(name = "pid")
	public String getPid()
	{
		return pid;
	}

	@Override
	public void setPid(String pid)
	{
		this.pid = pid;
	}
	
	@XmlElement(name = "type")
	public String getType()
	{
		return type;
	}

	@Override
	public Map<String, List<String>> generateDataMap()
	{
		Map<String, List<String>> data = new HashMap<String, List<String>>();
		
		data.put("type", Arrays.asList(this.getType()));
		if (this.getTitle() != null)
			data.put("name", new ArrayList<String>(Arrays.asList(this.getTitle())));
		if (this.getBriefTitle() != null)
			data.put("abbrName", new ArrayList<String>(Arrays.asList(this.getBriefTitle())));
		if (this.getAltTitle() != null)
			data.put("altName", new ArrayList<String>(Arrays.asList(this.getAltTitle())));
		if (this.getSubType() != null)
			data.put("subType", new ArrayList<String>(Arrays.asList(this.getSubType())));
		if (this.getOwnerGroup() != null)
			data.put("ownerGroup", new ArrayList<String>(Arrays.asList(this.getOwnerGroup())));
		if (this.getMetadataLanguage() != null)
			data.put("metaLang", new ArrayList<String>(Arrays.asList(this.getMetadataLanguage())));
		if (this.getDataLanguage() != null)
			data.put("dataLang", new ArrayList<String>(Arrays.asList(this.getDataLanguage())));
		
		if (this.getDateCoverage() != null && this.getDateCoverage().size() > 0)
		{
			ArrayList<String> datesFrom = new ArrayList<String>();
			ArrayList<String> datesTo = new ArrayList<String>();
			for (DateCoverage iDc : this.getDateCoverage())
			{
				datesFrom.add(iDc.getDateFrom());
				datesTo.add(iDc.getDateTo());
			}
			data.put("dateFrom", datesFrom);
			data.put("dateTo", datesTo);
		}
		if (this.getCoverageDateTextList() != null && this.getCoverageDateTextList().size() > 0)
			data.put("coverageDateText", this.getCoverageDateTextList());
		if (this.getGeospatialLocations() != null && this.getGeospatialLocations().size() > 0)
		{
			ArrayList<String> covTypes = new ArrayList<String>();
			ArrayList<String> covVals = new ArrayList<String>();
			for (GeospatialLocation iGl : this.getGeospatialLocations())
			{
				covTypes.add(iGl.getCovAreaType());
				covVals.add(iGl.getCovAreaValue());
			}
			data.put("covAreaType", covTypes);
			data.put("covAreaValue", covVals);
		}
		if (this.getSignificanceStatement() != null)
			data.put("significanceStatement", new ArrayList<String>(Arrays.asList(this.getSignificanceStatement())));
		if (this.getBriefDesc() != null)
			data.put("briefDesc", new ArrayList<String>(Arrays.asList(this.getBriefDesc())));
		if (this.getFullDesc() != null)
			data.put("fullDesc", new ArrayList<String>(Arrays.asList(this.getFullDesc())));
		if (this.getCitationType() != null)
			data.put("fullCitationType", new ArrayList<String>(Arrays.asList(this.getCitationType())));
		if (this.getCitationText() != null)
			data.put("fullCitation", new ArrayList<String>(Arrays.asList(this.getCitationText())));
		
		if (this.getPublications() != null)
		{
			ArrayList<String> pubTypeList = new ArrayList<String>();
			ArrayList<String> pubValueList = new ArrayList<String>();
			ArrayList<String> pubTitleList = new ArrayList<String>();
			for (Publication iPub : this.getPublications())
			{
				pubTypeList.add(iPub.getIdType());
				pubValueList.add(iPub.getId());
				pubTitleList.add(iPub.getTitle());
			}
			data.put("pubType", pubTypeList);
			data.put("pubValue", pubValueList);
			data.put("pubTitle", pubTitleList);
		}

		if (this.getRelated() != null)
		{
			ArrayList<String> relatedWebUrlList = new ArrayList<String>();
			ArrayList<String> relatedWebTitleList = new ArrayList<String>();
			for (RelatedWebsites iRelated : this.getRelated())
			{
				relatedWebUrlList.add(iRelated.getRelatedWebUrl());
				relatedWebTitleList.add(iRelated.getRelatedWebTitle());
			}
			data.put("relatedWebURL", relatedWebUrlList);
			data.put("relatedWebTitle", relatedWebTitleList);
		}

		if (this.getEmails() != null && this.getEmails().size() > 0)
			data.put("email", this.getEmails());
		if (this.getContactAddress() != null)
			data.put("postalAddress", new ArrayList<String>(Arrays.asList(this.getContactAddress())));
		if (this.getPhones() != null)
			data.put("phone", this.getPhones());
		if (this.getFaxes() != null)
			data.put("fax", this.getFaxes());
		if (this.getWebsites() != null)
			data.put("websiteAddress", this.getWebsites());
		if (this.getCreators() != null)
			data.put("creator", this.getCreators());
		if (this.getSupervisors() != null)
			data.put("supervisor", this.getSupervisors());
		if (this.getCollaborators() != null)
			data.put("collaborator", this.getCollaborators());
		
		if (this.getAnzForCodes() != null)
			data.put("anzforSubject", this.getAnzForCodes());
		if (this.getAnzSeoCodes() != null)
			data.put("anzseoSubject", this.getAnzSeoCodes());
		if (this.getKeywords() != null)
			data.put("locSubject", this.getKeywords());
		if (this.getResearchType() != null)
			data.put("anztoaSubject", new ArrayList<String>(Arrays.asList(this.getResearchType())));
		
		if (this.getAccessRights() != null)
			data.put("accessRights", new ArrayList<String>(Arrays.asList(this.getAccessRights())));
		if (this.getRightsStatement() != null)
			data.put("rightsStatement", new ArrayList<String>(Arrays.asList(this.getRightsStatement())));
		if (this.getLicenceType() != null)
			data.put("licenceType", new ArrayList<String>(Arrays.asList(this.getLicenceType())));
		if (this.getLicence() != null)
			data.put("licence", new ArrayList<String>(Arrays.asList(this.getLicence())));
		
		if (this.getDataLocation() != null)
			data.put("dataLocation", new ArrayList<String>(Arrays.asList(this.getDataLocation())));
		if (this.getRetentionPeriod() != null)
			data.put("dataRetention", new ArrayList<String>(Arrays.asList(this.getRetentionPeriod())));
		if (this.getDisposalDate() != null)
			data.put("disposalDate", new ArrayList<String>(Arrays.asList(this.getDisposalDate())));
		if (this.getDataExtent() != null)
			data.put("dataExtent", new ArrayList<String>(Arrays.asList(this.getDataExtent())));
		if (this.getDataSize() != null)
			data.put("dataSize", new ArrayList<String>(Arrays.asList(this.getDataSize())));
		if (this.getDataMgmtPlan() != null)
			data.put("dataMgmtPlan", new ArrayList<String>(Arrays.asList(this.getDataMgmtPlan().toString())));
		
		return data;
	}
	
}
