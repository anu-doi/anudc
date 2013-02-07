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

package au.edu.anu.datacommons.ands.xml;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;

/**
 * CitationMetadata
 * 
 * Australian National University Data Commons
 * 
 * Class for the citationMetadata element in the ANDS RIF-CS schema
 *
 * JUnit Coverage:
 * RegistryObjectsTest
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		12/10/2012	Genevieve Turner (GT)	Initial
 * 0.2		04/12/2012	Genevieve Turner (GT)	Updated to comply with rif-cs version 1.4 including the removal of the edition methods
 * </pre>
 *
 */
public class CitationMetadata {
	private Identifier identifier;
	private List<Contributor> contributors;
	private String title;
	private String version;
	private String publisher;
	private String placePublished;
	private List<ANDSDate> dates;
	private String url;
	private String context;

	/**
	 * Constructor
	 * 
	 * Constructor class for Citation Metadata
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		12/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 *
	 */
	public CitationMetadata() {
		contributors = new ArrayList<Contributor>();
		dates = new ArrayList<ANDSDate>();
	}
	
	/**
	 * getIdentifier
	 *
	 * Get the identifier
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		12/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the identifier
	 */
	@Valid
	@NotNull(message="Citation metadata requires an identifier")
	@XmlElement(namespace=Constants.ANDS_RIF_CS_NS)
	public Identifier getIdentifier() {
		return identifier;
	}
	
	/**
	 * setIdentifier
	 *
	 * Set the identifier
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		12/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param identifier the identifier to set
	 */
	public void setIdentifier(Identifier identifier) {
		this.identifier = identifier;
	}

	/**
	 * getContributors
	 *
	 * Get the contributors
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the contributors
	 */
	@Valid
	@Size(min=1, message="Citation Metadata requires at least one contributor")
	@XmlElement(name="contributor", namespace=Constants.ANDS_RIF_CS_NS)
	public List<Contributor> getContributors() {
		return contributors;
	}

	/**
	 * setContributors
	 *
	 * Set the contributors
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param contributors the contributors to set
	 */
	public void setContributors(List<Contributor> contributors) {
		this.contributors = contributors;
	}

	/**
	 * getTitle
	 *
	 * Get the title
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the title
	 */
	@NotNull(message="Citation metadata requires a contributor")
	@XmlElement(namespace=Constants.ANDS_RIF_CS_NS)
	public String getTitle() {
		return title;
	}

	/**
	 * setTitle
	 *
	 * Set the title
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * getVersion
	 *
	 * Get the edition
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		04/12/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the version
	 */
	@XmlElement(name="version", namespace=Constants.ANDS_RIF_CS_NS)
	public String getVersion() {
		return version;
	}

	/**
	 * setEdition
	 *
	 * Set the edition
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * getPublisher
	 *
	 * Get the publisher
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * 0.2		04/12/2012	Genevieve Turner (GT)	Updated to comply with rif-cs version 1.4
	 * </pre>
	 * 
	 * @return the publisher
	 */
	@NotNull(message="Citation metadata requires a publisher")
	@XmlElement(namespace=Constants.ANDS_RIF_CS_NS)
	public String getPublisher() {
		return publisher;
	}

	/**
	 * setPublisher
	 *
	 * Set the publisher
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param publisher the publisher to set
	 */
	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	/**
	 * getPlacePublished
	 *
	 * Get the place published
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * 0.2		04/12/2012	Genevieve Turner (GT)	Updated to comply with rif-cs version 1.4
	 * </pre>
	 * 
	 * @return the placePublished
	 */
	@XmlElement(namespace=Constants.ANDS_RIF_CS_NS)
	public String getPlacePublished() {
		return placePublished;
	}

	/**
	 * setPlacePublished
	 *
	 * SEt the place published
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param placePublished the placePublished to set
	 */
	public void setPlacePublished(String placePublished) {
		this.placePublished = placePublished;
	}

	/**
	 * getDates
	 *
	 * Get the citation dates
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		04/12/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @return the dates
	 */
	@Valid
	@Size(min=1, message="Citation Metadata requires at least one date")
	@XmlElement(namespace=Constants.ANDS_RIF_CS_NS)
	public List<ANDSDate> getDates() {
		return dates;
	}

	/**
	 * setDates
	 *
	 * Set the citation dates
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		04/12/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param dates the dates to set
	 */
	public void setDates(List<ANDSDate> dates) {
		this.dates = dates;
	}

	/**
	 * getUrl
	 *
	 * Get the url
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * 0.2		04/12/2012	Genevieve Turner (GT)	Updated to comply with rif-cs version 1.4
	 * </pre>
	 * 
	 * @return the url
	 */
	@XmlElement(namespace=Constants.ANDS_RIF_CS_NS)
	public String getUrl() {
		return url;
	}

	/**
	 * setUrl
	 *
	 * Set the url
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * getContext
	 *
	 * Get the context
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * 0.2		04/12/2012	Genevieve Turner (GT)	Updated to comply with rif-cs version 1.4
	 * </pre>
	 * 
	 * @return the context
	 */
	@XmlElement(namespace=Constants.ANDS_RIF_CS_NS)
	public String getContext() {
		return context;
	}

	/**
	 * setContext
	 *
	 * Set the context
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param context the context to set
	 */
	public void setContext(String context) {
		this.context = context;
	}
}
