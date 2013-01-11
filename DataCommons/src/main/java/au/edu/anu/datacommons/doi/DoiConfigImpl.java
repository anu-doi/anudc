package au.edu.anu.datacommons.doi;

import java.util.Properties;

import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This implementation of DoiConfig returns configuration information required by the DOI Client to create and send DOI requests. The configuration values are read
 * from a Properties object passed as a parameter to the constructor. The following keys are read from the properties file:
 * <ul>
 * <li>doi.baseUri <em>Required</em></li>
 * <li>doi.appId <em>Required</em></li>
 * <li>doi.useTestPrefix <em>Default: true</em></li>
 * <li>doi.debug <em>Default: false</em></li>
 * <li>doi.useProxy <em>Default: false</em></li>
 * <li>doi.proxyServer <em>Required if doi.useProxy=true</em></li>
 * <li>doi.proxyPort <em>Required if doi.useProxy=true</em></li>
 * <li>doi.proxyUsername <em>Optional, required if the proxy server requires login details</em></li>
 * <li>doi.proxyPassword <em>Optional, required if the proxy server requires login details</em></li>
 * <li>doi.landingUri <em>Required</em></li>
 * </ul>
 */
public class DoiConfigImpl implements DoiConfig
{
	private static final Logger LOGGER = LoggerFactory.getLogger(DoiConfigImpl.class);
	
	private Properties props;
	
	/**
	 * Constructor for DoiConfigImpl that accepts a Properties object containing configuration values.
	 * 
	 * @param props
	 *            Properties object containing configuration values.
	 */
	public DoiConfigImpl(Properties props)
	{
		this.props = props;
	}
	
	@Override
	public String getBaseUri()
	{
		return props.getProperty("doi.baseUri");
	}

	@Override
	public String getAppId()
	{
		return props.getProperty("doi.appId");
	}

	@Override
	public boolean useTestPrefix()
	{
		return Boolean.parseBoolean(props.getProperty("doi.useTestPrefix", "true"));
	}

	@Override
	public boolean isDebug()
	{
		return Boolean.parseBoolean(props.getProperty("doi.debug"));
	}

	@Override
	public boolean useProxy()
	{
		return Boolean.parseBoolean(props.getProperty("doi.useProxy"));
	}

	@Override
	public String getProxyServer()
	{
		return props.getProperty("doi.proxyServer");
	}

	@Override
	public String getProxyPort()
	{
		return props.getProperty("doi.proxyPort");
	}

	@Override
	public String getProxyUsername()
	{
		return props.getProperty("doi.proxyUsername");
	}

	@Override
	public String getProxyPassword()
	{
		return props.getProperty("doi.proxyPassword");
	}
	
	@Override
	public UriBuilder getLandingUri()
	{
		return UriBuilder.fromPath(props.getProperty("doi.landingUri"));
	}
}
