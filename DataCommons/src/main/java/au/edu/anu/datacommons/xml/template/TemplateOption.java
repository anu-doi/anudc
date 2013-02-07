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

package au.edu.anu.datacommons.xml.template;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * TemplateOption
 * 
 * Australian National University Data Commons
 * 
 * The TemplateOption class is utilised for marshalling and unmarshalling JAXB objects with the
 * template root element.
 * 
 * JUnit coverage:
 * JAXBTransformTest
 * 
 * Version	Date		Developer			Description
 * 0.1		19/03/2012	Genevieve Turner	Initial build
 * 
 */
public class TemplateOption {
	private String label;
	private String value;

	/**
	 * getLabel
	 * 
	 * Returns the label attribute of the option
	 * 
	 * Version	Date		Developer			Description
	 * 0.1		13/03/2012	Genevieve Turner	Initial build
	 * 
	 * @return The label of the option
	 */
	@XmlAttribute
	public String getLabel() {
		return label;
	}
	
	/**
	 * setLabel
	 * 
	 * Sets the label attribute of the option
	 * 
	 * Version	Date		Developer			Description
	 * 0.1		13/03/2012	Genevieve Turner	Initial build
	 * 
	 * @param label The label of the option
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * getValue
	 * 
	 * Returns the value attribute of the item
	 * 
	 * Version	Date		Developer			Description
	 * 0.1		13/03/2012	Genevieve Turner	Initial build
	 * 
	 * @return The value of the option
	 */
	@XmlAttribute
	public String getValue() {
		return value;
	}

	/**
	 * setValue
	 * 
	 * Sets the value attribute of the option
	 * 
	 * Version	Date		Developer			Description
	 * 0.1		13/03/2012	Genevieve Turner	Initial build
	 * 
	 * @param value The value of the option
	 */
	public void setValue(String value) {
		this.value = value;
	}
}
