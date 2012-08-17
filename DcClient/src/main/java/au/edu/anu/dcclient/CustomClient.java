package au.edu.anu.dcclient;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

public class CustomClient
{
	private static Client client = null;
	private static ClientFilter authFilter = null;
	
	protected CustomClient()
	{
	}
	
	public static Client getInstance()
	{
		if (client == null)
		{
			client = Client.create();
			client.setChunkedEncodingSize(1024 * 1024);
			client.addFilter(new ClientFilter() {
				@Override
				public ClientResponse handle(ClientRequest cr) throws ClientHandlerException
				{
					cr.getHeaders().add("User-Agent", "BagIt Library Parallel Fetcher");
					return getNext().handle(cr);
				}
			});
		}
		
		return client;
	}

	public static void setAuth(String username, String password)
	{
		if (authFilter != null)
			getInstance().removeFilter(authFilter);
		authFilter = new HTTPBasicAuthFilter(username, password);
		getInstance().addFilter(authFilter);
	}
	
}
