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

package au.edu.anu.datacommons.storage.tagfiles;

import gov.loc.repository.bagit.Manifest.Algorithm;
import gov.loc.repository.bagit.utilities.MessageDigestHelper;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import au.edu.anu.datacommons.storage.provider.StorageException;
import au.edu.anu.datacommons.storage.provider.StorageProvider;
import au.edu.anu.datacommons.storage.provider.StorageProviderResolver;
import au.edu.anu.datacommons.tasks.ThreadPoolService;

/**
 * Service class that provides methods for reading and writing to tag files within collection records. The changes are
 * cached in memory until the next scheduled write. A changes are written to disk repeatedly at an interval set in the
 * writeFreq field. A final write is performed when instance is destroyed.
 * 
 * @author Rahul Khanna
 * 
 */
@Component
public class TagFilesService {
	private static final Logger LOGGER = LoggerFactory.getLogger(TagFilesService.class);

	@Autowired(required = true)
	private ThreadPoolService threadPoolSvc;
	
	@Autowired
	private StorageProviderResolver providerResolver;

	private final Map<String, Map<Class<? extends AbstractKeyValueFile>, AbstractKeyValueFile>> pidMap;
	private long writeFreq;
	private int cacheSize;
	
	/**
	 * Runnable task class that writes changes to disk in another thread.
	 */
	private Runnable writeAllPidTagFilesTask = new Runnable() {

			@Override
			public void run() {
				try {
					writeAllPidTagFiles();
				} catch (Exception e) {
					// Not rethrowing this exception so the this task continues to get scheduled. An uncaught exception
					// will result in this task not running again.
					LOGGER.error(e.getMessage(), e);
				}
			}

	};

	public TagFilesService() {
		super();
		pidMap = Collections.synchronizedMap(new LinkedHashMap<String, Map<Class<? extends AbstractKeyValueFile>, AbstractKeyValueFile>>(
						cacheSize + 1, 0.75f, true) {
					private static final long serialVersionUID = 1L;

					@Override
					protected boolean removeEldestEntry(
							Map.Entry<String, Map<Class<? extends AbstractKeyValueFile>, AbstractKeyValueFile>> eldest) {
							if (this.size() > cacheSize) {
								writePidTagFiles(eldest);
								LOGGER.trace("Cache maxed at {}. Removing tag files for {} from cache", cacheSize, eldest.getKey());
								return true;
							} else {
								return false;
							}
					}
				});
	}
	
	/**
	 * Sets the frequency at which changes will be written to disk.
	 * 
	 * @param writeFreq
	 * Frequency as 
	 */
	public void setWriteFreq(long writeFreq) {
		this.writeFreq = writeFreq;
	}
	
	/**
	 * Sets the maximum number of records whose tag files will be held in memory.
	 * 
	 * @param cacheSize
	 *            Max number of records in cache as int
	 */
	public void setMaxEntries(int cacheSize) {
		this.cacheSize = cacheSize;
	}

	/**
	 * Adds an entry into a specified tag file.
	 * 
	 * @param pid
	 *            Identifier of collection record
	 * @param clazz
	 *            Class of the tag file (extends {@link AbstractKeyValueFile})
	 * @param key
	 *            Key to be stored
	 * @param value
	 *            Value to be stored against the Key
	 * @throws IOException
	 *             when unable to read values from file (if tag file not already loaded in memory)
	 */
	public void addEntry(String pid, Class<? extends AbstractKeyValueFile> clazz, String key, String value)
			throws IOException {
		loadPid(pid);
		Map<Class<? extends AbstractKeyValueFile>, AbstractKeyValueFile> tagFilesMap = pidMap.get(pid);
		AbstractKeyValueFile keyValueFile = tagFilesMap.get(clazz);
		keyValueFile.put(key, value);
	}

	/**
	 * Returns all key-value entries in a specified tag file.
	 * 
	 * @param pid
	 *            Identifier of collection record
	 * @param clazz
	 *            Class of the tag file (extends {@link AbstractKeyValueFile})
	 * @return Key-Value pairs as a Map<String, String>
	 * @throws IOException
	 *             when unable to read values from file (if tag file not already loaded in memory)
	 */
	public Map<String, String> getAllEntries(String pid, Class<? extends AbstractKeyValueFile> clazz)
			throws IOException {
		loadPid(pid);
		Map<Class<? extends AbstractKeyValueFile>, AbstractKeyValueFile> tagFilesMap = pidMap.get(pid);
		AbstractKeyValueFile keyValueFile = tagFilesMap.get(clazz);
		return Collections.unmodifiableMap(keyValueFile);
	}

	/**
	 * Removes the specified key from a tag file.
	 * 
	 * @param pid
	 *            Identifier of collection record
	 * @param clazz
	 *            Class of the tag file (extends {@link AbstractKeyValueFile})
	 * @param key
	 *            Key to delete
	 * @throws IOException
	 *             when unable to read values from file (if tag file not already loaded in memory)
	 */
	public void removeEntry(String pid, Class<? extends AbstractKeyValueFile> clazz, String key) throws IOException {
		loadPid(pid);
		Map<Class<? extends AbstractKeyValueFile>, AbstractKeyValueFile> tagFilesMap = pidMap.get(pid);
		AbstractKeyValueFile keyValueFile = tagFilesMap.get(clazz);
		keyValueFile.remove(key);
	}

	/**
	 * Gets the value for a specified key in a tag file.
	 * 
	 * @param pid
	 *            Identifier of collection record
	 * @param clazz
	 *            Class of the tag file (extends {@link AbstractKeyValueFile})
	 * @param key
	 *            Key whose value is requested
	 * @return Value as String
	 * @throws IOException
	 *             when unable to read values from file (if tag file not already loaded in memory)
	 */
	public String getEntryValue(String pid, Class<? extends AbstractKeyValueFile> clazz, String key) throws IOException {
		loadPid(pid);
		Map<Class<? extends AbstractKeyValueFile>, AbstractKeyValueFile> tagFilesMap = pidMap.get(pid);
		AbstractKeyValueFile keyValueFile = tagFilesMap.get(clazz);
		return keyValueFile.get(key);
	}
	
	/**
	 * Returns if a tag file contains the specified key.
	 * 
	 * @param pid
	 *            Identifier of collection record
	 * @param clazz
	 *            Class of the tag file (extends {@link AbstractKeyValueFile})
	 * @param key
	 *            Key whose value is requested
	 * @return true if Key exists in the tag file, false otherwise.
	 * @throws IOException
	 *             when unable to read values from file (if tag file not already loaded in memory)
	 */
	public boolean containsKey(String pid, Class<? extends AbstractKeyValueFile> clazz, String key) throws IOException {
		loadPid(pid);
		Map<Class<? extends AbstractKeyValueFile>, AbstractKeyValueFile> tagFilesMap = pidMap.get(pid);
		AbstractKeyValueFile keyValueFile = tagFilesMap.get(clazz);
		return keyValueFile.containsKey(key);
	}
	
	/**
	 * Removes all entries from a specified tag file.
	 * 
	 * @param pid
	 *            Identifier of collection record
	 * @param clazz
	 *            Class of the tag file (extends {@link AbstractKeyValueFile})
	 * @throws IOException
	 *             when unable to read values from file (if tag file not already loaded in memory)
	 */
	public void clearAllEntries(String pid, Class<? extends AbstractKeyValueFile> clazz) throws IOException {
		loadPid(pid);
		Map<Class<? extends AbstractKeyValueFile>, AbstractKeyValueFile> tagFilesMap = pidMap.get(pid);
		AbstractKeyValueFile keyValueFile = tagFilesMap.get(clazz);
		keyValueFile.clear();
	}
	
	/**
	 * Returns a collection of classes for tagfiles that have been loaded for a specified record.
	 * 
	 * @param pid
	 *            Identifier of collection record
	 * @return Classes of tagfiles as Collection<Class<? extends AbstractKeyValueFile>>
	 * @throws IOException
	 *             when unable to read values from file (if tag file not already loaded in memory)
	 */
	public Collection<Class<? extends AbstractKeyValueFile>> getTagFileClasses(String pid) throws IOException {
		loadPid(pid);
		Map<Class<? extends AbstractKeyValueFile>, AbstractKeyValueFile> tagFilesMap = pidMap.get(pid);
		return Collections.unmodifiableSet(tagFilesMap.keySet());
	}
	
	/**
	 * Generates a message digest for each tag file loaded for a specified collection record.
	 * 
	 * @param pid
	 *            Identifier of collection record
	 * @param alg
	 *            Algorithm of message digest to generate
	 * @return Map of tag file names as keys and their computed message digest as values.
	 * @throws IOException
	 *             when unable to read values from file (if tag file not already loaded in memory)
	 */
	public Map<String, String> generateMessageDigests(String pid, Algorithm alg) throws IOException {
		Map<String, String> tagFilesMd = new HashMap<String, String>();
		loadPid(pid);
		Map<Class<? extends AbstractKeyValueFile>, AbstractKeyValueFile> tagFilesMap = pidMap.get(pid);
		for (Entry<Class<? extends AbstractKeyValueFile>, AbstractKeyValueFile> tagFileEntry : tagFilesMap.entrySet()) {
			String filepath = tagFileEntry.getValue().getFilepath();
			String md = MessageDigestHelper.generateFixity(tagFileEntry.getValue().serialize(), alg);
			tagFilesMd.put(filepath, md);
		}
		return tagFilesMd;
	}

	/**
	 * Schdules the write to disk task in a thread pool at a fixed frequency so all changes to tag files get written to
	 * disk 
	 */
	@PostConstruct
	public void postConstruct() {
		threadPoolSvc.scheduleWhenIdleWithFixedDelay(writeAllPidTagFilesTask, writeFreq, writeFreq, TimeUnit.SECONDS);
		threadPoolSvc.addCleanupTask(writeAllPidTagFilesTask);
	}

	/**
	 * Loads the tag files of a collection record and stores it in pidMap.
	 * 
	 * @param pid
	 *            Identifier of collection record
	 * @throws IOException
	 */
	private void loadPid(String pid) throws IOException {
		synchronized(pidMap) {
			if (!pidMap.containsKey(pid)) {
				pidMap.put(pid, readTagFiles(pid));
			}
		}
	}

	/**
	 * Reads all tag files for a specified record.
	 * 
	 * @param pid
	 *            Identifier of collection record
	 * @return Map of Tag file Classes and an instance of the tag file as Map<Class<? extends AbstractKeyValueFile>,
	 *         AbstractKeyValueFile>
	 * @throws IOException
	 */
	private Map<Class<? extends AbstractKeyValueFile>, AbstractKeyValueFile> readTagFiles(String pid)
			throws IOException {
		Map<Class<? extends AbstractKeyValueFile>, AbstractKeyValueFile> tagFiles = new HashMap<>();
		StorageProvider storageProvider;
		try {
			storageProvider = providerResolver.getStorageProvider(pid);
		} catch (StorageException e) {
			throw new IOException(e);
		}

		// BagIt
		tagFiles.put(BagItTagFile.class,
				new BagItTagFile(storageProvider.readTagFileStream(pid, BagItTagFile.FILEPATH)));
		// Bag Info
		tagFiles.put(BagInfoTagFile.class,
				new BagInfoTagFile(storageProvider.readTagFileStream(pid, BagInfoTagFile.FILEPATH)));

		// Tag Manifest
		tagFiles.put(TagManifestMd5TagFile.class,
				new TagManifestMd5TagFile(storageProvider.readTagFileStream(pid, TagManifestMd5TagFile.FILEPATH)));

		// External References
		tagFiles.put(ExtRefsTagFile.class,
				new ExtRefsTagFile(storageProvider.readTagFileStream(pid, ExtRefsTagFile.FILEPATH)));
		// Manifest MD5
		tagFiles.put(ManifestMd5TagFile.class,
				new ManifestMd5TagFile(storageProvider.readTagFileStream(pid, ManifestMd5TagFile.FILEPATH)));
		// File Metadata
		tagFiles.put(FileMetadataTagFile.class,
				new FileMetadataTagFile(storageProvider.readTagFileStream(pid, FileMetadataTagFile.FILEPATH)));
		// Preservation Files
		tagFiles.put(PreservationMapTagFile.class,
				new PreservationMapTagFile(storageProvider.readTagFileStream(pid, PreservationMapTagFile.FILEPATH)));
		// Pronom File Formats
		tagFiles.put(PronomFormatsTagFile.class,
				new PronomFormatsTagFile(storageProvider.readTagFileStream(pid, PronomFormatsTagFile.FILEPATH)));
		// Timestamps
		tagFiles.put(TimestampsTagFile.class,
				new TimestampsTagFile(storageProvider.readTagFileStream(pid, TimestampsTagFile.FILEPATH)));
		// Virus Scan Results
		tagFiles.put(VirusScanTagFile.class,
				new VirusScanTagFile(storageProvider.readTagFileStream(pid, VirusScanTagFile.FILEPATH)));

		return tagFiles;
	}

	/**
	 * Calls <code>writePidTagFiles</code> for each record in memory so changes in tag files are written to disk.
	 */
	private void writeAllPidTagFiles() {
		Set<Entry<String,Map<Class<? extends AbstractKeyValueFile>,AbstractKeyValueFile>>> entrySet = pidMap.entrySet();
		synchronized(pidMap) {
			Iterator<Entry<String, Map<Class<? extends AbstractKeyValueFile>, AbstractKeyValueFile>>> iter = entrySet.iterator();
			while (iter.hasNext()) {
				writePidTagFiles(iter.next());
			}
		}
	}

	/**
	 * Writes changes to all tag files for a specified record to disk.
	 * 
	 * @param entry
	 *            PidMap entry
	 */
	private void writePidTagFiles(Entry<String, Map<Class<? extends AbstractKeyValueFile>, AbstractKeyValueFile>> entry) {
		Map<Class<? extends AbstractKeyValueFile>, AbstractKeyValueFile> tagFiles = entry.getValue();
		if (tagFiles != null) {
			for (Entry<Class<? extends AbstractKeyValueFile>, AbstractKeyValueFile> tagFileEntry : tagFiles.entrySet()) {
				try {
					if (tagFileEntry.getValue().hasUnsavedChanges()) {
						StorageProvider storageProvider = providerResolver.getStorageProvider(entry.getKey());
						storageProvider.writeTagFileStream(entry.getKey(), tagFileEntry.getValue().getFilepath(), tagFileEntry.getValue().serialize());
					}
				} catch (IOException | StorageException e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
		}
	}
}
