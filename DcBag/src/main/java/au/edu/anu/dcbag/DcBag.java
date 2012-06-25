package au.edu.anu.dcbag;

import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.Bag.Format;
import gov.loc.repository.bagit.BagFactory;
import gov.loc.repository.bagit.BagFactory.LoadOption;
import gov.loc.repository.bagit.BagFile;
import gov.loc.repository.bagit.BagInfoTxt;
import gov.loc.repository.bagit.FetchTxt.FilenameSizeUrl;
import gov.loc.repository.bagit.Manifest.Algorithm;
import gov.loc.repository.bagit.ManifestHelper;
import gov.loc.repository.bagit.ProgressListenable;
import gov.loc.repository.bagit.ProgressListener;
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
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DcBag implements ProgressListenable
{
	private static final Logger LOGGER = LoggerFactory.getLogger(DcBag.class);
	private static final Algorithm BAGS_ALGORITHM = Algorithm.MD5;
	private static final BagFactory bf = new BagFactory();

	private Bag bag;
	private Set<ProgressListener> plSet = null;

	public DcBag(String extIdentifier)
	{
		this.bag = bf.createBag();
		generateTagFiles(extIdentifier);
	}

	public DcBag(File bagsDir, String extIdentifier, LoadOption loadOption)
	{
		this.bag = bf.createBag(getBagFile(bagsDir, convertToDiskSafe(extIdentifier)), loadOption);
	}

	public DcBag(File bagFile, LoadOption loadOption)
	{
		this.bag = bf.createBag(bagFile, loadOption);
	}

	public DcBag(Bag bag)
	{
		this.bag = bf.createBag(bag);
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

	public File save() throws Exception
	{
		if (this.bag.getFile() == null)
			throw new FileNotFoundException("Bag not already saved.");

		if (this.bag.getFormat() == null)
			throw new FileNotFoundException("Bag format not specified.");

		return saveAs(bag.getFile().getParentFile(), convertToDiskSafe(this.bag.getBagInfoTxt().getExternalIdentifier()), bag.getFormat());
	}

	public File saveAs(File bagsDir, String bagName, Format format) throws IOException
	{
		if (!this.bag.verifyValid().isSuccess())
			this.bag = this.bag.makeComplete();

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
			FileSystemWriter fsWriter = new FileSystemWriter(bf);
			copyProgressListeners(fsWriter);
			this.bag = fsWriter.write(this.bag, new File(bagsDir, bagFilename));
		}
		else if (format == Format.ZIP)
		{
			ZipWriter zipWriter = new ZipWriter(bf);
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
				this.bag = zipWriter.write(this.bag, bagFile);
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
		if (this.bag != null)
			try
			{
				this.bag.close();
			}
			catch (IOException e)
			{
			}
	}

	public Bag getBag()
	{
		return bag;
	}

	public void setBag(Bag bag)
	{
		this.bag = bag;
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
			bagFile = new File(bagsDir, convertToDiskSafe(bagName) + Format.values()[i].extension);
			if (bagFile.exists())
				break;
		}

		return bagFile;
	}

	public static boolean deleteDir(File dir)
	{
		if (dir.isDirectory())
		{
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++)
				if (!deleteDir(new File(dir, children[i])))
					return false;
		}
		// The directory is now empty so delete it
		return dir.delete();
	}

	public String getExternalIdentifier()
	{
		return this.bag.getBagInfoTxt().getExternalIdentifier();
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
			for (ProgressListener l : plSet)
				listenable.addProgressListener(l);
	}
	
	@Override
	protected void finalize()
	{
		this.close();
	}
}
