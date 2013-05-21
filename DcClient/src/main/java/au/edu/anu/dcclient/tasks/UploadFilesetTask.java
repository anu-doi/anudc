package au.edu.anu.dcclient.tasks;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.ClientResponse;

public class UploadFilesetTask extends AbstractDcBagTask<Map<File, FileTaskInfo>, Void> {
	private static final Logger LOGGER = LoggerFactory.getLogger(UploadFilesetTask.class);
	
	private final String pid;
	private long totalBytesToUpload = 0L;
	private long bytesUploaded = 0L;
	private Map<File, FileTaskInfo> filesetStatus = new HashMap<File, FileTaskInfo>();
	
	public UploadFilesetTask(String pid, Collection<File> fileset) {
		this.pid = pid;
		populateFilesetStatusKeys(fileset);
	}

	@Override
	protected Map<File, FileTaskInfo> doInBackground() throws Exception {
		for (File iFile : filesetStatus.keySet()) {
			final FileTaskInfo fileTaskInfo = filesetStatus.get(iFile);
			UploadFileTask upTask = new UploadFileTask(pid, iFile.getName(), iFile);
			upTask.addPropertyChangeListener(new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					LOGGER.trace("{}, {}", evt.getSource().toString());
					if ("progress".equals(evt.getPropertyName())) {
						setProgress((Integer) evt.getNewValue());
					}
				}
			});
			
			bytesUploaded += iFile.length();
			fileTaskInfo.setStatus(FileTaskInfo.Status.INPROGRESS);
			ClientResponse resp = upTask.doInBackground();
			fileTaskInfo.setRespStatusCode(resp.getStatus());
			fileTaskInfo.setRespStatusStr(resp.getEntity(String.class));
			if (fileTaskInfo.getRespStatusCode() == 200) {
				fileTaskInfo.setStatus(FileTaskInfo.Status.SUCCESS);
			} else {
				fileTaskInfo.setStatus(FileTaskInfo.Status.FAILED);
			}
		}
		
		setProgress(100);
		return filesetStatus;
	}

	private void populateFilesetStatusKeys(Collection<File> fileset) {
		for (File iFile : fileset) {
			FileTaskInfo iFileInfo = new FileTaskInfo(iFile.length());
			filesetStatus.put(iFile, iFileInfo);
			totalBytesToUpload += iFile.length();
		}
	}
	
}
