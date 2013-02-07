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

package au.edu.anu.dcclient.collection;

import static java.text.MessageFormat.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * This class represents a Collection Info file. This class provides methods to read key value pairs from the collection information file.
 */
public class CollectionInfo
{
	private static final Logger LOGGER = LoggerFactory.getLogger(CollectionInfo.class);
	private static final String NEWLINE = System.getProperty("line.separator");

	private final File sourceFile;
	private File filesDir = null;
	private String pid = null;
	
	private final MultivaluedMap<String, String> createCollMap = new MultivaluedMapImpl();
	private final Set<String[]> relationSet = new HashSet<String[]>();  

	/**
	 * Constructor that instantiates a CollectionInfo object for a specified Collection Information file.
	 * 
	 * @param sourceFile
	 *            Collection Info file to read data from
	 * @throws IOException
	 *             when unable to read the collection information file
	 */
	public CollectionInfo(File sourceFile) throws IOException
	{
		if (!sourceFile.isFile())
			throw new IOException(format("{0} is not a file.", sourceFile.getAbsolutePath()));
		if (!sourceFile.canRead())
			throw new IOException(format("Unable to read {0}. Ensure read permission.", sourceFile.getAbsolutePath()));
		if (!sourceFile.canWrite())
			throw new IOException(format("Unable to write {0}. Ensure write permission.", sourceFile.getAbsolutePath()));
		
		this.sourceFile = sourceFile;
		readMap(this.sourceFile);
	}

	/**
	 * Gets the pid as specified in the collection file. If not specified in file, null is returned.
	 * 
	 * @return Pid as String if present in Collection Information file, null otherwise.
	 */
	public String getPid()
	{
		return pid;
	}

	/**
	 * Sets the pid only if it doesn't already exist in the collection.
	 * 
	 * @param pid
	 *            Pid as String
	 * @throws IOException
	 *             when unable to write the Pid in the collection information file.
	 */
	public void setPid(String pid) throws IOException
	{
		if (createCollMap.containsKey("pid"))
		{
			LOGGER.warn("Pid already exists. Leaving Pid unchanged.");
			return;
		}
		
		this.pid = pid;
		createCollMap.add("pid", pid);
		
		// Write pid to collection info file.
		FileWriter writer = null;
		try
		{
			writer = new FileWriter(this.sourceFile, true);
			writer.write(format("{0}pid={1}{0}", NEWLINE, pid));
		}
		finally
		{
			IOUtils.closeQuietly(writer);
		}
	}

	/**
	 * Gets the location of payload files.
	 * 
	 * @return Location of payload files as File
	 */
	public File getFilesDir()
	{
		return filesDir;
	}

	/**
	 * Returns a {@code MultivaluedMap<String, String>} representation of the data in the collection information file.
	 * 
	 * @return MultivaluedMap object
	 */
	public MultivaluedMap<String, String> getCreateCollMap()
	{
		return createCollMap;
	}

	/**
	 * Gets the list of relations specified in the collection file as a {@code Set<String[]>}. For each set element, the String[0] contains relationship type
	 * and String[1] contains the pid of the related record.
	 * 
	 * @return
	 */
	public Set<String[]> getRelationSet()
	{
		return relationSet;
	}

	/**
	 * Reads the contents of the collection information file and parses it to extract data as key-value pairs.
	 * 
	 * @param sourceFile
	 * 
	 * @throws IOException
	 */
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
				String value = line.substring(equalsIndex + 1).trim().replace("\\r\\n", "<br />");
				
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
				
				if (key.equalsIgnoreCase("relation"))
				{
					relationSet.add(value.split(",", 2));
					continue;
				}

				createCollMap.add(key, value);
			}
		}
		finally
		{
			IOUtils.closeQuietly(reader);
			IOUtils.closeQuietly(fis);
		}
	}
}
