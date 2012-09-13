package au.edu.anu.dcbag;

import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFile;

import java.util.HashMap;

public class FileSummaryMap extends HashMap<BagFile, FileSummary>
{
	private static final long serialVersionUID = 1L;

	public FileSummaryMap(Bag bag)
	{
		for (BagFile iBagFile : bag.getPayload())
			this.put(iBagFile, new FileSummary(bag, iBagFile));
	}
}
