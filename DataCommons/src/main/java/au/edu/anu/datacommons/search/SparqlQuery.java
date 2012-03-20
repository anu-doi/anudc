package au.edu.anu.datacommons.search;

import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import au.edu.anu.datacommons.properties.GlobalProps;

/**
 * SparqlQuery
 * 
 * Australian National University Data Commons
 * 
 * Generates a SPARQL query with words to search as input. The terms can be words or phrases (enclosed in double quotes) and have 'AND' and 'OR' as
 * binary operators. The default binary operator is AND, which means when two words are provided without a binary operator, results containing both the terms
 * will be returned. The following are valid term strings:
 * 
 * <ul>
 * <li>first second "third fourth"</li>
 * <li>first OR "second third"</li>
 * <li>first AND second OR third</li>
 * </ul>
 * 
 * Usage:
 * 
 * <code>
 * Sparql query = new SparqlQuery();
 * query.setTerms("term1 term2 etc...");
 * String queryToExec = query.generateQuery();
 * </code>
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		14/03/2012	Rahul Khanna (RK)		Initial
 * </pre>
 */
public final class SparqlQuery
{
	private final Logger log = Logger.getLogger(this.getClass().getName());

	private StringBuilder sparqlQuery;
	private ArrayList<String> terms;
	private String[] retFields;
	private String[] dcFieldsToSearch;

	/**
	 * SparqlQuery
	 * 
	 * Autralian National University Data Commons
	 * 
	 * Constructor for this class.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		16/03/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 */
	public SparqlQuery()
	{
		terms = new ArrayList<String>();
	}

	/**
	 * setTerms
	 * 
	 * Autralian National University Data Commons
	 * 
	 * Accepts a String of terms and splits them into individual terms.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		16/03/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @param termsString
	 *            Keywords to search. For example:
	 */
	public void setTerms(String termsString)
	{
		// Seperate out the phrases from individual words.
		Matcher matcher = Pattern.compile("\"[^\"]+\"|[^\"\\s]+").matcher(termsString);
		while (matcher.find())
		{
			terms.add(matcher.group(0).replaceAll("\"", ""));
			log.info(terms.get(terms.size() - 1));
		}

		// Read the list of fields to search terms in.
		dcFieldsToSearch = GlobalProps.getProperty(GlobalProps.PROP_SEARCH_SEARCHFIELDS).split(",");

		// Read the list of fields that are returned by the query.
		retFields = GlobalProps.getProperty(GlobalProps.PROP_SEARCH_RETURNFIELDS).split(",");
	}

	/**
	 * generateQuery
	 * 
	 * Autralian National University Data Commons
	 * 
	 * Generates the SPARQL query that searches for the terms set using setTerms method.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		16/03/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @return The SPARQL query as a String.
	 */
	public String generateQuery()
	{
		// TODO: Evaluate use of Jena instead of building a query using strings.
		sparqlQuery = new StringBuilder();

		sparqlQuery.append("PREFIX  dc:  <http://purl.org/dc/elements/1.1/>\r\n");
		sparqlQuery.append("SELECT ");
		for (int i = 0; i < retFields.length; i++)
		{
			sparqlQuery.append("?");
			sparqlQuery.append(retFields[i]);
			sparqlQuery.append(" ");
		}

		sparqlQuery.append("\r\n");
		sparqlQuery.append("{\r\n");
		for (int i = 0; i < dcFieldsToSearch.length; i++)
		{
			if (!dcFieldsToSearch[i].equalsIgnoreCase("title"))
			{
				sparqlQuery.append("OPTIONAL {");
			}

			sparqlQuery.append("?item dc:");
			sparqlQuery.append(dcFieldsToSearch[i]);
			sparqlQuery.append(" ?");
			sparqlQuery.append(dcFieldsToSearch[i]);

			if (!dcFieldsToSearch[i].equalsIgnoreCase("title"))
			{
				sparqlQuery.append("}");
			}

			sparqlQuery.append("\r\n");
		}

		sparqlQuery.append("FILTER\r\n");
		sparqlQuery.append("(\r\n");

		for (int iTerm = 0; iTerm < terms.size(); iTerm++)
		{
			if (iTerm > 0)
			{
				if (terms.get(iTerm).equals("OR"))
				{
					sparqlQuery.append("||\r\n");
					iTerm++;
				}
				else if (terms.get(iTerm).equals("AND"))
				{
					sparqlQuery.append("&&\r\n");
					iTerm++;
				}
				else
				{
					sparqlQuery.append("&&\r\n");
				}
			}

			sparqlQuery.append("(\r\n");

			for (int jSrchFld = 0; jSrchFld < dcFieldsToSearch.length; jSrchFld++)
			{
				if (jSrchFld > 0)
					sparqlQuery.append("|| ");

				sparqlQuery.append("regex(?");
				sparqlQuery.append(dcFieldsToSearch[jSrchFld]);
				sparqlQuery.append(", \"");
				sparqlQuery.append(terms.get(iTerm));
				sparqlQuery.append("\", \"i\")\r\n");
			}
			sparqlQuery.append(")\r\n");
		}

		sparqlQuery.append(")\r\n");
		sparqlQuery.append("}\r\n");

		log.info("Returning SPARQL query:\r\n" + sparqlQuery);
		return sparqlQuery.toString();
	}
}
