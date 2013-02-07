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

import javax.xml.bind.annotation.XmlElement;

/**
 * ExistenceDates
 * 
 * Australian National University Data Commons
 * 
 * Class for the existenceDates element in the ANDS RIF-CS schema
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
public class ExistenceDates {
	private ANDSDate startDate;
	private ANDSDate endDate;
	
	/**
	 * getStartDate
	 *
	 * Get the start date for the existence dates
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the startDate
	 */
	@XmlElement(namespace=Constants.ANDS_RIF_CS_NS)
	public ANDSDate getStartDate() {
		return startDate;
	}
	
	/**
	 * setStartDate
	 *
	 * Set the start date for the existence dates
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param startDate the startDate to set
	 */
	public void setStartDate(ANDSDate startDate) {
		this.startDate = startDate;
	}
	
	/**
	 * getEndDate
	 *
	 * Get the end date for the existence dates
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the endDate
	 */
	@XmlElement(namespace=Constants.ANDS_RIF_CS_NS)
	public ANDSDate getEndDate() {
		return endDate;
	}
	
	/**
	 * setEndDate
	 *
	 * Set the end date for the existence dates
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param endDate the endDate to set
	 */
	public void setEndDate(ANDSDate endDate) {
		this.endDate = endDate;
	}
}
