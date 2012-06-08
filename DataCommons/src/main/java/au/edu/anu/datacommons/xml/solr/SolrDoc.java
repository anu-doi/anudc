package au.edu.anu.datacommons.xml.solr;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * SolrDoc
 * 
 * Australian National University Data Commons
 * 
 * Placeholder
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
@XmlJavaTypeAdapter(SolrMapAdapter.class)
public class SolrDoc {
	Map<String, List<String>> returnVals;
	
	/**
	 * Constructor
	 * 
	 * Initial Constructor
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		08/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 *
	 */
	public SolrDoc() {
		returnVals = new HashMap<String, List<String>>();
	}
	
	/**
	 * getReturnVals
	 *
	 * Gets the return values
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		08/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return
	 */
	public Map<String, List<String>> getReturnVals() {
		return returnVals;
	}

	/**
	 * setReturnVals
	 *
	 * Sets the return values
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		08/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param returnVals The return values
	 */
	public void setReturnVals(Map<String, List<String>> returnVals) {
		this.returnVals = returnVals;
	}

}
