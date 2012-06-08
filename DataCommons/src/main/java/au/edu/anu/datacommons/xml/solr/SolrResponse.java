package au.edu.anu.datacommons.xml.solr;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * SolrResponse
 * 
 * Australian National University Data Commons
 * 
 * Class to process the response from solr
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		08/06/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
@XmlRootElement(name="response")
public class SolrResponse {
	private SolrResult result;
	
	/**
	 * getResult
	 *
	 * Gets the result
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		08/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return The result from solr
	 */
	public SolrResult getResult() {
		return result;
	}
	
	/**
	 * setResult
	 *
	 * Sets the result
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		08/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param result the solr result to set
	 */
	public void setResult(SolrResult result) {
		this.result = result;
	}
	
}
