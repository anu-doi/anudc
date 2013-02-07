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

package au.edu.anu.datacommons.data.solr;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * SolrAppListener
 * 
 * Australian National University Data Commons
 * 
 * Placeholder
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		19/06/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
public class SolrAppListener implements ServletContextListener {
	/**
	 * contextDestroyed
	 * 
	 * Called when the servlet is uninitialised.  Shuts down the connnections the SolrManager
	 * is currently using.
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		19/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param arg0 The event initiated
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		SolrManager.getInstance().shutdown();
	}

	/**
	 * contextInitialized
	 * 
	 * Called when the servlet is initialised
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		19/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param arg0 The event initiated
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
	}
}
