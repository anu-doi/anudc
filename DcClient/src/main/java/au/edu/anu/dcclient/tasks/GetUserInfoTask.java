package au.edu.anu.dcclient.tasks;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.util.concurrent.Callable;

import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.dcclient.CustomClient;
import au.edu.anu.dcclient.Global;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

public class GetUserInfoTask extends AbstractDcBagTask<String[]>
{
	private static final Logger LOGGER = LoggerFactory.getLogger(GetUserInfoTask.class);

	private final URI userInfoUri;

	public GetUserInfoTask(URI userInfoUri)
	{
		this.userInfoUri = userInfoUri;
	}

	@Override
	public String[] call()
	{
		String[] userInfo = null;

		stopWatch.start();
		try
		{
			WebResource webResource = client.resource(this.userInfoUri);
			ClientResponse response = webResource.get(ClientResponse.class);
			LOGGER.info("Server returned: HTTP {}", response.getStatus());
			if (response.getClientResponseStatus() == Status.OK)
			{
				String respStr = response.getEntity(String.class);
				int separatorIndex = respStr.indexOf(':');
				userInfo = new String[] { respStr.substring(0, separatorIndex), respStr.substring(separatorIndex + 1) };
			}
		}
		finally
		{
			stopWatch.end();
			LOGGER.info("Time - Get User Info Task: {}", stopWatch.getFriendlyElapsed());
		}

		return userInfo;
	}
}
