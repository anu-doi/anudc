package au.edu.anu.dcclient.collection;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.core.util.MultivaluedMapImpl;

public class CollectionInfo extends MultivaluedMapImpl implements MultivaluedMap<String, String>
{
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(CollectionInfo.class);

	private File filesDir;
	
	public CollectionInfo(File sourceFile) throws IOException
	{
		super();
		readMap(sourceFile);
	}

	private void readMap(File sourceFile) throws IOException
	{
		FileInputStream fis = null;
		BufferedReader reader = null;
		
		try
		{
			fis = new FileInputStream(sourceFile);
			reader = new BufferedReader(new InputStreamReader(fis));
			String line;
			LOGGER.trace("File contents: ");
			for (line = reader.readLine(); line != null; line = reader.readLine())
			{
				LOGGER.trace(line);
				line = line.trim();
				
				if (line.length() == 0)
					continue;
				
				// Skip if first character is '#'.
				if (line.charAt(0) == '#')
					continue;

				// If '=' doesn't exist in line at all, skip line.
				int equalsIndex = line.indexOf('=');
				if (equalsIndex == -1)
					continue;

				String key = line.substring(0, equalsIndex).trim();
				String value = line.substring(equalsIndex + 1).trim();
				
				// Skip line if key or value is ""
				if (key.length() == 0 || value.length() == 0)
					continue;

				if (key.equalsIgnoreCase("files.dir"))
				{
					this.filesDir = new File(value);
					continue;
				}
				
				this.add(key, value);
			}
		}
		finally
		{
			IOUtils.closeQuietly(reader);
			IOUtils.closeQuietly(fis);
		}
	}
	
	public File getFilesDir()
	{
		return filesDir;
	}
}