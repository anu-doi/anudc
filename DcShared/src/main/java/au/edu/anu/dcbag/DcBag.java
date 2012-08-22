package au.edu.anu.dcbag;

import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.Bag.Format;
import gov.loc.repository.bagit.BagFactory;
import gov.loc.repository.bagit.BagFactory.LoadOption;
import gov.loc.repository.bagit.BagFile;
import gov.loc.repository.bagit.BagInfoTxt;
import gov.loc.repository.bagit.FetchTxt.FilenameSizeUrl;
import gov.loc.repository.bagit.Manifest;
import gov.loc.repository.bagit.Manifest.Algorithm;
import gov.loc.repository.bagit.ManifestHelper;
import gov.loc.repository.bagit.ProgressListenable;
import gov.loc.repository.bagit.ProgressListener;
import gov.loc.repository.bagit.transformer.Completer;
import gov.loc.repository.bagit.utilities.MessageDigestHelper;
import gov.loc.repository.bagit.utilities.SimpleResult;
import gov.loc.repository.bagit.writer.impl.FileSystemWriter;
import gov.loc.repository.bagit.writer.impl.ZipWriter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.dcbag.DcBagProps.DataSource;
import au.edu.anu.dcbag.fido.PronomFormat;

public class DcBag implements ProgressListenable
{
	private static final Logger LOGGER = LoggerFactory.getLogger(DcBag.class);
	public static final Algorithm BAGS_ALGORITHM = Algorithm.MD5;
	public static final BagFactory BAG_FACTORY = new BagFactory();

	private Bag bag;
	private Set<ProgressListener> plSet = null;

	public DcBag(String extIdentifier)
	{
		this.bag = BAG_FACTORY.createBag();
		generateTagFiles(extIdentifier);
	}

	public DcBag(File bagsDir, String extIdentifier, LoadOption loadOption)
	{
		this.bag = BAG_FACTORY.createBag(getBagFile(bagsDir, convertToDiskSafe(extIdentifier)), loadOption);
	}

	public DcBag(File bagFile, LoadOption loadOption)
	{
		this.bag = BAG_FACTORY.createBag(bagFile, loadOption);
	}

	public DcBag(Bag bag)
	{
		this.bag = BAG_FACTORY.createBag(bag);
	}

	public void addFileToPayload(File fileToAdd)
	{
		this.bag.addFileToPayload(fileToAdd);
	}

	public void removeBagFile(String filepath)
	{
		this.bag.removeBagFile(filepath);
	}

	public void addFetchEntry(String filename, long filesize, String url)
	{
		// Fetch file
		if (bag.getFetchTxt() == null)
			bag.putBagFile(bag.getBagPartFactory().createFetchTxt());
		bag.getFetchTxt().add(new FilenameSizeUrl(filename, filesize, url));
	}

	public File save() throws IOException, DcBagException
	{
		if (this.bag.getFile() == null)
			throw new FileNotFoundException("Bag not already saved.");

		if (this.bag.getFormat() == null)
			throw new FileNotFoundException("Bag format not specified.");

		return saveAs(bag.getFile().getParentFile(), convertToDiskSafe(this.bag.getBagInfoTxt().getExternalIdentifier()), bag.getFormat());
	}

	public File saveAs(File bagsDir, String bagName, Format format) throws IOException, DcBagException
	{
		customValidate();

		if (!this.bag.verifyValid().isSuccess())
			makeComplete();

		List<String> extIdList = this.bag.getBagInfoTxt().getExternalIdentifierList();
		if (extIdList.size() != 1)
		{
			extIdList.clear();
			extIdList.add(bagName);
		}

		String bagFilename = convertToDiskSafe(bagName) + format.extension;
		File bagFile = new File(bagsDir, bagFilename);
		if (format == Format.FILESYSTEM)
		{
			FileSystemWriter fsWriter = new FileSystemWriter(BAG_FACTORY);
			copyProgressListeners(fsWriter);
			Bag tempBag = fsWriter.write(this.bag, new File(bagsDir, bagFilename));
			this.bag.close();
			this.bag = tempBag;
		}
		else if (format == Format.ZIP)
		{
			ZipWriter zipWriter = new ZipWriter(BAG_FACTORY);
			copyProgressListeners(zipWriter);
			zipWriter.setCompressionLevel(ZipWriter.DEFAULT_COMPRESSION_LEVEL);
			// Code below has been added due to a bug in BagIt that a prevents a ZIP file from being updated.
			if (bag.getFile() != null && bag.getFile().compareTo(bagFile) == 0)
			{
				File tempFile = File.createTempFile("ANU_Dc", null);
				Bag tempBag = zipWriter.write(this.bag, tempFile);
				this.bag.close();
				this.bag = zipWriter.write(tempBag, bagFile);
				tempFile.delete();
			}
			else
			{
				Bag tempBag = zipWriter.write(this.bag, bagFile);
				this.bag.close();
				this.bag = tempBag;
			}
		}

		return bagFile;
	}

	public Set<Entry<String, String>> getPayloadFileList()
	{
		return this.bag.getPayloadManifest(BAGS_ALGORITHM).entrySet();
	}

	public BagInfoTxt getBagInfoTxt()
	{
		return bag.getBagInfoTxt();
	}

	public InputStream getBagFileStream(String baggedFile)
	{
		if (this.bag.getBagFile(baggedFile) != null)
			return this.bag.getBagFile(baggedFile).newInputStream();
		else
			return null;
	}

	public long getBagFileSize(String baggedFile)
	{
		return this.bag.getBagFile(baggedFile).getSize();
	}

	public String getBagFileHash(String baggedFile)
	{
		return this.bag.getChecksums(baggedFile).get(BAGS_ALGORITHM);
	}

	public File extractFile(String bagFilePath, File extractDir) throws Exception
	{
		BagFile bagFile = bag.getBagFile(bagFilePath);
		byte[] buffer = new byte[8192];			// 8K.

		updateProgress("Preparing file for extraction", bagFilePath, 0L, 1L);
		// Create the directory tree for the file to be extracted.
		File outputFile = new File(extractDir, bagFilePath);
		outputFile.getParentFile().mkdirs();
		outputFile.createNewFile();

		BufferedInputStream inStream = null;
		BufferedOutputStream outStream = null;
		try
		{
			inStream = new BufferedInputStream(bagFile.newInputStream());
			outStream = new BufferedOutputStream(new FileOutputStream(outputFile));
			long bytesDone = 0;
			int numBytesRead;
			while ((numBytesRead = inStream.read(buffer)) != -1)
			{
				outStream.write(buffer, 0, numBytesRead);
				updateProgress("Extracting file", bagFilePath, ++bytesDone * buffer.length, bagFile.getSize());
			}

			// Perform hash check.
			String hash = this.bag.getChecksums(bagFilePath).get(BAGS_ALGORITHM);
			String check = MessageDigestHelper.generateFixity(outputFile, BAGS_ALGORITHM);
			if (!hash.equals(check))
			{
				outputFile.delete();
				throw new Exception("Mismatched Hash values.");
			}
		}
		catch (Exception e)
		{
			updateProgress("Error extracting file", bagFilePath, 0L, 1L);
			throw e;
		}
		finally
		{
			IOUtils.closeQuietly(inStream);
			IOUtils.closeQuietly(outStream);
		}

		return outputFile;
	}

	public void generateTagFiles(String extIdentifier)
	{
		// BagItTxt
		if (bag.getBagItTxt() == null)
			bag.putBagFile(bag.getBagPartFactory().createBagItTxt());

		// Payload manifest
		if (bag.getPayloadManifest(BAGS_ALGORITHM) == null)
			bag.putBagFile(bag.getBagPartFactory().createManifest(ManifestHelper.getPayloadManifestFilename(BAGS_ALGORITHM, bag.getBagConstants())));

		// Tag Manifest
		if (bag.getTagManifest(BAGS_ALGORITHM) == null)
			bag.putBagFile(bag.getBagPartFactory().createManifest(ManifestHelper.getTagManifestFilename(BAGS_ALGORITHM, bag.getBagConstants())));

		// BagInfoTxt
		if (bag.getBagInfoTxt() == null)
			bag.putBagFile(bag.getBagPartFactory().createBagInfoTxt());
		bag.getBagInfoTxt().addExternalIdentifier(extIdentifier);
	}

	public File getFile()
	{
		return this.bag.getFile();
	}

	public SimpleResult verifyComplete()
	{
		return this.bag.verifyComplete();
	}

	public SimpleResult verifyValid()
	{
		return this.bag.verifyValid();
	}

	public void close()
	{
		IOUtils.closeQuietly(this.bag);
	}

	public Bag getBag()
	{
		return bag;
	}

	public void setBag(Bag bag)
	{
		this.bag = bag;
	}

	public String getExternalIdentifier()
	{
		return this.bag.getBagInfoTxt().getExternalIdentifier();
	}

	public DcBagProps getBagPropertyFile()
	{
		BagFile dcBagPropFile = this.bag.getBagFile(DcBagProps.DCPROPS_FILEPATH);
		if (dcBagPropFile == null)
			return null;
		else
			return new DcBagProps(DcBagProps.DCPROPS_FILEPATH, dcBagPropFile, this.bag.getBagItTxt().getCharacterEncoding());
	}

	public String getBagProperty(String key)
	{
		String value = null;
		DcBagProps bagProps = getBagPropertyFile();
		if (bagProps != null)
			value = bagProps.get(key);
		return value;
	}

	public void setBagProperty(String key, String value)
	{
		DcBagProps dcBagProps;
		// Check if the dc-props.txt file already exists. If not, then create it and add to bag.
		dcBagProps = getBagPropertyFile();
		if (dcBagProps == null)
		{
			dcBagProps = new DcBagProps(DcBagProps.DCPROPS_FILEPATH, bag.getBagItTxt().getCharacterEncoding());
		}

		// If the key doesn't already exist, add it, else update it.
		dcBagProps.put(key, value);
		String checksum = MessageDigestHelper.generateFixity(dcBagProps.newInputStream(), BAGS_ALGORITHM);
		bag.putBagFile(dcBagProps);
		bag.getTagManifest(BAGS_ALGORITHM).put(DcBagProps.DCPROPS_FILEPATH, checksum);
	}

	public void makeComplete()
	{
		this.bag = bag.makeComplete();
	}

	public void makeComplete(Completer c)
	{
		this.bag = bag.makeComplete(c);
	}

	public PronomFormatsTxt getPronomFormatsTxt()
	{
		BagFile pronomFormatsTxt = this.bag.getBagFile(PronomFormatsTxt.PRONOMFORMATS_FILEPATH);
		if (pronomFormatsTxt != null)
			return new PronomFormatsTxt(PronomFormatsTxt.PRONOMFORMATS_FILEPATH, pronomFormatsTxt, this.bag.getBagItTxt().getCharacterEncoding());
		else
			return null;
	}

	public PronomFormat getPronomFormat(BagFile bagFile)
	{
		return getPronomFormat(bagFile.getFilepath());
	}

	public PronomFormat getPronomFormat(String filePath)
	{
		PronomFormatsTxt pTxt = getPronomFormatsTxt();
		return new PronomFormat(pTxt.get(filePath));
	}

	public static String convertToDiskSafe(String source)
	{
		return source.trim().toLowerCase().replaceAll("\\*|\\?|\\\\|:|/|\\s", "_");
	}

	public static File getBagFile(File bagsDir, String bagName)
	{
		File bagFile = null;

		for (int i = 0; i < Format.values().length; i++)
		{
			File possibleBagFile = new File(bagsDir, convertToDiskSafe(bagName) + Format.values()[i].extension);
			if (possibleBagFile.exists())
			{
				bagFile = possibleBagFile;
				break;
			}
		}

		return bagFile;
	}

	@Override
	public void addProgressListener(ProgressListener progressListener)
	{
		if (this.plSet == null)
			this.plSet = new HashSet<ProgressListener>(1);
		this.plSet.add(progressListener);
	}

	@Override
	public void removeProgressListener(ProgressListener progressListener)
	{
		if (plSet != null)
			this.plSet.remove(progressListener);
	}

	public void customValidate() throws DcBagException
	{
		if (getBagProperty(DcBagProps.FIELD_DATASOURCE) != null
				&& getBagProperty(DcBagProps.FIELD_DATASOURCE).equals(DcBagProps.DataSource.INSTRUMENT.toString()))
		{
			// Verify the integrity of tagmanifest.
			Manifest tagManifest = bag.getTagManifest(BAGS_ALGORITHM);
			List<Manifest> payloadManifestList = bag.getPayloadManifests();

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

			if (getBagProperty(DcBagProps.FIELD_DATASOURCE).equals(DataSource.INSTRUMENT.toString()))
			{
				// Hash check files in payload manifest.
				Set<Entry<String, String>> plManifestFiles = bag.getPayloadManifest(BAGS_ALGORITHM).entrySet();
				for (Entry<String, String> iEntry : plManifestFiles)
				{
					BagFile iFile = bag.getBagFile(iEntry.getKey());

					// Check if file exists. Then check its hash value matches the one in the manifest.
					if (iFile == null || !iFile.exists())
						throw new DcBagException("Bag doesn't contain file " + iFile.getFilepath());
					if (!MessageDigestHelper.fixityMatches(iFile.newInputStream(), BAGS_ALGORITHM, iEntry.getValue()))
						throw new DcBagException("Bag contains modified existing files.");
				}
			}
		}
	}

	public void replaceWith(File newBagFile, boolean deleteOrig) throws DcBagException, IOException
	{
		// If data source is instrument then verify payload files.
		if (getBagProperty(DcBagProps.FIELD_DATASOURCE) != null
				&& getBagProperty(DcBagProps.FIELD_DATASOURCE).equals(DcBagProps.DataSource.INSTRUMENT.toString()))
		{
			Manifest curPlManifest = bag.getPayloadManifest(BAGS_ALGORITHM);

			Bag newBag = BAG_FACTORY.createBag(newBagFile, LoadOption.BY_MANIFESTS);
			Manifest newPlManifest = newBag.getPayloadManifest(BAGS_ALGORITHM);

			try
			{
				for (String iPlFile : curPlManifest.keySet())
				{
					// The new bag should contain the files already present in the current bag.
					if (!newPlManifest.containsKey(iPlFile))
					{
						LOGGER.error("New bag doesn't contain payload file: {}", iPlFile);
						throw new DcBagException("New bag doesn't contain file: " + iPlFile);
					}

					// Hashes for current payload files should match the hashes for the same files in the new bag.
					if (!curPlManifest.get(iPlFile).equals(newPlManifest.get(iPlFile)))
					{
						LOGGER.error("Hash doesn't match for file: {}", iPlFile);
						throw new DcBagException("Hash doesn't match for file: " + iPlFile);
					}
				}
			}
			catch (DcBagException e)
			{
				IOUtils.closeQuietly(newBag);
				throw e;
			}
			finally
			{
				newBag.close();
			}
		}

		IOUtils.closeQuietly(this.bag);
		File curBagFile = this.bag.getFile();
		if (curBagFile != null)
		{
			try
			{
				archiveBag(curBagFile);
			}
			catch (IOException e)
			{
				LOGGER.warn("Unable to archive bag");
			}
		}

		this.bag = BAG_FACTORY.createBag(newBagFile, LoadOption.BY_MANIFESTS);
		saveAs(curBagFile.getParentFile(), getExternalIdentifier(), Format.FILESYSTEM);

		if (deleteOrig)
			if (!FileUtils.deleteQuietly(newBagFile))
				LOGGER.warn("Unable to delete temporary uploaded bag.");
	}

	public Map<BagFile, FileSummary> getFileSummaryMap()
	{
		Map<BagFile, FileSummary> fsMap = new HashMap<BagFile, FileSummary>();
		for (BagFile iBagFile : this.bag.getPayload())
			fsMap.put(iBagFile, new FileSummary(this, iBagFile));

		return fsMap;
	}

	private void archiveBag(File file) throws IOException
	{
		Date dateNow = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
		File targetFile = new File(file.getParentFile(), file.getName() + "-" + dateFormat.format(dateNow));
		if (file.isDirectory())
			FileUtils.moveDirectory(file, targetFile);
		else
			FileUtils.moveFile(file, targetFile);
	}

	private void updateProgress(String activity, Object item, Long count, Long total)
	{
		if (plSet != null)
		{
			for (ProgressListener pl : this.plSet)
				pl.reportProgress(activity, item, count, total);
		}
	}

	private void copyProgressListeners(ProgressListenable listenable)
	{
		if (plSet != null)
			for (ProgressListener pl : this.plSet)
				listenable.addProgressListener(pl);
	}
}
