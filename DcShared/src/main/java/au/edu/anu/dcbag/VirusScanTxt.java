package au.edu.anu.dcbag;

import gov.loc.repository.bagit.BagFile;
import gov.loc.repository.bagit.utilities.namevalue.impl.AbstractNameValueBagFile;

public class VirusScanTxt extends AbstractNameValueBagFile
{
	private static final String TYPE = "VirusScan";
	public static final String FILEPATH = "virus-scan.txt";
	
	public VirusScanTxt(String filepath, BagFile bagFile, String encoding)
	{
		super(filepath, bagFile, encoding);
	}

	public VirusScanTxt(String filepath, String encoding)
	{
		super(filepath, encoding);
	}

	@Override
	public String getType()
	{
		return TYPE;
	}
}
