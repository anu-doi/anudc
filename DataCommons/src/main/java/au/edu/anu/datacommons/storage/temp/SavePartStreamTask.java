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
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
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
 * @author Rahul Khanna
 *
 */
public class SavePartStreamTask extends SaveInputStreamTask {
	private static final Logger LOGGER = LoggerFactory.getLogger(SavePartStreamTask.class);
	
	private String fileId;
	private int part;
	private boolean isLastPart;
	
	
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
	
	private String generatePartFilename(String fileId, int part) {
		return fileId + "." + String.valueOf(part);
	}
	
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

	private void deletePartFiles(List<Path> partFiles) {
		for (Path partFile : partFiles) {
			try {
				Files.deleteIfExists(partFile);
			} catch (Exception e) {
				// No op because it's a temporary file.
			}
		}
	}
	
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
	
	private long addFilePartSizes(List<Path> partFiles) throws IOException {
		long totalSize = 0L;
		for (Path partFile : partFiles) {
			totalSize += Files.size(partFile);
		}
		return totalSize;
	}

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
