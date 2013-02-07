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

import java.text.MessageFormat;

import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagHelper;
import gov.loc.repository.bagit.utilities.SimpleResult;
import gov.loc.repository.bagit.verify.Verifier;

public class PayloadOxumVerifier implements Verifier {

	public static final String CODE_INCORRECT_PAYLOAD_OXUM = "incorrect_payload-oxum";
	
	@Override
	public SimpleResult verify(Bag bag) {
		SimpleResult result = new SimpleResult(true);
		if (bag.getBagInfoTxt() == null) {
			result.addWarningMessage("Bag does not have a bag-info.txt");
			return result;
		}
		String checkOxum = bag.getBagInfoTxt().getPayloadOxum();
		if (checkOxum == null) {
			result.addWarningMessage("Bag-info.txt does not have a Payload-Oxum field");
			return result;
		}

		String genOxum = BagHelper.generatePayloadOxum(bag);
		if (! checkOxum.equals(genOxum)) {
			result.setSuccess(false);
			result.addMessage(CODE_INCORRECT_PAYLOAD_OXUM, 
					MessageFormat.format("Expected payload-oxum {0}, but found payload-oxum {1}", checkOxum, genOxum));
		}
		return result;
	}

}
