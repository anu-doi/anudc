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

package au.edu.anu.datacommons.storage.filesystem;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This class duplicates the functionality provided by Paths.get.
 * <p>
 * <em>This is a workaround for Spring Framework's inability to instatiate a Path object through the Paths.get factory
 * method.</em>
 * 
 * @author Rahul Khanna
 * 
 */
public class PathCreator {
	
	public static Path createPath(String path) {
		if (path == null || path.trim().length() == 0) {
			return null;
		}
		
		return Paths.get(path);
	}
	
}
