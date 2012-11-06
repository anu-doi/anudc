package au.edu.anu.datacommons.doi;

import java.util.Properties;

import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DoiConfigImpl implements DoiConfig
{
	private static final Logger LOGGER = LoggerFactory.getLogger(DoiConfigImpl.class);
	
	private Properties props;
	
	public DoiConfigImpl(Properties props)
	{
		this.props = props;
	}
	
	public String getBaseUri()
	{
		return props.getProperty("doi.baseUri");
	}

	public String getAppId()
	{
		return props.getProperty("doi.appId");
	}

	public boolean useTestPrefix()
	{
		return Boolean.parseBoolean(props.getProperty("doi.useTestPrefix", "true"));
	}

	public boolean isDebug()
	{
		return Boolean.parseBoolean(props.getProperty("doi.debug"));
	}

	public boolean useProxy()
	{
		return Boolean.parseBoolean(props.getProperty("doi.useProxy"));
	}

	public String getProxyServer()
	{
		return props.getProperty("doi.proxyServer");
	}

	public String getProxyPort()
	{
		return props.getProperty("doi.proxyPort");
	}

	public String getProxyUsername()
	{
		return props.getProperty("doi.proxyUsername");
	}

	public String getProxyPassword()
	{
		return props.getProperty("doi.proxyPassword");
	}
	
	public UriBuilder getLandingUri()
	{
		return UriBuilder.fromPath(props.getProperty("doi.landingUri"));
	}
}
