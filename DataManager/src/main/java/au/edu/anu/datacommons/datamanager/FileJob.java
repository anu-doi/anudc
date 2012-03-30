package au.edu.anu.datacommons.datamanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yourmediashelf.fedora.client.FedoraClient;
import com.yourmediashelf.fedora.client.FedoraClientException;
import com.yourmediashelf.fedora.client.request.AddDatastream;
import com.yourmediashelf.fedora.client.response.AddDatastreamResponse;

/**
 * FileJob
 * 
 * Australian National University Data Commons
 * 
 * This class is instantiated to encapsulate a job to reference a file to a fedora object. A specified datastream is created/updated to link the contents of the
 * datastream to the file. This is done so the file doesn't need to be stored within the Fedora Repository itself.
 * 
 * Usage: <code>
 * FileJob job = new FileJob(new File("C:\\Abc\\Xyz.docx"), FedoraProps.getUploadBaseUri());
 * job.submit(FedoraProps.getFedoraClient());
 * </code>
 * 
 * <pre>
 * Version	Date		Developer			Description
 * 0.1		30/03/2012	Rahul Khanna (RK)	Initial
 * </pre>
 * 
 */
public final class FileJob
{
	private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getClass());

	public static final String DATASTREAM_PROPERTY_PID = "Pid";
	public static final String DATASTREAM_PROPERTY_ID = "DsId";
	public static final String DATASTREAM_PROPERTY_STATE = "State";
	public static final String DATASTREAM_PROPERTY_LABEL = "Label";
	public static final String DATASTREAM_PROPERTY_VERSIONABLE = "Versionable";
	public static final String DATASTREAM_PROPERTY_CHECKSUMTYPE = "ChecksumType";
	public static final String DATASTREAM_PROPERTY_CHECKSUM = "Checksum";
	public static final String DATASTREAM_PROPERTY_MIMETYPE = "MimeType";
	public static final String DATASTREAM_PROPERTY_CONTROLGROUP = "ControlGroup";

	private AddDatastream addDsCmd = null;
	private String uploadBaseUri = null;

	/**
	 * FileJob
	 * 
	 * Australian National University Data Commons
	 * 
	 * Default constructor allowing the AddDatastream object to be set from outside this class.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		30/03/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 */
	public FileJob()
	{
	}

	/**
	 * FileJob
	 * 
	 * Australian National University Data Commons
	 * 
	 * Constructor that takes a file object for the properties file containing information about the datastream the file is to uploaded against.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		30/03/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @param dsPropFile
	 *            File object for the properties file containing datastream and Fedora object information
	 * @param uploadBaseUri
	 *            The Base URI of uploaded files. the pid and dsid will be appended to the path by this method.
	 * @throws IOException
	 */
	public FileJob(File dsPropFile, String uploadBaseUri) throws IOException
	{
		Properties dsProps = new Properties();
		dsProps.load(new FileInputStream(dsPropFile));

		if (dsProps.getProperty(DATASTREAM_PROPERTY_PID) == null || dsProps.getProperty(DATASTREAM_PROPERTY_ID) == null)
			throw new NullPointerException("Invalid properties file. Mandatory properties not specified.");

		this.uploadBaseUri = uploadBaseUri;

		// Create datastream object.
		this.addDsCmd = new AddDatastream(dsProps.getProperty(DATASTREAM_PROPERTY_PID), dsProps.getProperty(DATASTREAM_PROPERTY_ID));

		if (dsProps.getProperty(DATASTREAM_PROPERTY_STATE) != null)
			this.addDsCmd = this.addDsCmd.dsState(dsProps.getProperty(DATASTREAM_PROPERTY_STATE));

		if (dsProps.getProperty(DATASTREAM_PROPERTY_LABEL) != null)
			this.addDsCmd = this.addDsCmd.dsLabel(dsProps.getProperty(DATASTREAM_PROPERTY_LABEL));

		if (dsProps.getProperty(DATASTREAM_PROPERTY_VERSIONABLE) != null)
			this.addDsCmd = this.addDsCmd.versionable(Boolean.parseBoolean(dsProps.getProperty(DATASTREAM_PROPERTY_VERSIONABLE)));

		if (dsProps.getProperty(DATASTREAM_PROPERTY_CHECKSUMTYPE) != null)
			this.addDsCmd = this.addDsCmd.checksumType(dsProps.getProperty(DATASTREAM_PROPERTY_CHECKSUMTYPE));

		if (dsProps.getProperty(DATASTREAM_PROPERTY_CHECKSUM) != null)
			this.addDsCmd = this.addDsCmd.checksum(dsProps.getProperty(DATASTREAM_PROPERTY_CHECKSUM));

		if (dsProps.getProperty(DATASTREAM_PROPERTY_MIMETYPE) != null)
		{
			this.addDsCmd = this.addDsCmd.mimeType(dsProps.getProperty(DATASTREAM_PROPERTY_MIMETYPE));
		}
		else
		{
			// TODO: Add feature (low priority) to auto detect MIME type based on file extension (fast) or file contents (slow).
		}

		// Only 'R' control group is handled as all data is to be stored outside the repository with only a reference. 
		// TODO Encode string being appended to ensure it is URL-safe AND Windows disk-safe AND Linux disk-safe.
		StringBuilder locationUri = new StringBuilder(this.uploadBaseUri);
		locationUri.append("/");
		locationUri.append(dsProps.getProperty(DATASTREAM_PROPERTY_PID));		// Dir with the name of the Pid
		locationUri.append("/");
		locationUri.append(dsProps.getProperty(DATASTREAM_PROPERTY_ID));		// Dir with name of datastream id
		locationUri.append("/");
		locationUri.append(dsPropFile.getName());
		this.addDsCmd = this.addDsCmd.dsLocation(locationUri.toString());
	}

	/**
	 * getAddDsCmd
	 * 
	 * Australian National University Data Commons
	 * 
	 * Returns the AddDatastream object containing the datastream information.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		30/03/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @return AddDatastream object
	 */
	public AddDatastream getAddDsCmd()
	{
		return this.addDsCmd;
	}

	/**
	 * setAddDsCmd
	 * 
	 * Australian National University Data Commons
	 * 
	 * Sets the AddDatastream object containing the datastream information.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		30/03/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 */
	public void setAddDsCmd(AddDatastream addDsCmd)
	{
		this.addDsCmd = addDsCmd;
	}

	/**
	 * getUploadBaseUri
	 * 
	 * Australian National University Data Commons
	 * 
	 * Returns the base URI of uploaded files.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		30/03/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @return Base URI as String
	 */
	public String getUploadBaseUri()
	{
		return uploadBaseUri;
	}

	/**
	 * setUploadBaseUri
	 * 
	 * Australian National University Data Commons
	 * 
	 * Sets the base URI of uploaded files.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		30/03/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @param uploadBaseUri
	 */
	public void setUploadBaseUri(String uploadBaseUri)
	{
		this.uploadBaseUri = uploadBaseUri;
	}

	/**
	 * execute
	 * 
	 * Australian National University Data Commons
	 * 
	 * Executes the job created.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		30/03/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @param fedoraClient
	 *            FedoraClient object to which a request is to be executed.
	 * @throws FedoraClientException
	 */
	public void execute(FedoraClient fedoraClient) throws FedoraClientException
	{
		AddDatastreamResponse addDsResp = this.addDsCmd.execute(fedoraClient);
		LOGGER.info("Add Datastream Response Status: " + addDsResp.getStatus());
	}
}
