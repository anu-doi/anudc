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
import gov.loc.repository.bagit.Manifest;
import gov.loc.repository.bagit.Manifest.Algorithm;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Rahul Khanna
 *
 */
public class TempFileTask extends AbstractTempFileTask {
	private static final Logger LOGGER = LoggerFactory.getLogger(TempFileTask.class);

	private static final int CONNECTION_TIMEOUT_MS = 30000;
	private static final int READ_TIMEOUT_MS = 30000;

	private URL fileUrl = null;
	private InputStream inputStream = null;
	private long expectedSize = -1;
	
	private File savedFile = null;

	public TempFileTask(URL fileUrl, File dir) {
		super(dir);
		this.fileUrl = fileUrl;
	}

	public TempFileTask(InputStream inputStream, File dir) {
		super(dir);
		this.inputStream = inputStream;
	}
	
	public void setExpectedSize(long expectedSize) {
		this.expectedSize = expectedSize;
	}

	@Override
	public File call() throws Exception {
		InputStream digestInputStream = wrapInputStream();
		this.savedFile = File.createTempFile("TempFile", null, this.dir);
		saveAs(digestInputStream, this.savedFile);
		verifySavedFileSize();
		verifySavedFileMessageDigest();
		return savedFile;
	}

	private InputStream wrapInputStream() throws IOException {
		InputStream digestInputStream = null;
		if (this.fileUrl != null) {
			if (this.digester != null) {
				digestInputStream = new DigestInputStream(openUrlStream(this.fileUrl), this.digester);
			} else {
				digestInputStream = openUrlStream(this.fileUrl);
			}
		} else if (this.inputStream != null) {
			if (this.digester != null) {
				digestInputStream = new DigestInputStream(this.inputStream, this.digester);
			} else {
				digestInputStream = inputStream;
			}
		}
		return digestInputStream;
	}

	private void verifySavedFileSize() throws IOException {
		if (this.expectedSize != -1) {
			if (this.savedFile.length() != this.expectedSize) {
				throw new IOException(format("Actual size {0} bytes does not match expected {1} bytes", this.savedFile.length(), this.expectedSize));
			}
		}
	}

	private void verifySavedFileMessageDigest() throws IOException {
		if (this.digester != null) {
			this.calculatedMd = calcMessageDigest(this.digester);
			if (!expectedMd.equals(calculatedMd)) {
				String errorMsg = format("Calculated {0} {1} does not match expected {2}.", digester.getAlgorithm(),
						calculatedMd, expectedMd);
				LOGGER.error(errorMsg);
				if (!savedFile.delete()) {
					savedFile.deleteOnExit();
				}
				throw new IOException(errorMsg);
			} else {
				LOGGER.debug("Calculated {} {} matches expected {}", new Object[] {digester.getAlgorithm(), calculatedMd, expectedMd});
			}
		}
	}

	private InputStream openUrlStream(URL fileUrl) throws IOException {
		URLConnection connection = fileUrl.openConnection();
		connection.setConnectTimeout(CONNECTION_TIMEOUT_MS);
		connection.setReadTimeout(READ_TIMEOUT_MS);
		LOGGER.debug("Opened InputStream from {}", fileUrl);
		return connection.getInputStream();
	}
}
