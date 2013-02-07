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

package gov.loc.repository.bagit.v0_95.impl;

import gov.loc.repository.bagit.Bag.BagConstants;
import gov.loc.repository.bagit.BagFile;
import gov.loc.repository.bagit.BagInfoTxt;

public class BagInfoTxtImpl extends gov.loc.repository.bagit.impl.BagInfoTxtImpl implements BagInfoTxt {

	public static final String FIELD_PACKING_DATE = "Packing-Date";
	public static final String FIELD_PACKAGE_SIZE = "Package-Size";
	
	public BagInfoTxtImpl(BagFile bagFile, BagConstants bagConstants) {
		super(bagFile, bagConstants);
	}
	
	public BagInfoTxtImpl(BagConstants bagConstants) {
		super(bagConstants);
			
	}
	
	@Override
	public String getBagSize() {
		return this.getCaseInsensitive(FIELD_PACKAGE_SIZE);
	}

	@Override
	public void setBagSize(String bagSize) {
		this.put(FIELD_PACKAGE_SIZE, bagSize);
	}
	
	@Override
	public String getBaggingDate() {
		return this.getCaseInsensitive(FIELD_PACKING_DATE);
	}
	
	@Override
	public void setBaggingDate(String baggingDate) {
		this.put(FIELD_PACKING_DATE, baggingDate);
	}
	
}
