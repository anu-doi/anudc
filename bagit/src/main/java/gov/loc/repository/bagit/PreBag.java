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

import gov.loc.repository.bagit.BagFactory.Version;
import gov.loc.repository.bagit.transformer.Completer;

import java.io.File;
import java.util.List;

public interface PreBag {
	void setFile(File file);
	File getFile();
	void setTagFiles(List<File> tagFiles);
	List<File> getTagFiles();
	void setIgnoreAdditionalDirectories(List<String> dirs);
	Bag makeBagInPlace(Version version, boolean retainBaseDirectory);
	Bag makeBagInPlace(Version version, boolean retainBaseDirectory, boolean keepEmptyDirectories);
	Bag makeBagInPlace(Version version, boolean retainBaseDirectory, boolean keepEmptyDirectories, Completer completer);
	Bag makeBagInPlace(Version version, boolean retainBaseDirectory, Completer completer);
}
