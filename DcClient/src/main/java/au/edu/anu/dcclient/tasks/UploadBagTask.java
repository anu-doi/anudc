package au.edu.anu.dcclient.tasks;

import gov.loc.repository.bagit.Bag.Format;
import gov.loc.repository.bagit.ProgressListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.Authenticator;
import java.net.URI;
import java.text.MessageFormat;
import java.util.concurrent.Callable;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.dcbag.DcBag;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

public class UploadBagTask extends AbstractDcBagTask implements Callable<ClientResponse>
{
	private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getClass());

	private DcBag dcBag;
	private URI bagBaseUri;

	/**
	 * UploadBagTask
	 * 
	 * Australian National University Data Commons
	 * 
	 * Constructor for UploadBagTask
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		26/06/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @param dcBag
	 * @param bagBaseUri
	 */
	public UploadBagTask(DcBag dcBag, URI bagBaseUri)
	{
		super();
		this.dcBag = dcBag;
		this.bagBaseUri = bagBaseUri;
	}

	/**
	 * call
	 * 
	 * Australian National University Data Commons
	 * 
	 * Uploads a bag to ANU Data Commons
	 * 
	 * @see java.util.concurrent.Callable#call()
	 * 
	 *      <pre>
	 * Version	Date		Developer			Description
	 * 0.1		26/06/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @return Response from the server as ClientResponse.
	 * @throws Exception
	 */
	@Override
	public ClientResponse call() throws Exception
	{
		File zipFile = null;
		try
		{
			updateProgress("Creating ZIP file for upload", dcBag, null, null);
			if (this.plSet != null)
				for (ProgressListener l : plSet)
					dcBag.addProgressListener(l);
			zipFile = dcBag.saveAs(dcBag.getFile().getParentFile(), dcBag.getExternalIdentifier(), Format.ZIP);
			if (zipFile == null)
				throw new Exception("Unable to save the bag as a ZIP file.");

			updateProgress("Uploading ZIP file to Data Commons", zipFile, null, null);
			ClientResponse resp = uploadFile(zipFile);
			updateProgress("done", null, null, null);
			dcBag.close();
			zipFile.delete();
			return resp;
		}
		finally
		{
			if (zipFile != null)
				zipFile.delete();
		}

	}

	/**
	 * uploadFile
	 * 
	 * Australian National University Data Commons
	 * 
	 * Submits a POST request to Bags URI.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		26/06/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @param serializedBagFile
	 *            Bagfile
	 * @return Response from the server as ClientResponse
	 * @throws UniformInterfaceException
	 * @throws ClientHandlerException
	 * @throws FileNotFoundException
	 */
	private ClientResponse uploadFile(File serializedBagFile) throws UniformInterfaceException, ClientHandlerException, FileNotFoundException
	{
		URI pidUri = UriBuilder.fromUri(bagBaseUri).path(dcBag.getExternalIdentifier()).build();
		Client client = Client.create(new DefaultClientConfig());
		// client.addFilter(new HTTPBasicAuthFilter(Authenticator.requestPasswordAuthentication(pidUri.getHost(), )));
		client.setChunkedEncodingSize(1024 * 1024);
		WebResource webResource = client.resource(pidUri);
		// TODO Replace content disposition using an object.
		// ContentDisposition contDisp2 = new ContentDispositionBuilder("attachment").fileName(serializedBagFile.getName()).size(serializedBagFile.length()).build();
		String contDisp = MessageFormat.format("attachment; filename=\"{0}\"", serializedBagFile.getName());

		ClientResponse response = webResource.type(MediaType.APPLICATION_OCTET_STREAM_TYPE).header("Content-Disposition", contDisp)
				.post(ClientResponse.class, new FileInputStream(serializedBagFile));
		LOGGER.info("Response status: {}", response.getStatus());
		return response;
	}
}
