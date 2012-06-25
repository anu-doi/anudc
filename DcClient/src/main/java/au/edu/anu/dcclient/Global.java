package au.edu.anu.dcclient;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Properties;

import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.loc.repository.bagit.BagFactory;

public abstract class Global
{
	private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getClass());
	
	private static final BagFactory bagFactory = new BagFactory();

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

	public static BagFactory getBagFactory()
	{
		return bagFactory;
	}
	
	public static String getBagUploadUrl()
	{
		return globalProperties.getProperty("app.server.uploadUri");
	}
	
	public static URI getBagUploadUri()
	{
		return UriBuilder.fromPath(getBagUploadUrl()).build();
	}
	
	public static String getLocalBagStoreAsString()
	{
		return globalProperties.getProperty("local.bagStore");
	}
	
	public static File getLocalBagStoreAsFile()
	{
		return new File(getLocalBagStoreAsString());
	}
}
