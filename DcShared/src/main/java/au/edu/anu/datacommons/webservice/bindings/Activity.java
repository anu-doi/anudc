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

package au.edu.anu.datacommons.webservice.bindings;

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

/**
 * This class represents an Activity record and contains metadata information of that record. This class can then be marshalled and unmarshalled to and from an
 * XML document as required for Data Commons Web Service. The class marshalls into the following XML element:
 * 
 * <pre>
 * {@code
 * <activity tmplt="...">
 * 	<abbrName>...</abbrName>
 * 	<altName>...</altName>
 * 	<anzforSubject>...</anzforSubject>
 * 	<anzforSubject>...</anzforSubject>
 * 	<anzseoSubject>...</anzseoSubject>
 * 	<anzseoSubject>...</anzseoSubject>
 * 	<arcNumber>...</arcNumber>
 * 	<briefDesc>...</briefDesc>
 * 	<postalAddress>...</postalAddress>
 * 	<email>...</email>
 * 	<email>...</email>
 * 	<fax>...</fax>
 * 	<fax>...</fax>
 * 	<fullDesc>...</fullDesc>
 * 	<fundingBody>...</fundingBody>
 * 	<locSubject>...</locSubject>
 * 	<locSubject>...</locSubject>
 * 	<ownerGroup>...</ownerGroup>
 * 	<phone>...</phone>
 * 	<phone>...</phone>
 * 	<publication>
 * 		<pubValue>...</pubValue>
 * 		<pubType>...</pubType>
 * 		<pubTitle>...</pubTitle>
 * 	</publication>
 * 	<relatedWebsites>
 * 		<relatedWebTitle>...</relatedWebTitle>
 * 		<relatedWebURL>...</relatedWebURL>
 * 	</relatedWebsites>
 * 	<relatedWebsites>
 * 		<relatedWebTitle>...</relatedWebTitle>
 * 		<relatedWebURL>...</relatedWebURL>
 * 	</relatedWebsites>
 * 	<anztoaSubject>...</anztoaSubject>
 * 	<subType>...</subType>
 * 	<name>...</name>
 * 	<type>...</type>
 * 	<websiteAddress>...</websiteAddress>
 * 	<websiteAddress>...</websiteAddress>
 * </activity>
 * }
 * </pre>
 */
@XmlType
public class Activity implements FedoraItem
{
	private String pid;
	private String template;
	private final String type = "activity";

	private String subType;

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
	private List<String> extIds;

	// All elements not captured as its own field.
	private List<Element> nodes;

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

	/**
	 * Gets the sub type.
	 * 
	 * @return the sub type
	 */
	@XmlElement(name = "subType")
	public String getSubType()
	{
		return subType;
	}

	/**
	 * Sets the sub type.
	 * 
	 * @param subType
	 *            the new sub type
	 */
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

	@Override
	public void setTemplate(String template)
	{
		this.template = template;
	}

	@Override
	@XmlElement(name = "ownerGroup")
	public String getOwnerGroup()
	{
		return ownerGroup;
	}

	public void setOwnerGroup(String ownerGroup)
	{
		this.ownerGroup = ownerGroup;
	}

	/**
	 * Gets the title.
	 * 
	 * @return the title
	 */
	@XmlElement(name = "name")
	public String getTitle()
	{
		return title;
	}

	/**
	 * Sets the title.
	 * 
	 * @param title
	 *            the new title
	 */
	public void setTitle(String title)
	{
		this.title = title;
	}

	/**
	 * Gets the brief description.
	 * 
	 * @return the brief description
	 */
	@XmlElement(name = "briefDesc")
	public String getBriefDesc()
	{
		return briefDesc;
	}

	/**
	 * Sets the brief description.
	 * 
	 * @param briefDesc
	 *            the new brief description
	 */
	public void setBriefDesc(String briefDesc)
	{
		this.briefDesc = briefDesc;
	}

	/**
	 * Gets the emails.
	 * 
	 * @return the emails
	 */
	@XmlElement(name = "email")
	public List<String> getEmails()
	{
		return emails;
	}

	/**
	 * Sets the emails.
	 * 
	 * @param emails
	 *            the new emails
	 */
	public void setEmails(List<String> emails)
	{
		this.emails = emails;
	}

	/**
	 * Gets the ANZFOR codes.
	 * 
	 * @return the ANZFOR codes
	 */
	@XmlElement(name = "anzforSubject")
	public List<String> getAnzForCodes()
	{
		return anzForCodes;
	}

	/**
	 * Sets the ANZFOR codes.
	 * 
	 * @param anzForCodes
	 *            the new ANZFOR codes
	 */
	public void setAnzForCodes(List<String> anzForCodes)
	{
		this.anzForCodes = anzForCodes;
	}

	/**
	 * Gets the ANZSEO codes.
	 * 
	 * @return the ANZSEO codes
	 */
	@XmlElement(name = "anzseoSubject")
	public List<String> getAnzSeoCodes()
	{
		return anzSeoCodes;
	}

	/**
	 * Sets the ANZSEO codes.
	 * 
	 * @param anzSeoCodes
	 *            the new ANZSEO codes
	 */
	public void setAnzSeoCodes(List<String> anzSeoCodes)
	{
		this.anzSeoCodes = anzSeoCodes;
	}

	/**
	 * Gets the research type.
	 * 
	 * @return the research type
	 */
	@XmlElement(name = "anztoaSubject")
	public String getResearchType()
	{
		return researchType;
	}

	/**
	 * Sets the research type.
	 * 
	 * @param researchType
	 *            the new research type
	 */
	public void setResearchType(String researchType)
	{
		this.researchType = researchType;
	}

	/**
	 * Gets the keywords.
	 * 
	 * @return the keywords
	 */
	@XmlElement(name = "locSubject")
	public List<String> getKeywords()
	{
		return keywords;
	}

	/**
	 * Sets the keywords.
	 * 
	 * @param keywords
	 *            the new keywords
	 */
	public void setKeywords(List<String> keywords)
	{
		this.keywords = keywords;
	}

	/**
	 * Gets the abbreviated title.
	 * 
	 * @return the abbreviated title
	 */
	@XmlElement(name = "abbrName")
	public String getAbbrTitle()
	{
		return abbrTitle;
	}

	/**
	 * Sets the abbreviated title.
	 * 
	 * @param abbrTitle
	 *            the new abbreviated title
	 */
	public void setAbbrTitle(String abbrTitle)
	{
		this.abbrTitle = abbrTitle;
	}

	/**
	 * Gets the alternate title.
	 * 
	 * @return the alternate title
	 */
	@XmlElement(name = "altName")
	public String getAltTitle()
	{
		return altTitle;
	}

	/**
	 * Sets the alternate title.
	 * 
	 * @param altTitle
	 *            the new alternate title
	 */
	public void setAltTitle(String altTitle)
	{
		this.altTitle = altTitle;
	}

	/**
	 * Gets the arc number.
	 * 
	 * @return the arc number
	 */
	@XmlElement(name = "arcNumber")
	public String getArcNumber()
	{
		return arcNumber;
	}

	/**
	 * Sets the arc number.
	 *
	 * @param arcNumber the new arc number
	 */
	public void setArcNumber(String arcNumber)
	{
		this.arcNumber = arcNumber;
	}

	/**
	 * Gets the funding bodies.
	 * 
	 * @return the funding bodies
	 */
	@XmlElement(name = "fundingBody")
	public List<String> getFundingBodies()
	{
		return fundingBodies;
	}

	/**
	 * Sets the funding bodies.
	 * 
	 * @param fundingBodies
	 *            the new funding bodies
	 */
	public void setFundingBodies(List<String> fundingBodies)
	{
		this.fundingBodies = fundingBodies;
	}

	/**
	 * Gets the full desc.
	 * 
	 * @return the full desc
	 */
	@XmlElement(name = "fullDesc")
	public String getFullDesc()
	{
		return fullDesc;
	}

	/**
	 * Sets the full desc.
	 * 
	 * @param fullDesc
	 *            the new full desc
	 */
	public void setFullDesc(String fullDesc)
	{
		this.fullDesc = fullDesc;
	}

	/**
	 * Gets the contact address.
	 * 
	 * @return the contact address
	 */
	@XmlElement(name = "postalAddress")
	public String getContactAddress()
	{
		return contactAddress;
	}

	/**
	 * Sets the contact address.
	 * 
	 * @param contactAddress
	 *            the new contact address
	 */
	public void setContactAddress(String contactAddress)
	{
		this.contactAddress = contactAddress;
	}

	/**
	 * Gets the phones.
	 * 
	 * @return the phones
	 */
	@XmlElement(name = "phone")
	public List<String> getPhones()
	{
		return phones;
	}

	/**
	 * Sets the phones.
	 * 
	 * @param phones
	 *            the new phones
	 */
	public void setPhones(List<String> phones)
	{
		this.phones = phones;
	}

	/**
	 * Gets the faxes.
	 * 
	 * @return the faxes
	 */
	@XmlElement(name = "fax")
	public List<String> getFaxes()
	{
		return faxes;
	}

	/**
	 * Sets the faxes.
	 * 
	 * @param faxes
	 *            the new faxes
	 */
	public void setFaxes(List<String> faxes)
	{
		this.faxes = faxes;
	}

	/**
	 * Gets the websites.
	 * 
	 * @return the websites
	 */
	@XmlElement(name = "websiteAddress")
	public List<String> getWebsites()
	{
		return websites;
	}

	/**
	 * Sets the websites.
	 * 
	 * @param websites
	 *            the new websites
	 */
	public void setWebsites(List<String> websites)
	{
		this.websites = websites;
	}

	/**
	 * Gets the publications.
	 * 
	 * @return the publications
	 */
	@XmlElement(name = "publication")
	public List<Publication> getPublications()
	{
		return publications;
	}

	/**
	 * Sets the publications.
	 * 
	 * @param publications
	 *            the new publications
	 */
	public void setPublications(List<Publication> publications)
	{
		this.publications = publications;
	}

	/**
	 * Gets the related.
	 * 
	 * @return the related
	 */
	@XmlElement(name = "relatedWebsites")
	public List<RelatedWebsites> getRelated()
	{
		return related;
	}

	/**
	 * Sets the related.
	 * 
	 * @param related
	 *            the new related
	 */
	public void setRelated(List<RelatedWebsites> related)
	{
		this.related = related;
	}

	/**
	 * Gets the ext ids.
	 * 
	 * @return the ext ids
	 */
	@XmlElement(name = "externalId")
	public List<String> getExtIds()
	{
		return extIds;
	}

	/**
	 * Sets the ext ids.
	 * 
	 * @param extIds
	 *            the new ext ids
	 */
	public void setExtIds(List<String> extIds)
	{
		this.extIds = extIds;
	}

	/**
	 * Gets the nodes.
	 * 
	 * @return the nodes
	 */
	@XmlAnyElement()
	public List<Element> getNodes()
	{
		return nodes;
	}

	/**
	 * Sets the nodes.
	 * 
	 * @param nodes
	 *            the new nodes
	 */
	public void setNodes(List<Element> nodes)
	{
		this.nodes = nodes;
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

		if (this.getExtIds() != null && this.getExtIds().size() > 0)
			data.put("externalId", this.getExtIds());

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
