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

package au.edu.anu.datacommons.storage.tagfiles;

import java.io.IOException;
import java.io.InputStream;

/**
 * Tag file class for timestamps.txt . The file contains last modified date and time of all payload files.
 * 
 * @author Rahul Khanna
 *
 */
public class TimestampsTagFile extends AbstractKeyValueFile {
	private static final long serialVersionUID = 1L;

	public static final String FILEPATH = "timestamps.txt";

	public TimestampsTagFile(InputStream stream) throws IOException {
		super(stream);
	}
	
	@Override
	public String getFilepath() {
		return FILEPATH;
	}
}
