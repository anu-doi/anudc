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

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.freemarker.SelectOptions;

/**
 * Data
 * 
 * Australian National University Data Comons
 * 
 * Data item class for jaxb processing of the 'data' type xml objects.
 * 
 * JUnit Coverage:
 * None
 * 
 * Version	Date		Developer				Description
 * 0.1		29/03/2012	Genevieve Turner (GT)	Initial
 * 
 */
@XmlJavaTypeAdapter(DataItemAdapter.class)
public class DataItem {
	static final Logger LOGGER = LoggerFactory.getLogger(DataItem.class);
	
	private String name_;
	private String value_;
	private String description_;
	private List<DataItem> childValues_;
	
	/**
	 * Constructor class.
	 */
	public DataItem() {
		childValues_ = new ArrayList<DataItem>();
	}
	
	/**
	 * getName_
	 * 
	 * Gets name of the object
	 * 
	 * Version	Date		Developer				Description
	 * 0.1		29/03/2012	Genevieve Turner (GT)	Initial
	 * 
	 * @return The name of the object
	 */
	public String getName() {
		return name_;
	}
	
	/**
	 * setName_
	 * 
	 * Sets the name of the xml object
	 * 
	 * Version	Date		Developer				Description
	 * 0.1		29/03/2012	Genevieve Turner (GT)	Initial
	 * 
	 * @param name_ The name of the object
	 */
	public void setName(String name_) {
		this.name_ = name_;
	}
	
	/**
	 * getValue_
	 * 
	 * Gets the text node value of the xml object
	 * 
	 * Version	Date		Developer				Description
	 * 0.1		29/03/2012	Genevieve Turner (GT)	Initial
	 * 
	 * @return The text node value of the object
	 */
	public String getValue() {
		return value_;
	}
	
	/**
	 * setValue_
	 * 
	 * Sets the text node value of the object
	 * 
	 * Version	Date		Developer				Description
	 * 0.1		29/03/2012	Genevieve Turner (GT)	Initial
	 * 
	 * @param value_ The text node value of the object
	 */
	public void setValue(String value_) {
		this.value_ = value_;
	}

	/**
	 * Get the item description
	 * 
	 * @return The description
	 */
	public String getDescription() {
		return description_;
	}

	/**
	 * Set the item description
	 * 
	 * @param description_ The description
	 */
	public void setDescription(String description_) {
		this.description_ = description_;
	}

	/**
	 * Get the child values
	 * 
	 * @return The child values
	 */
	public List<DataItem> getChildValues() {
		return childValues_;
	}

	/**
	 * Set the child values
	 * 
	 * @param childValues_  The child values
	 */
	public void setChildValues(List<DataItem> childValues_) {
		this.childValues_ = childValues_;
	}
	
	public List<DataItem> getChildElementByName(String elementName) {
		List<DataItem> elements = new ArrayList<DataItem>();
		for (DataItem item : childValues_) {
			if (item.getName().equalsIgnoreCase(elementName)) {
				elements.add(item);
			}
		}
		return elements;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((childValues_ == null) ? 0 : childValues_.hashCode());
		result = prime * result + ((description_ == null) ? 0 : description_.hashCode());
		result = prime * result + ((name_ == null) ? 0 : name_.hashCode());
		result = prime * result + ((value_ == null) ? 0 : value_.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DataItem other = (DataItem) obj;
		if (childValues_ == null) {
			if (other.childValues_ != null)
				return false;
		} else if (!childValues_.equals(other.childValues_))
			return false;
		if (description_ == null) {
			if (other.description_ != null)
				return false;
		} else if (!description_.equals(other.description_))
			return false;
		if (name_ == null) {
			if (other.name_ != null)
				return false;
		} else if (!name_.equals(other.name_))
			return false;
		if (value_ == null) {
			if (other.value_ != null)
				return false;
		} else if (!value_.equals(other.value_))
			return false;
		return true;
	}
}
