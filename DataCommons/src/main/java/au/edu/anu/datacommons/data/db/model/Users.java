package au.edu.anu.datacommons.data.db.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PostLoad;
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
	private UserRegistered user_registered;
	
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
			if (user_registered == null ) {
				LOGGER.error("User {} does not have any details", id);
				detailsFound = false;
			}
			else {
				displayName = user_registered.getGiven_name() + " " + user_registered.getLast_name();
				givenName = user_registered.getGiven_name();
				familyName = user_registered.getLast_name();
				email = username;
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
	 * getUser_registered
	 * 
	 * Gets information about the registered user
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		17/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @return Registered user information
	 */
	@OneToOne (cascade=CascadeType.ALL) //, mappedBy="user")
	@PrimaryKeyJoinColumn
	public UserRegistered getUser_registered() {
		return user_registered;
	}

	/**
	 * setUser_registered
	 * 
	 * Sets information about the registered user
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		17/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param user_registered
	 */
	public void setUser_registered(UserRegistered user_registered) {
		this.user_registered = user_registered;
	}
}
