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

import gov.loc.repository.bagit.ManifestWriter;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ManifestWriterImpl implements ManifestWriter {
	
	private static final Log log = LogFactory.getLog(ManifestWriterImpl.class);	
	
	private PrintWriter writer = null;
	private String separator = null;
	
	public ManifestWriterImpl(OutputStream out, String separator) {
		try {
			// UTF-8 is the only supported BagIt encoding at present.
			// Fixes #356.
			this.writer = new PrintWriter(new OutputStreamWriter(out, "UTF-8"));
			this.separator = separator;
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
			
	public void write(String file, String fixityValue) {
		this.writer.println(fixityValue + separator + file);
		log.debug(MessageFormat.format("Wrote to manifest:  Filename is {0}.  Fixity is {1}.", file, fixityValue));		
	}
	
	public void write(String file, String fixityValue, String _separator) {
		if(_separator != null)
			this.separator = _separator;
			
		this.writer.println(fixityValue + separator + file);
		log.debug(MessageFormat.format("Wrote to manifest:  Filename is {0}.  Fixity is {1}.", file, fixityValue));		
	}
	
	public void close() {
		this.writer.close();
	}
}
