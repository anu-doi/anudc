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

package au.edu.anu.datacommons.storage;

import java.io.File;
import java.io.FileFilter;

/**
 * @author Rahul Khanna
 *
 */
public class StorageFileFilter implements FileFilter {
	private File baseDir;
	
	public StorageFileFilter(File baseDir) {
		super();
		this.baseDir = baseDir;
	}

	@Override
	public boolean accept(File pathname) {
		return !hasHiddenParents(pathname);
	}

	/**
	 * Checks if the specified File or any of its parents' names (up to, but excluding the base directory) start with a
	 * period character.
	 * 
	 * @param pathname
	 *            File object to check
	 * @return true if contains hidden parents. false otherwise.
	 */
	private boolean hasHiddenParents(File pathname) {
		boolean hasHiddenParents = false;
		for (File i = pathname; i != null && !i.equals(baseDir); i = i.getParentFile()) {
			if (i.getName().startsWith(".")) {
				hasHiddenParents = true;
				break;
			}
		}
		return hasHiddenParents;
	}

}
