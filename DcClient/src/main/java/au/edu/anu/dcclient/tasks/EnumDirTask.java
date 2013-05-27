package au.edu.anu.dcclient.tasks;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.SwingWorker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnumDirTask extends SwingWorker<Collection<File>, File> {
	private static final Logger LOGGER = LoggerFactory.getLogger(EnumDirTask.class);
	
	private List<File> files = new ArrayList<File>();
	private long totalBytes = 0L;
	
	private File rootDir;
	private boolean includeSubDirs;
	
	public EnumDirTask(File rootDir, boolean includeSubDirs) {
		this.rootDir = rootDir;
		this.includeSubDirs = includeSubDirs;
	}
	
	@Override
	protected Collection<File> doInBackground() throws Exception {
		getFilesInDir(rootDir);
		setProgress(100);
		return files;
	}

	private void getFilesInDir(File dir) {
		File[] filesInDir = dir.listFiles();
		for (int i = 0; i < filesInDir.length; i++) {
			if (filesInDir[i].isFile()) {
				publish(filesInDir[i]);
				files.add(filesInDir[i]);
				totalBytes += filesInDir[i].length();
			} else if (filesInDir[i].isDirectory() && includeSubDirs == true) {
				getFilesInDir(filesInDir[i]);
			}
		}
	}

	public long getTotalBytes() {
		return totalBytes;
	}
}
