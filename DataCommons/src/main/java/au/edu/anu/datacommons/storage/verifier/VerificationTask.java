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

package au.edu.anu.datacommons.storage.verifier;

import static java.text.MessageFormat.*;
import gov.loc.repository.bagit.Manifest.Algorithm;
import gov.loc.repository.bagit.utilities.FilenameHelper;
import gov.loc.repository.bagit.utilities.MessageDigestHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.storage.tagfiles.AbstractKeyValueFile;
import au.edu.anu.datacommons.storage.tagfiles.FileMetadataTagFile;
import au.edu.anu.datacommons.storage.tagfiles.PreservationMapTagFile;
import au.edu.anu.datacommons.storage.tagfiles.PronomFormatsTagFile;
import au.edu.anu.datacommons.storage.tagfiles.TimestampsTagFile;
import au.edu.anu.datacommons.storage.tagfiles.VirusScanTagFile;
import au.edu.anu.datacommons.storage.verifier.ResultMessage.Category;
import au.edu.anu.datacommons.storage.verifier.ResultMessage.Severity;

/**
 * @author Rahul Khanna
 * 
 */
public class VerificationTask implements Callable<VerificationResults> {
	private static final Logger LOGGER = LoggerFactory.getLogger(VerificationTask.class);

	private File bagDir;
	private VerificationResults results;
	private Map<File, String> payloadFiles = new HashMap<File, String>();
	private Map<File, String> tagFiles = new HashMap<File, String>();
	private Map<File, String> manifests = new HashMap<File, String>();

	public VerificationTask(File bagDir) {
		this.bagDir = bagDir;
		this.results = new VerificationResults(bagDir.getName());
	}

	@Override
	public VerificationResults call() throws Exception {
		iterateTagFiles();
		iteratePayloadFiles();
		iterateManifests();
		validateTagFiles();
		validatePayloadManifests();
		validateChecksums();
		checkArtifacts();

		return results;
	}

	private void iterateTagFiles() {
		addFiles(bagDir, tagFiles, true);
	}

	private void iteratePayloadFiles() {
		addFiles(new File(bagDir, "data/"), payloadFiles, false);
	}

	private void iterateManifests() {
		for (Entry<File, String> tagFileEntry : tagFiles.entrySet()) {
			if (tagFileEntry.getValue().startsWith("manifest-")) {
				manifests.put(tagFileEntry.getKey(), tagFileEntry.getValue());
			}
		}
	}

	private void addFiles(File rootDir, Map<File, String> files, boolean exclDataDir) {
		File[] filesAndDirs = rootDir.listFiles();
		for (File iFileOrDir : filesAndDirs) {
			if (iFileOrDir.isDirectory()) {
				if (!(exclDataDir && iFileOrDir.getName().equals("data"))) {
					addFiles(iFileOrDir, files, false);
				}
			} else if (iFileOrDir.isFile()) {
				files.put(iFileOrDir,
						FilenameHelper.removeBasePath(bagDir.getAbsolutePath(), iFileOrDir.getAbsolutePath()));
			} else {
				addEntry(Severity.WARN, Category.OTHER,
						FilenameHelper.removeBasePath(bagDir.getAbsolutePath(), iFileOrDir.getAbsolutePath()),
						"Unexpected item found");
			}
		}
	}

	private void validateTagFiles() {
		List<Class<? extends AbstractKeyValueFile>> tagFilesClasses = new ArrayList<Class<? extends AbstractKeyValueFile>>();
		tagFilesClasses.add(PronomFormatsTagFile.class);
		tagFilesClasses.add(VirusScanTagFile.class);
		tagFilesClasses.add(FileMetadataTagFile.class);
		tagFilesClasses.add(TimestampsTagFile.class);
		tagFilesClasses.add(PreservationMapTagFile.class);
		List<AbstractKeyValueFile> customTagFiles = new ArrayList<AbstractKeyValueFile>();

		for (Class<? extends AbstractKeyValueFile> c : tagFilesClasses) {
			try {
				AbstractKeyValueFile tagFile = c.getConstructor(File.class).newInstance(bagDir);
				if (tagFile.getFile().isFile()) {
					customTagFiles.add(tagFile);
				} else {
					addEntry(Severity.ERROR, Category.TAGFILE_NOTFOUND, tagFile.getFile().getName(), null);
				}
			} catch (Exception e) {
				addEntry(Severity.ERROR, Category.VALIDATION_EXCEPTION, c.getName(),
						format("Exception on read: {0}", e.getMessage()));
			}
		}

		// Verify each payload file has a corresponding entry in each custom tag file.
		for (Entry<File, String> plFile : payloadFiles.entrySet()) {
			for (AbstractKeyValueFile tagFile : customTagFiles) {
				if (!tagFile.containsKey(plFile.getValue())) {
					addEntry(Severity.WARN, Category.TAGFILE_ENTRY_MISSING, plFile.getValue(),
							format("No entry in {0}", tagFile.getFile().getName()));
				}
			}
		}

		// Verify that there's a payload file for each entry in each custom manifest.
		for (AbstractKeyValueFile ctf : customTagFiles) {
			for (String key : ctf.keySet()) {
				if (!payloadFiles.values().contains(key)) {
					addEntry(Severity.WARN, Category.PAYLOADFILE_NOTFOUND, key,
							format("{0} refers to missing file", ctf.getFile().getName()));
				}
			}
		}
	}

	private void checkArtifacts() {
		for (Entry<File, String> tagFile : tagFiles.entrySet()) {
			if (tagFile.getValue().startsWith("metadata/")) {
				addEntry(Severity.WARN, Category.ARTIFACT_FOUND, tagFile.getValue(), null);
			}
		}
		if (new File(bagDir, "metadata/").isDirectory()) {
			addEntry(Severity.WARN, Category.ARTIFACT_FOUND, "metadata/", null);
		}

	}

	private void validatePayloadManifests() {
		for (Entry<File, String> manifestTagFile : manifests.entrySet()) {
			try {
				ManifestTagFile manifest = new ManifestTagFile(manifestTagFile.getKey());

				for (String plFilepath : manifest.keySet()) {
					if (!payloadFiles.values().contains(plFilepath)) {
						addEntry(Severity.ERROR, Category.PAYLOADFILE_NOTFOUND, plFilepath,
								format("{0} refers to missing file", manifestTagFile.getValue()));
					}
				}
				for (String plFilepath : payloadFiles.values()) {
					if (!manifest.keySet().contains(plFilepath)) {
						addEntry(Severity.ERROR, Category.MANIFEST_ENTRY_MISSING, plFilepath,
								format("{0} refers to missing file", manifestTagFile.getValue()));
					}
				}
			} catch (IOException e) {
				addEntry(Severity.ERROR, Category.VALIDATION_EXCEPTION, manifestTagFile.getKey().getName(),
						format("Exception on read: {0}", e.getMessage()));
			}
		}
	}
	
	private void validateChecksums() {
		ExecutorService execSvc = Executors.newFixedThreadPool(8);
		List<Future<?>> tasks = new ArrayList<Future<?>>();
		for (Entry<File, String> manifestTagFile : manifests.entrySet()) {
			String manifestFilename = manifestTagFile.getKey().getName();
			String bagItAlgorithm = manifestFilename.substring(manifestFilename.lastIndexOf('-') + 1,
					manifestFilename.lastIndexOf('.'));
			final Algorithm alg = Algorithm.valueOfBagItAlgorithm(bagItAlgorithm);
			ManifestTagFile manifest;
			try {
				manifest = new ManifestTagFile(manifestTagFile.getKey());
				for (Entry<File, String> plFile : payloadFiles.entrySet()) {
					final String expectedFixity = manifest.get(plFile.getValue());
					final Entry<File, String> fPlFile = plFile;
					if (expectedFixity != null) {
						
						tasks.add(execSvc.submit(new Runnable() {

							@Override
							public void run() {
								String fixity = MessageDigestHelper.generateFixity(fPlFile.getKey(), alg);
								if (!fixity.equals(expectedFixity)) {
									addEntry(
											Severity.ERROR,
											Category.CHECKSUM_MISMATCH,
											fPlFile.getValue(),
											format("Computed {0} {1} does not match {2}", alg.javaSecurityAlgorithm, fixity,
													expectedFixity));
								}
							}
							
						}));
					}
				}
			} catch (IOException e) {
				addEntry(Severity.ERROR, Category.VALIDATION_EXCEPTION, manifestTagFile.getKey().getName(),
						format("Exception on read: {0}", e.getMessage()));
			}
		}
		for (Future<?> f : tasks) {
			try {
				f.get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		execSvc.shutdown();
	}

	private synchronized void addEntry(Severity severity, Category category, String filepath, String msg) {
		this.results.addMessage(new ResultMessage(severity, category, filepath, msg == null ? "" : msg));
		LOGGER.trace("{}-{}: [{}] {}", severity.toString(), category.toString(), filepath, msg == null ? "" : msg);
	}

}
