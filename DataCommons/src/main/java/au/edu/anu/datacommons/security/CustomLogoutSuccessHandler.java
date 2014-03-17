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
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import au.edu.anu.datacommons.data.db.dao.UsersDAO;
import au.edu.anu.datacommons.data.db.dao.UsersDAOImpl;
import au.edu.anu.datacommons.data.db.model.UserShibboleth;
import au.edu.anu.datacommons.data.db.model.Users;

/**
 * CustomLogoutSuccessHandler
 *
 * Australian National University Data Commons
 * 
 * This is a custom logout success handler.  Its purpose is to also log the user out of the local service provider for shibboleth
 * users when the logout button is clicked.
 *
 * JUnit coverage:
 * None
 * 
 * @author Genevieve Turner
 *
 */
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {
	static final Logger LOGGER = LoggerFactory.getLogger(CustomLogoutSuccessHandler.class);

	@Override
	public void onLogoutSuccess(HttpServletRequest request,
			HttpServletResponse response, Authentication authentication) throws IOException,
			ServletException {
		LOGGER.info("Determining URL to redirect user to...");
		
		CustomUser customUser = (CustomUser) authentication.getPrincipal();

		UsersDAO userDAO = new UsersDAOImpl(Users.class);
		Users user = userDAO.getSingleById(customUser.getId());
		StringBuilder sb = new StringBuilder();
		
		if (user.getUserExtra() instanceof UserShibboleth) {
			sb.append(getServerBaseURL(request));
			sb.append("/Shibboleth.sso/Logout?return=");
		}
		sb.append(getServerBaseURL(request));
		sb.append(request.getContextPath());

		LOGGER.info("Redirect URL: {}", sb.toString());
		response.sendRedirect(sb.toString());
		return;
	}
	
	/**
	 * Get the server url without the context
	 * 
	 * @param request The request object
	 * @return The server url string
	 */
	private String getServerBaseURL(HttpServletRequest request) {
		StringBuilder sb = new StringBuilder();
		sb.append(request.getScheme());
		sb.append("://");
		sb.append(request.getServerName());
		int serverPort = request.getServerPort();
		if ((serverPort != 80) && (serverPort != 443)) {
			sb.append(":");
			sb.append(serverPort);
		}
		return sb.toString();
	}

}
