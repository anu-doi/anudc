/*******************************************************************************
 * Australian National University Data Commons
 * Copyright (C) 2013  The Australian National University
 * 
 * This file is part of Australian National University Data Commons.
 * 
 * Australian National University Data Commons is free software: you
 * can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package au.edu.anu.datacommons.search;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.config.Config;
import au.edu.anu.datacommons.properties.GlobalProps;

/**
 * SparqlQuery
 * 
 * Australian National University Data Commons
 * 
 * Generates a SPARQL query with words to search as input. The terms can be words or phrases (enclosed in double quotes) and have 'AND' and 'OR' as binary
 * operators. The default binary operator is AND, which means when two words are provided without a binary operator, results containing both the terms will be
 * returned. The following are valid term strings:
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
 * Sparql query = new SparqlQuery("term1 term2 etc...");
 * String queryToExec = query.generateQuery();
 * </code>
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		14/03/2012	Rahul Khanna (RK)		Initial
 * 0.2		13/06/2012	Genevieve Turner (GT)	Changed query string output to debug
 * 0.1		17/07/2012	Genevieve Turner(GT)	Added the ability to add a set of triples
 * </pre>
 */
public final class SparqlQuery
{
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	private StringBuilder sparqlQuery;
	private ArrayList<String> terms;
	private String[] retFields;
	private String[] dcFieldsToSearch;

	private ArrayList<String> prefixes;
	private ArrayList<String> vars;
	private ArrayList<String> triples;
	private ArrayList<String> filters;
	private int offset = 0;
	private int limit = 0;

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

		prefixes = new ArrayList<String>();
		vars = new ArrayList<String>();
		triples = new ArrayList<String>();
		filters = new ArrayList<String>();

		// Read the list of fields to search terms in.
		dcFieldsToSearch = GlobalProps.getProperty(GlobalProps.PROP_SEARCH_SEARCHFIELDS).split(",");

		// Read the list of fields that are returned by the query.
		retFields = GlobalProps.getProperty(GlobalProps.PROP_SEARCH_RETURNFIELDS).split(",");
	}

	/**
	 * SparqlQuery
	 * 
	 * Autralian National University Data Commons
	 * 
	 * Overloaded Constructor for this class that accepts keywords to search for and calls the setTerms method automatically.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		28/03/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @param termsString
	 *            A space-separated list of keywords to search as a String.
	 */
	public SparqlQuery(String termsString)
	{
		this();
		setTerms(termsString);
	}

	public SparqlQuery(String termsString, int offset, int limit)
	{
		this(termsString);
		this.offset = offset;
		this.limit = limit;
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
		LOGGER.info("Terms split up: ");
		while (matcher.find())
		{
			terms.add(matcher.group(0).replaceAll("\"", ""));
			LOGGER.info("\"" + terms.get(terms.size() - 1) + "\"");
		}

		// Once the terms are set and the other elements required to create a SPARQL query for running a search.
		setDefaultSearchElements();
	}

	/**
	 * getOffset
	 * 
	 * Australian National University Data Commons
	 * 
	 * Gets the offset value in this query.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		08/05/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @return Offset as int. 0 if not specified.
	 */
	public int getOffset()
	{
		return offset;
	}

	/**
	 * setOffset
	 * 
	 * Australian National University Data Commons
	 * 
	 * Sets the offset value in this query.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		08/05/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @param offset
	 *            Offset as int.
	 */
	public void setOffset(int offset)
	{
		this.offset = offset;
	}

	/**
	 * getLimit
	 * 
	 * Australian National University Data Commons
	 * 
	 * Gets the limit value in this query that limits the number of search results returned.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		08/05/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @return Limit as int.
	 */
	public int getLimit()
	{
		return limit;
	}

	/**
	 * setLimit
	 * 
	 * Australian National University Data Commons
	 * 
	 * Sets the limit value in this query to limit the number of search results returned.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		08/05/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @param limit
	 *            Limit as int.
	 */
	public void setLimit(int limit)
	{
		this.limit = limit;
	}

	/**
	 * addPrefix
	 * 
	 * Autralian National University Data Commons
	 * 
	 * Adds a namespace prefix element
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		28/03/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @param prefix
	 *            A prefix string that will translate into a namespace URI. Colon following a prefix is optional and is automatically added. E.g. dc
	 * @param namespaceUri
	 *            The namespace URI the prefix will be translated into. Angle brackets surrounding the URI are optional and are automatically added. E.g.
	 *            "http://purl.org/dc/elements/1.1/"
	 */
	public void addPrefix(String prefix, String namespaceUri)
	{
		StringBuilder prefixLine = new StringBuilder();

		prefixLine.append("PREFIX ");
		prefixLine.append(prefix);
		if (prefix.charAt(prefix.length() - 1) != ':')
			prefixLine.append(":");
		prefixLine.append(" ");
		if (namespaceUri.charAt(0) != '<')
			prefixLine.append("<");
		prefixLine.append(namespaceUri);
		if (namespaceUri.charAt(namespaceUri.length() - 1) != '>')
			prefixLine.append(">");

		prefixes.add(prefixLine.toString());
	}

	/**
	 * addVar
	 * 
	 * Autralian National University Data Commons
	 * 
	 * Adds a field to the list of fields whose value is included in the resultset by the SPARQL query for the rows that meet the criteria (filters). "*" can be
	 * used to return all fields.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		28/03/2012	Rahul Khanna (RK)	Initial.
	 * </pre>
	 * 
	 * @param var
	 *            Field that's included in the resultset.
	 */
	public void addVar(String var)
	{
		StringBuilder formattedVar = new StringBuilder();
		if (var.charAt(0) != '?')
			formattedVar.append("?");
		formattedVar.append(var);
		vars.add(var);
	}

	/**
	 * addTriple
	 * 
	 * Autralian National University Data Commons
	 * 
	 * Adds a triple to the list of triples to be used for
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		28/03/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @param subject
	 *            Subject as string. '?' must precede a non-literal.
	 * @param predicate
	 *            A fully qualified predicate including namespace or namespace prefix if specified.
	 * @param object
	 * 
	 * @param isOptional
	 */
	public void addTriple(String subject, String predicate, String object, boolean isOptional)
	{
		StringBuilder tripleLine = new StringBuilder();

		if (isOptional)
			tripleLine.append("OPTIONAL {");
		tripleLine.append(subject);
		tripleLine.append(" ");
		tripleLine.append(predicate);
		tripleLine.append(" ");
		tripleLine.append(object);
		if (isOptional)
			tripleLine.append("}");

		triples.add(tripleLine.toString());
	}
	
	/**
	 * addTripleSet
	 *
	 * Provides a more flexible way of adding triples.  Useful for performing actions
	 * such as UNION.
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		17/07/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param triple The set of triples to add
	 */
	public void addTripleSet(String triple)
	{
		triples.add(triple);
	}

	/**
	 * addFilter
	 * 
	 * Autralian National University Data Commons
	 * 
	 * Adds a filter to the list of filters that comprise the criteria that that each object should match to be included in the resultset.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		28/03/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @param filter
	 *            A complete filter item as string.
	 * @param operator
	 *            The opeartor to use before this filter, such as "&&" or "||".
	 */
	public void addFilter(String filter, String operator)
	{
		StringBuilder filterLine = new StringBuilder();
		if (operator != null && !operator.equals(""))
		{
			filterLine.append(operator);
			filterLine.append(Config.NEWLINE);
		}

		filterLine.append("(");
		filterLine.append(Config.NEWLINE);
		filterLine.append(filter);
		filterLine.append(Config.NEWLINE);
		filterLine.append(")");

		filters.add(filterLine.toString());
	}

	/**
	 * setDefaultSearchTemplate
	 * 
	 * Autralian National University Data Commons
	 * 
	 * Adds the default elements for a SPARQL query for search.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		28/03/2012	Rahul Khanna (RK)	Initial.
	 * </pre>
	 */
	public void setDefaultSearchElements()
	{
		// Add DC prefix.
		addPrefix("dc", "http://purl.org/dc/elements/1.1/");

		// Add Vars.
		for (int i = 0; i < retFields.length; i++)
			addVar("?" + retFields[i]);

		// Add triples.
		for (int i = 0; i < dcFieldsToSearch.length; i++)
		{
			if (i == 0)
				addTriple("?item", "dc:" + dcFieldsToSearch[i], "?" + dcFieldsToSearch[i], false);
			else
				addTriple("?item", "dc:" + dcFieldsToSearch[i], "?" + dcFieldsToSearch[i], true);
		}

		// Generate a filter for each search term that has a regular expression for each field that should be searched for the term.
		for (int iTerm = 0; iTerm < terms.size(); iTerm++)
		{
			String operator;
			if (iTerm > 0)
			{
				if (terms.get(iTerm).equals("OR"))
				{
					operator = "||";
					iTerm++;
				}
				else if (terms.get(iTerm).equals("AND"))
				{
					operator = "&&";
					iTerm++;
				}
				else
				{
					operator = "&&";
				}
			}
			else
			{
				operator = "";
			}

			StringBuilder regExpForTerm = new StringBuilder();
			for (int jDcField = 0; jDcField < dcFieldsToSearch.length; jDcField++)
			{
				if (jDcField > 0)
				{
					regExpForTerm.append(Config.NEWLINE);
					regExpForTerm.append("|| ");
				}

				regExpForTerm.append("regex(");
				regExpForTerm.append("?");
				regExpForTerm.append(dcFieldsToSearch[jDcField]);
				regExpForTerm.append(", \"");
				regExpForTerm.append(terms.get(iTerm));
				regExpForTerm.append("\", ");
				regExpForTerm.append("\"i\")");
			}

			addFilter(regExpForTerm.toString(), operator);
		}
	}

	/**
	 * generateQuery
	 * 
	 * Autralian National University Data Commons
	 * 
	 * Creates a SPARQL query using the elements of a SPARQL query (prefixes, vars, triples and filters) that searches for terms in Dublin Core fields specified
	 * and returns fields specified as vars.
	 * 
	 * <code>
	 * PREFIX  dc:  <http://purl.org/dc/elements/1.1/>		// Prefixes
	 * SELECT ?item ?title ?description 					// Vars
	 * {
	 * ?item dc:title ?title							
	 * OPTIONAL {?item dc:creator ?creator}
	 * OPTIONAL {?item dc:subject ?subject}
	 * OPTIONAL {?item dc:description ?description}
	 * OPTIONAL {?item dc:publisher ?publisher}
	 * OPTIONAL {?item dc:contributor ?contributor}			// Triples (some may be optional)
	 * OPTIONAL {?item dc:date ?date}
	 * OPTIONAL {?item dc:type ?type}
	 * OPTIONAL {?item dc:format ?format}
	 * OPTIONAL {?item dc:identifier ?identifier}
	 * OPTIONAL {?item dc:source ?source}
	 * OPTIONAL {?item dc:language ?language}
	 * OPTIONAL {?item dc:relation ?relation}
	 * OPTIONAL {?item dc:coverage ?coverage}
	 * OPTIONAL {?item dc:rights ?rights}
	 * FILTER												// Filters
	 * (
	 * (
	 * regex(?title, "condition", "i")						// "condition" is a search keyword/phrase.
	 * || regex(?creator, "condition", "i")
	 * || regex(?subject, "condition", "i")
	 * || regex(?description, "condition", "i")
	 * || regex(?publisher, "condition", "i")
	 * || regex(?contributor, "condition", "i")
	 * || regex(?date, "condition", "i")
	 * || regex(?type, "condition", "i")
	 * || regex(?format, "condition", "i")
	 * || regex(?identifier, "condition", "i")
	 * || regex(?source, "condition", "i")
	 * || regex(?language, "condition", "i")
	 * || regex(?relation, "condition", "i")
	 * || regex(?coverage, "condition", "i")
	 * || regex(?rights, "condition", "i")
	 * )
	 * )
	 * }
	 * OFFSET 0												// Offset
	 * LIMIT 10												// Limit
	 * </code>
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		16/03/2012	Rahul Khanna (RK)	Initial
	 * 0.2		13/06/2012	Genevieve Turner (GT)	Changed query string output to debug
	 * </pre>
	 * 
	 * @return The SPARQL query as a String.
	 */
	public String generateQuery()
	{
		sparqlQuery = new StringBuilder();

		// Prefixes.
		for (String iPrefix : prefixes)
		{
			sparqlQuery.append(iPrefix);
			sparqlQuery.append(Config.NEWLINE);
		}

		// SELECT clause for vars.
		sparqlQuery.append("SELECT");
		for (String iVar : vars)
		{
			sparqlQuery.append(" ");
			sparqlQuery.append(iVar);
		}

		// WHERE clause for triples.
		sparqlQuery.append(Config.NEWLINE);
		sparqlQuery.append("WHERE {");
		sparqlQuery.append(Config.NEWLINE);
		sparqlQuery.append("");
		for (String iTriple : triples)
		{
			sparqlQuery.append(iTriple);
			sparqlQuery.append(" .");
			sparqlQuery.append(Config.NEWLINE);
		}

		// FILTER clause for Filters.
		if (filters.size() > 0)
		{
			sparqlQuery.append("FILTER (");
			sparqlQuery.append(Config.NEWLINE);

			for (String iFilter : filters)
			{
				sparqlQuery.append(iFilter);
				sparqlQuery.append(Config.NEWLINE);
			}

			sparqlQuery.append(")");
			sparqlQuery.append(Config.NEWLINE);
		}

		sparqlQuery.append("}");

		if (offset > 0)
		{
			sparqlQuery.append(Config.NEWLINE);
			sparqlQuery.append("OFFSET ");
			sparqlQuery.append(offset);
		}

		if (limit > 0)
		{
			sparqlQuery.append(Config.NEWLINE);
			sparqlQuery.append("LIMIT ");
			sparqlQuery.append(limit);
		}

		LOGGER.trace("Returning SPARQL query: {}", sparqlQuery);
		return sparqlQuery.toString();
	}
}
