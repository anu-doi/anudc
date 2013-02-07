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

import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.input.NullInputStream;
import org.apache.commons.io.output.NullOutputStream;

import gov.loc.repository.bagit.BagFile;
import gov.loc.repository.bagit.transfer.BagTransferException;
import gov.loc.repository.bagit.transfer.FetchedFileDestination;
import gov.loc.repository.bagit.transfer.FetchedFileDestinationFactory;

public class NullFetchDestination implements FetchedFileDestinationFactory
{
	@Override
	public FetchedFileDestination createDestination(String path, Long size) throws BagTransferException 
	{
		return new NullDestination(path);
	}

	private static class NullDestination implements FetchedFileDestination
	{
		private String path;
		
		public NullDestination(String path)
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
		public OutputStream openOutputStream(boolean append) throws BagTransferException {
			return new NullOutputStream();
		}		

		@Override
		public BagFile commit() throws BagTransferException
		{
			return new NullBagFile(this.path);
		}
		
		@Override
		public void abandon()
		{
			// Do nothing.
		}
	};
	
	private static class NullBagFile implements BagFile
	{
		private String path;
		
		public NullBagFile(String path)
		{
			this.path = path;
		}
		
		@Override
		public boolean exists()
		{
			return false;
		}

		@Override
		public String getFilepath()
		{
			return this.path;
		}

		@Override
		public long getSize()
		{
			return 0;
		}

		@Override
		public InputStream newInputStream()
		{
			return new NullInputStream(0);
		}
	}
}
