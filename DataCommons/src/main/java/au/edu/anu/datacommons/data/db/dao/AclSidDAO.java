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

import au.edu.anu.datacommons.data.db.model.AclSid;

/**
 * AclSidDAO
 * 
 * Australian National University Data Commons
 * 
 * Data Access Object Interface for the acl_sid table
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		28/08/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
public interface AclSidDAO extends GenericDAO<AclSid, Long>  {
	/**
	 * getAclSidByUsername
	 *
	 * Retreive the acl sid row by the username
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		28/08/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param username Username to retrieve acl sid for
	 * @return The acl sid
	 */
	public AclSid getAclSidByUsername(String username);
}
