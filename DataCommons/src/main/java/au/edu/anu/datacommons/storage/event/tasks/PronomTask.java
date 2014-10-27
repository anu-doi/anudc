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
import java.util.concurrent.Semaphore;

import au.edu.anu.datacommons.storage.completer.fido.FidoParser;
import au.edu.anu.datacommons.storage.provider.StorageProvider;
import au.edu.anu.datacommons.storage.tagfiles.PronomFormatsTagFile;
import au.edu.anu.datacommons.storage.tagfiles.TagFilesService;

/**
 * A storage event task that adds a tagfile entry to store FIDO results for a file.
 * 
 * @author Rahul Khanna
 *
 */
public class PronomTask extends AbstractTagFileTask {
	private static Semaphore permit = new Semaphore(1);
	
	public PronomTask(String pid, StorageProvider storageProvider, String relPath, TagFilesService tagFilesSvc) {
		super(pid, storageProvider, relPath, tagFilesSvc);
	}

	@Override
	protected void processTask() throws Exception {
		try (InputStream fileStream = createInputStream()) {
			permit.acquire();
			FidoParser fido = new FidoParser(fileStream, storageProvider.getFileInfo(this.pid, this.relPath).getFilename());
			String fidoStr = fido.getFidoStr();
			tagFilesSvc.addEntry(pid, PronomFormatsTagFile.class, dataPrependedRelPath, fidoStr);
		} finally {
			permit.release();
		}
	}
}
