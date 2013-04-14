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

package au.edu.anu.datacommons.image.filter;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.List;

import au.edu.anu.datacommons.image.main.DefaultProperties;

/**
 * ImageAndDistFileFilter
 * 
 * Australian National University Data Commons
 * 
 * Filters image files
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		12/04/2013	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
public class ImageAndDistFileFilter implements FileFilter {
	private static final List<String> acceptableFormats = Arrays.asList(DefaultProperties.getProperty("image.types").split(","));

	/**
	 * accept
	 * 
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * X.X		12/04/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param file
	 * @return
	 * @see java.io.FileFilter#accept(java.io.File)
	 */
	@Override
	public boolean accept(File file) {
		if (!file.isFile()) {
			return false;
		}
		String name = file.getName();
		if (name.contains("-dist.")) {
			return false;
		}
		String suffix = name.substring(name.lastIndexOf("."));
		if (acceptableFormats.contains(suffix.toLowerCase())) {
			return true;
		}
		
		return false;
	}

}
