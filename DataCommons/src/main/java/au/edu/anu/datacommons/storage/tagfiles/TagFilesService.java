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
import java.nio.file.Path;
import java.nio.file.Paths;
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
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import au.edu.anu.datacommons.storage.DcStorage;
import au.edu.anu.datacommons.tasks.ThreadPoolService;

/**
 * @author Rahul Khanna
 * 
 */
@Component
public class TagFilesService implements AutoCloseable {
	private static final Logger LOGGER = LoggerFactory.getLogger(TagFilesService.class);

	@Autowired(required = true)
	private ThreadPoolService threadPoolSvc;

	private volatile boolean isClosed = false;
	private final Map<String, Map<Class<? extends AbstractKeyValueFile>, AbstractKeyValueFile>> pidMap;
	private final Path bagsRoot;
	private long writeFreq;
	private int cacheSize;

	public TagFilesService(String bagsRoot) {
		this(Paths.get(bagsRoot));
	}

	public TagFilesService(Path bagsRoot) {
		super();
		this.bagsRoot = bagsRoot;
		pidMap = Collections
				.synchronizedMap(new LinkedHashMap<String, Map<Class<? extends AbstractKeyValueFile>, AbstractKeyValueFile>>(
						cacheSize, 0.75f, true) {
					private static final long serialVersionUID = 1L;

					@Override
					protected boolean removeEldestEntry(
							Map.Entry<String, Map<Class<? extends AbstractKeyValueFile>, AbstractKeyValueFile>> eldest) {
						synchronized (this) {
							if (this.size() > cacheSize) {
								writePidTagFiles(eldest.getKey());
								return true;
							} else {
								return false;
							}
						}
					}
				});
	}
	
	public void setWriteFreq(long writeFreq) {
		this.writeFreq = writeFreq;
	}
	
	public void setMaxEntries(int cacheSize) {
		this.cacheSize = cacheSize;
	}

	public void addEntry(String pid, Class<? extends AbstractKeyValueFile> clazz, String key, String value)
			throws IOException {
		loadPid(pid);
		Map<Class<? extends AbstractKeyValueFile>, AbstractKeyValueFile> tagFilesMap = pidMap.get(pid);
		AbstractKeyValueFile keyValueFile = tagFilesMap.get(clazz);
		keyValueFile.put(key, value);
	}

	public Map<String, String> getAllEntries(String pid, Class<? extends AbstractKeyValueFile> clazz)
			throws IOException {
		loadPid(pid);
		Map<Class<? extends AbstractKeyValueFile>, AbstractKeyValueFile> tagFilesMap = pidMap.get(pid);
		AbstractKeyValueFile keyValueFile = tagFilesMap.get(clazz);
		return Collections.unmodifiableMap(keyValueFile);
	}

	public void removeEntry(String pid, Class<? extends AbstractKeyValueFile> clazz, String key) throws IOException {
		loadPid(pid);
		Map<Class<? extends AbstractKeyValueFile>, AbstractKeyValueFile> tagFilesMap = pidMap.get(pid);
		AbstractKeyValueFile keyValueFile = tagFilesMap.get(clazz);
		keyValueFile.remove(key);
	}

	public String getEntryValue(String pid, Class<? extends AbstractKeyValueFile> clazz, String key) throws IOException {
		loadPid(pid);
		Map<Class<? extends AbstractKeyValueFile>, AbstractKeyValueFile> tagFilesMap = pidMap.get(pid);
		AbstractKeyValueFile keyValueFile = tagFilesMap.get(clazz);
		return keyValueFile.get(key);
	}
	
	public boolean containsKey(String pid, Class<? extends AbstractKeyValueFile> clazz, String key) throws IOException {
		loadPid(pid);
		Map<Class<? extends AbstractKeyValueFile>, AbstractKeyValueFile> tagFilesMap = pidMap.get(pid);
		AbstractKeyValueFile keyValueFile = tagFilesMap.get(clazz);
		return keyValueFile.containsKey(key);
	}
	
	public void clearAllEntries(String pid, Class<? extends AbstractKeyValueFile> clazz) throws IOException {
		loadPid(pid);
		Map<Class<? extends AbstractKeyValueFile>, AbstractKeyValueFile> tagFilesMap = pidMap.get(pid);
		AbstractKeyValueFile keyValueFile = tagFilesMap.get(clazz);
		keyValueFile.clear();
	}
	
	public Collection<Class<? extends AbstractKeyValueFile>> getTagFileClasses(String pid) throws IOException {
		loadPid(pid);
		Map<Class<? extends AbstractKeyValueFile>, AbstractKeyValueFile> tagFilesMap = pidMap.get(pid);
		return Collections.unmodifiableSet(tagFilesMap.keySet());
	}
	
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

	@PostConstruct
	public void postConstruct() {
		final long fWriteFreq = writeFreq;
		threadPoolSvc.scheduleWithFixedDelay(new Runnable() {

			@Override
			public void run() {
				try {
					writeAllPidTagFiles();
				} catch (Exception e) {
					// Not rethrowing this exception so the this task continues to get executed next time.
					LOGGER.error(e.getMessage(), e);
				}
			}

		}, fWriteFreq, fWriteFreq, TimeUnit.SECONDS);
	}

	@Override
	@PreDestroy
	public void close() throws Exception {
		if (!isClosed) {
			writeAllPidTagFiles();
			isClosed = true;
		}
	}

	private synchronized void loadPid(String pid) throws IOException {
		if (isClosed) {
			throw new IllegalStateException();
		}
		if (!pidMap.containsKey(pid)) {
			pidMap.put(pid, readTagFiles(pid));
		}
	}

	private Map<Class<? extends AbstractKeyValueFile>, AbstractKeyValueFile> readTagFiles(String pid)
			throws IOException {
		Path bagDir = bagsRoot.resolve(DcStorage.convertToDiskSafe(pid));
		Map<Class<? extends AbstractKeyValueFile>, AbstractKeyValueFile> tagFiles = new HashMap<>();
		// BagIt
		tagFiles.put(BagItTagFile.class, new BagItTagFile(bagDir.resolve(BagItTagFile.FILEPATH).toFile()));
		// Bag Info
		tagFiles.put(BagInfoTagFile.class, new BagInfoTagFile(bagDir.resolve(BagInfoTagFile.FILEPATH).toFile()));
		
		// Tag Manifest
		tagFiles.put(TagManifestMd5TagFile.class, new TagManifestMd5TagFile(bagDir.resolve(TagManifestMd5TagFile.FILEPATH).toFile()));
		
		// External References
		tagFiles.put(ExtRefsTagFile.class, new ExtRefsTagFile(bagDir.resolve(ExtRefsTagFile.FILEPATH).toFile()));
		// Manifest MD5
		tagFiles.put(ManifestMd5TagFile.class, new ManifestMd5TagFile(bagDir.resolve(ManifestMd5TagFile.FILEPATH).toFile()));
		// File Metadata
		tagFiles.put(FileMetadataTagFile.class, new FileMetadataTagFile(bagDir.resolve(FileMetadataTagFile.FILEPATH)
				.toFile()));
		// Preservation Files
		tagFiles.put(PreservationMapTagFile.class,
				new PreservationMapTagFile(bagDir.resolve(PreservationMapTagFile.FILEPATH).toFile()));
		// Pronom File Formats
		tagFiles.put(PronomFormatsTagFile.class, new PronomFormatsTagFile(bagDir.resolve(PronomFormatsTagFile.FILEPATH)
				.toFile()));
		// Timestamps
		tagFiles.put(TimestampsTagFile.class,
				new TimestampsTagFile(bagDir.resolve(TimestampsTagFile.FILEPATH).toFile()));
		// Virus Scan Results
		tagFiles.put(VirusScanTagFile.class, new VirusScanTagFile(bagDir.resolve(VirusScanTagFile.FILEPATH).toFile()));

		return tagFiles;
	}

	private void writeAllPidTagFiles() {
		Set<String> keySet = pidMap.keySet();
		synchronized(pidMap) {
			Iterator<String> iter = keySet.iterator();
			while (iter.hasNext()) {
				writePidTagFiles(iter.next());
			}
		}
	}

	private void writePidTagFiles(String pid) {
		Map<Class<? extends AbstractKeyValueFile>, AbstractKeyValueFile> tagFiles = pidMap.get(pid);
		if (tagFiles != null) {
			for (Entry<Class<? extends AbstractKeyValueFile>, AbstractKeyValueFile> tagFileEntry : tagFiles.entrySet()) {
				try {
					tagFileEntry.getValue().write();
				} catch (IOException e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
		}
	}
}
