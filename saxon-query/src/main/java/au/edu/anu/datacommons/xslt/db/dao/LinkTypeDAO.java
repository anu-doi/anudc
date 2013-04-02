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

import au.edu.anu.datacommons.xslt.db.model.LinkType;

/**
 * LinkTypeDAO
 * 
 * Australian National University Data Commons
 * 
 * Data Access Object Interface for the link_type table
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		18/02/2013	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
public interface LinkTypeDAO extends GenericDAO<LinkType, Long> {
	/**
	 * getByCode
	 *
	 * Get the link type by the code
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		18/02/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param code The code to retrieve
	 * @return The link type associated with the code
	 */
	public LinkType getByCode(String type);
}
