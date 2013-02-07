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

package gov.loc.repository.bagit.v0_93;

import gov.loc.repository.bagit.BagFactory.Version;
import gov.loc.repository.bagit.impl.AbstractBagInfoTxtImplTest;


public class BagInfoTxtImplTest extends AbstractBagInfoTxtImplTest {

	@Override
	public Version getVersion() {
		return Version.V0_93;
	}
	
	@Override
	public String getTestBagInfoTxtBagInfoTxtString() {
		return "Source-Organization: Spengler University\n" +
		"Organization-Address: 1400 Elm St., Cupertino, California, 95014\n" +
		"Contact-Name: Edna Janssen\n" +
		"Contact-Phone: +1 408-555-1212\n" +
		"Contact-Email: ej@spengler.edu\n" +
		"External-Description: Uncompressed greyscale TIFF images from the\n" +
		"     Yoshimuri papers collection.\n" +
		"Packing-Date: 2008-01-15\n" +
		"External-Identifier: spengler_yoshimuri_001\n" +
		"Package-Size: 260 GB\n" +
		"Bag-Group-Identifier: spengler_yoshimuri\n" +
		"Bag-Count: 1 of 15\n" +
		"Internal-Sender-Identifier: /storage/images/yoshimuri\n" +
		"Internal-Sender-Description: Uncompressed greyscale TIFFs created from\n" +
		"     microfilm.\n";

	}	
}
