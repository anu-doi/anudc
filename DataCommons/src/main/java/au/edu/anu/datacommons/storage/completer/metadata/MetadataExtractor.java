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

package au.edu.anu.datacommons.storage.completer.metadata;

import java.util.Map;

import org.apache.tika.metadata.Metadata;

/**
 * 
 * @author Rahul Khanna
 *
 */
public interface MetadataExtractor {
	/**
	 * Get a multivalued map containing the metadata extracted from a file.
	 * 
	 * @return
	 * Metadata as Map<String, String[]>
	 */
	public Map<String, String[]> getMetadataMap();

	/**
	 * Get the metadata object containing the metadata extracted from a file.
	 * 
	 * @return Metadata
	 */
	public Metadata getMetadata();
}
