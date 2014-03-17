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
import javax.persistence.Table;

/**
 * UserShibboleth
 *
 * Australian National University Data Commons
 * 
 * Class that holds information about Shibboleth Users
 *
 * JUnit coverage:
 * None
 * 
 * @author Genevieve Turner
 *
 */
@Entity
@Table(name="user_shibboleth")
public class UserShibboleth extends UserExtra {
	private String displayName;
	private String email;
	private String institution;
	
	/**
	 * Constructor
	 */
	public UserShibboleth() {
		
	}
	
	/**
	 * Constructor
	 * 
	 * @param user The user object the information is associated with
	 * @param displayName The display name
	 * @param email The email address
	 * @param institution The institution
	 */
	public UserShibboleth(Users user, String displayName, String email, String institution) {
		super.setUser(user);
		this.displayName = displayName;
		this.email = email;
		this.institution = institution;
	}

	/**
	 * Get the users display name
	 * 
	 * @return The display name
	 */
	@Column(name="display_name")
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * Set the users display name
	 * 
	 * @param displayName The display name
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * Get the users email address
	 * 
	 * @return The email address
	 */
	@Column(name="email")
	public String getEmail() {
		return email;
	}

	/**
	 * Set the users email address
	 * 
	 * @param email The email address
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Get the users institution
	 * @return
	 */
	@Column(name="institution")
	public String getInstitution() {
		return institution;
	}

	/**
	 * Set the users institution
	 * 
	 * @param institution The institution
	 */
	public void setInstitution(String institution) {
		this.institution = institution;
	}
}
