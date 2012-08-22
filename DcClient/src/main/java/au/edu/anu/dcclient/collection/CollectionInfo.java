package au.edu.anu.dcclient.collection;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;
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
	private static final String NEWLINE = System.getProperty("line.separator");

	private File sourceFile;
	private File filesDir = null;
	private String pid = null;

	public CollectionInfo(File sourceFile) throws IOException
	{
		super();
		if (!sourceFile.isFile())
			throw new IOException(MessageFormat.format("{0} is not a file.", sourceFile.getAbsolutePath()));
		if (!sourceFile.canRead())
			throw new IOException(MessageFormat.format("Unable to read {0}. Ensure read permission.", sourceFile.getAbsolutePath()));
		if (!sourceFile.canWrite())
			throw new IOException(MessageFormat.format("Unable to write {0}. Ensure write permission.", sourceFile.getAbsolutePath()));
		
		this.sourceFile = sourceFile;
		readMap(this.sourceFile);
	}

	public String getPid()
	{
		return pid;
	}

	public void setPid(String pid) throws IOException
	{
		if (this.containsKey("pid"))
		{
			LOGGER.warn("Pid already exists. Leaving Pid unchanged.");
			return;
		}
		
		this.pid = pid;
		super.add("pid", pid);
		
		// Write pid to collection info file.
		FileWriter writer = null;
		try
		{
			writer = new FileWriter(this.sourceFile, true);
			writer.write(MessageFormat.format("{0}pid={1}{0}", NEWLINE, pid));
		}
		finally
		{
			IOUtils.closeQuietly(writer);
		}

	}

	public File getFilesDir()
	{
		return filesDir;
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
					if (!this.filesDir.exists() || !this.filesDir.isDirectory())
						throw new IOException(MessageFormat.format("{0} doesn't exist or isn't a directory.", this.filesDir.getAbsolutePath()));
					continue;
				}

				if (key.equalsIgnoreCase("pid"))
				{
					this.pid = value;
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
}
