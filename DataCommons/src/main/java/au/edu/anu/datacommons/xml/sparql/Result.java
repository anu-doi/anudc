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

package au.edu.anu.datacommons.xml.sparql;

import java.util.HashMap;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * Result
 * 
 * Australian National University Data Commons
 * 
 * This details the section that contains information to transform a Sparql Result in
 * to a java class. It contains the fields that have been returned by the sparql query.
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		01/08/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
@XmlJavaTypeAdapter(ResultAdapter.class)
public class Result {
	HashMap<String, ResultItem> fields_;

	public Result() {
		fields_ = new HashMap<String, ResultItem>();
	}
	
	/**
	 * getFields
	 *
	 * Gets the fields returned in the sparql result
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/07/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the fields
	 */
	public HashMap<String, ResultItem> getFields() {
		return fields_;
	}

	/**
	 * setFields
	 *
	 * Sets the fields
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/07/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param fields the fields to set
	 */
	public void setFields(HashMap<String, ResultItem> fields) {
		this.fields_ = fields;
	}
}
