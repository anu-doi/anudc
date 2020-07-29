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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.ws.rs.WebApplicationException;
import javax.xml.bind.JAXBException;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.yourmediashelf.fedora.client.FedoraClientException;

import au.edu.anu.datacommons.collectionrequest.Email;
import au.edu.anu.datacommons.data.db.dao.FedoraObjectDAO;
import au.edu.anu.datacommons.data.db.dao.FedoraObjectDAOImpl;
import au.edu.anu.datacommons.data.db.dao.GenericDAO;
import au.edu.anu.datacommons.data.db.dao.GenericDAOImpl;
import au.edu.anu.datacommons.data.db.dao.PublishLocationDAO;
import au.edu.anu.datacommons.data.db.dao.PublishLocationDAOImpl;
import au.edu.anu.datacommons.data.db.dao.UsersDAO;
import au.edu.anu.datacommons.data.db.dao.UsersDAOImpl;
import au.edu.anu.datacommons.data.db.model.AuditObject;
import au.edu.anu.datacommons.data.db.model.FedoraObject;
import au.edu.anu.datacommons.data.db.model.Groups;
import au.edu.anu.datacommons.data.db.model.PublishLocation;
import au.edu.anu.datacommons.data.db.model.PublishReady;
import au.edu.anu.datacommons.data.db.model.ReviewReady;
import au.edu.anu.datacommons.data.db.model.ReviewReject;
import au.edu.anu.datacommons.data.db.model.Template;
import au.edu.anu.datacommons.data.db.model.Users;
import au.edu.anu.datacommons.data.fedora.FedoraBroker;
import au.edu.anu.datacommons.data.fedora.FedoraReference;
import au.edu.anu.datacommons.data.solr.SolrManager;
import au.edu.anu.datacommons.data.solr.SolrUtils;
import au.edu.anu.datacommons.data.solr.dao.SolrSearchDAO;
import au.edu.anu.datacommons.data.solr.dao.SolrSearchDAOImpl;
import au.edu.anu.datacommons.data.solr.model.SolrSearchResult;
import au.edu.anu.datacommons.exception.DataCommonsException;
import au.edu.anu.datacommons.exception.ValidateException;
import au.edu.anu.datacommons.properties.GlobalProps;
import au.edu.anu.datacommons.publish.FieldValidate;
import au.edu.anu.datacommons.publish.Publish;
import au.edu.anu.datacommons.publish.Validate;
import au.edu.anu.datacommons.security.CustomUser;
import au.edu.anu.datacommons.security.acl.CustomACLPermission;
import au.edu.anu.datacommons.security.acl.PermissionService;
import au.edu.anu.datacommons.security.service.FedoraObjectService;
import au.edu.anu.datacommons.security.service.GroupService;
import au.edu.anu.datacommons.util.Constants;
import au.edu.anu.datacommons.util.Util;
import au.edu.anu.datacommons.xml.transform.ViewTransform;

/**
 * PublishServiceImpl
 * 
 * Australian National University Data Commons
 * 
 * Implementation class for publishing services
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		11/12/2012	Genevieve Turner (GT)	Initial
 * 0.2		02/01/2012	Genevieve Turner (GT)	Updated to reflect changes in error handling
 * 0.3		28/03/2012	Genevieve Turner (GT)	Moved the addPublishLocation method to the publication area in the publishObject method
 * </pre>
 *
 */
@Service("publishServiceImpl")
public class PublishServiceImpl implements PublishService {
	static final Logger LOGGER = LoggerFactory.getLogger(PublishService.class);
	
	private static final int rows = 100;
	
	@Resource(name="groupServiceImpl")
	GroupService groupService;
	
	@Resource(name="fedoraObjectServiceImpl")
	FedoraObjectService fedoraObjectService;
	
	@Resource(name="permissionService")
	PermissionService permissionService;

	@Resource(name = "mailSender")
	JavaMailSenderImpl mailSender;
	
	/**
	 * validateMultiple
	 * 
	 * Validate multiple records
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		10/12/2012	Genevieve Turner(GT)	Initial
	 * 0.2		02/01/2012	Genevieve Turner (GT)	Removed a number of thrown exceptions
	 * </pre>
	 * 
	 * @param publishers The locations to validate against
	 * @param ids The ids of the items to validate
	 * @return A map consisting of the pid and validation messages
	 * @see au.edu.anu.datacommons.publish.service.PublishService#validateMultiple(java.lang.String[], java.lang.String[])
	 */
	public Map<String, List<LocationValidationMessage>> validateMultiple(String[] publishers, String[] ids) {
		List<PublishLocation> publishLocations = getPublishLocationsFromList(Arrays.asList(publishers));
		
		return validateMultiple(publishLocations, Arrays.asList(ids));
	}
	
	/**
	 * validateMultiple
	 *
	 * Validate multiple records
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		10/12/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param publishLocations The locations to validate
	 * @param ids The ids of the records to vadliate
	 * @return The validation results
	 */
	public Map<String, List<LocationValidationMessage>> validateMultiple(List<PublishLocation> publishLocations, List<String> ids) {
		Map<String, List<LocationValidationMessage>> issueMap = new HashMap<String, List<LocationValidationMessage>>();
		for (PublishLocation publishLocation : publishLocations) {
			try {
				Publish genericPublish = (Publish) Class.forName(publishLocation.getExecute_class()).newInstance();
				for (String id : ids) {
					List<String> messages = genericPublish.checkValidity(id);
					if (issueMap.containsKey(id)) {
						LocationValidationMessage validationMessage = new LocationValidationMessage(publishLocation.getName(), messages);
						issueMap.get(id).add(validationMessage);
					}
					else {
						List<LocationValidationMessage> validationMessages = new ArrayList<LocationValidationMessage>();
						validationMessages.add(new LocationValidationMessage(publishLocation.getName(), messages));
						issueMap.put(id, validationMessages);
					}
				}
			}
			catch (ClassNotFoundException e) {
				LOGGER.error("Class not found class: " + publishLocation.getExecute_class(), e);
			}
			catch (IllegalAccessException e) {
				LOGGER.error("Illegal acces to class: " + publishLocation.getExecute_class(), e);
			}
			catch (InstantiationException e) {
				LOGGER.error("Error instantiating class: " + publishLocation.getExecute_class(), e);
			}
		}
		
		return issueMap;
	}
	
	/**
	 * getPublishLocationsFromList
	 *
	 * Retrieve a list of potential publish locations
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		10/12/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param publishers
	 * @return
	 */
	private List<PublishLocation> getPublishLocationsFromList(List<String> publishers) {
		GenericDAOImpl<PublishLocation, Long> publishLocationDAO = new GenericDAOImpl<PublishLocation, Long>(PublishLocation.class);
		
		List<PublishLocation> publishLocations = new ArrayList<PublishLocation>();
		for (String publisher : publishers) {
			Long id = Long.parseLong(publisher);
			PublishLocation publishLocation = publishLocationDAO.getSingleById(id);
			publishLocations.add(publishLocation);
		}
		return publishLocations;
	}
	
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
	 * @see au.edu.anu.datacommons.publish.service.PublishService#getValidationGroups()
	 */
	public List<Groups> getValidationGroups() {
		return groupService.getValidationGroups();
	}
	
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
	 * @see au.edu.anu.datacommons.publish.service.PublishService#getMultiplePublishGroups()
	 */
	public List<Groups> getMultiplePublishGroups() {
		return groupService.getMultiplePublishGroups();
	}
	
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
	 * @see au.edu.anu.datacommons.publish.service.PublishService#getGroupObjects(java.lang.Long, java.lang.Integer)
	 */
	public SolrSearchResult getGroupObjects(Long groupId, Integer page) throws SolrServerException, IOException {
		return getFedoraObjects(groupId, page);
	}
	
	/**
	 * getFedoraObjects
	 *
	 * Search for the objects contained within a group
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		10/12/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param groupId
	 * @param page
	 * @return
	 * @throws SolrServerException
	 */
	private SolrSearchResult getFedoraObjects(Long groupId, Integer page) throws SolrServerException, IOException {
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setRows(rows);
		
		if (page != null) {
			int start = page.intValue() * rows;
			solrQuery.setStart(start);
		}
		
		solrQuery.addField("id");
		solrQuery.addField("unpublished.name");
		
		solrQuery.setQuery("*:*");
		solrQuery.addFilterQuery("unpublished.ownerGroup:" + groupId);
		solrQuery.setSort("id", ORDER.asc);
		
		SolrClient solrClient = SolrManager.getInstance().getSolrClient();
		QueryResponse queryResponse = solrClient.query(solrQuery);
		SolrDocumentList documentList = queryResponse.getResults();
		SolrSearchResult results = new SolrSearchResult(documentList);
		
		return results;
	}
	
	/**
	 * getPublishers
	 * 
	 * Returns a list of publishers
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		06/12/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return
	 * @see au.edu.anu.datacommons.publish.service.PublishService#getPublishers()
	 */
	public List<PublishLocation> getPublishers() {
		PublishLocationDAO publishDAO = new PublishLocationDAOImpl();
		
		List<PublishLocation> publishLocations = publishDAO.getAll();
		
		return publishLocations;
	}
	
	@Override
	public List<PublishLocation> getPublishers(FedoraObject fedoraObject) {
		PublishLocationDAO publishDAO = new PublishLocationDAOImpl();
		List<PublishLocation> publishLocations = publishDAO.getAllWithTemplates();
		List<PublishLocation> removeLocations = new ArrayList<PublishLocation>();
		boolean hasTemplate = false;
		for (PublishLocation location : publishLocations) {
			if (location.getTemplates() != null) {
				for (Template template : location.getTemplates()) {
					if (template.getTemplatePid().equals(fedoraObject.getTmplt_id())) {
						hasTemplate = true;
					}
				}
			}
			if (!hasTemplate) {
				removeLocations.add(location);
			}
			hasTemplate = false;
		}
		publishLocations.removeAll(removeLocations);
		
		return publishLocations;
	}

	@Override
	public List<PublishLocation> getPublishers(String username) {
		List<PublishLocation> publishLocations = getPublishers();
		List<PublishLocation> publishLocationsWithPermission = new ArrayList<PublishLocation>();
		for (PublishLocation location : publishLocations) {
			List<Permission> permission = permissionService.getListOfPermission(PublishLocation.class, location.getId(), username);
			if (permission.contains(CustomACLPermission.PUBLISH)) {
				publishLocationsWithPermission.add(location);
			}
		}
		return publishLocationsWithPermission;
	}
	
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
	 * @return message to display on the screen
	 * @throws ValidateException
	 * @see au.edu.anu.datacommons.publish.service.PublishService#publish(au.edu.anu.datacommons.data.db.model.FedoraObject, java.util.List)
	 */
	public List<String> publish(FedoraObject fedoraObject, List<String> publishers) throws ValidateException {
		List<PublishLocation> publishLocations = getPublishLocationsFromList(publishers);
		return publishObject(fedoraObject, publishLocations);
	}
	
	/**
	 * isPublishable
	 *
	 * Verify whether the objects are publishable or not
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		10/12/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param fedoraObject The object to publish
	 * @param publishLocations The locations to publish to
	 * @return
	 */
	public boolean isPublishable(FedoraObject fedoraObject, List<PublishLocation> publishLocations) {
		boolean isPublishable = true;
		for (int i = 0; isPublishable && i < publishLocations.size(); i++) {
			PublishLocation publishLocation = publishLocations.get(i);
			try {
				Publish publish = (Publish) Class.forName(publishLocation.getExecute_class()).newInstance();
				publish.checkValidity(fedoraObject.getObject_id());
				isPublishable = publish.isAllowedToPublish();
			}
			catch (ClassNotFoundException e) {
				LOGGER.error("Class not found class: " + publishLocation.getExecute_class(), e);
				isPublishable = false;
			}
			catch (IllegalAccessException e) {
				LOGGER.error("Illegal acces to class: " + publishLocation.getExecute_class(), e);
				isPublishable = false;
			}
			catch (InstantiationException e) {
				LOGGER.error("Error instantiating class: " + publishLocation.getExecute_class(), e);
				isPublishable = false;
			}
		}
		
		return isPublishable;
	}
	
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
	 * @see au.edu.anu.datacommons.publish.service.PublishService#publishMultiple(java.lang.String[], java.lang.String[])
	 */
	public Map<String, String> publishMultiple(String[] ids, String[] publishers) {
		List<PublishLocation> publishLocations = getPublishLocationsFromList(Arrays.asList(publishers));
		
		List<FedoraObject> fedoraObjects = new ArrayList<FedoraObject>();
		boolean hasPermission = true;
		
		for (String pid : ids) {
			FedoraObject fedoraObject = fedoraObjectService.getItemByPid(pid);
			fedoraObjects.add(fedoraObject);
			// The security annotations need to be the interface rather than the implementation methods.  As we do not retrieve the pids prior to this
			// we have no prior opportunity to check the permissions.
			if (!permissionService.checkPermission(fedoraObject, CustomACLPermission.PUBLISH_MULTI)) {
				hasPermission = false;
			}
		}
		if (!hasPermission) {
			return null;
		}
		
		boolean canPublish = true;
		for (FedoraObject fedoraObject : fedoraObjects) {
			if(!isPublishable(fedoraObject, publishLocations)) {
				canPublish = false;
			}
		}
		if (!canPublish) {
			return null;
		}
		Map<String, String> returnMap = new HashMap<String, String>();
		
		for (FedoraObject fedoraObject : fedoraObjects) {
			try {
				this.publishObject(fedoraObject, publishLocations);
				returnMap.put(fedoraObject.getObject_id(), "success");
			}
			catch (ValidateException e) {
				returnMap.put(fedoraObject.getObject_id(), "failure");
			}
		}
		return returnMap;
	}
	
	/**
	 * publishObject
	 *
	 * Publish an object
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		10/12/2012	Genevieve Turner(GT)	Initial
	 * 0.3		28/03/2012	Genevieve Turner (GT)	Moved the addPublishLocation method to the publication area
	 * </pre>
	 * 
	 * @param fedoraObject The object to publish
	 * @param publishers The locations to publish to
	 * @return A post publishing message
	 * @throws ValidateException
	 */
	@PreAuthorize("hasPermission(#fedoraObject, 'PUBLISH')")
	private List<String> publishObject(FedoraObject fedoraObject, List<PublishLocation> publishers) throws ValidateException {
		verifyHasPublishPermissionsForLocations(fedoraObject, publishers);
		prePublish(fedoraObject);
		
		List<String> locations = new ArrayList<String>();
		try {
			for (PublishLocation publishLocation : publishers) {
				try {
					Publish publish = (Publish) Class.forName(publishLocation.getExecute_class()).newInstance();
					publish.publish(fedoraObject, publishLocation);
					if (!fedoraObject.getPublished()) {
						fedoraObject.setPublished(Boolean.TRUE);
					}
					locations.add(publishLocation.getName());
				}
				catch (ClassNotFoundException e) {
					LOGGER.error("Class not found class: " + publishLocation.getExecute_class(), e);
				}
				catch (IllegalAccessException e) {
					LOGGER.error("Illegal acces to class: " + publishLocation.getExecute_class(), e);
				}
				catch (InstantiationException e) {
					LOGGER.error("Error instantiating class: " + publishLocation.getExecute_class(), e);
				}
			}
		}
		finally {
			if (locations.size() > 0) {
				String message = Util.listToStringWithNewline(locations);
				FedoraObjectDAOImpl object = new FedoraObjectDAOImpl();
				object.update(fedoraObject);
				
				CustomUser customUser = (CustomUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
				
				LOGGER.info("User id: {}",customUser.getId());
				
				AuditObject auditObject = new AuditObject();
				auditObject.setLog_date(new java.util.Date());
				auditObject.setLog_type("PUBLISH");
				auditObject.setObject_id(fedoraObject.getId());
				auditObject.setUser_id(customUser.getId());
				auditObject.setAfter(message);
				GenericDAO<AuditObject,Long> auditObjectDAO = new GenericDAOImpl<AuditObject,Long>(AuditObject.class);
				auditObjectDAO.create(auditObject);
			}
		}
		
		removePublishReady(fedoraObject);
		removeReviewReady(fedoraObject);
		removeReviewReject(fedoraObject);
		
		return locations;
	}
	
	/**
	 * Verify whether the user actually has permissions to publish to the given location
	 * 
	 * @param fedoraObject The item to publish
	 * @param publishLocations The locations to verify
	 * @throws ValidateException
	 */
	private void verifyHasPublishPermissionsForLocations(FedoraObject fedoraObject, List<PublishLocation> publishLocations) throws ValidateException {
		List<PublishLocation> templatePublishLocations = getPublishers(fedoraObject);
		if (!templatePublishLocations.containsAll(publishLocations)) {
			publishLocations.removeAll(templatePublishLocations);
			List<String> messageList = new ArrayList<String>();
			messageList.add("The item cannot be published to the following location(s):");
			for (PublishLocation location : publishLocations) {
				messageList.add(location.getName());
			}
			throw new ValidateException(messageList);
		}
		
		List<PublishLocation> unpublishableLocations = new ArrayList<PublishLocation>();
		for (PublishLocation location : publishLocations) {
			if (!permissionService.checkPermission(PublishLocation.class, location.getId(), CustomACLPermission.PUBLISH)) {
				unpublishableLocations.add(location);
			}
		}
		if (unpublishableLocations.size() > 0) {
			List<String> messageList = new ArrayList<String>();
			messageList.add("You do not have permission to publish to the following location(s):");
			for (PublishLocation location : unpublishableLocations) {
				messageList.add(location.getName());
			}
			throw new ValidateException(messageList);
		}
	}
	
	/**
	 * prePublish
	 *
	 * Performs actions prior to publishing
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		10/12/2012	Genevieve Turner(GT)	Initial
	 * 0.2		02/01/2012	Genevieve Turner (GT)	Updated with changes to error handling
	 * </pre>
	 * 
	 * @param fedoraObject The object to publish
	 * @throws ValidateException
	 */
	private void prePublish(FedoraObject fedoraObject) throws ValidateException {
		Validate validate = new FieldValidate();
		
		if (!validate.isValid(fedoraObject.getObject_id())) {
			List<String> messages = validate.getErrorMessages();
			messages.add(0, "Not all required fields have been filled out correctly");
			throw new ValidateException(messages);
		}
		setDefaultsForPublish(fedoraObject);
		updatePublishXML(fedoraObject);
	}
	
	/**
	 * setDefaultsForPublish
	 *
	 * Set some default values prior to publishing
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		10/12/2012	Genevieve Turner(GT)	Initial
	 * 0.2		02/01/2012	Genevieve Turner (GT)	Updated to reflect the changes to citation creators
	 * </pre>
	 * 
	 * @param fedoraObject The object to publish
	 */
	private void setDefaultsForPublish(FedoraObject fedoraObject) {
		Map<String, String> form = new HashMap<String, String>();
		
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		
		//TODO create a properties file for these?
		form.put("citCreatorSurname", "The Australian National University");
		form.put("citationYear", Integer.toString(year));
		form.put("citationPublisher", "The Australian National University Data Commons");
		
		ViewTransform viewTransform = new ViewTransform();
		try {
			viewTransform.setDefaultPublishData(null, fedoraObject, form, null);
		}
		catch (JAXBException e) {
			LOGGER.error("Error transforming document for saving publication");
		}
		catch (FedoraClientException e) {
			LOGGER.error("Error saving updates for publication");
		}
	}
	
	/**
	 * updatePublishXML
	 *
	 * Update the publish xml and set the record available for oai pmh harvest
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		10/12/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param fedoraObject The object to publish
	 */
	private void updatePublishXML(FedoraObject fedoraObject) {
		FedoraReference fedoraReference = new FedoraReference();
		fedoraReference.setPredicate_(GlobalProps.getProperty(GlobalProps.PROP_FEDORA_OAIPROVIDER_URL));
		fedoraReference.setObject_("oai:" + fedoraObject.getObject_id());
		fedoraReference.setIsLiteral_(Boolean.FALSE);
		
		String location = String.format("%s/objects/%s/datastreams/XML_SOURCE/content", GlobalProps.getProperty(GlobalProps.PROP_FEDORA_URI), fedoraObject.getObject_id());
		try {
			FedoraBroker.addDatastreamByReference(fedoraObject.getObject_id(), Constants.XML_PUBLISHED, "M", "XML Published", location);
			FedoraBroker.addRelationship(fedoraObject.getObject_id(), fedoraReference);
		}
		catch (FedoraClientException e) {
			LOGGER.info("Exception publishing to ANU: ", e);
		}
	}
	
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
	 * @see au.edu.anu.datacommons.publish.service.PublishService#getItemInformation(java.lang.String[])
	 */
	public SolrSearchResult getItemInformation(String[] ids) throws SolrServerException, IOException {
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setRows(rows);
		
		solrQuery.addField("id");
		solrQuery.addField("unpublished.name");
		
		solrQuery.setQuery("*:*");
		solrQuery.setSort("id", ORDER.asc);
		
		StringBuilder filterQuery = new StringBuilder();
		filterQuery.append("(");
		for (String pid : ids) {
			filterQuery.append("id:");
			filterQuery.append(SolrUtils.escapeSpecialCharacters(pid));
			filterQuery.append(" ");
		}
		filterQuery.append(")");
		solrQuery.addFilterQuery(filterQuery.toString());
		
		SolrClient solrClient = SolrManager.getInstance().getSolrClient();
		QueryResponse queryResponse = solrClient.query(solrQuery);
		SolrDocumentList documentList = queryResponse.getResults();
		LOGGER.info("Number of results found: {}", documentList.getNumFound());
		SolrSearchResult results = new SolrSearchResult(documentList);
		
		return results;
	}
	
	/**
	 * removeReviewReady
	 *
	 * Remove the ready for the review status from the database
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		07/12/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param fedoraObject The fedora object
	 */
	private void removeReviewReady(FedoraObject fedoraObject) {
		if (fedoraObject.getReviewReady() != null) {
			GenericDAO<ReviewReady, Long> reviewReadyDAO = new GenericDAOImpl<ReviewReady, Long>(ReviewReady.class);
			reviewReadyDAO.delete(fedoraObject.getId());
		}
	}
	
	/**
	 * removePublishReady
	 *
	 * Remove the ready for the publish status from the database
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		07/12/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param fedoraObject The fedora object
	 */
	private void removePublishReady(FedoraObject fedoraObject) {
		if (fedoraObject.getPublishReady() != null) {
			GenericDAO<PublishReady, Long> publishReadyDAO = new GenericDAOImpl<PublishReady, Long>(PublishReady.class);
			publishReadyDAO.delete(fedoraObject.getId());
		}
	}
	
	/**
	 * removeReviewReject
	 *
	 * Remove the reject status from the database
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		07/12/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param fedoraObject The fedora object
	 */
	private void removeReviewReject(FedoraObject fedoraObject) {
		if (fedoraObject.getReviewReject() != null) {
			GenericDAO<ReviewReject, Long> rejectDAO = new GenericDAOImpl<ReviewReject, Long>(ReviewReject.class);
			rejectDAO.delete(fedoraObject.getId());
		}
	}
	
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
	 * @see au.edu.anu.datacommons.publish.service.PublishService#validatePublishLocation(au.edu.anu.datacommons.data.db.model.FedoraObject, java.util.List)
	 */
	public List<String> validatePublishLocation(FedoraObject fedoraObject, List<String> publishers) {
		List<PublishLocation> publishLocations = getPublishLocationsFromList(publishers);
		
		Map<String, List<LocationValidationMessage>> validateReturn = validateMultiple(publishLocations, Arrays.asList(fedoraObject.getObject_id()));
		List<LocationValidationMessage> message = validateReturn.get(fedoraObject.getObject_id());
		
		if (message != null && message.size() > 0) {
			return message.get(0).getMessages();
		}
		
		return null;
	}
	
	/**
	 * getReadyForReview
	 * 
	 * Returns a list of items that are ready for a review
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		18/12/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return A list of objects that are ready for review
	 * @see au.edu.anu.datacommons.security.service.FedoraObjectService#getReadyForReview()
	 */
	public List<FedoraObject> getReadyForReview() {
		FedoraObjectDAO fedoraObjectDAO = new FedoraObjectDAOImpl();
		List<FedoraObject> reviewReadyList = fedoraObjectDAO.getAllReadyForReview();
		return reviewReadyList;
	}
	
	/**
	 * getRejected
	 * 
	 * Returns a list of items that have been rejected.
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		18/12/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return A list of objects that have been rejected in the review page
	 * @see au.edu.anu.datacommons.security.service.FedoraObjectService#getRejected()
	 */
	public List<FedoraObject> getRejected() {
		FedoraObjectDAO fedoraObjectDAO = new FedoraObjectDAOImpl();
		List<FedoraObject> rejectedList = fedoraObjectDAO.getAllRejected();
		return rejectedList;
	}
	
	/**
	 * getReadyForPublish
	 * 
	 * Returns a list of items that are ready for publish
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		18/12/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return A list of objects that are ready for publishing
	 * @see au.edu.anu.datacommons.security.service.FedoraObjectService#getReadyForPublish()
	 */
	public List<FedoraObject> getReadyForPublish() {
		FedoraObjectDAO fedoraObjectDAO = new FedoraObjectDAOImpl();
		List<FedoraObject> publishReadyList = fedoraObjectDAO.getAllReadyForPublish();
		return publishReadyList;
	}
	
	/**
	 * setReadyForReview
	 * 
	 * Sets the item as ready for review
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		18/12/2012	Genevieve Turner(GT)	Initial
	 * 0.2		02/01/2012	Genevieve Turner (GT)	Updated to reflect changes in error handling
	 * </pre>
	 * 
	 * @param fedoraObject The item to set as ready for review
	 * @see au.edu.anu.datacommons.security.service.FedoraObjectService#setReadyForReview(au.edu.anu.datacommons.data.db.model.FedoraObject)
	 */
	public void setReadyForReview(FedoraObject fedoraObject) {
		if (fedoraObject.getReviewReady() != null || fedoraObject.getPublishReady() != null) {
			LOGGER.info("Record is already in the review queue");
			throw new DataCommonsException(412, "Record is already in the review queue");
		}
		
		Validate validate = new FieldValidate();
		if (!validate.isValid(fedoraObject.getObject_id())) {
			List<String> errorMessages = validate.getErrorMessages();
			errorMessages.add(0, "Not all required fields have been filled out correctly");
			throw new ValidateException(errorMessages);
		}
		
		ReviewReady reviewReady = new ReviewReady();
		reviewReady.setId(fedoraObject.getId());
		reviewReady.setDate_submitted(new Date());
		
		GenericDAO<ReviewReady, Long> reviewReadyDAO = new GenericDAOImpl<ReviewReady, Long>(ReviewReady.class);
		reviewReadyDAO.create(reviewReady);
		
		removeReviewReject(fedoraObject);
		saveAuditReviewLog(fedoraObject, "REVIEW_READY", null);
		
		String sendEmail = GlobalProps.getProperty("review.reviewready.email.enabled", "false");
		if ("true".equals(sendEmail)) {
			sendReadyEmail(fedoraObject, "ANU Data Commons - Ready for Review", "mailtmpl/readyforreview.txt", CustomACLPermission.REVIEW);
		}
	}
	
	private void sendReadyEmail(FedoraObject fedoraObject, String subject, String mailTemplate, Permission permission) {
		try {
			Set<Sid> recipientSids = permissionService.getSidsByPermissions(fedoraObject, permission);
			
			Set<String> recipientEmails = new HashSet<String>();
			
			for (Sid sid : recipientSids) {
				if (sid instanceof PrincipalSid) {
					PrincipalSid recipientSid = (PrincipalSid) sid;
					LOGGER.debug("Principal: {}", recipientSid.getPrincipal());
					UsersDAO usersDAO = new UsersDAOImpl();
					Users recipient = usersDAO.getUserByName(recipientSid.getPrincipal());
					LOGGER.debug("Recipient email address: {}", recipient.getEmail());
					recipientEmails.add(recipient.getEmail());
				}
			}
			
			if (recipientEmails.size() == 0) {
				String defaultEmail = GlobalProps.getProperty("review.default.email");
				if (defaultEmail != null && !defaultEmail.equals("review.default.email")) {
					LOGGER.debug("Using default email of '{}' to send to review", defaultEmail);
					recipientEmails.add(defaultEmail);
				}
			}
			
			if (recipientEmails.size() > 0) {
				String queryString = "id:\"" + fedoraObject.getObject_id() + "\"";
				SolrQuery solrQuery = new SolrQuery();
				solrQuery.setQuery(queryString);
				solrQuery.addField("id");
				solrQuery.addField("unpublished.name");
				
				SolrSearchDAO solrSearchDAO = new SolrSearchDAOImpl();
				SolrSearchResult solrSearchResult = solrSearchDAO.executeSearch(solrQuery);
				SolrDocumentList docList = solrSearchResult.getDocumentList();
				Iterator<SolrDocument> it = docList.iterator();
				while (it.hasNext()) {
					SolrDocument doc = it.next();
					
					LOGGER.debug("Record to email ready for review for ID: {}, Name: {}", doc.getFieldValue("id"), doc.getFieldValue("unpublished.name"));
	
					String itemURL = GlobalProps.getProperty("app.server") + "/DataCommons/item/" + doc.getFieldValue("id");
					
					Map<String, String> varMap = new HashMap<String, String>();
					varMap.put("itemName", (String)doc.getFieldValue("unpublished.name"));
					varMap.put("itemURL", itemURL);
					
					// Add recipients and send emails
					Email email = new Email(mailSender);
					for (String emailAddr : recipientEmails) {
						email.addRecipient(emailAddr);
					}
//					email.addRecipient("genevieve.turner@anu.edu.au");
					email.setSubject(subject);
					email.setBody(mailTemplate, varMap);
					
					email.send();
					
				}
			}
		}
		catch (SolrServerException | IOException e) {
			LOGGER.error("Exception sending emails", e);
		}
	}
	
	/**
	 * setReadyForPublish
	 * 
	 * Sets the item as ready for publish
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		18/12/2012	Genevieve Turner(GT)	Initial
	 * 0.2		02/01/2012	Genevieve Turner (GT)	Updated to reflect changes in error handling
	 * </pre>
	 * 
	 * @param fedoraObject The item to set as ready for publish
	 * @see au.edu.anu.datacommons.security.service.FedoraObjectService#setReadyForPublish(au.edu.anu.datacommons.data.db.model.FedoraObject)
	 */
	public void setReadyForPublish(FedoraObject fedoraObject) {
		if (fedoraObject.getReviewReady() == null) {
			LOGGER.info("Ready for review is null in publish");
			throw new DataCommonsException(412, "Record is not currently in the ready for review stage");
		}
		else if (fedoraObject.getPublishReady() != null) {
			LOGGER.info("Record is already in the publish queue");
			throw new DataCommonsException(412, "Record is already in the publish queue");
		}
		
		PublishReady publishReady = new PublishReady();
		publishReady.setId(fedoraObject.getId());
		publishReady.setDate_submitted(new Date());
		
		GenericDAO<PublishReady, Long> publishReadyDAO = new GenericDAOImpl<PublishReady, Long>(PublishReady.class);
		publishReadyDAO.create(publishReady);
		
		removeReviewReady(fedoraObject);
		removeReviewReject(fedoraObject);
		setReviewXML(fedoraObject.getObject_id());
		saveAuditReviewLog(fedoraObject, "PUBLISH_READY", null);
		
		String sendEmail = GlobalProps.getProperty("review.publishready.email.enabled", "false");
		if ("true".equals(sendEmail)) {
			sendReadyEmail(fedoraObject, "ANU Data Commons - Ready for Publish", "mailtmpl/readyforpublish.txt", CustomACLPermission.PUBLISH);
		}
	}
	
	/**
	 * setRejected
	 * 
	 * Rejects the item for publishing
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		18/12/2012	Genevieve Turner(GT)	Initial
	 * 0.2		02/01/2012	Genevieve Turner (GT)	Updated to reflect changes in error handling
	 * </pre>
	 * 
	 * @param fedoraObject The item to set to rejected
	 * @param reasons The reason for the rejection
	 * @see au.edu.anu.datacommons.security.service.FedoraObjectService#setRejected(au.edu.anu.datacommons.data.db.model.FedoraObject, java.util.List)
	 */
	public void setRejected(FedoraObject fedoraObject, List<String> reasons) {
		if (fedoraObject.getReviewReady() == null && fedoraObject.getPublishReady() == null) {
			throw new WebApplicationException(412);
		}
		if (reasons == null || reasons.size() == 0 || reasons.get(0).trim().length() == 0) {
			throw new ValidateException("No reasons were given to indicate why more work is required");
		}
		LOGGER.info("Reasons: {}, Number of reasons: {}", reasons, reasons.size());
		
		ReviewReject reviewReject = new ReviewReject();
		reviewReject.setId(fedoraObject.getId());
		reviewReject.setDate_submitted(new Date());
		reviewReject.setReason(reasons.get(0));
		
		// Add the rejection reason
		GenericDAO<ReviewReject, Long> rejectDAO = new GenericDAOImpl<ReviewReject, Long>(ReviewReject.class);
		rejectDAO.create(reviewReject);
		
		// Remove it from the ready or publish queues
		removeReviewReady(fedoraObject);
		removePublishReady(fedoraObject);
		
		setReviewXML(fedoraObject.getObject_id());
		saveAuditReviewLog(fedoraObject, "REVIEW_REJECT", reasons.get(0));
	}
	
	/**
	 * saveAuditReviewLog
	 *
	 * Set the audit log values for the review functionality
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		18/12/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param fedoraObject The object to place an audit row on
	 * @param log_type The type of audit row
	 * @param message A message (if any) about the review/rejection
	 */
	public void saveAuditReviewLog(FedoraObject fedoraObject, String log_type, String message) {
		CustomUser customUser = (CustomUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		AuditObject auditObject = new AuditObject();
		auditObject.setLog_date(new java.util.Date());
		auditObject.setLog_type(log_type);
		auditObject.setObject_id(fedoraObject.getId());
		auditObject.setUser_id(customUser.getId());
		if (Util.isNotEmpty(message)) {
			auditObject.setAfter(message);
		}
		
		GenericDAO<AuditObject,Long> auditDao = new GenericDAOImpl<AuditObject,Long>(AuditObject.class);
		auditDao.create(auditObject);
	}

	/**
	 * setReviewXML
	 *
	 * Set the review XML
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.13		25/07/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param pid The pid to set the review xml for
	 */
	private void setReviewXML(String pid) {
		String location = String.format("%s/objects/%s/datastreams/XML_SOURCE/content", GlobalProps.getProperty(GlobalProps.PROP_FEDORA_URI), pid);
		try {
			FedoraBroker.addDatastreamByReference(pid, Constants.XML_REVIEW, "M", "XML Review", location);
		}
		catch (FedoraClientException e) {
			LOGGER.info("Exception setting review xml: ", e);
		}
	}
	
}
