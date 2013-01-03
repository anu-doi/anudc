package au.edu.anu.datacommons.storage;

import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFile;
import gov.loc.repository.bagit.impl.StringBagFile;
import gov.loc.repository.bagit.transformer.Completer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.tika.exception.TikaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import au.edu.anu.dcbag.PronomFormatsTxt;
import au.edu.anu.dcbag.VirusScanTxt;
import au.edu.anu.dcbag.clamscan.ClamScan;
import au.edu.anu.dcbag.clamscan.ScanResult;
import au.edu.anu.dcbag.fido.FidoParser;
import au.edu.anu.dcbag.metadata.MetadataExtractor;
import au.edu.anu.dcbag.metadata.MetadataExtractorImpl;

/**
 * Completes a bag to add additional tag files as required by ANU DataCommons. Requires the bag to be completer through another completer to update tag and
 * manifest contents.
 * 
 * @see Completer
 */
public class DcStorageCompleter implements Completer
{
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
	public Bag complete(Bag bag)
	{
		bag = handlePronomTxt(bag);
		bag = handleAvScan(bag);
		bag = handleMetadata(bag);
		return bag;
	}

	/**
	 * Runs Fido on each payload file in the bag to be completed, saves the Fido Output String in pronom-formats.txt as a tag file.
	 * 
	 * @param bag
	 *            Bag containing the files to be processed.
	 * @return Bag object with pronom-formats.txt containing Fido Strings for each payload file.
	 */
	private Bag handlePronomTxt(Bag bag)
	{
		PronomFormatsTxt pFormats = getOrCreatePronomFormats(bag);

		// Get Fido Output for each payload file.
		pFormats.clear();
		for (BagFile iBagFile : bag.getPayload())
		{
			FidoParser fido;
			try
			{
				fido = new FidoParser(iBagFile.newInputStream());
				LOGGER.trace("Fido result for {}: {}", iBagFile.getFilepath(), fido.getOutput());
				pFormats.put(iBagFile.getFilepath(), fido.getOutput());
			}
			catch (IOException e)
			{
				LOGGER.warn("Unable to get Fido output for file {}", iBagFile.getFilepath());
			}
		}

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
	private Bag handleAvScan(Bag bag)
	{
		VirusScanTxt vsTxt = getOrCreateVirusScan(bag);

		// Get scan result for each payload file.
		vsTxt.clear();
		ClamScan cs = new ClamScan("localhost", 3310);
		if (cs.ping() == true)
		{
			for (BagFile iBagFile : bag.getPayload())
			{
				InputStream is = iBagFile.newInputStream();
				ScanResult sr = cs.scan(is);
				vsTxt.put(iBagFile.getFilepath(), sr.getResult());
			}
		}

		bag.putBagFile(vsTxt);
		return bag;
	}

	/**
	 * Extracts metadata of each payload file and stores the metadata as XMP file as well as a plain serialised object containing the metadata as
	 * <code>Map<String, String[]></code>.
	 * 
	 * @param bag
	 *            Bag containing the payload files whose metadata is to be extracted and stored as tagfiles.
	 * 
	 * @return Bag with tagfiles containing metadata about each payload file.
	 */
	private Bag handleMetadata(Bag bag)
	{
		// Delete metadata directory in bag.
		bag.removeTagDirectory("metadata/");

		// Extract metadata and save serialize Metadata object.
		for (BagFile iBagFile : bag.getPayload())
		{
			MetadataExtractor me;
			try
			{
				me = new MetadataExtractorImpl(iBagFile.newInputStream());

				try
				{
					handleTikaXmp(bag, iBagFile, me);
				}
				catch (TikaException e)
				{
					LOGGER.warn("Tika Exception for " + iBagFile.getFilepath(), e);
				}

				try
				{
					handleTikaSerialize(bag, iBagFile, me);
				}
				catch (IOException e)
				{
					LOGGER.warn("IOException for " + iBagFile.getFilepath(), e);
				}
			}
			catch (IOException e)
			{
				LOGGER.warn("IOException for " + iBagFile.getFilepath(), e);
			}
			catch (SAXException e)
			{
				LOGGER.warn("SAXException for " + iBagFile.getFilepath(), e);
			}
			catch (TikaException e)
			{
				LOGGER.warn("TikaException for " + iBagFile.getFilepath(), e);
			}
		}
		
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
	private void handleTikaXmp(Bag bag, BagFile bf, MetadataExtractor me) throws TikaException
	{
		String xmpFilename = "metadata/" + bf.getFilepath().substring(bf.getFilepath().indexOf('/') + 1) + ".xmp";
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
	private void handleTikaSerialize(Bag bag, BagFile bf, MetadataExtractor me) throws IOException
	{
		ByteArrayOutputStream bos = null;
		ObjectOutputStream objOutStream = null;
		try
		{
			String serMetaFilename = "metadata/" + bf.getFilepath().substring(bf.getFilepath().indexOf('/') + 1) + ".ser";
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
		}
		finally
		{
			IOUtils.closeQuietly(objOutStream);
			IOUtils.closeQuietly(bos);
		}
	}

	/**
	 * Gets the PronomFormatsTxt from a bag. Creates one if it doesn't exist.
	 * 
	 * @param bag
	 *            Bag containing PronomFormatsTxt
	 * @return PronomFormatsTxt object containing pronom format details of each payload file.
	 */
	private PronomFormatsTxt getOrCreatePronomFormats(Bag bag)
	{
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
	private VirusScanTxt getOrCreateVirusScan(Bag bag)
	{
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
	private String getCharEncoding(Bag bag)
	{
		return bag.getBagItTxt().getCharacterEncoding();
	}
}
