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

package au.edu.anu.datacommons.storage.info;

import static java.text.MessageFormat.format;
import gov.loc.repository.bagit.Manifest;
import gov.loc.repository.bagit.utilities.FilenameHelper;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import au.edu.anu.datacommons.storage.info.FileInfo.Type;
import au.edu.anu.datacommons.storage.tagfiles.ExtRefsTagFile;
import au.edu.anu.datacommons.storage.tagfiles.FileMetadataTagFile;
import au.edu.anu.datacommons.storage.tagfiles.ManifestTagFile;
import au.edu.anu.datacommons.storage.tagfiles.PronomFormatsTagFile;
import au.edu.anu.datacommons.storage.tagfiles.TagFilesService;
import au.edu.anu.datacommons.storage.tagfiles.VirusScanTagFile;

/**
 * @author Rahul Khanna
 * 
 */
@Component
public class RecordDataInfoService {
	private static final Logger LOGGER = LoggerFactory.getLogger(RecordDataInfoService.class);

	@Autowired(required = true)
	private TagFilesService tagFilesSvc;

	public RecordDataInfo createRecordDataInfo(String pid, Path bagDir) throws IOException {
		RecordDataInfo rdi = new RecordDataInfo();
		rdi.setPid(pid);

		if (Files.isDirectory(getPayloadDir(bagDir))) {
			populateFileInfos(rdi, pid, bagDir);
		}

		rdi.setExtRefs(tagFilesSvc.getAllEntries(pid, ExtRefsTagFile.class).values());
		return rdi;
	}

	public FileInfo createFileInfo(String pid, Path bagDir, Path relPath) throws IOException {
		FileInfo fi = new FileInfo();
		Path payloadDir = getPayloadDir(bagDir);
		Path filepath = payloadDir.resolve(relPath);
		fi.setFilename(filepath.getFileName().toString());
		fi.setRelFilepath(FilenameHelper.normalizePathSeparators(payloadDir.relativize(filepath).toString()).toString());
		fi.setSize(Files.size(filepath));
		if (Files.isRegularFile(filepath)) {
			fi.setType(Type.FILE);
		} else if (Files.isDirectory(filepath)) {
			fi.setType(Type.DIR);
			fi.setRelFilepath(fi.getRelFilepath() + "/");
		} else {
			throw new IOException(format("Unexpected file object {0}", filepath));
		}

		fi.setLastModified(new Date(Files.getLastModifiedTime(filepath).toMillis()));
		fi.setMessageDigests(retrieveMessageDigests(pid, fi.getRelFilepath()));
		fi.setPronomFormat(retrievePronomFormat(pid, fi.getRelFilepath()));
		fi.setScanResult(retrieveScanResult(pid, fi.getRelFilepath()));
		fi.setMetadata(retrieveMetadata(pid, fi.getRelFilepath()));
		

		return fi;
	}

	private void populateFileInfos(RecordDataInfo rdi, String pid, Path bagDir) throws IOException {
		long nFiles = 0L;
		long sizeBytes = 0L;

		Path payloadDir = getPayloadDir(bagDir);
		List<Path> fileList = listFilesInDirFullPath(payloadDir, true);
		SortedSet<FileInfo> fileInfos = new TreeSet<FileInfo>();
		for (Path p : fileList) {
			FileInfo fi = createFileInfo(pid, bagDir, payloadDir.relativize(p));
			if (fi.getType() == Type.FILE) {
				nFiles++;
				sizeBytes += fi.getSize();
			} else {
				fi.setRelFilepath(fi.getRelFilepath() + "/");
			}
			fileInfos.add(fi);
		}

		rdi.setFiles(fileInfos);
		rdi.setSize(sizeBytes);
		rdi.setNumFiles(nFiles);
	}

	private List<Path> listFilesInDirRelPath(Path root, boolean recurse) throws IOException {
		List<Path> fileList = new ArrayList<>();
		for (Path p : listFilesInDirFullPath(root, recurse)) {
			fileList.add(root.relativize(p));
		}
		return fileList;
	}

	private List<Path> listFilesInDirFullPath(Path root, boolean recurse) throws IOException {
		List<Path> fileList = new ArrayList<>();
		DirectoryStream<Path> dirStream = Files.newDirectoryStream(root);
		for (Path p : dirStream) {
			if (!isExcluded(p)) {
				if (Files.isDirectory(p)) {
					fileList.add(p);
					if (recurse) {
						fileList.addAll(listFilesInDirFullPath(p, true));
					}
				} else if (Files.isRegularFile(p)) {
					fileList.add(p);
				}
			}
		}
		return fileList;
	}
	
	private Map<String, String> retrieveMessageDigests(String pid, String relPath) throws IOException {
		Map<String, String> mdMap = new HashMap<String, String>(1);
		// MD5
		mdMap.put(Manifest.Algorithm.MD5.javaSecurityAlgorithm, tagFilesSvc.getEntryValue(pid, ManifestTagFile.class, "data/" + relPath));
		return mdMap;
	}

	private PronomFormat retrievePronomFormat(String pid, String relPath) throws IOException {
		return new PronomFormat(tagFilesSvc.getEntryValue(pid, PronomFormatsTagFile.class, "data/" + relPath));
	}
	
	private String retrieveScanResult(String pid, String relPath) throws IOException {
		return tagFilesSvc.getEntryValue(pid, VirusScanTagFile.class, "data/" + relPath);
	}
	
	private Map<String, String[]> retrieveMetadata(String pid, String relPath) throws IOException {
		String metadataJson = tagFilesSvc.getEntryValue(pid, FileMetadataTagFile.class, "data/" + relPath);
		return deserializeFromJson(metadataJson);
	}
	
	private Map<String, String[]>deserializeFromJson(String jsonStr) throws JsonParseException, JsonMappingException, IOException {
		Map<String, String[]> metadataMap;
		if (jsonStr != null && jsonStr.length() > 0) {
			ObjectMapper mapper = new ObjectMapper();
			metadataMap = mapper.readValue(jsonStr, new TypeReference<Map<String, String[]>>() {});
		} else {
			metadataMap = Collections.unmodifiableMap(Collections.<String, String[]>emptyMap());
		}
		return metadataMap;
	}

	private boolean isExcluded(Path p) {
		boolean isExcluded = false;
		// Exclude files and folders starting with '.'
		if (p.getFileName().toString().startsWith(".")) {
			isExcluded = true;
		}
		return isExcluded;
	}

	private Path getPayloadDir(Path bagDir) {
		return bagDir.resolve("data/");
	}
}
