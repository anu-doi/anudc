package au.edu.anu.dcclient.tasks;

import static java.text.MessageFormat.*;
import gov.loc.repository.bagit.Manifest.Algorithm;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URI;

import javax.ws.rs.core.MediaType;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.dcclient.progress.ProgressInputStream;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class UploadFileTask extends AbstractDcBagTask<ClientResponse, Void> {
	private static final Logger LOGGER = LoggerFactory.getLogger(UploadFileTask.class);
	
	protected final String pid;
	protected final String targetFilename;
	protected final File fileToUpload;
	
	public UploadFileTask(String pid, String targetFilename, File fileToUpload) {
		this.pid = pid;
		this.targetFilename = targetFilename;
		this.fileToUpload = fileToUpload;
		validateParameters();
	}
	
	@Override
	protected ClientResponse doInBackground() throws Exception {
		ClientResponse resp;

		URI pidUri = getBagFileUri(pid, "data/" + targetFilename);
		LOGGER.debug("Uploading file to {}", pidUri.toString());

		WebResource webResource = client.resource(pidUri);
		resp = uploadFile(webResource);
		return resp;
	}

	/**
	 * Calculates Message Digest of file fileToUpload.
	 * 
	 * @return Message Digest as String
	 * @throws FileNotFoundException 
	 */
	private String calcMd5() throws FileNotFoundException {
		CalcMessageDigestTask mdTask = new CalcMessageDigestTask(this.fileToUpload, Algorithm.MD5);
		mdTask.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if ("progress".equals(evt.getPropertyName())) {
					setProgress((Integer) (evt.getNewValue()) / 2);
				}
			}
		});
		
		String md5sum = mdTask.calcMd();
		LOGGER.trace("{}, MD5: {}", fileToUpload.getAbsolutePath(), md5sum);
		return md5sum;
	}

	private ClientResponse uploadFile(WebResource webResource) throws FileNotFoundException {
		ClientResponse resp;
		ProgressInputStream fileStream = null;
		try {
			String md5sum = calcMd5();
			fileStream = new ProgressInputStream(new FileInputStream(this.fileToUpload), this.fileToUpload.length());
			fileStream.addPropertyChangeListener(new PropertyChangeListener() {
				
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					if (evt.getPropertyName() != null
							&& evt.getPropertyName().equals("percentComplete")) {
						setProgress(50 + ((Integer) (evt.getNewValue()) / 2));
					}
				}
			});
			resp = webResource.type(MediaType.APPLICATION_OCTET_STREAM_TYPE).header("Content-MD5", md5sum)
					.post(ClientResponse.class, fileStream);
		} finally {
			IOUtils.closeQuietly(fileStream);
		}
		return resp;
	}

	/**
	 * Checks if file to upload exists and is a single file.
	 * 
	 * @throws IllegalArgumentException
	 *             if the file to upload doesn't exist or is not a file.
	 */
	private void validateParameters() {
		if (!fileToUpload.exists()) {
			throw new IllegalArgumentException(format("File {0} doesn't exist.", fileToUpload.getAbsolutePath()));
		}
		if (!fileToUpload.isFile()) {
			throw new IllegalArgumentException(format("{0} is not a file.", fileToUpload.getAbsolutePath()));
		}
	}
}
