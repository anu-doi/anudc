package au.edu.anu.datacommons.services;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.connection.fedora.FedoraBroker;
import au.edu.anu.datacommons.connection.fedora.FedoraReference;
import au.edu.anu.datacommons.util.Util;
import au.edu.anu.datacommons.xml.template.Template;
import au.edu.anu.datacommons.xml.transform.ViewTransform;

import com.sun.jersey.api.representation.Form;
import com.sun.jersey.api.view.Viewable;
import com.yourmediashelf.fedora.client.FedoraClientException;

/**
 * DisplayResource
 * 
 * Australian National University Data Comons
 * 
 * Displays a page given given the specified data
 * 
 * Version	Date		Developer				Description
 * 0.1		08/03/2012	Genevieve Turner (GT)	Initial
 * 0.2		19/03/2012	Genevieve Turner (GT)	Updating to return a page
 * 0.3		23/03/2012	Genevieve Turner (GT)	Updated to include saving of new records and a side page
 * 0.4		29/03/2012	Genevieve Turner (GT)	Updated to include editing
 * 
 */
@Path("/display")
public class DisplayResource {
	static final Logger LOGGER = LoggerFactory.getLogger(DisplayResource.class);

	@Context UriInfo uriInfo;

	/**
	 * getItem
	 * 
	 * Version	Date		Developer				Description
	 * 0.1		08/03/2012	Genevieve Turner (GT)	Initial
	 * 0.2		19/03/2012	Genevieve Turner (GT)	Updating to return a page
	 * 0.3		23/03/2012	Genevieve Turner (GT)	Updated to contain information about the side page
	 * 
	 * @param layout The layout to use with display (i.e. the xsl stylesheet)
	 * @param template The template that determines the fields on the screen
	 * @param item The item to retrieve data for
	 * @return Returns the viewable for the jsp file to pick up.
	 */
	@GET
	@Produces(MediaType.TEXT_HTML)
	public Viewable getItem(@QueryParam("layout") String layout, @QueryParam("tmplt") String template, @QueryParam("item") String item)
	{
		String toPage = "/page.jsp";
		
		String page = null;
		ViewTransform viewTransform = new ViewTransform();
		
		try {
			page = viewTransform.getPage(layout, template, item, null);
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
	
	/**
	 * postItem
	 * 
	 * Version	Date		Developer				Description
	 * 0.2		20/03/2012	Genevieve Turner (GT)	Added as a placeholder
	 * 0.3		23/03/2012	Genevieve Turner (GT)	Modified to save data
	 * 
	 * @param layout The layout to use with display (i.e. the xsl stylesheet)
	 * @param template The template that determines the fields on the screen
	 * @param item The item to retrieve data for
	 * @param form Form data that has been posted
	 * @return Returns the viewable for the jsp file to pick up.
	 */
	@POST
	@Path("/new")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_HTML)
	public Viewable postItem(@QueryParam("layout") String layout, @QueryParam("tmplt") String template, @QueryParam("item") String item, Form form)
	{
		//TODO place post logic and return a proper screen.
		String toPage = "/page.jsp";
		String page = null;
		ViewTransform viewTransform = new ViewTransform();
		try {
			item = viewTransform.saveData(template, null, form);
			page = viewTransform.getPage("def:display", null, item, null);
			
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
	 * editItem
	 * 
	 * Return a transformed page for a single field type.
	 * 
	 * Version	Date		Developer				Description
	 * 0.4		29/03/2012	Genevieve Turner (GT)	Added
	 * 
	 * @param layout The layout to use with display (i.e. the xsl stylesheet)
	 * @param template The template that determines the fields on the screen
	 * @param item The item to retrieve data for
	 * @param form Form data that has been posted
	 * @return Returns the viewable for the jsp file to pick up.
	 */
	@GET
	@Path("/edit/{fieldName}")
	@Produces(MediaType.TEXT_HTML)
	public String editItem(@QueryParam("layout") String layout, @QueryParam("tmplt") String template, @QueryParam("item") String item, @PathParam("fieldName") String fieldName)
	{
		String fields = "";
		ViewTransform viewTransform = new ViewTransform();
		try {
			fields = viewTransform.getPage(layout, template, item, fieldName);
		}
		catch (FedoraClientException e) {
			LOGGER.error("Exception: ", e);
		}
		return fields;
	}

	/**
	 * editItem
	 * 
	 * Gets a page given the specified parameters, that is enabled for editing.
	 * 
	 * Version	Date		Developer				Description
	 * 0.4		29/03/2012	Genevieve Turner (GT)	Added
	 * 
	 * @param layout The layout to use with display (i.e. the xsl stylesheet)
	 * @param template The template that determines the fields on the screen
	 * @param item The item to retrieve data for
	 * @return Returns the viewable for the jsp file to pick up.
	 */
	@GET
	@Path("/edit")
	@Produces(MediaType.TEXT_HTML)
	public Viewable editItem(@QueryParam("layout") String layout, @QueryParam("tmplt") String tmplt, @QueryParam("item") String item)
	{
		// get the viewable with code already pre-existing
		Viewable viewable = getItem(layout, tmplt, item);
		Object model = viewable.getModel();
		if(model instanceof Map) {
			Map<String, Object> values = (Map<String, Object>) model;
			try {
				// Add the template objects to the viewable, and change the side page
				Template template = new ViewTransform().getTemplateObject(tmplt, item);
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
	 * editChangeItem
	 * 
	 * Updates the object given the specified parameters, and form values.
	 * 
	 * Version	Date		Developer				Description
	 * 0.4		29/03/2012	Genevieve Turner (GT)	Added
	 * 
	 * @param layout The layout to use with display (i.e. the xsl stylesheet)
	 * @param template The template that determines the fields on the screen
	 * @param item The item to retrieve data for
	 * @param form Form data that has been posted
	 * @return Returns the viewable for the jsp file to pick up.
	 */
	@POST
	@Path("/edit")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_HTML)
	public Viewable editChangeItem(@QueryParam("layout") String layout, @QueryParam("tmplt") String tmplt, @QueryParam("item") String item, Form form)
	{
		String toPage = "/page.jsp";
		String sidepage = "edit.jsp";
		String page = null;
		Template template = null;
		ViewTransform viewTransform = new ViewTransform();
		try {
			item = viewTransform.saveData(tmplt, item, form);
			page = viewTransform.getPage("def:display", null, item, null);
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
	 * Add a link to the specified object.
	 * 
	 * Version	Date		Developer				Description
	 * 0.4		29/03/2012	Genevieve Turner (GT)	Added
	 * 
	 * @param item The item to retrieve data for
	 * @param form Form data that has been posted
	 * @return Returns the viewable for the jsp file to pick up.
	 */
	@POST
	@Path("/addLink")
	@Produces(MediaType.TEXT_HTML)
	public String addLink(@QueryParam("item") String item, Form form)
	{
		String value = "<html><body>Reference added</body></html>";
		String link = "http://anu.edu.au/related/";
		FedoraReference reference = new FedoraReference();
		String referenceType = form.getFirst("txtType");
		String referenceItem = form.getFirst("txtItem");
		reference.setPredicate_(link + referenceType);
		reference.setObject_(referenceItem);
		reference.setIsLiteral_(Boolean.FALSE);
		if(Util.isNotEmpty(item)) {
			try {
				FedoraBroker.addRelationship(item, reference);
			}
			catch (Exception e) {
				LOGGER.error("Exception adding relationship", e);
				value = "<html><body>Exception adding reference</body></html>";
			}
		}
		//TODO update the return for this
		
		return value;
	}
	
}
