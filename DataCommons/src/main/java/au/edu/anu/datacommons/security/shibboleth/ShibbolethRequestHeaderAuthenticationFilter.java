package au.edu.anu.datacommons.security.shibboleth;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;
import org.springframework.util.Assert;

public class ShibbolethRequestHeaderAuthenticationFilter extends
		RequestHeaderAuthenticationFilter {
	static final Logger LOGGER = LoggerFactory.getLogger(ShibbolethRequestHeaderAuthenticationFilter.class);

	private UserDetailsManager userDetailsManager;
	private ShibbolethLoginHandler loginHandler;
	private boolean enable = true;
	
	/**
	 * Set the user details manager
	 * 
	 * @param userDetailsManager The user details manager
	 */
	public void setUserDetailsManager(UserDetailsManager userDetailsManager) {
		LOGGER.info("In setUserDetailsManager");
		this.userDetailsManager = userDetailsManager;
	}
	
	/**
	 * Set the login handler
	 * 
	 * @param loginHandler THe login handler
	 */
	public void setShibbolethLoginHandler(ShibbolethLoginHandler loginHandler) {
		LOGGER.info("In setShibbolethLoginHandler");;
		this.loginHandler = loginHandler;
	}
	
	/**
	 * Indicate whether this authentication filter is enabled
	 * 
	 * @param enabled
	 */
	public void setEnabled(boolean enabled) {
		LOGGER.info("In setEnabled");
		this.enable = enabled;
	}
	
	@Override
	public void afterPropertiesSet() {
		super.afterPropertiesSet();
		LOGGER.info("In afterPropertiesSet");;
		Assert.notNull(userDetailsManager, "An UserDetailsManager must be set");
		Assert.notNull(loginHandler, "A ShibbolethLoginHandler must be set");
	}
	
	@Override
	protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
		LOGGER.info("In getPreAuthenticatedPrincipal for ShibbolethRequestHeaderAuthenticationFilter");
		if (!enable) {
			return null;
		}
		String o = (String) request.getAttribute("persistent-id");
		if (o != null && !o.equals("")) {
			if (!userDetailsManager.userExists(o)) {
				loginHandler.newUserLogin(o, request);
			}
			else {
				loginHandler.existingUserLogin(o, request);
			}
		}
		return o;
	}
}
