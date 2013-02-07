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

package au.edu.anu.datacommons.webservice.bindings;

import java.util.List;
import java.util.Map;

public interface FedoraItem
{
	/**
	 * Generates a map of data values required for processing by Data Commons
	 * 
	 * @return {@code Map<String, List<String>>}
	 */
	public Map<String, List<String>> generateDataMap();
	
	/**
	 * Returns the template used by this item.
	 * 
	 * @return Template value as a Pid of the template record used.
	 */
	public String getTemplate();
	
	/**
	 * Sets the template to be used by this item.
	 * 
	 * @param template
	 *            Pid of the template to be used
	 */
	public void setTemplate(String template);
	
	/**
	 * Gets the pid of this item.
	 * 
	 * @return Pid as String
	 */
	public String getPid();
	
	/**
	 * Sets the pid of this item.
	 * 
	 * @param pid
	 *            Pid as String
	 */
	public void setPid(String pid);
	
	/**
	 * Gets the type of this item - activity, service, party or collection.
	 * 
	 * @return Type as String
	 */
	public String getType();
	
	/**
	 * Gets the owner group of this item.
	 * 
	 * @return Owner Group ID as String
	 */
	public String getOwnerGroup();
	
	/**
	 * Sets the owner group of this item.
	 * 
	 * @param ownerGroup
	 *            Owner Group as String
	 */
	public void setOwnerGroup(String ownerGroup);
}
