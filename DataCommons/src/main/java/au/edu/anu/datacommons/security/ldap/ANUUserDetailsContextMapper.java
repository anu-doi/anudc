package au.edu.anu.datacommons.security.ldap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;

import au.edu.anu.datacommons.data.db.dao.UsersDAO;
import au.edu.anu.datacommons.data.db.dao.UsersDAOImpl;
import au.edu.anu.datacommons.data.db.model.Users;
import au.edu.anu.datacommons.security.CustomUser;

/**
 * CustomLDAPUserDetailsContextMapper
 * 
 * Australian National University Data Commons
 * 
 * The CustomLDAPUserDetailsContextMapper class adds default roles to an ANU User logging
 * in via the ANU LDAP.
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		11/07/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
public class ANUUserDetailsContextMapper implements
		UserDetailsContextMapper {
	static final Logger LOGGER = LoggerFactory.getLogger(ANUUserDetailsContextMapper.class);

	/**
	 * mapUserFromContext
	 * 
	 * Maps ldap information to the user.  This implementation sets the users roles
	 * from a set of defaults and from the database.
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		11/07/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param ctx Details about the user logging in retrieved from ldap
	 * @param username The username of the user logging in
	 * @param authorities A list of authorities given to the user
	 * @return The user details
	 * @see org.springframework.security.ldap.userdetails.UserDetailsContextMapper#mapUserFromContext(org.springframework.ldap.core.DirContextOperations, java.lang.String, java.util.Collection)
	 */
	@Override
	public UserDetails mapUserFromContext(DirContextOperations ctx,
			String username, Collection<GrantedAuthority> authorities) {
		//TODO Retrieve authorities from the database
		List<GrantedAuthority> authoritiesList = new ArrayList<GrantedAuthority>(authorities);
		addCustomAuthorities(username, authoritiesList);
		
		UsersDAO usersDAO = new UsersDAOImpl(Users.class);
		Users users = usersDAO.getUserByName(username);
		
		CustomUser user = null;
		if (users != null) {
			user = new CustomUser(users.getUsername(), users.getPassword(), true, true, true, true, authoritiesList, users.getId(), users.getDisplayName());
		}
		else {
			Users newUser = new Users();
			newUser.setUsername(username);
			newUser.setPassword(username);
			newUser.setEnabled(Boolean.TRUE);
			newUser.setUser_type(new Long(1));
			usersDAO.create(newUser);

			LOGGER.info("New User displayName: {})", newUser.getDisplayName());
			user = new CustomUser(newUser.getUsername(), newUser.getPassword(), true, true, true, true, authoritiesList, newUser.getId(), newUser.getDisplayName());
		}
		LOGGER.info("Setting user details?");
		return user;
	}

	/**
	 * mapUserToContext
	 * 
	 * Currently not implemented
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		11/07/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param user User information stored in the context
	 * @param ctx LDAP context information
	 * @see org.springframework.security.ldap.userdetails.UserDetailsContextMapper#mapUserToContext(org.springframework.security.core.userdetails.UserDetails, org.springframework.ldap.core.DirContextAdapter)
	 */
	@Override
	public void mapUserToContext(UserDetails user, DirContextAdapter ctx) {
		// Not implemented
	}

	/**
	 * addCustomAuthorities
	 *
	 * Adds custom authorities to the logged in user.  Currently these include 'ROLE_ANU_USER'
	 * and 'ROLE_REGISTERED'
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		11/07/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param username The username of the person logging in
	 * @param authorities A list of the users authorities
	 */
	private void addCustomAuthorities(String username, List<GrantedAuthority> authorities) {
		authorities.add(new GrantedAuthorityImpl("ROLE_ANU_USER"));
		authorities.add(new GrantedAuthorityImpl("ROLE_REGISTERED"));
	}
}
