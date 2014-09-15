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
package au.edu.anu.datacommons.embargo;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * EmbargoAppListener
 *
 * Australian National University Data Commons
 * 
 * Servlet Context Listener for Embargo functions.
 *
 * JUnit coverage:
 * None
 * 
 * @author Genevieve Turner
 *
 */
public class EmbargoAppListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		EmbargoManager.getInstance().shutdown();
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		EmbargoManager.getInstance().start();
	}

}
