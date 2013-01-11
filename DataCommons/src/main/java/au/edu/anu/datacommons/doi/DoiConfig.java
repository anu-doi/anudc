package au.edu.anu.datacommons.doi;

import javax.ws.rs.core.UriBuilder;

/**
 * This interface defines the public methods to be defined in a DoiConfig object that provides
 */
public interface DoiConfig
{
	/**
	 * Gets the base URI of the DOI web service.
	 * 
	 * @return The base URI of the DOI web service as String.
	 */
	public String getBaseUri();
	
	/**
	 * Gets the App ID provided that identifies the source of a DOI request.
	 * 
	 * @return App ID as String.
	 */
	public String getAppId();
	
	/**
	 * Gets whether "TEST" should be prepended to the App ID so a test DOI can be minted.
	 * 
	 * @return true if TEST prefix should be prepended.
	 */
	public boolean useTestPrefix();
	
	/**
	 * Gets whether debugging should be enabled.
	 * 
	 * @return true if debug=true flag should be set in the DOI requests.
	 */
	public boolean isDebug();
	
	/**
	 * Gets whether a proxy server should be used for the DOI requests.
	 * 
	 * @return true if DOI requests should be sent through a proxy server.
	 */
	public boolean useProxy();
	
	/**
	 * Gets the hostname or IP address of the proxy server.
	 * 
	 * @return Hostname or IP address of the proxy server as String.
	 */
	public String getProxyServer();
	
	/**
	 * Gets the port of the proxy server.
	 * 
	 * @return The port number as String
	 */
	public String getProxyPort();
	
	/**
	 * Gets the username to be used for the proxy server.
	 * 
	 * @return The proxy username as String.
	 */
	public String getProxyUsername();
	
	/**
	 * Gets the password to be used for the proxy server.
	 * 
	 * @return The proxy password.
	 */
	public String getProxyPassword();
	
	/**
	 * Gets the base URI to be used for a landing page of a record.
	 * 
	 * @return Base URI as String.
	 */
	public UriBuilder getLandingUri();
}
