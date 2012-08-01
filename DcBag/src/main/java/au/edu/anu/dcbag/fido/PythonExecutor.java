package au.edu.anu.dcbag.fido;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PythonExecutor
{
	private static final Logger LOGGER = LoggerFactory.getLogger(PythonExecutor.class);

	private File pyFile;
	private Process pythonProcess;
	private List<String> cmdLine;

	public PythonExecutor(File pyFile) throws IOException
	{
		cmdLine = new ArrayList<String>();
		cmdLine.add(getPythonExe());
		cmdLine.add(pyFile.getCanonicalPath());
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
		
		// LOGGER.debug("Executing: {}", execStr.toString());
		pythonProcess = Runtime.getRuntime().exec(cmdLine.toArray(new String[0]));
	}

	protected InputStream getOutputAsInputStream()
	{
		return pythonProcess.getInputStream();
	}

	public String getOutputAsString() throws IOException
	{
		StringBuilder output = new StringBuilder();
		BufferedReader stdout = new BufferedReader(new InputStreamReader(getOutputAsInputStream()));
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
