package au.edu.anu.datacommons.xml.dc;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DublinCoreConstants
 * 
 * Australian National University Data Commons
 * 
 * Contains constants for the system
 * 
 * JUnit coverage:
 * None
 * 
 * Version	Date		Developer				Description
 * 0.1		23/03/2012	Genevieve Turner (GT)	Initial
 * 
 */
public class DublinCoreConstants {
	static final Logger LOGGER = LoggerFactory.getLogger(DublinCoreConstants.class);
	
	private  static final String DC_PROPERTIES_FILE = "dublincore.properties";
	public static final String DC = "http://purl.org/dc/elements/1.1/";
	public static final String OAI_DC = "http://www.openarchives.org/OAI/2.0/oai_dc/";
	public static final String XSI = "http://www.w3.org/2001/XMLSchema-instance";
	
	private static final Properties dcproperties_;
	
	static {
		Properties fallback = new Properties();
		// put in a placeholder as if the properties file can't be found there are errors
		// when calling the properties otherwise
		fallback.put("key", "default");
		dcproperties_ = new Properties(fallback);
		
		try {
			InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(DC_PROPERTIES_FILE);
			try {
				dcproperties_.load(stream);
			}
			finally {
				stream.close();
			}
		}
		catch (IOException e) {
			LOGGER.error("Error reading properties file: ", e);
		}
	}
	
	/**
	 * getFieldName
	 * 
	 * Return the associated dublin core field name for the given value
	 * 
	 * Version	Date		Developer				Description
	 * 0.1		08/03/2012	Genevieve Turner (GT)	Initial
	 * 
	 * @param propertyName The value to find the field name for
	 * @return The dublin core field name
	 */
	public static String getFieldName(String propertyName) {
		return dcproperties_.getProperty(propertyName);
	}
}
