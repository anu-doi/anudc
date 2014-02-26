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
 
package au.edu.anu.datacommons.storage.event.tasks;

import gov.loc.repository.bagit.Manifest.Algorithm;
import gov.loc.repository.bagit.utilities.MessageDigestHelper;

import java.nio.file.Path;

import au.edu.anu.datacommons.storage.tagfiles.ManifestMd5TagFile;
import au.edu.anu.datacommons.storage.tagfiles.TagFilesService;

/**
 * 
 * @author Rahul Khanna
 *
 */
public class ManifestMd5Task extends AbstractTagFileTask {
	public ManifestMd5Task(String pid, Path bagDir, String relPath, TagFilesService tagFilesSvc) {
		super(pid, bagDir, relPath, tagFilesSvc);
	}

	@Override
	public Void call() throws Exception {
		String md = MessageDigestHelper.generateFixity(createInputStream(), Algorithm.MD5);
		tagFilesSvc.addEntry(pid, ManifestMd5TagFile.class, dataPrependedRelPath, md);
		return null;
	}
}
