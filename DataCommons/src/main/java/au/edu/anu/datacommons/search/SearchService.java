package au.edu.anu.datacommons.search;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import au.edu.anu.datacommons.properties.GlobalProps;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
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
 * Version	Date		Developer			Description
 * 0.1		26/03/2012	Rahul Khanna (RK)	Initial.
 * </pre>
 * 
 */
@Path("/search")
public class SearchService
{
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@QueryParam("q") private String q;
	// TODO Once determined how object info such as published flag, group etc. are stored use this parameter to filter out results.
	@QueryParam("filter") private String filter;

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
	 * Version	Date		Developer			Description
	 * 0.1		23/03/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 */
	@GET
	@Produces(MediaType.TEXT_XML)
	public Response doGetAsXml()
	{
		ClientResponse respFromRiSearch = runRiSearch();

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
	 * Version	Date		Developer			Description
	 * 0.1		23/03/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 */
	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response doGetAsHtml()
	{
		Response resp = null;

		// Perform search if terms to search are provided, else display the search page without any search results.
		if (q != null && !q.equals(""))
		{
			ClientResponse respFromRiSearch = runRiSearch();

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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (ParserConfigurationException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
		{
			resp = Response.ok(new Viewable("/search.jsp")).build();
		}

		return resp;
	}

	/**
	 * runRiSearch
	 * 
	 * Australian National University Data Commons
	 * 
	 * This method sends a request to the RISearch service by Fedora. The response is an XML document.
	 * 
	 * @return Web service response with the status of request and entity.
	 * 
	 *         <pre>
	 * Version	Date		Developer			Description
	 * 0.1		26/03/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 */
	private ClientResponse runRiSearch()
	{
		// Initialise web service objects for RiSearch.
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		client.addFilter(new HTTPBasicAuthFilter(GlobalProps.getProperty(GlobalProps.PROP_FEDORA_USERNAME), GlobalProps
				.getProperty(GlobalProps.PROP_FEDORA_PASSWORD)));
		WebResource riSearchService = client.resource(UriBuilder.fromUri(GlobalProps.getProperty(GlobalProps.PROP_FEDORA_URI)).build());
		MultivaluedMap<String, String> queryMap = new MultivaluedMapImpl();

		// Assign constant parameters to queryMap.
		queryMap.add("dt", "on");
		queryMap.add("format", "Sparql");
		queryMap.add("lang", "sparql");
		queryMap.add("limit", "1000");
		queryMap.add("type", "tuples");

		// Throw exception if no search terms provided. JSP has validation to check this as well.
		if (q.trim().equals(""))
			throw new NullPointerException("Terms to search not specified");

		// Generate the SPARQL query from the terms
		SparqlQuery sparqlQuery = new SparqlQuery(q);

		// Send request to RiSearch service and return response.
		riSearchService = riSearchService.path(GlobalProps.getProperty(GlobalProps.PROP_FEDORA_RISEARCHURL));
		riSearchService = riSearchService.queryParams(queryMap);
		riSearchService = riSearchService.queryParam("query", sparqlQuery.generateQuery());
		ClientResponse respFromRiSearch = riSearchService.type(MediaType.APPLICATION_FORM_URLENCODED).accept(MediaType.TEXT_XML).post(ClientResponse.class);

		return respFromRiSearch;
	}
}
