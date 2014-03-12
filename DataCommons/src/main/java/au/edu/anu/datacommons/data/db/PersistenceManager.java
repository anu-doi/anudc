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

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	
	private static final PersistenceManager singleton_ = new PersistenceManager();
	
	private EntityManagerFactory emf;
	
	/**
	 * getInstance
	 * 
	 * Returns an instance of the object
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @return Returns the PersistenceManager
	 */
	public static PersistenceManager getInstance() {
		return singleton_;
	}
	
	/**
	 * Constructor class
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 */
	private PersistenceManager() {
	}
	
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
	public EntityManagerFactory getEntityManagerFactory() {
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
	public void closeEntityManagerFactory() {
		if (emf != null) {
			try {
				emf.close();
				emf = null;
				LOGGER.info("Persistence finished at " + new java.util.Date());
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
	protected synchronized void createEntityManagerFactory() {
		if (this.emf == null) {
			this.emf = Persistence.createEntityManagerFactory("datacommons");
			LOGGER.info("Persistence started at " + new java.util.Date());
		}
	}
}
