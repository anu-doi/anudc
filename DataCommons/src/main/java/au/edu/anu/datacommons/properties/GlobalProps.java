/**
 * 
 */
package au.edu.anu.datacommons.properties;

import java.io.IOException;
import java.util.Properties;

/**
 * GlobalProps
 * 
 * Australian National University Data Commons
 * 
 * Contains static methods that reads (no write functions) values for keys in the global.properties file.
 * 
 * Usage example: To return the value for the key containing Fedora's URI.
 * 
 * <pre>
 * GlobalProps.getProperty(GlobalProps.PROP_FEDORA_SERVER);
 * 
 * <pre>
 * 
 * <pre>
 * Version	Date		Developer			Description
 * 0.1		8/03/2012	Rahul Khanna (RK)	Initial.
 * 
 * <pre>
 * 
 */
public final class GlobalProps
{
	private static final Properties globalProperties;

	// Name of the properties file from which properties will be read.
	private static final String GLOBAL_PROPERTIES_FILENAME = "global.properties";

	// List of valid Keys in global.properties file that can be used as parameters in the methods of this class.
	public static final String PROP_FEDORA_SERVER = "FedoraBaseURI";
	public static final String PROP_FEDORA_USERNAME = "FedoraUsername";
	public static final String PROP_FEDORA_PASSWORD = "FedoraPassword";
	public static final String PROP_FILEUPLOAD_DIR = "FileUploadDir";
	public static final String PROP_FILEUPLOAD_TEMPDIR = "FileUploadTempDir";

	static
	{
		globalProperties = new Properties();

		try
		{
			globalProperties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(GLOBAL_PROPERTIES_FILENAME));
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @see Properties.getProperty()
	 * @param key
	 * @return value of the key, null if the key doesn't exist.
	 */
	public static String getProperty(String key)
	{
		return globalProperties.getProperty(key);
	}

	/**
	 * @see Properties.getProperty()
	 * @param key
	 * @param defaultValue
	 * @return value of the key or defaultValue if key doesn't exist.
	 */
	public static String getProperty(String key, String defaultValue)
	{
		return globalProperties.getProperty(key, defaultValue);
	}
}
