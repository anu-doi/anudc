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
import gov.loc.repository.bagit.impl.BagInfoTxtImpl;
import gov.loc.repository.bagit.utilities.FilenameHelper;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
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
import au.edu.anu.datacommons.storage.tagfiles.BagInfoTagFile;
import au.edu.anu.datacommons.storage.tagfiles.ExtRefsTagFile;
import au.edu.anu.datacommons.storage.tagfiles.FileMetadataTagFile;
import au.edu.anu.datacommons.storage.tagfiles.ManifestMd5TagFile;
import au.edu.anu.datacommons.storage.tagfiles.PreservationMapTagFile;
import au.edu.anu.datacommons.storage.tagfiles.PronomFormatsTagFile;
import au.edu.anu.datacommons.storage.tagfiles.TagFilesService;
import au.edu.anu.datacommons.storage.tagfiles.VirusScanTagFile;
import au.edu.anu.datacommons.util.StopWatch;

/**
 * A service class that generates a {@link RecordDataInfo} object containing details about the files in a collection
 * record.
 * 
 * @author Rahul Khanna
 * 
 */
@Component
public class RecordDataInfoService {
	private static final Logger LOGGER = LoggerFactory.getLogger(RecordDataInfoService.class);

	@Autowired(required = true)
	private TagFilesService tagFilesSvc;

	/**
	 * Creates a RecordDataInfo object for all files in a collection record. For collections with a large number of
	 * files this process may take a long time to complete.
	 * 
	 * @param pid
	 *            Identifier of collection record
	 * @param plDir
	 *            Payload directory
	 * @return Generated RecordDataInfo object
	 * @throws IOException
	 */
	public RecordDataInfo createRecordDataInfo(String pid, Path plDir) throws IOException {
		RecordDataInfo rdi = new RecordDataInfo();
		rdi.setPid(pid);
		setRecordInfo(rdi, pid);

		if (Files.isDirectory(plDir)) {
			populateFileInfos(rdi, pid, plDir);
		}

		rdi.setExtRefs(tagFilesSvc.getAllEntries(pid, ExtRefsTagFile.class).values());
		return rdi;
	}
	
	/**
	 * Creates a RecordDataInfo object containing information about files in a specified directory of a collection
	 * record.
	 * 
	 * @param pid
	 *            Identifier of collection record
	 * @param plDir
	 *            Payload directory
	 * @param relPath
	 *            Relative path to the directory whose files are to be included in the RDI object.
	 * @return Generated RecordDataInfo object
	 * @throws IOException
	 */
	public RecordDataInfo createDirLimitedRecordDataInfo(String pid, Path plDir, String relPath) throws IOException {
		RecordDataInfo rdi = new RecordDataInfo();
		rdi.setPid(pid);
		setRecordInfo(rdi, pid);

		if (Files.isDirectory(plDir.resolve(relPath))) {
			populateFileInfos(rdi, pid, plDir, relPath);
		}

		rdi.setExtRefs(tagFilesSvc.getAllEntries(pid, ExtRefsTagFile.class).values());
		return rdi;
	}

	/**
	 * Creates a single FileInfo object for a specified file within a collection record.
	 * 
	 * @param pid
	 *            Identifier of the collection record
	 * @param payloadDir
	 *            Payload directory
	 * @param relPath
	 *            Relative path to the file
	 * @return
	 * @throws NoSuchFileException
	 * @throws IOException
	 */
	public FileInfo createFileInfo(String pid, Path payloadDir, Path relPath) throws NoSuchFileException, IOException {
		FileInfo fi = new FileInfo();
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
			fi.setMessageDigests(retrieveMessageDigests(pid, fi.getRelFilepath()));
			fi.setPronomFormat(retrievePronomFormat(pid, fi.getRelFilepath()));
			fi.setScanResult(retrieveScanResult(pid, fi.getRelFilepath()));
			fi.setMetadata(retrieveMetadata(pid, fi.getRelFilepath()));
			fi.setPresvPath(retrievePresvFilepath(pid, fi.getRelFilepath()));
		} else if (Files.isDirectory(filepath)) {
			fi.setType(Type.DIR);
			fi.setRelFilepath(fi.getRelFilepath() + "/");
		} else {
			throw new IOException(format("Unexpected file object {0}", filepath));
		}

		fi.setLastModified(new Date(Files.getLastModifiedTime(filepath).toMillis()));

		return fi;
	}

	private void populateFileInfos(RecordDataInfo rdi, String pid, Path payloadDir) throws IOException {
		populateFileInfos(rdi, pid, payloadDir, null);
	}

	/**
	 * Populates a {@link RecordDataInfo} object with FileInfo objects.
	 * 
	 * @param rdi
	 *            The RDI object to populate
	 * @param pid
	 *            Identifier of collection record
	 * @param payloadDir
	 *            Payload directory of the collection record
	 * @param relPath
	 * @throws IOException
	 */
	private void populateFileInfos(RecordDataInfo rdi, String pid, Path payloadDir, String relPath) throws IOException {
		long nFiles = 0L;
		long sizeBytes = 0L;

		StopWatch sw = new StopWatch();
		sw.start();
		List<Path> fileList;
		if (relPath == null) {
			fileList = listFilesInDirFullPath(payloadDir, true);
		} else {
			fileList = listFilesInDirFullPath(payloadDir.resolve(relPath), false);
			for (Path parentDir = payloadDir.resolve(relPath); !parentDir.equals(payloadDir); parentDir = parentDir
					.getParent()) {
				fileList.add(parentDir);
			}
		}
		sw.stop();
		LOGGER.trace("{} files/dirs enumerated in {}: {}", format("{0}", fileList.size()), pid,
				sw.getTimeElapsedFormatted());

		SortedSet<FileInfo> fileInfos = new TreeSet<FileInfo>();
		sw.start();
		for (Path p : fileList) {
			try {
				FileInfo fi = createFileInfo(pid, payloadDir, payloadDir.relativize(p));
				if (fi.getType() == Type.FILE) {
					nFiles++;
					sizeBytes += fi.getSize();
				}
				fileInfos.add(fi);
			} catch (NoSuchFileException | AccessDeniedException e) {
				// Not rethrowing as the file may have been deleted during enumeration.
			}
		}
		sw.stop();
		LOGGER.trace("{} FileInfo objects created for {}: {}", format("{0}", fileInfos.size()), pid,
				sw.getTimeElapsedFormatted());

		rdi.setFiles(fileInfos);
		rdi.setDirSize(sizeBytes);
		rdi.setDirNumFiles(nFiles);
	}
	
	/**
	 * Returns a list of Path objects, each representing a file in the directory.
	 * 
	 * @param root
	 *            Root directory from where to start the tree walk
	 * @param recurse
	 *            true if the list should include files within subdirectories of the specified directory
	 * @return List of Path objects for each file
	 * @throws IOException
	 */
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

	/**
	 * Sets aggregated information in a RecordDataInfo object about files in a collection record.
	 * 
	 * @param rdi
	 *            RecordDataInfo object to which aggregated information will be added.
	 * @param pid
	 *            Identifier of collection record.
	 * @throws IOException
	 */
	private void setRecordInfo(RecordDataInfo rdi, String pid) throws IOException {
		String payloadOxum = tagFilesSvc.getEntryValue(pid, BagInfoTagFile.class, BagInfoTxtImpl.FIELD_PAYLOAD_OXUM);
		if (payloadOxum != null) {
			String[] payloadOxumParts = payloadOxum.split("\\.");
			try {
				rdi.setRecordSize(Long.parseLong(payloadOxumParts[0], 10));
			} catch (NumberFormatException | IndexOutOfBoundsException e) {
				LOGGER.warn("{}/{} contains invalid value {} for key {}", pid, BagInfoTagFile.FILEPATH, payloadOxum,
						BagInfoTxtImpl.FIELD_PAYLOAD_OXUM);
			}
			try {
				rdi.setRecordNumFiles(Long.parseLong(payloadOxumParts[1], 10));
			} catch (NumberFormatException | IndexOutOfBoundsException e) {
				LOGGER.warn("{}/{} contains invalid value {} for key {}", pid, BagInfoTagFile.FILEPATH, payloadOxum,
						BagInfoTxtImpl.FIELD_PAYLOAD_OXUM);
			}
		}
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

	private boolean isExcluded(Path p) {
		boolean isExcluded = false;
		// Exclude files and folders starting with '.'
		if (p.getFileName().toString().startsWith(".")) {
			isExcluded = true;
		}
		return isExcluded;
	}

	private String prependData(String relFilepath) {
		return "data/" + relFilepath;
	}

	private String stripData(String presv) {
		return presv.replaceFirst("^data/", "");
	}
}
