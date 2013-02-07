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

/**
 * Provides configuration and environment information:
 * 
 * <ul>
 * <li>Directory where configuration files are location</li>
 * <li>New Line String specific to the platform on which the JVM is running</li>
 * <li>Character Set constant</li>
 * <ul>
 */
public class Config
{
	/**
	 * Location of configuration files. Individual projects may store configuration within a subdirectory within the returned subdirectory. Returns C:\AnuDc if
	 * on Windows, /etc/anudc on *nix.
	 */
	public static final File DIR;
	
	/**
	 * Returns the value of system property line.separator
	 */
	public static final String NEWLINE = System.getProperty("line.separator");

	/**
	 * Returns the default character set to be used.
	 */
	public static final String CHARSET = "UTF-8";

	static
	{
		String osName = System.getProperty("os.name");
		if (osName.toLowerCase().startsWith("windows"))
			DIR = new File("C:\\AnuDc");
		else
			DIR = new File("/etc/anudc");

		if (!DIR.exists())
			DIR.mkdirs();
	}
}
