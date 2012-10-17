package au.edu.anu.datacommons.phenomics;

import java.util.List;
import java.util.Map;

import au.edu.anu.datacommons.webservice.bindings.DcRequest;

public interface Processable
{
	public List<DcRequest> generateActivityRequests();
	
	public List<DcRequest> generateCollectionRequests();
	
	public Map<DcRequest, Map<String, DcRequest>> generateDcRequests();
}
