package au.edu.anu.datacommons.security.shibboleth;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;

import au.edu.anu.datacommons.security.CustomUser;

public class ShibbolethLoginHandlerImpl implements ShibbolethLoginHandler {
	static final Logger LOGGER = LoggerFactory.getLogger(ShibbolethLoginHandler.class);
	
	private ShibbolethUserDetailsManager userDetailsManager;
	private boolean createUsers = true;
	
	public void setUserDetailsManager(ShibbolethUserDetailsManager userDetailsManager) {
		this.userDetailsManager = userDetailsManager;
	}
	
	public void setCreateUsers(boolean on) {
		createUsers = on;
	}

	@Override
	public void newUserLogin(String username, HttpServletRequest request) {
		LOGGER.info("In new User Login");
		LOGGER.info("Username is: {}", username);
		if (createUsers) {
			String displayName = (String) request.getAttribute("displayName");
			String institution = (String) request.getAttribute("o");
			String email = (String) request.getAttribute("mail");
			
			CustomUser customUser = new CustomUser(username, username, true, true, true, true, new ArrayList<GrantedAuthority>(), null, displayName, email, institution);
			userDetailsManager.createUser(customUser);
		}
	}

	@Override
	public void existingUserLogin(String username, HttpServletRequest request) {
		LOGGER.info("In existingUserLogin");
		
		String displayName = (String) request.getAttribute("displayName");
		String institution = (String) request.getAttribute("o");
		String email = (String) request.getAttribute("mail");
		CustomUser user = (CustomUser) userDetailsManager.loadUserByUsername(username);
		user.setDisplayName(displayName);
		user.setInstitution(institution);
		user.setEmail(email);
	}

}
