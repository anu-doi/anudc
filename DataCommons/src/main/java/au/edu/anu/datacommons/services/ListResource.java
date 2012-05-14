package au.edu.anu.datacommons.services;

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

import org.json.simple.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import au.edu.anu.datacommons.properties.GlobalProps;
import au.edu.anu.datacommons.search.SparqlPoster;
import au.edu.anu.datacommons.search.SparqlQuery;
import au.edu.anu.datacommons.search.SparqlResultSet;
import au.edu.anu.datacommons.util.Util;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.view.Viewable;

/**
 * ListResource
 * 
 * Australian National University Data Commons
 * 
 * Gets lists of items
 * 
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		04/05/2012	Genevieve Turner (GT)	Initial
 * </pre>
 * 
 */
@Component
@Path("list")
public class ListResource {
	static final Logger LOGGER = LoggerFactory.getLogger(ListResource.class);

	@Resource(name="riSearchService")
	SparqlPoster riSearchService;

	@Resource(name="riSearchJSONService")
	SparqlPoster riSearchJSONService;
	
	/**
	 * getTemplates
	 * 
	 * Returns a list of templates to display create new records
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		04/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * Returns a list of templates 
	 */
	@GET
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	@Produces(MediaType.TEXT_HTML)
	@Path("template")
	public Response getTemplates() {
		Response response = null;
		SparqlQuery sparqlQuery = new SparqlQuery();

		sparqlQuery.addVar("?item");
		sparqlQuery.addVar("?title");
		sparqlQuery.addVar("?description");
		sparqlQuery.addTriple("?item", "<dc:type>", "'Template'", false);
		sparqlQuery.addTriple("?item", "<dc:title>", "?title", false);
		sparqlQuery.addTriple("?item", "<dc:description>", "?description", true);
		
		if (riSearchService == null) {
			LOGGER.info("riSearchService is null");
		}
		else {
			LOGGER.info("riSearchService is not null");
		}

		ClientResponse riSearchResponse = riSearchService.post(sparqlQuery.generateQuery());
		try {
			String responseString = riSearchResponse.getEntity(String.class);
			LOGGER.debug("riSearchResponse: {}", responseString);
			// For some reason XPath doesn't work properly if you directly get the document from the stream
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			Document resultsXmlDoc = factory.newDocumentBuilder().parse(new InputSource(new StringReader(responseString)));
			SparqlResultSet resultSet = new SparqlResultSet(resultsXmlDoc);

			HashMap<String, Object> model = new HashMap<String, Object>();
			model.put("resultSet", resultSet);
			
			response = Response.ok(new Viewable("/listtemplate.jsp", model)).build();
		}
		catch (SAXException e)
		{
			LOGGER.error("Error creating document", e);
		}
		catch (ParserConfigurationException e)
		{
			LOGGER.error("Error creating document", e);
		}
		catch (IOException e)
		{
			LOGGER.error("Error creating document", e);
		}
		
		return response;
	}

	@GET
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	@Produces(MediaType.APPLICATION_JSON)
	@Path("items")
	public Response getItems(@QueryParam("title") String title, @QueryParam("type") String type) {
		Response response = null;
		if (Util.isNotEmpty(title)) {
			SparqlQuery sparqlQuery = new SparqlQuery();
	
			sparqlQuery.addVar("?item");
			sparqlQuery.addVar("?title");
			sparqlQuery.addTriple("?item", "<dc:title>", "?title", false);
			sparqlQuery.addTriple("?item", "<dc:type>", "?type", false);
			String titleFilterString = "regex(str(?title), '" + title + "', 'i')";
			sparqlQuery.addFilter(titleFilterString, "");
			String typeFilterString = "regex(str(?type), '" + type +"', 'i')";
			sparqlQuery.addFilter(typeFilterString.toString(), "&&");
			
			ClientResponse riSearchResponse = riSearchJSONService.post(sparqlQuery.generateQuery());
			LOGGER.info("Return statis is: {}", riSearchResponse.getStatus());
			String jsonArray = riSearchResponse.getEntity(String.class);
			LOGGER.info("JSON Response: {}", jsonArray);
			response = Response.ok(jsonArray, MediaType.APPLICATION_JSON).build();
		}
		else {
			response = Response.ok("", MediaType.APPLICATION_JSON).build();
		}
		return response;
	}
/*
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	@Produces(MediaType.APPLICATION_JSON)
	@Path("items")
	public Response getLinkTypes(String type) {
		Response response = null;
		
		return response;
	}*/
}
