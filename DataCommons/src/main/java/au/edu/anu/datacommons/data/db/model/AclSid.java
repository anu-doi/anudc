package au.edu.anu.datacommons.data.db.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * AclSid
 * 
 * Australian National University Data Commons
 * 
 * Class for the acl_sid table
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		28/08/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
@Entity
@Table(name="acl_sid")
public class AclSid {
	private Long id;
	private Boolean principal;
	private String sid;
	/**
	 * getId
	 *
	 * Get the id
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		28/08/2012	Genevieve Turner(GT)	Initial
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
	 * 0.1		28/08/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}
	
	/**
	 * getPrincipal
	 *
	 * Get whether it is a user or a role
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		28/08/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the principal, true if a user, false if a role
	 */
	public Boolean getPrincipal() {
		return principal;
	}
	
	/**
	 * setPrincipal
	 *
	 * Set whether it is a user or a role
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		28/08/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param principal the principal to set, true if a user, false if a role
	 */
	public void setPrincipal(Boolean principal) {
		this.principal = principal;
	}
	
	/**
	 * getSid
	 *
	 * Get the sid (username)
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		28/08/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the sid
	 */
	public String getSid() {
		return sid;
	}
	
	/**
	 * setSid
	 *
	 * Set the sid (username)
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		28/08/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param sid the sid to set
	 */
	public void setSid(String sid) {
		this.sid = sid;
	}
}
