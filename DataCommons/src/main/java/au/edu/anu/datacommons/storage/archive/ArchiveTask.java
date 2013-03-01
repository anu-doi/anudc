package au.edu.anu.datacommons.storage.archive;

import static java.text.MessageFormat.*;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.Callable;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.properties.GlobalProps;
import au.edu.anu.datacommons.storage.DcStorage;

public class ArchiveTask implements Callable<Collection<ArchiveItem>> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ArchiveTask.class);
	// private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
	private static final MessageFormat archivedFileFormat = new MessageFormat(
			"{0,date,yyyy-MM-dd_HHmmss}.{1}.{2}.{3}/{4}");
	static File archiveBaseDir = null;

	private Collection<ArchiveItem> items = new ArrayList<ArchiveItem>();

	public void addArchiveItem(ArchiveItem item) {
		this.items.add(item);
	}

	public void addArchiveItems(Collection<ArchiveItem> items) {
		this.items.addAll(items);
	}

	@Override
	public Collection<ArchiveItem> call() throws Exception {
		for (ArchiveItem item : this.items) {
			File fileToArchive = null;
			File archivedFile = null;
			try {
				fileToArchive = item.getFileToArchive();
				archivedFile = generateArchivedFile(item);
				createIfNotExists(archivedFile.getParentFile());
				if (fileToArchive.renameTo(archivedFile)) {
					item.setArchivedFile(archivedFile);
					LOGGER.debug(
							"Archived {} ({}) [{}:{}] to {}",
							new Object[] { item.getFileToArchive(),
									FileUtils.byteCountToDisplaySize(item.getArchivedFile().length()),
									item.getAlgorithm() == null ? null : item.getAlgorithm().javaSecurityAlgorithm,
									item.getMessageDigest(), item.getArchivedFile().getAbsolutePath() });
				} else {
					LOGGER.error("Unable to archive {} to {}.", fileToArchive, archivedFile);
				}
			} catch (IOException e) {
				LOGGER.error("Unable to archive {} to {}.", fileToArchive, archivedFile);
			}
		}
		return this.items;
	}

	private File generateArchivedFile(ArchiveItem item) throws IOException {
		File archivedFile;
		archivedFile = new File(getArchiveDir(item.getPid()), generateArchivedFilename(item));
		return archivedFile;
	}

	private String generateArchivedFilename(ArchiveItem item) {
		String generatedFilename;
		Object[] filenameElements = new Object[5];
		filenameElements[0] = new Date();
		filenameElements[1] = item.getOp().toString();
		filenameElements[2] = item.getAlgorithm() == null ? "null" : item.getAlgorithm().javaSecurityAlgorithm;
		filenameElements[3] = item.getMessageDigest() == null ? "null" : item.getMessageDigest();
		filenameElements[4] = item.getFileToArchive().getName();
		generatedFilename = archivedFileFormat.format(filenameElements);
		return generatedFilename;
	}

	private File getArchiveDir(String pid) throws IOException {
		File archiveBaseDir;
		if (ArchiveTask.archiveBaseDir == null) {
			archiveBaseDir = GlobalProps.getArchiveBaseDirAsFile();
		} else {
			archiveBaseDir = ArchiveTask.archiveBaseDir;
		}
		File archivePidDir = new File(archiveBaseDir, DcStorage.convertToDiskSafe(pid));
		createIfNotExists(archivePidDir);
		return archivePidDir;
	}

	private void createIfNotExists(File dir) throws IOException {
		if (!dir.exists()) {
			if (!dir.mkdirs()) {
				throw new IOException(format("Unable to create directory {0}.", dir.getAbsolutePath()));
			}
		}
	}
}
