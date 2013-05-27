package au.edu.anu.dcclient.tasks;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.WebResource;

public class DownloadFilesTask extends AbstractDcBagTask<Map<String, File>, Void> {
	private static final Logger LOGGER = LoggerFactory.getLogger(DownloadFilesTask.class);
	
	private String pid;
	private List<String> filepaths;
	private Map<String, File> fileResults;
	
	public DownloadFilesTask(String pid, List<String> filepaths) {
		this.pid = pid;
		this.filepaths = filepaths;
		fileResults = new HashMap<String, File>(filepaths.size());
	}

	@Override
	protected Map<String, File> doInBackground() throws Exception {
		for (String filepath : filepaths) {
			fileResults.put(filepath, null);
			try {
				ClientResponse resp = downloadFile(filepath);
				if (resp.getClientResponseStatus() == Status.OK) {
					File dlFile = resp.getEntity(File.class);
					fileResults.put(filepath, dlFile);
					LOGGER.trace(dlFile.getAbsolutePath());
				}
			} catch (Exception e) {
				// No Op.
			}
		}

		return fileResults;
	}


	private ClientResponse downloadFile(String filepath) {
		ClientResponse resp;

		URI pidUri = getBagFileUri(pid, filepath);
		LOGGER.debug("Downloading file from {}", pidUri.toString());

		WebResource webResource = client.resource(pidUri);
		resp = webResource.type(MediaType.APPLICATION_OCTET_STREAM_TYPE).get(ClientResponse.class);
		
		return resp;
	}
	
}
