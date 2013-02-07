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

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import gov.loc.repository.bagit.BagFile;

public class StringBagFile implements BagFile {

	private String filepath;
	private byte[] buf = new byte[0];
	private static final String ENC = "utf-8";
	
	public StringBagFile(String name, byte[] data)
	{
		this.filepath = name;
		this.buf = data;
	}
	
	public StringBagFile(String name, String str) {		
		this.filepath = name;
		if (str != null) {
			try {
				this.buf = str.getBytes(ENC);
			}
			catch(Exception ex) {
				throw new RuntimeException(ex);
			}
		}
	}
	
	@Override
	public boolean exists() {
		if (buf.length == 0) {
			return false;
		}
		return true;
	}

	@Override
	public String getFilepath() {
		return this.filepath;
	}

	@Override
	public long getSize() {
		return buf.length;
	}

	@Override
	public InputStream newInputStream() {
		return new ByteArrayInputStream(this.buf);
	}

}
