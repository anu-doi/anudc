/*******************************************************************************
 * Australian National University Data Commons
 * Copyright (C) 2013  The Australian National University
 * 
 * This file is part of Australian National University Data Commons.
 * 
 * Australian National University Data Commons is free software: you
 * can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package au.edu.anu.datacommons.storage.archive;

import static java.text.MessageFormat.format;
import gov.loc.repository.bagit.Manifest;
import gov.loc.repository.bagit.Manifest.Algorithm;
import gov.loc.repository.bagit.utilities.MessageDigestHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.storage.DcStorage;

/**
 * Task that archives a file by storing it with the same name in a directory with the name of the form:
 * <pre>
 * 2013-08-08_112436.DELETE.MD5.82b4c71afa19cea1926f9e7aa13ede32
 * <-------1------->.<--2->.<3>.<-------------4---------------->
 * </pre>
 * <ol>
 * <li>Date and time in the format yyyy-MM-dd_HHmmss of the operation that caused the file to be archived.</li>
 * <li>Operation that caused the file to be archived.</li>
 * <li>Message Digest algorithm</li>
 * <li>Calculated Message digest of the file being archived.</li>
 * </ol>
 * 
 * @author Rahul Khanna
 * 
 */
public class ArchiveTask implements Callable<File> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ArchiveTask.class);
	private static final MessageFormat archivedFileFormat = new MessageFormat(
			"{0,date,yyyy-MM-dd_HHmmss}.{1}.{2}.{3}/{4}");
	private static final Random rand = new Random();

	public enum Operation {
		DELETE, REPLACE
	}

	private File archiveRootDir;
	private String pid;
	private File fileToArchive;
	private Date timeofArchival;
	private Manifest.Algorithm alg = null;
	private Operation op;

	/**
	 * Constructor for Archive Task that moves the file to be archived into a temporary folder with a name of the form:
	 * 
	 * <pre>
	 * 2013-08-08_112436.DELETE.temp.[RANDOM_NUMBER]
	 * </pre>
	 * <p>
	 * Then, when the {@code call} method gets called by the executor service, the message digest is calculated and the
	 * directory is renamed to the full format.
	 * 
	 * @param archiveRootDir
	 *            Root directory for all archive directories.
	 * @param pid
	 *            Pid of the record to which the file belonged.
	 * @param fileToArchive
	 *            File to archive
	 * @param alg
	 *            Algorithm to use to calculate message digest of file's contents.
	 * @param op
	 *            Operation that caused the file to be archived.
	 * @throws IOException
	 *             when unable to move the file to the temporary directory
	 */
	public ArchiveTask(File archiveRootDir, String pid, File fileToArchive, Algorithm alg, Operation op)
			throws IOException {
		super();
		this.archiveRootDir = archiveRootDir;
		this.pid = pid;
		this.op = op;
		this.timeofArchival = new Date();
		this.fileToArchive = fileToArchive;
		this.fileToArchive = interimArchive(fileToArchive);
		this.alg = alg;
	}

	/**
	 * Calculates the MD5 for the interim-archived file (in the constructor) and renames the directory with the actual
	 * MD5.
	 */
	@Override
	public File call() throws Exception {
		File archivedFile = new File(getPidArchiveDir(), generateArchivedFilename());
		LOGGER.debug("Archiving file {} to {}", fileToArchive.getAbsolutePath(), archivedFile.getAbsolutePath());
		if (!fileToArchive.getParentFile().renameTo(archivedFile.getParentFile())) {
			throw new IOException(format("Unable to archive {0} to {1}", fileToArchive.getAbsolutePath(),
					archivedFile.getAbsolutePath()));
		}
		return archivedFile;
	}

	private File interimArchive(File fileToArchive) throws IOException {
		File interimArchivedFile;
		do {
			interimArchivedFile = new File(getPidArchiveDir(), generateArchivedFilename());
		} while (interimArchivedFile.getParentFile().exists());
		
		LOGGER.debug("Interim archiving file {} to {}", fileToArchive.getAbsolutePath(),
				interimArchivedFile.getAbsolutePath());
		createIfNotExists(interimArchivedFile.getParentFile());
		synchronized (fileToArchive) {
			if (!fileToArchive.renameTo(interimArchivedFile)) {
				throw new IOException(format("Unable to move file {0} to {1}", fileToArchive.getAbsolutePath(),
						interimArchivedFile.getAbsolutePath()));
			}
		}
		return interimArchivedFile;
	}

	private String generateArchivedFilename() {
		String generatedFilename;
		Object[] filenameElements = new Object[5];
		filenameElements[0] = this.timeofArchival;
		filenameElements[1] = this.op.toString();
		if (fileToArchive.isDirectory()) {
			filenameElements[2] = "DIR";
			filenameElements[3] = "NOMD5";
		} else {
			if (this.alg != null) {
				filenameElements[2] = alg.javaSecurityAlgorithm;
				filenameElements[3] = MessageDigestHelper.generateFixity(fileToArchive, alg);
			} else {
				filenameElements[2] = "temp";
				filenameElements[3] = String.valueOf(rand.nextLong());
			}
		}
		filenameElements[4] = this.fileToArchive.getName();
		generatedFilename = archivedFileFormat.format(filenameElements);
		return generatedFilename;
	}

	private File getPidArchiveDir() throws IOException {
		File archivePidDir = new File(this.archiveRootDir, DcStorage.convertToDiskSafe(pid));
		createIfNotExists(archivePidDir);
		return archivePidDir;
	}

	private void createIfNotExists(File dir) throws IOException {
		if (!dir.isDirectory()) {
			if (!dir.mkdirs()) {
				if (!dir.isDirectory()) {
					throw new IOException(format("Unable to create directory {0}.", dir.getAbsolutePath()));
				}
			}
		}
	}

}
