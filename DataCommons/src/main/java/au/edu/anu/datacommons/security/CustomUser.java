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

package au.edu.anu.datacommons.security;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import au.edu.anu.datacommons.data.db.model.Users;

/**
 * CustomUser
 * 
 * Australian National University Data Commons
 * 
 * Class that extends the User so that we can have custom details
 * 
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		26/04/2012	Genevieve Turner (GT)	Initial
 * 0.2		23/05/2012	Genevieve Turner (GT)	Updated for display name
 * 0.3		23/08/2012	Genevieve Turner (GT)	Updated to allow for passing through a Users object
 * 0.4		14/11/2012	Genevieve Turner (GT)	Updated such that the id is a Long object rather than primitive long
 * </pre>
 * 
 */
public class CustomUser extends User {
	private static final long serialVersionUID = 1L;
	private Long id;
	private String displayName;
	private String email;
	private String institution;
	
	/**
	 * Constructor
	 * 
	 * Constructor to build the custom user
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/04/2012	Genevieve Turner (GT)	Initial
	 * 0.2		23/05/2012	Genevieve Turner (GT)	Updated for display name
	 * </pre>
	 * 
	 * @param username The username presented to the authentication provider
	 * @param password The password pressented to the authentication provider
	 * @param enabled Set to true if the user is enabled
	 * @param accountNonExpired set to true if the account has not expired
	 * @param credentialsNonExpired set to true if the credentials have not expired
	 * @param accountNonLocked set to true if the account is not locked
	 * @param authorities the authorities that should be granted to the caller if they presented the correct username and password and the user is enabled
	 * @param id The id of the row in the database for the user presented to the authentication provider
	 * @throws IllegalArgumentException
	 */
	public CustomUser (String username, String password, boolean enabled,
			boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked,
			List<GrantedAuthority> authorities, Long id, String displayName) throws IllegalArgumentException {
		super(username, password, enabled, accountNonExpired, credentialsNonExpired,
				accountNonLocked, authorities);
		this.id = id;
		this.displayName = displayName;
	}
	
	public CustomUser (String username, String password, boolean enabled,
			boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked,
			List<GrantedAuthority> authorities, Long id, String displayName, String email, String institution) throws IllegalArgumentException {
		super(username, password, enabled, accountNonExpired, credentialsNonExpired,
				accountNonLocked, authorities);
		this.id = id;
		this.displayName = displayName;
		this.institution = institution;
		this.email = email;
	}
	
	/**
	 * Constructor
	 * 
	 * Constructor to build the custom user
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.3		23/08/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param user User to create a information for
	 * @param enabled Set to true if the user is enabled
	 * @param accountNonExpired set to true if the account has not expired
	 * @param credentialsNonExpired set to true if the credentials have not expired
	 * @param accountNonLocked set to true if the account is not locked
	 * @param authorities the authorities that should be granted to the caller if they presented the correct username and password and the user is enabled
	 * @throws IllegalArgumentException
	 */
	public CustomUser (Users user, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired,
			boolean accountNonLocked, List<GrantedAuthority> authorities) throws IllegalArgumentException {
		super(user.getUsername(), user.getPassword(), enabled, accountNonExpired, credentialsNonExpired,
				accountNonLocked, authorities);
		this.id = user.getId();
		this.displayName = user.getDisplayName();
		this.email = user.getEmail();
	}

	/**
	 * getId
	 * 
	 * Returns the user id
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/04/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @return The users id
	 */
	public long getId() {
		return id;
	}

	/**
	 * setId
	 * 
	 * Sets the user id
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/04/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @return The users id
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * getDisplayName
	 * 
	 * Gets the user display name
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		23/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @return The users display name
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * setId
	 * 
	 * Sets the user display name
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		23/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param displayName The users display name
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getInstitution() {
		return institution;
	}

	public void setInstitution(String institution) {
		this.institution = institution;
	}

	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * toString
	 * 
	 * The toString method for the class
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/04/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @return A string representation of this method
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(super.toString()).append(": ");
		sb.append("Id: ").append(this.id).append("; ");
		return sb.toString();
	}
}
