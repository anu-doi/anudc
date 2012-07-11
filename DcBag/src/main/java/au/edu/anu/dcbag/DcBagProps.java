package au.edu.anu.dcbag;

import gov.loc.repository.bagit.BagFile;
import gov.loc.repository.bagit.utilities.namevalue.impl.AbstractNameValueMapListBagFile;

public class DcBagProps extends AbstractNameValueMapListBagFile
{
	private static final String TYPE = "DC-Props";

	public static final String FIELD_DATASOURCE = "Data-Source";
	public static final String DCPROPS_FILEPATH = "dc-props.txt";

	public enum DataSource
	{
		INSTRUMENT("instrument"), GENERAL("general");

		String value;
		private DataSource(String value)
		{
			this.value = value;
		}
		
		@Override
		public String toString()
		{
			return this.value;
		}
	}

	public DcBagProps(String filepath, String encoding)
	{
		super(filepath, encoding);
	}

	public DcBagProps(String filepath, BagFile bagFile, String encoding)
	{
		super(filepath, bagFile, encoding);
	}

	@Override
	public String getType()
	{
		return TYPE;
	}
}
