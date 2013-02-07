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

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Authorities
 * 
 * Australian National University Data Commons
 * 
 * Class used to update authorities.
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		21/08/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
@Entity
@Table(name="authorities")
public class Authorities {
	private String username_;
	private String authority_;
	
	/**
	 * getUsername
	 *
	 * Gets the username
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		21/08/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the username
	 */
	@Id
	public String getUsername() {
		return username_;
	}
	
	/**
	 * setUsername
	 *
	 * Sets the username
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		21/08/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username_ = username;
	}
	
	/**
	 * getAuthority
	 *
	 * Gets the authority
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		21/08/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the authority
	 */
	public String getAuthority() {
		return authority_;
	}
	
	/**
	 * setAuthority
	 *
	 * Sets the authority
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		21/08/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param authority the authority to set
	 */
	public void setAuthority(String authority) {
		this.authority_ = authority;
	}
}
