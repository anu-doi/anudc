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

import au.edu.anu.datacommons.data.db.model.FedoraObject;

/**
 * FedoraObjectDAO
 * 
 * Interface for retrieving fedora objects.
 * 
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		26/04/2012	Genevieve Turner (GT)	Initial
 * 0.2		14/05/2012	Genevieve Turner (GT)	Updated extensions
 * 0.3		25/07/2012	Genevieve Turner (GT)	Updated for review processing
 * </pre>
 * 
 * @param <T> The type of the object that is instantiated
 * @param <PK> The type of the primary key that is instantiated
 */
public interface FedoraObjectDAO extends GenericDAO<FedoraObject, Long> {
	/**
	 * getSingleByName
	 * 
	 * Retrieves an object with the given name
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		29/03/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param name The name of the object to retrieve
	 */
	FedoraObject getSingleByName(String name);
	
	/**
	 * getAllReadyForReview
	 *
	 * Gets all objects that are ready for review
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * X.X		25/07/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return A list of objects ready for review
	 */
	List<FedoraObject> getAllReadyForReview();
	
	/**
	 * getAllReadyForPublish
	 *
	 * Gets all objects that are ready for publish
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * X.X		25/07/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return A list of objects ready for publish
	 */
	List<FedoraObject> getAllReadyForPublish();
	
	/**
	 * getAllRejected
	 *
	 * Gets all objects that have been rejected
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * X.X		25/07/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return A list of rejected objects
	 */
	List<FedoraObject> getAllRejected();
}
