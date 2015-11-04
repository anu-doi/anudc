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

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.properties.GlobalProps;
import au.edu.anu.datacommons.util.StopWatch;

/**
 * PersistenceManager
 * 
 * Australian National University Data Commons
 * 
 * A manager class that creates and closes an EntityManagerFactory that can then be used throughout
 * the application
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
public class PersistenceManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(PersistenceManager.class);
	
	private static EntityManagerFactory emf;
	
	/**
	 * getEntityManagerFactory
	 * 
	 * Returns the entity manager factory.
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @return
	 */
	public static EntityManagerFactory getEntityManagerFactory() {
		if (emf == null) {
			createEntityManagerFactory();
		}
		return emf;
	}
	
	/**
	 * closeEntityManagerFactory
	 * 
	 * Closes the entity maanger factory
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 */
	public static void closeEntityManagerFactory() {
		if (emf != null && emf.isOpen()) {
			try {
				emf.close();
			} catch (IllegalStateException e) {
				// No op as the emf is already closed.
			}
		}
	}
	
	/**
	 * createEntityManagerFactory
	 * 
	 * Creates the entity manager factory
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 */
	private static synchronized void createEntityManagerFactory() {
		if (emf == null) {
			Map<String, String> connProps = new HashMap<String, String>();
			
			String jdbcDriver = GlobalProps.getProperty("jdbc.driver");
			String jdbcUrl = GlobalProps.getProperty("jdbc.url");
			String jdbcUser = GlobalProps.getProperty("jdbc.username");
			String jdbcPassword = GlobalProps.getProperty("jdbc.password");
			
			connProps.put("hibernate.connection.driver_class", jdbcDriver);
			connProps.put("hibernate.connection.url", jdbcUrl);
			connProps.put("hibernate.connection.user", jdbcUser);
			connProps.put("hibernate.connection.password", jdbcPassword);
			
			StopWatch sw = new StopWatch();
			sw.start();
			emf = Persistence.createEntityManagerFactory("datacommons", connProps);
			sw.stop();
			LOGGER.debug("Time to create EntityManagerFactory: {}", sw.getTimeElapsedFormatted());
		}
	}
}
