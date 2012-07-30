package au.edu.anu.dcbag.fido;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PythonExecutor
{
	private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getClass());

	private File pyFile;
	private Process pythonProcess;
	private String pythonExe;

	public PythonExecutor(File pyFile)
	{
		initPythonExe();
		this.pyFile = pyFile;
	}

	public void execute() throws IOException
	{
		execute(null);
	}

	public void execute(String cmdParams) throws IOException
	{
		StringBuilder execStr = new StringBuilder();
		execStr.append("\"");
		execStr.append(pythonExe);
		execStr.append("\"");
		execStr.append(" \"");
		execStr.append(pyFile.getCanonicalPath());
		execStr.append("\"");
		if (cmdParams != null && !cmdParams.equals(""))
		{
			execStr.append(" ");
			execStr.append(cmdParams);
		}
		LOGGER.debug("Executing: {}", execStr.toString());
		pythonProcess = Runtime.getRuntime().exec(execStr.toString());
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

		return output.toString();
	}
	
	private void initPythonExe()
	{
		Properties fidoProps = new Properties();
		File propFile = new File(System.getProperty("user.home"), "fido.properties");
		FileInputStream fis = null;
		try
		{
			fis = new FileInputStream(propFile);
			fidoProps.load(fis);
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
		
		this.pythonExe = fidoProps.getProperty("python.exe");
	}
}
