package au.edu.anu.datacommons.xml.sparql;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Sparql
 * 
 * Australian National University Data Commons
 * 
 * Defines the 'sparql' node of a risearch result. Utilised when transforming the result return
 * to a java class.
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		01/08/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
@XmlRootElement(name="sparql", namespace="http://www.w3.org/2001/sw/DataAccess/rf1/result")
public class Sparql {
	Results results;

	/**
	 * getResults
	 *
	 * Get the results
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/07/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the results
	 */
	@XmlElement(namespace="http://www.w3.org/2001/sw/DataAccess/rf1/result")
	public Results getResults() {
		return results;
	}

	/**
	 * setResults
	 *
	 * Set the results
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/07/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param results the results to set
	 */
	public void setResults(Results results) {
		this.results = results;
	}
	
}
