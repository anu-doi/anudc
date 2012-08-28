package au.edu.anu.dcbag;

import au.edu.anu.dcbag.BagPropsTxt.DataSource;
import au.edu.anu.dcbag.BagPropsTxt.Key;
import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFile;

public class BagSummary
{
	private final Bag bag;
	private BagPropsTxt bagPropsTxt = null;
	
	public BagSummary(Bag bag)
	{
		this.bag = bag;
	}
	
	public String getFriendlySize()
	{
		return bag.getBagInfoTxt().getBagSize();
	}
	
	public long getNumFiles()
	{
		return bag.getPayload().size();
	}
	
	public String getPid()
	{
		return bag.getBagInfoTxt().getExternalIdentifier();
	}
	
	public DataSource getDataSource()
	{
		// Read the Bag properties file.
		if (bagPropsTxt == null)
		{
			BagFile bagPropsTxtFile = bag.getBagFile(BagPropsTxt.FILEPATH);
			if (bagPropsTxtFile != null)
				this.bagPropsTxt = new BagPropsTxt(BagPropsTxt.FILEPATH, bagPropsTxtFile, bag.getBagItTxt().getCharacterEncoding());
		}
		
		// If the Bag properites file exists, read the data source. If data source not specified, set GENERAL.
		DataSource dataSource = null;
		if (this.bagPropsTxt != null)
		{
			String value = bagPropsTxt.get(Key.DATASOURCE.toString());
			if (value == null || value.length() == 0)
				dataSource = DataSource.GENERAL;
			dataSource = DataSource.valueOf(value);
		}
		
		return dataSource;
	}
}
