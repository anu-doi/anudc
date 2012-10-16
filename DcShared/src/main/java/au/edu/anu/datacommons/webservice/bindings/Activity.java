package au.edu.anu.datacommons.webservice.bindings;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.w3c.dom.Element;

@XmlType
public class Activity implements FedoraItem
{
	private String pid;

	private String subType;
	private String template;

	private String ownerGroup;
	private String title;
	private String abbrTitle;
	private String altTitle;
	private String arcNumber;
	private List<String> fundingBodies;

	private String briefDesc;
	private String fullDesc;

	private List<String> emails;
	private String contactAddress;
	private List<String> phones;
	private List<String> faxes;
	private List<String> websites;

	private List<String> anzForCodes;
	private List<String> anzSeoCodes;
	private List<String> keywords;
	private String researchType;

	private List<Publication> publications;
	private List<RelatedWebsites> related;

	// All elements not captured as its own field.
	private List<Element> nodes;

	@Override
	@XmlElement(name = "pid")
	public String getPid()
	{
		return pid;
	}

	public void setPid(String pid)
	{
		this.pid = pid;
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

	@Override
	@XmlAttribute(name = "tmplt")
	public String getTemplate()
	{
		return template;
	}

	public void setTemplate(String template)
	{
		this.template = template;
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

	@XmlElement(name = "name")
	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
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

	@XmlElement(name = "email")
	public List<String> getEmails()
	{
		return emails;
	}

	public void setEmails(List<String> emails)
	{
		this.emails = emails;
	}

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

	@XmlElement(name = "anztoaSubject")
	public String getResearchType()
	{
		return researchType;
	}

	public void setResearchType(String researchType)
	{
		this.researchType = researchType;
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

	@XmlElement(name = "abbrName")
	public String getAbbrTitle()
	{
		return abbrTitle;
	}

	public void setAbbrTitle(String abbrTitle)
	{
		this.abbrTitle = abbrTitle;
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

	@XmlElement(name = "arcNumber")
	public String getArcNumber()
	{
		return arcNumber;
	}

	public void setArcNumber(String arcNumber)
	{
		this.arcNumber = arcNumber;
	}

	@XmlElement(name = "fundingBody")
	public List<String> getFundingBodies()
	{
		return fundingBodies;
	}

	public void setFundingBodies(List<String> fundingBodies)
	{
		this.fundingBodies = fundingBodies;
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

	@XmlAnyElement()
	public List<Element> getNodes()
	{
		return nodes;
	}

	public void setNodes(List<Element> nodes)
	{
		this.nodes = nodes;
	}

	@Override
	public Map<String, List<String>> generateDataMap()
	{
		Map<String, List<String>> data = new HashMap<String, List<String>>();
		
		data.put("type", Arrays.asList("activity"));
		if (this.getSubType() != null)
			data.put("subType", new ArrayList<String>(Arrays.asList(this.getSubType())));
		if (this.getOwnerGroup() != null)
			data.put("ownerGroup", new ArrayList<String>(Arrays.asList(this.getOwnerGroup())));
		if (this.getTitle() != null)
			data.put("name", new ArrayList<String>(Arrays.asList(this.getTitle())));
		if (this.getAbbrTitle() != null)
			data.put("abbrName", new ArrayList<String>(Arrays.asList(this.getAbbrTitle())));
		if (this.getAltTitle() != null)
			data.put("altName", new ArrayList<String>(Arrays.asList(this.getAltTitle())));
		if (this.getArcNumber() != null)
			data.put("arcNumber", new ArrayList<String>(Arrays.asList(this.getArcNumber())));
		if (this.getFundingBodies() != null && this.getFundingBodies().size() > 0)
			data.put("fundingBody", this.getFundingBodies());

		if (this.getBriefDesc() != null)
			data.put("briefDesc", new ArrayList<String>(Arrays.asList(this.getBriefDesc())));
		if (this.getFullDesc() != null)
			data.put("fullDesc", new ArrayList<String>(Arrays.asList(this.getFullDesc())));

		if (this.getEmails() != null && this.getEmails().size() > 0)
			data.put("email", this.getEmails());
		if (this.getContactAddress() != null)
			data.put("postalAddress", new ArrayList<String>(Arrays.asList(this.getContactAddress())));
		if (this.getPhones() != null && this.getPhones().size() > 0)
			data.put("phone", this.getPhones());
		if (this.getFaxes() != null && this.getFaxes().size() > 0)
			data.put("fax", this.getFaxes());
		if (this.getWebsites() != null && this.getWebsites().size() > 0)
			data.put("websiteAddress", this.getWebsites());

		if (this.getAnzForCodes() != null && this.getAnzForCodes().size() > 0)
			data.put("anzforSubject", this.getAnzForCodes());
		if (this.getAnzSeoCodes() != null && this.getAnzSeoCodes().size() > 0)
			data.put("anzseoSubject", this.getAnzSeoCodes());
		if (this.getKeywords() != null && this.getKeywords().size() > 0)
			data.put("locSubject", this.getKeywords());
		if (this.getResearchType() != null)
			data.put("anztoaSubject", new ArrayList<String>(Arrays.asList(this.getResearchType())));

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

		return data;
	}

	@Override
	public String toString()
	{
		// TODO Implement.
		return super.toString();
	}
}
