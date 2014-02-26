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
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
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

import javax.xml.crypto.dsig.Manifest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.storage.filesystem.FileFactory;
import au.edu.anu.datacommons.storage.tagfiles.AbstractKeyValueFile;
import au.edu.anu.datacommons.storage.tagfiles.FileMetadataTagFile;
import au.edu.anu.datacommons.storage.tagfiles.ManifestMd5TagFile;
import au.edu.anu.datacommons.storage.tagfiles.PreservationMapTagFile;
import au.edu.anu.datacommons.storage.tagfiles.PronomFormatsTagFile;
import au.edu.anu.datacommons.storage.tagfiles.TagFilesService;
import au.edu.anu.datacommons.storage.tagfiles.TimestampsTagFile;
import au.edu.anu.datacommons.storage.tagfiles.VirusScanTagFile;
import au.edu.anu.datacommons.storage.verifier.ResultMessage.Category;
import au.edu.anu.datacommons.storage.verifier.ResultMessage.Severity;
import au.edu.anu.datacommons.tasks.ThreadPoolService;

/**
 * @author Rahul Khanna
 * 
 */
public class VerificationTask implements Callable<VerificationResults> {
	private static final Logger LOGGER = LoggerFactory.getLogger(VerificationTask.class);

	private Path bagDir;
	private String pid;
	private TagFilesService tagFilesSvc;
	private ThreadPoolService threadPoolSvc;
	
	private VerificationResults results;
	private Map<Path, String> payloadFiles = new HashMap<>();
	private Map<Path, String> tagFiles = new HashMap<>();
	private Map<Path, String> manifests = new HashMap<>();


	public VerificationTask(String pid, Path bagDir, TagFilesService tagFilesSvc, ThreadPoolService threadPoolSvc) {
		this.pid = pid;
		this.bagDir = bagDir;
		this.tagFilesSvc = tagFilesSvc;
		this.threadPoolSvc = threadPoolSvc;
		
		this.results = new VerificationResults(bagDir.getFileName().toString());
	}

	@Override
	public VerificationResults call() throws Exception {
		tagFiles = enumerateFiles(bagDir, true);
		payloadFiles = enumerateFiles(bagDir.resolve("data/"), false);
		iterateManifests();
		
		validateTagFiles();
		validatePayloadManifests();
		validateChecksums();
		
		checkArtifacts();

		return results;
	}

	private void iterateManifests() {
		for (Entry<Path, String> tagFileEntry : tagFiles.entrySet()) {
			if (tagFileEntry.getValue().startsWith("manifest-")) {
				manifests.put(tagFileEntry.getKey(), tagFileEntry.getValue());
			}
		}
	}

	private Map<Path, String> enumerateFiles(Path rootDir, boolean exclDataDir) throws IOException {
		Map<Path, String> files = new HashMap<Path, String>();
		if (Files.isDirectory(rootDir)) {
			try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(rootDir)) {
				for (Path dirEntry : dirStream) {
					if (Files.isDirectory(dirEntry)) {
						if (!(exclDataDir && dirEntry.getFileName().toString().equals("data"))) {
							files.putAll(enumerateFiles(dirEntry, false));
						}
					} else if (Files.isRegularFile(dirEntry)) {
						files.put(dirEntry, FilenameHelper.normalizePathSeparators(bagDir.relativize(dirEntry).toString()));
					} else {
						addEntry(Severity.WARN, Category.OTHER,
								FilenameHelper.normalizePathSeparators(bagDir.relativize(dirEntry).toString()),
								"Unexpected item found");
					}
				}
			}
		}
		return files;
	}
	
	private void validateTagFiles() {
		List<Class<? extends AbstractKeyValueFile>> tagFilesClasses = new ArrayList<Class<? extends AbstractKeyValueFile>>();
		tagFilesClasses.add(PronomFormatsTagFile.class);
		tagFilesClasses.add(VirusScanTagFile.class);
		tagFilesClasses.add(FileMetadataTagFile.class);
		tagFilesClasses.add(TimestampsTagFile.class);
		tagFilesClasses.add(PreservationMapTagFile.class);
		
		// Verify each payload file has a corresponding entry in each custom tag file.
		for (Entry<Path, String> plFile : payloadFiles.entrySet()) {
			for (Class<? extends AbstractKeyValueFile> clazz : tagFilesClasses) {
				try {
					String tagFilename = (String) clazz.getField("FILEPATH").get(clazz);
					if (!tagFilesSvc.containsKey(pid, clazz, plFile.getValue())) {
						addEntry(Severity.WARN, Category.TAGFILE_ENTRY_MISSING, plFile.getValue(), format("No entry in {0}", tagFilename));
					}
				} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException | IOException e) {
					addEntry(Severity.ERROR, Category.VALIDATION_EXCEPTION, clazz.getSimpleName(), format("Exception on read: {0}", e.getMessage()));
				}
			}
		}
		
		for (Class<? extends AbstractKeyValueFile> clazz : tagFilesClasses) {
			try {
				String tagFilename = (String) clazz.getField("FILEPATH").get(clazz);
				for (String tagFileKey : tagFilesSvc.getAllEntries(pid, clazz).keySet()) {
					if (!payloadFiles.values().contains(tagFileKey)) {
						addEntry(Severity.WARN, Category.PAYLOADFILE_NOTFOUND, tagFileKey,
								format("{0} refers to missing file", tagFilename));
					}
				}
			} catch (IOException | IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
				addEntry(Severity.ERROR, Category.VALIDATION_EXCEPTION, clazz.getSimpleName(), format("Exception on read: {0}", e.getMessage()));
			}
		}
	}

	private void checkArtifacts() {
		for (Entry<Path, String> tagFile : tagFiles.entrySet()) {
			if (tagFile.getValue().startsWith("metadata/")) {
				addEntry(Severity.WARN, Category.ARTIFACT_FOUND, tagFile.getValue(), null);
			}
		}
		if (Files.isDirectory(bagDir.resolve("data/").resolve("metadata/"))) {
			addEntry(Severity.WARN, Category.ARTIFACT_FOUND, "metadata/", null);
		}

	}

	private void validatePayloadManifests() {
		try {
			Map<String, String> md5Entries = tagFilesSvc.getAllEntries(pid, ManifestMd5TagFile.class);

			for (String plFilepath : md5Entries.keySet()) {
				if (!payloadFiles.values().contains(plFilepath)) {
					addEntry(Severity.ERROR, Category.PAYLOADFILE_NOTFOUND, plFilepath,
							format("{0} refers to missing file", ManifestMd5TagFile.FILEPATH));
				}
			}
			for (String plFilepath : payloadFiles.values()) {
				if (!md5Entries.keySet().contains(plFilepath)) {
					addEntry(Severity.ERROR, Category.MANIFEST_ENTRY_MISSING, plFilepath,
							format("{0} refers to missing file", ManifestMd5TagFile.FILEPATH));
				}
			}

		} catch (IOException e) {
			addEntry(Severity.ERROR, Category.VALIDATION_EXCEPTION, ManifestMd5TagFile.FILEPATH,
					format("Exception on read: {0}", e.getMessage()));
		}
	}
	
	private void validateChecksums() throws IOException {
		Map<String, String> expectedMd5Map = tagFilesSvc.getAllEntries(pid, ManifestMd5TagFile.class);
		Map<String, Future<String>> calculatedFixityMap = new HashMap<>();
		for (Entry<Path, String> plFile : payloadFiles.entrySet()) {
			if (expectedMd5Map.get(plFile.getValue()) != null) {
				final Path fPlPath = plFile.getKey();
				calculatedFixityMap.put(plFile.getValue(), threadPoolSvc.submit(new Callable<String>() {
					
					@Override
					public String call() throws Exception {
						return MessageDigestHelper.generateFixity(fPlPath.toFile(), Algorithm.MD5);
					}
					
				}));
			}
		}
		
		for (Entry<String, Future<String>> entry : calculatedFixityMap.entrySet()) {
			String relPath = entry.getKey();
			String expectedFixity = expectedMd5Map.get(relPath);
			try {
				String calculatedFixity = entry.getValue().get();
				if (expectedMd5Map.containsKey(relPath) && !expectedMd5Map.get(relPath).equals(calculatedFixity)) {
					addEntry(
							Severity.ERROR,
							Category.CHECKSUM_MISMATCH,
							relPath,
							format("Computed MD5 {0} does not match {1}", calculatedFixity, expectedFixity));
				}
			} catch (InterruptedException | ExecutionException e) {
				addEntry(Severity.ERROR, Category.VALIDATION_EXCEPTION, ManifestMd5TagFile.FILEPATH,
						format("Exception on read: {0}", e.getMessage()));
			}
		}
	}

	private synchronized void addEntry(Severity severity, Category category, String filepath, String msg) {
		this.results.addMessage(new ResultMessage(severity, category, filepath, msg == null ? "" : msg));
		LOGGER.trace("{}-{}: [{}] {}", severity.toString(), category.toString(), filepath, msg == null ? "" : msg);
	}

}
