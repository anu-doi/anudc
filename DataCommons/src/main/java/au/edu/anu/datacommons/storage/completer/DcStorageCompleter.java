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

package au.edu.anu.datacommons.storage.completer;

import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFile;
import gov.loc.repository.bagit.transformer.Completer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.tika.exception.TikaException;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import au.edu.anu.datacommons.properties.GlobalProps;
import au.edu.anu.datacommons.storage.completer.fido.FidoParser;
import au.edu.anu.datacommons.storage.completer.metadata.MetadataExtractor;
import au.edu.anu.datacommons.storage.completer.metadata.MetadataExtractorImpl;
import au.edu.anu.datacommons.storage.completer.virusscan.ClamScan;
import au.edu.anu.datacommons.storage.info.ScanResult;
import au.edu.anu.datacommons.storage.tagfiles.FileMetadataTagFile;
import au.edu.anu.datacommons.storage.tagfiles.PronomFormatsTagFile;
import au.edu.anu.datacommons.storage.tagfiles.TimestampsTagFile;
import au.edu.anu.datacommons.storage.tagfiles.VirusScanTagFile;

/**
 * Completes a bag to add additional tag files as required by ANU DataCommons. Requires the bag to be completed through
 * another completer to update tag and manifest contents.
 * 
 * @see Completer
 */
public class DcStorageCompleter extends AbstractCustomCompleter {
	private static final Logger LOGGER = LoggerFactory.getLogger(DcStorageCompleter.class);
	
	/**
	 * Completes a bag as per ANU Data Commons requirements.
	 * 
	 * @param bag
	 *            Bag to be completed
	 * 
	 * @see Completer#complete(Bag)
	 */
	@Override
	public Bag complete(final Bag bag) {
		ExecutorService es = Executors.newFixedThreadPool(4, new ThreadFactory() {
			AtomicInteger ai = new AtomicInteger(1);
			
			@Override
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r, "comppool-thread-" + ai.getAndIncrement());
				t.setPriority(2);
				return t;
			}
		});
		List<Future<?>> tasks = new ArrayList<Future<?>>(3);
		tasks.add(es.submit(new Runnable() {

			@Override
			public void run() {
				try {
					handlePronomTxt(bag);
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
			
		}));
		tasks.add(es.submit(new Runnable() {

			@Override
			public void run() {
				try {
					handleAvScan(bag);
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
			
		}));
		tasks.add(es.submit(new Runnable() {

			@Override
			public void run() {
				try {
					handleMetadata(bag);
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
			
		}));
		tasks.add(es.submit(new Runnable() {
			
			@Override
			public void run() {
				try {
					handleTimestamps(bag);
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
			
		}));

		waitForTasks(tasks);
		es.shutdown();
		try {
			// Adding this only as failsafe as thread pool is shutdown only after all tasks are completed.
			es.awaitTermination(1, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			LOGGER.error(e.getMessage());
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
	 * @throws IOException 
	 */
	private Bag handlePronomTxt(Bag bag) throws IOException {
		LOGGER.debug("Updating Pronom IDs in bag {}...", bag.getFile().getAbsolutePath());
		PronomFormatsTagFile pFormats = new PronomFormatsTagFile(bag.getFile());

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
				InputStream fileStream = null;
				try {
					fileStream = iBagFile.newInputStream();
					FidoParser fido = new FidoParser(fileStream, iBagFile.getFilepath());
					LOGGER.trace("Fido result for {}: {}", iBagFile.getFilepath(), fido.getFidoStr());
					pFormats.put(iBagFile.getFilepath(), fido.getFidoStr());
				} catch (IOException e) {
					LOGGER.warn("Unable to get Fido output for file {}", iBagFile.getFilepath());
				} finally {
					IOUtils.closeQuietly(fileStream);
				}
			}
		}
		pFormats.write();
		synchronized (bag) {
			bag.addFileAsTag(pFormats.getFile());
		}
		LOGGER.debug("Finished updating Pronom IDs in bag {}.", bag.getFile().getAbsolutePath());
		return bag;
	}

	/**
	 * Scans all payload files using ClamAV, gets the scan status and stores in virus-scan.txt as a tag file.
	 * 
	 * @param bag
	 *            Bag containing the payload files to scan
	 * 
	 * @return Bag with the updated virus-scan.txt tagfile
	 * @throws IOException 
	 */
	private Bag handleAvScan(Bag bag) throws IOException {
		LOGGER.debug("Updating Virus Scan statuses in bag {}...", bag.getFile().getAbsolutePath());
		VirusScanTagFile vsTxt = new VirusScanTagFile(bag.getFile());

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
		vsTxt.write();
		synchronized (bag) {
			bag.addFileAsTag(vsTxt.getFile());
		}
		LOGGER.debug("Finished updating Virus Scan statuses in bag {}.", bag.getFile().getAbsolutePath());
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
	 * @throws IOException 
	 */
	private Bag handleMetadata(Bag bag) throws IOException {
		LOGGER.debug("Updating File Metadata in bag {}...", bag.getFile().getAbsolutePath());
		FileMetadataTagFile fileMetadata = new FileMetadataTagFile(bag.getFile());

		if (this.limitAddUpdatePayloadFilepaths == null && this.limitDeletePayloadFilepaths == null) {
			deleteMetadataDir(bag);
			fileMetadata.clear();
		} else if (this.limitDeletePayloadFilepaths != null) {
			for (String filepath : this.limitDeletePayloadFilepaths) {
				fileMetadata.remove(filepath);
			}
		}

		// Get scan result for each payload file.
		for (BagFile iBagFile : bag.getPayload()) {
			if (isLimited(this.limitAddUpdatePayloadFilepaths, iBagFile.getFilepath())) {
				InputStream dataStream = null;
				MetadataExtractor me;
				try {
					dataStream = iBagFile.newInputStream();
					me = new MetadataExtractorImpl(dataStream);
					fileMetadata.put(iBagFile.getFilepath(), serializeToJson(me.getMetadataMap()));
				} catch (SAXException e) {
					LOGGER.warn("{} for {}", e.getMessage(), iBagFile.getFilepath());
				} catch (TikaException e) {
					LOGGER.warn("{} for {}", e.getMessage(), iBagFile.getFilepath());
				} finally {
					IOUtils.closeQuietly(dataStream);
				}
			}
		}
		fileMetadata.write();
		synchronized (bag) {
			bag.addFileAsTag(fileMetadata.getFile());
		}
		LOGGER.debug("Finished File Metadata in bag {}.", bag.getFile().getAbsolutePath());
		return bag;
	}

	private Bag handleTimestamps(Bag bag) throws IOException {
		LOGGER.debug("Updating file timestamps in bag {}...", bag.getFile().getAbsolutePath());
		TimestampsTagFile timestamps = new TimestampsTagFile(bag.getFile());
		
		if (this.limitAddUpdatePayloadFilepaths == null && this.limitDeletePayloadFilepaths == null) {
			timestamps.clear();
		} else if (this.limitDeletePayloadFilepaths != null) {
			for (String filepath : this.limitDeletePayloadFilepaths) {
				timestamps.remove(filepath);
			}
		}
		
		for (BagFile iBagFile : bag.getPayload()) {
			if (isLimited(this.limitAddUpdatePayloadFilepaths, iBagFile.getFilepath())) {
				timestamps.put(iBagFile.getFilepath(), String.valueOf(new File(bag.getFile(), iBagFile.getFilepath()).lastModified()));
			}
		}
		timestamps.write();
		synchronized (bag) {
			bag.addFileAsTag(timestamps.getFile());
		}
		LOGGER.debug("Finished updating file timestamps in bag {}.", bag.getFile().getAbsolutePath());
		return bag;
	}
	
	private void waitForTasks(List<Future<?>> tasks) {
		for (Future<?> f : tasks) {
			try {
				f.get();
			} catch (InterruptedException e) {
				LOGGER.warn(e.getMessage(), e);
			} catch (ExecutionException e) {
				LOGGER.warn(e.getMessage(), e);
			}
		}
		tasks.clear();
	}

	private String serializeToJson(Object obj) throws JsonGenerationException, JsonMappingException, IOException {
		ObjectMapper objMapper = new ObjectMapper();
		return objMapper.writeValueAsString(obj);
	}

	/**
	 * Deletes the metadata directory in a bag as it's no longer needed. The data is now stored in tagfile
	 * file-metadata.txt .
	 * 
	 * @param bag
	 */
	private void deleteMetadataDir(Bag bag) {
		File metadataDir = new File(bag.getFile(), "metadata/");
		if (metadataDir.isDirectory()) {
			FileUtils.deleteQuietly(metadataDir);
		}
	}
}
