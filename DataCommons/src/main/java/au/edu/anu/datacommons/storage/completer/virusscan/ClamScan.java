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

package au.edu.anu.datacommons.storage.completer.virusscan;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.properties.GlobalProps;

/**
 * @author Rahul Khanna
 *
 */
public class ClamScan {
	private static final Logger LOGGER = LoggerFactory.getLogger(ClamScan.class);

    private static final String CONFIG_CLAMSCAN_PATH = "clamscan.path";
    private static final String CONFIG_CLAMSCAN_TIMEOUT = "clamscan.timeout";

	public String scan(InputStream scanStream) throws IOException, InterruptedException {
		Objects.requireNonNull(scanStream);
		String respStr = execClamScan(scanStream);
    	return respStr;
    }

	private String execClamScan(InputStream scanStream) throws IOException, InterruptedException {
		String respStr;

		ProcessBuilder pb = generateClamScanProcessBuilder();
		Process csProc = pb.start();
		try (InputStream csStdout = new BufferedInputStream(csProc.getInputStream());
				OutputStream csStdin = new BufferedOutputStream(csProc.getOutputStream())) {
			// copy bytes from the stream to be scanned into the process' stdin
			long nBytesCopied = IOUtils.copyLarge(scanStream, csStdin);
			// close stdin so the process knows no more bytes are coming
			IOUtils.closeQuietly(csStdin);
			respStr = IOUtils.toString(csStdout, StandardCharsets.UTF_8);
			// trim response of whitespaces and newlines
			respStr = respStr.trim();
			csProc.waitFor(getClamScanTimeout(), TimeUnit.SECONDS);
			LOGGER.debug("ClamScan scanned streamSize={} bytes;result={}", nBytesCopied, respStr);
		}
		return respStr;
	}

	private ProcessBuilder generateClamScanProcessBuilder() {
		return new ProcessBuilder(getClamScanProcPath(), "--no-summary", "--max-filesize=500M", "-");
	}
    
    private String getClamScanProcPath() {
    	return GlobalProps.getProperty(CONFIG_CLAMSCAN_PATH);
    }
    
    private long getClamScanTimeout() {
    	return GlobalProps.getPropertyAsLong(CONFIG_CLAMSCAN_TIMEOUT, 120L);
    }
}
