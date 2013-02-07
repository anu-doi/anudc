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

package au.edu.anu.datacommons.security.tokenauth;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedCredentialsNotFoundException;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;

import au.edu.anu.datacommons.config.Config;
import au.edu.anu.datacommons.config.PropertiesFile;

/**
 * This class allows for token-based authentication where a token specified in the HTTP request header 'X-Auth-Token' allows the request to be authenticated to a
 * predefined user. Usernames and their associated tokens are stored in a properties file.  
 */
public class TokenHeaderAuthenticationFilter extends AbstractPreAuthenticatedProcessingFilter
{
	private static final Logger LOGGER = LoggerFactory.getLogger(TokenHeaderAuthenticationFilter.class);
	
	private static final String TOKEN_HEADER = "X-Auth-Token";
	private static Properties tokens;
	
	static
	{
		try
		{
			tokens = new PropertiesFile(new File(Config.DIR, "datacommons/tokens.properties"));
		}
		catch (IOException e)
		{
			LOGGER.warn("tokens.properties doesn't exist or unreadable. No tokens will be authenticated.");
		}
	}

	/**
	 * Extracts the token value from the HTTP header key, looks up if the token is associated with a username. If yes, then that user is authenticated. If not
	 * then the request will be authenticated using another filter.
	 */
	@Override
	protected Object getPreAuthenticatedPrincipal(HttpServletRequest request)
	{
		// Principal = Uni Id of user
		String token = request.getHeader(TOKEN_HEADER);
		String principal;
		if (token != null && tokens != null)
		{
			principal = tokens.getProperty(token);
			if (principal != null)
				principal = principal.toLowerCase();
		}
		else
			principal = null;

		return principal;
	}

	@Override
	protected Object getPreAuthenticatedCredentials(HttpServletRequest request)
	{
		// Credentials not required.
		return "N/A";
	}
}
