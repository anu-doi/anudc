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

package au.edu.anu.dcbag.fido;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a Fido Parser object that passes an InputStream or File object to Fido for parsing.
 */
public class FidoParser
{
	private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getClass());

	private final String output;
	private final PronomFormat fileFormat;
	private final PythonExecutor pyExec;

	/**
	 * Instantiates a new fido parser for parsing data in an InputStream.
	 * 
	 * @param fileStream
	 *            the file stream to parse
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public FidoParser(InputStream fileStream) throws IOException
	{
		pyExec = new PythonExecutor(getFidoScriptFile(), new String[] { "-u" });
		pyExec.execute(new String[] { "-nocontainer", "-" });
		pyExec.sendStreamToStdIn(fileStream);
		output = pyExec.getOutputAsString();
		fileFormat = new PronomFormat(this.output);
		LOGGER.trace("Fido returned '{}'", output);
	}

	/**
	 * Instantiates a new fido parser for parsing data in the file specified in a File object.
	 * 
	 * @param fileToId
	 *            the file to id
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public FidoParser(File fileToId) throws IOException
	{
		pyExec = new PythonExecutor(getFidoScriptFile());
		pyExec.execute(new String[] { "-nocontainer", fileToId.getAbsolutePath() });
		output = pyExec.getOutputAsString();
		fileFormat = new PronomFormat(this.output);
		LOGGER.trace("Fido returned '{}'", output);
	}

	/**
	 * Gets the output String returned by Fido.
	 * 
	 * @return the output string
	 */
	public String getOutput()
	{
		return this.output;
	}

	/**
	 * Gets the PronomFormat object containing file format details.
	 * 
	 * @return the file format
	 */
	public PronomFormat getFileFormat()
	{
		return fileFormat;
	}

	private File getFidoScriptFile()
	{
		Properties fidoProps = new Properties();
		File propFile = new File(System.getProperty("user.home"), "fido.properties");
		FileInputStream fis;
		try
		{
			fis = new FileInputStream(propFile);
			fidoProps.load(fis);
			fis.close();
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new File(fidoProps.getProperty("fido.py"));
	}
}
