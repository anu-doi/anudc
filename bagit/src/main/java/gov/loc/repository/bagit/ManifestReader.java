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

package gov.loc.repository.bagit;

import java.io.Closeable;
import java.text.MessageFormat;
import java.util.Iterator;

public interface ManifestReader extends Iterator<ManifestReader.FilenameFixity>, Closeable {

	public class FilenameFixity {
		private String filename;
		private String fixityValue;
		
		public FilenameFixity(String file, String fixityValue) {
			this.filename = file;
			this.fixityValue = fixityValue;
		}
			
		public FilenameFixity()	{			
		}
		
		public void setFilename(String file) {
			this.filename = file;
		}
		
		public String getFilename() {
			return filename;
		}
		
		public void setFixityValue(String fixityValue) {
			this.fixityValue = fixityValue;
		}
		
		public String getFixityValue() {
			return fixityValue;
		}
		
		@Override
		public String toString() {
			return MessageFormat.format("Filename is {0}.  Fixity is {1}.", this.filename, this.fixityValue);
		}
	}	
}
