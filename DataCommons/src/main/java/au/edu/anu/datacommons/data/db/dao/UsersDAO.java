package au.edu.anu.datacommons.data.db.dao;

import au.edu.anu.datacommons.data.db.model.Users;

/**
 * UsersDAO
 * 
 * Australian National University Data Commons
 * 
 * Class to perform actions with users in the database
 * 
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		16/05/2012	Genevieve Turner (GT)	Initial
 * </pre>
 * 
 */
public interface UsersDAO extends GenericDAO<Users, Long> {
	/**
	 * getUserByName
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		16/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param The username of the object to return
	 * @return The object with the username
	 */
	public Users getUserByName(String username);
}
