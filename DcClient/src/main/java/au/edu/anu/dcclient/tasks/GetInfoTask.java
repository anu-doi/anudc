package au.edu.anu.dcclient.tasks;

import gov.loc.repository.bagit.ProgressListener;

import java.net.URI;
import java.util.concurrent.Callable;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

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
	public GetInfoTask(URI pidBagUri)
	{
		super();
		this.pidBagUri = pidBagUri;
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
		updateProgress("Getting Pid Info", pidBagUri.toString(), 1L, 1L);
		WebResource webResource = client.resource(pidBagUri);
		ClientResponse response = webResource.accept(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);
		updateProgress("Pid Info received", pidBagUri.toString(), 1L, 1L);
		updateProgress("done", null, null, null);
		return response;
	}
}
