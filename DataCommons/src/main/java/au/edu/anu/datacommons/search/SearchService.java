package au.edu.anu.datacommons.search;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;

import javax.annotation.Resource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import au.edu.anu.datacommons.util.Util;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.view.Viewable;
import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * SearchService
 * 
 * Australian National University Data Commons
 * 
 * Class provides a REST service using Jersey for searching the Fedora repository
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		26/03/2012	Rahul Khanna (RK)		Initial.
 * 0.2		04/05/2012	Genevieve Turner (GT)	Updated for the removal of the method 'runRiSearch'
 * </pre>
 * 
 */
@Component
@Path("/search")
public class SearchService
{
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Resource(name="riSearchService")
	SparqlPoster riSearchService;
	
	// TODO Once determined how object info such as published flag, group etc. are stored use this parameter to filter out results.
//	@QueryParam("filter") private String filter;

	/**
	 * doGetAsXml
	 * 
	 * Australian National University Data Commons
	 * 
	 * This method is called when the search service is accessed and the type requested is XML.
	 * 
	 * @return XML containing search results as a Response object.
	 * 
	 *         <pre>
	 * Version	Date		Developer				Description
	 * 0.1		23/03/2012	Rahul Khanna (RK)		Initial
	 * 0.2		04/05/2012	Genevieve Turner (GT)	Updated for the removal of the method 'runRiSearch'
	 * </pre>
	 */
	@GET
	@Produces(MediaType.TEXT_XML)
	public Response doGetAsXml(@QueryParam("q") String q, @QueryParam("filter") String filter)
	{
		if (!Util.isNotEmpty(q)) {
			return Response.status(400).build();
		}
		
		// Generate the SPARQL query from the terms
		SparqlQuery sparqlQuery = new SparqlQuery(q);

		ClientResponse respFromRiSearch = riSearchService.post(sparqlQuery.generateQuery());

		return Response.ok(respFromRiSearch.getEntity(String.class)).build();
	}

	/**
	 * doGetAsHtml
	 * 
	 * Australian National University Data Commons
	 * 
	 * This method is called when the search service is accessed and the type requested is HTML.
	 * 
	 * @return Response to display the Search JSP and passing an object to it.
	 * 
	 *         <pre>
	 * Version	Date		Developer				Description
	 * 0.1		23/03/2012	Rahul Khanna (RK)		Initial
	 * 0.2		04/05/2012	Genevieve Turner (GT)	Updated for the removal of the method 'runRiSearch'
	 * </pre>
	 */
	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response doGetAsHtml(@QueryParam("q") String q, @QueryParam("filter") String filter)
	{
		Response resp = null;
		
		// Perform search if terms to search are provided, else display the search page without any search results.
		if (Util.isNotEmpty(q))
		{
			// Generate the SPARQL query from the terms
			SparqlQuery sparqlQuery = new SparqlQuery(q);

			ClientResponse respFromRiSearch = riSearchService.post(sparqlQuery.generateQuery());

			try
			{
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				// This is disabled because it's causing problems when the elements in the doc are extracted using XPath. Probably because the SPARQL NS URI doesn't exist.
				// factory.setNamespaceAware(true);
				Document resultsXmlDoc = factory.newDocumentBuilder().parse(new InputSource(new StringReader(respFromRiSearch.getEntity(String.class))));
				SparqlResultSet resultSet = new SparqlResultSet(resultsXmlDoc);
				
				HashMap<String, Object> model = new HashMap<String, Object>();
				model.put("resultSet", resultSet);

				resp = Response.ok(new Viewable("/search.jsp", model)).build();
			}
			catch (SAXException e)
			{
				LOGGER.error("Exception with XML: ", e);
			}
			catch (ParserConfigurationException e)
			{
				LOGGER.error("Exception with XML: ", e);
			}
			catch (IOException e)
			{
				LOGGER.error("Exception with XML: ", e);
			}
		}
		else
		{
			resp = Response.ok(new Viewable("/search.jsp")).build();
		}

		return resp;
	}
}