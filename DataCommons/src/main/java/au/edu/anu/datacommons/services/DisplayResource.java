package au.edu.anu.datacommons.services;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.util.Util;
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
 */
@Path("/display")
public class DisplayResource {
	static final Logger LOGGER = LoggerFactory.getLogger(DisplayResource.class);

	@Context UriInfo uriInfo;

	/**
	 * getItem
	 * 
	 * 
	 * 
	 * Version	Date		Developer				Description
	 * 0.1		08/03/2012	Genevieve Turner (GT)	Initial
	 * 0.2		19/03/2012	Genevieve Turner (GT)	Updating to return a page
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
			page = viewTransform.getPage(layout, template, item);
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
		
		Viewable viewable = new Viewable(toPage, values);
		
		return viewable;
	}
	
	/**
	 * postItem
	 * 
	 * Version	Date		Developer				Description
	 * 0.2		20/03/2012	Genevieve Turner (GT)	Added as a placeholder
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
		for(Entry param : form.entrySet()) {
			LOGGER.info("Param: " + param.getKey() + ", Value: " + param.getValue());
		}
		String toPage = "/page.jsp";

		Map<String, Object> values = new HashMap<String, Object>();
		toPage = "/error.jsp";
		Viewable viewable = new Viewable(toPage, values);
		
		return viewable;
	}
}
