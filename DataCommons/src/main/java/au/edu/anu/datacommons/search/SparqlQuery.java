/**
 * 
 */
package au.edu.anu.datacommons.search;

import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Rahul Khanna
 *
 */
public final class SparqlQuery
{
	private StringBuilder sparqlQuery;
	private String termsString;
	private ArrayList<String> terms;
	private String[] heads;
	private String[] dcFieldsToSearch;
	
	private final Logger log = Logger.getLogger(this.getClass().getName());
	
	public SparqlQuery()
	{
		terms = new ArrayList<String>();
	}
	
	public void setTerms(String termsString)
	{
		// Seperate out the phrases from individual words.
		Matcher matcher = Pattern.compile("\"[^\"]+\"|[^\"\\s]+").matcher(termsString);
		while (matcher.find())
		{
			terms.add(matcher.group(0).replaceAll("\"", ""));
			log.info(terms.get(terms.size() - 1));
		}
	}
	
	public String generateQuery()
	{
		// TODO: Evaluate use of Jena instead of building a query using strings.
		sparqlQuery = new StringBuilder();
		sparqlQuery.append("PREFIX  dc:  <http://purl.org/dc/elements/1.1/>\r\n");
		// TODO: Replace select values with elements of array head.
		sparqlQuery.append("SELECT ?item ?title ?description\r\n");
		sparqlQuery.append("{\r\n");
		sparqlQuery.append("?item dc:title ?title\r\n");
		// TODO: Replace the following with elements of array dcFields to Search.
		sparqlQuery.append("OPTIONAL {?item dc:creator ?creator}\r\n");
		sparqlQuery.append("OPTIONAL {?item dc:description ?description}\r\n");
		sparqlQuery.append("OPTIONAL {?item dc:subject ?subject}\r\n");
		sparqlQuery.append("OPTIONAL {?item dc:publisher ?publisher}\r\n");
		sparqlQuery.append("OPTIONAL {?item dc:contributor ?contributor}\r\n");
		sparqlQuery.append("OPTIONAL {?item dc:date ?date}\r\n");
		sparqlQuery.append("OPTIONAL {?item dc:type ?type}\r\n");
		sparqlQuery.append("OPTIONAL {?item dc:format ?format}\r\n");
		sparqlQuery.append("OPTIONAL {?item dc:identifier ?identifier}\r\n");
		sparqlQuery.append("OPTIONAL {?item dc:source ?source}\r\n");
		sparqlQuery.append("OPTIONAL {?item dc:language ?language}\r\n");
		sparqlQuery.append("OPTIONAL {?item dc:relation ?relation}\r\n");
		sparqlQuery.append("OPTIONAL {?item dc:coverage ?coverage}\r\n");
		sparqlQuery.append("OPTIONAL {?item dc:rights ?rights}\r\n");
		
		sparqlQuery.append("\r\n");
		sparqlQuery.append("FILTER\r\n");
		sparqlQuery.append("(\r\n");
		
		for (int i = 0; i < terms.size(); i++)
		{
			if (i > 0)
			{
				if (terms.get(i).equals("OR"))
				{
					sparqlQuery.append("||\r\n");
					i++;
				}
				else if (terms.get(i).equals("AND"))
				{
					sparqlQuery.append("&&\r\n");
					i++;
				}
				else
				{
					sparqlQuery.append("&&\r\n");
				}
			}

			sparqlQuery.append("(\r\n");
			// TODO: Replace the following with only the fields that need to be searched.
			sparqlQuery.append("regex(?title, \"" + terms.get(i) + "\", \"i\")\r\n");
			sparqlQuery.append("|| regex(?creator, \"" + terms.get(i) + "\", \"i\")\r\n");
			sparqlQuery.append("|| regex(?subject, \"" + terms.get(i) + "\", \"i\")\r\n");
			sparqlQuery.append("|| regex(?description, \"" + terms.get(i) + "\", \"i\")\r\n");
			sparqlQuery.append("|| regex(?publisher, \"" + terms.get(i) + "\", \"i\")\r\n");
			sparqlQuery.append("|| regex(?contributor, \"" + terms.get(i) + "\", \"i\")\r\n");
			sparqlQuery.append("|| regex(?date, \"" + terms.get(i) + "\", \"i\")\r\n");
			sparqlQuery.append("|| regex(?type, \"" + terms.get(i) + "\", \"i\")\r\n");
			sparqlQuery.append("|| regex(?format, \"" + terms.get(i) + "\", \"i\")\r\n");
			sparqlQuery.append("|| regex(?identifier, \"" + terms.get(i) + "\", \"i\")\r\n");
			sparqlQuery.append("|| regex(?source, \"" + terms.get(i) + "\", \"i\")\r\n");
			sparqlQuery.append("|| regex(?language, \"" + terms.get(i) + "\", \"i\")\r\n");
			sparqlQuery.append("|| regex(?relation, \"" + terms.get(i) + "\", \"i\")\r\n");
			sparqlQuery.append("|| regex(?coverage, \"" + terms.get(i) + "\", \"i\")\r\n");
			sparqlQuery.append("|| regex(?rights, \"" + terms.get(i) + "\", \"i\")\r\n");
			sparqlQuery.append(")\r\n");
			
		}
		
		sparqlQuery.append(")\r\n");
		sparqlQuery.append("}\r\n");
		
		log.info("returned SPARQL query:\r\n" + sparqlQuery);
		return sparqlQuery.toString();
	}
}
