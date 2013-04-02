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

package au.edu.anu.datacommons.xslt.db.model;

import java.io.Serializable;

import javax.persistence.Embeddable;

/**
 * SelectCodePK
 * 
 * Australian National University Data Commons
 * 
 * Primary key type for the SelectCode table
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		18/02/2013	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
@Embeddable
public class SelectCodePK implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String select_name_;
	private String code_;
	
	/**
	 * getSelect_name
	 *
	 * Gets field name to which the select code is associated
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		18/02/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the select_name
	 */
	public String getSelect_name() {
		return select_name_;
	}
	
	/**
	 * setSelect_name
	 *
	 * Sets field name to which the select code is associated
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		18/02/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param select_name the select_name to set
	 */
	public void setSelect_name(String select_name) {
		this.select_name_ = select_name;
	}
	
	/**
	 * getCode
	 *
	 * Gets the code for the select
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		18/02/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the code
	 */
	public String getCode() {
		return code_;
	}
	
	/**
	 * setCode
	 *
	 * Sets the code for the select
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		18/02/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code_ = code;
	}
	
	/**
	 * hashCode
	 * 
	 * Method to override hashCode
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		18/02/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return (int) select_name_.hashCode() + code_.hashCode();
	}
	
	/**
	 * equals
	 * 
	 * Method to override equals
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		18/02/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param obj
	 * @return
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof SelectCodePK)) {
			return false;
		}
		SelectCodePK pk = (SelectCodePK) obj;
		return pk.getSelect_name().equals(select_name_) && pk.getCode().equals(code_);
	}
}
