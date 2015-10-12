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

package au.edu.anu.datacommons.storage.controller;

import static java.text.MessageFormat.format;
import gov.loc.repository.bagit.Manifest;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import au.edu.anu.datacommons.storage.datafile.StagedDataFile;
import au.edu.anu.datacommons.storage.event.StorageEvent;
import au.edu.anu.datacommons.storage.event.StorageEvent.EventType;
import au.edu.anu.datacommons.storage.event.StorageEventListener;
import au.edu.anu.datacommons.storage.event.StorageEventListener.EventTime;
import au.edu.anu.datacommons.storage.info.FileInfo;
import au.edu.anu.datacommons.storage.info.FileInfo.Type;
import au.edu.anu.datacommons.storage.info.PronomFormat;
import au.edu.anu.datacommons.storage.info.RecordDataInfoService;
import au.edu.anu.datacommons.storage.info.RecordDataSummary;
import au.edu.anu.datacommons.storage.provider.StorageException;
import au.edu.anu.datacommons.storage.provider.StorageProvider;
import au.edu.anu.datacommons.storage.provider.StorageProviderResolver;
import au.edu.anu.datacommons.storage.search.StorageSearchService;
import au.edu.anu.datacommons.storage.tagfiles.ExtRefsTagFile;
import au.edu.anu.datacommons.storage.tagfiles.FileMetadataTagFile;
import au.edu.anu.datacommons.storage.tagfiles.ManifestMd5TagFile;
import au.edu.anu.datacommons.storage.tagfiles.PreservationMapTagFile;
import au.edu.anu.datacommons.storage.tagfiles.PronomFormatsTagFile;
import au.edu.anu.datacommons.storage.tagfiles.TagFilesService;
import au.edu.anu.datacommons.storage.tagfiles.VirusScanTagFile;
import au.edu.anu.datacommons.storage.verifier.VerificationResults;
import au.edu.anu.datacommons.storage.verifier.VerificationTask;
import au.edu.anu.datacommons.tasks.ThreadPoolService;

/**
 * @author Rahul Khanna
 *
 */
public class StorageControllerImpl implements StorageController {
	private static final Logger LOGGER = LoggerFactory.getLogger(StorageControllerImpl.class);
	
	@Autowired
	private StorageProviderResolver providerResolver;

	@Autowired(required = true)
	private RecordDataInfoService rdiSvc;
	
	@Autowired
	private Set<StorageEventListener> eventListeners;
	@Autowired(required = true)
	private ThreadPoolService threadPoolSvc;
	@Autowired
	private StorageSearchService searchSvc;
	@Autowired(required = true)
	private TagFilesService tagFilesSvc;

	
	@Override
	public FileInfo addFile(String pid, String filepath, StagedDataFile source) throws IOException, StorageException {
		// Validation
		pid = validatePid(pid);
		filepath = validateRelPath(filepath, false);
		Objects.requireNonNull(source);
		
		StorageProvider sp = providerResolver.getStorageProvider(pid);
		EventType eventType;
		if (!sp.fileExists(pid, filepath)) {
			eventType = EventType.ADD_FILE;
		} else {
			eventType = EventType.UPDATE_FILE;
		}
		StorageEvent event = new StorageEvent(eventType, pid, sp, filepath, source);
		notifyListeners(EventTime.PRE, event);
		FileInfo addedFile = sp.addFile(pid, filepath, source);
		notifyListeners(EventTime.POST, event);
		return addedFile;
	}
	
	@Override
	public FileInfo addDir(String pid, String filepath) throws IOException, StorageException {
		// Validation
		pid = validatePid(pid);
		filepath = validateRelPath(filepath, false);

		StorageProvider sp = providerResolver.getStorageProvider(pid);
		return sp.addDir(pid, filepath);
	}
	
	@Override
	public void deleteFile(String pid, String filepath) throws IOException, StorageException {
		// Validation
		pid = validatePid(pid);
		filepath = validateRelPath(filepath, false);
		
		StorageProvider sp = providerResolver.getStorageProvider(pid);
		
		if (fileExists(pid, filepath)) {
			StorageEvent event = new StorageEvent(EventType.DELETE_FILE, pid, sp, filepath);
			notifyListeners(EventTime.PRE, event);
			// Checking again if file exists as it may have been deleted by the pre-event tasks.
			if (fileExists(pid, filepath)) {
				sp.deleteFile(pid, filepath);
			}
			notifyListeners(EventTime.POST, event);
		} else if (dirExists(pid, filepath)) {
			FileInfo fileInfo = sp.getDirInfo(pid, filepath, Integer.MAX_VALUE);
			
			// Delete only files in first pass as deleting folders can delete files within them.
			List<StorageEvent> deleteEvents = new ArrayList<>();
			for (FileInfo child : fileInfo.getChildrenRecursive()) {
				if (child.getType() == Type.FILE) {
					StorageEvent event = new StorageEvent(EventType.DELETE_FILE, pid, sp, child.getRelFilepath());
					deleteEvents.add(event);
					notifyListeners(EventTime.PRE, event);
					try {
						sp.deleteFile(pid, child.getRelFilepath());
					} catch (IOException e) {
						LOGGER.error("Unable to delete {}/{}", pid, child.getRelFilepath());
					}
				}
			}
			// Delete the dir and that will delete all folders within it. Files already deleted in the first pass.
			sp.deleteFile(pid, filepath);
			
			// Notify post event listeners after all files have been deleted so they don't start acting on files
			// still being deleted.
			for (StorageEvent event : deleteEvents) {
				notifyListeners(EventTime.POST, event);
			}
		} else {
			throw new FileNotFoundException(format("{0} does not contain {1}", pid, filepath));
		}
	}
	
	@Override
	public void renameFile(String pid, String oldFilepath, String newFilepath) throws IOException, StorageException {
		// Validation
		pid = validatePid(pid);
		oldFilepath = validateRelPath(oldFilepath, false);
		newFilepath = validateRelPath(newFilepath, false);
		
		StorageProvider sp = providerResolver.getStorageProvider(pid);
		
		StorageEvent event = new StorageEvent(EventType.RENAME_FILE, pid, sp, oldFilepath, newFilepath);
		notifyListeners(EventTime.PRE, event);
		sp.renameFile(pid, oldFilepath, newFilepath);
		notifyListeners(EventTime.POST, event);
	}
	
	@Override
	public boolean fileExists(String pid, String filepath) throws StorageException {
		// Validation
		pid = validatePid(pid);
		filepath = validateRelPath(filepath, true);

		StorageProvider sp = providerResolver.getStorageProvider(pid);
		return sp.fileExists(pid, filepath);
	}
	
	@Override
	public boolean dirExists(String pid, String filepath) throws StorageException {
		// Validation
		pid = validatePid(pid);
		filepath = validateRelPath(filepath, true);

		StorageProvider sp = providerResolver.getStorageProvider(pid);
		return sp.dirExists(pid, filepath);
	}
	
	@Override
	public InputStream getFileStream(String pid, String filepath) throws IOException, StorageException {
		// Validation
		pid = validatePid(pid);
		filepath = validateRelPath(filepath, true);

		StorageProvider sp = providerResolver.getStorageProvider(pid);
		return sp.readStream(pid, filepath);
	}
	
	@Override
	public RecordDataSummary getRecordDataSummary(String pid) throws IOException, StorageException {
		return rdiSvc.createRecordDataSummary(pid);
	}

	@Override
	public FileInfo getFileInfo(String pid, String filepath) throws IOException, StorageException {
		// Validation
		pid = validatePid(pid);
		filepath = validateRelPath(filepath, true);

		StorageProvider sp = providerResolver.getStorageProvider(pid);
		FileInfo fileInfo = sp.getDirInfo(pid, filepath, 1);
		addMetadata(pid, fileInfo);
		return fileInfo;
	}
	
	@Override
	public VerificationResults verifyIntegrity(String pid) throws IOException, StorageException {
		pid = validatePid(pid);
		StorageProvider sp = providerResolver.getStorageProvider(pid);
		VerificationTask vt = new VerificationTask(pid, sp, tagFilesSvc, threadPoolSvc);
		try {
			VerificationResults results = vt.call();
			return results;
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw new StorageException(e);
		}
		
	}
	
	@Override
	public void fixIntegrity(String pid) throws IOException, StorageException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Adds a reference to an external URL in a bag.
	 * 
	 * @param pid
	 *            Pid of a collection as String
	 * @param url
	 *            External URL as String
	 * @throws IOException
	 * @throws StorageException 
	 */
	public void addExtRefs(String pid, Collection<String> urls) throws IOException, StorageException {
		// Validation
		pid = validatePid(pid);
		validateNonEmptyCollection(urls);
		
		StorageProvider sp = providerResolver.getStorageProvider(pid);
		StorageEvent event = new StorageEvent(EventType.TAGFILE_UPDATE, pid, sp);
		notifyListeners(EventTime.PRE, event);
		for (String url : urls) {
			tagFilesSvc.addEntry(pid, ExtRefsTagFile.class, base64Encode(url), url);
		}
		notifyListeners(EventTime.POST, event);
	}

	/**
	 * Deletes an external reference in the bag of a specified record.
	 * 
	 * @param pid
	 *            Pid of the record whose bag contains the external reference to delete
	 * @param url
	 *            URL to delete
	 * @throws IOException
	 * @throws StorageException 
	 */
	public void deleteExtRefs(String pid, Collection<String> urls) throws IOException, StorageException {
		// Validation
		pid = validatePid(pid);
		validateNonEmptyCollection(urls);
		
		StorageProvider sp = providerResolver.getStorageProvider(pid);
		StorageEvent event = new StorageEvent(EventType.TAGFILE_UPDATE, pid, sp);
		notifyListeners(EventTime.PRE, event);
		for (String url : urls) {
			tagFilesSvc.removeEntry(pid, ExtRefsTagFile.class, base64Encode(url));
		}
		notifyListeners(EventTime.POST, event);
	}

	@Override
	public InputStream createZipStream(String pid, Set<String> relPaths) throws IOException, StorageException {
		// Validation
		final String fPid = validatePid(pid);
		Set<String> validatedRelPaths = new HashSet<>(relPaths.size());
		for (String relPath : relPaths) {
			validatedRelPaths.add(validateRelPath(relPath, false));
		}
		relPaths = validatedRelPaths;
		
		final StorageProvider sp = providerResolver.getStorageProvider(pid);
		final PipedOutputStream sink = new PipedOutputStream();
		PipedInputStream zipInStream = new PipedInputStream(sink);
		final HashSet<FileInfo> fileInfos = new HashSet<>();
		
		for (String relPath : relPaths) {
			fileInfos.add(sp.getDirInfo(pid, relPath, Integer.MAX_VALUE));
		}
		
		Callable<Void> zipWriter = new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				try (ZipOutputStream zipOutStream = new ZipOutputStream(sink)) {
					addZipEntries(zipOutStream, fileInfos);
				}
				
				return null;
			}

			private void addZipEntries(ZipOutputStream zipOutStream, final Set<FileInfo> fileInfos)
					throws IOException {
				for (FileInfo fi : fileInfos) {
					if (fi.getType() == Type.DIR) {
						addZipEntries(zipOutStream, fi.getChildren());
					} else {
						addZipEntry(zipOutStream, fi);
					}
				}
			}

			private void addZipEntry(ZipOutputStream zipOutStream, FileInfo fi) throws IOException {
				ZipEntry zipEntry = new ZipEntry(fi.getRelFilepath());
				try (InputStream fileStream = sp.readStream(fPid, fi.getRelFilepath())) {
					zipOutStream.putNextEntry(zipEntry);
					IOUtils.copy(fileStream, zipOutStream);
				} finally {
					zipOutStream.closeEntry();
					zipOutStream.flush();
				}
			}
			
		};
		
		threadPoolSvc.submitCachedPool(zipWriter);
		return zipInStream;
	}
	
	@Override
	public void indexFiles(String pid) throws IOException, StorageException {
		final String fPid = validatePid(pid);
		final StorageProvider sp = providerResolver.getStorageProvider(pid);

		threadPoolSvc.submit(new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				searchSvc.indexAllFiles(fPid, sp);
				return null;
			}

		});
	}

	@Override
	public void deindexFiles(String pid) throws IOException, StorageException {
		final String fPid = validatePid(pid);
		final StorageProvider sp = providerResolver.getStorageProvider(pid);

		threadPoolSvc.submit(new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				searchSvc.deindexAllFiles(fPid, sp);
				return null;
			}

		});
	}

	private void notifyListeners(EventTime time, StorageEvent event) throws IOException {
		if (this.eventListeners != null) {
			for (StorageEventListener iListener : this.eventListeners) {
				iListener.notify(time, event);
			}
		}
	}
	
	private void addMetadata(String pid, FileInfo fileInfo) {
		if (fileInfo.getType() == Type.DIR) {
			for (FileInfo child : fileInfo.getChildren()) {
				addMetadata(pid, child);
			}
		} else if (fileInfo.getType() == Type.FILE) {
			try {
				fileInfo.setMessageDigests(retrieveMessageDigests(pid, fileInfo.getRelFilepath()));
			} catch (IOException e) {
				LOGGER.warn("Unable to retrieve Message Digests for {}", pid);
			}
			try {
				fileInfo.setPronomFormat(retrievePronomFormat(pid, fileInfo.getRelFilepath()));
			} catch (IOException e) {
				LOGGER.warn("Unable to retrieve Pronom Formats for {}", pid);
			}
			try {
				fileInfo.setScanResult(retrieveScanResult(pid, fileInfo.getRelFilepath()));
			} catch (IOException e) {
				LOGGER.warn("Unable to retrieve Virus Scan results for {}", pid);
			}
			try {
				fileInfo.setMetadata(retrieveMetadata(pid, fileInfo.getRelFilepath()));
			} catch (IOException e) {
				LOGGER.warn("Unable to retrieve File Metadata for {}", pid);
			}
			try {
				fileInfo.setPresvPath(retrievePresvFilepath(pid, fileInfo.getRelFilepath()));
			} catch (IOException e) {
				LOGGER.warn("Unable to retrieve File Preservation for {}", pid);
			}
		}
	}
	
	private String validatePid(String pid) {
		if (Objects.requireNonNull(pid).length() == 0) {
			throw new IllegalArgumentException("Pid cannot be empty");
		}
		String validatedPid = pid.trim().toLowerCase();
		return validatedPid;
	}

	private String validateRelPath(String relPath, boolean allowHidden) {
		// Normalise path, change path separators to unix-style and remove trailing separator, if any.
		String validatedRelPath = FilenameUtils.normalizeNoEndSeparator(Objects.requireNonNull(relPath), true);

		String[] pathParts = Objects.requireNonNull(validatedRelPath).split("\\/");
		for (String part : pathParts) {
			if (!allowHidden) {
				// Throw an exception if any path part starts with a '.' which indicate a hidden directory. These are
				// special folders in which derived data files are stored.
				if (part.startsWith(".")) {
					throw new IllegalArgumentException(format("Path cannot contain hidden names - {0}",
							validatedRelPath));
				}
			}

			// Throw an exception if any path part contains '..' - which may grant access to files outside the bags
			// root.
			if (part.contains("..")) {
				throw new IllegalArgumentException(format("Path connot contain double period - {0}", validatedRelPath));
			}
		}

		return validatedRelPath;

	}

	private void validateNonEmptyCollection(Collection<String> urls) {
		if (Objects.requireNonNull(urls).isEmpty()) {
			throw new IllegalArgumentException("URL list cannot be empty.");
		}
	}
	
	/**
	 * Retrieves the message digests for a specified payload file.
	 * 
	 * @param pid
	 *            Identifier of collection record
	 * @param relPath
	 *            Relative path to the file whose message digests are required.
	 * @return Map<String, String> with algorithms as Keys and message digests as values.
	 * @throws IOException
	 */
	private Map<String, String> retrieveMessageDigests(String pid, String relPath) throws IOException {
		Map<String, String> mdMap = new HashMap<String, String>(1);
		// MD5
		mdMap.put(Manifest.Algorithm.MD5.javaSecurityAlgorithm,
				tagFilesSvc.getEntryValue(pid, ManifestMd5TagFile.class, prependData(relPath)));
		return mdMap;
	}

	/**
	 * Retrieves the pronom format for a specified file in a collection record.
	 * 
	 * @param pid
	 *            Identifier of collection record.
	 * @param relPath
	 *            Relative path of the file whose pronom format is to be retrieved.
	 * @return Fido string as PronomFormat object
	 * @throws IOException
	 */
	private PronomFormat retrievePronomFormat(String pid, String relPath) throws IOException {
		return new PronomFormat(tagFilesSvc.getEntryValue(pid, PronomFormatsTagFile.class, prependData(relPath)));
	}
	
	/**
	 * Retrieves the virus scan result for a specified file in a collection record.
	 * 
	 * @param pid
	 *            Identifier of collection record
	 * @param relPath
	 *            Relative path to the file whose virus scan result is to be retrieved.
	 * @return Virus Scan result as String
	 * @throws IOException
	 */
	private String retrieveScanResult(String pid, String relPath) throws IOException {
		return tagFilesSvc.getEntryValue(pid, VirusScanTagFile.class, prependData(relPath));
	}
	
	/**
	 * Retrieve the metadata for a specified file in a collection record.
	 * 
	 * @param pid
	 *            Identifier of a collection record
	 * @param relPath
	 *            Relative path to the file whose metadata is to be retrieved
	 * @return Metadata as Map<String, String[]>
	 * @throws IOException
	 */
	private Map<String, String[]> retrieveMetadata(String pid, String relPath) throws IOException {
		String metadataJson = tagFilesSvc.getEntryValue(pid, FileMetadataTagFile.class, prependData(relPath));
		return deserializeFromJson(metadataJson);
	}
	
	/**
	 * Retrieve the path to the preserved file of payload file, if it exists, for a payload file.
	 * 
	 * @param pid
	 *            Identifier of collection record
	 * @param relFilepath
	 *            Relative path to the file whose preserved file is to be retrieved.
	 * @return Path to the preserved format of the file. null if a preserved file doesn't exist.
	 * @throws IOException
	 */
	private String retrievePresvFilepath(String pid, String relFilepath) throws IOException {
		String presv = tagFilesSvc.getEntryValue(pid, PreservationMapTagFile.class, prependData(relFilepath));
		if (presv != null) {
			if (!presv.equals("PRESERVED") && !presv.equals("UNCONVERTIBLE")) {
				presv = stripData(presv);
			} else {
				presv = null;
			}
		}
		return presv;
	}

	private Map<String, String[]> deserializeFromJson(String jsonStr) throws JsonParseException, JsonMappingException,
			IOException {
		Map<String, String[]> metadataMap;
		if (jsonStr != null && jsonStr.length() > 0) {
			ObjectMapper mapper = new ObjectMapper();
			metadataMap = mapper.readValue(jsonStr, new TypeReference<Map<String, String[]>>() {
			});
		} else {
			metadataMap = Collections.unmodifiableMap(Collections.<String, String[]> emptyMap());
		}
		return metadataMap;
	}

	private String prependData(String relFilepath) {
		return "data/" + relFilepath;
	}

	private String stripData(String presv) {
		return presv.replaceFirst("^data/", "");
	}

	/**
	 * Returns a Base64 encoding of a provided String
	 * 
	 * @param stringToEncode
	 *            String to encode
	 * @return Base64 encoded String
	 */
	private String base64Encode(String stringToEncode) {
		String base64Encoded = new String(Base64.encodeBase64(stringToEncode.getBytes()));
		return base64Encoded;
	}

}
