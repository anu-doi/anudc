package au.edu.anu.datacommons.storage;

import static java.text.MessageFormat.format;
import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFile;
import gov.loc.repository.bagit.Manifest;
import gov.loc.repository.bagit.utilities.FilenameHelper;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.storage.info.BagSummary;
import au.edu.anu.datacommons.storage.info.FileSummary;
import au.edu.anu.datacommons.storage.info.FileSummaryMap;
import au.edu.anu.datacommons.storage.info.PronomFormat;
import au.edu.anu.datacommons.storage.info.PronomFormatsTxt;
import au.edu.anu.datacommons.storage.info.ScanResult;
import au.edu.anu.datacommons.storage.info.VirusScanTxt;

public class BagSummaryTask implements Callable<BagSummary> {
	private static final Logger LOGGER = LoggerFactory.getLogger(BagSummaryTask.class);
	
	private Bag bag;
	
	public BagSummaryTask(Bag bag) {
		this.bag = bag;
	}
	
	@Override
	public BagSummary call() throws Exception {
		return generateBagSummary();
	}

	public BagSummary generateBagSummary() {
		FileSummaryMap fsMap = createFsMap();
		
		BagSummary bagSummary = new BagSummary(bag, fsMap);
		return bagSummary;
	}

	/**
	 * Creates a FileSummaryMap and populates it with filepath as keys and FileSummary as values.
	 * 
	 * @return FileSummaryMap
	 */
	private FileSummaryMap createFsMap() {
		FileSummaryMap fsMap = new FileSummaryMap();
		for (BagFile iBagFile : bag.getPayload()) {
			File file = new File(bag.getFile(), iBagFile.getFilepath());
			FileSummary fs = new FileSummary(iBagFile.getFilepath(), file);
			fsMap.put(iBagFile.getFilepath(), fs);
		}
		
		populateMessageDigests(fsMap, bag);
		populatePronomIds(fsMap, bag);
		populateVirusScans(fsMap, bag);
		populateMetadata(fsMap, bag);
		return fsMap;
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
		BagFile pronomFormatsTxt = bag.getBagFile(PronomFormatsTxt.FILEPATH);
		if (pronomFormatsTxt != null) {
			PronomFormatsTxt pronomTagFile;
			try {
				pronomTagFile = new PronomFormatsTxt(PronomFormatsTxt.FILEPATH, pronomFormatsTxt, bag.getBagItTxt()
						.getCharacterEncoding());
			} catch (Exception e) {
				pronomTagFile = null;
				LOGGER.warn("Unable to read {}. Error: {}", pronomFormatsTxt.getFilepath(), e.getMessage());
			}
			if (pronomTagFile != null) {
				for (Entry<String, String> pronomEntry : pronomTagFile.entrySet()) {
					FileSummary fs = fsMap.get(pronomEntry.getKey());
					if (fs != null) {
						fs.setPronomFormat(new PronomFormat(pronomEntry.getValue()));
					}
				}
			}
		} else {
			LOGGER.warn("{} doesn't exist.", PronomFormatsTxt.FILEPATH);
		}
	}

	private void populateVirusScans(FileSummaryMap fsMap, Bag bag) {
		BagFile vsTxt = bag.getBagFile(VirusScanTxt.FILEPATH);
		if (vsTxt != null) {
			VirusScanTxt vs;
			try {
				vs = new VirusScanTxt(VirusScanTxt.FILEPATH, vsTxt, bag.getBagItTxt().getCharacterEncoding());
			} catch (Exception e) {
				vs = null;
				LOGGER.warn("Unable to read {}. Error: {}", VirusScanTxt.FILEPATH, e.getMessage());
			}
			if (vs != null) {
				for (Entry<String, String> vsEntry : vs.entrySet()) {
					FileSummary fs = fsMap.get(vsEntry.getKey());
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
		} else {
			LOGGER.warn("{} doesn't exist.", VirusScanTxt.FILEPATH);
		}
	}

	private void populateMetadata(FileSummaryMap fsMap, Bag bag) {
		for (Entry<String, FileSummary> entry : fsMap.entrySet()) {
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
