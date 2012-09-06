package au.edu.anu.dcbag;

import au.edu.anu.dcbag.BagPropsTxt.DataSource;
import au.edu.anu.dcbag.BagPropsTxt.Key;
import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFile;
import gov.loc.repository.bagit.BagInfoTxt;

public class BagSummary
{
	private final Bag bag;
	private BagPropsTxt bagPropsTxt = null;
	private FileSummaryMap fsMap = null;
	private BagInfoTxt bagInfoTxt = null;
	
	public BagSummary(Bag bag)
	{
		this.bag = bag;
		
		// Read bag properties file.
		BagFile bagPropsTxtFile = bag.getBagFile(BagPropsTxt.FILEPATH);
		if (bagPropsTxtFile != null)
			this.bagPropsTxt = new BagPropsTxt(BagPropsTxt.FILEPATH, bagPropsTxtFile, bag.getBagItTxt().getCharacterEncoding());
		
		// File summary map.
		this.fsMap = new FileSummaryMap(this.bag);
		
		// Bag Info Txt
		this.bagInfoTxt = bag.getBagInfoTxt();
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
		// If the Bag properites file exists, read the data source. If data source not specified, set GENERAL.
		DataSource dataSource = null;
		if (this.bagPropsTxt != null)
		{
			String value = bagPropsTxt.get(Key.DATASOURCE.toString());
			if (value == null || value.length() == 0)
				dataSource = DataSource.GENERAL;
			dataSource = DataSource.getValueOf(value);
		}
		
		return dataSource;
	}
	
	public FileSummaryMap getFileSummaryMap()
	{
		return fsMap;
	}

	public BagInfoTxt getBagInfoTxt()
	{
		return bagInfoTxt;
	}
}
