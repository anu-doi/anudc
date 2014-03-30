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

package au.edu.anu.datacommons.doi;

import javax.ws.rs.core.UriBuilder;

/**
 * This interface defines the public methods to be defined in a DoiConfig object that provides configuration data to
 * {@link DoiClient} required for interacting with the DOI minting service.
 * 
 * @author Rahul Khanna
 * 
 */
public interface DoiConfig {
	/**
	 * Gets the base URL of the DOI web service.
	 * 
	 * @return The base URL of the DOI web service as String.
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
