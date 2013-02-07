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

import java.io.InputStream;

/**
 * Represents a file in a bag.  The file may be either a tag file,
 * such as a {@link Manifest} or a {@link BagInfoTxt}, or it may
 * be an ordinary payload file.
 */
public interface BagFile {
	
	/**
	 * Opens the file for reading.
	 * @return A new stream for reading the contents of the file.
	 * @throws RuntimeException Thrown if the file cannot be opened.
	 */
	InputStream newInputStream();
	
	/**
	 * Gets the path of the file, relative to the bag directory.
	 * @return The path of the file.  Will never be null.
	 */
	String getFilepath();
	
	/**
	 * Determines whether the file exists on disk or not.
	 * @return Return <c>true</c> if the file file exists;
	 * <c>false</c> otherwise.
	 */
	boolean exists();
	
	/**
	 * Gets the size of the file on disk.
	 * @return The size of the file, or 0 if the file does not exist.
	 */
	long getSize();
	
}
