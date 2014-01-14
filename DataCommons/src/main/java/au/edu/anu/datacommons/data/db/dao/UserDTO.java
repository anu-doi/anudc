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
package au.edu.anu.datacommons.data.db.dao;

import au.edu.anu.datacommons.data.db.model.Users;

/*
 * UserDTO
 *
 * Australian National University Data Commons
 * 
 * Return class when searching for users
 *
 * JUnit coverage:
 * None
 * 
 * @author Genevieve Turner
 *
 */
public class UserDTO {
	private Long id;
	private String username;
	private String displayName;
	private String email;
	
	/**
	 * Consttructor
	 */
	public UserDTO() {
		
	}
	
	/**
	 * Constructor
	 * 
	 * @param user The user to populate the information from
	 */
	public UserDTO(Users user) {
		this.id = user.getId();
		this.username = user.getUsername();
		this.displayName = user.getDisplayName();
		this.email = user.getEmail();
	}

	/**
	 * Get the id of the user object
	 * 
	 * @return The id of the user object
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Set the id of the user object
	 * 
	 * @param id The id of the user object
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Get the user name
	 * 
	 * @return The user name
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Set the user name
	 * 
	 * @param username The user name
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Get the display name
	 * 
	 * @return The display name
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * Set the display name
	 * 
	 * @param displayName The display name
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * Get the email adress
	 * 
	 * @return The email address
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Set the email address
	 * 
	 * @param email The email address
	 */
	public void setEmail(String email) {
		this.email = email;
	}
}
