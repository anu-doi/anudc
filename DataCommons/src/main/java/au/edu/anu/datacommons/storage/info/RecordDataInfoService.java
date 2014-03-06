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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
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
import au.edu.anu.datacommons.storage.tagfiles.ManifestMd5TagFile;
import au.edu.anu.datacommons.storage.tagfiles.PreservationMapTagFile;
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

	public FileInfo createFileInfo(String pid, Path bagDir, Path relPath) throws NoSuchFileException, IOException {
		FileInfo fi = new FileInfo();
		Path payloadDir = getPayloadDir(bagDir);
		Path filepath = payloadDir.resolve(relPath);
		fi.setFilename(filepath.getFileName().toString());
		Path parent = payloadDir.relativize(filepath).getParent();
		if (parent != null) {
			fi.setDirpath(FilenameHelper.normalizePathSeparators(parent.toString()));
		} else {
			fi.setDirpath("");
		}
		fi.setRelFilepath(FilenameHelper.normalizePathSeparators(payloadDir.relativize(filepath).toString()));
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
		fi.setPresvPath(retrievePresvFilepath(pid, fi.getRelFilepath()));
		
		return fi;
	}

	private void populateFileInfos(RecordDataInfo rdi, String pid, Path bagDir) throws IOException {
		long nFiles = 0L;
		long sizeBytes = 0L;

		Path payloadDir = getPayloadDir(bagDir);
		List<Path> fileList = listFilesInDirFullPath(payloadDir, true);
		SortedSet<FileInfo> fileInfos = new TreeSet<FileInfo>();
		for (Path p : fileList) {
			try {
				FileInfo fi = createFileInfo(pid, bagDir, payloadDir.relativize(p));
				if (fi.getType() == Type.FILE) {
					nFiles++;
					sizeBytes += fi.getSize();
				}
				fileInfos.add(fi);
			} catch (NoSuchFileException e) {
				// Not rethrowing as the file may have been deleted during enumeration. 
			} 
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
		try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(root)) {
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
		} catch (FileSystemException e) {
			LOGGER.warn("Skipping inaccessible file/folder {}: ", root.toString(), e.getMessage());
		}
		return fileList;
	}
	
	private Map<String, String> retrieveMessageDigests(String pid, String relPath) throws IOException {
		Map<String, String> mdMap = new HashMap<String, String>(1);
		// MD5
		mdMap.put(Manifest.Algorithm.MD5.javaSecurityAlgorithm,
				tagFilesSvc.getEntryValue(pid, ManifestMd5TagFile.class, prependData(relPath)));
		return mdMap;
	}

	private PronomFormat retrievePronomFormat(String pid, String relPath) throws IOException {
		return new PronomFormat(tagFilesSvc.getEntryValue(pid, PronomFormatsTagFile.class, prependData(relPath)));
	}
	
	private String retrieveScanResult(String pid, String relPath) throws IOException {
		return tagFilesSvc.getEntryValue(pid, VirusScanTagFile.class, prependData(relPath));
	}
	
	private Map<String, String[]> retrieveMetadata(String pid, String relPath) throws IOException {
		String metadataJson = tagFilesSvc.getEntryValue(pid, FileMetadataTagFile.class, prependData(relPath));
		return deserializeFromJson(metadataJson);
	}
	
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

	private String prependData(String relFilepath) {
		return "data/" + relFilepath;
	}

	private String stripData(String presv) {
		return presv.replaceFirst("^data/", "");
	}
}
