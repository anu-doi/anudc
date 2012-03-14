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
 * @author Rahul Khanna
 * 
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

	public SparqlResultSet(Document resultsDoc)
	{
		this.resultsDoc = resultsDoc;
		this.xpFactory = XPathFactory.newInstance();

		// Extract the columns and resultsArr that have been returned.
		extractCols();
		extractResults();
	}

	private void extractCols()
	{
		try
		{
			NodeList variableNodes = (NodeList) xpFactory.newXPath().compile("/sparql/head/variable").evaluate(this.resultsDoc, XPathConstants.NODESET);
			this.numCols = variableNodes.getLength();
			this.colsArr = new String[this.numCols];

			for (int i = 0; i < numCols; i++)
			{
				// colsArr[i] = variableNodes.item(i).getAttributes().getNamedItem("name").getNodeValue();
				colsArr[i] = (String) xpFactory.newXPath().compile("attribute::name").evaluate(variableNodes.item(i), XPathConstants.STRING);
			}
			
			log.info("Number of columns in Search Resultset: " + this.numCols);
		}
		catch (XPathExpressionException e)
		{
			e.printStackTrace();
			this.numCols = 0;
		}
	}
	
	private void extractResults()
	{
		try
		{
			NodeList resultNodes = (NodeList) xpFactory.newXPath().compile("/sparql/results/result").evaluate(this.resultsDoc, XPathConstants.NODESET);
			this.numResults = resultNodes.getLength();
			this.resultsArr = new String[numResults][numCols];

			for (int iResult = 0; iResult < numResults; iResult++)
			{
				for (int jCol = 0; jCol < numCols; jCol++)
				{
					// NodeList row = (NodeList) xpFactory.newXPath().compile(colsArr[jCol] + "/text()").evaluate(resultNodes.item(iResult), XPathConstants.STRING);
					if (colsArr[jCol].equalsIgnoreCase("item"))
						resultsArr[iResult][jCol] = (String) xpFactory.newXPath().compile(colsArr[jCol] + "/attribute::uri")
								.evaluate(resultNodes.item(iResult), XPathConstants.STRING);
					else
						resultsArr[iResult][jCol] = (String) xpFactory.newXPath().compile(colsArr[jCol] + "/text()")
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

	public String[] getCols()
	{
		return this.colsArr;
	}

	public String[][] getAllResults()
	{
		return this.resultsArr;
	}
	
	public int getNumCols()
	{
		return numCols;
	}
	
	public int getNumResults()
	{
		return numResults;
	}
}
