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

package au.edu.anu.datacommons.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

import org.apache.tika.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extends the Properties class to provide additional functionality of checking the modification of the underlying properties file before returning the value of
 * a property. If the properties file is updated, the key-value pairs are updated.
 * 
 */
public class PropertiesFile extends Properties
{
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesFile.class);

	private File propFile;
	private long lastRead;

	/**
	 * Constructor for PropertiesFile that specifies the file to read properties from and to monitor for changes.
	 *
	 * @param file File to read properties from and monitor
	 */
	public PropertiesFile(File file) throws IOException
	{
		this.propFile = file;
		refreshProps();
	}

	@Override
	public String getProperty(String key)
	{
		refreshProps();
		return super.getProperty(key);
	}

	@Override
	public String getProperty(String key, String defaultValue)
	{
		refreshProps();
		return super.getProperty(key, defaultValue);
	}

	@Override
	public synchronized boolean containsKey(Object key)
	{
		refreshProps();
		return super.containsKey(key);
	}
	
	/**
	 * Checks if the properties file has been modified. Reloads the properties from the file if modified, exits otherwise.
	 */
	private void refreshProps()
	{
		if (propFile.lastModified() > this.lastRead)
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
			catch (IOException e)
			{
				LOGGER.warn("Unable to read properties file. Returning existing values.");
			}
			finally
			{
				IOUtils.closeQuietly(inStream);
			}
		}
	}
}
