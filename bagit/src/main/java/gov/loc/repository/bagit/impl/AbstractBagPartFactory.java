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

import java.io.InputStream;
import java.io.OutputStream;

import gov.loc.repository.bagit.BagFactory;
import gov.loc.repository.bagit.BagFile;
import gov.loc.repository.bagit.BagInfoTxt;
import gov.loc.repository.bagit.BagInfoTxtReader;
import gov.loc.repository.bagit.BagInfoTxtWriter;
import gov.loc.repository.bagit.BagItTxt;
import gov.loc.repository.bagit.BagItTxtReader;
import gov.loc.repository.bagit.BagItTxtWriter;
import gov.loc.repository.bagit.FetchTxt;
import gov.loc.repository.bagit.FetchTxtReader;
import gov.loc.repository.bagit.FetchTxtWriter;
import gov.loc.repository.bagit.Manifest;
import gov.loc.repository.bagit.ManifestWriter;
import gov.loc.repository.bagit.Bag.BagConstants;
import gov.loc.repository.bagit.Bag.BagPartFactory;

public abstract class AbstractBagPartFactory implements BagPartFactory {

	protected BagConstants bagConstants;
	protected BagFactory bagFactory;
	
	public AbstractBagPartFactory(BagFactory bagFactory, BagConstants bagConstants) {
		this.bagConstants = bagConstants;
		this.bagFactory = bagFactory;
		
	}
	
	@Override
	public BagItTxt createBagItTxt(BagFile bagFile) {
		return new BagItTxtImpl(bagFile, this.bagConstants);
	}

	@Override
	public BagItTxt createBagItTxt() {
		return new BagItTxtImpl(this.bagConstants);
	}

	@Override
	public BagItTxtReader createBagItTxtReader(String encoding, InputStream in) {
		return new BagItTxtReaderImpl(encoding, in);
	}

	@Override
	public BagItTxtWriter createBagItTxtWriter(OutputStream out,
			String encoding, int lineLength, int indentSpaces) {
		return new BagItTxtWriterImpl(out, encoding, lineLength, indentSpaces);
	}

	@Override
	public BagItTxtWriter createBagItTxtWriter(OutputStream out, String encoding) {
		return new BagItTxtWriterImpl(out, encoding);
	}

	@Override
	public Manifest createManifest(String name) {
		return new ManifestImpl(name, this.bagConstants, this);
	}

	@Override
	public Manifest createManifest(String name, BagFile sourceBagFile) {
		return new ManifestImpl(name, this.bagConstants, this, sourceBagFile);
	}
	
	@Override
	public ManifestWriter createManifestWriter(OutputStream out,
			String manifestSeparator) {
		if (manifestSeparator != null) {
			return new ManifestWriterImpl(out, manifestSeparator);
		}
		return this.createManifestWriter(out);
	}
	
	@Override
	public BagInfoTxt createBagInfoTxt() {
		return new BagInfoTxtImpl(this.bagConstants);
	}
	
	@Override
	public BagInfoTxt createBagInfoTxt(BagFile bagFile) {
		return new BagInfoTxtImpl(bagFile, this.bagConstants);
	}
	
	@Override
	public BagInfoTxtReader createBagInfoTxtReader(String encoding,
			InputStream in) {
		return new BagInfoTxtReaderImpl(encoding, in);
	}
	
	@Override
	public BagInfoTxtWriter createBagInfoTxtWriter(OutputStream out,
			String encoding) {
		return new BagInfoTxtWriterImpl(out, encoding);
	}
	
	@Override
	public BagInfoTxtWriter createBagInfoTxtWriter(OutputStream out,
			String encoding, int lineLength, int indentSpaces) {
		return new BagInfoTxtWriterImpl(out, encoding, lineLength, indentSpaces);
	}
	
	@Override
	public FetchTxt createFetchTxt() {
		return new FetchTxtImpl(this.bagConstants, this);
	}

	@Override
	public FetchTxt createFetchTxt(BagFile sourceBagFile) {
		return new FetchTxtImpl(this.bagConstants, this, sourceBagFile);
	}

	@Override
	public FetchTxtReader createFetchTxtReader(InputStream in, String encoding) {
		return new FetchTxtReaderImpl(in, encoding);
	}

	@Override
	public FetchTxtWriter createFetchTxtWriter(OutputStream out) {
		return new FetchTxtWriterImpl(out);
	}
			
}
