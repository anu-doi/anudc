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

import gov.loc.repository.bagit.FetchTxt;
import gov.loc.repository.bagit.FetchTxtWriter;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.MessageFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FetchTxtWriterImpl implements FetchTxtWriter {
	private static final Log log = LogFactory.getLog(FetchTxtWriterImpl.class);
	
	public static final String SEPARATOR = "  ";
	
	private PrintWriter writer = null;
	
	public FetchTxtWriterImpl(OutputStream out) {
		this.writer = new PrintWriter(out);
	}
	
	@Override
	public void write(String filename, Long size, String url) {
		String sizeString = FetchTxt.NO_SIZE_MARKER;
		if (size != null) {
			sizeString = size.toString();
		}
		try {
			String newUrl = url.replaceAll(" ", "%20");
			this.writer.println(newUrl + SEPARATOR + sizeString + SEPARATOR + filename);
			log.debug(MessageFormat.format("Wrote to fetch.txt:  Filename is {0}.  Size is {1}. Url is {2}.", filename, size, newUrl));
		}
		catch(Exception ex) {
			throw new RuntimeException(ex);
		}
		
	}
		
	public void close() {
		this.writer.close();
	}
}
