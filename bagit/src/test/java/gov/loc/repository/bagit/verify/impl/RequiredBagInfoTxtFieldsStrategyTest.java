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

import static org.junit.Assert.*;

import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFactory;
import gov.loc.repository.bagit.BagInfoTxt;
import gov.loc.repository.bagit.impl.BagInfoTxtImpl;
import gov.loc.repository.bagit.verify.Verifier;
import gov.loc.repository.bagit.verify.impl.RequiredBagInfoTxtFieldsVerifier;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class RequiredBagInfoTxtFieldsStrategyTest {

	BagFactory bagFactory = new BagFactory();
	
	@Test
	public void testVerify() {
		Verifier strategy = new RequiredBagInfoTxtFieldsVerifier(new String[] {BagInfoTxtImpl.FIELD_BAG_COUNT, BagInfoTxtImpl.FIELD_BAG_GROUP_IDENTIFIER});
		
		Bag bag = bagFactory.createBag();
		try {
			BagInfoTxt bagInfo = bag.getBagPartFactory().createBagInfoTxt();
			assertFalse(bag.verify(strategy).isSuccess());
			
			bag.putBagFile(bagInfo);
			assertFalse(bag.verify(strategy).isSuccess());
			bagInfo.setBagCount("1 of 2");
			assertFalse(bag.verify(strategy).isSuccess());
			bagInfo.setBagGroupIdentifier("foo");
			assertTrue(bag.verify(strategy).isSuccess());
			bagInfo.setBagSize("45 gb");
			assertTrue(bag.verify(strategy).isSuccess());
		} finally {
			IOUtils.closeQuietly(bag);
		}
		
	}

}
