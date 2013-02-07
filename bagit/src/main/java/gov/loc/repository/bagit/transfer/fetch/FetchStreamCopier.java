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

package gov.loc.repository.bagit.transfer.fetch;

import static java.text.MessageFormat.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import gov.loc.repository.bagit.transfer.BagTransferCancelledException;
import gov.loc.repository.bagit.utilities.LongRunningOperationBase;

class FetchStreamCopier extends LongRunningOperationBase
{
	private static final Log log = LogFactory.getLog(FetchStreamCopier.class);
	
	private String action;
	private Object item;
	private Long total;
	
	public FetchStreamCopier(String action, Object item, Long total)
	{
		this.action = action;
		this.item = item;
		this.total = total;
	}
	
	public long copy(InputStream in, OutputStream out) throws IOException, BagTransferCancelledException
	{
		log.trace(format(
				"Starting copy from {3} to {4}. Action={0}; Item={1}; Total={2}",
				this.action, 
				this.item,
				this.total,
				in.getClass().getName(), 
				out.getClass().getName()));
		
		byte[] buffer = new byte[4 * 1024];
		long totalCopied = 0;
	
		log.trace("Reading from input stream.");
		int read = in.read(buffer, 0, buffer.length);
		log.trace(format("Read {0} bytes from input stream.", read));
		
		while (read >= 0)
		{
			log.trace(format("Writing {0} bytes to output stream.", read));
			out.write(buffer, 0, read);
			totalCopied += read;
			log.trace(format("Write complete.  Total copied: {0}", totalCopied));

			log.trace("Updating progress.");
			this.progress(this.action, this.item, totalCopied, this.total);
			log.trace("Progress updated.");
			
			log.trace("Checking for cancellation.");
			if (this.isCancelled())
				throw new BagTransferCancelledException(format("Copy cancelled after {0} bytes.", totalCopied));
			log.trace("Not cancelled.");
			
			log.trace("Reading from input stream.");
			read = in.read(buffer, 0, buffer.length);
			log.trace(format("Read {0} bytes from input stream.", read));
		}				
		
		log.trace(format("Copy complete.  Total bytes copied: {0}", totalCopied));
		return totalCopied;
	}
}
