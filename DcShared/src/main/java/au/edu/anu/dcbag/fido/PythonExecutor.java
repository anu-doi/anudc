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
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PythonExecutor
{
	private static final Logger LOGGER = LoggerFactory.getLogger(PythonExecutor.class);

	private Process pythonProcess;
	private List<String> cmdLine = new ArrayList<String>();

	public PythonExecutor(File pythonScript) throws IOException
	{
		this(pythonScript, null);
	}
	
	public PythonExecutor(File pythonScript, String[] pythonSwitches) throws IOException
	{
		cmdLine.add(getPythonExe());
		if (pythonSwitches != null)
			cmdLine.addAll(Arrays.asList(pythonSwitches));
		cmdLine.add(pythonScript.getAbsolutePath());
	}

	public void execute() throws IOException
	{
		execute(null);
	}

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

	protected InputStream getStdOutAsInputStream()
	{
		return pythonProcess.getInputStream();
	}
	
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
			output.append(System.getProperty("line.separator"));
		}

		LOGGER.info("Fido returned: {}", output.toString());
		return output.toString();
	}
	
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
