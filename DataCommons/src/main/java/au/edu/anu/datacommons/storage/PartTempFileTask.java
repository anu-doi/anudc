package au.edu.anu.datacommons.storage;

import java.io.IOException;

import org.apache.commons.fileupload.FileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PartTempFileTask extends TempFileTask {
	private static final Logger LOGGER = LoggerFactory.getLogger(PartTempFileTask.class);
	
	private int partNum;
	private boolean isLastPart;
	private String pid;
	
	public PartTempFileTask(FileItem fileItem, int partNum, boolean isLastPart, String pid, String dirSuffix) throws IOException {
		super(fileItem.getInputStream());
		this.partNum = partNum;
		this.isLastPart = isLastPart;
		this.pid = pid;
	}
}
