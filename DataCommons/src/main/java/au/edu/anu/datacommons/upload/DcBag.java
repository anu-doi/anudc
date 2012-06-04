package au.edu.anu.datacommons.upload;

import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.Bag.Format;
import gov.loc.repository.bagit.BagFactory;
import gov.loc.repository.bagit.BagFactory.LoadOption;
import gov.loc.repository.bagit.BagFile;
import gov.loc.repository.bagit.BagInfoTxt;
import gov.loc.repository.bagit.FetchTxt.FilenameSizeUrl;
import gov.loc.repository.bagit.Manifest.Algorithm;
import gov.loc.repository.bagit.ManifestHelper;
import gov.loc.repository.bagit.PreBag;
import gov.loc.repository.bagit.utilities.BagVerifyResult;
import gov.loc.repository.bagit.utilities.MessageDigestHelper;
import gov.loc.repository.bagit.writer.Writer;
import gov.loc.repository.bagit.writer.impl.FileSystemWriter;
import gov.loc.repository.bagit.writer.impl.ZipWriter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.properties.GlobalProps;

public class DcBag
{
	private static final Logger LOGGER = LoggerFactory.getLogger(DcBag.class);
	private static final Algorithm BAGS_ALGORITHM = Algorithm.MD5;
	private static final BagFactory bf = new BagFactory();

	private Bag bag;
	private String bagName;

	public DcBag(String bagName)
	{
		bag = bf.createBag();
		this.bagName = bagName;
		generateTagFiles();
	}

	public DcBag(String bagName, File bagFileDir)
	{
		PreBag preBag = bf.createPreBag(bagFileDir);
		bag = preBag.makeBagInPlace(BagFactory.LATEST, false, true);
		bag.setFile(bagFileDir);
		this.bagName = bagName;
	}

	public DcBag(File bagFile, LoadOption loadOption)
	{
		bag = bf.createBag(bagFile, loadOption);
		bag.setFile(bagFile);
	}

	public void addFileToPayload(File fileToAdd)
	{
		bag.addFileToPayload(fileToAdd);
	}

	public void addFetchEntry(String filename, long filesize, String url)
	{
		// Fetch file
		if (bag.getFetchTxt() == null)
			bag.putBagFile(bag.getBagPartFactory().createFetchTxt());

		bag.getFetchTxt().add(new FilenameSizeUrl(filename, filesize, url));
	}
	
	public List<FilenameSizeUrl> getFetchEntries()
	{
		return this.bag.getFetchTxt();
	}

	public void save() throws Exception
	{
		if (bag.getFormat() != null)
			save(bag.getFormat());
		else
			save(Format.FILESYSTEM);
	}

	public void save(Format format) throws Exception
	{
		bag.makeComplete();

		if (verifyComplete().isSuccess() == false)
			throw new Exception("Incomplete Bag.");

		if (verifyValid().isSuccess() == false)
			throw new Exception("Invalid Bag.");

		if (format == Format.FILESYSTEM)
		{
			FileSystemWriter writer = new FileSystemWriter(bf);

			if (bag.getFile() == null)
			{
				File bagFile = new File(GlobalProps.getBagsDirAsFile(), this.bagName);
				writer.write(bag, bagFile);
				this.bag.setFile(bagFile);
			}
			else
			{
				writer.write(bag, bag.getFile());
			}
		}
		else if (format == Format.ZIP)
		{
			ZipWriter writer = new ZipWriter(bf);
			// TODO Setting a compression level throws exception.
			// writer.setCompressionLevel(9);

			if (bag.getFile() == null)
			{
				File bagFile = new File(GlobalProps.getBagsDirAsFile(), this.bagName + ".zip");
				writer.write(bag, bagFile);
				this.bag.setFile(bagFile);
			}
			else
			{
				writer.write(bag, bag.getFile());
			}
		}
		else
		{
			throw new Exception("Invalid bag format specified.");
		}
	}

	public Set<Entry<String, String>> getPayloadFileList()
	{
		return this.bag.getPayloadManifest(BAGS_ALGORITHM).entrySet();
	}

	public BagInfoTxt getBagInfoTxt()
	{
		return bag.getBagInfoTxt();
	}

	public void changeFormat()
	{
		Format bagFormat = bag.getFormat();
		Writer writer;
		String filePath;

		if (bagFormat.isSerialized)
		{
			// De-serialise it.
			writer = new FileSystemWriter(bf);
			filePath = bag.getFile().getAbsolutePath();
			filePath = filePath.substring(0, filePath.lastIndexOf('.'));
		}
		else
		{
			// Serialise it.
			writer = new ZipWriter(bf);
			filePath = bag.getFile().getAbsolutePath() + ".zip";
		}

		LOGGER.debug("Writing " + filePath);
		writer.write(bag, new File(filePath));
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
	
	public File extractFile(String baggedFileToExtract, File extractDir) throws Exception
	{
		BagFile baggedFile = bag.getBagFile(baggedFileToExtract);
		BufferedInputStream inStream = new BufferedInputStream(baggedFile.newInputStream());
		BufferedOutputStream outStream;
		byte[] buffer = new byte[8192];			// 8K.
		int numBytesRead;
		File outputFile = new File(extractDir, baggedFileToExtract);

		outputFile.getParentFile().mkdirs();
		outputFile.createNewFile();

		outStream = new BufferedOutputStream(new FileOutputStream(outputFile));
		while ((numBytesRead = inStream.read(buffer)) != -1)
		{
			outStream.write(buffer, 0, numBytesRead);
		}

		// Perform hash check.
		String hash = this.bag.getChecksums(baggedFileToExtract).get(BAGS_ALGORITHM);
		String check = MessageDigestHelper.generateFixity(outputFile, BAGS_ALGORITHM);
		if (!hash.equals(check))
		{
			outputFile.delete();
			throw new Exception("Mismatched Hash values.");
		}

		return outputFile;
	}
	
	public void generateTagFiles()
	{
		// Generate payload manifest
		if (bag.getPayloadManifest(BAGS_ALGORITHM) == null)
		{
			bag.putBagFile(bag.getBagPartFactory().createManifest(ManifestHelper.getPayloadManifestFilename(BAGS_ALGORITHM, bag.getBagConstants())));
		}

		// Tag Manifest
		if (bag.getTagManifest(BAGS_ALGORITHM) == null)
		{
			bag.putBagFile(bag.getBagPartFactory().createManifest(ManifestHelper.getTagManifestFilename(BAGS_ALGORITHM, bag.getBagConstants())));
		}

		// BagItTxt
		if (bag.getBagItTxt() == null)
		{
			bag.putBagFile(bag.getBagPartFactory().createBagItTxt());
		}

		// BagInfoTxt
		if (bag.getBagInfoTxt() == null)
		{
			bag.putBagFile(bag.getBagPartFactory().createBagInfoTxt());
		}
	}

	public File getBagAsFile()
	{
		return this.bag.getFile();
	}
	
	public BagVerifyResult verifyComplete()
	{
		return this.bag.verifyComplete();
	}

	public BagVerifyResult verifyValid()
	{
		return this.bag.verifyValid();
	}

	public void close()
	{
		if (this.bag != null)
			this.bag.close();
	}
}
