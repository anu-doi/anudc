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

package gov.loc.repository.bagit.v0_96;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Test;

import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.Manifest;
import gov.loc.repository.bagit.Bag.Format;
import gov.loc.repository.bagit.BagFactory.Version;
import gov.loc.repository.bagit.Manifest.Algorithm;
import gov.loc.repository.bagit.impl.BaseBagImplTest;

public class BagWithEncodedNamesTest extends BaseBagImplTest {

	@Override
	public Version getVersion() {
		return Version.V0_96;
	}
	
	@Override
	protected String getBagName() {
		return "bag-with-encoded-names";
	};
		
	@Test
	public void testFileSystemBagByPayloadManifests() throws Exception {
		this.testBag(this.getBagByPayloadManifests(Format.FILESYSTEM));
	}

	@Test
	public void testFileSystemBagByPayloadFiles() throws Exception {
		this.testBag(this.getBagByPayloadFiles(Format.FILESYSTEM));
	}

	@Override
	protected void assertBagPayloadManifests(Bag bag) throws Exception
	{
		List<Manifest> payloadManifests = bag.getPayloadManifests();
		assertEquals(1, payloadManifests.size());
		assertEquals("manifest-md5.txt", payloadManifests.get(0).getFilepath());
		assertEquals("manifest-md5.txt", bag.getPayloadManifest(Algorithm.MD5).getFilepath());
		assertNull(bag.getPayloadManifest(Algorithm.SHA1));
		assertEquals("ad0234829205b9033196ba818f7a872b", bag.getChecksums("data/%test2.txt").get(Algorithm.MD5));
	}
	
	protected void assertBagPayloadFiles(Bag bag) throws Exception
	{
		assertEquals(5, bag.getPayload().size());
		assertNotNull(bag.getBagFile("data/dir1/~test3.txt"));
		assertNull(bag.getBagFile("xdata/dir1/test3.txt"));
	}



}
