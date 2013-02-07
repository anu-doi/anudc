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

package gov.loc.repository.bagit.verify.impl;

import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagInfoTxt;
import gov.loc.repository.bagit.utilities.SimpleResult;
import gov.loc.repository.bagit.verify.Verifier;

public class RequiredBagInfoTxtFieldsVerifier implements Verifier {

	public static final String CODE_MISSING_BAGINFOTXT = "missing_baginfotxt";
	public static final String CODE_MISSING_REQUIRED_FIELD = "missing_required_field";
	
	private String[] requiredFields;
	
	public RequiredBagInfoTxtFieldsVerifier(String[] requiredFields) {
		this.requiredFields = requiredFields;
	}
	
	public SimpleResult verify(Bag bag) {
		SimpleResult result = new SimpleResult(true);
		
		BagInfoTxt bagInfo = bag.getBagInfoTxt();
		if (bagInfo == null) {
			result.setSuccess(false);
			result.addMessage(CODE_MISSING_BAGINFOTXT, "Bag-info.txt is missing");
		} else {
			for(String field : this.requiredFields) {
				String value = bagInfo.get(field);
				if (value == null || value.length() == 0) {
					result.setSuccess(false);
					result.addMessage(CODE_MISSING_REQUIRED_FIELD, "Required field {0} is not provided.", field);
				}
			}
		}
		
		return result;
		
	}

}
