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

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;

import au.edu.anu.datacommons.storage.tagfiles.TagFilesService;
import au.edu.anu.datacommons.storage.tagfiles.TimestampsTagFile;

/**
 * @author Rahul Khanna
 * 
 */
public class TimestampTask extends AbstractTagFileTask {
	
	public TimestampTask(String pid, Path bagDir, String relPath, TagFilesService tagFilesSvc) {
		super(pid, bagDir, relPath, tagFilesSvc);
	}

	@Override
	protected void processTask() throws Exception {
		FileTime lastModifiedTime = Files.getLastModifiedTime(absFilepath);
		String lastModifiedStr = Long.toString(lastModifiedTime.toMillis(), 10);
		tagFilesSvc.addEntry(pid, TimestampsTagFile.class, dataPrependedRelPath, lastModifiedStr);
	}
}
