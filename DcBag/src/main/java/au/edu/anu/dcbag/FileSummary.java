package au.edu.anu.dcbag;

import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFile;

import java.net.URI;
import java.text.MessageFormat;
import java.util.Map;

import au.edu.anu.dcbag.clamscan.ScanResult;
import au.edu.anu.dcbag.fido.PronomFormat;
import au.edu.anu.dcbag.metadata.MetadataExtractorImpl;

public class FileSummary
{
	public static final long GIGABYTE = 1073741824L;
	public static final long MEGABYTE = 1048576L;
	public static final long KILOBYTE = 1024L;

	private final String path;
	private final long sizeInBytes;
	private final String format;
	private final String formatPuid;
	private final String md5;
	private final Map<String, String[]> metadata;
	private final ScanResult scanResult;

	public FileSummary(DcBag bag, BagFile bagFile)
	{
		this.path = bagFile.getFilepath();
		this.sizeInBytes = bagFile.getSize();
		PronomFormat pFmt = bag.getPronomFormat(bagFile);
		this.format = pFmt.getFormatName();
		this.formatPuid = pFmt.getPuid();
		this.md5 = bag.getBagFileHash(bagFile.getFilepath());
		this.metadata = new MetadataExtractorImpl(bagFile.newInputStream()).getMetadataMap();
		VirusScanTxt vs = new VirusScanTxt(VirusScanTxt.VIRUSSCAN_FILEPATH, bag.getBag().getBagFile(VirusScanTxt.VIRUSSCAN_FILEPATH), bag.getBag()
				.getBagItTxt().getCharacterEncoding());
		this.scanResult = new ScanResult(vs.get(bagFile.getFilepath()));
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
		if (sizeInBytes >= GIGABYTE)
			friendlySize = msgFmt.format(new Object[] { sizeInBytes / GIGABYTE, "GB" });
		else if (sizeInBytes >= MEGABYTE)
			friendlySize = msgFmt.format(new Object[] { sizeInBytes / MEGABYTE, "MB" });
		else if (sizeInBytes >= KILOBYTE)
			friendlySize = msgFmt.format(new Object[] { sizeInBytes / KILOBYTE, "KB" });
		else
			friendlySize = msgFmt.format(new Object[] { sizeInBytes, "bytes" });

		return friendlySize.toString();
	}

	public ScanResult getScanResult()
	{
		return scanResult;
	}
}