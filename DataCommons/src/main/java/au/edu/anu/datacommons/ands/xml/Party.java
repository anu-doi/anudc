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

import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Party
 * 
 * Australian National University Data Commons
 * 
 * Class for the party element in the ANDS RIF-CS schema
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
@XmlRootElement(name="party")
public class Party extends ObjectType {
	private List<ExistenceDates> existenceDates;
	
	/**
	 * Constructor
	 * 
	 * Constructor class for the Party object
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		15/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 *
	 */
	public Party() {
		existenceDates = new ArrayList<ExistenceDates>();
	}
	
	/**
	 * getExistenceDates
	 *
	 * Get party the existence dates
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the existenceDates
	 */
	@Size(min=1, message="Quality Level 3 - At least one set of existence dates")
	@XmlElement(name="existenceDates", namespace=Constants.ANDS_RIF_CS_NS)
	public List<ExistenceDates> getExistenceDates() {
		return existenceDates;
	}

	/**
	 * setExistenceDates
	 *
	 * Set party the existence dates
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param existenceDates the existenceDates to set
	 */
	public void setExistenceDates(List<ExistenceDates> existenceDates) {
		this.existenceDates = existenceDates;
	}
}
