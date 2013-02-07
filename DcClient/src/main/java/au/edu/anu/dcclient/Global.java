package au.edu.anu.dcclient;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Properties;

import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Global
{
	private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getClass());

	// Name of the properties file from which properties will be read.
	private static final String GLOBAL_PROPERTIES_FILENAME = "global.properties";
	private static final Properties globalProperties;

	static
	{
		globalProperties = new Properties();

		try
		{
			globalProperties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(GLOBAL_PROPERTIES_FILENAME));
		}
		catch (IOException e)
		{
			LOGGER.error("Unable to read configuration file", e);
		}
	}

	/**
	 * getBagUploadUrl
	 * 
	 * Australian National University Data Commons
	 * 
	 * Gets the Url of from where bags are accessible in the ANU Data Commons.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		26/06/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @return Url as String
	 */
	public static String getBagUploadUrl()
	{
		return getBagUploadUri().toString();
	}

	/**
	 * getBagUploadUri
	 * 
	 * Australian National University Data Commons
	 * 
	 * Gets the base Uri of bags in ANU Data Commons.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		26/06/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @return Base Uri as Uri
	 */
	public static URI getBagUploadUri()
	{
		String appBagUri = globalProperties.getProperty("app.server.uploadUri");
		URI uri = UriBuilder.fromPath(getAppServerUriAsString()).path(appBagUri).build();
		return uri;
	}

	/**
	 * getLocalBagStoreAsString
	 * 
	 * Australian National University Data Commons
	 * 
	 * Gets the local directory where bags are stored.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		26/06/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @return Local bags directory as String
	 */
	public static String getLocalBagStoreAsString()
	{
		return System.getProperty("local.bagsDir", globalProperties.getProperty("local.bagsDir"));
	}

	/**
	 * getLocalBagStoreAsFile
	 * 
	 * Australian National University Data Commons
	 * 
	 * Gets the local directory where bags are stored.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		26/06/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @return Local bags directory as File
	 */
	public static File getLocalBagStoreAsFile()
	{
		return new File(getLocalBagStoreAsString());
	}
	
	/**
	 * Gets the Create URI as specified in the properties file against the key <code>app.server.createUri</code>.
	 * 
	 * @return URI to be used for creating records.
	 */
	public static URI getCreateUri()
	{
		return UriBuilder.fromUri(getAppServerUriAsString()).path(globalProperties.getProperty("app.server.createUri")).build();
	}
	
	/**
	 * Gets the base application URI as specified in the properties file against the key <code>app.server</code>.
	 * 
	 * @return Base URI of Data Commons
	 */
	public static String getAppServerUriAsString()
	{
		return System.getProperty("app.server", globalProperties.getProperty("app.server"));
	}
	
	/**
	 * Gets the URI from where information about a User can be obtained as specified against the key <code>app.server.userInfoUri</code>.
	 * 
	 * @return URI of user information
	 */
	public static URI getUserInfoUri()
	{
		return UriBuilder.fromUri(getAppServerUriAsString()).path(globalProperties.getProperty("app.server.userInfoUri")).build();
	}
	
	/**
	 * Gets the URI to be used for adding links between records as specified in the properties file against the key <code>app.server.addLinkUri</code>.
	 * 
	 * @return URI to be used for adding links to records
	 */
	public static URI getAddLinkUri()
	{
		return UriBuilder.fromUri(getAppServerUriAsString()).path(globalProperties.getProperty("app.server.addLinkUri")).build();
	}
}
