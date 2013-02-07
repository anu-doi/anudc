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

package gov.loc.repository.bagit.transfer.dest;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import gov.loc.repository.bagit.BagFile;
import gov.loc.repository.bagit.impl.StringBagFile;
import gov.loc.repository.bagit.transfer.BagTransferException;
import gov.loc.repository.bagit.transfer.FetchedFileDestination;

/**
 * A {@link FetchedFileDestination} that stores data in a byte
 * array in memory.  Useful for unit testing, or for when downloading
 * tag files or other known-to-be-small files.
 * 
 * @version $Id$
 */
public class ByteArrayFetchDestination implements FetchedFileDestination
{
	private String path;
	private ByteArrayOutputStream stream;
	
	public ByteArrayFetchDestination(String path)
	{
		this.path = path;
	}
	
	@Override
	public String getFilepath()
	{
		return this.path;
	}
	
	@Override
	public boolean getSupportsDirectAccess()
	{
		return false;
	}
	
	@Override
	public String getDirectAccessPath()
	{
		throw new IllegalStateException("Direct access is not supported by this fetch destination.");
	}

	@Override
	public void abandon()
	{
		this.stream = null;
	}

	@Override
	public BagFile commit() throws BagTransferException
	{
		if (this.stream == null)
			throw new BagTransferException("No data ever written to destination.");
		
		StringBagFile result = new StringBagFile(this.path, this.stream.toByteArray());
		this.stream = null;
		
		return result;
	}

	@Override
	public OutputStream openOutputStream(boolean append) throws BagTransferException
	{
		return this.stream = new ByteArrayOutputStream();
	}
	
}
