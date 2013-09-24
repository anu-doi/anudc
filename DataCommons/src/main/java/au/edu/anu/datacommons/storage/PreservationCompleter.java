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

import static java.text.MessageFormat.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.properties.GlobalProps;
import au.edu.anu.datacommons.storage.completer.preserve.PreservationFormatConverter;
import au.edu.anu.datacommons.storage.tagfiles.PreservationMapTagFile;
import au.gov.naa.digipres.xena.kernel.XenaException;
import au.gov.naa.digipres.xena.kernel.normalise.NormaliserResults;
import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFile;
import gov.loc.repository.bagit.transformer.Completer;
import gov.loc.repository.bagit.utilities.FilenameHelper;

/**
 * @author Rahul Khanna
 *
 */
public class PreservationCompleter extends AbstractCustomCompleter {
	private static final Logger LOGGER = LoggerFactory.getLogger(PreservationCompleter.class);
	
	public static final String PRESERVATION_PATH = "data/.preserve/";
	
	@Override
	public Bag complete(Bag bag) {
		try {
			bag = handlePreservation(bag);
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return bag;
	}

	private Bag handlePreservation(Bag bag) throws IOException {
		PreservationMapTagFile presvTagFile = new PreservationMapTagFile(bag.getFile());
		if (this.limitAddUpdatePayloadFilepaths == null && this.limitDeletePayloadFilepaths == null) {
			presvTagFile.clear();
			File presvDir = new File(bag.getFile(), PRESERVATION_PATH);
			if (presvDir.isDirectory()) {
				FileUtils.deleteDirectory(presvDir);
			}
		} else if (this.limitDeletePayloadFilepaths != null) {
			deletePreservedFiles(bag, presvTagFile);
		}

		createPreservedFiles(bag, presvTagFile);

		presvTagFile.write();
		synchronized (bag) {
			bag.loadFromFiles();
			bag.addFileAsTag(presvTagFile.getFile());
		}
		return bag;
	}

	/**
	 * Deletes preserved files. For complete re-preservation of all payload files, all tag entries from preservation tag
	 * file and all preserved file in the preservation folder is deleted.
	 * 
	 * @param bag
	 * @param presvTagFile
	 */
	private void deletePreservedFiles(Bag bag, PreservationMapTagFile presvTagFile) {
		List<String> preservedFilepathsToDelete = new ArrayList<String>(); 
		for (String filepath : this.limitDeletePayloadFilepaths) {
			if (presvTagFile.containsKey(filepath)) {
				String preservedFilepath = presvTagFile.get(filepath);
				if (preservedFilepath != null) {
					File preservedFile = new File(bag.getFile(), preservedFilepath);
					if (preservedFile.isFile()) {
						if (preservedFile.delete()) {
							preservedFilepathsToDelete.add(preservedFilepath);
						} else {
							LOGGER.warn("Unable to delete preserved file {}", preservedFile.getAbsolutePath());
						}
					}
					presvTagFile.remove(preservedFilepath);
				}
				presvTagFile.remove(filepath);
			}
		}
		this.limitDeletePayloadFilepaths.addAll(preservedFilepathsToDelete);
	}

	/**
	 * Converts added payload files to a preserved format, if possible. If a file cannot be converted, no
	 * prevervation-format file is created and the tag file marks the payload file as UNCONVERTABLE. If a
	 * preservation-format file is created, an entry is added in the preservation tag file with its value referring to
	 * the relative filepath of the converted file that's stored in data/.preserve/ directory.
	 * 
	 * @param bag
	 * @param presvTagFile
	 * @throws IOException
	 *             when unable to save created preserved-format file to disk.
	 */
	private void createPreservedFiles(Bag bag, PreservationMapTagFile presvTagFile) throws IOException {
		File presvTempDir = new File(GlobalProps.getUploadDirAsFile(), bag.getFile().getName());
		createDir(presvTempDir);
		List<File> preservedFiles = new ArrayList<File>();
		for (BagFile iBagFile : bag.getPayload()) {
			if (isLimited(this.limitAddUpdatePayloadFilepaths, iBagFile.getFilepath())) {
				File input = new File(bag.getFile(), iBagFile.getFilepath());
				try {
					PreservationFormatConverter pfc = new PreservationFormatConverter(input, presvTempDir);
					NormaliserResults results = pfc.convert();
					if (results != null) {
						File convertedFileInTemp = new File(results.getDestinationDirString(),
								results.getOutputFileName());
						String convertedFileInPresvFilepath = iBagFile.getFilepath()
								.replaceFirst("data/", PRESERVATION_PATH)
								.replaceFirst(input.getName() + "$", results.getOutputFileName());
						File convertedFileInPresv = new File(bag.getFile(), convertedFileInPresvFilepath);
						createDir(convertedFileInPresv.getParentFile());
						if (convertedFileInTemp.renameTo(convertedFileInPresv)) {
							String preservedFilepath = FilenameHelper.removeBasePath(bag.getFile().getAbsolutePath(),
									convertedFileInPresv.getAbsolutePath());
							preservedFiles.add(convertedFileInPresv);
							if (this.limitAddUpdatePayloadFilepaths != null) {
								this.limitAddUpdatePayloadFilepaths.add(preservedFilepath);
							}
							presvTagFile.put(iBagFile.getFilepath(), preservedFilepath);
							presvTagFile.put(convertedFileInPresvFilepath, "PRESERVED");
						} else {
							LOGGER.warn("Unable to move {} to {}", convertedFileInTemp.getAbsolutePath(),
									convertedFileInPresv.getAbsolutePath());
						}
					} else {
						presvTagFile.put(iBagFile.getFilepath(), "UNCONVERTIBLE");
					}
				} catch (IOException e) {
					LOGGER.warn("Unable to create preservation format for {}. Exception: {}", input.getAbsolutePath(),
							e.getMessage());
				}
			}
		}

		if (presvTempDir.isDirectory()) {
			if (!presvTempDir.delete()) {
				presvTempDir.deleteOnExit();
			}
		}
	}

	private void createDir(File dir) throws IOException {
		if (!dir.isDirectory()) {
			if (!dir.mkdirs()) {
				throw new IOException(format("Unable to create directory {0}", dir.getAbsolutePath()));
			}
		}
	}
}
