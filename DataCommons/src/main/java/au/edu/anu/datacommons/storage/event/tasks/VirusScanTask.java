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

import java.io.InputStream;
import java.nio.file.Path;

import au.edu.anu.datacommons.properties.GlobalProps;
import au.edu.anu.datacommons.storage.completer.virusscan.ClamScan;
import au.edu.anu.datacommons.storage.info.ScanResult;
import au.edu.anu.datacommons.storage.tagfiles.TagFilesService;
import au.edu.anu.datacommons.storage.tagfiles.VirusScanTagFile;

/**
 * @author Rahul Khanna
 *
 */
public class VirusScanTask extends AbstractTagFileTask {

	public VirusScanTask(String pid, Path bagDir, String relPath, TagFilesService tagFilesSvc) {
		super(pid, bagDir, relPath, tagFilesSvc);
	}

	@Override
	public Void call() throws Exception {
		ClamScan cs = new ClamScan(GlobalProps.getClamScanHost(), GlobalProps.getClamScanPort(), GlobalProps.getClamScanTimeout());
		try (InputStream fileStream = createInputStream()) {
			ScanResult sr = cs.scan(fileStream);
			tagFilesSvc.addEntry(pid, VirusScanTagFile.class, dataPrependedRelPath, sr.getResult());
		}
		return null;
	}

}
