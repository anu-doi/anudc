package au.edu.anu.datacommons.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import au.edu.anu.datacommons.security.cas.ANUUserDetailsService;

/**
 * CustomAuthenticationEntryPoint
 * 
 * Australian National University Data Commons
 * 
 * This is a custom AuthenticationEntryPoint so that a 401 response is returned if the
 * user is not logged in and is attempting to access page that requires authentication.
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
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
	static final Logger LOGGER = LoggerFactory.getLogger(ANUUserDetailsService.class);

	/**
	 * commence
	 * 
	 * Method that returns a HTTP 401 response if the user is unauthorised.
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		11/07/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param request HTTP request information
	 * @param response HTTP resposne information
	 * @param authenticationException Exception that occured
	 * @throws IOException 
	 * @throws ServletException
	 * @see org.springframework.security.web.AuthenticationEntryPoint#commence(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.springframework.security.core.AuthenticationException)
	 */
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authenticationException) throws IOException, ServletException {
		response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
	}

}
