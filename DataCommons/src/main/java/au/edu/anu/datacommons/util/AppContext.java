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

package au.edu.anu.datacommons.util;

import org.springframework.context.ApplicationContext;

/**
 * AppContext
 * 
 * Australian National University Data Commons
 * 
 * This class makes available the ApplicationContext so that spring beans can be retrieved
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		17/09/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
public class AppContext {
	private static ApplicationContext ctx;
	
	/**
	 * setApplicationContext
	 *
	 * Set the application context
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		17/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param applicationContext The application context to set
	 */
	public static void setApplicationContext(ApplicationContext applicationContext) {
		ctx = applicationContext;
	}
	
	/**
	 * getApplicationContext
	 *
	 * Get the application context
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		17/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return The application context
	 */
	public static ApplicationContext getApplicationContext() {
		return ctx;
	}
}
