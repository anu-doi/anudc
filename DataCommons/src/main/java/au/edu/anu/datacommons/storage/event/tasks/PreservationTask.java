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
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.storage.DcStorage;
import au.edu.anu.datacommons.storage.completer.preserve.PreservationFormatConverter;
import au.edu.anu.datacommons.storage.tagfiles.PreservationMapTagFile;
import au.edu.anu.datacommons.storage.tagfiles.TagFilesService;
import au.gov.naa.digipres.xena.kernel.normalise.NormaliserResults;

/**
 * @author Rahul Khanna
 * 
 */
public class PreservationTask extends AbstractTagFileTask {
	private static final Logger LOGGER = LoggerFactory.getLogger(PreservationTask.class);
	public static final String PRESERVATION_PATH = "data/.preserve/";

	private DcStorage dcStorageSvc;

	public PreservationTask(String pid, Path bagDir, String relPath, TagFilesService tagFilesSvc, DcStorage dcStorageSvc) {
		super(pid, bagDir, relPath, tagFilesSvc);
		this.dcStorageSvc = dcStorageSvc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see au.edu.anu.datacommons.storage.event.tasks.AbstractStorageEventTask#call()
	 */
	@Override
	public Void call() throws Exception {
		if (this.dataPrependedRelPath.startsWith(PRESERVATION_PATH)) {
			tagFilesSvc.addEntry(pid, PreservationMapTagFile.class, dataPrependedRelPath, "PRESERVED");
		} else {
			preserveFile();
		}

		return null;
	}

	private void preserveFile() {
		try {
			Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"));
			PreservationFormatConverter pfc = new PreservationFormatConverter(this.absFilepath.toFile(),
					tempDir.toFile());
			NormaliserResults results = pfc.convert();
			if (results != null) {
				Path convertedFileInTemp = Paths.get(results.getDestinationDirString(), results.getOutputFileName());

				String presvRelpath = this.dataPrependedRelPath
						.toString()
						.replaceFirst("data/", PRESERVATION_PATH)
						.replaceFirst(this.absFilepath.getFileName().toString() + "$",
								results.getOutputFileName());
				this.dcStorageSvc.addHiddenFile(pid, convertedFileInTemp.toFile(), presvRelpath.replaceFirst("data/", ""));
				tagFilesSvc.addEntry(pid, PreservationMapTagFile.class, dataPrependedRelPath, presvRelpath);
			} else {
				tagFilesSvc.addEntry(pid, PreservationMapTagFile.class, this.dataPrependedRelPath, "UNCONVERTIBLE");
			}
		} catch (IOException e) {
			LOGGER.warn("Unable to create preservation format for {}/{}. Exception: {}", pid,
					this.dataPrependedRelPath, e.getMessage());
		}

	}
}
