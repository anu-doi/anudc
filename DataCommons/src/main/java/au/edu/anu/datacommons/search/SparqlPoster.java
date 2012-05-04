package au.edu.anu.datacommons.search;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * SparqlPoster
 * 
 * Implementation to 
 */
public class SparqlPoster {
	static final Logger LOGGER = LoggerFactory.getLogger(SparqlPoster.class);
	
	private MultivaluedMapImpl parameters;
	private String url;
	private String username;
	private String password;
	private String type;
	private String acceptType;
	
	public MultivaluedMapImpl getParameters() {
		return parameters;
	}
	
	public void setParameters(MultivaluedMapImpl parameters) {
		this.parameters = parameters;
	}
	
	public void addParameter(String key, String value) {
		parameters.add(key, value);
	}
	
	public void removeParameter(String key) {
		parameters.remove(key);
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAcceptType() {
		return acceptType;
	}

	public void setAcceptType(String acceptType) {
		this.acceptType = acceptType;
	}
	
	public ClientResponse post(String query) {
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		client.addFilter(new HTTPBasicAuthFilter(username, password));
		
		WebResource webService = client.resource(UriBuilder.fromUri(url).build());
		webService = webService.queryParams(parameters);
		
		// This is separate so it is not added to the parameters field
		webService = webService.queryParam("query", query);
		
		ClientResponse clientResponse = webService.type(type).accept(MediaType.TEXT_XML).post(ClientResponse.class);
		
		return clientResponse;
	}
}
