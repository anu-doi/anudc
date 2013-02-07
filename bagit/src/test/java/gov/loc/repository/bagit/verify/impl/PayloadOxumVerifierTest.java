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

import java.io.File;

import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFactory;
import gov.loc.repository.bagit.utilities.ResourceHelper;
import gov.loc.repository.bagit.utilities.SimpleResult;
import org.junit.Test;

public class PayloadOxumVerifierTest {
    
	PayloadOxumVerifier verifier = new PayloadOxumVerifier();
    
    BagFactory bagFactory = new BagFactory();
    
    
    @Test
    public void testVerifyPayloadOxum() throws Exception {
    	Bag testBag = this.getBag();
    	try {
    		//No payload-oxum return true
    		SimpleResult result = verifier.verify(testBag);
    		assertTrue(result.isSuccess());
    		assertEquals(1, result.getWarningMessages().size());
    		
    		//Correct payload-oxum
    		testBag.getBagInfoTxt().setPayloadOxum("25.5");
    		result = verifier.verify(testBag);
    		assertTrue(result.isSuccess());
    		
    		//Incorrect payload-oxum
    		testBag.getBagInfoTxt().setPayloadOxum("25.4");
    		result = verifier.verify(testBag);
    		assertFalse(result.isSuccess());
    		assertTrue(result.hasSimpleMessage(PayloadOxumVerifier.CODE_INCORRECT_PAYLOAD_OXUM));
    	} finally {
    		testBag.close();
    	}
    	
    }
    
    private Bag getBag() throws Exception {
        File bagDir = ResourceHelper.getFile("bags/v0_96/bag"); 
    	return this.bagFactory.createBag(bagDir);  
    }   
    
}
