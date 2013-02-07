package au.edu.anu.dcbag;

import static java.text.MessageFormat.*;
import gov.loc.repository.bagit.BagFile;
import gov.loc.repository.bagit.utilities.namevalue.impl.AbstractNameValueMapListBagFile;

/**
 *  This class represents bag-props.txt file in a bag that contains Data Commons specific attributes of a bag and its contents. The file stores keys and values as
 *  specified for tag files in the BagIt specification.
 */
public class BagPropsTxt extends AbstractNameValueMapListBagFile
{
	private static final String TYPE = "Bag-Props";
	public static final String FILEPATH = "bag-props.txt";

	@Deprecated
	public static final String FIELD_DATASOURCE = "Data-Source";

	/**
	 * Valid keys against which values can be stored.
	 */
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
				throw new IllegalArgumentException(format("Invalid DataSource type {0}.", value));
		}
		
	}
	
	/**
	 * Instantiates a new bag props txt.
	 * 
	 * @param filepath
	 *            the filepath
	 * @param encoding
	 *            the encoding
	 */
	public BagPropsTxt(String filepath, String encoding)
	{
		super(filepath, encoding);
	}

	/**
	 * Instantiates an existing bag props txt in a bag.
	 * 
	 * @param filepath
	 *            the filepath of file
	 * @param bagFile
	 *            the bag file if the file already exists in bag
	 * @param encoding
	 *            the encoding to be used in the text file
	 */
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
