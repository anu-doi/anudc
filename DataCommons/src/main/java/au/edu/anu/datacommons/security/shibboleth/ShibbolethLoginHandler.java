package au.edu.anu.datacommons.security.shibboleth;

import javax.servlet.http.HttpServletRequest;

/**
 * ShibbolethLoginHandler
 *
 * Australian National University Data Commons
 * 
 * Login handler for Shibboleth
 *
 * JUnit coverage:
 * None
 * 
 * @author Genevieve Turner
 *
 */
public interface ShibbolethLoginHandler {
	/**
	 * Process the login for a user the system has not seen before.
	 * 
	 * @param id The id of the user logging in
	 * @param request The http request for the login
	 */
	public void newUserLogin(String id, HttpServletRequest request);
	
	/**
	 * Process the login for a user the system has seen before.
	 * 
	 * @param id The id of the user logging in
	 * @param request The http request for the login
	 */
	public void existingUserLogin(String id, HttpServletRequest request);
}
