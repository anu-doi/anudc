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
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.SavedRequest;

/**
 * CustomSuccessHandler
 * 
 * Australian National University Data Commons
 * 
 * Custom functions for handling successful logins.
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		13/09/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
public class CustomSuccessHandler extends
		SavedRequestAwareAuthenticationSuccessHandler {
	static final Logger LOGGER = LoggerFactory.getLogger(CustomSuccessHandler.class);
	
	/**
	 * onAuthenticationSuccess
	 * 
	 * Handles a successful login, it redirects to the referer if it exsits, it then continues on
	 * with standard processing for SavedRequestAwareAuthenticatioNSuccessHandlers.
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		13/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param request The request information
	 * @param response The resposne information
	 * @param authentication Authentication information
	 * @throws IOException
	 * @throws ServletException
	 * @see org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler#onAuthenticationSuccess(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.springframework.security.core.Authentication)
	 */
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) 
			throws IOException, ServletException {
		Object savedObject = request.getSession().getAttribute("SPRING_SECURITY_SAVED_REQUEST");
		if (savedObject instanceof SavedRequest) {
			SavedRequest savedRequest = (SavedRequest) savedObject;
			List<String> referer = savedRequest.getHeaderValues("Referer");
			if (referer.size() > 0) {
				referer.get(0);
				getRedirectStrategy().sendRedirect(request, response, referer.get(0));
				return;
			}
		}
		super.onAuthenticationSuccess(request, response, authentication);
	}
}
