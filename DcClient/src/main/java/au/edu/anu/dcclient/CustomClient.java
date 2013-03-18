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

package au.edu.anu.dcclient;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.api.json.JSONConfiguration;

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
	public static synchronized Client getInstance()
	{
		if (client == null)
		{
			ClientConfig clientConfig = new DefaultClientConfig();
			clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
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
			client.addFilter(new LoggingFilter(System.out));
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
