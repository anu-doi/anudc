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

package au.edu.anu.datacommons.data.db.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Authorities
 * 
 * Australian National University Data Commons
 * 
 * Class used to update authorities.
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		21/08/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
@Entity
@Table(name="authorities")
public class Authorities {
	private AuthoritiesPK id_;

	/**
	 * getId
	 * 
	 * Get the id
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		24/12/2013	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @return The id
	 */
	@Id
	public AuthoritiesPK getId() {
		return id_;
	}

	/**
	 * setId
	 * 
	 * Set the id
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		24/12/2013	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param id The id
	 */
	public void setId(AuthoritiesPK id) {
		this.id_ = id;
	}
}
