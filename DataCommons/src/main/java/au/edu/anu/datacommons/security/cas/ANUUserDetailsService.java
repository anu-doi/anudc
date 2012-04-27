package au.edu.anu.datacommons.security.cas;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;


/**
 * ANUUserDetailsService
 * 
 * Australian National University Data Commons
 * 
 * The ANUUserDetailsService class adds default roles to an ANU User logged in via CAS.
 * The roles currently include 'ROLE_ANU_USER' and 'ROLE_REGISTERED'.
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		26/04/2012	Genevieve Turner (GT)	Initial
 * </pre>
 * 
 */
public class ANUUserDetailsService extends JdbcDaoImpl {
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
	 * </pre>
	 * 
	 * @param username The username of the person logging in
	 * @return Returns information about the user
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserDetails user =  new User(username, username, true, true, true, true, AuthorityUtils.NO_AUTHORITIES);

		Set<GrantedAuthority> dbAuthsSet = new HashSet<GrantedAuthority>();

        if (enableAuthorities) {
            dbAuthsSet.addAll(loadUserAuthorities(user.getUsername()));
        }

        if (enableGroups) {
            dbAuthsSet.addAll(loadGroupAuthorities(user.getUsername()));
        }

        List<GrantedAuthority> dbAuths = new ArrayList<GrantedAuthority>(dbAuthsSet);

        addCustomAuthorities(user.getUsername(), dbAuths);

        if (dbAuths.size() == 0) {
            logger.debug("User '" + username + "' has no authorities and will be treated as 'not found'");

            throw new UsernameNotFoundException(
                    messages.getMessage("JdbcDaoImpl.noAuthority",
                            new Object[] {username}, "User {0} has no GrantedAuthority"), username);
        }

        return createUserDetails(username, user, dbAuths);
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
