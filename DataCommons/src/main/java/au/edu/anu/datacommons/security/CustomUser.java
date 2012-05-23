package au.edu.anu.datacommons.security;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

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
 * </pre>
 * 
 */
public class CustomUser extends User {
	private static final long serialVersionUID = 1L;
	private long id;
	private String displayName;
	
	/**
	 * Constructor
	 * 
	 * Constructor to build the custom user
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/04/2012	Genevieve Turner (GT)	Initial
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
			List<GrantedAuthority> authorities, long id, String displayName) throws IllegalArgumentException {
		super(username, password, enabled, accountNonExpired, credentialsNonExpired,
				accountNonLocked, authorities);
		this.id = id;
		this.displayName = displayName;
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
