package au.edu.anu.datacommons.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesFile extends Properties
{
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesFile.class);
	
	private File propFile;
	private long lastRead;

	public PropertiesFile(File file) throws IOException
	{
		this.propFile = file;
		loadProps();
	}

	private void loadProps() throws IOException
	{
		InputStream inStream = null;
		try
		{
			inStream = new FileInputStream(this.propFile);
			LOGGER.debug("Reloading properties from file {}", this.propFile.getAbsolutePath());
			this.clear();
			this.load(inStream);
			this.lastRead = new Date().getTime();
		}
		finally
		{
			try
			{
				inStream.close();
			}
			catch (IOException e)
			{
			}
		}
	}
	
	@Override
	public String getProperty(String key)
	{
		if (propFile.lastModified() > this.lastRead)
		{
			try
			{
				loadProps();
			}
			catch (IOException e)
			{
				LOGGER.warn("Unable to read properties file. Returning existing values.");
			}
		}
		
		return super.getProperty(key);
	}
	
	@Override
	public String getProperty(String key, String defaultValue)
	{
		String value = this.getProperty(key);
		if (value == null)
			value = defaultValue;
		return value;
	}
}
