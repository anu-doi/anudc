package au.edu.anu.datacommons.search;

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
	static final Logger LOGGER = LoggerFactory.getLogger(ExternalPoster.class);
	
	private MultivaluedMapImpl parameters;
	private String url;
	private String username;
	private String password;
	private String type;
	private String acceptType;
	
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
	 * @return The parametes that have been set
	 */
	public MultivaluedMapImpl getParameters() {
		return parameters;
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
	 * addParameter
	 * 
	 * Add a parameter string and value
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		04/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param key Key of the parameter to add
	 * @param value Value of the parameter to add
	 */
	public void addParameter(String key, String value) {
		parameters.add(key, value);
	}
	
	/**
	 * removeParameter
	 * 
	 * Removes the parameter with the specified key
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		04/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param key The key of the parameter to remove
	 */
	public void removeParameter(String key) {
		parameters.remove(key);
	}
	
	/**
	 * getUrl
	 * 
	 * The url that the post is applied to
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		04/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @return The url that the post is applied to
	 */
	public String getUrl() {
		return url;
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
	 * getUsername
	 * 
	 * Gets the username of the person to query as
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		04/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @return The username
	 */
	public String getUsername() {
		return username;
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
	 * getPassword
	 * 
	 * Gets the password of the person to query as
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		04/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @return The password
	 */
	public String getPassword() {
		return password;
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
		this.password = password;
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
	 * getAcceptType
	 * 
	 * Gets the expected return type from the post
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		04/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @return The return type
	 */
	public String getAcceptType() {
		return acceptType;
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
	public ClientResponse post(String queryParamName, String query) {
		WebResource webService = getResource();
		// This is separate so it is not added to the parameters field and thus the parameters can be reused
		webService = webService.queryParam(queryParamName, query);
		LOGGER.debug("Posting url is: {}", webService.getURI());
		ClientResponse clientResponse = webService.type(type).accept(acceptType).post(ClientResponse.class);
		
		return clientResponse;
	}
	
	/**
	 * post
	 *
	 * Executes a post given the classes properties and the multivalued map properties
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.3		08/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param mv A multivalued map that contains the posting options
	 * @return The response from the post
	 */
	public ClientResponse post(MultivaluedMapImpl mv) {
		WebResource webService = getResource();
		webService = webService.queryParams(mv);
		
		LOGGER.info("Posting url is: {}", webService.getURI());
		ClientResponse clientResponse = webService.type(type).accept(acceptType).post(ClientResponse.class);
		
		return clientResponse;
	}
	
	/**
	 * getResource
	 *
	 * Retrieves the web resource
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.3		08/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return The web resource to perform actions on
	 */
	private WebResource getResource() {
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		client.addFilter(new HTTPBasicAuthFilter(username, password));
		
		WebResource webService = client.resource(UriBuilder.fromUri(url).build());
		if(parameters != null) {
			webService = webService.queryParams(parameters);
		}
		
		return webService;
	}
}
