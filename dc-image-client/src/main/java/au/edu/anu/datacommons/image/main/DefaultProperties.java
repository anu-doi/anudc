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

package au.edu.anu.datacommons.image.main;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * DefaultProperties
 * 
 * Australian National University Data Commons
 * 
 * Default Properties class
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		12/04/2013	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
public class DefaultProperties {
	private static Properties defaultProperties;
	
	static {
		try {
			InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("default.properties");
			defaultProperties = new Properties();
			defaultProperties.load(is);
		}
		catch(IOException e) {
			System.out.println("Error loading properties file default.properties");
		}
	}
	
	/**
	 * getProperty
	 *
	 * Return the property with the given name
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		12/04/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param property The property to retrieve
	 * @return The property value
	 */
	public static String getProperty(String property) {
		return defaultProperties.getProperty(property);
	}
}
