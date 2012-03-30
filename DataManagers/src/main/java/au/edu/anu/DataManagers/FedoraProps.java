package au.edu.anu.DataManagers;

import java.io.IOException;
import java.util.Properties;

import com.yourmediashelf.fedora.client.FedoraClient;
import com.yourmediashelf.fedora.client.FedoraCredentials;

/**
 * FedoraProps
 * 
 * Australian National University Data Commons
 * 
 * FedoraProps provides static methods to read the properties file containing information about the Fedora Repository. The filename from which these settings
 * are read is specified in FEDORA_PROPERTIES_FILENAME. The following keys are mandatory and must be present in the properties file:
 * 
 * <ul>
 * <li>Fedora.BaseURI: The base URL of Fedora. E.g. http://localhost:8081/fedora</li>
 * <li>Fedora.Username: Username to use to connect to the Fedora Repository.</li>
 * <li>Fedora.Password: Password to use to connect to the Fedora Repository.</li>
 * <li>Upload.UploadHttpBaseURI: The base HTTP URL where the files will be hosted. The pid and the DsId will be appended to the URL as sub-directories.</li>
 * <ul>
 * 
 * Usage:
 * <code>
 * FedoraProps.getFedoraClient();		// To get a FedoraClient object to which Fedora requests can be sent.
 * FedoraProps.getUploadBaseUri();		// To get the base URI of hosted files. The files would be stored in the baseUri/pid/dsid location (except externally hosted files).
 * </code>
 * 
 * <pre>
 * Version	Date		Developer			Description
 * 0.1		29/03/2012	Rahul Khanna (RK)	Initial
 * </pre>
 * 
 */
public final class FedoraProps
{
	private static final String FEDORA_PROPERTIES_FILENAME = "fedora.properties";
	private static final String PROP_FEDORA_URI = "Fedora.BaseURI";
	private static final String PROP_FEDORA_USERNAME = "Fedora.Username";
	private static final String PROP_FEDORA_PASSWORD = "Fedora.Password";
	private static final String PROP_UPLOAD_HTTPBASEURI = "Upload.UploadHttpBaseURI";

	private static FedoraClient fedoraClient;
	private static String uploadBaseUri;

	static
	{
		// Read the Fedora URI and credentials details and create a FedoraClient object which can be got using getFedoraClient method.
		// Also read the base URI for uploaded files. The subdirectories (/pid/dsid/) are appended by the FileJob object once the file to be added is known.
		Properties fedoraProps = new Properties();
		try
		{
			fedoraProps.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(FEDORA_PROPERTIES_FILENAME));
			fedoraClient = new FedoraClient(new FedoraCredentials(fedoraProps.getProperty(PROP_FEDORA_URI), fedoraProps.getProperty(PROP_FEDORA_USERNAME),
					fedoraProps.getProperty(PROP_FEDORA_PASSWORD)));
			uploadBaseUri = fedoraProps.getProperty(PROP_UPLOAD_HTTPBASEURI);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			fedoraProps = null;
			fedoraClient = null;
			uploadBaseUri = null;
		}
	}

	/**
	 * getFedoraClient
	 * 
	 * Australian National University Data Commons
	 * 
	 * Returns an instance of FedoraClient which can then be used to execute FedoraRequests.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		29/03/2012	Rahul Khanna (RK)	Initial.
	 * </pre>
	 * 
	 * @return FedoraClient object
	 */
	public static FedoraClient getFedoraClient()
	{
		if (fedoraClient != null)
			return fedoraClient;
		else
			throw new NullPointerException("Unable to read properties file: " + FEDORA_PROPERTIES_FILENAME);
	}

	/**
	 * getUploadBaseUri
	 * 
	 * Australian National University Data Commons
	 * 
	 * Gets the Base URI of the location where uploaded files are available.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		30/03/2012	Rahul Khanna		Initial
	 * </pre>
	 * 
	 * @return Base URI as String.
	 */
	public static String getUploadBaseUri()
	{
		if (uploadBaseUri != null)
			return uploadBaseUri;
		else
			throw new NullPointerException("Unable to retrieve value for key " + PROP_UPLOAD_HTTPBASEURI + " from properties file "
					+ FEDORA_PROPERTIES_FILENAME);
	}
}
