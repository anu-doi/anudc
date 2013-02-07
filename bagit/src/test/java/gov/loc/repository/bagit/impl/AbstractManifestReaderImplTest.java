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

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;

import gov.loc.repository.bagit.BagFactory;
import gov.loc.repository.bagit.ManifestReader;
import gov.loc.repository.bagit.Bag.BagPartFactory;
import gov.loc.repository.bagit.BagFactory.Version;
import gov.loc.repository.bagit.ManifestReader.FilenameFixity;

import org.junit.Before;
import org.junit.Test;


public abstract class AbstractManifestReaderImplTest {

	public abstract Version getVersion();
	
	BagFactory bagFactory = new BagFactory();
	BagPartFactory factory;
	
	@Before
	public void setup() {
		this.factory = bagFactory.getBagPartFactory(this.getVersion());
	}

	
	public boolean canReadLine(String line) throws Exception {
		return this.canReadLine(line, "8ad8757baa8564dc136c1e07507f4a98", "data/test1.txt");
	}

	public boolean canReadLine(String line, String expectedFixityValue, String expectedFilename, boolean treatBackslashAsPathSeparator) throws Exception {
		ManifestReader reader = factory.createManifestReader(new ByteArrayInputStream(line.getBytes("utf-8")), "utf-8", treatBackslashAsPathSeparator);
		boolean canRead = false;
		if (reader.hasNext()) {
			canRead = true;
			FilenameFixity ff = reader.next();
			assertEquals(expectedFixityValue, ff.getFixityValue());
			assertEquals(expectedFilename, ff.getFilename());			
		}
		reader.close();
		return canRead;
	}

	public boolean canReadLine(String line, String expectedFixityValue, String expectedFilename) throws Exception {
		ManifestReader reader = factory.createManifestReader(new ByteArrayInputStream(line.getBytes("utf-8")), "utf-8");
		boolean canRead = false;
		if (reader.hasNext()) {
			canRead = true;
			FilenameFixity ff = reader.next();
			assertEquals(expectedFixityValue, ff.getFixityValue());
			assertEquals(expectedFilename, ff.getFilename());			
		}
		reader.close();
		return canRead;
	}

	
	@Test
	public void testSingleSpaceWithUnixSep() throws Exception {
		assertEquals(this.canReadLine("8ad8757baa8564dc136c1e07507f4a98 data/test1.txt\n"), this.canReadSingleSpaceWithUnixSep());
		
	}
	
	public boolean canReadSingleSpaceWithUnixSep() {
		return false;
	}
	
	@Test
	public void testSingleSpaceWithWindowsSep() throws Exception {
		assertEquals(this.canReadSingleSpaceWithWindowsSep(), this.canReadLine("8ad8757baa8564dc136c1e07507f4a98 data\\test1.txt\n"));
	}
	
	public boolean canReadSingleSpaceWithWindowsSep() {
		return false;
	}

	@Test
	public void testTabWithUnixSep() throws Exception {
		assertEquals(this.canReadLine("8ad8757baa8564dc136c1e07507f4a98	data/test1.txt\n"), this.canReadTabWithUnixSep());
		
	}
	
	public boolean canReadTabWithUnixSep() {
		return false;
	}

	@Test
	public void testTabWithUnixSepWithSpaceInFilename() throws Exception {
		ManifestReader reader = factory.createManifestReader(new ByteArrayInputStream("8ad8757baa8564dc136c1e07507f4a98	data/test 1.txt\n".getBytes("utf-8")), "utf-8");
		if (reader.hasNext()) {
			assertTrue(this.canReadTabWithUnixSepWithSpaceInFilename());
			FilenameFixity ff = reader.next();
			assertEquals("8ad8757baa8564dc136c1e07507f4a98", ff.getFixityValue());
			assertEquals("data/test 1.txt", ff.getFilename());			
		} else {
			assertFalse(this.canReadTabWithUnixSepWithSpaceInFilename());
		}
		
	}
	
	public boolean canReadTabWithUnixSepWithSpaceInFilename() {
		return false;
	}

	@Test
	public void testDoubleSpaceWithUnixSep() throws Exception {
		assertEquals(this.canReadLine("8ad8757baa8564dc136c1e07507f4a98  data/test1.txt\n"), this.canReadDoubleSpaceWithUnixSep());
		
	}
	public boolean canReadDoubleSpaceWithUnixSep() {
		return false;
	}

	@Test
	public void testUnderscoreWithUnixSep() throws Exception {
		assertEquals(this.canReadLine("8ad8757baa8564dc136c1e07507f4a98_data/test1.txt\n"), this.canReadUnderscoreWithUnixSep());
		
	}
	
	public boolean canReadUnderscoreWithUnixSep() {
		return false;
	}

	@Test
	public void testSpaceAstericksWithUnixSep() throws Exception {
		assertEquals(this.canReadLine("8ad8757baa8564dc136c1e07507f4a98 *data/test1.txt\n"), this.canReadSpaceAstericksWithUnixSep());
		
	}
	
	public boolean canReadSpaceAstericksWithUnixSep() {
		return false;
	}

}
