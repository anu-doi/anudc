package au.edu.anu.dcbag.fido;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Properties;

import org.apache.tika.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FidoParser
{
	private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getClass());
	
	private final String output;
	private final PronomFormat fileFormat;
	private final PythonExecutor pyExec;
	
	public FidoParser(InputStream fileStream) throws IOException
	{
		pyExec = new PythonExecutor(getFidoScriptFile(), new String[] {"-u"});
		pyExec.execute(new String[] {"-nocontainer", "-"});
		pyExec.sendStreamToStdIn(fileStream);
		output = pyExec.getOutputAsString();
		fileFormat = new PronomFormat(this.output);
		LOGGER.trace("Fido returned '{}'", output);
	}

	public FidoParser(File fileToId) throws IOException
	{
		pyExec = new PythonExecutor(getFidoScriptFile());
		pyExec.execute(new String[] {"-nocontainer", fileToId.getAbsolutePath()});
		output = pyExec.getOutputAsString();
		fileFormat = new PronomFormat(this.output);
		LOGGER.trace("Fido returned '{}'", output);
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
