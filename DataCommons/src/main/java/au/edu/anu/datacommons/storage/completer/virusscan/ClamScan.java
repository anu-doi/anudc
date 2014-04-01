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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.properties.GlobalProps;

/**
 * @author Rahul Khanna
 *
 */
public class ClamScan {
	private static final Logger LOGGER = LoggerFactory.getLogger(ClamScan.class);
	
	public String scan(Path filepath) throws IOException, InterruptedException {
		String scanResult = null;
		ProcessBuilder pb = createClamScanProcessBuilder(filepath);
		Process csProc = pb.start();

		String stdOut = readStdOut(csProc);
		String stdErr = readStdErr(csProc);
		csProc.waitFor();
		// ClamScan returns 0 when no virus, 1 when virus found, 2 (or above) if improperly configured.
		if (csProc.exitValue() == 0 || csProc.exitValue() == 1) {
			LOGGER.trace("ClamScan result for {}: {}", filepath, stdOut);
			scanResult = formatScanResult(stdOut);
		} else {
			LOGGER.error("ClamScan errored while scanning {}. stdout: {}, stderr: {}", filepath.toString(), stdOut,
					stdErr);
		}
		return scanResult;
	}

	private String readStdOut(Process proc) throws IOException {
		return readInputStream(proc.getInputStream());
	}
	
	private String readStdErr(Process proc) throws IOException {
		return readInputStream(proc.getErrorStream());
	}

	private String readInputStream(InputStream is) throws IOException {
		StringBuilder sb = new StringBuilder();
		try (InputStreamReader reader = new InputStreamReader(is)) {
			char[] buffer = new char[80];
			for (int nCharsRead = reader.read(buffer); nCharsRead != -1; nCharsRead = reader.read(buffer)) {
				sb.append(buffer, 0, nCharsRead);
			}
		}
		return sb.toString();
	}
	
	private String formatScanResult(String stdOut) {
		String scanResult = null;
		try {
			scanResult = stdOut.substring(stdOut.lastIndexOf(':') + 1).trim();
		} catch (NullPointerException | IndexOutOfBoundsException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return scanResult;
	}

	private ProcessBuilder createClamScanProcessBuilder(Path filepath) {
		ProcessBuilder pb = new ProcessBuilder(getNicePath().toString(), getNiceness(), getClamScanPath().toString(),
				filepath.toString(), "--no-summary");
		if (Files.isDirectory(getClamScanPath().getParent())) {
			pb.directory(getClamScanPath().getParent().toFile());
		}
		return pb;
	}
	
	private Path getNicePath() {
		return GlobalProps.getNicePath();
	}
	
	private String getNiceness() {
		return GlobalProps.getNiceness();
	}
	
	private Path getClamScanPath() {
		return GlobalProps.getClamScanPath();
	}
}
