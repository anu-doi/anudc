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

package gov.loc.repository.bagit.utilities.namevalue.impl;

import gov.loc.repository.bagit.utilities.namevalue.NameValueWriter;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.MessageFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class NameValueWriterImpl implements NameValueWriter {	
	
	private static final Log log = LogFactory.getLog(NameValueWriterImpl.class);
	
	private PrintWriter writer = null;
	private int lineLength = 79;
	//Default to 4
	private String indent = "   ";
	private String type;
	
	public NameValueWriterImpl(OutputStream out, String encoding, int lineLength, int indentSpaces, String type) {
		this.init(out, encoding, type);
		this.lineLength = lineLength;
		this.indent = "";
		for(int i=0; i<indentSpaces; i++) {
			this.indent += " ";
		}
	}

	public NameValueWriterImpl(OutputStream out, String encoding, String type) {
		this.init(out, encoding, type);
	}
	
	private void init(OutputStream out, String encoding, String type) {
		try {
			this.writer = new PrintWriter(new OutputStreamWriter(out, encoding), true);
		}
		catch(Exception ex) {
			throw new RuntimeException(ex);
		}
		this.type = type;
	}
	
	public void write(String name, String value) {		
		String line = name + ": " + (value !=null ? value : "");
		boolean isFirst = true;
		while (line.length() > 0) {
			int workingLength = lineLength;
			if (! isFirst) {
				workingLength = lineLength - this.indent.length();
			}
			String linePart = "";
			if (line.length() <= workingLength) {
				linePart = line;
				line = "";
			}
			else {
				//Start at lineLength and work backwards until find a space
				int index = workingLength;
				while(index >= 0 && line.charAt(index) != ' ') {
					index = index-1;
				}
				if (index < 0) {
					//Use whole line
					linePart = line;
					line = "";
				}
				else {
					linePart = line.substring(0, index);
					line = line.substring(index + 1);
				}
					
			}
			if (isFirst) {
				isFirst = false;
			}
			else {
				linePart = this.indent + linePart;
			}
			this.writer.println(linePart);
		}
		log.debug(MessageFormat.format("Wrote to {0}: Name is {1}. Value is {2}.", this.type, name, value));
	}
	
	
	public void close() {
		this.writer.close();
	}
}
