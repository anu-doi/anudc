package au.edu.anu.datacommons.xml.sparql;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAnyElement;

/**
 * Results
 * 
 * Australian National University Data Commons
 * 
 * Defines the 'results' node from a sparql query result.
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
public class Results {
	List<Result> results_;

	public Results() {
		results_ = new ArrayList<Result>();
	}
	
	/**
	 * getResults
	 *
	 * Gets the result nodes
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/07/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the results
	 */
	@XmlAnyElement
	public List<Result> getResults() {
		return results_;
	}

	/**
	 * setResults
	 *
	 * Sets the result nodes
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/07/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param results the results to set
	 */
	public void setResults(List<Result> results) {
		this.results_ = results;
	}
}
