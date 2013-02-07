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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.config.Config;

/**
 * This class allows for execution of a Python script, passing specified parameters to it, send data through standard input stream (STDIN) and retrieve the
 * output from the Standard Output (STDOUT)
 */
public class PythonExecutor
{
	private static final Logger LOGGER = LoggerFactory.getLogger(PythonExecutor.class);

	private Process pythonProcess;
	private final List<String> cmdLine = new ArrayList<String>();

	/**
	 * Instantiates a new python executor for a specified Python script.
	 * 
	 * @param pythonScript
	 *            the python script
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public PythonExecutor(File pythonScript) throws IOException
	{
		this(pythonScript, null);
	}
	
	/**
	 * Instantiates a new python executor for a specified Python script with command line arguments for the Python interpreter.
	 * 
	 * @param pythonScript
	 *            the python script
	 * @param pythonSwitches
	 *            the python switches
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public PythonExecutor(File pythonScript, String[] pythonSwitches) throws IOException
	{
		cmdLine.add(getPythonExe());
		if (pythonSwitches != null)
			cmdLine.addAll(Arrays.asList(pythonSwitches));
		cmdLine.add(pythonScript.getAbsolutePath());
	}

	/**
	 * Executes the script with no command line parameters passed to the script.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void execute() throws IOException
	{
		execute(null);
	}

	/**
	 * Executes the script with command line parameters passed as a String array.
	 * 
	 * @param cmdParams
	 *            command line parameters as String[]
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void execute(String[] cmdParams) throws IOException
	{
		if (cmdParams != null)
			for (String iParam : cmdParams)
				cmdLine.add(iParam);
		
		if (LOGGER.isDebugEnabled())
		{
			StringBuilder cmdLineAsStr = new StringBuilder();
			for (String cmdArg : cmdLine)
			{
				if (cmdArg.contains(" "))
					cmdLineAsStr.append("\"");
				cmdLineAsStr.append(cmdArg);
				if (cmdArg.contains(" "))
					cmdLineAsStr.append("\"");
				cmdLineAsStr.append(" ");
			}
			LOGGER.debug("Executing: {}", cmdLineAsStr.toString().trim());
		}
		pythonProcess = Runtime.getRuntime().exec(cmdLine.toArray(new String[0]));
	}

	/**
	 * Sends bytes from an InputStream to the script's Stdin.
	 * 
	 * @param inStream
	 * InputStream from which data will be read
	 * @throws IOException
	 * when unable to read from input stream or write to StdIn
	 */
	public void sendStreamToStdIn(InputStream inStream) throws IOException
	{
		OutputStream outStream = pythonProcess.getOutputStream();
		byte buffer[] = new byte[1024 * 1024];
		int numBytesRead = 0;

		try
		{
			while ((numBytesRead = inStream.read(buffer)) != -1)
				outStream.write(buffer, 0, numBytesRead);
			outStream.flush();
		}
		finally
		{
			IOUtils.closeQuietly(inStream);
			IOUtils.closeQuietly(outStream);
		}
	}

	/**
	 * Gets the StdOut as an InputStream from which data can be read.
	 * 
	 * @return StdOut as InputStream
	 */
	protected InputStream getStdOutAsInputStream()
	{
		return pythonProcess.getInputStream();
	}
	
	/**
	 * Gets the StdIn stream as OutputStream to which data can be written.
	 * 
	 * @return StdIn as OutputStream
	 */
	protected OutputStream getStdInAsOutputStream()
	{
		return pythonProcess.getOutputStream();
	}

	public String getOutputAsString() throws IOException
	{
		StringBuilder output = new StringBuilder();
		BufferedReader stdout = new BufferedReader(new InputStreamReader(getStdOutAsInputStream()));
		for (String str = stdout.readLine(); str != null; str = stdout.readLine())
		{
			output.append(str);
			output.append(Config.NEWLINE);
		}

		LOGGER.info("Fido returned: {}", output.toString());
		return output.toString();
	}
	
	/**
	 * Reads the location of the python executable from fido.properties file.
	 * 
	 * @return Path to Python executable as String
	 */
	private String getPythonExe()
	{
		Properties fidoProps = new Properties();
		File propFile = new File(System.getProperty("user.home"), "fido.properties");
		FileInputStream fis = null;
		String pythonExe = "python2.7";
		try
		{
			fis = new FileInputStream(propFile);
			fidoProps.load(fis);
			pythonExe = fidoProps.getProperty("python.exe");
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
		finally
		{
			IOUtils.closeQuietly(fis);
		}

		LOGGER.debug("Using {} for python executable", pythonExe);
		return pythonExe;
	}
}
