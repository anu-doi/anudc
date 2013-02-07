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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Location
 * 
 * Australian National University Data Commons
 * 
 * Class for the location element in the ANDS RIF-CS schema
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		15/10/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
public class Location {
	private XMLGregorianCalendar dateFrom;
	private XMLGregorianCalendar dateTo;
	private String type;
	private List<Address> addresses;
	private List<Spatial> spatialAddresses;
	
	public Location() {
		addresses = new ArrayList<Address>();
		spatialAddresses = new ArrayList<Spatial>();
	}
	
	/**
	 * getDateFrom
	 *
	 * Get the date from
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the dateFrom
	 */
	@XmlAttribute
	public XMLGregorianCalendar getDateFrom() {
		return dateFrom;
	}
	
	/**
	 * setDateFrom
	 *
	 * Set the date from
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param dateFrom the dateFrom to set
	 */
	public void setDateFrom(XMLGregorianCalendar dateFrom) {
		this.dateFrom = dateFrom;
	}
	
	/**
	 * getDateTo
	 *
	 * Get the date to
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the dateTo
	 */
	@XmlAttribute
	public XMLGregorianCalendar getDateTo() {
		return dateTo;
	}
	
	/**
	 * setDateTo
	 *
	 * Set the date to
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param dateTo the dateTo to set
	 */
	public void setDateTo(XMLGregorianCalendar dateTo) {
		this.dateTo = dateTo;
	}
	
	/**
	 * getType
	 *
	 * Get the location type
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the type
	 */
	@XmlAttribute
	public String getType() {
		return type;
	}
	
	/**
	 * setType
	 *
	 * Set the location type
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * getAddresses
	 *
	 * Get the location address
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the addresses
	 */
	//TODO make it address or spatial location, for the moment address will be required for all
	//TODO update message
	@Valid
	@Size(min=1, message="A Location is required")
	@XmlElement(name="address", namespace=Constants.ANDS_RIF_CS_NS)
	public List<Address> getAddresses() {
		return addresses;
	}
	
	/**
	 * setAddresses
	 *
	 * Set the location address
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param addresses the addresses to set
	 */
	public void setAddresses(List<Address> addresses) {
		this.addresses = addresses;
	}
	
	/**
	 * getSpatialAddresses
	 *
	 * Get the spatial address
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the spatialAddresses
	 */
	@Valid
	@XmlElement(name="spatial", namespace=Constants.ANDS_RIF_CS_NS)
	public List<Spatial> getSpatialAddresses() {
		return spatialAddresses;
	}
	
	/**
	 * setSpatialAddresses
	 *
	 * Set the spatial address
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param spatialAddresses the spatialAddresses to set
	 */
	public void setSpatialAddresses(List<Spatial> spatialAddresses) {
		this.spatialAddresses = spatialAddresses;
	}
}
