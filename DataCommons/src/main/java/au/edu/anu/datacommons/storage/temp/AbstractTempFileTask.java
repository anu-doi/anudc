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

import gov.loc.repository.bagit.Manifest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.security.DigestInputStream;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.concurrent.Callable;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Rahul Khanna
 *
 */
public abstract class AbstractTempFileTask implements Callable<File> {
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTempFileTask.class);
	protected static int BUFFER_SIZE = 8192;
	protected static Random random = new Random();
	
	protected File dir;
	protected MessageDigest digester;
	protected String expectedMd;
	protected String calculatedMd;
	
	public AbstractTempFileTask(File dir) {
		this.dir = dir;
	}

	public void setExpectedMessageDigest(Manifest.Algorithm algorithm, String expectedMd) {
		try {
			this.digester = MessageDigest.getInstance(algorithm.javaSecurityAlgorithm);
			this.expectedMd = expectedMd.trim().toLowerCase();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Saves an InputStream to a File on disk. Closes the InputStream when done.
	 * 
	 * @param src
	 *            InputStream from where data will be read
	 * @param file
	 *            File to which data will be written
	 * @throws IOException
	 *             when unable to read from inputstream or write to file
	 */
	protected void saveAs(InputStream src, File file) throws IOException {
		FileOutputStream fileOutStream = null;
		try {
			ReadableByteChannel sourceChannel = Channels.newChannel(src);
			fileOutStream = new FileOutputStream(file);
			FileChannel targetChannel = fileOutStream.getChannel();
			copyChannel(sourceChannel, targetChannel);
			LOGGER.debug("Saved InputStream to {}. ({})", file.getAbsolutePath(),
					FileUtils.byteCountToDisplaySize(file.length()));
		} catch (IOException e) {
			LOGGER.error("Unable to save InputStream. {}", e.getMessage());
			if (file.isFile() && !file.delete()) {
				file.deleteOnExit();
				LOGGER.warn("Unable to delete {}. Will be deleted on JVM shutdown", file.getAbsolutePath());
			}
			throw e;
		} finally {
			IOUtils.closeQuietly(src);
			IOUtils.closeQuietly(fileOutStream);
		}
	}

	protected void copyChannel(ReadableByteChannel srcChnl, WritableByteChannel destChnl) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
		while (srcChnl.read(buffer) != -1) {
			buffer.flip();
			destChnl.write(buffer);
			buffer.compact();
		}

		buffer.flip();
		while (buffer.hasRemaining()) {
			destChnl.write(buffer);
		}
	}
	
	protected String calcMessageDigest(MessageDigest digester) {
		return new String(Hex.encodeHex(digester.digest(), true));
	}

	public String getCalculatedMd() {
		return this.calculatedMd;
	}
}
