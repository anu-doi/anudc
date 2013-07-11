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

import static java.text.MessageFormat.*;
import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFile;
import gov.loc.repository.bagit.Manifest;
import gov.loc.repository.bagit.utilities.FilenameHelper;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.io.IOUtils;

/**
 * Represents a Map of FileSummary for each BagFile in a specified Bag.
 */
public class FileSummaryMap extends TreeMap<String, FileSummary> {
	private static final long serialVersionUID = 1L;

	protected FileSummaryMap() {
		super();
	}

	/**
	 * Instantiates a new file summary map.
	 * 
	 * @param bag
	 *            the bag containing files whose summary will be read
	 */
	public FileSummaryMap(Bag bag) {
		super();
		populateMap(bag);
	}

	/**
	 * Gets the file summary.
	 * 
	 * @param bagFilePath
	 *            the bag file path
	 * @return the file summary
	 */
	public FileSummary getFileSummary(String bagFilePath) {
		return this.get(bagFilePath);
	}

	protected void populateMap(Bag bag) {
		for (BagFile iBagFile : bag.getPayload()) {
			FileSummary fs = new FileSummary(bag, iBagFile);
			this.put(iBagFile.getFilepath(), fs);
		}
		
		populateMessageDigests(bag);
		populatePronomIds(bag);
		populateVirusScans(bag);
		populateMetadata(bag);
	}

	protected void populateMessageDigests(Bag bag) {
		List<Manifest> payloadManifests = bag.getPayloadManifests();
		for (Manifest m : payloadManifests) {
			for (Entry<String, String> entry : m.entrySet()) {
				FileSummary fs = this.get(entry.getKey());
				if (fs != null) {
					fs.getMessageDigests().put(m.getAlgorithm().javaSecurityAlgorithm, entry.getValue());
				}
			}
		}
	}

	protected void populatePronomIds(Bag bag) {
		BagFile pronomFormatsTxt = bag.getBagFile(PronomFormatsTxt.FILEPATH);
		if (pronomFormatsTxt != null) {
			PronomFormatsTxt tagFile = new PronomFormatsTxt(pronomFormatsTxt.getFilepath(), pronomFormatsTxt, bag
					.getBagItTxt().getCharacterEncoding());
			for (Entry<String, String> pronomEntry : tagFile.entrySet()) {
				FileSummary fs = this.get(pronomEntry.getKey());
				if (fs != null) {
					fs.setPronomFormat(new PronomFormat(pronomEntry.getValue()));
				}
			}
		}
	}

	protected void populateVirusScans(Bag bag) {
		BagFile vsTxt = bag.getBagFile(VirusScanTxt.FILEPATH);
		if (vsTxt != null) {
			VirusScanTxt vs = new VirusScanTxt(VirusScanTxt.FILEPATH, vsTxt, bag.getBagItTxt()
					.getCharacterEncoding());
			for (Entry<String, String> vsEntry : vs.entrySet()) {
				FileSummary fs = this.get(vsEntry.getKey());
				if (fs != null) {
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
				}
			}
		}
	}

	private void populateMetadata(Bag bag) {
		for (Entry<String, FileSummary> entry : this.entrySet()) {
			ObjectInputStream objInStream = null;
			try {
				String serMetadataFilename = getSerialisedMetadataFilename(entry.getKey());
				BagFile serMetadataBagFile = bag.getBagFile(serMetadataFilename);
				if (serMetadataBagFile != null) {
					objInStream = new ObjectInputStream(serMetadataBagFile.newInputStream());
					entry.getValue().setMetadata(readMetadata(objInStream));
				}
			} catch (Exception e) {
//				LOGGER.warn(e.getMessage(), e);
				entry.getValue().setMetadata(new HashMap<String, String[]>());
			} finally {
				IOUtils.closeQuietly(objInStream);
			}
		}
	}
	
	private String getSerialisedMetadataFilename(String filepath) {
		return format("metadata/{0}.ser", FilenameHelper.getName(filepath));
	}
	
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
}
