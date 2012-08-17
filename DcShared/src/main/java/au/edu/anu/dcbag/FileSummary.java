package au.edu.anu.dcbag;

import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFile;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URI;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.stream.FileImageOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

import au.edu.anu.dcbag.clamscan.ScanResult;
import au.edu.anu.dcbag.clamscan.ScanResult.Status;
import au.edu.anu.dcbag.fido.PronomFormat;
import au.edu.anu.dcbag.metadata.MetadataExtractor;
import au.edu.anu.dcbag.metadata.MetadataExtractorImpl;

public class FileSummary
{
	private final String path;
	private final long sizeInBytes;
	private final String format;
	private final String formatPuid;
	private final String md5;
	private Map<String, String[]> metadata;
	private String scanResult;

	public FileSummary(DcBag bag, BagFile bf)
	{
		this.path = bf.getFilepath();
		this.sizeInBytes = bf.getSize();
		PronomFormat pFmt = bag.getPronomFormat(bf);
		if (pFmt != null)
		{
			this.format = pFmt.getFormatName();
			this.formatPuid = pFmt.getPuid();
		}
		else
		{
			this.format = "";
			this.formatPuid = "";
		}
		this.md5 = bag.getBagFileHash(bf.getFilepath());
		
		try
		{
			ObjectInputStream objInStream = new ObjectInputStream(bag.getBagFileStream("metadata/" + bf.getFilepath().substring(bf.getFilepath().indexOf('/') + 1) + ".ser"));
			this.metadata = (Map<String, String[]>) objInStream.readObject();
		}
		catch (Exception e)
		{
			this.metadata = new HashMap<String, String[]>();
		}
		
		BagFile virusScanTxt = bag.getBag().getBagFile(VirusScanTxt.VIRUSSCAN_FILEPATH);
		if (virusScanTxt != null)
		{
			VirusScanTxt vs = new VirusScanTxt(VirusScanTxt.VIRUSSCAN_FILEPATH, virusScanTxt, bag.getBag()
				.getBagItTxt().getCharacterEncoding());
			ScanResult sr = new ScanResult(vs.get(bf.getFilepath()));
			this.scanResult = sr.getStatus().toString();
			if (sr.getStatus() == Status.FAILED)
				this.scanResult += ", " + sr.getSignature();
		}
		else
		{
			this.scanResult = Status.ERROR.toString();
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
		MessageFormat msgFmt = new MessageFormat("{0, number, integer} {1}");
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
