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
import gov.loc.repository.bagit.BagFile;
import gov.loc.repository.bagit.impl.StringBagFile;
import gov.loc.repository.bagit.transformer.Completer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.tika.exception.TikaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import au.edu.anu.datacommons.properties.GlobalProps;
import au.edu.anu.datacommons.storage.completer.fido.FidoParser;
import au.edu.anu.datacommons.storage.completer.metadata.MetadataExtractor;
import au.edu.anu.datacommons.storage.completer.metadata.MetadataExtractorImpl;
import au.edu.anu.datacommons.storage.completer.virusscan.ClamScan;
import au.edu.anu.datacommons.storage.info.PronomFormatsTxt;
import au.edu.anu.datacommons.storage.info.ScanResult;
import au.edu.anu.datacommons.storage.info.VirusScanTxt;

/**
 * Completes a bag to add additional tag files as required by ANU DataCommons. Requires the bag to be completer through
 * another completer to update tag and manifest contents.
 * 
 * @see Completer
 */
public class DcStorageCompleter implements Completer {
	private static final Logger LOGGER = LoggerFactory.getLogger(DcStorageCompleter.class);
	
	private List<String> limitAddUpdatePayloadFilepaths = null;
	private List<String> limitDeletePayloadFilepaths = null;

	public List<String> getLimitAddUpdatePayloadFilepaths() {
		return limitAddUpdatePayloadFilepaths;
	}

	public void setLimitAddUpdatePayloadFilepaths(List<String> limitAddUpdatePayloadFilepaths) {
		this.limitAddUpdatePayloadFilepaths = limitAddUpdatePayloadFilepaths;
	}

	public List<String> getLimitDeletePayloadFilepaths() {
		return limitDeletePayloadFilepaths;
	}

	public void setLimitDeletePayloadFilepaths(List<String> limitDeletePayloadFilepaths) {
		this.limitDeletePayloadFilepaths = limitDeletePayloadFilepaths;
	}

	/**
	 * Completes a bag as per ANU Data Commons requirements.
	 * 
	 * @param bag
	 *            Bag to be completed
	 * 
	 * @see Completer#complete(Bag)
	 */
	@Override
	public Bag complete(Bag bag) {
		try {
			bag = handlePronomTxt(bag);
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
		}
		try {
			bag = handleAvScan(bag);
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
		}
		try {
			bag = handleMetadata(bag);
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
		}
		return bag;
	}

	/**
	 * Runs Fido on each payload file in the bag to be completed, saves the Fido Output String in pronom-formats.txt as
	 * a tag file.
	 * 
	 * @param bag
	 *            Bag containing the files to be processed.
	 * @return Bag object with pronom-formats.txt containing Fido Strings for each payload file.
	 */
	private Bag handlePronomTxt(Bag bag) {
		LOGGER.debug("Updating Pronom IDs in bag {}...", bag.getFile().getAbsolutePath());
		PronomFormatsTxt pFormats = getOrCreatePronomFormats(bag);

		if (this.limitAddUpdatePayloadFilepaths == null && this.limitDeletePayloadFilepaths == null) {
			pFormats.clear();
		} else if (this.limitDeletePayloadFilepaths != null) {
			for (String filepath : this.limitDeletePayloadFilepaths) {
				if (pFormats.containsKey(filepath)) {
					pFormats.remove(filepath);
				}
			}
		}

		// Get Fido Output for each payload file.
		for (BagFile iBagFile : bag.getPayload()) {
			if (isLimited(this.limitAddUpdatePayloadFilepaths, iBagFile.getFilepath())) {
				FidoParser fido;
				InputStream fileStream = null;
				try {
					fileStream = iBagFile.newInputStream();
					fido = new FidoParser(fileStream);
					LOGGER.trace("Fido result for {}: {}", iBagFile.getFilepath(), fido.getFidoStr());
					pFormats.put(iBagFile.getFilepath(), fido.getFidoStr());
				} catch (IOException e) {
					LOGGER.warn("Unable to get Fido output for file {}", iBagFile.getFilepath());
				} finally {
					IOUtils.closeQuietly(fileStream);
				}
			}
		}
		LOGGER.debug("Finished updating Pronom IDs in bag {}.", bag.getFile().getAbsolutePath());
		bag.putBagFile(pFormats);
		return bag;
	}

	/**
	 * Scans all payload files using ClamAV, gets the scan status and stores in virus-scan.txt as a tag file.
	 * 
	 * @param bag
	 *            Bag containing the payload files to scan
	 * 
	 * @return Bag with the updated virus-scan.txt tagfile
	 */
	private Bag handleAvScan(Bag bag) {
		LOGGER.debug("Updating Virus Scan statuses in bag {}...", bag.getFile().getAbsolutePath());
		VirusScanTxt vsTxt = getOrCreateVirusScan(bag);

		if (this.limitAddUpdatePayloadFilepaths == null && this.limitDeletePayloadFilepaths == null) {
			vsTxt.clear();
		} else if (this.limitDeletePayloadFilepaths != null) {
			for (String filepath : this.limitDeletePayloadFilepaths) {
				if (vsTxt.containsKey(filepath)) {
					vsTxt.remove(filepath);
				}
			}
		}

		// Get scan result for each payload file.
		ClamScan cs = new ClamScan(GlobalProps.getClamScanHost(), GlobalProps.getClamScanPort(), GlobalProps.getClamScanTimeout());
		if (cs.ping() == true) {
			for (BagFile iBagFile : bag.getPayload()) {
				if (isLimited(this.limitAddUpdatePayloadFilepaths, iBagFile.getFilepath())) {
					InputStream streamToScan = null;
					try {
						streamToScan = iBagFile.newInputStream();
						ScanResult sr = cs.scan(streamToScan);
						vsTxt.put(iBagFile.getFilepath(), sr.getResult());
					} finally {
						IOUtils.closeQuietly(streamToScan);
					}
				}
			}
		}
		LOGGER.debug("Finished updating Virus Scan statuses in bag {}.", bag.getFile().getAbsolutePath());
		bag.putBagFile(vsTxt);
		return bag;
	}

	/**
	 * Extracts metadata of each payload file and stores the metadata as XMP file as well as a plain serialised object
	 * containing the metadata as <code>Map<String, String[]></code>.
	 * 
	 * @param bag
	 *            Bag containing the payload files whose metadata is to be extracted and stored as tagfiles.
	 * 
	 * @return Bag with tagfiles containing metadata about each payload file.
	 */
	private Bag handleMetadata(Bag bag) {
		LOGGER.debug("Updating File Metadata in bag {}...", bag.getFile().getAbsolutePath());
		if (this.limitAddUpdatePayloadFilepaths == null && this.limitDeletePayloadFilepaths == null) {
			bag.removeTagDirectory("metadata/");
		} else if (this.limitDeletePayloadFilepaths != null) {
			for (String filepath : this.limitDeletePayloadFilepaths) {
				String metaFilepath = createMetaFilepath(filepath);
				bag.removeBagFile(metaFilepath);
				new File(bag.getFile(), metaFilepath).delete();

				String xmpFilepath = createXmpFilepath(filepath);
				bag.removeBagFile(xmpFilepath);
				new File(bag.getFile(), xmpFilepath).delete();
			}
		}

		// Extract metadata and save serialize Metadata object.
		for (BagFile iBagFile : bag.getPayload()) {
			if (isLimited(this.limitAddUpdatePayloadFilepaths, iBagFile.getFilepath())) {
				MetadataExtractor me;
				try {
					InputStream dataStream = null;
					try {
						dataStream = iBagFile.newInputStream();
						me = new MetadataExtractorImpl(dataStream);
					} finally {
						IOUtils.closeQuietly(dataStream);
					}

					try {
						handleTikaXmp(bag, iBagFile, me);
					} catch (TikaException e) {
						LOGGER.warn("Tika Exception for " + iBagFile.getFilepath(), e);
					}
					try {
						handleTikaSerialize(bag, iBagFile, me);
					} catch (IOException e) {
						LOGGER.warn("IOException for " + iBagFile.getFilepath(), e);
					}
				} catch (IOException e) {
					LOGGER.warn("IOException for " + iBagFile.getFilepath(), e);
				} catch (SAXException e) {
					LOGGER.warn("SAXException for " + iBagFile.getFilepath(), e);
				} catch (TikaException e) {
					LOGGER.warn("TikaException for " + iBagFile.getFilepath(), e);
				}
			}
		}
		LOGGER.debug("Finished updating File Metadata in bag {}.", bag.getFile().getAbsolutePath());
		return bag;
	}

	/**
	 * Adds an XMP file containing metadata for each payload file.
	 * 
	 * @param bag
	 *            Bag containing the payload file.
	 * @param bf
	 *            The bagfile whose metadata is to be extracted.
	 * @param me
	 *            MetadataExtractor object obtained using Apache Tika
	 * @throws TikaException
	 *             If unable to get XMP data from metadata object
	 */
	private void handleTikaXmp(Bag bag, BagFile bf, MetadataExtractor me) throws TikaException {
		String xmpFilename = createXmpFilepath(bf.getFilepath());
		StringBagFile xmpFile = new StringBagFile(xmpFilename, me.getXmpMetadata().toString());
		LOGGER.debug("Storing XMP data for {} in {}", bf.getFilepath(), xmpFilename);
		LOGGER.trace(me.getXmpMetadata().toString());
		bag.putBagFile(xmpFile);
	}

	/**
	 * Serializes the MetadataExtractor object for a payload file and stores in the bag as tagfile.
	 * 
	 * @param bag
	 *            Bag containing the payload file whose MetadataExtractor object will be serialised.
	 * @param bf
	 *            BagFile whose metadata will be serialised.
	 * @param me
	 *            MetadataExtractor object containing metadata about payload file.
	 * @throws IOException
	 *             If unable to save serialised file to disk.
	 */
	private void handleTikaSerialize(Bag bag, BagFile bf, MetadataExtractor me) throws IOException {
		ByteArrayOutputStream bos = null;
		ObjectOutputStream objOutStream = null;
		try {
			String serMetaFilename = createMetaFilepath(bf.getFilepath());
			bos = new ByteArrayOutputStream();
			objOutStream = new ObjectOutputStream(bos);
			objOutStream.writeObject(me.getMetadataMap());
			StringBagFile serMetaFile = new StringBagFile(serMetaFilename, bos.toByteArray());
			LOGGER.debug("Storing serialized metadata for {} in {}.", bf.getFilepath(), serMetaFilename);
			Map<String, String[]> metadataMap = me.getMetadataMap();
			for (String key : metadataMap.keySet())
				for (String value : metadataMap.get(key))
					LOGGER.trace("{}: {}", key, value);
			bag.putBagFile(serMetaFile);
		} finally {
			IOUtils.closeQuietly(objOutStream);
			IOUtils.closeQuietly(bos);
		}
	}
	
	private String createMetaFilepath(String payloadFilepath) {
		return format("metadata/{0}.ser", payloadFilepath.substring(payloadFilepath.indexOf('/') + 1));
	}
	
	private String createXmpFilepath(String payloadFilepath) {
		return format("metadata/{0}.xmp", payloadFilepath.substring(payloadFilepath.indexOf('/') + 1));
	}

	private boolean isLimited(List<String> limitList, String filepath) {
		boolean isLimited = false;
		if (limitList == null) {
			isLimited = true;
		} else {
			if (limitList.contains(filepath)) {
				isLimited = true;
			}
		}
		return isLimited;
	}
	

	/**
	 * Gets the PronomFormatsTxt from a bag. Creates one if it doesn't exist.
	 * 
	 * @param bag
	 *            Bag containing PronomFormatsTxt
	 * @return PronomFormatsTxt object containing pronom format details of each payload file.
	 */
	private PronomFormatsTxt getOrCreatePronomFormats(Bag bag) {
		PronomFormatsTxt pFormats;
		BagFile pronomBagFile = bag.getBagFile(PronomFormatsTxt.FILEPATH);
		if (pronomBagFile == null)
			pFormats = new PronomFormatsTxt(PronomFormatsTxt.FILEPATH, getCharEncoding(bag));
		else
			pFormats = new PronomFormatsTxt(PronomFormatsTxt.FILEPATH, pronomBagFile, getCharEncoding(bag));
		return pFormats;
	}

	/**
	 * Gets the VirusScanTxt from a bag. Creates one if it doesn't exist.
	 * 
	 * @param bag
	 *            Bag containing the VirusScanTxt
	 * 
	 * @return VirusScanTxt object containing virus scan details of each payload file.
	 */
	private VirusScanTxt getOrCreateVirusScan(Bag bag) {
		VirusScanTxt vsTxt;
		BagFile avStatusFile = bag.getBagFile(VirusScanTxt.FILEPATH);
		if (avStatusFile == null)
			vsTxt = new VirusScanTxt(VirusScanTxt.FILEPATH, getCharEncoding(bag));
		else
			vsTxt = new VirusScanTxt(VirusScanTxt.FILEPATH, avStatusFile, getCharEncoding(bag));
		return vsTxt;
	}

	/**
	 * Gets the character encoding for tagfiles specified in bagit.txt.
	 * 
	 * @param bag
	 *            Bag whose tag file character encoding to retrieve
	 * @return Character encoding as String. By default it will be "UTF-8"
	 */
	private String getCharEncoding(Bag bag) {
		return bag.getBagItTxt().getCharacterEncoding();
	}
}
