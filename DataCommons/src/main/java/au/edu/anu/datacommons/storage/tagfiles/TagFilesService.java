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

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import au.edu.anu.datacommons.storage.DcStorage;

/**
 * @author Rahul Khanna
 * 
 */
@Component
public class TagFilesService implements AutoCloseable {
	private static final Logger LOGGER = LoggerFactory.getLogger(TagFilesService.class);
	private static final int MAX_ENTRIES = 10;
	
	private volatile boolean isClosed = false;
	private LinkedHashMap<String, Map<Class<? extends AbstractKeyValueFile>, AbstractKeyValueFile>> pidMap;
	private Path bagsRoot;

	public TagFilesService(String bagsRoot) {
		this(Paths.get(bagsRoot));
	}
	
	public TagFilesService(Path bagsRoot) {
		super();
		this.bagsRoot = bagsRoot;
		pidMap = new LinkedHashMap<String, Map<Class<? extends AbstractKeyValueFile>, AbstractKeyValueFile>>(MAX_ENTRIES, 0.75f, true) {
			private static final long serialVersionUID = 1L;

			@Override
			protected boolean removeEldestEntry(
					java.util.Map.Entry<String, Map<Class<? extends AbstractKeyValueFile>, AbstractKeyValueFile>> eldest) {
				if (this.size() > MAX_ENTRIES) {
					prepPidEntryRemoval(eldest.getKey());
					return true;
				} else {
					return false;
				}
			}
		};
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

	@Override
	@PreDestroy
	public void close() throws Exception {
		if (!isClosed) {
			for (String pid : pidMap.keySet()) {
				prepPidEntryRemoval(pid);
			}
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
		// External References
		tagFiles.put(ExtRefsTagFile.class, new ExtRefsTagFile(bagDir.resolve(ExtRefsTagFile.FILEPATH).toFile()));
		// Manifest MD5
		tagFiles.put(ManifestTagFile.class, new ManifestTagFile(bagDir.resolve(ManifestTagFile.FILEPATH).toFile()));
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

	private void prepPidEntryRemoval(String pid) {
		Map<Class<? extends AbstractKeyValueFile>, AbstractKeyValueFile> tagFiles = pidMap.get(pid);
		for (Entry<Class<? extends AbstractKeyValueFile>, AbstractKeyValueFile> tagFileEntry : tagFiles.entrySet()) {
			try {
				tagFileEntry.getValue().write();
			} catch (IOException e) {
				LOGGER.error(e.getMessage());
			}
		}
		LOGGER.trace("Removed tag files for Pid {} from RU list", pid);
	}
}
