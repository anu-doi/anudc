package au.edu.anu.datacommons.storage.archive;

import static java.text.MessageFormat.*;
import gov.loc.repository.bagit.Manifest;
import gov.loc.repository.bagit.Manifest.Algorithm;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArchiveItem {
	private static final Logger LOGGER = LoggerFactory.getLogger(ArchiveItem.class);
	
	private String pid;
	private File fileToArchive;
	private Manifest.Algorithm algorithm;
	private String messageDigest;
	private Operation op;
	private File archivedFile = null;
	
	public enum Operation {
		DELETE, REPLACE
	}

	public ArchiveItem(String pid, File fileToArchive, Algorithm algorithm, String messageDigest, Operation op) {
		validateParams(pid, fileToArchive, algorithm, messageDigest, op);
		this.pid = pid;
		this.fileToArchive = fileToArchive;
		this.algorithm = algorithm;
		this.messageDigest = messageDigest;
		this.op = op;
	}

	public String getPid() {
		return pid;
	}
	
	public File getFileToArchive() {
		return fileToArchive;
	}

	public Manifest.Algorithm getAlgorithm() {
		return algorithm;
	}

	public String getMessageDigest() {
		return messageDigest;
	}

	public Operation getOp() {
		return op;
	}

	public File getArchivedFile() {
		return archivedFile;
	}

	/**
	 * Sets the location of the file after being archived. This should only be called by a class of the same package.
	 * 
	 * @param archivedFile
	 *            File object pointing to the location of the archived file.
	 */
	void setArchivedFile(File archivedFile) {
		this.archivedFile = archivedFile;
	}

	private void validateParams(String pid, File fileToArchive, Algorithm algorithm, String messageDigest, Operation op) {
		if (pid == null || pid.length() == 0) {
			throw new IllegalArgumentException(format("Invalid Pid: {0}", pid));
		}
		if (fileToArchive == null || !fileToArchive.exists()) {
			throw new IllegalArgumentException(format("Invalid file to archive: {0}", fileToArchive == null ? "null"
					: fileToArchive.getAbsolutePath()));
		}
	}
}
