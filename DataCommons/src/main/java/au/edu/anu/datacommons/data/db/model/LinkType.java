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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * LinkType
 * 
 * Australian National University Data Commons
 * 
 * Entity class for the link_type table
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		19/09/2012	Genevieve Turner (GT)	Initial
 * 0.2		27/09/2012	Genevieve Turner (GT)	Added reverse column
 * </pre>
 *
 */
@Entity
@Table(name="link_type")
public class LinkType {
	private Long id;
	private String code;
	private String description;
	private String reverse;
	
	/**
	 * getId
	 *
	 * Get the id
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		18/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}
	
	/**
	 * setId
	 *
	 * Set the id
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		18/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}
	
	/**
	 * getCode
	 *
	 * Get the code
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		18/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the code
	 */
	@Column(name="code")
	public String getCode() {
		return code;
	}
	
	/**
	 * setCode
	 *
	 * Set the code
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		18/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}
	
	/**
	 * getDescription
	 *
	 * Get the description
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		18/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the description
	 */
	@Column(name="description")
	public String getDescription() {
		return description;
	}
	
	/**
	 * setDescription
	 *
	 * Set the description
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		18/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * getReverse
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		27/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the reverse
	 */
	@Column(name="reverse")
	public String getReverse() {
		return reverse;
	}

	/**
	 * setReverse
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		27/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param reverse the reverse to set
	 */
	public void setReverse(String reverse) {
		this.reverse = reverse;
	}
}
