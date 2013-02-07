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

import org.junit.Test;

import gov.loc.repository.bagit.BagFactory.Version;
import gov.loc.repository.bagit.impl.AbstractManifestReaderImplTest;

public class ManifestReaderImplTest extends AbstractManifestReaderImplTest {

	@Override
	public boolean canReadDoubleSpaceWithUnixSep() {
		return true;
	}

	@Override
	public boolean canReadSingleSpaceWithUnixSep() {
		return true;
	}

	@Override
	public boolean canReadSingleSpaceWithWindowsSep() {
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
	@Test
	public void testSpaceAstericksWithUnixSep() throws Exception {
		assertTrue(this.canReadLine("8ad8757baa8564dc136c1e07507f4a98 *data/test1.txt\n", "8ad8757baa8564dc136c1e07507f4a98", "*data/test1.txt"));
		
	}
		
	@Override
	public boolean canReadSpaceAstericksWithUnixSep() {
		return true;
	}

	@Override
	public Version getVersion() {
		return Version.V0_94;
	}

	
}
