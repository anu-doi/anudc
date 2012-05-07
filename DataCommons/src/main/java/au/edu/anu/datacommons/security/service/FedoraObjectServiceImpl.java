package au.edu.anu.datacommons.security.service;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import au.edu.anu.datacommons.data.db.dao.FedoraObjectDAOImpl;
import au.edu.anu.datacommons.data.db.model.FedoraObject;
import au.edu.anu.datacommons.data.fedora.FedoraBroker;
import au.edu.anu.datacommons.data.fedora.FedoraReference;
import au.edu.anu.datacommons.search.SparqlPoster;
import au.edu.anu.datacommons.search.SparqlQuery;
import au.edu.anu.datacommons.search.SparqlResultSet;
import au.edu.anu.datacommons.util.Util;
import au.edu.anu.datacommons.xml.template.Template;
import au.edu.anu.datacommons.xml.transform.ViewTransform;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.view.Viewable;
import com.yourmediashelf.fedora.client.FedoraClientException;

/**
 * FedoraObjectServiceImpl
 * 
 * Australian National University Data Comons
 * 
 * Service implementation for Retrieving pages, creating, and saving information for
 * objects.
 * 
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		26/04/2012	Genevieve Turner (GT)	Initial
 * 0.2		02/05/2012	Genevieve Turner (GT)	Fixed issue with pid and added in related links
 * </pre>
 * 
 */
@Service("fedoraObjectServiceImpl")
public class FedoraObjectServiceImpl implements FedoraObjectService {
	static final Logger LOGGER = LoggerFactory.getLogger(FedoraObjectServiceImpl.class);

	@Resource(name="riSearchService")
	SparqlPoster riSearchService;
	
	/**
	 * getItemByName
	 * 
	 * Gets the fedora object given the pid
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/04/2012	Genevieve Turner (GT)	Initial
	 * 0.2		02/05/2012	Genevieve Turner (GT)	Updated to fix issue with url encoded pid
	 * </pre>
	 * 
	 * @param id The fedora object pid
	 * @return Returns the FedoraObject of the given pid
	 */
	public FedoraObject getItemByName(String pid) {
		LOGGER.debug("Retrieving object for: {}", pid);
		String decodedpid = null;
		try {
			decodedpid = URLDecoder.decode(pid,"UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			LOGGER.error("Error decoding pid: ", e);
		}
		if (decodedpid == null) {
			return null;
		}
		LOGGER.debug("Decoded pid: {}", decodedpid);
		FedoraObjectDAOImpl object = new FedoraObjectDAOImpl();
		FedoraObject item = (FedoraObject) object.getSingleByName(decodedpid);
		return item;
	}

	/**
	 * getViewPage
	 * 
	 * Transforms the given information into information for display
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/04/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param fedoraObject The item to transform to a display
	 * @param layout The layout that defines the flow of the items on the page
	 * @param tmplt The template that determines the fields on the screen
	 * @return Returns the viewable for the jsp file to pick up.
	 */
	public Viewable getViewPage(FedoraObject fedoraObject, String layout, String tmplt) {
		Viewable viewable = getPage(layout, tmplt, fedoraObject);
		return viewable;
	}
	
	/**
	 * getNewPage
	 * 
	 * Transforms the given information into information for display for a new page
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/04/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param layout The layout that defines the flow of the items on the page
	 * @param tmplt The template that determines the fields on the screen
	 * @return Returns the viewable for the jsp file to pick up.
	 */
	public Viewable getNewPage(String layout, String tmplt) {
		Viewable viewable = getPage(layout, tmplt, null);
		return viewable;
	}
	
	/**
	 * saveNew
	 * 
	 * Saves the information then displays a page with the given information
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/04/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param layout The layout to display the page
	 * @param tmplt The template that determines the fields on the screen
	 * @param form Contains the parameters from the request
	 * @return Returns the viewable for the jsp file to pick up.
	 */
	public Viewable saveNew(String layout, String tmplt, Map<String, List<String>> form) {
		//TODO place post logic and return a proper screen.
		String toPage = "/page.jsp";
		String page = null;
		FedoraObject fedoraObject = null;
		ViewTransform viewTransform = new ViewTransform();
		try {
			fedoraObject = viewTransform.saveData(tmplt, null, form);
			page = viewTransform.getPage(layout, null, fedoraObject, null, false);
		}
		catch (JAXBException e) {
			LOGGER.error("Exception transforming jaxb", e);
			toPage = "/error.jsp";
		}
		catch (FedoraClientException e) {
			LOGGER.error("Exception creating/retrieving objects", e);
			toPage = "/error.jsp";
		}
		
		//TODO this should probably be changed
		String sidepage = "buttons.jsp";
		
		Map<String, Object> values = new HashMap<String, Object>();
		values.put("page", page);
		values.put("sidepage", sidepage);
		values.put("fedoraObject", fedoraObject);
		Viewable viewable = new Viewable(toPage, values);
		
		return viewable;
	}

	/**
	 * getEditItem
	 * 
	 * Retrieves information about a particular field
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/04/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param fedoraObject The item to transform to a display
	 * @param layout The layout that defines the flow of the items on the page
	 * @param tmplt The template that determines the fields on the screen
	 * @param fieldName
	 * @return
	 */
	public String getEditItem(FedoraObject fedoraObject, String layout, String tmplt, String fieldName) {
		LOGGER.info("In get edit item");
		String fields = "";
		ViewTransform viewTransform = new ViewTransform();
		try {
			fields = viewTransform.getPage(layout, tmplt, fedoraObject, fieldName, true);
		}
		catch (FedoraClientException e) {
			LOGGER.error("Exception: ", e);
		}
		return fields;
	}

	/**
	 * getEditPage
	 * 
	 * Updates the object given the specified parameters, and form values.
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/04/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param fedoraObject The  fedora object to get the page for
	 * @param layout The layout to use with display (i.e. the xsl stylesheet)
	 * @param tmplt The template that determines the fields on the screen
	 * @return Returns the viewable for the jsp file to pick up.
	 */
	public Viewable getEditPage(FedoraObject fedoraObject, String layout, String tmplt) {
		Viewable viewable = getPage(layout, tmplt, fedoraObject);

		Object model = viewable.getModel();
		if(model instanceof Map) {
			Map<String, Object> values = (Map<String, Object>) model;
			try {
				// Add the template objects to the viewable, and change the side page
				Template template = new ViewTransform().getTemplateObject(tmplt, fedoraObject);
				values.put("template", template);
				values.put("fedoraObject", fedoraObject);
				values.put("sidepage", "edit.jsp");
				
			}
			catch (FedoraClientException e) {
				LOGGER.error("Exception: ", e);
			}
			catch (JAXBException e) {
				LOGGER.error("Error transforming object: ", e);
			}
		}
		
		return viewable;
	}
	
	/**
	 * saveEdit
	 * 
	 * Updates the object given the specified parameters, and form values.
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/04/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param fedoraObject The  fedora object to get the page for
	 * @param tmplt The template that determines the fields on the screen
	 * @param form The form fields of the screen
	 * @return Returns the viewable for the jsp file to pick up.
	 */
	public Viewable saveEdit(FedoraObject fedoraObject, String layout, String tmplt, Map<String, List<String>> form) {
		String toPage = "/page.jsp";
		String sidepage = "edit.jsp";
		String page = null;
		Template template = null;
		ViewTransform viewTransform = new ViewTransform();
		try {
			fedoraObject = viewTransform.saveData(tmplt, fedoraObject, form);
			page = viewTransform.getPage(layout, null, fedoraObject, null, false);
			template = new ViewTransform().getTemplateObject(tmplt, fedoraObject);
		}
		catch (JAXBException e) {
			LOGGER.error("Exception transforming jaxb", e);
			toPage = "/error.jsp";
		}
		catch (FedoraClientException e) {
			LOGGER.error("Exception creating/retrieving objects", e);
			toPage = "/error.jsp";
		}

		Map<String, Object> values = new HashMap<String, Object>();
		values.put("page", page);
		values.put("sidepage", sidepage);
		values.put("template", template);

		SparqlResultSet resultSet = getLinks(fedoraObject);
		values.put("resultSet", resultSet);
		
		Viewable viewable = new Viewable(toPage, values);
		
		return viewable;
	}
	
	/**
	 * addLink
	 * 
	 * Create a link between two items
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/04/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param fedoraObject The item to transform to a display
	 * @param form Contains the parameters from the request
	 * @return A response for the web page
	 */
	public String addLink(FedoraObject fedoraObject, Map<String, List<String>> form) {
		String value = "<html><body>Reference added</body></html>";
		String link = "http://anu.edu.au/related/";
		FedoraReference reference = new FedoraReference();
		String referenceType = form.get("txtType").get(0);
		String referenceItem = form.get("txtItem").get(0);
		reference.setPredicate_(link + referenceType);
		reference.setObject_(referenceItem);
		reference.setIsLiteral_(Boolean.FALSE);
		try {
			FedoraBroker.addRelationship(fedoraObject.getObject_id(), reference);
		}
		catch (Exception e) {
			LOGGER.error("Exception adding relationship", e);
			value = "<html><body>Exception adding reference</body></html>";
		}
		
		//TODO update the return for this
		return value;
	}

	/**
	 * getPage
	 * 
	 * Retrieves a page for the given values
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/04/2012	Genevieve Turner (GT)	Initial
	 * 0.2		03/05/2012	Genevieve Turner (GT)	Updated to add related links to the page
	 * </pre>
	 * 
	 * @param layout The layout to use with display (i.e. the xsl stylesheet)
	 * @param tmplt The template that determines the fields on the screen
	 * @param fedoraObject The object of the page to retrieve
	 * @return Returns the viewable for the jsp file to pick up.
	 */
	private Viewable getPage(String layout, String template, FedoraObject fedoraObject) {
		if (fedoraObject == null) {
			LOGGER.info("Fedora Object is null");
		}
		String toPage = "/page.jsp";
		String page = null;
		ViewTransform viewTransform = new ViewTransform();
		try {
			page = viewTransform.getPage(layout, template, fedoraObject, null, false);
		}
		catch (FedoraClientException e) {
			LOGGER.error("Exception: ", e);
			toPage = "/error.jsp";
		}

		if(!Util.isNotEmpty(page)) {
			LOGGER.error("Page is empty");
			toPage = "/error.jsp";
		}
		
		Map<String, Object> values = new HashMap<String, Object>();
		values.put("page", page);
		if (fedoraObject != null) {
			//TODO This is should probably be modified
			values.put("fedoraObject", fedoraObject);

			String sidepage = "buttons.jsp";
			values.put("sidepage", sidepage);
			
			SparqlResultSet resultSet = getLinks(fedoraObject);
			values.put("resultSet", resultSet);
		}
		
		Viewable viewable = new Viewable(toPage, values);
		
		return viewable;
	}
	
	/**
	 * getLinks
	 * 
	 * Retrieves the links for a page.
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.3		26/04/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param fedoraObject The object to retrieve the links for
	 * @return The results of the query
	 */
	private SparqlResultSet getLinks(FedoraObject fedoraObject) {
		SparqlResultSet resultSet = null;
		SparqlQuery sparqlQuery = new SparqlQuery();
		
		sparqlQuery.addVar("?item");
		sparqlQuery.addVar("?title");
		sparqlQuery.addVar("?predicate");
		sparqlQuery.addTriple("<info:fedora/" + fedoraObject.getObject_id() + ">", "?predicate", "?item", false);
		sparqlQuery.addTriple("?item", "<dc:title>", "?title", false);
		sparqlQuery.addFilter("regex(str(?predicate), 'http://anu.edu.au/related', 'i')", "");
		
		ClientResponse respFromRiSearch = riSearchService.post(sparqlQuery.generateQuery());
		try {
			// For some reason XPath doesn't work properly if you directly get the document from the stream
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			Document resultsXmlDoc = factory.newDocumentBuilder().parse(new InputSource(new StringReader(respFromRiSearch.getEntity(String.class))));
			resultSet = new SparqlResultSet(resultsXmlDoc);
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
		return resultSet;
	}
}
