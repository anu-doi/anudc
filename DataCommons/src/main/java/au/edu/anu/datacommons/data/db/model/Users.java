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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.ldap.LdapPerson;
import au.edu.anu.datacommons.ldap.LdapRequest;

/**
 * Users
 * 
 * Australian National University Data Comons
 * 
 * Entity class for the users database table
 * 
 * JUnit Coverage:
 * UsersTest
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		16/05/2012	Genevieve Turner (GT)	Initial
 * 0.2		17/05/2012	Genevieve Turner (GT)	Added user type column
 * </pre>
 * 
 */
@Entity
@Table(name = "users")
public class Users
{
	static final Logger LOGGER = LoggerFactory.getLogger(Users.class);
	
	private Long id;
	private String username;
	private String password;
	private Boolean enabled;

	private String displayName;		// Transient
	private String givenName;		// Transient
	private String familyName;		// Transient
	private String email;			// Transient

	private Long user_type;
	private UserExtra userExtra;
	
	/**
	 * getId
	 * 
	 * Gets the id of the user
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		17/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @return The id of the domain
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId()
	{
		return id;
	}

	/**
	 * setId
	 * 
	 * Sets the id of the user
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		17/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param id The id of the domain
	 */
	protected void setId(Long id)
	{
		this.id = id;
	}

	/**
	 * getUsername
	 * 
	 * Gets the username of the user
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		17/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @return
	 */
	@Column(name="username")
	public String getUsername() {
		return username;
	}
	/**
	 * setUsername
	 * 
	 * Sets the username of the user
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		17/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param username The username of the user
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * getPassword
	 * 
	 * Gets the password of the user
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		17/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @return
	 */
	@Column(name="password")
	public String getPassword() {
		return password;
	}

	/**
	 * setPassword
	 * 
	 * Sets the password of the user
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		17/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param username The username of the user
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * getEnabled
	 * 
	 * Gets whether the user is enabled
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		17/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @return
	 */
	@Column(name="enabled")
	public Boolean getEnabled() {
		return enabled;
	}

	/**
	 * setEnabled
	 * 
	 * Sets whether the user is enabled
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		17/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param enabled Whether the user is enabled
	 */
	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	@Transient
	public String getDisplayName()
	{
		return displayName;
	}

	@Transient
	public String getGivenName()
	{
		return givenName;
	}

	@Transient
	public String getFamilyName()
	{
		return familyName;
	}

	@Transient
	public String getEmail()
	{
		return email;
	}

	@PostLoad
	@PostPersist
	public void getPersonDetails()
	{
		boolean detailsFound = true;
		if (user_type.longValue() == 1) {
			LdapPerson ldapPerson;
			LdapRequest ldapReq = new LdapRequest();
			try
			{
				ldapPerson = ldapReq.searchUniId(username);
				displayName = ldapPerson.getDisplayName();
				givenName = ldapPerson.getGivenName();
				familyName = ldapPerson.getFamilyName();
				email = ldapPerson.getEmail();
			}
			catch (Exception e)
			{
				//TODO retrieve details for users that are no longer at ANU
				detailsFound = false;
			}
		}
		else if (user_type.longValue() == 2) {
			if (userExtra == null ) {
				LOGGER.error("User {} does not have any details", id);
				detailsFound = false;
			}
			else {
				UserRegistered user_registered = (UserRegistered) userExtra;
				displayName = user_registered.getGiven_name() + " " + user_registered.getLast_name();
				givenName = user_registered.getGiven_name();
				familyName = user_registered.getLast_name();
				email = username;
			}
		}
		else if (user_type.longValue() == 3) {
			if (userExtra == null) {
				LOGGER.error("User : {} does not have any details", id);
				detailsFound = false;
			}
			else {
				UserShibboleth shibbolethUser = (UserShibboleth) userExtra;
				LOGGER.info("Shibboleth User {} email is {}", shibbolethUser.getDisplayName(), shibbolethUser.getEmail());
				displayName = shibbolethUser.getDisplayName();
				givenName = "";
				familyName = "";
				email = shibbolethUser.getEmail();
			}
		}
		else {
			detailsFound = false;
		}
		if (!detailsFound) {
			LOGGER.error("Unable to find details for user: {}", id);
			displayName = "";
			givenName = "";
			familyName = "";
			email = "";
		}
	}
	
	/**
	 * getUser_type
	 * 
	 * Sets the users type e.g. ANU User or Registered User
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		17/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @return The type of user
	 */
	@Column(name="user_type")
	public Long getUser_type() {
		return user_type;
	}


	/**
	 * setUser_type
	 * 
	 * Sets the users type e.g. ANU User or Registered User
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		17/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param user_type The type of user
	 */
	public void setUser_type(Long user_type) {
		this.user_type = user_type;
	}

	/**
	 * Get t he extra user information.  This will be dependant upon what type of user object it is (i.e. a Shibboleth User or Registered User).
	 * 
	 * @return The extra user information object
	 */
	@OneToOne (cascade=CascadeType.ALL, fetch = FetchType.EAGER)
	@PrimaryKeyJoinColumn
	public UserExtra getUserExtra() {
		return userExtra;
	}

	/**
	 * Set t he extra user information.  This will be dependant upon what type of user object it is (i.e. a Shibboleth User or Registered User).
	 * 
	 * @param userExtra The extra user information object
	 */
	public void setUserExtra(UserExtra userExtra) {
		this.userExtra = userExtra;
	}
}
