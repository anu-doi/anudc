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

/**
 * AclObjectIdentityDAO
 * 
 * Australian National University Data Commons
 * 
 * AclObjectIdentity interface extension
 * 
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		03/05/2012	Genevieve Turner (GT)	Initial
 * </pre>
 * 
 * @param <T> The object type to implement
 * @param <PK> The primary key type to implement
 */
public interface AclObjectIdentityDAO<T, PK> extends GenericDAO {
	
	/**
	 * Retreives an object based on the provided class, and identity values
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param aclClass The id of the class to retrieve
	 * @param aclIdentity The id of the object to retrieve a row for
	 * @return Returns the retrieved object
	 */
	public T getObjectByClassAndIdentity(Long aclClass, Long aclIdentity);
}
