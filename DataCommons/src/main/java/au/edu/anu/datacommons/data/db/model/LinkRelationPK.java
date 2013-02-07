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

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * LinkRelationPK
 * 
 * Australian National University Data Commons
 * 
 * Primary key for the link_relation table
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		19/09/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
@Embeddable
public class LinkRelationPK implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String category1;
	private String category2;
	private LinkType link_type;
	
	/**
	 * getCategory1
	 *
	 * Get the first category
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		18/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the category1
	 */
	public String getCategory1() {
		return category1;
	}
	
	/**
	 * setCategory1
	 *
	 * Set the first category
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		18/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param category1 the category1 to set
	 */
	public void setCategory1(String category1) {
		this.category1 = category1;
	}
	
	/**
	 * getCategory2
	 *
	 * Get the second category
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		18/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the category2
	 */
	public String getCategory2() {
		return category2;
	}
	
	/**
	 * setCategory2
	 *
	 * Set the second category
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		18/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param category2 the category2 to set
	 */
	public void setCategory2(String category2) {
		this.category2 = category2;
	}

	/**
	 * getLink_type
	 *
	 * Get the link type
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		18/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the link_type
	 */
	@ManyToOne
	@JoinColumn(name="link_type_id", nullable=false, updatable=false)
	public LinkType getLink_type() {
		return link_type;
	}

	/**
	 * setLink_type
	 *
	 * Set the link type
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		18/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param link_type the link_type to set
	 */
	public void setLink_type(LinkType link_type) {
		this.link_type = link_type;
	}
	
	/**
	 * hashCode
	 * 
	 * Overrides the hashCode method
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		19/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return The hash code
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return (int) category1.hashCode() + category2.hashCode() + link_type.hashCode();
	}
	
	/**
	 * equals
	 * 
	 * Overrides the equals method
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		19/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param obj The object to compare
	 * @return Whether the objects are equal
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if(!(obj instanceof LinkRelationPK)) {
			return false;
		}
		LinkRelationPK pk = (LinkRelationPK) obj;
		return pk.getCategory1().equals(category1) && pk.getCategory2().equals(category2) &&
				pk.getLink_type().getId().equals(link_type.getId()) &&
				pk.getLink_type().getCode().equals(link_type.getCode()) &&
				pk.getLink_type().getDescription().equals(link_type.getDescription());
	}
}
