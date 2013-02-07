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

package au.edu.anu.datacommons.ands.xml;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * RegistryObjects
 * 
 * Australian National University Data Commons
 * 
 * Class for the registryObjects element in the ANDS RIF-CS schema
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		15/10/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
@XmlRootElement(name="registryObjects")
public class RegistryObjects {
	private List<RegistryObject> registryObjects;
	
	/**
	 * Constructor
	 * 
	 * Constructor for the RegistryObjects class
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		15/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 *
	 */
	public RegistryObjects() {
		registryObjects = new ArrayList<RegistryObject>();
	}

	/**
	 * getRegistryObjects
	 *
	 * Get the registry objects
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the registryObjects
	 */
	@Size(min=1, message="Quality Level 1 - At least one registry object is required")
	@Valid
	@XmlElement(name="registryObject", namespace=Constants.ANDS_RIF_CS_NS)
	public List<RegistryObject> getRegistryObjects() {
		return registryObjects;
	}

	/**
	 * setRegistryObjects
	 *
	 * Set the registry objects
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param registryObjects the registryObjects to set
	 */
	public void setRegistryObjects(List<RegistryObject> registryObjects) {
		this.registryObjects = registryObjects;
	}
}
