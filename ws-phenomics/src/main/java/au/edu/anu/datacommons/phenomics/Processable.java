package au.edu.anu.datacommons.phenomics;

import java.util.List;
import java.util.Map;

import au.edu.anu.datacommons.webservice.bindings.DcRequest;
import au.edu.anu.datacommons.webservice.bindings.FedoraItem;

public interface Processable
{
	public Map<DcRequest, Map<String, FedoraItem>> generateDcRequests();
}
