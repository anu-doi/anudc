package au.edu.anu.dcclient.tasks;

import static java.text.MessageFormat.*;
import gov.loc.repository.bagit.Bag.Format;
import gov.loc.repository.bagit.ProgressListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.io.FileUtils;
import org.apache.tika.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.dcbag.DcBag;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

/**
 * This class represents a task that uploads a bag to Data Commons.
 */
public class UploadBagTask extends AbstractDcBagTask<ClientResponse>
{
	private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getClass());

	private final DcBag dcBag;
	private final URI bagBaseUri;

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
		stopWatch.start();

		File zipFile = null;
		try
		{
			updateProgress("Creating ZIP file for upload", dcBag, null, null);
			System.out.print("Zipping bag contents... ");
			if (this.plSet != null)
				for (ProgressListener l : plSet)
					dcBag.addProgressListener(l);
			zipFile = dcBag.saveAs(dcBag.getFile().getParentFile(), dcBag.getExternalIdentifier(), Format.ZIP);
			if (zipFile == null)
				throw new Exception("Unable to save the bag as a ZIP file.");

			System.out.println("[OK]");
			updateProgress("Uploading ZIP file to ANU Data Commons", zipFile, null, null);
			ClientResponse resp = uploadFile(zipFile);
			dcBag.close();
			zipFile.delete();
			return resp;
		}
		finally
		{
			dcBag.close();
			updateProgress("done", null, null, null);
			if (zipFile != null)
				zipFile.delete();
			stopWatch.end();
			LOGGER.info("Time - Upload Bag Task: {}", stopWatch.getFriendlyElapsed());
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
		WebResource webResource = client.resource(pidUri);
		// TODO Replace content disposition using an object.
		// ContentDisposition contDisp2 = new ContentDispositionBuilder("attachment").fileName(serializedBagFile.getName()).size(serializedBagFile.length()).build();
		String contDisp = format("attachment; filename=\"{0}\"", serializedBagFile.getName());

		FileInputStream bagStream = null;
		ClientResponse response;
		System.out.println(format("Uploading Zip file ({0}) to ANU Data Commons...", FileUtils.byteCountToDisplaySize(serializedBagFile.length())));
		try
		{
			bagStream = new FileInputStream(serializedBagFile) {
				protected long numBytes = 0L;
				protected String progressString = "";
				
				@Override
				public int read() throws IOException
				{
					System.out.print(".");
					return super.read();
				}
				
				@Override
				public int read(byte b[]) throws IOException
				{
					numBytes += b.length;
					String tempStr = FileUtils.byteCountToDisplaySize(numBytes);
					if (!tempStr.equals(progressString))
					{
						progressString = tempStr;
						System.out.print(format("\r{0} transferred.          ", FileUtils.byteCountToDisplaySize(numBytes)));
					}
					return super.read(b);
				}
				
				@Override
				public int read(byte b[], int off, int len) throws IOException {
			        System.out.print(".");
					return super.read(b, off, len);
			    }
				
			};
			response = webResource.type(MediaType.APPLICATION_OCTET_STREAM_TYPE).header("Content-Disposition", contDisp).post(ClientResponse.class, bagStream);
			System.out.println();
		}
		finally
		{
			IOUtils.closeQuietly(bagStream);
		}
		LOGGER.info("Response status: {}", response.getStatus());
		return response;
	}
}
