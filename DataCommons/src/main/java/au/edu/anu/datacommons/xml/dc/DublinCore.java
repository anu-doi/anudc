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

package au.edu.anu.datacommons.xml.dc;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * DublinCore
 * 
 * Australian National University Data Comons
 * 
 * Dublin Core JAXB class
 * 
 * Version	Date		Developer				Description
 * 0.1		08/03/2012	Genevieve Turner (GT)	Initial
 * 
 */
@XmlRootElement(name="dc", namespace=DublinCoreConstants.OAI_DC)
public class DublinCore {
	List<JAXBElement<String>> items_;
	
	/**
	 * Constructor
	 * 
	 * Version	Date		Developer				Description
	 * 0.1		08/03/2012	Genevieve Turner (GT)	Initial
	 */
	public DublinCore() {
		items_ = new ArrayList<JAXBElement<String>>();
	}
	
	/**
	 * getItems
	 * 
	 * Returns a list of dublin core items
	 * 
	 * Version	Date		Developer				Description
	 * 0.1		08/03/2012	Genevieve Turner (GT)	Initial
	 * 
	 * @return The list of dublin core items
	 */
	//TODO potentially add in a spefic vocab that can be used
	@XmlAnyElement
	public List<JAXBElement<String>> getItems_() {
		return items_;
	}
	
	/**
	 * setItems
	 * 
	 * Sets the list of dublin core items
	 * 
	 * Version	Date		Developer				Description
	 * 0.1		08/03/2012	Genevieve Turner (GT)	Initial
	 * 
	 * @param items The dublin core items
	 */
	public void setItems_(List<JAXBElement<String>> items_) {
		this.items_ = items_;
	}
	
}
