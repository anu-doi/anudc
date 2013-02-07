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

package au.edu.anu.datacommons.data.fedora;


/**
 * FedoraReference
 * 
 * Australian National University Data Commons
 * 
 * Class for references or relationships to be added to Fedora Commons
 * 
 * Version	Date		Developer				Description
 * 0.1		08/03/2012	Genevieve Turner (GT)	Initial
 */
public class FedoraReference {
	private String predicate_;
	private String object_;
	private Boolean isLiteral_;
	
	/**
	 * getPredicate_
	 * 
	 * Returns the reference predicate
	 * 
	 * Version	Date		Dev						Description
	 * 0.1		08/03/2012	Genevieve Turner (GT)	Initial
	 * 
	 * @return The predicate of the reference
	 */
	public String getPredicate_() {
		return predicate_;
	}
	
	/**
	 * setPredicate_
	 * 
	 * Sets the reference predicate
	 * 
	 * Version	Date		Dev						Description
	 * 0.1		08/03/2012	Genevieve Turner (GT)	Initial
	 * 
	 * @param predicate_ The predicate of the reference
	 */
	public void setPredicate_(String predicate_) {
		this.predicate_ = predicate_;
	}
	
	/**
	 * getObject_
	 * 
	 * Gets the reference object
	 * 
	 * Version	Date		Dev						Description
	 * 0.1		08/03/2012	Genevieve Turner (GT)	Initial
	 * 
	 * @return The object of the reference
	 */
	public String getObject_() {
		return object_;
	}
	
	/**
	 * setObject_
	 * 
	 * Gets the reference object
	 * 
	 * Version	Date		Dev						Description
	 * 0.1		08/03/2012	Genevieve Turner (GT)	Initial
	 * 
	 * @param object_ The object of the reference
	 */
	public void setObject_(String object_) {
		this.object_ = object_;
	}
	
	/**
	 * getIsLiteral_
	 * 
	 * Gets whether the reference is a literal or a uri
	 * 
	 * Version	Date		Dev						Description
	 * 0.1		08/03/2012	Genevieve Turner (GT)	Initial
	 * 
	 * @return Whether the value is a literal or not
	 */
	public Boolean getIsLiteral_() {
		return isLiteral_;
	}
	
	/**
	 * getIsLiteral_
	 * 
	 * Sets whether the reference is a literal or a uri
	 * 
	 * Version	Date		Dev						Description
	 * 0.1		08/03/2012	Genevieve Turner (GT)	Initial
	 * 
	 * @param isLiteral_ Whether the value is a literal or not
	 */
	public void setIsLiteral_(Boolean isLiteral_) {
		this.isLiteral_ = isLiteral_;
	}
}
