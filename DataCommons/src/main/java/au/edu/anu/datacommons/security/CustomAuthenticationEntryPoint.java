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
 * 0.2		12/07/2012	Rahul Khanna (RK)		Added auth header if request from Bagit.
 * 0.3		31/10/2012	Genevieve Turner (GT)	Updated to redirect to a login landing page
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
	 * 0.2		12/07/2012	Rahul Khanna (RK)		Added auth header if request from Bagit.
	 * 0.3		31/10/2012	Genevieve Turner (GT)	Updated to redirect to a login landing page
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
		// Add auth header if the request is from bagit.
		String userAgent = request.getHeader("User-Agent");
		if (userAgent == null || userAgent.length() == 0 || userAgent.equals("BagIt Library Parallel Fetcher") || userAgent.indexOf("DataCommons") != -1)
		{
			response.addHeader("WWW-Authenticate", "Basic realm=\"Spring Security Application\"");
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
		}
		else
		{
			response.sendRedirect(request.getServletContext().getContextPath() + "/login-select");
//			response.sendRedirect(request.getServletContext().getContextPath() + "/login");
		}
	}

}
