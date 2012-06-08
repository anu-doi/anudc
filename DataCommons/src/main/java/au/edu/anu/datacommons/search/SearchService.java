package au.edu.anu.datacommons.search;

import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import au.edu.anu.datacommons.search.RiSearchRequest.Format;
import au.edu.anu.datacommons.search.RiSearchRequest.Language;
import au.edu.anu.datacommons.search.RiSearchRequest.Type;
import au.edu.anu.datacommons.util.Util;
import au.edu.anu.datacommons.xml.solr.SolrResponse;
import au.edu.anu.datacommons.xml.transform.JAXBTransform;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.view.Viewable;

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
	private static final Logger LOGGER = LoggerFactory.getLogger(SearchService.class);
	private static final String SEARCH_JSP = "/search.jsp";

	@Resource(name = "riSearchService")
	ExternalPoster riSearchService;

	// TODO Once determined how object info such as published flag, group etc. are stored use this parameter to filter out results.
	//	@QueryParam("filter") private String filter;

	/**
	 * doGetAsXml
	 * 
	 * Australian National University Data Commons
	 * 
	 * This method is called when the search service is accessed and the type requested is XML.
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		23/03/2012	Rahul Khanna (RK)		Initial
	 * 0.2		04/05/2012	Genevieve Turner (GT)	Updated for the removal of the method 'runRiSearch'
	 * </pre>
	 * 
	 * @return XML containing search results as a Response object.
	 */
	@GET
	@Produces(MediaType.TEXT_XML)
	public Response doGetAsXml(@QueryParam("q") String q, @QueryParam("filter") String filter)
	{
		if (!Util.isNotEmpty(q))
		{
			return Response.status(400).build();
		}

		// Generate the SPARQL query from the terms
		SparqlQuery sparqlQuery = new SparqlQuery(q);

		ClientResponse respFromRiSearch = riSearchService.post("query", sparqlQuery.generateQuery());

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
	 * 0.3		8/05/2012	Rahul Khanna (RK)		Updated for RiSearchRequest object.
	 * </pre>
	 */
	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response doGetAsHtml(@QueryParam("q") String q, @QueryParam("offset") int offset, @QueryParam("limit") int limit, @QueryParam("filter") String filter)
	{
		Response resp = null;
		int totalResults;

		// Perform search if terms to search are provided, else display the search page without any search results.
		if (Util.isNotEmpty(q))
		{
			// Generate the SPARQL query from the search terms.
			SparqlQuery sparqlQuery = new SparqlQuery(q);

			// Run the query to get the total number of results, not the actual resultset itself.
			RiSearchRequest riSearchReq = new RiSearchRequest(Type.TUPLES, Language.SPARQL, Format.COUNT, sparqlQuery.generateQuery());
			ClientResponse respFromRiSearch = riSearchReq.execute();
			totalResults = Integer.parseInt(respFromRiSearch.getEntity(String.class));
			LOGGER.debug("Total results from query: {}.", totalResults);

			// To the SPARQL query already generated, add the offset and limit clauses and rerun the query to get the limited resultset as per offset and limit.
			sparqlQuery.setOffset(offset);
			sparqlQuery.setLimit(limit);
			riSearchReq.setParamFormat(Format.SPARQL);
			riSearchReq.setParamQuery(sparqlQuery.generateQuery());
			respFromRiSearch = riSearchReq.execute();

			// ClientResponse respFromRiSearch = riSearchService.post(sparqlQuery.generateQuery());

			// Wrap the resultset received in a SparqlResultSet object and include it in the model of the viewable.
			try
			{
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				// This is disabled because it's causing problems when the elements in the doc are extracted using XPath. Probably because the SPARQL NS URI doesn't exist.
				// factory.setNamespaceAware(true);
				Document resultsXmlDoc = factory.newDocumentBuilder().parse(new InputSource(new StringReader(respFromRiSearch.getEntity(String.class))));
				SparqlResultSet resultSet = new SparqlResultSet(resultsXmlDoc);

				HashMap<String, Object> model = new HashMap<String, Object>();
				model.put("resultSet", resultSet);
				model.put("totalResults", totalResults);

				resp = Response.ok(new Viewable(SEARCH_JSP, model)).build();
			}
			catch (Exception e)
			{
				LOGGER.error("Exception with XML: ", e);
				resp = Response.serverError().build();
			}
		}
		else
		{
			resp = Response.ok(new Viewable(SEARCH_JSP)).build();
		}

		return resp;
	}

	/**
	 * doGetAsJson
	 * 
	 * Australian National University Data Commons
	 * 
	 * Submits a request to RI Search Service. Returns response as JSON.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		08/05/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @param q
	 *            Search terms.
	 * @param offset
	 *            Offset as int.
	 * @param limit
	 *            Limit as int.
	 * @param filter
	 *            Filter as String.
	 * @return HTTP OK Response with JSON string as its entity.
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response doGetAsJson(@QueryParam("q") String q, @QueryParam("offset") int offset, @QueryParam("limit") int limit, @QueryParam("filter") String filter)
	{
		// Submit a request to the RI Search service.
		RiSearchRequest riSearchReq = new RiSearchRequest(Type.TUPLES, Language.SPARQL, Format.JSON, new SparqlQuery(q, offset, limit).generateQuery());
		ClientResponse respFromRiSearch = riSearchReq.execute();

		// Return response.
		return Response.ok(respFromRiSearch.getEntity(String.class), MediaType.APPLICATION_JSON_TYPE).build();
	}
}
