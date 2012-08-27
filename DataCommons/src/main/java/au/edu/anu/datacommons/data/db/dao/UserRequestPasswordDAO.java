package au.edu.anu.datacommons.data.db.dao;

import au.edu.anu.datacommons.data.db.model.UserRequestPassword;

/**
 * UserRequestPasswordDAO
 * 
 * Australian National University Data Commons
 * 
 * DAO for the UserRequestPassword class
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		27/08/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
public interface UserRequestPasswordDAO  extends GenericDAO<UserRequestPassword, Long> {
	/**
	 * getByLink
	 *
	 * Gets a UserRequestPassword given the specified link
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		27/08/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param link The link to retrieve the UserRequestPassword for
	 * @return The password change request information
	 */
	public UserRequestPassword getByLink(String link);
}
