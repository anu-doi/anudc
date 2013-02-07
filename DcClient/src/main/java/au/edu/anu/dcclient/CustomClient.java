package au.edu.anu.dcclient;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

/**
 * This class is a wrapper for a singleton Client object to be used throughout the application for sending HTTP requests to Data Commons.
 */
public class CustomClient
{
	private static Client client = null;
	private static ClientFilter authFilter = null;
	
	protected CustomClient()
	{
	}

	/**
	 * Gets the Client object instance. If one doesn't exist, creates one for subsequent getInstance requests.
	 * 
	 * @return Client object
	 */
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

	/**
	 * Adds a Basic Authentication filter to the client. The filter adds the Basic Web Authentication header to all outgoing HTTP requests.
	 * 
	 * @param username
	 *            Username as String
	 * @param password
	 *            Password as String
	 */
	public static void setAuth(String username, String password)
	{
		if (authFilter != null)
			getInstance().removeFilter(authFilter);
		authFilter = new HTTPBasicAuthFilter(username, password);
		getInstance().addFilter(authFilter);
	}
	
}
