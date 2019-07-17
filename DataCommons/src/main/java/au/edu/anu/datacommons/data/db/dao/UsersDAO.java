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

import au.edu.anu.datacommons.data.db.model.Users;

/**
 * UsersDAO
 * 
 * Australian National University Data Commons
 * 
 * Class to perform actions with users in the database
 * 
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		16/05/2012	Genevieve Turner (GT)	Initial
 * </pre>
 * 
 */
public interface UsersDAO extends GenericDAO<Users, Long> {
	/**
	 * getUserByName
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		16/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param The username of the object to return
	 * @return The object with the username
	 */
	public Users getUserByName(String username);
	
	public List<Users> findRegisteredUsers(String givenName, String surname, String email);
}
