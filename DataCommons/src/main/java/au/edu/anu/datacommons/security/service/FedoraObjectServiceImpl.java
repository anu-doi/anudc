package au.edu.anu.datacommons.security.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import au.edu.anu.datacommons.connection.db.dao.FedoraObjectDAO;
import au.edu.anu.datacommons.connection.db.model.FedoraObject;
import au.edu.anu.datacommons.connection.fedora.FedoraBroker;
import au.edu.anu.datacommons.connection.fedora.FedoraReference;
import au.edu.anu.datacommons.util.Util;
import au.edu.anu.datacommons.xml.template.Template;
import au.edu.anu.datacommons.xml.transform.ViewTransform;

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
 * </pre>
 * 
 */
@Service("fedoraObjectServiceImpl")
public class FedoraObjectServiceImpl implements FedoraObjectService {
	static final Logger LOGGER = LoggerFactory.getLogger(FedoraObjectServiceImpl.class);
	
	/**
	 * getItemByName
	 * 
	 * Gets the fedora object given the pid
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/04/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param id The fedora object pid
	 * @return Returns the FedoraObject of the given pid
	 */
	public FedoraObject getItemByName(String pid) {
		FedoraObjectDAO object = new FedoraObjectDAO();
		FedoraObject item = object.getSingleByName(pid);
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
		Viewable viewable = getPage(layout, tmplt, fedoraObject.getObject_id());
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
	 * @param tmplt The template that determines the fields on the screen
	 * @param form Contains the parameters from the request
	 * @return Returns the viewable for the jsp file to pick up.
	 */
	public Viewable saveNew(String tmplt, Map<String, List<String>> form) {
		//TODO place post logic and return a proper screen.
		String toPage = "/page.jsp";
		String page = null;
		ViewTransform viewTransform = new ViewTransform();
		try {
			String item = viewTransform.saveData(tmplt, null, form);
			page = viewTransform.getPage("def:display", null, item, null, false);
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
			fields = viewTransform.getPage(layout, tmplt, fedoraObject.getObject_id(), fieldName, true);
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
		Viewable viewable = getPage(layout, tmplt, fedoraObject.getObject_id());

		Object model = viewable.getModel();
		if(model instanceof Map) {
			Map<String, Object> values = (Map<String, Object>) model;
			try {
				// Add the template objects to the viewable, and change the side page
				Template template = new ViewTransform().getTemplateObject(tmplt, fedoraObject.getObject_id());
				values.put("template", template);
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
	public Viewable saveEdit(FedoraObject fedoraObject, String tmplt, Map<String, List<String>> form) {
		String toPage = "/page.jsp";
		String sidepage = "edit.jsp";
		String page = null;
		Template template = null;
		ViewTransform viewTransform = new ViewTransform();
		try {
			String item = viewTransform.saveData(tmplt, fedoraObject.getObject_id(), form);
			page = viewTransform.getPage("def:display", null, item, null, false);
			template = new ViewTransform().getTemplateObject(tmplt, item);
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
		//String referenceType = form.getFirst("txtType");
		//String referenceItem = form.getFirst("txtItem");
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
	 * </pre>
	 * 
	 * @param layout The layout to use with display (i.e. the xsl stylesheet)
	 * @param tmplt The template that determines the fields on the screen
	 * @param item The pid of the page to retrieve
	 * @return Returns the viewable for the jsp file to pick up.
	 */
	private Viewable getPage(String layout, String template, String item) {
		String toPage = "/page.jsp";
		String page = null;
		ViewTransform viewTransform = new ViewTransform();
		try {
			page = viewTransform.getPage(layout, template, item, null, false);
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
		
		//TODO This is should probably be modified
		String sidepage = "buttons.jsp";
		
		values.put("sidepage", sidepage);
		
		Viewable viewable = new Viewable(toPage, values);
		
		return viewable;
	}
}
