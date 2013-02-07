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

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;

/**
 * Format
 * 
 * Australian National University Data Commons
 * 
 * Class for the format element in the ANDS RIF-CS schema
 *
 * JUnit Coverage:
 * RegistryObjectsTest
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		04/12/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
public class Format {
	private Identifier identifier;

	/**
	 * getIdentifier
	 *
	 * Get the identifier for the format
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		04/12/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the identifier
	 */
	@Valid
	@NotNull(message="Related Info format requires an identifier")
	@XmlElement(namespace=Constants.ANDS_RIF_CS_NS)
	public Identifier getIdentifier() {
		return identifier;
	}

	/**
	 * setIdentifier
	 *
	 * Set the identifier for the format
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		04/12/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param identifier the identifier to set
	 */
	public void setIdentifier(Identifier identifier) {
		this.identifier = identifier;
	}
}
