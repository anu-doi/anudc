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
import javax.xml.bind.annotation.XmlElement;

import au.edu.anu.datacommons.validator.AtLeastOneOf;

/**
 * Address
 * 
 * Australian National University Data Commons
 * 
 * Relates to the address object in the ANDS RIF-CS schema
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		12/10/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
@AtLeastOneOf(fieldNames={"electronicAddresses", "physicalAddresses"}, message="Each location must have an address")
public class Address {
	private List<ElectronicAddress> electronicAddresses;
	private List<PhysicalAddress> physicalAddresses;
	
	/**
	 * Constructor
	 * 
	 * Constructor class that initialises lists
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		05/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 */
	public Address() {
		electronicAddresses = new ArrayList<ElectronicAddress>();
		physicalAddresses = new ArrayList<PhysicalAddress>();
	}

	/**
	 * getElectronicAddresses
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * X.X		12/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the electronicAddresses
	 */
	@Valid
	@XmlElement(name="electronic", namespace=Constants.ANDS_RIF_CS_NS)
	public List<ElectronicAddress> getElectronicAddresses() {
		return electronicAddresses;
	}

	/**
	 * setElectronicAddresses
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * X.X		12/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param electronicAddresses the electronicAddresses to set
	 */
	public void setElectronicAddresses(List<ElectronicAddress> electronicAddresses) {
		this.electronicAddresses = electronicAddresses;
	}

	/**
	 * getPhysicalAddresses
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * X.X		12/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the physicalAddresses
	 */
	@Valid
	@XmlElement(name="physical", namespace=Constants.ANDS_RIF_CS_NS)
	public List<PhysicalAddress> getPhysicalAddresses() {
		return physicalAddresses;
	}

	/**
	 * setPhysicalAddresses
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * X.X		12/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param physicalAddresses the physicalAddresses to set
	 */
	public void setPhysicalAddresses(List<PhysicalAddress> physicalAddresses) {
		this.physicalAddresses = physicalAddresses;
	}

}
