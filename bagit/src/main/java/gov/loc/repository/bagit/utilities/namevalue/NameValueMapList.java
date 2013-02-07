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

package gov.loc.repository.bagit.utilities.namevalue;

import gov.loc.repository.bagit.utilities.namevalue.NameValueReader.NameValue;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface NameValueMapList extends Map<String, String>, Iterable<NameValue> {
	/*
	 * Appends name-values.
	 * Any exists name-values with the same key are not touched.
	 */
	void putList(String key, Collection<String> values);

	/*
	 * Appends name-values.
	 * Any exists name-values with the same key are not touched.
	 */
	void putListAll(Collection<NameValue> nameValues);
	
	/*
	 * Appends name-value.
	 * Any exists name-values with the same key are not touched.
	 */	
	void putList(NameValue nameValue);
	
	/*
	 * Appends name-value.
	 * Any exists name-values with the same key are not touched.
	 */		
	void putList(String key, String value);
	
	/*
	 * Same semantics as List.remove()
	 */
	boolean removeList(String key, String value);
	
	/*
	 * Same semantics as List.remove()
	 */
	boolean removeList(NameValue nameValue);
	
	boolean removeAllList(String key);
	
	List<String> getList(String key);
	
	/*
	 * Same semantics as Map.put()
	 */
	String put(NameValue nameValue);
	List<NameValue> asList();
	int sizeList();
}
