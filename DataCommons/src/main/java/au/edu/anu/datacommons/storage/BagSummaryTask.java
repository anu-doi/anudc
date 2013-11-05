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
 
package au.edu.anu.datacommons.storage;

import static java.text.MessageFormat.*;
import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFactory;
import gov.loc.repository.bagit.BagFactory.LoadOption;
import gov.loc.repository.bagit.BagFile;
import gov.loc.repository.bagit.Manifest;
import gov.loc.repository.bagit.utilities.FilenameHelper;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.storage.filesystem.FileFactory;
import au.edu.anu.datacommons.storage.info.BagSummary;
import au.edu.anu.datacommons.storage.info.FileSummary;
import au.edu.anu.datacommons.storage.info.FileSummaryMap;
import au.edu.anu.datacommons.storage.info.PronomFormat;
import au.edu.anu.datacommons.storage.info.ScanResult;
import au.edu.anu.datacommons.storage.tagfiles.ExtRefsTagFile;
import au.edu.anu.datacommons.storage.tagfiles.FileMetadataTagFile;
import au.edu.anu.datacommons.storage.tagfiles.PreservationMapTagFile;
import au.edu.anu.datacommons.storage.tagfiles.PronomFormatsTagFile;
import au.edu.anu.datacommons.storage.tagfiles.VirusScanTagFile;

/**
 * @author Rahul Khanna
 */
public class BagSummaryTask implements Callable<BagSummary> {
	private static final Logger LOGGER = LoggerFactory.getLogger(BagSummaryTask.class);

	private FileFactory ff;
	private File bagDir;
	
	public BagSummaryTask(FileFactory ff, File bagDir) {
		this.ff = ff;
		this.bagDir = bagDir;
	}
	
	@Override
	public BagSummary call() throws Exception {
		return generateBagSummary();
	}

	public BagSummary generateBagSummary() {
		Bag bag = new BagFactory().createBag(this.bagDir, LoadOption.BY_FILES);
		FileSummaryMap fsMap = createFsMap(bag);
		BagSummary bagSummary = new BagSummary(fsMap);
		try {
			File tagFile = ff.getFile(bag.getFile(), ExtRefsTagFile.FILEPATH);
			bagSummary.setExtRefsTxt(Collections.unmodifiableMap(new ExtRefsTagFile(tagFile)));
		} catch (IOException e) {
			LOGGER.warn("Unable to read ExtRefsTxt in {}", bag.getFile().getAbsolutePath());
		}
		bagSummary.setBagInfoTxt(Collections.unmodifiableMap(bag.getBagInfoTxt()));
		long bagSize = calcBagSize(fsMap);
		bagSummary.setBagSize(bagSize);
		bagSummary.setFriendlySize(FileUtils.byteCountToDisplaySize(bagSize));
		return bagSummary;
	}
	
	private long calcBagSize(FileSummaryMap fsMap) {
		long bagSize = 0L;
		for (FileSummary fs : fsMap.values()) {
			bagSize += fs.getSizeInBytes();
		}
		return bagSize;
	}


	/**
	 * Creates a FileSummaryMap and populates it with filepath as keys and FileSummary as values.
	 * 
	 * @return FileSummaryMap
	 */
	private FileSummaryMap createFsMap(Bag bag) {
		FileSummaryMap fsMap = new FileSummaryMap();
		for (BagFile iBagFile : bag.getPayload()) {
			if (!DcStorage.containsHiddenDirs(iBagFile.getFilepath())) {
				File file = ff.getFile(bag.getFile(), iBagFile.getFilepath());
				FileSummary fs = new FileSummary(iBagFile.getFilepath(), file);
				fsMap.put(iBagFile.getFilepath(), fs);
			}
		}
		
		populatePreservedFiles(fsMap, bag);
		populateMessageDigests(fsMap, bag);
		populatePronomIds(fsMap, bag);
		populateVirusScans(fsMap, bag);
		populateMetadata(fsMap, bag);
		return fsMap;
	}
	
	private void populatePreservedFiles(FileSummaryMap fsMap, Bag bag) {
		PreservationMapTagFile presvTagFile;
		try {
			presvTagFile = new PreservationMapTagFile(ff.getFile(bag.getFile(), PreservationMapTagFile.FILEPATH));
			for (Entry<String, String> presvEntry : presvTagFile.entrySet()) {
				FileSummary fs = fsMap.get(presvEntry.getKey());
				if (fs != null) {
					if (!presvEntry.getValue().equals("UNCONVERTIBLE") && !presvEntry.getValue().equals("PRESERVED")) {
						fs.setPresvFilepath(presvEntry.getValue());
					} else {
						fs.setPresvFilepath(null);
					}
				}
			}
		} catch (IOException e) {
			LOGGER.warn("Unable to read {} in {}.", PreservationMapTagFile.FILEPATH, bag.getFile().getAbsolutePath());
		}
		
	}
	

	/**
	 * Load message digests for each payload file from each manifest file.
	 * 
	 * @param fsMap
	 *            FileSummaryMap
	 * @param bag
	 *            Bag
	 */
	private void populateMessageDigests(FileSummaryMap fsMap, Bag bag) {
		List<Manifest> payloadManifests = bag.getPayloadManifests();
		if (payloadManifests == null || payloadManifests.size() < 1) {
			LOGGER.error("No payload manifests found.");
		}
		for (Manifest m : payloadManifests) {
			for (Entry<String, String> entry : m.entrySet()) {
				FileSummary fs = fsMap.get(entry.getKey());
				if (fs != null) {
					fs.getMessageDigests().put(m.getAlgorithm().javaSecurityAlgorithm, entry.getValue());
				}
			}
		}
	}

	private void populatePronomIds(FileSummaryMap fsMap, Bag bag) {
		try {
			PronomFormatsTagFile pronomFormats = new PronomFormatsTagFile(ff.getFile(bag.getFile(), PronomFormatsTagFile.FILEPATH));
			for (Entry<String, String> pronomEntry : pronomFormats.entrySet()) {
				FileSummary fs = fsMap.get(pronomEntry.getKey());
				if (fs != null) {
					try {
						PronomFormat pronomFormat = new PronomFormat(pronomEntry.getValue());
						fs.setPronomFormat(pronomFormat);
					} catch (Exception e) {
						// No op.
					}
				}
			}
		} catch (IOException e) {
			LOGGER.warn("Unable to read {} in {}", PronomFormatsTagFile.FILEPATH, bag.getFile().getAbsolutePath());
		}
	}

	private void populateVirusScans(FileSummaryMap fsMap, Bag bag) {
		try {
			VirusScanTagFile vs = new VirusScanTagFile(ff.getFile(bag.getFile(), VirusScanTagFile.FILEPATH));
			for (Entry<String, String> vsEntry : vs.entrySet()) {
				FileSummary fs = fsMap.get(vsEntry.getKey());
				if (fs != null) {
					try {
						if (vsEntry.getValue() != null && vsEntry.getValue().length() > 0) {
							ScanResult sr = new ScanResult(vsEntry.getValue());
							String srStr;
							if (sr.getStatus() == ScanResult.Status.FAILED) {
								srStr = sr.getStatus().toString() + ", " + sr.getSignature();
							} else {
								srStr = sr.getStatus().toString();
							}
							fs.setScanResult(srStr);
						} else {
							fs.setScanResult("NOT SCANNED");
						}
					} catch (Exception e) {
						// No op.
					}
				}
			}
		} catch (IOException e) {
			LOGGER.warn("Unable to read {} in {}", VirusScanTagFile.FILEPATH, bag.getFile().getAbsolutePath());
		}
	}

	private void populateMetadata(FileSummaryMap fsMap, Bag bag) {
		try {
			FileMetadataTagFile fileMetadata = new FileMetadataTagFile(ff.getFile(bag.getFile(), FileMetadataTagFile.FILEPATH));
			for (Entry<String, String> metadataEntry : fileMetadata.entrySet()) {
				FileSummary fs = fsMap.get(metadataEntry.getKey());
				if (fs != null) {
					try {
						if (metadataEntry.getValue() != null && metadataEntry.getValue().length() > 0) {
							fs.setMetadata(deserializeFromJson(metadataEntry.getValue()));
						}
					} catch (Exception e) {
						// No op.
					}
				}
			}
		} catch (IOException e) {
			LOGGER.warn("Unable to read {} in {}", FileMetadataTagFile.FILEPATH, bag.getFile().getAbsolutePath());
		}
		readMetadataFromHistoricalSource(fsMap, bag);
	}
	
	private Map<String, String[]>deserializeFromJson(String jsonStr) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(jsonStr, new TypeReference<Map<String, String[]>>() {});
	}

	/**
	 * For FileSummary objects that don't have metadata assigned, reads the metadata from historical source - file with
	 * the name 'payloadfile.txt.ser' in the metadata folder in the bag. Metadata for each payload file is now stored in
	 * tagfile file-metadata.txt .
	 * 
	 * @param fsMap
	 * @param bag
	 */
	@Deprecated
	private void readMetadataFromHistoricalSource(FileSummaryMap fsMap, Bag bag) {
		for (Entry<String, FileSummary> entry : fsMap.entrySet()) {
			if (entry.getValue().getMetadata() == null) {
				ObjectInputStream objInStream = null;
				try {
					String serMetadataFilename = getSerialisedMetadataFilename(entry.getKey());
					BagFile serMetadataBagFile = bag.getBagFile(serMetadataFilename);
					if (serMetadataBagFile != null) {
						objInStream = new ObjectInputStream(serMetadataBagFile.newInputStream());
						entry.getValue().setMetadata(readMetadata(objInStream));
					}
				} catch (Exception e) {
					LOGGER.warn(e.getMessage(), e);
				} finally {
					IOUtils.closeQuietly(objInStream);
				}
			}
		}
	}
	
	@Deprecated
	@SuppressWarnings("unchecked")
	private Map<String, String[]> readMetadata(ObjectInputStream objInStream) throws IOException,
			ClassNotFoundException {
		Map<String, String[]> metadataObj;
		try {
			metadataObj = (Map<String, String[]>) objInStream.readObject();
		} finally {
			IOUtils.closeQuietly(objInStream);
		}
		return metadataObj;
	}
	
	@Deprecated
	private String getSerialisedMetadataFilename(String filepath) {
		return format("metadata/" + FilenameHelper.getName(filepath) + ".ser");
	}
}
