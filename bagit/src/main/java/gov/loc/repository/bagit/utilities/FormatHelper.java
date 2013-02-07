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

package gov.loc.repository.bagit.utilities;

import gov.loc.repository.bagit.Bag.Format;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

public class FormatHelper {
	
	public static boolean isZip(File file) {
		return hasMagicNumber(file, new String[] {"50","4B"}, 0);
	}
	
	private static boolean hasMagicNumber(File file, String[] magicNumber, int offset) {
		try {
			return hasMagicNumber(new FileInputStream(file), magicNumber, offset);
		} catch(Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	private static boolean hasMagicNumber(InputStream in, String[] magicNumber, int offset) {
		boolean matches = true;		
		try {
			for(int i=0; i < offset; i++) {
				in.read();
			}
			
			for(String magicPart : magicNumber) {
				String filePart = Integer.toHexString(in.read());
				if (! filePart.equalsIgnoreCase(magicPart)) {
					matches = false;
				}
			}					
			return matches;
		}
		catch(Exception ex) {
			throw new RuntimeException(ex);
		} finally {
			IOUtils.closeQuietly(in);
		}
	}
	
	public static Format getFormat(File file) throws UnknownFormatException {
		if (file == null) {
			throw new RuntimeException("Cannot determine format");
		}
		else if (file.isDirectory()) {
			return Format.FILESYSTEM;
		}
		else if (isZip(file)) {
			return Format.ZIP;
		}
		throw new UnknownFormatException();
	}

	public static class UnknownFormatException extends Exception {
		private static final long serialVersionUID = 1L;

		public UnknownFormatException() {
			super("Unknown format");
		}
	}
	
}
