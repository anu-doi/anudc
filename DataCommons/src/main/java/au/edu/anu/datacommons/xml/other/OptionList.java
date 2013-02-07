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

package au.edu.anu.datacommons.xml.other;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import au.edu.anu.datacommons.data.db.model.Groups;
import au.edu.anu.datacommons.data.db.model.SelectCode;

/**
 * OptionList
 * 
 * Australian National University Data Commons
 * 
 * Creates a list of options available to the system
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		20/06/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
@XmlRootElement(name="options")
@XmlSeeAlso(Groups.class)
public class OptionList {
	List<Groups> groups;
	List<SelectCode> selectCodes;
	
	public OptionList() {
		groups = new ArrayList<Groups>();
		selectCodes = new ArrayList<SelectCode>();
	}
	
	@XmlElement(name="ownerGroup")
	public List<Groups> getGroups() {
		return groups;
	}

	public void setGroups(List<Groups> groups) {
		this.groups = groups;
	}

	/**
	 * getSelectCodes
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * X.X		20/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the selectCodes
	 */
	@XmlAnyElement
	public List<SelectCode> getSelectCodes() {
		return selectCodes;
	}

	/**
	 * setSelectCodes
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * X.X		20/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param selectCodes the selectCodes to set
	 */
	public void setSelectCodes(List<SelectCode> selectCodes) {
		this.selectCodes = selectCodes;
	}
}
