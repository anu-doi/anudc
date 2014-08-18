/*******************************************************************************
 * Australian National University Data Commons
 * Copyright (C) 2013  The Australian National University
 * 
 * This file is part of Australian National University Data Commons.
 * 
 * Australian National University Data Commons is free software: you
 * can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package au.edu.anu.datacommons.security.service;

import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;

import au.edu.anu.datacommons.data.db.model.FedoraObject;
import au.edu.anu.datacommons.webservice.bindings.FedoraItem;
import au.edu.anu.datacommons.xml.sparql.Result;

import com.yourmediashelf.fedora.client.FedoraClientException;

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
 * 0.11		12/11/2012	Genevieve Turner (GT)	Added the rid parameter for some methods
 * 0.12		11/12/2012	Genevieve Turner (GT)	Moved some publishing methods to PublishService
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
	 * 0.11		12/11/2012	Genevieve Turner (GT)	Added the request id
	 * </pre>
	 * 
	 * @param tmplt The template that determines the fields on the screen
	 * @param form Contains the parameters from the request
	 * @param rid The request id
	 * @return Returns the viewable for the jsp file to pick up.
	 * @throws JAXBException 
	 * @throws FedoraClientException 
	 */
	public FedoraObject saveNew(String tmplt, Map<String, List<String>> form, Long rid) throws FedoraClientException, JAXBException;
	
	/**
	 * saveNew
	 *
	 * Saves the information contained in the fedora item
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * X.X		XX/XX/2012	Rahul Khanna (RK)		Initial
	 * 0.11		12/11/2012	Genevieve Turner (GT)	Added the request id
	 * </pre>
	 * 
	 * @param item
	 * @param rid The request ID
	 * @return
	 * @throws FedoraClientException
	 * @throws JAXBException
	 */
	public FedoraObject saveNew(FedoraItem item, Long rid) throws FedoraClientException, JAXBException;
	
	/**
	 * getEditPage
	 * 
	 * Updates the object given the specified parameters, and form values.
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/04/2012	Genevieve Turner (GT)	Initial
	 * 0.11		12/11/2012	Genevieve Turner (GT)	Added the request id
	 * 0.12		13/11/2012	Genevieve Turner (GT)	Added whether edit mode should be used in retrieving the page (i.e. no published information is retrieved)
	 * </pre>
	 * 
	 * @param fedoraObject The  fedora object to get the page for
	 * @param layout The layout that defines the flow of the items on the page
	 * @param tmplt The template that determines the fields on the screen
	 * @param editMode Whether the returned information contains does or does not contained published information (false for only the unpublished information to be returned)
	 * @return Returns the viewable for the jsp file to pick up
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#fedoraObject, 'WRITE')")
	public Map<String, Object> getEditPage(FedoraObject fedoraObject, String layout, String tmplt, boolean editMode);

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
	 * 0.11		12/11/2012	Genevieve Turner (GT)	Added the request id
	 * </pre>
	 * 
	 * @param fedoraObject The item to transform to a display
	 * @param tmplt The template that determines the fields on the screen
	 * @param form Contains the parameters from the request
	 * @param rid The request id
	 * @return Returns the viewable for the jsp file to pick up
	 * @throws JAXBException 
	 * @throws FedoraClientException 
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#fedoraObject, 'WRITE')")
	public Map<String, Object> saveEdit(FedoraObject fedoraObject, String tmplt, Map<String, List<String>> form, Long rid);
	
	/**
	 * saveEdit
	 * 
	 * Updates an existing Fedora object with new values.
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * X.X		XX/XX/XXXX	Rahul Khanna (RK)		Initial
	 * 0.11		12/11/2012	Genevieve Turner (GT)	Added the request id
	 * </pre>
	 * 
	 * @param item
	 * @param rid The request id
	 * @return
	 * @throws FedoraClientException
	 * @throws JAXBException
	 */
	public FedoraObject saveEdit(FedoraItem item, Long rid) throws FedoraClientException, JAXBException;
	
	/**
	 * delete
	 * 
	 * Set the status of the given object to deleted
	 * 
	 * @param fedoraObject The fedora object
	 * @throws FedoraClientException
	 */
	@PreAuthorize("hasPermission(#fedoraObject, 'DELETE') or hasPermission(#fedoraObject, 'ADMINISTRATION')")
	public void delete(FedoraObject fedoraObject) throws FedoraClientException;
	
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
	 * <pre>
	 * Version	Date		Developer				Description
	 * X.X		XX/XX/2012	Rahul Khanna (RK)		Initial
	 * 0.11		22/10/2012	Genevieve Turner(GT)	Added the request id
	 * </pre>
	 * 
	 * @param pid
	 *            Pid of collection
	 * @param tmplt
	 *            Template
	 * @param rid The request id
	 * @throws FedoraObjectException
	 *             When unable to generate a DOI
	 */
	public void generateDoi(String pid, String tmplt, Long rid) throws FedoraObjectException;

	boolean isFilesPublic(String pid);

	void setFilesPublic(String pid, boolean isFilesPublic);
	
	@PostAuthorize("hasPermission(returnObject, 'READ')")
	FedoraObject getItemByPidReadAccess(String pid);
	
	@PostAuthorize("hasPermission(returnObject, 'WRITE')")
	FedoraObject getItemByPidWriteAccess(String pid);

	List<FedoraObject> getAllPublishedAndPublic();
}
