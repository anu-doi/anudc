package au.edu.anu.datacommons.services;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;

import javax.annotation.Resource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import au.edu.anu.datacommons.search.SparqlPoster;
import au.edu.anu.datacommons.search.SparqlQuery;
import au.edu.anu.datacommons.search.SparqlResultSet;

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
			// For some reason XPath doesn't work properly if you directly get the document from the stream
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			Document resultsXmlDoc = factory.newDocumentBuilder().parse(new InputSource(new StringReader(riSearchResponse.getEntity(String.class))));
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
}
