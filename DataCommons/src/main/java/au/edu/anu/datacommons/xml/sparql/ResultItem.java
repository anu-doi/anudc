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

package au.edu.anu.datacommons.xml.sparql;

/**
 * ResultItem
 * 
 * Australian National University Data Commons
 * 
 * A result item that holds information such as the name of the return field, the value,
 * and whether the field is a literal value or a uri attribute.
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		01/08/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
public class ResultItem {
	private String name_;
	private String value_;
	private Boolean isLiteral_;
	
	/**
	 * getName
	 *
	 * Get the name of the field
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/07/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the name
	 */
	public String getName() {
		return name_;
	}
	
	/**
	 * setName
	 *
	 * Set the name of the field
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/07/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name_ = name;
	}
	
	/**
	 * getValue
	 *
	 * Get the value of the field
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/07/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the value
	 */
	public String getValue() {
		return value_;
	}
	
	/**
	 * setValue
	 *
	 * Set the value of the field
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/07/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value_ = value;
	}
	
	/**
	 * getIsLiteral
	 *
	 * Get whether the field is literal or not, i.e. whether the sparql result
	 * returned an item that has a 'uri' attribute or a text node.
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/07/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return whether the field is literal
	 */
	public Boolean getIsLiteral() {
		return isLiteral_;
	}
	
	/**
	 * setIsLiteral
	 *
	 * Sets whether the field is literal or not.
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/07/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param whether the field is literal
	 */
	public void setIsLiteral(Boolean isLiteral) {
		this.isLiteral_ = isLiteral;
	}
}
