/**
 * 
 */
package au.edu.anu.datacommons.search;

import java.util.logging.Logger;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * SparqlResultSet
 * 
 * Australian National University Data Commons
 * 
 * This class provides methods to access relevant values from a resultset obtained by running a SPARQL query.
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		16/03/2012	Rahul Khanna (RK)		Initial
 * </pre>
 */

public class SparqlResultSet
{
	// private static final String SPARQL_RESULT_NS = "http://www.w3.org/2001/sw/DataAccess/rf1/result";
	private final Logger log = Logger.getLogger(this.getClass().getName());
	private final Document resultsDoc;
	private final XPathFactory xpFactory;

	private String[] colsArr = null;
	private String[][] resultsArr = null;
	private int numCols;
	private int numResults;

	/**
	 * SparqlResultSet
	 * 
	 * Australian National University Data Commons
	 * 
	 * Constructor for the class.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		16/03/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @param resultsDoc
	 *            The output of a SPARQL query as an XML document
	 */
	public SparqlResultSet(Document resultsDoc)
	{
		// TODO This may need to be changed to resultDoc.clone()
		this.resultsDoc = resultsDoc;
		this.xpFactory = XPathFactory.newInstance();

		// Extract the columns and resultsArr that have been returned.
		extractCols();
		extractResults();
	}

	/**
	 * extractCols
	 * 
	 * Autralian National University Data Commons
	 * 
	 * Extracts the names of columns in the resultset. E.g. item, title, description. The list of fields returned is specified in the global properties file and
	 * is used by SparqlQuery class.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		16/03/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 */

	private void extractCols()
	{
		try
		{
			NodeList variableNodes = (NodeList) this.xpFactory.newXPath().compile("/sparql/head/variable").evaluate(this.resultsDoc, XPathConstants.NODESET);
			this.numCols = variableNodes.getLength();
			this.colsArr = new String[this.numCols];

			for (int i = 0; i < numCols; i++)
			{
				// colsArr[i] = variableNodes.item(i).getAttributes().getNamedItem("name").getNodeValue();
				colsArr[i] = (String) this.xpFactory.newXPath().compile("attribute::name").evaluate(variableNodes.item(i), XPathConstants.STRING);
			}

			log.info("Number of columns in Search Resultset: " + this.numCols);
		}
		catch (XPathExpressionException e)
		{
			e.printStackTrace();
			this.numCols = 0;
		}
	}

	/**
	 * extractResults
	 * 
	 * Autralian National University Data Commons
	 * 
	 * Extracts the rows from the SPARQL resultset from XML into an Array.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		16/03/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 */
	private void extractResults()
	{
		try
		{
			NodeList resultNodes = (NodeList) this.xpFactory.newXPath().compile("/sparql/results/result").evaluate(this.resultsDoc, XPathConstants.NODESET);
			this.numResults = resultNodes.getLength();
			this.resultsArr = new String[numResults][numCols];

			for (int iResult = 0; iResult < numResults; iResult++)
			{
				for (int jCol = 0; jCol < numCols; jCol++)
				{
					// NodeList row = (NodeList) this.xpFactory.newXPath().compile(colsArr[jCol] + "/text()").evaluate(resultNodes.item(iResult), XPathConstants.STRING);
					if (colsArr[jCol].equalsIgnoreCase("item"))
						resultsArr[iResult][jCol] = (String) this.xpFactory.newXPath().compile(colsArr[jCol] + "/attribute::uri")
								.evaluate(resultNodes.item(iResult), XPathConstants.STRING);
					else
						resultsArr[iResult][jCol] = (String) this.xpFactory.newXPath().compile(colsArr[jCol] + "/text()")
								.evaluate(resultNodes.item(iResult), XPathConstants.STRING);
				}
			}

			log.info("Number of search results: " + this.numResults);
		}
		catch (XPathExpressionException e)
		{
			e.printStackTrace();
			this.numResults = 0;
		}
	}

	/**
	 * getCols
	 * 
	 * Autralian National University Data Commons
	 * 
	 * Gets the column names for which values exist in the SPARQL resultset.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		16/03/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @return Column names as a String Array.
	 */
	public String[] getCols()
	{
		return this.colsArr;
	}

	/**
	 * getAllResults
	 * 
	 * Autralian National University Data Commons
	 * 
	 * Gets all rows of data from the resultset.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		16/03/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @return Query results as String[][]. The first dimension representing rows, second representing columns.
	 */
	public String[][] getAllResults()
	{
		return this.resultsArr;
	}

	/**
	 * getNumCols
	 * 
	 * Autralian National University Data Commons
	 * 
	 * Gets the number of columns in the resultset.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		16/03/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @return Number of columns as int.
	 */
	public int getNumCols()
	{
		return numCols;
	}

	/**
	 * getNumResults
	 * 
	 * Autralian National University Data Commons
	 * 
	 * Gets the number of rows in the resultset.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		16/03/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @return	Number of rows as int.
	 */
	public int getNumResults()
	{
		return numResults;
	}
}
