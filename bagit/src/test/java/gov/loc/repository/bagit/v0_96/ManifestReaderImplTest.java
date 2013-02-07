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

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;

import gov.loc.repository.bagit.BagFactory;
import gov.loc.repository.bagit.ManifestReader;
import gov.loc.repository.bagit.Bag.BagPartFactory;
import gov.loc.repository.bagit.BagFactory.Version;
import gov.loc.repository.bagit.ManifestReader.FilenameFixity;
import gov.loc.repository.bagit.impl.AbstractManifestReaderImplTest;

import org.junit.Test;


public class ManifestReaderImplTest extends AbstractManifestReaderImplTest{

	BagFactory bagFactory = new BagFactory();
	
	@Test
	public void test() throws Exception {
		BagPartFactory factory = this.bagFactory.getBagPartFactory(Version.V0_96);
		String manifest = 
			"8ad8757baa8564dc136c1e07507f4a98 data/test1.txt\n" +
			"8ad8757baa8564dc136c1e07507f4a98	data/test3.txt\n" +
			"8ad8757baa8564dc136c1e07507f4a98	data/test 4.txt\n" +
			"8ad8757baa8564dc136c1e07507f4a98  data/test5.txt\n" +
			"8ad8757baa8564dc136c1e07507f4a98 *data/test6.txt\n" +
			"8ad8757baa8564dc136c1e07507f4a98__data/test7.txt\n";
		
		ManifestReader reader = factory.createManifestReader(new ByteArrayInputStream(manifest.getBytes("utf-8")), "utf-8");
		FilenameFixity ff = reader.next();
		assertEquals("8ad8757baa8564dc136c1e07507f4a98", ff.getFixityValue());
		assertEquals("data/test1.txt", ff.getFilename());
				
		ff = reader.next();
		assertEquals("8ad8757baa8564dc136c1e07507f4a98", ff.getFixityValue());
		assertEquals("data/test3.txt", ff.getFilename());
		
		ff = reader.next();
		assertEquals("8ad8757baa8564dc136c1e07507f4a98", ff.getFixityValue());
		assertEquals("data/test 4.txt", ff.getFilename());
		
		ff = reader.next();
		assertEquals("8ad8757baa8564dc136c1e07507f4a98", ff.getFixityValue());
		assertEquals("data/test5.txt", ff.getFilename());

		ff = reader.next();
		assertEquals("8ad8757baa8564dc136c1e07507f4a98", ff.getFixityValue());
		assertEquals("data/test6.txt", ff.getFilename());
		
		//Skip bad lines
		assertFalse(reader.hasNext());
		
		reader.close();

		
	}
	
	@Override
	public boolean canReadDoubleSpaceWithUnixSep() {
		return true;
	}

	@Override
	public boolean canReadSingleSpaceWithUnixSep() {
		return true;
	}
		
	@Override
	public void testSingleSpaceWithWindowsSep() throws Exception {
		//Reading \ as a file char, not a path separator
		boolean isException = false;
		try {
			assertEquals(true, this.canReadLine("8ad8757baa8564dc136c1e07507f4a98 data\\test1.txt\n", "8ad8757baa8564dc136c1e07507f4a98", "data\\test1.txt"));
		} catch(RuntimeException ex) {
			isException = true;
		}
		assertTrue(isException);

		//Reading / as a path separator
		assertEquals(true, this.canReadLine("8ad8757baa8564dc136c1e07507f4a98 data\\test1.txt\n", "8ad8757baa8564dc136c1e07507f4a98", "data/test1.txt", true));		
	}
	
	@Override
	public boolean canReadSpaceAstericksWithUnixSep() {
		return true;
	}

	@Override
	public boolean canReadTabWithUnixSep() {
		return true;
	}

	@Override
	public boolean canReadTabWithUnixSepWithSpaceInFilename() {
		return true;
	}
	
	@Override
	public Version getVersion() {
		return Version.V0_96;
	}
	
}
