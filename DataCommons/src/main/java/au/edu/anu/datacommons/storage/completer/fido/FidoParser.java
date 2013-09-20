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

package au.edu.anu.datacommons.storage.completer.fido;

import static java.text.MessageFormat.format;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.properties.GlobalProps;
import au.edu.anu.datacommons.storage.info.PronomFormat;

/**
 * Represents a Fido Parser object that passes an InputStream or File object to Fido for parsing.
 */
public class FidoParser {
	private static final Logger LOGGER = LoggerFactory.getLogger(FidoParser.class);

	private String fidoStr = null;
	private PronomFormat fileFormat = null;
	private final PythonExecutor pyExec;

	/**
	 * Instantiates a new fido parser for parsing data in an InputStream.
	 * 
	 * @param fileStream
	 *            the file stream to parse
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public FidoParser(InputStream fileStream, String filename) throws IOException
	{
		if (fileStream == null) {
			throw new NullPointerException();
		}
		pyExec = new PythonExecutor(Arrays.asList("-u", getFidoPath(), "-nocontainer", "-filename", filename, "-"));
		pyExec.execute();
		try {
			pyExec.sendStreamToStdIn(fileStream);
		} catch (IOException e) {
			
		}
	}

	/**
	 * Instantiates a new fido parser for parsing data in the file specified in a File object.
	 * 
	 * @param fileToId
	 *            the file to id
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public FidoParser(File fileToId) throws IOException {
		if (fileToId == null) {
			throw new NullPointerException();
		}
		List<String> cmdParams = Arrays.asList(getFidoPath(), "-nocontainer",
				fileToId.getAbsolutePath());
		pyExec = new PythonExecutor(cmdParams);
		pyExec.execute();
	}

	/**
	 * Gets the PronomFormat object containing file format details.
	 * 
	 * @return the file format
	 * @throws IOException 
	 */
	public PronomFormat getFileFormat() throws IOException {
		if (fileFormat == null) {
			fileFormat = new PronomFormat(getFidoStr());
		}
		return fileFormat;
	}

	/**
	 * Gets the output String returned by Fido.
	 * 
	 * @return the output string
	 * @throws IOException 
	 */
	public String getFidoStr() throws IOException {
		if (fidoStr == null) {
			fidoStr = filterFirstLine(pyExec.getOutputAsString());
			LOGGER.trace("Fido output string: {}", fidoStr);
		}
		return fidoStr;
	}

	private String filterFirstLine(String str) {
		int splitIndex = str.indexOf('\r');
		if (splitIndex == -1) {
			splitIndex = str.indexOf('\n');
		}
		return str.substring(0, splitIndex);
	}

	private String getFidoPath() {
		String fidoPath = GlobalProps.getFidoPath();
		LOGGER.trace("Using Fido script at {}", fidoPath);
		return fidoPath;
	}
}
