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

package gov.loc.repository.bagit.impl;

import gov.loc.repository.bagit.Bag.BagConstants;
import gov.loc.repository.bagit.BagFile;
import gov.loc.repository.bagit.BagItTxt;
import gov.loc.repository.bagit.utilities.namevalue.impl.AbstractNameValueBagFile;

public class BagItTxtImpl extends AbstractNameValueBagFile implements BagItTxt {

	public static final String VERSION_KEY = "BagIt-Version";
	public static final String CHARACTER_ENCODING_KEY = "Tag-File-Character-Encoding";	
	
	private static final long serialVersionUID = 1L;

	public BagItTxtImpl(BagFile bagFile, BagConstants bagConstants) {
		super(bagConstants.getBagItTxt(), bagFile, bagConstants.getBagEncoding());
	}
	
	public BagItTxtImpl(BagConstants bagConstants) {
		super(bagConstants.getBagItTxt(), bagConstants.getBagEncoding());
		this.put(VERSION_KEY, bagConstants.getVersion().versionString);
		this.put(CHARACTER_ENCODING_KEY, bagConstants.getBagEncoding());
			
	}
	
	public String getCharacterEncoding() {
		return this.get(CHARACTER_ENCODING_KEY);
	}

	public String getVersion() {
		return this.get(VERSION_KEY);
	}

	@Override
	public String getType() {
		return TYPE;
	}
	
}
