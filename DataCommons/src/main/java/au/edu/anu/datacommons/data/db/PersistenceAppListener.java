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

package au.edu.anu.datacommons.data.db;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * PersistenceAppListener
 * 
 * Australian National University Data Commons
 * 
 * A listener class that will close the entity manager factory when the web server is closed
 * 
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		26/04/2012	Genevieve Turner (GT)	Initial
 * </pre>
 * 
 */
public class PersistenceAppListener implements ServletContextListener {
	
	/**
	 * contextDestroyed
	 * 
	 * Called when the servlet is uninitialised
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @see ServletContextListener#contextDestroyed(ServletContextEvent)
	 */
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		PersistenceManager.getInstance().closeEntityManagerFactory();
	}

	/**
	 * contextInitialized
	 * 
	 * Called when the servlet is initialised
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @see ServletContextListener#contextInitialized(ServletContextEvent)
	 */
	@Override
	public void contextInitialized(ServletContextEvent event) {
	}
}
