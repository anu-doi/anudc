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

import java.text.MessageFormat;
import java.util.List;

public interface FetchTxt extends List<FetchTxt.FilenameSizeUrl>, BagFile {

	static final String NO_SIZE_MARKER = "-";
	
	public class FilenameSizeUrl {
		private String filename;
		private Long size;
		private String url;
		
		public FilenameSizeUrl() {
		}

		public FilenameSizeUrl(String filename, Long size, String url) {
			this.setFilename(filename);
			this.setSize(size);
			this.setUrl(url);
		}

		public void setFilename(String filename) {
			this.filename = filename;
		}

		public String getFilename() {
			return filename;
		}

		public void setSize(Long size) {
			this.size = size;
		}

		public Long getSize() {
			return size;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getUrl() {
			return url;
		}

		@Override
		public String toString() {
			String size = NO_SIZE_MARKER;
			if (this.size != null) {
				size = this.size.toString();
			}
			return MessageFormat.format("Filename is {0}. Size is {1}. Url is {2}.", this.filename, size, this.url);
		}
	}	

}
