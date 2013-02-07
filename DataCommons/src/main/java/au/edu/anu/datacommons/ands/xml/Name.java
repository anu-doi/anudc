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
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Name
 * 
 * Australian National University Data Commons
 * 
 * Class for the name element in the ANDS RIF-CS schema
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
public class Name {
	private XMLGregorianCalendar dateFrom;
	private XMLGregorianCalendar dateTo;
	private String type;
	private String lang;
	private List<NamePart> nameParts;
	
	/**
	 * Constructor
	 * 
	 * Constructor for the Name class
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		15/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 *
	 */
	public Name() {
		nameParts = new ArrayList<NamePart>();
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
	 * Get the name type
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the type
	 */
	@NotNull(message="Name type may not be null")
	@Pattern(regexp="primary|alternative|abbreviated", message="Name type must be one of primary, alternative or abbreviated")
	@XmlAttribute
	public String getType() {
		return type;
	}
	
	/**
	 * setType
	 *
	 * Set the name type
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
	 * getLang
	 *
	 * Get the name language
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the lang
	 */
	@XmlAttribute
	public String getLang() {
		return lang;
	}
	
	/**
	 * setLang
	 *
	 * Set the name language
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param lang the lang to set
	 */
	public void setLang(String lang) {
		this.lang = lang;
	}
	
	/**
	 * getNameParts
	 *
	 * Get the name parts
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the nameParts
	 */
	@Valid
	@Size(min=1, message="The name is missing parts")
	@XmlElement(name="namePart", namespace=Constants.ANDS_RIF_CS_NS)
	public List<NamePart> getNameParts() {
		return nameParts;
	}
	
	/**
	 * setNameParts
	 *
	 * Set the name parts
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param nameParts the nameParts to set
	 */
	public void setNameParts(List<NamePart> nameParts) {
		this.nameParts = nameParts;
	}
}
