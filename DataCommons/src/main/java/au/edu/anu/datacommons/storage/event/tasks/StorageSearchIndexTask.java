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

import java.nio.file.Path;

import au.edu.anu.datacommons.storage.info.FileInfo;
import au.edu.anu.datacommons.storage.provider.StorageProvider;
import au.edu.anu.datacommons.storage.search.StorageSearchService;

/**
 * A storage event task that requests the data search service to index a payload file.
 * 
 * @author Rahul Khanna
 *
 */
public class StorageSearchIndexTask extends AbstractStorageEventTask {

	private StorageSearchService searchSvc;
	
	public StorageSearchIndexTask(String pid, StorageProvider storageProvider, String relPath, StorageSearchService searchSvc) {
		super(pid, storageProvider, relPath);
		this.searchSvc = searchSvc;
	}

	@Override
	protected void processTask() throws Exception {
		searchSvc.indexFile(pid, relPath, storageProvider);		
	}
}
