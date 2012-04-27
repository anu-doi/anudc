package au.edu.anu.datacommons.security.service;

import java.util.List;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;

import au.edu.anu.datacommons.connection.db.model.FedoraObject;

import com.sun.jersey.api.view.Viewable;

/**
 * FedoraObjectService
 * 
 * Australian National University Data Comons
 * 
 * Service for Retrieving pages, creating, and saving information for
 * objects.
 * 
 * JUnit Coverage:
 * None
 * 
 * Version	Date		Developer				Description
 * 0.1		26/04/2012	Genevieve Turner (GT)	Initial
 * 
 */
public interface FedoraObjectService {
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
	public FedoraObject getItemByName(String pid);
	
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
	public Viewable getViewPage(FedoraObject fedoraObject, String layout, String tmplt);
	
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
	public Viewable getNewPage(String layout, String tmplt);
	
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
	public Viewable saveNew(String tmplt, Map<String, List<String>> form);
	
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
	 * @param layout The layout that defines the flow of the items on the page
	 * @param tmplt The template that determines the fields on the screen
	 * @return Returns the viewable for the jsp file to pick up
	 */
	@PreAuthorize("hasPermission(#fedoraObject, 'WRITE')")
	public Viewable getEditPage(FedoraObject fedoraObject, String layout, String tmplt);

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
	@PreAuthorize("hasPermission(#fedoraObject, 'WRITE')")
	public String getEditItem(FedoraObject fedoraObject, String layout, String tmplt, String fieldName);

	/**
	 * saveEdit
	 * 
	 * Updates the information about an object
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/04/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param fedoraObject The item to transform to a display
	 * @param tmplt The template that determines the fields on the screen
	 * @param form Contains the parameters from the request
	 * @return Returns the viewable for the jsp file to pick up
	 */
	@PreAuthorize("hasPermission(#fedoraObject, 'WRITE')")
	public Viewable saveEdit(FedoraObject fedoraObject, String tmplt, Map<String, List<String>> form);
	
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
	public String addLink(FedoraObject fedoraObject, Map<String, List<String>> form);
}
