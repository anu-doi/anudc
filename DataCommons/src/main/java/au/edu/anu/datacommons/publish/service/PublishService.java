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

package au.edu.anu.datacommons.publish.service;

import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import au.edu.anu.datacommons.data.db.model.FedoraObject;
import au.edu.anu.datacommons.data.db.model.Groups;
import au.edu.anu.datacommons.data.db.model.PublishLocation;
import au.edu.anu.datacommons.data.solr.model.SolrSearchResult;
import au.edu.anu.datacommons.exception.ValidateException;

/**
 * PublishService
 * 
 * Australian National University Data Commons
 * 
 * Interface for publishing services
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		11/12/2012	Genevieve Turner (GT)	Initial
 * 0.2		02/01/2012	Genevieve Turner (GT)	Removed a number of thrown exceptions for validateMultiple
 * </pre>
 *
 */
public interface PublishService {
	
	/**
	 * validateMultiple
	 *
	 * Validate multiple records
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		11/12/2012	Genevieve Turner(GT)	Initial
	 * 0.2		02/01/2012	Genevieve Turner (GT)	Removed a number of thrown exceptions
	 * </pre>
	 * 
	 * @param publishers The locations to validate against
	 * @param ids The ids of the items to validate
	 * @return A map consisting of the pid and validation messages
	 */
	public Map<String, List<LocationValidationMessage>> validateMultiple(String[] publishers, String[] ids);
	
	/**
	 * getValidationGroups
	 *
	 * Retrieve groups the user can validate against
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		10/12/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return The groups the user can validate records for
	 */
	public List<Groups> getValidationGroups();
	
	/**
	 * getMultiplePublishGroups
	 *
	 * Retrieves the groups for which as user can publish multiple records to
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		10/12/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return The list of groups
	 */
	public List<Groups> getMultiplePublishGroups();
	
	/**
	 * getGroupObjects
	 * 
	 * Retrieve a list of objects for a group
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		10/12/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param groupId The id of the group to retrieve object
	 * @param page The page number
	 * @return The search results
	 * @throws SolrServerException
	 */
	public SolrSearchResult getGroupObjects(Long groupId, Integer page) throws SolrServerException;
	
	/**
	 * getPublishers
	 *
	 * Returns a list of publishers
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		10/12/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return A list of publishers
	 */
	public List<PublishLocation> getPublishers();
	
	/**
	 * getPublishers
	 * 
	 * Returns a list of publishers
	 * 
	 * @param fedoraObject
	 * @return A list of publishers
	 */
	@PostFilter("hasPermission(filterObject,'PUBLISH')")
	public List<PublishLocation> getPublishers(FedoraObject fedoraObject);
	
	/**
	 * getPublishers
	 * 
	 * Returns a list of pbulishers
	 * 
	 * @param username The username of the user to retrieve publishers for
	 * @return A list of publishers
	 */
	public List<PublishLocation> getPublishers(String username);
	
	/**
	 * publishMultiple
	 *
	 * Publish records to multiple locations
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		10/12/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param ids The ids of the items to publish
	 * @param publishers The locations to publish to 
	 * @return Whether the publish was successful or not
	 */
	public Map<String, String> publishMultiple(String[] ids, String[] publishers);
	
	/**
	 * getItemInformation
	 *
	 * Retrieve information about a list of items
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		10/12/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param ids The id's to retrieve information for
	 * @return The search results
	 * @throws SolrServerException
	 */
	public SolrSearchResult getItemInformation(String[] ids) throws SolrServerException;

	/**
	 * publish
	 *
	 * Publishes to the provided list of publishers
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		10/12/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param fedoraObject The item to publish
	 * @param publishers The list of publishers to publish to
	 * @return A list of locations for which the item was published to
	 * @throws ValidateException
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#fedoraObject, 'PUBLISH')")
	public List<String> publish(FedoraObject fedoraObject, List<String> publishers) throws ValidateException;
	
	/**
	 * validatePublishLocation
	 *
	 * Gets validation messages for the given location
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		10/12/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param fedoraObject The fedoraObject to validate
	 * @param publishers The publisher(s) to validate
	 * @return A list of validation error messages
	 */
	public List<String> validatePublishLocation(FedoraObject fedoraObject, List<String> publishers);
	
	/**
	 * getReadyForReview
	 *
	 * Returns a list of items that are ready for a review
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		07/12/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return The list of items that are ready for review
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
	 * 0.1		07/12/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return The list of items that require more work
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
	 * 0.1		07/12/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return The list of items that are ready for publish
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
	 * 0.1		07/12/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param fedoraObject The item to make ready for review
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#fedoraObject, 'WRITE') or hasPermission(#fedoraObject, 'ADMINISTRATION')")
	public void setReadyForReview(FedoraObject fedoraObject);
	
	/**
	 * setReadyForPublish
	 *
	 *  Sets the item as ready for publish
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		07/12/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param fedoraObject The item to make ready for publishing
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
	 * 0.1		07/12/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param fedoraObject The item to reject
	 * @param reasons The reasons for rejection
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#fedoraObject, 'REVIEW') or hasPermission(#fedoraObject, 'PUBLISH') or hasPermission(#fedoraObject, 'ADMINISTRATION')")
	public void setRejected(FedoraObject fedoraObject, List<String> reasons);
}
