package au.edu.anu.dcbag;

import gov.loc.repository.bagit.BagFile;
import gov.loc.repository.bagit.utilities.namevalue.impl.AbstractNameValueBagFile;

/**
 * Represents the tag file containing pronom file information about each payload file.
 */
public class PronomFormatsTxt extends AbstractNameValueBagFile
{
	private static final long serialVersionUID = 1L;
	private static final String TYPE = "PronomFormats";
	public static final String FILEPATH = "pronom-formats.txt";

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
