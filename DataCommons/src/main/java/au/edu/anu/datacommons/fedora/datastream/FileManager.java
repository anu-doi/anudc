package au.edu.anu.datacommons.fedora.datastream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.List;
import java.util.ListIterator;
import java.util.Properties;
import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;

import au.edu.anu.datacommons.connection.fedora.FedoraBroker;
import au.edu.anu.datacommons.properties.GlobalProps;

import com.yourmediashelf.fedora.client.FedoraClient;
import com.yourmediashelf.fedora.client.FedoraClientException;
import com.yourmediashelf.fedora.client.request.AddDatastream;
import com.yourmediashelf.fedora.client.request.FindObjects;
import com.yourmediashelf.fedora.client.request.ListDatastreams;
import com.yourmediashelf.fedora.client.response.AddDatastreamResponse;
import com.yourmediashelf.fedora.client.response.FindObjectsResponse;
import com.yourmediashelf.fedora.client.response.ListDatastreamsResponse;
import com.yourmediashelf.fedora.generated.access.DatastreamType;

/**
 * FileManager
 * 
 * Autralian National University Data Commons
 * 
 * Class for processing files uploaded into the upload directory. Each uploaded file must have a corresponding .properties file that contains information about
 * the Fedora object and its datastream the file is to be associated with. Additional properties can also be specified such as State, Label, Versionable,
 * ChecksumType etc. This class can also be called from the command line for scheduled processing of files.
 * 
 * Usage: FileManager.process(new File("C:\\FileUpload\\FTP\\Links.txt.properties"));
 * 
 * This will pull the datastream info from the properties file and upload/reference the file Links.txt to it. It is critical that each uploaded file has a
 * corresponding properties file otherwise the file would be deemed "orphaned" and will not be processed.
 * 
 * <pre>
 * Version	Date		Developer			Description
 * 0.1		21/03/2012	Rahul Khanna (RK)	Initial.
 * </pre>
 * 
 */
public class FileManager
{
	private static final Logger log = Logger.getLogger("FileManager");

	// Supported keys in property files.
	public static final String DATASTREAM_PROPERTY_PID = "Pid";
	public static final String DATASTREAM_PROPERTY_ID = "Id";
	public static final String DATASTREAM_PROPERTY_STATE = "State";
	public static final String DATASTREAM_PROPERTY_LABEL = "Label";
	public static final String DATASTREAM_PROPERTY_VERSIONABLE = "Versionable";
	public static final String DATASTREAM_PROPERTY_CHECKSUMTYPE = "ChecksumType";
	public static final String DATASTREAM_PROPERTY_CHECKSUM = "Checksum";
	public static final String DATASTREAM_PROPERTY_MIMETYPE = "MimeType";
	public static final String DATASTREAM_PROPERTY_CONTROLGROUP = "ControlGroup";

	public static void main(String[] args)
	{
		// Check if an argument is provided
		if (args.length > 0)
		{
			for (int i = 0; i < args.length; i++)
			{
				if (args[i].indexOf(".properties") != -1)
				{
					System.out.print("Processing file " + args[i] + "...");
					process(new File(args[i]));
					System.out.println("done.");
				}
				else
				{
					System.out.println("Skipping non-properties file " + args[i]);
				}
			}
		}
		else
		{
			System.out.println("Processes a file that's been uploaded by associating it with a Fedora Object and one of its Datastreams.");
			System.out.println();
			System.out.println("FILEMANAGER filename [filename]...");
			System.out.println();
			System.out.println("\tfilename\t\tSpecifies the full path to a .properties file. More than one file can be specified as additional arguments.");
			System.out.println();
		}
	}

	public static void process(URI dsPropFileURI)
	{
		process(new File(dsPropFileURI));
	}

	public static void process(File dsPropFile)
	{
		FedoraClient fedoraClient;

		try
		{
			// Get the fedora client object.
			fedoraClient = FedoraBroker.getClient();

			// Read .properties file for the file to be uploaded/referenced to a fedora object.
			Properties fileProps = new Properties();
			fileProps.load(new FileInputStream(dsPropFile));

			// Check that the minimum required properties are specified - PID and ID.
			if (fileProps.getProperty(DATASTREAM_PROPERTY_PID) == null || fileProps.getProperty(DATASTREAM_PROPERTY_ID) == null)
				throw new NullPointerException("Invalid properties file. Mandatory properties not specified.");

			// Check if the Pid already exists.
			FindObjects findObjCmd = new FindObjects();
			findObjCmd = findObjCmd.pid();
			findObjCmd = findObjCmd.query("pid=" + fileProps.getProperty(DATASTREAM_PROPERTY_PID));
			FindObjectsResponse findObjResp = findObjCmd.execute(fedoraClient);

			log.info("HTTP Status of findObjects: " + findObjResp.getStatus());
			
			// Throw exception if more than one Pids returned.
			if (findObjResp.getPids().size() == 1)
				log.info("Pid: " + findObjResp.getPids().get(0) + " exists.");
			else
				throw new FedoraClientException(findObjResp.getPids().size() + " Pids returned. Only 1 expected.");
				
			// Check if a datastream with the same datastream ID for the Pid doesn't already exist.
			ListDatastreams listDsCmd = new ListDatastreams(fileProps.getProperty(DATASTREAM_PROPERTY_PID));
			ListDatastreamsResponse listDsResp = listDsCmd.execute(fedoraClient);
			if (listDsResp.getStatus() != HttpsURLConnection.HTTP_OK)
				throw new FedoraClientException("Unable to obtain list of existing datastreams for Pid '" + fileProps.getProperty(DATASTREAM_PROPERTY_PID)
						+ "'.");

			List<DatastreamType> listDs = listDsResp.getDatastreams();
			ListIterator<DatastreamType> i = listDs.listIterator();

			while (i.hasNext())
			{
				DatastreamType curDsType = i.next();
				if (curDsType.getDsid().equals(fileProps.getProperty(DATASTREAM_PROPERTY_ID)))
				{
					// TODO Handle overwriting. There could possibly be a flag in the properties file to indicate if the datastream should be overwritten.
					log.info("Datastream " + curDsType.getDsid() + " already exists for Pid " + fileProps.getProperty(DATASTREAM_PROPERTY_PID) + ".");
					break;
				}
			}

			// Create datastream object.
			AddDatastream addDsCmd = new AddDatastream(fileProps.getProperty(DATASTREAM_PROPERTY_PID), fileProps.getProperty(DATASTREAM_PROPERTY_ID));

			if (fileProps.getProperty(DATASTREAM_PROPERTY_STATE) != null)
				addDsCmd = addDsCmd.dsState(fileProps.getProperty(DATASTREAM_PROPERTY_STATE));

			if (fileProps.getProperty(DATASTREAM_PROPERTY_LABEL) != null)
				addDsCmd = addDsCmd.dsLabel(fileProps.getProperty(DATASTREAM_PROPERTY_LABEL));

			if (fileProps.getProperty(DATASTREAM_PROPERTY_VERSIONABLE) != null)
				addDsCmd = addDsCmd.versionable(Boolean.parseBoolean(fileProps.getProperty(DATASTREAM_PROPERTY_VERSIONABLE)));

			if (fileProps.getProperty(DATASTREAM_PROPERTY_CHECKSUMTYPE) != null)
				addDsCmd = addDsCmd.checksumType(fileProps.getProperty(DATASTREAM_PROPERTY_CHECKSUMTYPE));

			if (fileProps.getProperty(DATASTREAM_PROPERTY_CHECKSUM) != null)
				addDsCmd = addDsCmd.checksum(fileProps.getProperty(DATASTREAM_PROPERTY_CHECKSUM));

			if (fileProps.getProperty(DATASTREAM_PROPERTY_MIMETYPE) != null)
			{
				addDsCmd = addDsCmd.mimeType(fileProps.getProperty(DATASTREAM_PROPERTY_MIMETYPE));
			}
			else
			{
				// TODO: Add feature (low priority) to auto detect mime type based on file extension (fast) or file contents (slow).
			}

			// Linking file to the Fedora object based on the Control Group specified. 
			if (fileProps.getProperty(DATASTREAM_PROPERTY_CONTROLGROUP) != null)
			{
				addDsCmd = addDsCmd.controlGroup(fileProps.getProperty(DATASTREAM_PROPERTY_CONTROLGROUP));

				if (fileProps.getProperty(DATASTREAM_PROPERTY_CONTROLGROUP).equals("M") || fileProps.getProperty(DATASTREAM_PROPERTY_CONTROLGROUP).equals("E"))
				{
					addDsCmd = addDsCmd.content(new File("C:\\Rahul\\FileUpload\\FTP\\Links.txt"));
				}
				else if (fileProps.getProperty(DATASTREAM_PROPERTY_CONTROLGROUP).equals("X")
						|| fileProps.getProperty(DATASTREAM_PROPERTY_CONTROLGROUP).equals("R"))
				{
					// TODO Add code to manage X and R controlGroups.
					// TODO Encode string being appended to ensure it is URL-safe AND Windows disk-safe AND Linux disk-safe.
					StringBuilder locationUri = new StringBuilder(GlobalProps.getProperty(GlobalProps.PROP_UPLOAD_HTTPBASEURI));
					locationUri.append("/");
					locationUri.append(fileProps.getProperty(DATASTREAM_PROPERTY_PID));		// Dir with the name of the pid
					locationUri.append("/");
					locationUri.append(fileProps.getProperty(DATASTREAM_PROPERTY_ID));		// Dir with name of datastream id
					locationUri.append("/");
					locationUri.append(dsPropFile.getName());
					addDsCmd = addDsCmd.dsLocation(locationUri.toString());
				}
			}

			// Add the created dataset into Fedora.
			AddDatastreamResponse addDsResp = addDsCmd.execute(fedoraClient);
			log.info("Add Datastream Response Status: " + addDsResp.getStatus());
		}
		catch (FedoraClientException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (MalformedURLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
