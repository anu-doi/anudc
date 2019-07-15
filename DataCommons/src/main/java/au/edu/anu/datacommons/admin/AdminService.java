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
package au.edu.anu.datacommons.admin;

import java.util.List;

import au.edu.anu.datacommons.data.db.model.Domains;
import au.edu.anu.datacommons.data.db.model.Groups;

/**
 * AdminService
 *
 * Australian National University Data Commons
 * 
 * Service Interface for administrative actions
 *
 * JUnit coverage:
 * None
 * 
 * @author Genevieve Turner
 *
 */
public interface AdminService {
	/**
	 * Get all the domains
	 * 
	 * @return The domains
	 */
	public List<Domains> getDomains();
	
	/**
	 * Create a domain
	 * 
	 * @param domainName The name of the domain to create
	 */
	public void createOrEditDomain(Long domainId, String domainName);
	
	/**
	 * Get all the groups
	 * 
	 * @return The groups
	 */
	public List<Groups> getGroups();
	
	/**
	 * Create a group
	 * 
	 * @param groupName The name of the group to create
	 * @param domainId The id of the domain to create
	 */
	public void createOrEditGroup(Long groupId, String groupName, Long domainId);
}
