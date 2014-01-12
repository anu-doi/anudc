package au.edu.anu.datacommons.security.shibboleth;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;

import au.edu.anu.datacommons.data.db.PersistenceManager;
import au.edu.anu.datacommons.data.db.dao.GenericDAO;
import au.edu.anu.datacommons.data.db.dao.GenericDAOImpl;
import au.edu.anu.datacommons.data.db.dao.UsersDAO;
import au.edu.anu.datacommons.data.db.dao.UsersDAOImpl;
import au.edu.anu.datacommons.data.db.model.Authorities;
import au.edu.anu.datacommons.data.db.model.AuthoritiesPK;
import au.edu.anu.datacommons.data.db.model.UserShibboleth;
import au.edu.anu.datacommons.data.db.model.Users;
import au.edu.anu.datacommons.security.CustomUser;

public class ShibbolethUserDetailsManager implements UserDetailsManager {
	static final Logger LOGGER = LoggerFactory.getLogger(ShibbolethUserDetailsManager.class);

	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException, DataAccessException {
		LOGGER.debug("In loadUserByUsername");
		
		List<UserDetails> users = loadUsersByUsername(username);
		if (users.size() == 0) {
			throw new UsernameNotFoundException("Username " + username + " not found");
		}
		
		UserDetails user = users.get(0);
		
		Set<GrantedAuthority> dbAuthsSet = new HashSet<GrantedAuthority>();
		dbAuthsSet.addAll(loadUserAuthorities(username));
		
		List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>(dbAuthsSet);
		
		return createUserDetails(username, user, grantedAuthorities);
	}
	
	/**
	 * Load information about users with the given username
	 * 
	 * @param username The username to find information for
	 * @return The users
	 */
	protected List<UserDetails> loadUsersByUsername(String username) {
		LOGGER.info("In loadUsersByUsername");
		
		EntityManager em = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
		Query query = em.createQuery("SELECT u FROM Users u join fetch u.userExtra e WHERE username = :username");
		query.setParameter("username", username);
		List<Users> users = query.getResultList();
		List<UserDetails> anuUsers = new ArrayList<UserDetails>();
		for (Users user : users) {
			UserShibboleth shibbolethUser = (UserShibboleth) user.getUserExtra();
			LOGGER.info("Shibboleth User: {}, username: {}, email address: {}", user.getUsername(), shibbolethUser.getDisplayName(), shibbolethUser.getEmail());
			
			CustomUser customUser = new CustomUser(user.getUsername(), user.getUsername(), true, true, true, true, new ArrayList<GrantedAuthority>(),
					user.getId(), shibbolethUser.getDisplayName(), shibbolethUser.getEmail(), shibbolethUser.getInstitution());
			anuUsers.add(customUser);
		}
		
		return anuUsers;
	}
	
	/**
	 * Load the authorities associated with the given username
	 * 
	 * @param username The username
	 * @return The granted authorities
	 */
	protected List<GrantedAuthority> loadUserAuthorities(String username) {
		LOGGER.debug("In loadUserAuthorities");
		EntityManager em = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
		Query query = em.createQuery("SELECT a FROM Authorities a WHERE a.id.username = :username");
		query.setParameter("username", username);
		List<Authorities> authorities = query.getResultList();
		List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
		for (Authorities authority : authorities) {
			grantedAuthorities.add(new GrantedAuthorityImpl(authority.getId().getAuthority()));
		}
		
		return grantedAuthorities;
	}
	
	/**
	 * Generate the UserDetails object from the provided information.
	 * 
	 * @param username The username
	 * @param userFromUserQuery The user found from  previously found queries
	 * @param combinedAuthorities The authorities the user has
	 * @return The user details
	 */
	protected UserDetails createUserDetails(String username, UserDetails userFromUserQuery, List<GrantedAuthority> combinedAuthorities) {
		LOGGER.debug("In createUserDetails");
		CustomUser user = (CustomUser) userFromUserQuery;
		return new CustomUser(user.getUsername(), user.getPassword(), user.isEnabled(), user.isAccountNonExpired(), user.isCredentialsNonExpired(),
				user.isAccountNonLocked(), combinedAuthorities, user.getId(), user.getDisplayName(), user.getEmail(), user.getInstitution());
	}

	@Override
	public void changePassword(String oldPassword, String newPassword) {
		//No password changing for shibboleth users;
	}

	@Override
	public void createUser(UserDetails userDetails) {
		LOGGER.debug("In createUser");
		CustomUser customUser = (CustomUser) userDetails;
		String displayName = customUser.getDisplayName();
		String institution = customUser.getInstitution();
		String email = customUser.getEmail();
		
		Users user = new Users();
		user.setEnabled(Boolean.TRUE);
		user.setUsername(customUser.getUsername());
		user.setPassword(customUser.getUsername());
		user.setUser_type(new Long(3));
		
		UsersDAO userDAO = new UsersDAOImpl(Users.class);
		user = userDAO.create(user);
		
		UserShibboleth userShibboleth = new UserShibboleth(user.getId(), displayName, email, institution);
		user.setUserExtra(userShibboleth);
		
		userDAO.update(user);
		
		GenericDAO<Authorities, String> authorityDAO = new GenericDAOImpl<Authorities, String>(Authorities.class);
		
		Authorities authority = new Authorities();
		AuthoritiesPK authorityPk = new AuthoritiesPK();
		
		authorityPk.setUsername(user.getUsername());
		authorityPk.setAuthority("ROLE_REGISTERED");
		authority.setId(authorityPk);
		authorityDAO.create(authority);
		if ("Australian National University".equals(institution)) {
			authority = new Authorities();
			authorityPk = new AuthoritiesPK();
			authorityPk.setUsername(user.getUsername());
			authorityPk.setAuthority("ROLE_ANU_USER");
			authority.setId(authorityPk);
			authorityDAO.create(authority);
		}
	}
	
	@Override
	public void deleteUser(String username) {
		//Requires manual delete
	}

	@Override
	public void updateUser(UserDetails userDetails) {
		LOGGER.debug("In updateUser");
		
		CustomUser customUser = (CustomUser) userDetails;
		
		String displayName = customUser.getDisplayName();
		String institution = customUser.getInstitution();
		String email = customUser.getEmail();
		
		UsersDAO userDAO = new UsersDAOImpl(Users.class);
		Users user = userDAO.getUserByName(customUser.getUsername());
		UserShibboleth shibbolethUser = (UserShibboleth) user.getUserExtra();
		shibbolethUser.setDisplayName(displayName);
		shibbolethUser.setInstitution(institution);
		shibbolethUser.setEmail(email);
		user = userDAO.update(user);
	}

	@Override
	public boolean userExists(String username) {
		LOGGER.debug("In userExists");
		UsersDAO userDAO = new UsersDAOImpl(Users.class);
		Users user = userDAO.getUserByName(username);
		if (user != null) {
			return true;
		}
		return false;
	}

}
