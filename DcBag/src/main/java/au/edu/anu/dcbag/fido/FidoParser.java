package au.edu.anu.dcbag.fido;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FidoParser
{
	private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getClass());
	private static PythonExecutor pyExec;
	
	private final String output;
	private final PronomFormat fileFormat;
	
	public FidoParser(File fileToId) throws IOException, URISyntaxException
	{
		pyExec = new PythonExecutor(getFidoScriptFile());
		pyExec.execute(new String[] {fileToId.getCanonicalPath()});
		output = pyExec.getOutputAsString();
		fileFormat = new PronomFormat(this.output);
		LOGGER.info("Fido returned {}", output);
	}
	
	public String getOutput()
	{
		return this.output;
	}
	
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
