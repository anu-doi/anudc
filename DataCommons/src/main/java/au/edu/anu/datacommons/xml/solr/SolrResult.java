package au.edu.anu.datacommons.xml.solr;

import java.util.List;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * SolrResult
 * 
 * Australian National University Data Commons
 * 
 * Class that processes the solr result
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
@XmlRootElement(name="result")
public class SolrResult {
	private String name;
	private Integer numFound;
	private Integer start;
	private List<SolrDoc> docs;
	
	/**
	 * getName
	 *
	 * Gets the name of the result
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		08/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return The name of the result
	 */
	@XmlAttribute(name="name")
	public String getName() {
		return name;
	}
	
	/**
	 * setName
	 *
	 * Sets the name of the result
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		08/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param name The name of the result
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * getNumFound
	 *
	 * Gets the number of records found
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		08/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return The number of records found
	 */
	@XmlAttribute(name="numFound")
	public Integer getNumFound() {
		return numFound;
	}
	
	/**
	 * setNumFound
	 *
	 * Sets the number of records found
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		08/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param numFound The number of records found
	 */
	public void setNumFound(Integer numFound) {
		this.numFound = numFound;
	}

	/**
	 * getStart
	 *
	 * Gets the offset for the search results
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		08/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return The offset
	 */
	@XmlAttribute(name="start")
	public Integer getStart() {
		return start;
	}
	
	/**
	 * setStart
	 *
	 * Sets the offset for the search results
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		08/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param start
	 */
	public void setStart(Integer start) {
		this.start = start;
	}
	
	/**
	 * getDocs
	 *
	 * Gets the records retrieved from the solr search engine
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		08/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return A list of records retrieved
	 */
	@XmlAnyElement
	public List<SolrDoc> getDocs() {
		return docs;
	}

	/**
	 * setDocs
	 *
	 * Sets the list of records retrieved
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		08/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param docs The list of records retrieved
	 */
	public void setDocs(List<SolrDoc> docs) {
		this.docs = docs;
	}
}
