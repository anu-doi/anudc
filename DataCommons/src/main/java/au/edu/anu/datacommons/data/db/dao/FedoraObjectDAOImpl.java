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

package au.edu.anu.datacommons.data.db.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.data.db.PersistenceManager;
import au.edu.anu.datacommons.data.db.model.FedoraObject;

/**
 * FedoraObjectDAO
 * 
 * Australian National University Data Commons
 * 
 * Class to perform actions with fedora objects in the database
 * 
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		26/04/2012	Genevieve Turner (GT)	Initial
 * 0.2		03/05/2012	Genevieve Turner (GT)	Updated to include constructor class that sets the type in GenericDAOImpl
 * 0.3		25/07/2012	Genevieve Turner (GT)	Updated for review processing
 * </pre>
 * 
 */
public class FedoraObjectDAOImpl extends GenericDAOImpl<FedoraObject, Long> implements FedoraObjectDAO
{
	static final Logger LOGGER = LoggerFactory.getLogger(FedoraObjectDAOImpl.class);
	
	/**
	 * Constructor
	 * 
	 * Constructor class that includes the type
	 * 
	 * @param type The class type to retrive/set objects
	 */
	public FedoraObjectDAOImpl(Class<FedoraObject> type) {
		super(type);
	}
	
	/**
	 * getSingleByName
	 * 
	 * Retrieves a fedora object by the pid
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/04/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param name The pid of the object to return
	 * @return The Object returned from the query.
	 */
	public FedoraObject getSingleByName(String name) {
		EntityManager entityManager = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
		FedoraObject fedoraObject = null;
		try {
			Query query = entityManager.createQuery("from FedoraObject where object_id = :pid");
			query.setParameter("pid", name);
			fedoraObject = (FedoraObject) query.getSingleResult();
		}
		catch (NoResultException e) {
			LOGGER.warn("No entity found for pid {}", name);
		}
		finally {
			entityManager.close();
		}
		return fedoraObject;
	}
	
	/**
	 * getAllReadyForReview
	 * 
	 * Gets all objects that are ready for review
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.3		25/07/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return A list of objects ready for review
	 * @see au.edu.anu.datacommons.data.db.dao.FedoraObjectDAO#getAllReadyForReview()
	 */
	public List<FedoraObject> getAllReadyForReview() {
		EntityManager entityManager = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
		List<FedoraObject> fedoraObjects = null;
		
		try {
			fedoraObjects = entityManager.createQuery("SELECT fo FROM FedoraObject fo join fo.reviewReady rr").getResultList();
		}
		finally {
			entityManager.close();
		}
		
		return fedoraObjects;
	}
	
	/**
	 * getAllReadyForPublish
	 * 
	 * Gets all objects that are ready for publish
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.3		25/07/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return A list of objects ready for publish
	 * @see au.edu.anu.datacommons.data.db.dao.FedoraObjectDAO#getAllReadyForPublish()
	 */
	public List<FedoraObject> getAllReadyForPublish() {
		EntityManager entityManager = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
		List<FedoraObject> fedoraObjects = null;
		
		try {
			fedoraObjects = entityManager.createQuery("SELECT fo FROM FedoraObject fo join fo.publishReady pr").getResultList();
		}
		finally {
			entityManager.close();
		}
		
		return fedoraObjects;
	}
	
	/**
	 * getAllRejected
	 * 
	 * Gets all objects that have been rejected
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.3		25/07/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return A list of rejected objects
	 * @see au.edu.anu.datacommons.data.db.dao.FedoraObjectDAO#getAllRejected()
	 */
	public List<FedoraObject> getAllRejected() {
		EntityManager entityManager = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
		List<FedoraObject> fedoraObjects = null;
		
		try {
			fedoraObjects = entityManager.createQuery("SELECT fo FROM FedoraObject fo join fo.reviewReject rr").getResultList();
		}
		finally {
			entityManager.close();
		}
		
		return fedoraObjects;
	}
}
