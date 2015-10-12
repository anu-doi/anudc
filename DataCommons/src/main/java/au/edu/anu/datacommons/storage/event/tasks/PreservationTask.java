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

import gov.loc.repository.bagit.utilities.FilenameHelper;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Semaphore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.storage.completer.preserve.PreservationFormatConverter;
import au.edu.anu.datacommons.storage.completer.preserve.PreservationFormatConverter.InputStreamProvider;
import au.edu.anu.datacommons.storage.event.StorageEvent;
import au.edu.anu.datacommons.storage.event.StorageEvent.EventType;
import au.edu.anu.datacommons.storage.event.StorageEventListener;
import au.edu.anu.datacommons.storage.event.TagFilesStorageEventListener;
import au.edu.anu.datacommons.storage.event.StorageEventListener.EventTime;
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
	public static final String PRESERVATION_PATH = "data/.preserve/";
	
	private static Semaphore permit = new Semaphore(1);
	
	private StorageEventListener evtListener;

	public PreservationTask(String pid, StorageProvider storageProvider, String relPath, TagFilesService tagFilesSvc,
			StorageEventListener storageEventListener) {
		super(pid, storageProvider, relPath, tagFilesSvc);
		this.evtListener = storageEventListener;
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
		Path convertedFileInTemp = null;
		try {
			permit.acquire();
			Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"));
			InputStreamProvider inputStreamProvider = new InputStreamProvider() {

				@Override
				public InputStream getInputStream() throws IOException {
					return storageProvider.readStream(pid, relPath);
				}
			};
			PreservationFormatConverter pfc = new PreservationFormatConverter(this.relPath, inputStreamProvider,
					tempDir.toFile());
			NormaliserResults results = pfc.convert();
			if (results != null) {
				// file could be converted - converted file saved in tempDir
				convertedFileInTemp = Paths.get(results.getDestinationDirString(), results.getOutputFileName());
				UploadedFileInfo srcFileInfo = new UploadedFileInfo(convertedFileInTemp,
						Files.size(convertedFileInTemp), null);

				String convertedFileExtn = results.getOutputFileName()
						.substring(results.getOutputFileName().lastIndexOf('.'));
				String dataPrependedPresvRelPath = this.dataPrependedRelPath.replaceFirst("^data/", PRESERVATION_PATH)
						+ convertedFileExtn;
				String presvRelPath = dataPrependedPresvRelPath.replaceFirst("^data/", "");

				StorageEvent presvFileAddEvt = new StorageEvent(EventType.ADD_FILE, pid, storageProvider, presvRelPath,
						srcFileInfo);
				this.evtListener.notify(EventTime.PRE, presvFileAddEvt);

				// add preserved file in .preserve folder within payload directory maintaining relative path
				storageProvider.addFile(pid, presvRelPath, srcFileInfo);
				tagFilesSvc.addEntry(pid, PreservationMapTagFile.class, dataPrependedRelPath,
						dataPrependedPresvRelPath);

				this.evtListener.notify(EventTime.POST, presvFileAddEvt);
			} else {
				tagFilesSvc.addEntry(pid, PreservationMapTagFile.class, this.dataPrependedRelPath, "UNCONVERTIBLE");
			}
		} finally {
			permit.release();

			if (convertedFileInTemp != null && Files.isRegularFile(convertedFileInTemp)) {
				try {
					Files.delete(convertedFileInTemp);
				} catch (IOException e) {
					// No op - it's a temporary file that couldn't be deleted.
				}
			}
		}
	}
	

}
