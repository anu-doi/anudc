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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Collection
 * 
 * Australian National University Data Commons
 * 
 * Class for the collection element in the ANDS RIF-CS schema
 *
 * JUnit Coverage:
 * RegistryObjectsTest
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		12/10/2012	Genevieve Turner (GT)	Initial
 * 0.2		04/12/2012	Genevieve Turner (GT)	Updated to comply with rif-cs version 1.4
 * </pre>
 *
 */
@XmlRootElement(name="collection")
public class Collection extends ObjectType {
	private List<Rights> rights;
	private List<CitationInfo> citationInfo;
	private List<Dates> dates;
	
	/**
	 * Constructor
	 * 
	 * Constructor for the collection class
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		12/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 *
	 */
	public Collection() {
		rights = new ArrayList<Rights>();
		citationInfo = new ArrayList<CitationInfo>();
		dates = new ArrayList<Dates>();
	}

	/**
	 * getRights
	 *
	 * Get the rights for the collection
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the rights
	 */
	@Valid
	@Size(min=1, message="Quality Level 2 - There is no rights information for this record")
	@XmlElement(name="rights", namespace=Constants.ANDS_RIF_CS_NS)
	public List<Rights> getRights() {
		return rights;
	}

	/**
	 * setRights
	 *
	 * Set the rights for the collection
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param rights the rights to set
	 */
	public void setRights(List<Rights> rights) {
		this.rights = rights;
	}

	/**
	 * getCitationInfo
	 *
	 * Get the citation information for the collection
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the citationInfo
	 */
	@Valid
	@Size(min=1, message="Quality Level 3 - Citation data is recommended")
	@XmlElement(name="citationInfo", namespace=Constants.ANDS_RIF_CS_NS)
	public List<CitationInfo> getCitationInfo() {
		return citationInfo;
	}

	/**
	 * setCitationInfo
	 *
	 * Set the citation information for the collection
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param citationInfo the citationInfo to set
	 */
	public void setCitationInfo(List<CitationInfo> citationInfo) {
		this.citationInfo = citationInfo;
	}

	/**
	 * getDates
	 *
	 * Get the collection dates
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		04/12/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the dates
	 */
	@Valid
	@Size(min=1, message="Quality Level 3 - At least one dates element is recommended for this collection (For example the date of data creation)")
	@XmlElement(name="dates", namespace=Constants.ANDS_RIF_CS_NS)
	public List<Dates> getDates() {
		return dates;
	}

	/**
	 * setDates
	 *
	 * Set the collection dates
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		04/12/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param dates the dates to set
	 */
	public void setDates(List<Dates> dates) {
		this.dates = dates;
	}
}
