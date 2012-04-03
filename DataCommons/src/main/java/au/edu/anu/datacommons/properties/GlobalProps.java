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
 * 0.2		14/03/2012	Rahul Khanna (RK)	Added Search properties.
 * 0.3		20/03/2012	Rahul Khanna (RK)	Added File Upload properties.
 * <pre>
 * 
 */
public final class GlobalProps
{
	private static final Properties globalProperties;

	// Name of the properties file from which properties will be read.
	private static final String GLOBAL_PROPERTIES_FILENAME = "global.properties";

	// List of valid Keys in global.properties file that can be used as parameters in the methods of this class.
	public static final String PROP_FEDORA_URI = "Fedora.BaseURI";
	public static final String PROP_FEDORA_USERNAME = "Fedora.Username";
	public static final String PROP_FEDORA_PASSWORD = "Fedora.Password";
	public static final String PROP_FEDORA_RISEARCHURL = "Fedora.RISearchURI";
	public static final String PROP_LDAP_URI = "LdapUri";
	public static final String PROP_LDAP_BASEDN = "LdapBaseDn";
	public static final String PROP_LDAP_ATTRLIST = "LdapPerson.AttrList";
	public static final String PROP_LDAPATTR_UNIID = "LdapAttr.UniId";
	public static final String PROP_LDAPATTR_DISPLAYNAME = "LdapAttr.DisplayName";
	public static final String PROP_LDAPATTR_GIVENNAME = "LdapAttr.GivenName";
	public static final String PROP_LDAPATTR_FAMILYNAME = "LdapAttr.FamilyName";
	public static final String PROP_LDAPATTR_EMAIL = "LdapAttr.Email";
	public static final String PROP_LDAPATTR_PHONE = "LdapAttr.Phone";
	public static final String PROP_SEARCH_SEARCHFIELDS = "Search.DcSearchFields";
	public static final String PROP_SEARCH_RETURNFIELDS = "Search.DcReturnFields";
	public static final String PROP_SEARCH_URIREPLACE = "Search.UriReplace";
	public static final String PROP_UPLOAD_DIR = "Upload.UploadDir";
	public static final String PROP_UPLOAD_TEMPDIR = "Upload.TempDir";
	public static final String PROP_UPLOAD_MAXSIZEINMEM = "Upload.MaxSizeInMemInBytes";
	public static final String PROP_UPLOAD_HTTPBASEURI = "Upload.UploadHttpBaseURI";

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
