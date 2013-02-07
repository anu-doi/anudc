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

package au.edu.anu.datacommons.xml.data;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Data
 * 
 * Australian National University Data Comons
 * 
 * Data item for jaxb
 * 
 * JUnit Coverage:
 * None
 * 
 * Version	Date		Developer				Description
 * 0.1		23/03/2012	Genevieve Turner (GT)	Initial
 * 0.2		29/03/2012	Genevieve Turner (GT)	Added remove elements function and updated to fix issue when unmarshalling
 * 
 */
@XmlRootElement(name="data")
public class Data {
	static final Logger LOGGER = LoggerFactory.getLogger(Data.class);
	
	private List<DataItem> items_;
	
	/**
	 * Constructor
	 * 
	 * Version	Date		Developer				Description
	 * 0.1		23/03/2012	Genevieve Turner (GT)	Initial
	 * 0.2		29/03/2012	Genevieve Turner (GT)	Updated to fix issue when unmarshalling
	 */
	public Data() {
		items_ = new ArrayList<DataItem>();
	}
	
	/**
	 * getItems
	 * 
	 * Gets the list of JAXBElements with a string value
	 * 
	 * Version	Date		Developer				Description
	 * 0.1		23/03/2012	Genevieve Turner (GT)	Initial
	 * 0.2		29/03/2012	Genevieve Turner (GT)	Updated to fix issue when unmarshalling
	 * 
	 * @return The list of elements
	 */
	@XmlAnyElement
	public List<DataItem> getItems() {
		return items_;
	}
	
	/**
	 * setItems
	 *
	 * Sets the list of items
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.3		20/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param items The list of elements
	 */
	public void setItems(List<DataItem> items) {
		this.items_ = items;
	}
	
	/**
	 * removeElementsByName
	 * 
	 * Removes all the elements with the given name
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		29/03/2012	Genevieve Turner (GT)	Added function
	 * 0.3		20/06/2012	Genevieve Turner (GT)	Amended to return a list of removed items
	 * </pre>
	 * 
	 * @param localpart The name of the fields to remove
	 */
	public List<DataItem> removeElementsByName(String localpart) {
		List<DataItem> removedItems = new ArrayList<DataItem>();
		for (int i = items_.size() - 1; i >= 0; i--) {
			DataItem item = items_.get(i);
			if(item.getName().equals(localpart)) {
				removedItems.add(items_.remove(i));
			}
		}
		return removedItems;
	}
	
	/**
	 * Returns List of elements in a Fedora Object whose tagname is <em>elementName</em>.
	 * 
	 * @param elementName
	 *            Name of the element as String.
	 * @return List of DataItem objects referencing to the elements. Returns a blank list if no elements found.
	 */
	public List<DataItem> getElementByName(String elementName)
	{
		List<DataItem> elements = new ArrayList<DataItem>();
		for (DataItem iItem : items_)
			if (iItem.getName().equalsIgnoreCase(elementName))
				elements.add(iItem);
		return elements;
	}
	
	/**
	 * Returns the first element in a Fedora Object whose tagname is <em>elementName</em>.
	 * 
	 * @param elementName
	 *            Name of the element as String.
	 * @return DataItem with the provided elementName. null if an element with that name doesn't exist.
	 */
	public DataItem getFirstElementByName(String elementName)
	{
		for (DataItem iItem : items_)
			if (iItem.getName().equalsIgnoreCase(elementName))
				return iItem;
		return null;
	}
	
	/**
	 * hasElement
	 * 
	 * Checks if there is an element with the specified name
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.3		15/10/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param localpart The name of the fields to check if it exists
	 */
	public boolean hasElement(String localpart) {
		for (int i = items_.size() - 1; i >= 0; i--) {
			DataItem item = items_.get(i);
			if (item.getName().equals(localpart)) {
				return true;
			}
		}
		return false;
	}
}
