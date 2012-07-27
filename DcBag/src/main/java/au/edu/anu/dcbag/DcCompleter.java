package au.edu.anu.dcbag;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.dcbag.DcBagProps.DataSource;
import au.edu.anu.dcbag.fido.FidoParser;
import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFactory;
import gov.loc.repository.bagit.BagFile;
import gov.loc.repository.bagit.Manifest;
import gov.loc.repository.bagit.filesystem.FileNode;
import gov.loc.repository.bagit.filesystem.impl.FileFileNode;
import gov.loc.repository.bagit.impl.FileBagFile;
import gov.loc.repository.bagit.impl.StringBagFile;
import gov.loc.repository.bagit.transformer.impl.DefaultCompleter;
import gov.loc.repository.bagit.utilities.MessageDigestHelper;

public class DcCompleter extends DefaultCompleter
{
	private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getClass());

	public DcCompleter(BagFactory bagFactory)
	{
		super(bagFactory);
	}

	@Override
	public Bag complete(Bag bag)
	{
		bag = super.complete(bag);
		handlePronomTxt(bag);
		return super.complete(bag);
	}

	private void handlePronomTxt(Bag bag)
	{
		PronomFormatsTxt pFormats;
		BagFile pronomBagFile = bag.getBagFile(PronomFormatsTxt.PRONOMFORMATS_FILEPATH);
		if (pronomBagFile == null)
			pFormats = new PronomFormatsTxt(PronomFormatsTxt.PRONOMFORMATS_FILEPATH, bag.getBagItTxt().getCharacterEncoding());
		else
			pFormats = new PronomFormatsTxt(PronomFormatsTxt.PRONOMFORMATS_FILEPATH, pronomBagFile, bag.getBagItTxt().getCharacterEncoding());

		// Get Fido Output for each payload file.
		pFormats.clear();
		for (BagFile iBagFile : bag.getPayload())
		{
			FidoParser fido;
			try
			{
				File fullFilePath;
				fullFilePath = getFileFromBagFile(iBagFile);
				fido = new FidoParser(fullFilePath);
				pFormats.put(iBagFile.getFilepath(), fido.getOutput());
			}
			catch (IOException e)
			{
				LOGGER.warn("Unable to get Fido output for file {}", iBagFile.getFilepath());
			}
		}

		bag.putBagFile(pFormats);
	}

	private File getFileFromBagFile(BagFile bagFile)
	{
		// Using reflection, access private field 'file' in the bagfile that has the full path as its value.
		@SuppressWarnings("rawtypes")
		Class bagFileClass = bagFile.getClass();
		File file = null;
		try
		{
			Field fileField = bagFileClass.getDeclaredField("file");
			fileField.setAccessible(true);
			file = (File) fileField.get(bagFile);
		}
		catch (NoSuchFieldException e)
		{
			// If field 'file' doesn't exist, access fileNode and get File object from it.
			try
			{
				Field fileNodeField = bagFileClass.getDeclaredField("fileNode");
				fileNodeField.setAccessible(true);
				FileFileNode fileFileNode = (FileFileNode) fileNodeField.get(bagFile);
				file = fileFileNode.getFile();
			}
			catch (NoSuchFieldException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			catch (SecurityException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			catch (IllegalArgumentException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			catch (IllegalAccessException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		catch (SecurityException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IllegalArgumentException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return file;
	}
	
	public void checkValidMods(DcBag dcBag) throws DcBagException
	{
		if (dcBag.getBagProperty(DcBagProps.FIELD_DATASOURCE) != null
				&& dcBag.getBagProperty(DcBagProps.FIELD_DATASOURCE).equals(DcBagProps.DataSource.INSTRUMENT.toString()))
		{
			// Verify the integrity of tagmanifest.
			Manifest tagManifest = dcBag.getBag().getTagManifest(DcBag.BAGS_ALGORITHM);
			List<Manifest> payloadManifestList = dcBag.getBag().getPayloadManifests();

			for (Manifest iPlManifest : payloadManifestList)
			{
				if (tagManifest.containsKey(iPlManifest.getFilepath()))
				{
					String hashInManifest = tagManifest.get(iPlManifest.getFilepath());
					if (!MessageDigestHelper.fixityMatches(iPlManifest.newInputStream(), iPlManifest.getAlgorithm(), hashInManifest))
					{
						LOGGER.error("Payload manifest hash invalid.");
						throw new DcBagException("Payload manifest hash invalid.");
					}
				}
			}

			if (dcBag.getBagProperty(DcBagProps.FIELD_DATASOURCE).equals(DataSource.INSTRUMENT.toString()))
			{
				// Hash check files in payload manifest.
				Set<Entry<String, String>> plManifestFiles = dcBag.getBag().getPayloadManifest(DcBag.BAGS_ALGORITHM).entrySet();
				for (Entry<String, String> iEntry : plManifestFiles)
				{
					BagFile iFile = dcBag.getBag().getBagFile(iEntry.getKey());

					// Check if file exists. Then check its hash value matches the one in the manifest.
					if (iFile == null || !iFile.exists())
						throw new DcBagException("Bag doesn't contain file " + iFile.getFilepath());
					if (!MessageDigestHelper.fixityMatches(iFile.newInputStream(), DcBag.BAGS_ALGORITHM, iEntry.getValue()))
						throw new DcBagException("Bag contains modified existing files.");
				}
			}
		}

	}
}
