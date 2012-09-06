package au.edu.anu.dcbag;

import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFile;
import gov.loc.repository.bagit.Manifest.Algorithm;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URI;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.stream.FileImageOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.tika.exception.TikaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import au.edu.anu.dcbag.clamscan.ScanResult;
import au.edu.anu.dcbag.clamscan.ScanResult.Status;
import au.edu.anu.dcbag.fido.PronomFormat;
import au.edu.anu.dcbag.metadata.MetadataExtractor;
import au.edu.anu.dcbag.metadata.MetadataExtractorImpl;

public class FileSummary
{
	private static final Logger LOGGER = LoggerFactory.getLogger(FileSummary.class);

	private final String path;
	private final long sizeInBytes;
	private final String format;
	private final String formatPuid;
	private final String md5;
	private Map<String, String[]> metadata;
	private String scanResult;

	public FileSummary(Bag bag, BagFile bf)
	{
		this.path = bf.getFilepath();
		this.sizeInBytes = bf.getSize();
		this.md5 = bag.getChecksums(bf.getFilepath()).get(Algorithm.MD5);

		// Fido - Pronom format.
		PronomFormat pFmt = new PronomFormat(bag, bf);
		this.format = pFmt.getFormatName();
		this.formatPuid = pFmt.getPuid();

		// Serialised metadata.
		try
		{
			String serMetadataFilename = "metadata/" + bf.getFilepath().substring(bf.getFilepath().indexOf('/') + 1) + ".ser";
			ObjectInputStream objInStream = new ObjectInputStream(bag.getBagFile(serMetadataFilename).newInputStream());
			this.metadata = (Map<String, String[]>) objInStream.readObject();
		}
		catch (Exception e)
		{
			LOGGER.warn(e.getMessage(), e);
			this.metadata = new HashMap<String, String[]>();
		}

		// Virus scan results.
		BagFile virusScanTxt = bag.getBagFile(VirusScanTxt.FILEPATH);
		if (virusScanTxt != null)
		{
			VirusScanTxt vs = new VirusScanTxt(VirusScanTxt.FILEPATH, virusScanTxt, bag.getBagItTxt().getCharacterEncoding());
			String scanResultStr = vs.get(bf.getFilepath());
			if (scanResultStr == null || scanResultStr.length() == 0)
			{
				this.scanResult = "NOT SCANNED";
			}
			else
			{
				ScanResult sr = new ScanResult(scanResultStr);
				this.scanResult = sr.getStatus().toString();
				if (sr.getStatus() == Status.FAILED)
					this.scanResult += ", " + sr.getSignature();
			}
		}
		else
		{
			this.scanResult = "NOT SCANNED";
		}
	}

	public String getPath()
	{
		return path;
	}

	public long getSizeInBytes()
	{
		return sizeInBytes;
	}

	public String getFormat()
	{
		return format;
	}

	public String getFormatPuid()
	{
		return formatPuid;
	}

	public String getMd5()
	{
		return md5;
	}

	public Map<String, String[]> getMetadata()
	{
		return metadata;
	}

	public String getFriendlySize()
	{
		String friendlySize;
		MessageFormat msgFmt = new MessageFormat("{0,number,integer} {1}");
		if (sizeInBytes >= FileUtils.ONE_GB)
			friendlySize = msgFmt.format(new Object[] { sizeInBytes / FileUtils.ONE_GB, "GB" });
		else if (sizeInBytes >= FileUtils.ONE_MB)
			friendlySize = msgFmt.format(new Object[] { sizeInBytes / FileUtils.ONE_MB, "MB" });
		else if (sizeInBytes >= FileUtils.ONE_KB)
			friendlySize = msgFmt.format(new Object[] { sizeInBytes / FileUtils.ONE_KB, "KB" });
		else
			friendlySize = msgFmt.format(new Object[] { sizeInBytes, "bytes" });

		return friendlySize.toString();
	}

	public String getScanResult()
	{
		return scanResult;
	}
}
