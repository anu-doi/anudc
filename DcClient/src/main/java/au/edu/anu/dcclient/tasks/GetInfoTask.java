package au.edu.anu.dcclient.tasks;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * Represents a task that gets information about a bag associated with a record in Data Commons.
 */
public final class GetInfoTask extends AbstractDcBagTask<ClientResponse>
{
	private static final Logger LOGGER = LoggerFactory.getLogger(GetInfoTask.class);

	private final URI pidBagUri;

	/**
	 * 
	 * GetInfoTask
	 * 
	 * Australian National University Data Commons
	 * 
	 * Constructor for GetInfoTask
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		26/06/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @param pidBagUri
	 */
	public GetInfoTask(URI bagBaseUri, String pid)
	{
		super();
		this.pidBagUri = UriBuilder.fromUri(bagBaseUri).path(pid).build();
	}

	/**
	 * call
	 * 
	 * Australian National University Data Commons
	 * 
	 * Gets basic information about a collection's bag.
	 * 
	 * @see java.util.concurrent.Callable#call()
	 * 
	 *      <pre>
	 * Version	Date		Developer			Description
	 * 0.1		26/06/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @return Response of the request as ClientResponse
	 * @throws Exception
	 */
	@Override
	public ClientResponse call() throws Exception
	{
		stopWatch.start();

		ClientResponse response;
		try
		{
			updateProgress("Getting Pid Info", pidBagUri.toString(), 1L, 1L);
			WebResource webResource = client.resource(UriBuilder.fromUri(pidBagUri).path("bagit.txt").build());
			response = webResource.get(ClientResponse.class);
			updateProgress("Pid Info received", pidBagUri.toString(), 1L, 1L);
		}
		finally
		{
			updateProgress("done", null, null, null);
			stopWatch.end();
			LOGGER.info("Time - Get Bag Info Task: {}", stopWatch.getFriendlyElapsed());
		}

		return response;
	}
}
