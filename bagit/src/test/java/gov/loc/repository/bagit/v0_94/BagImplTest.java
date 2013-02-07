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

package gov.loc.repository.bagit.v0_94;

import static org.junit.Assert.*;

import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagInfoTxt;
import gov.loc.repository.bagit.BagFactory.Version;
import gov.loc.repository.bagit.impl.AbstractBagImplTest;

public class BagImplTest extends AbstractBagImplTest {

	@Override
	public Version getVersion() {
		return Version.V0_94;
	}

	@Override
	public void performAddlTestCreateBag(Bag bag) {
		BagInfoTxt bagInfo = bag.getBagInfoTxt();
		assertEquals(bagInfo.getBagSize(), bagInfo.get(gov.loc.repository.bagit.v0_94.impl.BagInfoTxtImpl.FIELD_PACKAGE_SIZE));
		assertEquals(bagInfo.getBaggingDate(), bagInfo.get(gov.loc.repository.bagit.v0_94.impl.BagInfoTxtImpl.FIELD_PACKING_DATE));

	}
	
	@Override
	public void performTestBagWithTagDirectory(Bag bag) {
		performTestBagWithTagDirectoryPrev97(bag);		
	}
	
	@Override
	public void performTestBagWithIgnoredTagDirectory(Bag bag) {
		performTestBagWithIgnoredTagDirectoryPrev97(bag);		
	}

}
