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

import static java.text.MessageFormat.*;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.security.DigestOutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Rahul Khanna
 *
 */
public class PartTempFileTask extends AbstractTempFileTask {
	static final Logger LOGGER = LoggerFactory.getLogger(PartTempFileTask.class);
	
	private final InputStream partStream;
	private final int part;
	private final boolean isLastPart;
	private final String fileId;
	
	public PartTempFileTask(InputStream partStream, int part, boolean isLastPart, File dir, String fileId) {
		super(dir);
		this.partStream = partStream;
		this.part = part;
		this.isLastPart = isLastPart;
		this.fileId = fileId;				
	}
	
	@Override
	public File call() throws Exception {
		File partFile = getPartFile();
		saveAs(partStream, partFile);
		if (isLastPart == false) {
			return null;
		} else {
			return mergeParts();
		}
	}
	
	protected File mergeParts() throws IOException {
		List<File> partFiles = getPartFiles();
		// Check the number of part files.
		if (partFiles.size() < part - 1) {
			throw new IOException(format("Expected {0} part files. {1} found for file id {2}.", part, partFiles.size(),
					fileId));
		}

		// Check if the merged file doesn't exist. Delete if it does.
		File mergedFile = new File(this.dir, fileId);
		if (mergedFile.exists()) {
			if (!mergedFile.delete()) {
				throw new IOException(format("Already existing merged file {0} could not be deleted for file Id {1}",
						mergedFile.getName(), fileId));
			}
		}

		LOGGER.debug("Merging files {} to {}", partFiles, mergedFile.getAbsolutePath());
		OutputStream mergedFileStream = null;
		try {
			if (this.digester != null) {
				mergedFileStream = new DigestOutputStream(new FileOutputStream(mergedFile), this.digester);
			} else {
				mergedFileStream = new FileOutputStream(mergedFile);
			}
			WritableByteChannel mergedFileChannel = Channels.newChannel(mergedFileStream);
			for (int i = 0; i < part; i++) {
				File partFile = partFiles.get(i);
				FileChannel partFileChannel = null;
				try {
					partFileChannel = new FileInputStream(partFile).getChannel();
					partFileChannel.transferTo(0, partFile.length(), mergedFileChannel);
				} finally {
					IOUtils.closeQuietly(partFileChannel);
				}
			}
		} finally {
			IOUtils.closeQuietly(mergedFileStream);
		}

		// Now that part files are merged, delete them.
		for (File partFile : partFiles) {
			if (!partFile.delete()) {
				partFile.deleteOnExit();
				LOGGER.warn(format("Unable to delete part file {0}. Scheduled deletion on JVM shutdown.",
						partFile.getName()));
			}
		}

		if (this.digester != null) {
			this.calculatedMd = calcMessageDigest(digester);
			if (!calculatedMd.equals(expectedMd)) {
				throw new IOException(format("Calculated message digest of {0}: {1} doesn''t match expected {2}",
						mergedFile.getAbsolutePath(), this.calculatedMd, this.expectedMd));
			}
		}

		return mergedFile;
	}

	private List<File> getPartFiles() {
		List<File> partFiles = Arrays.asList(this.dir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				return pathname.getName().startsWith(fileId + ".");
			}

		}));
		Collections.sort(partFiles, new PartFileComparator());
		return partFiles;
	}

	protected File getPartFile() throws IOException {
		File file = new File(this.dir, generatePartFilename(this.fileId, this.part));
		return file;
	}

	private String generatePartFilename(String fileId, int part) {
		return fileId + "." + String.valueOf(part);
	}

	private final class PartFileComparator implements Comparator<File> {
		@Override
		public int compare(File o1, File o2) {
			if (o1 == null) {
				throw new NullPointerException();
			}
			if (o2 == null) {
				throw new NullPointerException();
			}
			
			int o1PartNum = getPartNum(o1.getName());
			int o2PartNum = getPartNum(o2.getName());
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
}
