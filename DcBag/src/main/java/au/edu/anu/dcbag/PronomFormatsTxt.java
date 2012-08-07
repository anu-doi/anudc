package au.edu.anu.dcbag;

import gov.loc.repository.bagit.BagFile;
import gov.loc.repository.bagit.utilities.namevalue.impl.AbstractNameValueBagFile;

public class PronomFormatsTxt extends AbstractNameValueBagFile
{
	private static final String TYPE = "PronomFormats";
	public static final String PRONOMFORMATS_FILEPATH = "pronom-formats.txt";

	public PronomFormatsTxt(String filepath, BagFile bagFile, String encoding)
	{
		super(filepath, bagFile, encoding);
	}

	public PronomFormatsTxt(String filepath, String encoding)
	{
		super(filepath, encoding);
	}

	@Override
	public String getType()
	{
		return TYPE;
	}
}
