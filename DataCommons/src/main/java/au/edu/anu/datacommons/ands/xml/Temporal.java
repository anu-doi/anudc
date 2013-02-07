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

import javax.validation.Valid;
import javax.xml.bind.annotation.XmlElement;

import au.edu.anu.datacommons.validator.AtLeastOneOf;

/**
 * Temporal
 * 
 * Australian National University Data Commons
 * 
 * Class for the temporal element in the ANDS RIF-CS schema
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
@AtLeastOneOf(fieldNames={"date", "text"}, message="Temporal date requires either a date or a text date")
public class Temporal {
	private ANDSDate date;
	private String text;
	
	/**
	 * getDate
	 *
	 * Get the temporal date in the ANDS format
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * X.X		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the date
	 */
	@Valid
	@XmlElement(namespace=Constants.ANDS_RIF_CS_NS)
	public ANDSDate getDate() {
		return date;
	}
	
	/**
	 * setDate
	 *
	 * Set the temporal date in the ANDS format
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * X.X		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param date the date to set
	 */
	public void setDate(ANDSDate date) {
		this.date = date;
	}
	
	/**
	 * getText
	 *
	 * Get the temporal text
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * X.X		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the text
	 */
	@XmlElement(namespace=Constants.ANDS_RIF_CS_NS)
	public String getText() {
		return text;
	}
	
	/**
	 * setText
	 *
	 * Set the temporal text
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * X.X		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}
}
