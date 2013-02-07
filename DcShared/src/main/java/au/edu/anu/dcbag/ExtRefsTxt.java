package au.edu.anu.dcbag;

import gov.loc.repository.bagit.BagFile;
import gov.loc.repository.bagit.utilities.namevalue.impl.AbstractNameValueBagFile;

/**
 * This class represents the tag file containing URL references to resources not stored within the ANU Data Commons.
 */
public class ExtRefsTxt extends AbstractNameValueBagFile
{
	private static final long serialVersionUID = 1L;
	private static final String TYPE = "ExtRefs";
	
	public static final String FILEPATH = "ext-refs.txt";

	public ExtRefsTxt(String filepath, BagFile bagFile, String encoding)
	{
		super(filepath, bagFile, encoding);
	}

	public ExtRefsTxt(String filepath, String encoding)
	{
		super(filepath, encoding);
	}
	
	@Override
	public String getType()
	{
		return TYPE;
	}
}
