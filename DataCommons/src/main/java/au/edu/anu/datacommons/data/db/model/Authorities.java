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
