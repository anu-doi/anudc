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

package au.edu.anu.dcbag;

import static java.text.MessageFormat.*;
import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFile;
import gov.loc.repository.bagit.Manifest.Algorithm;
import gov.loc.repository.bagit.utilities.FilenameHelper;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.dcbag.clamscan.ScanResult;
import au.edu.anu.dcbag.clamscan.ScanResult.Status;
import au.edu.anu.dcbag.fido.PronomFormat;

/**
 * Represents the summary of a single file in a bag associated with a record. Provides the following details of each
 * file in a bag:
 * <ul>
 * <li>Path of file relative to root of bag. For example, data/abc.txt</li>
 * <li>Size of file in bytes</li>
 * <li>Pronom format name of file. For example, "Acrobat PDF 1.6 - Portable Document Format"</li>
 * <li>Pronom format ID of file. For example, "fmt/20"</li>
 * <li>MD5 hash value of the file contents</li>
 * <li>Technical metadata of the file.</li>
 * <li>Virus scan result of the file</li>
 * </ul>
 */
public class FileSummary {
	private static final Logger LOGGER = LoggerFactory.getLogger(FileSummary.class);

	private String filepath;
	private PronomFormat pronomFormat;
	private Map<Algorithm, String> messageDigests;
	private Map<String, String[]> metadata;
	private String scanResult;
	private String filename;
	private long lastModified;
	private long sizeInBytes;
	
	protected FileSummary() {
	}

	/**
	 * Instantiates a new file summary.
	 * 
	 * @param bag
	 *            the bag containing file whose summary will be read
	 * @param bf
	 *            the bag file whose summary will be read
	 */
	public FileSummary(Bag bag, BagFile bf) {
		this.filepath = bf.getFilepath();
		
		File file = new File(bag.getFile(), bf.getFilepath());
		this.filename = file.getName();
		this.lastModified = file.lastModified();
		this.sizeInBytes = file.length();
		
		this.messageDigests = bag.getChecksums(bf.getFilepath());
		pronomFormat = new PronomFormat(bag, bf);
		readSerialisedMetadata(bag, bf);
		readVirusScanStr(bag, bf);
	}

	/**
	 * Gets the path.
	 * 
	 * @return the path
	 */
	public String getFilepath() {
		return filepath;
	}

	/**
	 * Gets the size in bytes.
	 * 
	 * @return the size in bytes
	 */
	public long getSizeInBytes() {
		return this.sizeInBytes;
	}
	
	public PronomFormat getPronomFormat() {
		return pronomFormat;
	}

	/**
	 * Gets the md5.
	 * 
	 * @return the md5
	 */
	public String getMd5() {
		return messageDigests.get(Algorithm.MD5);
	}
	
	public Map<String, String> getMessageDigests() {
		Map<String, String> messageDigests = new HashMap<String, String>();
		for (Entry<Algorithm, String> entry : this.messageDigests.entrySet()) {
			messageDigests.put(entry.getKey().javaSecurityAlgorithm, entry.getValue());
		}
		return messageDigests;
	}
	
	public String getFilename() {
		return this.filename;
	}
	
	public long getLastModified() {
		return this.lastModified;
	}

	/**
	 * Gets the metadata.
	 * 
	 * @return the metadata
	 */
	public Map<String, String[]> getMetadata() {
		return metadata;
	}

	/**
	 * Gets the friendly size.
	 * 
	 * @return the friendly size
	 */
	public String getFriendlySize() {
		return FileUtils.byteCountToDisplaySize(getSizeInBytes());
	}

	/**
	 * Gets the scan result.
	 * 
	 * @return the scan result
	 */
	public String getScanResult() {
		return scanResult;
	}

	private void readSerialisedMetadata(Bag bag, BagFile bf) {
		ObjectInputStream objInStream = null;
		try {
			String serMetadataFilename = getSerialisedMetadataFilename(bf);
			BagFile serMetadataBagFile = bag.getBagFile(serMetadataFilename);
			if (serMetadataBagFile != null) {
				objInStream = new ObjectInputStream(serMetadataBagFile.newInputStream());
				this.metadata = readMetadata(objInStream);
			}
		} catch (Exception e) {
			LOGGER.warn(e.getMessage(), e);
			this.metadata = new HashMap<String, String[]>();
		} finally {
			IOUtils.closeQuietly(objInStream);
		}

	}

	private String getSerialisedMetadataFilename(BagFile bf) {
		return format("metadata/{0}.ser", FilenameHelper.getName(bf.getFilepath()));
	}

	private void readVirusScanStr(Bag bag, BagFile bf) {
		BagFile virusScanTxt = bag.getBagFile(VirusScanTxt.FILEPATH);
		if (virusScanTxt != null) {
			VirusScanTxt vs = new VirusScanTxt(VirusScanTxt.FILEPATH, virusScanTxt, bag.getBagItTxt()
					.getCharacterEncoding());
			String scanResultStr = vs.get(bf.getFilepath());
			if (scanResultStr == null || scanResultStr.length() == 0) {
				this.scanResult = "NOT SCANNED";
			} else {
				ScanResult sr = new ScanResult(scanResultStr);
				this.scanResult = sr.getStatus().toString();
				if (sr.getStatus() == Status.FAILED)
					this.scanResult += ", " + sr.getSignature();
			}
		} else {
			this.scanResult = "NOT SCANNED";
		}
	}

	/**
	 * Read metadata.
	 * 
	 * @param objInStream
	 *            the inputstream containing serialised object
	 * @return the map metadata as a {@code Map<String, String[]>}
	 * @throws IOException
	 *             Signals that an I/O exception has occurred
	 * @throws ClassNotFoundException
	 *             the class not found exception
	 */
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
