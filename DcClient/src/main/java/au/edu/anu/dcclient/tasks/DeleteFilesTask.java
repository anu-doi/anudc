package au.edu.anu.dcclient.tasks;

import static java.text.MessageFormat.*;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class DeleteFilesTask extends AbstractDcBagTask<Map<String, Integer>, Void> {
	private static final Logger LOGGER = LoggerFactory.getLogger(DeleteFilesTask.class);
	
	private String pid;
	private List<String> filepaths;
	private Map<String, Integer> result;

	public DeleteFilesTask(String pid, List<String> filepaths) {
		super();
		this.pid = pid;
		this.filepaths = filepaths;
		validateParams();
	}

	@Override
	protected Map<String, Integer> doInBackground() throws Exception {
		result = new HashMap<String, Integer>();
		int fileCount = 0;
		for (String filepath : filepaths) {
			URI fileUri = getBagFileUri(pid, filepath);
			WebResource webResource = client.resource(UriBuilder.fromUri(fileUri).build());
			ClientResponse response = webResource.accept(MediaType.TEXT_PLAIN_TYPE).delete(ClientResponse.class);
			result.put(filepath, response.getStatus());
			setProgress(++fileCount * 100 / filepaths.size());
		}
		return result;
	}

	private void validateParams() {
		if (pid == null || pid.length() == 0) {
			throw new IllegalArgumentException(format("Invalid pid specified: {}", pid));
		}
		if (filepaths == null || filepaths.size() == 0) {
			throw new IllegalArgumentException(format("Invalid filepaths specified: {}", filepaths.toString()));
		}
	}

}
