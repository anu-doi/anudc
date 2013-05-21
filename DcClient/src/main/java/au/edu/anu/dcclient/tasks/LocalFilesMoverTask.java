package au.edu.anu.dcclient.tasks;

import java.io.File;
import java.util.Map;

import javax.swing.SwingWorker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalFilesMoverTask extends SwingWorker<Map<File, File>, Void> {
	private static final Logger LOGGER = LoggerFactory.getLogger(LocalFilesMoverTask.class);
	
	private Map<File, File> moveList;
	
	public LocalFilesMoverTask(Map<File, File> moveList) {
		this.moveList = moveList;
	}
	
	@Override
	protected Map<File, File> doInBackground() throws Exception {
		int filesCount = moveList.size();
		int filesMoved = 0;
		for (File sourceFile : moveList.keySet()) {
			try {
				if (sourceFile.renameTo(moveList.get(sourceFile))) {
					LOGGER.debug("Moved {} to {}.", sourceFile.getAbsolutePath(), moveList.get(sourceFile)
							.getAbsolutePath());
				} else {
					LOGGER.error("Unable to move {} to {}.", sourceFile.getAbsolutePath(), moveList.get(sourceFile)
							.getAbsolutePath());
					moveList.put(sourceFile, null);
				}
			} catch (Exception e) {
				moveList.put(sourceFile, null);
			} finally {
				setProgress(++filesMoved * 100 / filesCount);
			}
		}
		return moveList;
	}

}
