package au.edu.anu.datacommons.properties;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Properties;

import javax.ws.rs.core.UriBuilder;

import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.config.Config;
import au.edu.anu.datacommons.config.PropertiesFile;

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
 * Version	Date		Developer				Description
 * 0.1		8/03/2012	Rahul Khanna (RK)		Initial.
 * 0.2		14/03/2012	Rahul Khanna (RK)		Added Search properties.
 * 0.3		20/03/2012	Rahul Khanna (RK)		Added File Upload properties.
 * 0.4		4/05/2012	Rahul Khanna (RK)		Added Random Password chars property.
 * 0.5		12/05/2012	Genevieve Turner (GT)	Changed case of properties, related uri and save namespace
 * 0.6		08/06/2012	Genevieve Turner (GT)	Added solr location
 * 0.7		13/06/2012	Genevieve Turner (GT)	Added solr standard return fields
 * <pre>
 * 
 */
public final class GlobalProps
{
	private static final Properties globalProperties;
	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalProps.class);

	// List of valid Keys in global.properties file that can be used as parameters in the methods of this class.
	public static final String PROP_FEDORA_URI = "fedora.baseURI";
	public static final String PROP_FEDORA_USERNAME = "fedora.username";
	public static final String PROP_FEDORA_PASSWORD = "fedora.password";
	public static final String PROP_FEDORA_RISEARCHURL = "fedora.riSearchURI";
	public static final String PROP_FEDORA_RELATEDURI = "fedora.relatedURI";
	public static final String PROP_FEDORA_SAVENAMESPACE = "fedora.saveNamespace";
	public static final String PROP_FEDORA_OAIPROVIDER_URL = "fedora.oaiprovider.url";
	public static final String PROP_FEDORA_NAMEFIELDS = "fedora.nameFields";
	public static final String PROP_LDAP_URI = "ldap.uri";
	public static final String PROP_LDAP_BASEDN = "ldap.baseDn";
	public static final String PROP_LDAP_ATTRLIST = "ldap.person.attrList";
	public static final String PROP_LDAPATTR_UNIID = "ldap.attr.uniId";
	public static final String PROP_LDAPATTR_DISPLAYNAME = "ldap.attr.displayName";
	public static final String PROP_LDAPATTR_GIVENNAME = "ldap.attr.givenName";
	public static final String PROP_LDAPATTR_FAMILYNAME = "ldap.attr.familyName";
	public static final String PROP_LDAPATTR_EMAIL = "ldap.attr.email";
	public static final String PROP_LDAPATTR_PHONE = "ldap.attr.phone";
	public static final String PROP_SEARCH_SEARCHFIELDS = "search.dcSearchFields";
	public static final String PROP_SEARCH_RETURNFIELDS = "search.dcReturnFields";
	public static final String PROP_SEARCH_URIREPLACE = "search.uriReplace";
	public static final String PROP_SEARCH_SOLR = "search.solr";
	public static final String PROP_SEARCH_SOLR_RETURNFIELDS = "search.solr.returnFields";
	public static final String PROP_UPLOAD_DIR = "upload.uploadDir";
	public static final String PROP_UPLOAD_TEMPDIR = "upload.tempDir";
	public static final String PROP_UPLOAD_MAXSIZEINMEM = "upload.maxSizeInMemInBytes";
	public static final String PROP_UPLOAD_HTTPBASEURI = "upload.uploadHttpBaseURI";
	public static final String PROP_UPLOAD_BAGSDIR = "upload.bagsDir";
	public static final String PROP_PASSWORDGENERATOR_CHARS = "passwordGenerator.chars";
	public static final String PROP_DROPBOX_PASSWORDLENGTH = "dropbox.passwordLength";
	public static final String PROP_EMAIL_DEBUG_SEND = "email.debug.sendmail";
	public static final String PROP_CAS_SERVER = "cas.server";
	public static final String PROP_APP_SERVER = "app.server";
	public static final String PROP_REVIEW_REJECTED_TITLE = "review.rejected.title";
	public static final String PROP_REVIEW_READY_TITLE = "review.reviewready.title";
	public static final String PROP_PUBLISH_READY_TITLE = "review.publishready.title";
	public static final String PROP_ORCA_RIFCS = "orca.rifcs.location";
	public static final String PROP_ORCA_XSL = "orca.transform.xsl";

	static
	{
		try
		{
			globalProperties = new PropertiesFile(new File(Config.DIR, "datacommons/datacommons.properties"));
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
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

	public static String getUploadDirAsString()
	{
		return getProperty(PROP_UPLOAD_DIR);
	}

	public static File getUploadDirAsFile()
	{
		File uploadDir = new File(getUploadDirAsString());
		if (!uploadDir.exists())
			if (uploadDir.mkdirs())
				LOGGER.error("The upload directory doesn't exist. Unable to create it.");
		return uploadDir;
	}

	public static String getBagsDirAsString()
	{
		return getProperty(PROP_UPLOAD_BAGSDIR);
	}

	public static File getBagsDirAsFile()
	{
		File bagsDir = new File(getBagsDirAsString());
		if (!bagsDir.exists())
			if (bagsDir.mkdirs())
				LOGGER.error("The bags directory doesn't exist. Unable to create it.");
		return bagsDir;
	}

	public static String getTempDirAsString()
	{
		return getProperty(PROP_UPLOAD_TEMPDIR);
	}

	public static File getTempDirAsFile()
	{
		File tempDir = new File(getTempDirAsString());
		if (!tempDir.exists())
			if (tempDir.mkdirs())
				LOGGER.error("The bags directory doesn't exist. Unable to create it.");
		
		return tempDir;
	}

	public static boolean getEmailDebugSend()
	{
		String emailDebugSendString = getProperty(PROP_EMAIL_DEBUG_SEND);
		boolean emailDebugSend = false;

		if (emailDebugSendString != null)
			emailDebugSend = Boolean.parseBoolean(getProperty(PROP_EMAIL_DEBUG_SEND));
		else
			LOGGER.warn("Property " + PROP_EMAIL_DEBUG_SEND + " not specified in Global Properties. Using Default: false");

		return emailDebugSend;
	}

	public static int getMaxSizeInMem()
	{
		try
		{
			return Integer.parseInt(getProperty(PROP_UPLOAD_MAXSIZEINMEM));
		}
		catch (NumberFormatException e)
		{
			LOGGER.warn("Property " + PROP_UPLOAD_MAXSIZEINMEM + " not specified in Global Properties.");
			return DiskFileItemFactory.DEFAULT_SIZE_THRESHOLD;
		}
	}
	
	public static URI getCasServerUri()
	{
		URI returnUri = UriBuilder.fromPath(getProperty(PROP_APP_SERVER)).path("DataCommons").path("j_spring_cas_security_check").build();
		URI loginUri = UriBuilder.fromUri(getProperty(PROP_CAS_SERVER)).path("login").queryParam("service", returnUri.toString()).build();
		return loginUri;
	}
}
