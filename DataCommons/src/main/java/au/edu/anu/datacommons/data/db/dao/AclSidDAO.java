package au.edu.anu.datacommons.data.db.dao;

import au.edu.anu.datacommons.data.db.model.AclSid;

/**
 * AclSidDAO
 * 
 * Australian National University Data Commons
 * 
 * Data Access Object Interface for the acl_sid table
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
public interface AclSidDAO extends GenericDAO<AclSid, Long>  {
	/**
	 * getAclSidByUsername
	 *
	 * Retreive the acl sid row by the username
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		28/08/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param username Username to retrieve acl sid for
	 * @return The acl sid
	 */
	public AclSid getAclSidByUsername(String username);
}
