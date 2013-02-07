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

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * ElectronicAddressArgument
 * 
 * Australian National University Data Commons
 * 
 * Class for the arg element in the ANDS RIF-CS schema
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		12/10/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
public class ElectronicAddressArgument {
	private String required;
	private String type;
	private String use;
	
	/**
	 * getRequired
	 *
	 * Get whether the electronic address argument is required
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the required
	 */
	@NotNull(message = "Whether an argument is required or not needs to be specified for an electronic argument")
	@Pattern(regexp="^true|false$", message="The required field of an argument must be true or false")
	@XmlAttribute
	public String getRequired() {
		return required;
	}
	
	/**
	 * setRequired
	 *
	 * Set whether the electronic address argument is required
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param required the required to set
	 */
	public void setRequired(String required) {
		this.required = required;
	}
	
	/**
	 * getType
	 *
	 * Get the argument type
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the type
	 */
	@NotNull(message="The type of an electronic argument must not be null")
	@Pattern(regexp="^string|object$", message="The type an electronic argument must be string or object")
	@XmlAttribute
	public String getType() {
		return type;
	}
	
	/**
	 * setType
	 *
	 * Set the argument type
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * getUse
	 *
	 * Get the use of the argument
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the use
	 */
	@Pattern(regexp="^inline|keyValue$", message="The use field of an electronic argument must be inline or keyValue")
	@XmlAttribute
	public String getUse() {
		return use;
	}
	
	/**
	 * setUse
	 *
	 * Set the use of the argument
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param use the use to set
	 */
	public void setUse(String use) {
		this.use = use;
	}
}
