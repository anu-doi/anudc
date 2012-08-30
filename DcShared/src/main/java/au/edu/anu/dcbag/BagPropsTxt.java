package au.edu.anu.dcbag;

import java.text.MessageFormat;

import gov.loc.repository.bagit.BagFile;
import gov.loc.repository.bagit.utilities.namevalue.impl.AbstractNameValueMapListBagFile;

public class BagPropsTxt extends AbstractNameValueMapListBagFile
{
	private static final String TYPE = "Bag-Props";
	public static final String FILEPATH = "bag-props.txt";

	@Deprecated
	public static final String FIELD_DATASOURCE = "Data-Source";

	public enum Key
	{
		DATASOURCE("Data-Source");
		
		String value;
		private Key(String value)
		{
			this.value = value;
		}
		
		@Override
		public String toString()
		{
			return this.value;
		}
	}
	
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
		
		public static DataSource getValueOf(String value)
		{
			if (value.toLowerCase().equals(INSTRUMENT.toString()))
				return INSTRUMENT;
			else if (value.toLowerCase().equals(GENERAL.toString()))
				return GENERAL;
			else
				throw new IllegalArgumentException(MessageFormat.format("Invalid DataSource type {0}.", value));
		}
		
	}

	public BagPropsTxt(String filepath, String encoding)
	{
		super(filepath, encoding);
	}

	public BagPropsTxt(String filepath, BagFile bagFile, String encoding)
	{
		super(filepath, bagFile, encoding);
	}

	@Override
	public String getType()
	{
		return TYPE;
	}
}
