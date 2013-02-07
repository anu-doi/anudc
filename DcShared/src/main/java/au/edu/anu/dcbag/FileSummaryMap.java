package au.edu.anu.dcbag;

import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFile;

import java.util.HashMap;

/**
 * Represents a Map of FileSummary for each BagFile in a specified Bag.
 */
public class FileSummaryMap extends HashMap<BagFile, FileSummary>
{
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new file summary map.
	 * 
	 * @param bag
	 *            the bag containing files whose summary will be read
	 */
	public FileSummaryMap(Bag bag)
	{
		for (BagFile iBagFile : bag.getPayload())
			this.put(iBagFile, new FileSummary(bag, iBagFile));
	}
	
	/**
	 * Gets the file summary.
	 * 
	 * @param bagFilePath
	 *            the bag file path
	 * @return the file summary
	 */
	public FileSummary getFileSummary(String bagFilePath)
	{
		for (BagFile bagFile : this.keySet())
			if (bagFile.getFilepath().equals(bagFilePath))
				return this.get(bagFile);
		return null;
	}
}
