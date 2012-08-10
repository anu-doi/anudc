package au.edu.anu.dcclient.tasks;

import gov.loc.repository.bagit.ProgressListener;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.util.concurrent.Callable;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import au.edu.anu.dcclient.Global;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

public final class GetInfoTask extends AbstractDcBagTask implements Callable<ClientResponse>
{
	private URI pidBagUri;

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
		Client client = Client.create();
		PasswordAuthentication auth = Authenticator.requestPasswordAuthentication(pidBagUri.getHost(), null, pidBagUri.getPort(), pidBagUri.getScheme(),
				"Please provide password for: " + Global.getBagUploadUrl(), "scheme");
		if (auth != null)
			client.addFilter(new HTTPBasicAuthFilter(auth.getUserName(), new String(auth.getPassword())));
		updateProgress("Getting Pid Info", pidBagUri.toString(), 1L, 1L);
		WebResource webResource = client.resource(UriBuilder.fromUri(pidBagUri).path("bagit.txt").build());
		ClientResponse response = webResource.header("User-Agent", "BagIt Library Parallel Fetcher").get(ClientResponse.class);
		updateProgress("Pid Info received", pidBagUri.toString(), 1L, 1L);
		updateProgress("done", null, null, null);
		return response;
	}
}
