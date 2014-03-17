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

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * UserExtra
 *
 * Australian National University Data Commons
 * 
 * Base class for extra user information
 *
 * JUnit coverage:
 * None
 * 
 * @author Genevieve Turner
 *
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class UserExtra {
	private Long id;
	private Users user;
	
	/**
	 * Get the id
	 * 
	 * @return The id
	 */
	@Id
	@GeneratedValue(generator = "user-pk")
	@GenericGenerator(name = "user-pk", strategy = "foreign", parameters = { @Parameter(name = "property", value = "user") })
	public Long getId() {
		return id;
	}
	
	/**
	 * Set the id
	 * 
	 * @param id THe id
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Get the user object
	 * 
	 * @return The user
	 */
	@OneToOne(cascade = CascadeType.ALL, mappedBy = "")
	@PrimaryKeyJoinColumn
	public Users getUser() {
		return user;
	}

	/**
	 * Set the user object
	 * 
	 * @param user The user
	 */
	public void setUser(Users user) {
		this.user = user;
	}
}
