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

import au.edu.anu.datacommons.collectionrequest.CollectionDropbox;
import au.edu.anu.datacommons.data.db.model.Groups;
import au.edu.anu.datacommons.data.db.model.Users;

/**
 * DropboxDAO
 * 
 * Australian National University Data Commons
 * 
 * Interface for retrieving and updating the CollectionDropbox class
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		29/06/2012	Genevieve Turner (GT)	Initial
 * 0.2		19/07/2012	Genevieve Turner (GT)	Added a function to get dropboxes for the specified user
 * </pre>
 *
 */
public interface DropboxDAO extends GenericDAO<CollectionDropbox, Long> {
	/**
	 * getPermittedRequests
	 *
	 * Gets a list of collection dropbox that the user is permitted to view 
	 * (determined by the listed groups)
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		29/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param groups A list of groups for which the user has permissions to view the dropbox
	 * @return A list of CollectionDropboxes based on the 
	 */
	public List<CollectionDropbox> getPermittedRequests(List<Groups> groups);
	
	/**
	 * getSingleByIdEager
	 *
	 * Retrieves the CollectionDropbox eagerly.
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		29/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param id The id of the dropbox to retreive
	 * @return The collection dropbox with the given id
	 */
	public CollectionDropbox getSingleByIdEager(Long id);
	
	/**
	 * getSingleByAccessCode
	 *
	 * Retrieves the CollectionDropbox by the access code
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		29/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param accessCode The access code of the dropbox that is being retrieved
	 * @return The collection dropbox
	 */
	public CollectionDropbox getSingleByAccessCode(Long accessCode);
	
	/**
	 * getUserDropboxes
	 *
	 * Retrieves a list of collection dropboxes for the user
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		19/07/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param user The user to retrieve a list of dropboxes for
	 * @return Returns a list of dropboxes for the given user
	 */
	public List<CollectionDropbox> getUserDropboxes(Users user);
}
