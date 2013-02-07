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

package gov.loc.repository.bagit.v0_97.impl;

import java.io.InputStream;
import java.io.OutputStream;

import gov.loc.repository.bagit.BagFactory;
import gov.loc.repository.bagit.ManifestReader;
import gov.loc.repository.bagit.ManifestWriter;
import gov.loc.repository.bagit.Bag.BagConstants;
import gov.loc.repository.bagit.BagFactory.Version;
import gov.loc.repository.bagit.impl.AbstractBagPartFactory;
import gov.loc.repository.bagit.impl.ManifestReaderImpl;
import gov.loc.repository.bagit.impl.ManifestWriterImpl;

public class BagPartFactoryImpl extends AbstractBagPartFactory {

	private static final String SPLIT_REGEX = "( \\*)|( \\t)|(\\s+)";
	private static final String SEPARATOR = "  ";
	
	public BagPartFactoryImpl(BagFactory bagFactory, BagConstants bagConstants) {
		super(bagFactory, bagConstants);
	}
		
	public ManifestReader createManifestReader(InputStream in, String encoding) {
		return new ManifestReaderImpl(in, encoding, SPLIT_REGEX, false);
	}
	
	@Override
	public ManifestReader createManifestReader(InputStream in, String encoding,
			boolean treatBackSlashAsPathSeparator) {
		return new ManifestReaderImpl(in, encoding, SPLIT_REGEX, treatBackSlashAsPathSeparator);
	}
	
	public ManifestWriter createManifestWriter(OutputStream out) {
		return new ManifestWriterImpl(out, SEPARATOR);			
	}
	
	@Override
	public Version getVersion() {
		return Version.V0_97;
	}
	
}
