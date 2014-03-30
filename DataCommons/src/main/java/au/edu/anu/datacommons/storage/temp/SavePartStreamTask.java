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

package au.edu.anu.datacommons.storage.temp;

import static java.text.MessageFormat.format;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.util.StopWatch;
import au.edu.anu.datacommons.util.Util;

/**
 * Task that saves a provided stream as a part-file. Once the last part is saved, all parts are merged in the correct
 * order to create the complete file.
 * 
 * @author Rahul Khanna
 *
 */
public class SavePartStreamTask extends SaveInputStreamTask {
	private static final Logger LOGGER = LoggerFactory.getLogger(SavePartStreamTask.class);
	
	private String fileId;
	private int part;
	private boolean isLastPart;
	
	
	/**
	 * Creates an instance of this task
	 * 
	 * @param uploadDir
	 *            Directory to which the stream will be saved.
	 * @param fileId
	 *            An arbitrary string that identifies the file to which a part stream belongs
	 * @param is
	 *            Part stream as InputStream
	 * @param part
	 *            A positive integer describing the part number in a sequence of parts.
	 * @param isLastPart
	 *            true if the part is the last part in a sequence
	 * @param expectedLength
	 *            Expected length of the entire file (not the part stream)
	 * @param expectedMd5
	 *            Expected MD5 of the entire file (not the part stream)
	 * @throws IOException
	 */
	public SavePartStreamTask(Path uploadDir, String fileId, InputStream is, int part, boolean isLastPart,
			long expectedLength, String expectedMd5) throws IOException {
		super(uploadDir, is, expectedLength, expectedMd5);
		this.fileId = fileId;
		this.part = part;
		this.isLastPart = isLastPart;
	}

	
	@Override
	public UploadedFileInfo call() throws Exception {
		UploadedFileInfo ufi = null;
		Path targetPartFile = createTempFile();
		StopWatch sw = new StopWatch();
		try {
			saveStreamToFile(this.dis, targetPartFile);
		} catch (Exception e) {
			LOGGER.error("Error saving {} ({}) Expected MD5:{} - {}", targetPartFile.toString(),
					Util.byteCountToDisplaySize(this.expectedLength), this.expectedMd5, e.getMessage());
			throw e;
		}
		if (isLastPart) {
			sw.start();
			List<Path> partFiles = getPartFiles();
			Path mergedFile = this.uploadDir.resolve(fileId);
			LOGGER.debug("Merging {} ({}) part files to {} Expected MD5:{}...", partFiles.size(),
					Util.byteCountToDisplaySize(addFilePartSizes(partFiles)), mergedFile.toString(), this.expectedMd5);
			mergeParts(partFiles, mergedFile);
			verifyExpecteds();
			ufi = new UploadedFileInfo(mergedFile.toAbsolutePath(), Files.size(mergedFile), this.actualMd5);
			sw.stop();
			LOGGER.debug("Merged {} ({}) Computed MD5:{}, Time: {}, Speed: {}", ufi.getFilepath().toString(),
					Util.byteCountToDisplaySize(ufi.getSize()), ufi.getMd5(), sw.getTimeElapsedFormatted(), sw.getRate(ufi.getSize()));
		}

		return ufi;
	}
	
	@Override
	protected Path createTempFile() throws IOException {
		Path targetFile = uploadDir.resolve(generatePartFilename(fileId, part)); 
		Files.createFile(targetFile);
		return targetFile;
	}
	
	/**
	 * Generates a unique filename to which the part stream will be saved.
	 * 
	 * @param fileId
	 *            Unique string identifying the full file.
	 * @param part
	 *            Part number of the stream.
	 * @return Unique filename for part stream as String
	 */
	private String generatePartFilename(String fileId, int part) {
		return fileId + "." + String.valueOf(part);
	}
	
	
	/**
	 * Merges specified file parts.
	 * 
	 * @param partFiles
	 *            List of part files in the correct sequence
	 * @param mergedFile
	 *            Path to the merged file
	 * @throws IOException
	 *             when unable to read from partFiles or write to mergedFile
	 */
	private void mergeParts(List<Path> partFiles, Path mergedFile) throws IOException {
		// Check the number of part files.
		if (partFiles.size() < part - 1) {
			throw new IOException(format("Expected {0} part files. {1} found for file id {2}.", part, partFiles.size(),
					fileId));
		}

		// Check if the merged file exists. Delete if it does.
		Files.deleteIfExists(mergedFile);

		DigestOutputStream mergedFileStream = new DigestOutputStream(Files.newOutputStream(mergedFile),
				createMd5Digest());
		try (WritableByteChannel mergedFileChannel = Channels.newChannel(mergedFileStream)) {
			for (int i = 0; i < part; i++) {
				Path partFile = partFiles.get(i);
				try (FileChannel partFileChannel = FileChannel.open(partFile)) {
					partFileChannel.transferTo(0, Files.size(partFile), mergedFileChannel);
				}
			}
		} finally {
			IOUtils.closeQuietly(mergedFileStream);
		}

		// Now that part files are merged, delete them.
		deletePartFiles(partFiles);

		this.actualLength = Files.size(mergedFile);
		this.actualMd5 = Hex.encodeHexString(mergedFileStream.getMessageDigest().digest());
	}

	/**
	 * Deletes specified part files
	 * 
	 * @param partFiles
	 *            List of part files
	 */
	private void deletePartFiles(List<Path> partFiles) {
		for (Path partFile : partFiles) {
			try {
				Files.deleteIfExists(partFile);
			} catch (Exception e) {
				// No op because it's a temporary file.
			}
		}
	}
	
	/**
	 * Gets a list of part files on disk for the fileId provided
	 * 
	 * @return List of part files as List<Path>
	 * @throws IOException
	 */
	private List<Path> getPartFiles() throws IOException {
		List<Path> partFiles = new ArrayList<>();
		PartFilesFilter partFilesFilter = new PartFilesFilter(this.fileId);
		try (DirectoryStream<Path> dirItems = Files.newDirectoryStream(this.uploadDir, partFilesFilter)) {
			for (Path dirItem : dirItems) {
				partFiles.add(dirItem);
			}
		}
		Collections.sort(partFiles, new PartFileComparator());
		return partFiles;
	}
	
	/**
	 * Get the sum of part file sizes
	 * 
	 * @param partFiles
	 *            List of part files whose size to add up
	 * @return Total of file sizes as long, measured in bytes
	 * @throws IOException
	 */
	private long addFilePartSizes(List<Path> partFiles) throws IOException {
		long totalSize = 0L;
		for (Path partFile : partFiles) {
			totalSize += Files.size(partFile);
		}
		return totalSize;
	}

	/**
	 * Comparator class for sorting part files into their correct sequence.
	 * 
	 * @author Rahul Khanna
	 *
	 */
	private final class PartFileComparator implements Comparator<Path> {
		@Override
		public int compare(Path o1, Path o2) {
			if (o1 == null) {
				throw new NullPointerException();
			}
			if (o2 == null) {
				throw new NullPointerException();
			}
			
			int o1PartNum = getPartNum(o1.getFileName().toString());
			int o2PartNum = getPartNum(o2.getFileName().toString());
			if (o1PartNum == o2PartNum) {
				return 0;
			} else if (o1PartNum < o2PartNum) {
				return -1;
			} else {
				return 1;
			}
		}
		
		private int getPartNum(String filename) {
			return Integer.parseInt(filename.substring(filename.lastIndexOf('.') + 1));
		}
	}
	
	/**
	 * Filter class that picks only part files for a specific file ID in any directory.
	 * 
	 * @author Rahul Khanna
	 * 
	 */
	private final class PartFilesFilter implements DirectoryStream.Filter<Path> {
		private String fileId;
		
		public PartFilesFilter(String fileId) {
			super();
			this.fileId = fileId;
		}

		@Override
		public boolean accept(Path entry) throws IOException {
			return entry.getFileName().toString().startsWith(this.fileId + ".");
		}
		
	}
}
