package au.edu.anu.datacommons.storage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import au.edu.anu.dcbag.BagPropsTxt;
import au.edu.anu.dcbag.DcBag;
import au.edu.anu.dcbag.DcBagException;
import au.edu.anu.dcbag.PronomFormatsTxt;
import au.edu.anu.dcbag.VirusScanTxt;
import au.edu.anu.dcbag.BagPropsTxt.DataSource;
import au.edu.anu.dcbag.clamscan.ClamScan;
import au.edu.anu.dcbag.clamscan.ScanResult;
import au.edu.anu.dcbag.clamscan.ScanResult.Status;
import au.edu.anu.dcbag.fido.FidoParser;
import au.edu.anu.dcbag.metadata.MetadataExtractor;
import au.edu.anu.dcbag.metadata.MetadataExtractorImpl;
import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFactory;
import gov.loc.repository.bagit.BagFile;
import gov.loc.repository.bagit.Manifest;
import gov.loc.repository.bagit.filesystem.FileNode;
import gov.loc.repository.bagit.filesystem.impl.FileFileNode;
import gov.loc.repository.bagit.impl.FileBagFile;
import gov.loc.repository.bagit.impl.StringBagFile;
import gov.loc.repository.bagit.transformer.Completer;
import gov.loc.repository.bagit.transformer.impl.DefaultCompleter;
import gov.loc.repository.bagit.utilities.MessageDigestHelper;

public class DcStorageCompleter implements Completer
{
	private static final Logger LOGGER = LoggerFactory.getLogger(DcStorageCompleter.class);
	
	@Override
	public Bag complete(Bag bag)
	{
		bag = handlePronomTxt(bag);
		bag = handleAvScan(bag);
		bag = handleMetadata(bag);
		return bag;
	}

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

	private void handleTikaXmp(Bag bag, BagFile bf, MetadataExtractor me) throws TikaException
	{
		String xmpFilename = "metadata/" + bf.getFilepath().substring(bf.getFilepath().indexOf('/') + 1) + ".xmp";
		StringBagFile xmpFile = new StringBagFile(xmpFilename, me.getXmpMetadata().toString());
		LOGGER.debug("Storing XMP data for {} in {}", bf.getFilepath(), xmpFilename);
		LOGGER.trace(me.getXmpMetadata().toString());
		bag.putBagFile(xmpFile);
	}

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

	private String getCharEncoding(Bag bag)
	{
		return bag.getBagItTxt().getCharacterEncoding();
	}
}
