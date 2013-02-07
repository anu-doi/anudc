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

import au.edu.anu.datacommons.data.db.model.LinkRelation;

/**
 * LinkRelationDAO
 * 
 * Australian National University Data Commons
 * 
 * Interface for retrieving link relations
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		19/09/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
public interface LinkRelationDAO extends GenericDAO<LinkRelation, Long> {
	/**
	 * getRelations
	 *
	 * Retrieves the relations for the categories
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		19/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param category1 The type of item to retrieve relations for
	 * @param category2 The type of item to relate to
	 * @return A list of relation links
	 */
	public List<LinkRelation> getRelations(String category1, String category2);
}
