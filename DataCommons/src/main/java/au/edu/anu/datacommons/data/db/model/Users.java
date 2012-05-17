package au.edu.anu.datacommons.data.db.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

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
@Table(name="users")
public class Users {
	private Long id;
	private String username;
	private String password;
	private Boolean enabled;
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
	public Long getId() {
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
	public void setId(Long id) {
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
