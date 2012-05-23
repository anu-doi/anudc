package au.edu.anu.datacommons.security.registered;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;

import au.edu.anu.datacommons.data.db.dao.UsersDAO;
import au.edu.anu.datacommons.data.db.dao.UsersDAOImpl;
import au.edu.anu.datacommons.data.db.model.Users;
import au.edu.anu.datacommons.security.CustomUser;

/**
 * RegisteredUserDetails
 * 
 * Australian National University Data Commons
 * 
 * The RegisteredUserDetails class utilises a custom user to provide extra user information
 * utilised within the application
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		17/05/2012	Genevieve Turner (GT)	Initial
 * </pre>
 * 
 */
public class RegisteredUserDetails extends JdbcDaoImpl {
	static final Logger LOGGER = LoggerFactory.getLogger(RegisteredUserDetails.class);
	
	/**
	 * createUserDetails
	 * 
	 * Sets the user details.  This method creates a custom user details method.
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		17/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param username The username of the person logging in
	 * @param userDetails Details about the user logging in
	 * @param authorities The authorities for the user logging in
	 * @return The custom user
	 */
	@Override
	protected UserDetails createUserDetails(String username, UserDetails userDetails, List<GrantedAuthority> authorities) {
		LOGGER.info("In createUserDetails");
		UsersDAO usersDAO = new UsersDAOImpl(Users.class);
		Users users = usersDAO.getUserByName(username);
		CustomUser user = null;
		LOGGER.info("displayName: {})", users.getDisplayName());
		if (users != null) {
			user = new CustomUser(userDetails.getUsername(), userDetails.getPassword(), userDetails.isEnabled(), true, true, true, authorities, users.getId(), users.getDisplayName());
		}
		else {
			LOGGER.error("User {} not found in the database", username);
		}
		return user;
	}
}
