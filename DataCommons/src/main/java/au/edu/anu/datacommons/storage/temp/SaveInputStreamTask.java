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

import au.edu.anu.datacommons.util.StopWatch;
import au.edu.anu.datacommons.util.Util;

/**
 * Saves an inputstream to disk.
 * 
 * @author Rahul Khanna
 *
 */
public class SaveInputStreamTask implements Callable<UploadedFileInfo> {
	private static final Logger LOGGER = LoggerFactory.getLogger(SaveInputStreamTask.class);
	
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");
	
	protected Path uploadDir;
	protected DigestInputStream dis;
	protected long expectedLength;
	protected String expectedMd5;
	protected long actualLength;
	protected String actualMd5;

	/**
	 * Creates an instance of this task
	 * 
	 * @param uploadDir
	 *            Directory to which a stream will be saved
	 * @param is
	 *            InputStream to save
	 * @param expectedLength
	 *            Expected length of the stream. No size verification is performed if <= 0 (not recommended)
	 * @param expectedMd5
	 *            Expected MD5 of the stream. No MD5 check is performed if null (not recommended)
	 * @throws IOException
	 *             if uploadDir doesn't exist or cannot be written to
	 */
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
		StopWatch sw = new StopWatch();
		sw.start();
		try {
			LOGGER.debug("Saving {} ({}) Expected MD5:{}...", targetFile.toString(),
					Util.byteCountToDisplaySize(expectedLength), expectedMd5);
			this.actualLength = saveStreamToFile(this.dis, targetFile);
			this.actualMd5 = Hex.encodeHexString(dis.getMessageDigest().digest());
			verifyExpecteds();
			ufi = new UploadedFileInfo(targetFile, this.actualLength, this.actualMd5);
			sw.stop();
			LOGGER.debug("Saved {} ({}) Computed MD5:{}, Time: {}, Speed: {}", ufi.getFilepath().toString(),
					Util.byteCountToDisplaySize(ufi.getSize()), ufi.getMd5(), sw.getTimeElapsedFormatted(), sw.getRate(ufi.getSize()));
		} catch (Exception e) {
			LOGGER.error("Error saving {} ({} bytes) Expected MD5:{} - {}", targetFile.toString(),
					this.expectedLength, this.expectedMd5, e.getMessage());
			throw e;
		} finally {
			IOUtils.closeQuietly(dis);
		}
		return ufi;
	}
	
	/**
	 * Writes out an InputStream to a file on disk.
	 * 
	 * @param srcStream
	 *            Stream to read data from
	 * @param targetFile
	 *            File to which the stream will be written.
	 * @return size of stream, measured in bytes
	 * @throws IOException
	 *             when unable to write to target file
	 */
	protected long saveStreamToFile(InputStream srcStream, Path targetFile) throws IOException {
		long position = 0;
		try (ReadableByteChannel srcChannel = Channels.newChannel(srcStream);
				FileChannel targetFileChannel = FileChannel.open(targetFile, StandardOpenOption.WRITE)) {
			long count = 0;
			while ((count = targetFileChannel.transferFrom(srcChannel, position, Long.MAX_VALUE)) > 0) {
				position += count;
			}
		}
		return position;
	}

	/**
	 * Creates a temporary file with a unique name to save the InputStream to.
	 * 
	 * @return The created temporary file
	 * @throws IOException
	 *             when unable to create a temporary file
	 */
	protected Path createTempFile() throws IOException {
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

	protected void verifyExpecteds() throws IOException {
		verifyLength();
		verifyMd5();
	}
	
	/**
	 * Verifies that the saved file is the size expected in bytes. If expected bytes is specified as <= 0 then size
	 * verification is not performed.
	 * 
	 * @throws IOException
	 */
	protected void verifyLength() throws IOException {
		if (this.expectedLength > 0) {
			if (this.actualLength != this.expectedLength) {
				throw new IOException(format("Saved stream''s length invalid - Expected: {0} ({1}), Actual: {2} ({3})",
						this.expectedLength, Util.byteCountToDisplaySize(this.expectedLength), this.actualLength,
						Util.byteCountToDisplaySize(this.actualLength)));
			}
		}
	}
	
	/**
	 * Verifies that the saved file's MD5 matches the expected MD5. 
	 * @throws IOException
	 */
	protected void verifyMd5() throws IOException {
		if (this.expectedMd5 != null) {
			if (!expectedMd5.equals(this.actualMd5)) {
				throw new IOException(format("Saved stream''s MD5 invalid - Expected: {0}, Actual: {1}", expectedMd5,
						this.actualMd5));
			}
		}
	}

	/**
	 * Creates an instance of the MessageDigest instance for MD5 algorithm.
	 * 
	 * @return An instance of MessageDigest for MD5 algorithm.
	 */
	protected MessageDigest createMd5Digest() {
		try {
			return MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			// This shouldn't ever happen.
			throw new RuntimeException(e);
		}
	}

	/**
	 * Verifies that the specified directory to which the stream will be saved exists and is writable.
	 * 
	 * @param uploadDir
	 *            Directory where stream will be saved
	 * @throws IOException
	 */
	private void validateUploadDir(Path uploadDir) throws IOException {
		if (!Files.isDirectory(uploadDir)) {
			throw new IOException(format("Upload directory {0} doesn''t exist.", uploadDir.toString()));
		}
		if (!Files.isWritable(uploadDir)) {
			throw new IOException(format("Upload directory {0} isn''t writable. Check permissions.", uploadDir.toString()));
		}
	}

}
