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

package au.edu.anu.datacommons.data.db.model;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import au.edu.anu.datacommons.xml.other.SelectCodeAdapter;

/**
 * SelectCode
 * 
 * Australian National University Data Commons
 * 
 * Select Codes mapping table.  This is mostly utilised in retrieving codes and descriptions for
 * select/combobox/lists
 * 
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		22/06/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
@Entity
@Table(name="select_code")
@XmlJavaTypeAdapter(value=SelectCodeAdapter.class)
public class SelectCode {
	private SelectCodePK id_;
	private String description_;
	private Boolean deprecated_;
	
	/**
	 * getId
	 *
	 * Gets the id for the select code
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		20/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the id
	 */
	@EmbeddedId
	public SelectCodePK getId() {
		return id_;
	}
	
	/**
	 * setId
	 *
	 * Sets the id for the select code
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		20/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param id the id to set
	 */
	public void setId(SelectCodePK id) {
		this.id_ = id;
	}
	
	/**
	 * getDescription
	 *
	 * Gets the description of the select code
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		20/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the description
	 */
	public String getDescription() {
		return description_;
	}
	
	/**
	 * setDescription
	 *
	 * Sets the description of the select code
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		20/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description_ = description;
	}
	
	/**
	 * getDeprecated
	 *
	 * Gets whether the select code is deprecated
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		20/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the deprecated
	 */
	public Boolean getDeprecated() {
		return deprecated_;
	}
	
	/**
	 * setDeprecated
	 *
	 * Sets whether the select code is deprecated
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		20/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param deprecated the deprecated to set
	 */
	public void setDeprecated(Boolean deprecated) {
		this.deprecated_ = deprecated;
	}
}
