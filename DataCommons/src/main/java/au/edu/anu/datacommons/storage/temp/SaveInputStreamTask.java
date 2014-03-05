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
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Callable;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.util.Util;

/**
 * @author Rahul Khanna
 *
 */
public class SaveInputStreamTask implements Callable<UploadedFileInfo> {
	private static final Logger LOGGER = LoggerFactory.getLogger(SaveInputStreamTask.class);
	
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");
	
	private Path uploadDir;
	private DigestInputStream dis;
	private long expectedLength;
	private String expectedMd5;

	
	public SaveInputStreamTask(Path uploadDir, InputStream is, long expectedLength, String expectedMd5) throws IOException {
		validateUploadDir(uploadDir);
		this.uploadDir = uploadDir;
		this.dis = new DigestInputStream(is, createMd5Digest());
		this.expectedLength = expectedLength;
		this.expectedMd5 = expectedMd5;
		
		if (expectedLength <= 0 && (expectedMd5 == null || expectedMd5.length() == 0)) {
			LOGGER.warn("Content Length and Expected MD5 not provided. Data integrity cannot be guaranteed.");
		}
	}
	
	@Override
	public UploadedFileInfo call() throws Exception {
		UploadedFileInfo ufi;
		Path targetFile = createTempFile();
		try (ReadableByteChannel srcChannel = Channels.newChannel(this.dis);
				FileChannel targetFileChannel = FileChannel.open(targetFile, StandardOpenOption.WRITE)) {
			LOGGER.debug("Saving file to {} ({}) Computed MD5:{}...", targetFile.toString(),
					Util.byteCountToDisplaySize(expectedLength), expectedMd5);

			long position = 0;
			long count = 0;
			while ((count = targetFileChannel.transferFrom(srcChannel, position, Long.MAX_VALUE)) > 0) {
				position += count;
			}

			verifyLength(position);
			String computedMd5 = Hex.encodeHexString(dis.getMessageDigest().digest());
			verifyMd5(computedMd5);

			ufi = new UploadedFileInfo(targetFile, position, computedMd5);
			LOGGER.debug("Saved file to {} ({}) Computed MD5:{}", ufi.getFilepath().toString(),
					Util.byteCountToDisplaySize(ufi.getSize()), ufi.getMd5());
		} catch (Exception e) {
			LOGGER.error("Error saving file to {} ({}) Expected MD5:{} - {}", targetFile.toString(),
					Util.byteCountToDisplaySize(this.expectedLength), this.expectedMd5, e.getMessage());
			throw e;
		}
		return ufi;
	}

	private Path createTempFile() throws IOException {
		Path tempFile;
		synchronized (uploadDir) {
			do {
				String filename = dateFormat.format(new Date());
				tempFile = uploadDir.resolve(filename);
			} while (Files.exists(tempFile));
			Files.createFile(tempFile);
		}
		return tempFile;
	}

	private void verifyLength(long position) throws IOException {
		if (this.expectedLength > 0) {
			if (position != expectedLength) {
				throw new IOException(format("Saved stream''s length invalid - Expected: {0} ({1}), Actual: {2} ({3})",
						this.expectedLength, Util.byteCountToDisplaySize(this.expectedLength), position,
						Util.byteCountToDisplaySize(position)));
			}
		}
	}
	
	private void verifyMd5(String computedMd5) throws IOException {
		if (this.expectedMd5 != null) {
			if (!expectedMd5.equals(computedMd5)) {
				throw new IOException(format("Saved stream''s MD5 invalid - Expected: {0}, Actual: {1}", expectedMd5,
						computedMd5));
			}
		}
	}

	private MessageDigest createMd5Digest() {
		try {
			return MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			// This shouldn't ever happen.
			throw new RuntimeException(e);
		}
	}

	private void validateUploadDir(Path uploadDir) throws IOException {
		if (!Files.isDirectory(uploadDir)) {
			throw new IOException(format("Upload directory {0} doesn''t exist.", uploadDir.toString()));
		}
		if (!Files.isWritable(uploadDir)) {
			throw new IOException(format("Upload directory {0} isn''t writable. Check permissions.", uploadDir.toString()));
		}
	}

}
