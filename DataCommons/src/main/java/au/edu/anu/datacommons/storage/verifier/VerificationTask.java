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

import static java.text.MessageFormat.format;
import gov.loc.repository.bagit.Manifest.Algorithm;
import gov.loc.repository.bagit.utilities.FilenameHelper;
import gov.loc.repository.bagit.utilities.MessageDigestHelper;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.storage.info.FileInfo;
import au.edu.anu.datacommons.storage.info.FileInfo.Type;
import au.edu.anu.datacommons.storage.provider.StorageProvider;
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
 * Task class that verifies the integrity of a Bag as per the BagIt specification.
 * <p>
 * <em>This class only </em>
 * 
 * @author Rahul Khanna
 * 
 */
public class VerificationTask implements Callable<VerificationResults> {
	private static final Logger LOGGER = LoggerFactory.getLogger(VerificationTask.class);

	private String pid;
	private StorageProvider sp;
	private TagFilesService tagFilesSvc;
	private ThreadPoolService threadPoolSvc;
	
	private VerificationResults results;
	private Set<FileInfo> payloadFiles = new HashSet<>();
	private static final List<Class<? extends AbstractKeyValueFile>> tagFilesClasses = new ArrayList<>();

	// private Map<Path, String> tagFiles = new HashMap<>();
	// private Map<Path, String> manifests = new HashMap<>();

	static {
		tagFilesClasses.add(PronomFormatsTagFile.class);
		tagFilesClasses.add(VirusScanTagFile.class);
		tagFilesClasses.add(FileMetadataTagFile.class);
		tagFilesClasses.add(TimestampsTagFile.class);
		tagFilesClasses.add(PreservationMapTagFile.class);
	}


	/**
	 * Creates an instance of this class that can be submitted to a thread pool for processing in another thread.
	 * 
	 * @param pid
	 *            Identifier of collection record
	 * @param bagDir
	 *            Bag directory
	 * @param tagFilesSvc
	 *            Tag Files Service
	 * @param threadPoolSvc
	 *            Thread Pool to which subtasks can be submitted
	 */
	public VerificationTask(String pid, StorageProvider sp, TagFilesService tagFilesSvc, ThreadPoolService threadPoolSvc) {
		this.pid = pid;
		this.sp = sp;
		this.tagFilesSvc = tagFilesSvc;
		this.threadPoolSvc = threadPoolSvc;
		
		this.results = new VerificationResults(this.pid);
	}

	@Override
	public VerificationResults call() throws Exception {
		FileInfo dirInfo = sp.getDirInfo(pid, "", Integer.MAX_VALUE);
		
//		tagFiles = enumerateFiles(bagDir, true);
		payloadFiles = dirInfo.getChildrenRecursive();
//		iterateManifests();
//		
		validateTagFiles();
		validatePayloadManifests();
		validateChecksums();
		checkArtifacts();
		return results;
	}

	/**
	 * Enumerates all the payload manifests in a bag. 
	 */
//	private void iterateManifests() {
//		for (Entry<Path, String> tagFileEntry : tagFiles.entrySet()) {
//			if (tagFileEntry.getValue().startsWith("manifest-")) {
//				manifests.put(tagFileEntry.getKey(), tagFileEntry.getValue());
//			}
//		}
//	}

	/**
	 * Enumerates files in a specified directory.
	 * 
	 * @param rootDir
	 *            Directory to start walking
	 * @param exclDataDir
	 *            true if the data directory (payload directory) should be excluded
	 * @return Map Path as keys and relative path with normalized path separators as values
	 * @throws IOException
	 */
//	private Map<Path, String> enumerateFiles(Path rootDir, boolean exclDataDir) throws IOException {
//		Map<Path, String> files = new HashMap<Path, String>();
//		if (Files.isDirectory(rootDir)) {
//			try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(rootDir)) {
//				for (Path dirEntry : dirStream) {
//					if (Files.isDirectory(dirEntry)) {
//						if (!(exclDataDir && dirEntry.getFileName().toString().equals("data"))) {
//							files.putAll(enumerateFiles(dirEntry, false));
//						}
//					} else if (Files.isRegularFile(dirEntry)) {
//						files.put(dirEntry,
//								FilenameHelper.normalizePathSeparators(bagDir.relativize(dirEntry).toString()));
//					} else {
//						addEntry(Severity.WARN, Category.OTHER,
//								FilenameHelper.normalizePathSeparators(bagDir.relativize(dirEntry).toString()),
//								"Unexpected item found");
//					}
//				}
//			}
//		}
//		return files;
//	}
	
	/**
	 * Verifies entries in each tag file by checking that a payload file exists for each entry and an entry exists
	 * for each payload file.
	 */
	private void validateTagFiles() {
		// Verify each payload file has a corresponding entry in each custom tag file.
		for (FileInfo plFile : payloadFiles) {
			if (plFile.getType().equals(Type.FILE)) {
				for (Class<? extends AbstractKeyValueFile> clazz : tagFilesClasses) {
					try {
						String tagFilename = (String) clazz.getField("FILEPATH").get(clazz);
						if (!tagFilesSvc.containsKey(pid, clazz, "data/" + plFile.getRelFilepath())) {
							addEntry(Severity.WARN, Category.TAGFILE_ENTRY_MISSING, plFile.getRelFilepath(),
									format("No entry in {0}", tagFilename));
						}
					} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException
							| IOException e) {
						addEntry(Severity.ERROR, Category.VALIDATION_EXCEPTION, clazz.getSimpleName(),
								format("Exception on read: {0}", e.getMessage()));
					}
				}
			}
		}

		// Verify each tag file entry has a corresponding payload file
		for (Class<? extends AbstractKeyValueFile> clazz : tagFilesClasses) {
			try {
				String tagFilename = (String) clazz.getField("FILEPATH").get(clazz);
				for (String tagFileKey : tagFilesSvc.getAllEntries(pid, clazz).keySet()) {
					if (!sp.fileExists(pid, tagFileKey.replaceFirst("^data/", ""))) {
						addEntry(Severity.WARN, Category.PAYLOADFILE_NOTFOUND, tagFileKey,
								format("{0} refers to missing file", tagFilename));
					}
				}
			} catch (IOException | IllegalArgumentException | IllegalAccessException | NoSuchFieldException
					| SecurityException e) {
				addEntry(Severity.ERROR, Category.VALIDATION_EXCEPTION, clazz.getSimpleName(),
						format("Exception on read: {0}", e.getMessage()));
			}
		}
	}

	/**
	 * Checks if the bag contains artifacts from previous storage formats.
	 */
	private void checkArtifacts() {
		for (FileInfo plFile : payloadFiles) {
			if (plFile.getType().equals(Type.DIR) && plFile.getRelFilepath().equals("metadata")) {
				addEntry(Severity.WARN, Category.ARTIFACT_FOUND, "metadata/", null);
			}
		}

	}

	/**
	 * Validates the entries in payload manifest.
	 */
	private void validatePayloadManifests() {
		try {
			Map<String, String> md5Entries = tagFilesSvc.getAllEntries(pid, ManifestMd5TagFile.class);

			for (String plFilepath : md5Entries.keySet()) {

				boolean plFileExists = false;
				for (FileInfo plFile : payloadFiles) {
					if (plFile.getType().equals(Type.FILE) && plFilepath.equals("data/" + plFile.getRelFilepath())) {
						plFileExists = true;
						break;
					}
				}

				if (!plFileExists) {
					addEntry(Severity.ERROR, Category.PAYLOADFILE_NOTFOUND, plFilepath,
							format("{0} refers to missing file", ManifestMd5TagFile.FILEPATH));
				}
			}
			for (FileInfo plFilepath : payloadFiles) {
				if (plFilepath.getType().equals(Type.FILE)
						&& !md5Entries.keySet().contains("data/" + plFilepath.getRelFilepath())) {
					addEntry(Severity.ERROR, Category.MANIFEST_ENTRY_MISSING, plFilepath.getRelFilepath(),
							format("{0} refers to missing file", ManifestMd5TagFile.FILEPATH));
				}
			}

		} catch (IOException e) {
			addEntry(Severity.ERROR, Category.VALIDATION_EXCEPTION, ManifestMd5TagFile.FILEPATH,
					format("Exception on read: {0}", e.getMessage()));
		}
	}
	
	/**
	 * Checks that the entries in the payload manifest actually match the contents of the respective payload file.
	 * 
	 * @throws IOException
	 */
	private void validateChecksums() throws IOException {
		Map<String, String> expectedMd5Map = tagFilesSvc.getAllEntries(pid, ManifestMd5TagFile.class);
		Map<String, Future<String>> calculatedFixityMap = new HashMap<>();
		for (FileInfo plFile : payloadFiles) {
			if (expectedMd5Map.get("data/" + plFile.getRelFilepath()) != null) {
				final Path fPlPath = plFile.getPath();
				calculatedFixityMap.put("data/" + plFile.getRelFilepath(), threadPoolSvc.submit(new Callable<String>() {
					
					@Override
					public String call() throws Exception {
						return MessageDigestHelper.generateFixity(fPlPath.toFile(), Algorithm.MD5);
					}
					
				}));
			}
		}
		
		for (Entry<String, Future<String>> entry : calculatedFixityMap.entrySet()) {
			String dataRelPath = entry.getKey();
			String expectedFixity = expectedMd5Map.get(dataRelPath);
			try {
				String calculatedFixity = entry.getValue().get();
				if (expectedMd5Map.containsKey(dataRelPath) && !expectedMd5Map.get(dataRelPath).equals(calculatedFixity)) {
					addEntry(
							Severity.ERROR,
							Category.CHECKSUM_MISMATCH,
							dataRelPath,
							format("Computed MD5 {0} does not match {1}", calculatedFixity, expectedFixity));
				}
			} catch (InterruptedException | ExecutionException e) {
				addEntry(Severity.ERROR, Category.VALIDATION_EXCEPTION, ManifestMd5TagFile.FILEPATH,
						format("Exception on read: {0}", e.getMessage()));
			}
		}
	}

	/**
	 * Adds an issue entry to results.
	 * 
	 * @param severity
	 *            Severity of the issue
	 * @param category
	 *            Category of the issue
	 * @param filepath
	 *            Filepath related to the issue
	 * @param msg
	 *            Message describing the issue
	 */
	private synchronized void addEntry(Severity severity, Category category, String filepath, String msg) {
		this.results.addMessage(new ResultMessage(severity, category, filepath, msg == null ? "" : msg));
		LOGGER.trace("{}-{}: [{}] {}", severity.toString(), category.toString(), filepath, msg == null ? "" : msg);
	}

}
