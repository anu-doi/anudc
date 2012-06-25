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

	public GetInfoTask(URI pidBagUri)
	{
		super();
		this.pidBagUri = pidBagUri;
	}
	
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
