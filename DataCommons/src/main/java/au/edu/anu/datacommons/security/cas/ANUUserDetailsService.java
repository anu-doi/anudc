/*******************************************************************************
 * Australian National University Data Commons
 * Copyright (C) 2013  The Australian National University
 * 
 * This file is part of Australian National University Data Commons.
 * 
 * Australian National University Data Commons is free software: you
 * can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package au.edu.anu.datacommons.security.cas;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;

import au.edu.anu.datacommons.data.db.dao.UsersDAO;
import au.edu.anu.datacommons.data.db.dao.UsersDAOImpl;
import au.edu.anu.datacommons.data.db.model.Users;
import au.edu.anu.datacommons.security.CustomUser;


/**
 * ANUUserDetailsService
 * 
 * Australian National University Data Commons
 * 
 * The ANUUserDetailsService class adds default roles to an ANU User logged in via CAS.
 * The roles currently include 'ROLE_ANU_USER' and 'ROLE_REGISTERED'.  It also provides
 * custom user information.
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		26/04/2012	Genevieve Turner (GT)	Initial
 * 0.2		16/05/2012	Genevieve Turner (GT)	Updated to use a custom user
 * 0.3		17/05/2012	Genevieve Turner (GT)	Renamed loadCustomUser to createUserDetails 
 * 0.4		17/05/2012	Geneiveve Turner (GT)	Updated to insert user into database when they log in 
 * 0.5		23/05/2012	Genevieve Turner (GT)	Updated for display name
 * 0.6		13/09/2012	Genevieve Turner (GT)	Updated so that it does not matter if the user logs in via upper or lowercase DCO-168
 * 0.7		19/09/2012	Genevieve Turner (GT)	Updates so that the display name is not null when it is a new user logging in
 * </pre>
 * 
 */
public class ANUUserDetailsService extends JdbcDaoImpl {
	static final Logger LOGGER = LoggerFactory.getLogger(ANUUserDetailsService.class);
	
	private boolean enableAuthorities = true;
	private boolean enableGroups = false;
	
	/**
	 * loadUserByUsername
	 * 
	 * Overrides the loadUserByUsername class so that the user is not required to be
	 * in the database to be able to log in.  It still retrieves additional permissions
	 * for the user if they exist.
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		29/03/2012	Genevieve Turner (GT)	Added
	 * 0.2		16/05/2012	Genevieve Turner (GT)	Updated to use a custom user
	 * 0.3		17/05/2012	Genevieve Turner (GT)	Updated to use createUserDetails function
	 * 0.6		13/09/2012	Genevieve Turner (GT)	Updated so that it does not matter if the user logs in via upper or lowercase DCO-168
	 * </pre>
	 * 
	 * @param username The username of the person logging in
	 * @return Returns information about the user
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		username = username.toLowerCase();
		
		Set<GrantedAuthority> dbAuthsSet = new HashSet<GrantedAuthority>();
		
		if (enableAuthorities) {
			dbAuthsSet.addAll(loadUserAuthorities(username));
		}
		
		if (enableGroups) {
			dbAuthsSet.addAll(loadGroupAuthorities(username));
		}
		
		List<GrantedAuthority> dbAuths = new ArrayList<GrantedAuthority>(dbAuthsSet);
		
		addCustomAuthorities(username, dbAuths);
		
		if (dbAuths.size() == 0) {
			logger.debug("User '" + username + "' has no authorities and will be treated as 'not found'");
		
			throw new UsernameNotFoundException(
					messages.getMessage("JdbcDaoImpl.noAuthority",
							new Object[] {username}, "User {0} has no GrantedAuthority"), username);
		}
		
		return createUserDetails(username, null, dbAuths);
	}
	
	/**
	 * createUserDetails
	 * 
	 * Loads the details of a custom user
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		16/05/2012	Genevieve Turner (GT)	Updated to use a custom user
	 * 0.3		17/05/2012	Genevieve Turner (GT)	Renamed from loadCustomUser to createUserDetails
	 * 0.4		17/05/2012	Genevieve Turner (GT)	Updated to insert user into database when they log in
	 * 0.5		23/05/2012	Genevieve Turner (GT)	Updated for display name
	 * 0.7		19/09/2012	Genevieve Turner (GT)	Updates so that the display name is not null when it is a new user logging in
	 * </pre>
	 * 
	 * @param username The username of the person logging in
	 * @param userDetails Details about the user logging in
	 * @param authorities The authorities for the user logging in
	 * @return The custom user
	 */
	protected UserDetails createUserDetails(String username, UserDetails userDetails, List<GrantedAuthority> authorities) {
		UsersDAO usersDAO = new UsersDAOImpl(Users.class);
		Users users = usersDAO.getUserByName(username);
		CustomUser user = null;
		if (users != null) {
			LOGGER.info("displayName: {})", users.getDisplayName());
		}
		else {
			Users newUser = new Users();
			newUser.setUsername(username);
			newUser.setPassword(username);
			newUser.setEnabled(Boolean.TRUE);
			newUser.setUser_type(new Long(1));
			usersDAO.create(newUser);
			users = usersDAO.getSingleById(newUser.getId());
			LOGGER.info("New User displayName: {})", users.getDisplayName());
		}
		user = new CustomUser(users.getUsername(), users.getPassword(), true, true, true, true, authorities, users.getId(), users.getDisplayName());
		return user;
	}

	/**
	 * addCustomAuthorities
	 * 
	 * Adds custom authorities to the logged in user.  Currently these include 'ROLE_ANU_USER'
	 * and 'ROLE_REGISTERED'
	 * 
	 * Version	Date		Developer				Description
	 * 0.1		26/04/2012	Genevieve Turner (GT)	Added
	 * 
	 * @param username The username of the person logging in
	 * @param authorities A list of the users authorities
	 */
	protected void addCustomAuthorities(String username, List<GrantedAuthority> authorities) {
		authorities.add(new GrantedAuthorityImpl("ROLE_ANU_USER"));
		authorities.add(new GrantedAuthorityImpl("ROLE_REGISTERED"));
	}
}
