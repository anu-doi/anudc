package au.edu.anu.dcclient.tasks;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.WebResource;

/**
 * This class represents a task that gets information about a user from DataCommons.
 */
public class GetUserInfoTask extends AbstractDcBagTask<String[]>
{
	private static final Logger LOGGER = LoggerFactory.getLogger(GetUserInfoTask.class);

	private final URI userInfoUri;

	/**
	 * Instantiates a new gets the user info task.
	 * 
	 * @param userInfoUri
	 *            the user info uri
	 */
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
