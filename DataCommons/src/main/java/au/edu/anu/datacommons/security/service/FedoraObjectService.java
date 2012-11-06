package au.edu.anu.datacommons.security.service;

import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import com.yourmediashelf.fedora.client.FedoraClientException;

import au.edu.anu.datacommons.data.db.model.FedoraObject;
import au.edu.anu.datacommons.data.db.model.PublishLocation;
import au.edu.anu.datacommons.webservice.bindings.FedoraItem;
import au.edu.anu.datacommons.xml.sparql.Result;

/**
 * FedoraObjectService
 * 
 * Australian National University Data Commons
 * 
 * Service for Retrieving pages, creating, and saving information for
 * objects.
 * 
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		26/04/2012	Genevieve Turner (GT)	Initial
 * 0.2		05/05/2012	Genevieve Turner (GT)	Added getting a list of publishers
 * 0.3		15/05/2012	Genevieve Turner (GT)	Publishing to publishers
 * 0.4		16/05/2012	Genevivee Turner (GT)	Updated to allow differing configurations for publishing
 * 0.5		22/06/2012	Genevieve Turner (GT)	Updated to only allow people with ADMIN or Publish permissions the ability to publish
 * 0.6		25/07/2012	Genevieve Turner (GT)	Added information for review processing
 * 0.7		01/08/2012	Genevieve Turner (GT)	Added retrieval of information for fedora objects
 * 0.8		03/08/2012	Genevieve Turner (GT)	Fixed issue with permissions
 * 0.9		02/10/2012	Genevieve Turner (GT)	Updated to verify report permissions
 * 0.10		15/10/2012	Genevieve Turner (GT)	Added validatePublishLocation method
 * </pre>
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
	public FedoraObject getItemByPid(String pid);
	
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
	public Map<String, Object> getViewPage(FedoraObject fedoraObject, String layout, String tmplt);
	
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
	public Map<String, Object> getNewPage(String layout, String tmplt);
	
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
	 * @throws JAXBException 
	 * @throws FedoraClientException 
	 */
	public FedoraObject saveNew(String tmplt, Map<String, List<String>> form) throws FedoraClientException, JAXBException;
	
	public FedoraObject saveNew(FedoraItem item) throws FedoraClientException, JAXBException;
	
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
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#fedoraObject, 'WRITE')")
	public Map<String, Object> getEditPage(FedoraObject fedoraObject, String layout, String tmplt);

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
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#fedoraObject, 'WRITE')")
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
	 * @throws JAXBException 
	 * @throws FedoraClientException 
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#fedoraObject, 'WRITE')")
	public Map<String, Object> saveEdit(FedoraObject fedoraObject, String tmplt, Map<String, List<String>> form);
	
	/**
	 * Updates an existing Fedora object with new values.
	 * 
	 * @param item
	 * @return
	 * @throws FedoraClientException
	 * @throws JAXBException
	 */
	public FedoraObject saveEdit(FedoraItem item) throws FedoraClientException, JAXBException;
	
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
	 * @throws FedoraClientException 
	 */
	public void addLink(FedoraObject fedoraObject, String linkType, String itemId) throws FedoraClientException;
	
	/**
	 * getPublishers
	 * 
	 * Returns a list of publishers
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		05/05/2012	Genevieve Turner (GT)	Initial
	 * 0.4		16/05/2012	Genevivee Turner (GT)	Updated to allow differing configurations for publishing
	 * </pre>
	 * 
	 * @return Returns a viewable of publishers
	 */
	public List<PublishLocation> getPublishers();
	
	/**
	 * publish
	 * 
	 * Publishes to the provided list of publishers
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.3		15/05/2012	Genevieve Turner (GT)	Initial
	 * 0.4		16/05/2012	Genevivee Turner (GT)	Updated to allow differing configurations for publishing
	 * 0.5		22/06/2012	Genevieve Turner (GT)	Updated to only allow people with ADMIN or Publish permissions the ability to publish
	 * </pre>
	 * 
	 * @param fedoraObject The item to publish
	 * @param publishers The list of publishers to publish to
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#fedoraObject, 'PUBLISH')")
	public String publish(FedoraObject fedoraObject, List<String> publishers);
	
	/**
	 * getReadyForReview
	 *
	 * Returns a list of items that are ready for a review
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.6		25/07/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return
	 */
	@PostFilter("hasPermission(filterObject,'REVIEW') or hasPermission(filterObject,'ADMINISTRATION')")
	public List<FedoraObject> getReadyForReview();
	
	/**
	 * getRejected
	 *
	 * Returns a list of items that have been rejected.
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.6		25/07/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return
	 */
	@PostFilter("hasPermission(filterObject,'WRITE') or hasPermission(filterObject,'ADMINISTRATION')")
	public List<FedoraObject> getRejected();
	
	/**
	 * getReadyForPublish
	 *
	 * Returns a list of items that are ready for publish
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.6		25/07/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return
	 */
	@PostFilter("hasPermission(filterObject,'PUBLISH') or hasPermission(filterObject,'ADMINISTRATION')")
	public List<FedoraObject> getReadyForPublish();
	
	/**
	 * setReadyForReview
	 *
	 * Sets the item as ready for review
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.6		25/07/2012	Genevieve Turner(GT)	Initial
	 * 0.8		03/08/2012	Genevieve Turner (GT)	Fixed issue with permissions
	 * </pre>
	 * 
	 * @param fedoraObject
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#fedoraObject, 'WRITE') or hasPermission(#fedoraObject, 'ADMINISTRATION')")
	public void setReadyForReview(FedoraObject fedoraObject);
	
	/**
	 * setReadyForPublish
	 *
	 * Sets the item as ready for publish
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.6		25/07/2012	Genevieve Turner(GT)	Initial
	 * 0.8		03/08/2012	Genevieve Turner (GT)	Fixed issue with permissions
	 * </pre>
	 * 
	 * @param fedoraObject
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#fedoraObject, 'REVIEW') or hasPermission(#fedoraObject, 'ADMINISTRATION')")
	public void setReadyForPublish(FedoraObject fedoraObject);
	
	/**
	 * setRejected
	 *
	 * Rejects the item for publishing
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.6		25/07/2012	Genevieve Turner(GT)	Initial
	 * 0.8		03/08/2012	Genevieve Turner (GT)	Fixed issue with permissions
	 * </pre>
	 * 
	 * @param fedoraObject
	 * @param reasons
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#fedoraObject, 'REVIEW') or hasPermission(#fedoraObject, 'PUBLISH') or hasPermission(#fedoraObject, 'ADMINISTRATION')")
	public void setRejected(FedoraObject fedoraObject, List<String> reasons);
	
	/**
	 * getListInformation
	 *
	 * Retrieves information given the list of fedora objects
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.7		01/08/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param fedoraObjects A list of fedora objects to retrieve information for
	 * @return Information about the given list of fedora objects.
	 */
	public List<Result> getListInformation(List<FedoraObject> fedoraObjects);
	
	/**
	 * hasReportPermission
	 *
	 * Verifies that the user has permission to review the report
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.9		02/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param fedoraObject The fedora object to verify
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#fedoraObject, 'ADMINISTRATION')")
	public void hasReportPermission(FedoraObject fedoraObject);
	
	/**
	 * validatePublishLocation
	 * 
	 * Gets validation messages for the given location
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.10		15/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param fedoraObject The fedoraObject to validate
	 * @param publishers The publisher(s) to validate
	 * @return A list of validation error messages
	 */
	public List<String> validatePublishLocation(FedoraObject fedoraObject, List<String> publishers);
	
	/**
	 * getLinks
	 *
	 * Get the links associated with the fedora object
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * X.X		22/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param fedoraObject The fedoraObject to retrieve links for
	 * @return A list of links
	 */
	public List<Result> getLinks(FedoraObject fedoraObject);
	
	/**
	 * removeLink
	 *
	 * Remove the specified link with the object
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * X.X		22/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param fedoraObject The fedoraObject to remove an association with
	 * @param linkType The relationship type to remove
	 * @param itemId The item to remove the relationship from
	 * @throws FedoraClientException
	 */
	public void removeLink(FedoraObject fedoraObject, String linkType, String itemId) throws FedoraClientException;
	
	/**
	 * Generates a DOI for a collection.
	 * 
	 * @param pid
	 *            Pid of collection
	 * @param tmplt
	 *            Template
	 * @throws FedoraObjectException
	 *             When unable to generate a DOI
	 */
	public void generateDoi(String pid, String tmplt) throws FedoraObjectException;
}
