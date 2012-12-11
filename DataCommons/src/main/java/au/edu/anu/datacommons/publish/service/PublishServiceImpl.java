package au.edu.anu.datacommons.publish.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.WebApplicationException;
import javax.xml.bind.JAXBException;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import au.edu.anu.datacommons.data.db.dao.FedoraObjectDAO;
import au.edu.anu.datacommons.data.db.dao.FedoraObjectDAOImpl;
import au.edu.anu.datacommons.data.db.dao.GenericDAO;
import au.edu.anu.datacommons.data.db.dao.GenericDAOImpl;
import au.edu.anu.datacommons.data.db.model.AuditObject;
import au.edu.anu.datacommons.data.db.model.FedoraObject;
import au.edu.anu.datacommons.data.db.model.Groups;
import au.edu.anu.datacommons.data.db.model.PublishLocation;
import au.edu.anu.datacommons.data.db.model.PublishReady;
import au.edu.anu.datacommons.data.db.model.ReviewReady;
import au.edu.anu.datacommons.data.db.model.ReviewReject;
import au.edu.anu.datacommons.data.fedora.FedoraBroker;
import au.edu.anu.datacommons.data.fedora.FedoraReference;
import au.edu.anu.datacommons.data.solr.SolrManager;
import au.edu.anu.datacommons.data.solr.SolrUtils;
import au.edu.anu.datacommons.exception.ValidationException;
import au.edu.anu.datacommons.properties.GlobalProps;
import au.edu.anu.datacommons.publish.FieldValidate;
import au.edu.anu.datacommons.publish.Publish;
import au.edu.anu.datacommons.publish.Validate;
import au.edu.anu.datacommons.publish.ValidateException;
import au.edu.anu.datacommons.search.SolrSearchResult;
import au.edu.anu.datacommons.security.CustomUser;
import au.edu.anu.datacommons.security.acl.CustomACLPermission;
import au.edu.anu.datacommons.security.acl.PermissionService;
import au.edu.anu.datacommons.security.service.FedoraObjectService;
import au.edu.anu.datacommons.security.service.GroupService;
import au.edu.anu.datacommons.util.Constants;
import au.edu.anu.datacommons.util.Util;
import au.edu.anu.datacommons.xml.transform.ViewTransform;

import com.yourmediashelf.fedora.client.FedoraClientException;

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
	 * @param publishers The locations to validate against
	 * @param ids The ids of the items to validate
	 * @return A map consisting of the pid and validation messages
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @see au.edu.anu.datacommons.publish.service.PublishService#validateMultiple(java.lang.String[], java.lang.String[])
	 */
	public Map<String, List<LocationValidationMessage>> validateMultiple(String[] publishers, String[] ids)
			throws ClassNotFoundException, IllegalAccessException, InstantiationException {
		
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
		LOGGER.info("In validate multiple");
		
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
	public SolrSearchResult getGroupObjects(Long groupId, Integer page) throws SolrServerException {
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
	private SolrSearchResult getFedoraObjects(Long groupId, Integer page) throws SolrServerException {
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
		solrQuery.setSortField("id", ORDER.asc);
		
		SolrServer solrServer = SolrManager.getInstance().getSolrServer();
		QueryResponse queryResponse = solrServer.query(solrQuery);
		SolrDocumentList documentList = queryResponse.getResults();
		LOGGER.info("Number of results found: {}", documentList.getNumFound());
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
		GenericDAO<PublishLocation, Long> publishDAO = new GenericDAOImpl<PublishLocation, Long>(PublishLocation.class);
		
		List<PublishLocation> publishLocations = publishDAO.getAll();
		
		return publishLocations;
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
				LOGGER.info("{} is unpublishable", fedoraObject.getObject_id());
				canPublish = false;
			}
		}
		if (!canPublish) {
			LOGGER.info("Cannot publish");
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
	 * </pre>
	 * 
	 * @param fedoraObject The object to publish
	 * @param publishers The locations to publish to
	 * @return A post publishing message
	 * @throws ValidateException
	 */
	@PreAuthorize("hasPermission(#fedoraObject, 'PUBLISH')")
	private List<String> publishObject(FedoraObject fedoraObject, List<PublishLocation> publishers) throws ValidateException {
		prePublish(fedoraObject);
		
		List<String> locations = new ArrayList<String>();
		try {
			for (PublishLocation publishLocation : publishers) {
				try {
					Publish publish = (Publish) Class.forName(publishLocation.getExecute_class()).newInstance();
					publish.publish(fedoraObject.getObject_id(), publishLocation.getCode());
					if (!fedoraObject.getPublished()) {
						fedoraObject.setPublished(Boolean.TRUE);
					}
					locations.add(publishLocation.getName());
					
					addPublishLocation(fedoraObject, publishLocation);
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
				FedoraObjectDAOImpl object = new FedoraObjectDAOImpl(FedoraObject.class);
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
	 * prePublish
	 *
	 * Performs actions prior to publishing
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		10/12/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param fedoraObject The object to publish
	 * @throws ValidateException
	 */
	private void prePublish(FedoraObject fedoraObject) throws ValidateException {
		Validate validate = new FieldValidate();
		
		if (!validate.isValid(fedoraObject.getObject_id())) {
			StringBuilder errorMessages = new StringBuilder();
			errorMessages.append("Not all required fields have been filled out correctly\n");
			errorMessages.append(Util.listToStringWithNewline(validate.getErrorMessages()));
			throw new ValidateException(errorMessages.toString());
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
	 * </pre>
	 * 
	 * @param fedoraObject The object to publish
	 */
	private void setDefaultsForPublish(FedoraObject fedoraObject) {
		Map<String, String> form = new HashMap<String, String>();
		
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		
		form.put("citationCreator", "The Australian National University");
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
	 * addPublishLocation
	 *
	 * Adds a publish location to the database for an item
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		10/12/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param fedoraObject The object to publish to
	 * @param publishLocation The location the object is being published to
	 */
	private void addPublishLocation(FedoraObject fedoraObject, PublishLocation publishLocation) {
		boolean addPublisher = true;
		for (int i = 0; addPublisher && i < fedoraObject.getPublishedLocations().size(); i++) {
			PublishLocation loc = fedoraObject.getPublishedLocations().get(i);
			if (loc.equals(publishLocation)) {
				addPublisher = false;
			}
			else if (loc.getId().equals(publishLocation.getId()) &&
					loc.getName().equals(publishLocation.getName())) {
				addPublisher = false;
			}
		}
		if (addPublisher) {
			fedoraObject.getPublishedLocations().add(publishLocation);
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
	public SolrSearchResult getItemInformation(String[] ids) throws SolrServerException {
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setRows(rows);
		
		solrQuery.addField("id");
		solrQuery.addField("unpublished.name");
		
		solrQuery.setQuery("*:*");
		solrQuery.setSortField("id", ORDER.asc);
		
		StringBuilder filterQuery = new StringBuilder();
		filterQuery.append("(");
		for (String pid : ids) {
			filterQuery.append("id:");
			filterQuery.append(SolrUtils.escapeSpecialCharacters(pid));
			filterQuery.append(" ");
		}
		filterQuery.append(")");
		solrQuery.addFilterQuery(filterQuery.toString());
		
		SolrServer solrServer = SolrManager.getInstance().getSolrServer();
		QueryResponse queryResponse = solrServer.query(solrQuery);
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
		FedoraObjectDAO fedoraObjectDAO = new FedoraObjectDAOImpl(FedoraObject.class);
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
		FedoraObjectDAO fedoraObjectDAO = new FedoraObjectDAOImpl(FedoraObject.class);
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
		FedoraObjectDAO fedoraObjectDAO = new FedoraObjectDAOImpl(FedoraObject.class);
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
	 * </pre>
	 * 
	 * @param fedoraObject The item to set as ready for review
	 * @see au.edu.anu.datacommons.security.service.FedoraObjectService#setReadyForReview(au.edu.anu.datacommons.data.db.model.FedoraObject)
	 */
	public void setReadyForReview(FedoraObject fedoraObject) {
		if (fedoraObject.getReviewReady() != null || fedoraObject.getPublishReady() != null) {
			LOGGER.info("Record is already in the review queue");
			throw new WebApplicationException(412);
		}
		
		Validate validate = new FieldValidate();
		if (!validate.isValid(fedoraObject.getObject_id())) {
			StringBuffer errorMessages = new StringBuffer();
			errorMessages.append("Not all required fields have been filled out correctly\n");
			errorMessages.append(Util.listToStringWithNewline(validate.getErrorMessages()));
			throw new ValidationException(errorMessages.toString());
		}
		
		ReviewReady reviewReady = new ReviewReady();
		reviewReady.setId(fedoraObject.getId());
		reviewReady.setDate_submitted(new Date());
		
		GenericDAO<ReviewReady, Long> reviewReadyDAO = new GenericDAOImpl<ReviewReady, Long>(ReviewReady.class);
		reviewReadyDAO.create(reviewReady);
		
		removeReviewReject(fedoraObject);
		saveAuditReviewLog(fedoraObject, "REVIEW_READY", null);
	}
	
	/**
	 * setReadyForPublish
	 * 
	 * Sets the item as ready for publish
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		18/12/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param fedoraObject The item to set as ready for publish
	 * @see au.edu.anu.datacommons.security.service.FedoraObjectService#setReadyForPublish(au.edu.anu.datacommons.data.db.model.FedoraObject)
	 */
	public void setReadyForPublish(FedoraObject fedoraObject) {
		if (fedoraObject.getReviewReady() == null) {
			LOGGER.info("Ready for review is null in publish");
			throw new WebApplicationException(412);
		}
		else if (fedoraObject.getPublishReady() != null) {
			LOGGER.info("Record is already in the publish queue");
			throw new WebApplicationException(412);
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
	}
	
	/**
	 * setRejected
	 * 
	 * Rejects the item for publishing
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		18/12/2012	Genevieve Turner(GT)	Initial
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
		if (reasons == null || reasons.size() == 0) {
			throw new ValidationException("No reasons given");
		}
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
