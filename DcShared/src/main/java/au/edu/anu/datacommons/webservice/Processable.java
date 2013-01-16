package au.edu.anu.datacommons.webservice;

import java.util.Map;

import au.edu.anu.datacommons.webservice.bindings.DcRequest;
import au.edu.anu.datacommons.webservice.bindings.FedoraItem;

/**
 * Interface that identifies a class that is able to generate DcRequest objects for submission to the Data Commons Web Service along with their relation to
 * other records.
 */
public interface Processable
{
	/**
	 * Generates a Map of DcRequest objects with the relations of the object created/updated in that request to other records.
	 * 
	 * @return Map&lt;DcRequest, Map&lt;String, FedoraItem&gt;&gt;
	 */
	public Map<DcRequest, Map<String, FedoraItem>> generateDcRequests();
}
