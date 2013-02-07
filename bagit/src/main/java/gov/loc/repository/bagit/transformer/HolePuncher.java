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

package gov.loc.repository.bagit.transformer;

import gov.loc.repository.bagit.Bag;

public interface HolePuncher {
	/*
	 * Make the bag holey.
	 * The involves creating a fetch.txt and removing the payload
	 * @param	bag the bag to make holey 
	 * @param	baseUrl	the url part to prepend to create the payload url
	 * @param	whether to include the payload directory ("data") in the payload url
	 * @param	whether to include the tags in the fetch.txt.  If true then includePayloadDirectory will be true.
	 * @param	whether to leave the tags in the returned bag.
	 * @return	the newly holey bag
	 */
	Bag makeHoley(Bag bag, String baseUrl, boolean includePayloadDirectoryInUrl, boolean includeTags, boolean leaveTags, boolean resume);
	
	Bag makeHoley(Bag bag, String baseUrl, boolean includePayloadDirectoryInUrl, boolean includeTags, boolean resume);

}
