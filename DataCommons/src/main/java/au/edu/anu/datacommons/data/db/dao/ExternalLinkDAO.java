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

import au.edu.anu.datacommons.data.db.model.ExternalLinkPattern;

/**
 * ExternalLinkDAO
 * 
 * Australian National University Data Commons
 * 
 * Data Access Object Implementation for the ExternalLinkPattern class
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		20/11/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
public interface ExternalLinkDAO extends GenericDAO<ExternalLinkPattern, Long> {
	/**
	 * getByObjectType
	 *
	 * Retrieve the patterns associated with the given object type
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		20/11/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param objectType The object type
	 * @return The patterns associated with the object type
	 */
	public List<ExternalLinkPattern> getByObjectType(String objectType);
	
	/**
	 * Find patterns that match the reference.
	 * 
	 * @param reference The value to find pattersn for
	 * @return A list of matching patterns
	 */
	public List<ExternalLinkPattern> findByReference(String reference);
}
