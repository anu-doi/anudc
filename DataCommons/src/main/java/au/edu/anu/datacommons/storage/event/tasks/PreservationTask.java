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

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Semaphore;

import au.edu.anu.datacommons.storage.completer.preserve.PreservationFormatConverter;
import au.edu.anu.datacommons.storage.provider.StorageProvider;
import au.edu.anu.datacommons.storage.tagfiles.PreservationMapTagFile;
import au.edu.anu.datacommons.storage.tagfiles.TagFilesService;
import au.edu.anu.datacommons.storage.temp.UploadedFileInfo;
import au.gov.naa.digipres.xena.kernel.normalise.NormaliserResults;

/**
 * A storage event task that creates a preservation file and adds a tag file entry in the preserve tagfile. 
 * 
 * @author Rahul Khanna
 * 
 */
public class PreservationTask extends AbstractTagFileTask {
	private static Semaphore permit = new Semaphore(1);
	public static final String PRESERVATION_PATH = "data/.preserve/";

	public PreservationTask(String pid, StorageProvider storageProvider, String relPath, TagFilesService tagFilesSvc) {
		super(pid, storageProvider, relPath, tagFilesSvc);
	}

	@Override
	protected void processTask() throws Exception {
		if (this.dataPrependedRelPath.startsWith(PRESERVATION_PATH)) {
			tagFilesSvc.addEntry(pid, PreservationMapTagFile.class, dataPrependedRelPath, "PRESERVED");
		} else {
			preserveFile();
		}
	}

	private void preserveFile() throws IOException, InterruptedException {
		try {
			permit.acquire();
			Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"));
			InputStream fileStream = storageProvider.readStream(this.pid,
					this.relPath);
			PreservationFormatConverter pfc = new PreservationFormatConverter(this.relPath, fileStream, tempDir.toFile());
			NormaliserResults results = pfc.convert();
			if (results != null) {
				Path convertedFileInTemp = Paths.get(results.getDestinationDirString(), results.getOutputFileName());

				String searchRegEx = storageProvider.getFileInfo(this.pid, this.relPath).getFilename() + "$";
				String presvRelpath = this.dataPrependedRelPath.replaceFirst("data/", PRESERVATION_PATH)
						.replaceFirst(searchRegEx, results.getOutputFileName());
				UploadedFileInfo srcFileInfo = new UploadedFileInfo(convertedFileInTemp,
						Files.size(convertedFileInTemp), null);
				storageProvider.addFile(pid, presvRelpath.replaceFirst("data/", ""), srcFileInfo);
				tagFilesSvc.addEntry(pid, PreservationMapTagFile.class, dataPrependedRelPath, presvRelpath);
			} else {
				tagFilesSvc.addEntry(pid, PreservationMapTagFile.class, this.dataPrependedRelPath, "UNCONVERTIBLE");
			}
		} finally {
			permit.release();
		}
	}
}
