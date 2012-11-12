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
