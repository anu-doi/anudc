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

package au.edu.anu.datacommons.xslt.db.dao;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.xslt.db.PersistenceManager;

/**
 * GenericDAOImpl
 * 
 * Australian National University Data Commons
 * 
 * Generic Data Access Object implementation
 * 
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		18/02/2013	Genevieve Turner (GT)	Initial
 * </pre>
 * 
 * @param <T> The object type to implement
 * @param <PK> The primary key type to implement
 */
public class GenericDAOImpl<T, PK extends Serializable> implements GenericDAO<T, PK> {
	static final Logger LOGGER = LoggerFactory.getLogger(GenericDAOImpl.class);
	
	private Class<T> type_;
	
	/**
	 * Constructor
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		18/02/2013	Genevieve Turner (GT)	Included so the actual type can be referenced
	 * </pre>
	 * 
	 * @param type The class type to retrive/set objects
	 */
	public GenericDAOImpl(Class<T> type) {
		// Attempting to have a default constructor that uses ParameterizedType causes errors
		// So we need to have a constructor that sets the class type
		this.type_ = type;
	}
	
	/**
	 * create
	 * 
	 * Creates the object in the database
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		18/02/2013	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param t The object to create in the database
	 * @return
	 */
	public T create(T o) {
		EntityManager entityManager = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
		EntityTransaction entityTransaction = entityManager.getTransaction();
		try {
			entityTransaction.begin();
			entityManager.persist(o);
			entityTransaction.commit();
		}
		finally {
			entityManager.close();
		}
		return o;
	}

	/**
	 * getSingleById
	 * 
	 * Retrieves the object by the primary key
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		18/02/2013	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param id The primary key of the object to retrieve
	 * @return
	 */
	public T getSingleById(PK id) {
		EntityManager entityManager = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
		T object = null;
		try {
			object = (T) entityManager.find(type_, id);
		}
		finally {
			entityManager.close();
		}
		return object;
	}

	/**
	 * getAll
	 * 
	 * Retrieves all objects of the approriate type
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		18/02/2013	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @return A list of objects of the appropriate type
	 */
	public List<T> getAll() {
		EntityManager entityManager = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
		List<T> objects = null;
		try {
			objects = entityManager.createQuery("from " + type_.getName()).getResultList();
		}
		finally {
			entityManager.close();
		}
		return objects;
	}

	/**
	 * update
	 * 
	 * Updates the object in the database
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		18/02/2013	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param t The object to update
	 * @return
	 */
	public T update(T o) {
		EntityManager entityManager = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
		EntityTransaction entityTransaction = entityManager.getTransaction();
		try {
			entityTransaction.begin();
			o = entityManager.merge(o);
			entityTransaction.commit();
		}
		finally {
			entityManager.close();
		}
		return o;
	}

	/**
	 * delete
	 * 
	 * Removes objects from the database
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		18/02/2013	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param t The object to delete
	 */
	public void delete (PK id) {
		EntityManager entityManager = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
		EntityTransaction entityTransaction = entityManager.getTransaction();
		try {
			entityTransaction.begin();
			entityManager.remove(entityManager.getReference(type_, id));
			entityTransaction.commit();
		}
		finally {
			entityManager.close();
		}
	}
}
