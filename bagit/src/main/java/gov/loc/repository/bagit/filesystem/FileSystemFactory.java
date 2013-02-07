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

package gov.loc.repository.bagit.filesystem;

import gov.loc.repository.bagit.Bag.Format;
import gov.loc.repository.bagit.filesystem.impl.FileFileSystem;
import gov.loc.repository.bagit.filesystem.impl.ZipFileSystem;
import gov.loc.repository.bagit.utilities.FormatHelper;
import gov.loc.repository.bagit.utilities.FormatHelper.UnknownFormatException;

import java.io.File;
import java.text.MessageFormat;

public class FileSystemFactory {

	public static DirNode getDirNodeForBag(File fileForBag) throws UnknownFormatException, UnsupportedFormatException {
		assert fileForBag != null;
		
		if (! fileForBag.exists()) {
			throw new RuntimeException(MessageFormat.format("{0} does not exist", fileForBag));
		}

		Format format = FormatHelper.getFormat(fileForBag);
		FileSystem fs = null;
		if (Format.FILESYSTEM == format) {
			fs = new FileFileSystem(fileForBag);
		} else if (Format.ZIP == format) {
			fs = new ZipFileSystem(fileForBag);
		} else {
			throw new UnsupportedFormatException();
		}

		DirNode root = fs.getRoot();
		if (format.isSerialized) {
			if (root.listChildren().size() != 1) {
				throw new RuntimeException("Unable to find bag_dir in serialized bag");
			}
			FileSystemNode bagDirNode = root.listChildren().iterator().next();
			if (! (bagDirNode instanceof DirNode)) {
				throw new RuntimeException("Unable to find bag_dir in serialized bag");
			}
			root = (DirNode)bagDirNode;
		}
		return root;
	}

	public static class UnsupportedFormatException extends Exception {
		private static final long serialVersionUID = 1L;

		public UnsupportedFormatException() {
			super("Unsupported format");
		}
	}

	
}
