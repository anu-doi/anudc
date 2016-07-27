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

package au.edu.anu.datacommons.search;

import java.nio.charset.StandardCharsets;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;
import com.sun.jersey.core.util.Base64;
import com.sun.jersey.core.util.MultivaluedMapImpl;


/**
 * ExternalPoster
 * 
 * Australian National University Data Comons
 * 
 * Executes the post of a Sparql Service.  For example to the fedora resource index search.
 * 
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		04/05/2012	Genevieve Turner (GT)	Initial
 * 0.2		11/05/2012	Genevieve Turner (GT)	Added logger row
 * 0.3		08/06/2012	Genevieve Turner (GT)	Renamed and added additional post type
 * </pre>
 * 
 */
public class ExternalPoster {
	private static final Logger LOGGER = LoggerFactory.getLogger(ExternalPoster.class);
	
	private Client client;
	private MultivaluedMapImpl parameters;
	private String url;
	private String username;
	private byte[] password;
	private String type;
	private String acceptType;
	
	public void setClient(Client client) {
		this.client = client;
	}
	

	/**
	 * setParameters
	 * 
	 * Sets the parameters to send with the query
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		04/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param parameters The parametes to be set
	 */
	public void setParameters(MultivaluedMapImpl parameters) {
		this.parameters = parameters;
	}
	
	/**
	 * setUrl
	 * 
	 * Set the url to post to
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		04/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param url The url
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	
	/**
	 * setUsername
	 * 
	 * Sets the username of the person to query as
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		04/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param username The username
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	
	/**
	 * getParameters
	 * 
	 * Gets the parameters to send with the query
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		04/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param password The password
	 */
	public void setPassword(String password) {
		this.password = password.getBytes(StandardCharsets.UTF_8);
	}

	/**
	 * getType
	 * 
	 * Gets the type of message being send through to the url
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		04/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @return The message type
	 */
	public String getType() {
		return type;
	}

	/**
	 * getParameters
	 * 
	 * Sets the type of message being send through to the url
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		04/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param type The message type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * setAcceptType
	 * 
	 * Sets the expected return type from the post
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		04/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param acceptType The return type
	 */
	public void setAcceptType(String acceptType) {
		this.acceptType = acceptType;
	}
	
	/**
	 * post
	 * 
	 * Executes a post given the classes properties
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		04/05/2012	Genevieve Turner (GT)	Initial
	 * 0.2		11/05/2012	Genevieve Turner (GT)	Added logger statement of the url that is being posted to
	 * 0.3		08/06/2012	Genevieve Turner (GT)	Moved the retrieval of the resource
	 * </pre>
	 * 
	 * @param query The query to execute
	 * @return The response object from the query
	 */
	public ClientResponse post(String paramName, String paramValue) {
		WebResource webService = client.resource(url);
		MultivaluedMap<String, String> reqBody = createReqBody(paramName, paramValue);
		Builder webReqBuilder = webService.type(type).accept(acceptType);
		webReqBuilder = addBasicAuth(webReqBuilder);
		ClientResponse clientResponse = webReqBuilder.post(ClientResponse.class, reqBody);
		return clientResponse;
	}


	private Builder addBasicAuth(Builder webReqBuilder) {
		if (username != null) {
			final byte[] prefix = (username + ":").getBytes(StandardCharsets.UTF_8);
	        final byte[] usernamePassword = new byte[prefix.length + password.length];
	        
	        System.arraycopy(prefix, 0, usernamePassword, 0, prefix.length);
	        System.arraycopy(password, 0, usernamePassword, prefix.length, password.length);
	        String authentication = "Basic " + new String(Base64.encode(usernamePassword), StandardCharsets.US_ASCII);
	        webReqBuilder = webReqBuilder.header(HttpHeaders.AUTHORIZATION, authentication);
		}
		return webReqBuilder;
	}


	private MultivaluedMap<String, String> createReqBody(String paramName, String paramValue) {
		MultivaluedMap<String, String> reqBody;
		if (parameters != null) {
			reqBody = new MultivaluedMapImpl(parameters);
		} else {
			reqBody = new MultivaluedMapImpl();
		}
		
		reqBody.add(paramName, paramValue);
		return reqBody;
	}

}
