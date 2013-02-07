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

import static java.text.MessageFormat.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.NoSuchElementException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.loc.repository.bagit.FetchTxt;
import gov.loc.repository.bagit.FetchTxtReader;
import gov.loc.repository.bagit.FetchTxt.FilenameSizeUrl;

public class FetchTxtReaderImpl implements FetchTxtReader {

	private static final Log log = LogFactory.getLog(FetchTxtReaderImpl.class);
		
	private BufferedReader reader = null;
	private FilenameSizeUrl next = null;

	public FetchTxtReaderImpl(InputStream in, String encoding) {
		try
		{
			InputStreamReader fr = new InputStreamReader(in, encoding);
			this.reader = new BufferedReader(fr);
			this.setNext();
		}
		catch(IOException ex)
		{
			throw new RuntimeException(ex);
		}
	}
	
	@Override
	public void close() {
		try
		{
			if (this.reader != null)
			{
				this.reader.close();
			}
		}
		catch(IOException ex)
		{
			log.error(ex);
		}

	}

	@Override
	public boolean hasNext() {
		if (this.next == null)
		{
			return false;
		}
		return true;
	}

	private void setNext()
	{
		try
		{
			while(true)
			{
				String line = this.reader.readLine();
				if (line == null)
				{
					this.next = null;
					return;
				}
				else
				{
					line = line.trim();
				}
				
				if (line.length() > 0)
				{
					String[] splitString = line.split("\\s+", 3);
					
					if (splitString.length == 3)
					{
						Long size = null;
						if (! FetchTxt.NO_SIZE_MARKER.equals(splitString[1])) {
							Long.parseLong(splitString[1]);
						}
						this.next = new FilenameSizeUrl(splitString[2], size, splitString[0]);
						return;
					}
					else
					{
						log.warn(format("Invalid fetch line: {0}", line));
					}
				}
			}
		}
		catch(IOException ex)
		{
			throw new RuntimeException(ex);
		}
	}
	@Override
	public FilenameSizeUrl next() {
		if (this.next == null)
		{
			throw new NoSuchElementException();
		}
		FilenameSizeUrl returnFilenameSizeUrl = this.next;
		this.setNext();
		log.debug("Read from fetch.txt: " + returnFilenameSizeUrl.toString());
		return returnFilenameSizeUrl;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();		

	}

}
