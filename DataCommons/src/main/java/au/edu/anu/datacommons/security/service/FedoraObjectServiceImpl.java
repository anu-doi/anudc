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

import static java.text.MessageFormat.format;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.IOUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.sun.jersey.api.client.ClientResponse;
import com.yourmediashelf.fedora.client.FedoraClientException;

import au.edu.anu.datacommons.data.db.dao.ExternalLinkDAO;
import au.edu.anu.datacommons.data.db.dao.ExternalLinkDAOImpl;
import au.edu.anu.datacommons.data.db.dao.FedoraObjectDAOImpl;
import au.edu.anu.datacommons.data.db.dao.GenericDAO;
import au.edu.anu.datacommons.data.db.dao.GenericDAOImpl;
import au.edu.anu.datacommons.data.db.dao.LinkTypeDAO;
import au.edu.anu.datacommons.data.db.dao.LinkTypeDAOImpl;
import au.edu.anu.datacommons.data.db.dao.TemplateDAO;
import au.edu.anu.datacommons.data.db.dao.TemplateDAOImpl;
import au.edu.anu.datacommons.data.db.model.ExternalLinkPattern;
import au.edu.anu.datacommons.data.db.model.FedoraObject;
import au.edu.anu.datacommons.data.db.model.Groups;
import au.edu.anu.datacommons.data.db.model.LinkType;
import au.edu.anu.datacommons.data.db.model.PublishReady;
import au.edu.anu.datacommons.data.db.model.ReviewReady;
import au.edu.anu.datacommons.data.db.model.TemplateAttribute;
import au.edu.anu.datacommons.data.fedora.FedoraBroker;
import au.edu.anu.datacommons.data.fedora.FedoraReference;
import au.edu.anu.datacommons.data.solr.SolrManager;
import au.edu.anu.datacommons.doi.DoiClient;
import au.edu.anu.datacommons.doi.DoiException;
import au.edu.anu.datacommons.doi.DoiResourceAdapter;
import au.edu.anu.datacommons.exception.DataCommonsException;
import au.edu.anu.datacommons.exception.ValidateException;
import au.edu.anu.datacommons.properties.GlobalProps;
import au.edu.anu.datacommons.search.ExternalPoster;
import au.edu.anu.datacommons.search.SparqlQuery;
import au.edu.anu.datacommons.security.acl.CustomACLPermission;
import au.edu.anu.datacommons.security.acl.PermissionService;
import au.edu.anu.datacommons.storage.DcStorage;
import au.edu.anu.datacommons.storage.controller.StorageController;
import au.edu.anu.datacommons.storage.info.RecordDataSummary;
import au.edu.anu.datacommons.storage.provider.StorageException;
import au.edu.anu.datacommons.util.Constants;
import au.edu.anu.datacommons.util.Util;
import au.edu.anu.datacommons.webservice.bindings.FedoraItem;
import au.edu.anu.datacommons.xml.data.Data;
import au.edu.anu.datacommons.xml.data.DataItem;
import au.edu.anu.datacommons.xml.sparql.Result;
import au.edu.anu.datacommons.xml.sparql.Sparql;
import au.edu.anu.datacommons.xml.template.Template;
import au.edu.anu.datacommons.xml.transform.JAXBTransform;
import au.edu.anu.datacommons.xml.transform.ViewTransform;

/**
 * FedoraObjectServiceImpl
 * 
 * Australian National University Data Commons
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
 * 0.3		05/05/2012	Genevieve Turner (GT)	Added getting a list of publishers
 * 0.4		15/05/2012	Genevieve Turner (GT)	Publishing to publishers
 * 0.5		16/05/2012	Genevivee Turner (GT)	Updated to allow differing configurations for publishing
 * 0.6		21/05/2012	Genevieve Turner (GT)	Added saving publication locations to database
 * 0.7		08/06/2012	Genevieve Turner (GT)	Updated to amend some of the publishing processes
 * 0.8		20/06/2012	Genevieve Turner (GT)	Moved permissions and change the page retrieval from a String to a Map
 * 0.9		20/06/2012	Genevieve Turner (GT)	Updated to add an audit row when an object is published
 * 0.10		11/07/2012	Genevieve Turner (GT)	Updated to allow or deny access to unpublished pages
 * 0.11		13/07/2012	Rahul Khanna (RK)		Updated filelist displayed on collection page
 * 0.12		17/07/2012	Genevieve Turner (GT)	Added validation prior to publishing
 * 0.13		24/07/2012	Genevieve Turner (GT)	Moved the generating of a list of messages to a util function
 * 0.14		27/07/2012	Genevieve Turner (GT)	Added method to retrieve some information about a fedora object
 * 0.15		20/08/2012	Genevieve Turner (GT)	Updated to use permissionService rather than aclService
 * 0.16		27/08/2012	Genevieve Turner (GT)	Fixed issue where group was not updated when editing
 * 0.17		28/08/2012	Genevieve Turner (GT)	Added the display of reverse links
 * 0.18		19/09/2012	Genevieve Turner (GT)	Updated to add a row to the audit log table for review statuses
 * 0.19		27/09/2012	Genevieve Turner (GT)	Updated to generate reverse links
 * 0.20		15/10/2012	Genevieve Turner (GT)	Modified/Added some functions surrounding publication
 * 0.21		22/10/2012	Genevieve Turner (GT)	Added link removal and chagned getLinks method to be public
 * 0.22		06/11/2012	Genevieve Turner (GT)	Updated to check if the user has permissions to update the group if not remove those permissions
 * 0.23		26/11/2012	Genevieve Turner (GT)	Added the removal of reverse links
 * 0.24		11/12/2012	Genevieve Turner (GT)	Moved some publishing methods to PublishServiceImpl
 * 0.25		02/01/2012	Genevieve Turner (GT)	Updated to enforce records requriing an ownerGroup, type and name/title for new records
 * </pre>
 * 
 */
@Service("fedoraObjectServiceImpl")
public class FedoraObjectServiceImpl implements FedoraObjectService {
	private static final Logger LOGGER = LoggerFactory.getLogger(FedoraObjectServiceImpl.class);
	private static JAXBContext dataContext;
	
	static {
		try {
			dataContext = JAXBContext.newInstance(Data.class);
		} catch (JAXBException e) {
			LOGGER.error("Unable to create JAXB Context for Data Element. Error: {}", e.getMessage());
		}
	}

	@Resource(name = "groupServiceImpl")
	private GroupService groupService;
	
	@Resource(name="riSearchService")
	ExternalPoster riSearchService;
	
	@Resource(name="permissionService")
	PermissionService permissionService;
	
	@Resource(name = "dcStorage")
	private DcStorage dcStorage;
	
	@Autowired
	protected StorageController storageController;
	
	/**
	 * getItemByName
	 * 
	 * Gets the fedora object given the pid
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/04/2012	Genevieve Turner (GT)	Initial
	 * 0.2		02/05/2012	Genevieve Turner (GT)	Updated to fix issue with url encoded pid
	 * 0.3		08/05/2012	Genevieve Turner (GT)	Updated to use newly created util decode function
	 * </pre>
	 * 
	 * @param id The fedora object pid
	 * @return Returns the FedoraObject of the given pid
	 */
	@Override
	public FedoraObject getItemByPid(String pid) {
		String decodedPid = null;
		decodedPid = Util.decodeUrlEncoded(pid);
		if (decodedPid == null) {
			return null;
		}
		FedoraObjectDAOImpl object = new FedoraObjectDAOImpl();
		FedoraObject item = object.getSingleByName(decodedPid);
		if (item != null) {
			LOGGER.trace("Retrieved item {}", item.getObject_id());
		}
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
	 * 0.23		13/11/2012	Genevieve Turner (GT)	Added the edit mode to the getPage method
	 * </pre>
	 * 
	 * @param fedoraObject The item to transform to a display
	 * @param layout The layout that defines the flow of the items on the page
	 * @param tmplt The template that determines the fields on the screen
	 * @return Returns the viewable for the jsp file to pick up.
	 */
	@Override
	public Map<String, Object> getViewPage(FedoraObject fedoraObject, String layout, String tmplt) {
		Map<String, Object> values = getPage(layout, tmplt, fedoraObject, false);
		return values;
	}
	
	public RecordDataSummary getRecordDataSummary(FedoraObject fedoraObject) {
		try {
			RecordDataSummary rdi = storageController.getRecordDataSummary(fedoraObject.getObject_id());
			return rdi;
		}
		catch (IOException | StorageException e) {
			LOGGER.error(e.getMessage(), e);
		}
		
		return null;
	}
	
	public Map<String, Object> getViewObjects(FedoraObject fedoraObject, String tmplt) {
		if (fedoraObject != null) {
			
		}
		
		Map<String, Object> values = null;
		return values;
	}
	
	/**
	 * getNewPage
	 * 
	 * Transforms the given information into information for display for a new page
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/04/2012	Genevieve Turner (GT)	Initial
	 * 0.23		13/11/2012	Genevieve Turner (GT)	Added the edit mode to the getPage method
	 * </pre>
	 * 
	 * @param layout The layout that defines the flow of the items on the page
	 * @param tmplt The template that determines the fields on the screen
	 * @return Returns the viewable for the jsp file to pick up.
	 */
	@Override
	public Map<String, Object> getNewPage(String layout, String tmplt) {
		Map<String, Object> values = getPage(layout, tmplt, null, false);
		return values;
	}
	
	/**
	 * saveNew
	 * 
	 * Saves the information then displays a page with the given information
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/04/2012	Genevieve Turner (GT)	Initial
	 * 0.8		20/06/2012	Genevieve Turner (GT)	Updated so that page retrieval is now using a map
	 * 0.15		20/08/2012	Genevieve Turner (GT)	Updated to use permissionService rather than aclService
	 * 0.23		12/11/2012	Genevieve Turner (GT)	Added the request id
	 * 0.25		02/01/2012	Genevieve Turner (GT)	Updated to enforce records requriing an ownerGroup, type and name/title
	 * </pre>
	 * 
	 * @param layout The layout to display the page
	 * @param tmplt The template that determines the fields on the screen
	 * @param form Contains the parameters from the request
	 * @param rid The request id
	 * @return Returns the viewable for the jsp file to pick up.
	 * @throws JAXBException 
	 * @throws FedoraClientException 
	 */
	@Override
	public FedoraObject saveNew(String tmplt, Map<String, List<String>> form, Long rid) throws FedoraClientException, JAXBException
	{
		FedoraObject fedoraObject = null;
		ViewTransform viewTransform = new ViewTransform();
		
		List<String> messages = new ArrayList<String>();
		if (form.get("ownerGroup") == null || form.get("ownerGroup").size() == 0 || form.get("ownerGroup").get(0).trim().equals("")) {
			messages.add("No Group Affiliation");
		}
		if (form.get("type") == null || form.get("type").size() == 0 || form.get("type").get(0).trim().equals("")) {
			messages.add("No item type has been set");
		}
		if ((form.get("name") == null || form.get("name").size() == 0 || form.get("name").get(0).trim().equals("")) && (form.get("lastName") == null || form.get("lastName").size() == 0 || form.get("lastName").get(0).trim().equals(""))) {
			messages.add("No name/title has been set");
		}
		
		if (messages.size() == 0) {
			// Check if the user has access to the ownerGroup
			String ownerGroup = form.get("ownerGroup").get(0);
			Long ownerGroupId = new Long(ownerGroup);
			List<Groups> groups = groupService.getCreateGroups();
			boolean groupFound = false;
			for (Groups group : groups) {
				if (group.getId().equals(ownerGroupId)) {
					groupFound = true;
					break;
				}
			}
			if (groupFound == false) {
				throw new AccessDeniedException(format("You do not have permissions to create in group {0}", ownerGroup));
			}
		}
		else {
			throw new ValidateException(messages);
		}
		
		fedoraObject = viewTransform.saveData(tmplt, null, form, rid);
		permissionService.saveObjectPermissions(fedoraObject);
		
		return fedoraObject;
	}
	
	/**
	 * Passes the template name and datamap to saveNew.
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * X.X		XX/XX/2012	Rahul Khanna (RK)		Initial
	 * 0.23		12/11/2012	Genevieve Turner (GT)	Added the request id
	 * </pre>
	 * 
	 * @param item
	 *            FedoraItem created from XML input.
	 * @param rid The request id
	 * @return FedoraObject created/updated.
	 * @throws JAXBException 
	 * @throws FedoraClientException 
	 */
	@Override
	public FedoraObject saveNew(FedoraItem item, Long rid) throws FedoraClientException, JAXBException
	{
		FedoraObject fedoraObject = null;
		fedoraObject = this.saveNew(item.getTemplate(), item.generateDataMap(), rid);
		return fedoraObject;
	}

	/**
	 * getEditItem
	 * 
	 * Retrieves information about a particular field
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/04/2012	Genevieve Turner (GT)	Initial
	 * 0.8		20/06/2012	Genevieve Turner (GT)	Updated so that page retrieval is now using a map
	 * </pre>
	 * 
	 * @param fedoraObject The item to transform to a display
	 * @param layout The layout that defines the flow of the items on the page
	 * @param tmplt The template that determines the fields on the screen
	 * @param fieldName
	 * @return
	 */
	@Override
	public String getEditItem(FedoraObject fedoraObject, String layout, String tmplt, String fieldName) {
		String fields = "";
		ViewTransform viewTransform = new ViewTransform();
		try {
			fields = (String)viewTransform.getPage(layout, tmplt, fedoraObject, fieldName, true, false).get("page");
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
	 * 0.23		13/11/2012	Genevieve Turner (GT)	Added whether edit mode should be used in retrieving the page (i.e. no published information is retrieved)
	 * </pre>
	 * 
	 * @param fedoraObject The  fedora object to get the page for
	 * @param layout The layout to use with display (i.e. the xsl stylesheet)
	 * @param tmplt The template that determines the fields on the screen
	 * @param editMode Whether the returned information contains does or does not contained published information (false for only the unpublished information to be returned)
	 * @return Returns the viewable for the jsp file to pick up.
	 */
	@Override
	public Map<String, Object> getEditPage(FedoraObject fedoraObject, String layout, String tmplt, boolean editMode) {
		Map<String, Object> values = getPage(layout, tmplt, fedoraObject, editMode);
		try {
			// Add the template objects to the viewable, and change the side page
			Template template = new ViewTransform().getTemplateObject(tmplt, fedoraObject);
			values.put("template", template);
			values.put("fedoraObject", fedoraObject);
			values.put("sidepage", "edit.jsp");
		}
		catch (FedoraClientException e) {
			LOGGER.error("Exception: ", e);
			values.put("topage", "/error.jsp");
		}
		catch (JAXBException e) {
			LOGGER.error("Error transforming object: ", e);
			values.put("topage", "/error.jsp");
		}
		return values;
	}
	
	/**
	 * saveEdit
	 * 
	 * Updates the object given the specified parameters, and form values.
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/04/2012	Genevieve Turner (GT)	Initial
	 * 0.7		04/06/2012	Genevieve Turner (GT)	Fixed an issue where the fedora object was not returned in the values map
	 * 0.8		20/06/2012	Genevieve Turner (GT)	Updated so that page retrieval is now using a map
	 * 0.13		25/07/2012	Genevieve Turner (GT)	Added removing of ready for review/publish
	 * 0.16		27/08/2012	Genevieve Turner (GT)	Fixed issue where group was not updated when editing
	 * 0.22		06/11/2012	Genevieve Turner (GT)	Updated to check if the user has permissions to update the group if not remove those permissions
	 * 0.23		12/11/2012	Genevieve Turner (GT)	Added the request id
	 * </pre>
	 * 
	 * @param fedoraObject The  fedora object to get the page for
	 * @param tmplt The template that determines the fields on the screen
	 * @param form The form fields of the screen
	 * @param rid The request id
	 * @return Returns the viewable for the jsp file to pick up.
	 */
	@Override
	public Map<String, Object> saveEdit(FedoraObject fedoraObject, String tmplt, 
			Map<String, List<String>> form, Long rid) {
		Map<String, Object> values = new HashMap<String, Object>();
		ViewTransform viewTransform = new ViewTransform();
		try {
			if (form.containsKey("ownerGroup")) {
				//TODO Update this so that an error is thrown if the user does not have permissions to update the group
				if (!permissionService.hasSetGroupPermissionsForObject(fedoraObject)) {
					form.remove("ownerGroup");
				}
			}
			fedoraObject = viewTransform.saveData(tmplt, fedoraObject, form, rid);
			removeReviewReady(fedoraObject);
			removePublishReady(fedoraObject);
			if (form.containsKey("ownerGroup")) {
				permissionService.saveObjectPermissions(fedoraObject);
			}
		}
		catch (JAXBException e) {
			LOGGER.error("Exception transforming jaxb", e);
			values.put("error", "true");
		}
		catch (FedoraClientException e) {
			LOGGER.error("Exception creating/retrieving objects", e);
			values.put("error", "true");
		}
		
		return values;
	}
	
	/**
	 * 
	 * saveEdit
	 * 
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * X.X		XX/XX/2012	Rahul Khanna (RK)		Initial
	 * 0.23		12/11/2012	Genevieve Turner (GT)	Added the request id
	 * </pre>
	 * 
	 * @param item
	 * @param rid The request id
	 * @return
	 * @throws FedoraClientException
	 * @throws JAXBException
	 * @see au.edu.anu.datacommons.security.service.FedoraObjectService#saveEdit(au.edu.anu.datacommons.webservice.bindings.FedoraItem, java.lang.Long)
	 */
	@Override
	public FedoraObject saveEdit(FedoraItem item, Long rid) throws FedoraClientException, JAXBException
	{
		ViewTransform viewTransform = new ViewTransform();
		FedoraObject fo = this.getItemByPid(item.getPid());
		fo = viewTransform.saveData(item.getTemplate(), fo, item.generateDataMap(), rid);
		return fo;
	}
	
	/**
	 * delete
	 * 
	 * Set the status of the given object to deleted
	 * 
	 * @param fedoraObject The fedora object
	 * @throws FedoraClientException
	 */
	public void delete(FedoraObject fedoraObject) throws FedoraClientException
	{
		FedoraBroker.updateObjectState(fedoraObject.getObject_id(), "D");
		// Add this as a work around for the fact that commitWithin does not seem to work for
		// the Solr XML delete so we want to commit after delete
		SolrClient solrClient = SolrManager.getInstance().getSolrClient();
		try {
			solrClient.deleteById(fedoraObject.getObject_id(), 5000);
		}
		catch (IOException e) {
			LOGGER.debug("Exception committing delete", e);
		}
		catch (SolrServerException e) {
			LOGGER.debug("Exception committing delete", e);
		}
	}
	
	/**
	 * addLink
	 * 
	 * Create a link between two items
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/04/2012	Genevieve Turner (GT)	Initial
	 * 0.19		27/09/2012	Genevieve Turner (GT)	Updated to generate reverse links
	 * </pre>
	 * 
	 * @param fedoraObject The item to transform to a display
	 * @param form Contains the parameters from the request
	 * @return A response for the web page
	 * @throws FedoraClientException 
	 */
	@Override
	public void addLink(FedoraObject fedoraObject, String linkType, String itemId) throws FedoraClientException {
		String link = GlobalProps.getProperty(GlobalProps.PROP_FEDORA_RELATEDURI);
		String pidNamespace = GlobalProps.getProperty(GlobalProps.PROP_FEDORA_SAVENAMESPACE);
		
		LinkTypeDAO linkTypeDAO = new LinkTypeDAOImpl();
		LinkType linkTypeRecord = linkTypeDAO.getByCode(linkType);
		if (null == linkTypeRecord) {
			throw new WebApplicationException(Response.status(400).entity("Invalid relation type").build());
		}
		
		if (itemId.startsWith("info:fedora/")) {
			String referencePid = itemId.substring(12);
			FedoraObject referenceItem = getItemByPid(referencePid);
			if (referenceItem == null) {
				throw new DataCommonsException(Status.NOT_FOUND, "Unable to find reference object");
			}
			addLink(fedoraObject, link + linkTypeRecord.getCode(), link + linkTypeRecord.getReverse(), referenceItem);
		}
		else if (itemId.startsWith(pidNamespace)) {
			FedoraObject referenceItem = getItemByPid(itemId);
			if (referenceItem == null) {
				throw new DataCommonsException(Status.NOT_FOUND, "Unable to find reference object");
			}
			addLink(fedoraObject, link + linkTypeRecord.getCode(), link + linkTypeRecord.getReverse(), referenceItem);
		}
		else {
			ExternalLinkDAO linkDAO = new ExternalLinkDAOImpl();
			List<ExternalLinkPattern> patterns = linkDAO.findByReference(itemId);
			// Check the link belongs to a pattern
			if (patterns == null || patterns.size() == 0) {
				throw new DataCommonsException(400, "Invalid external link");
			}
			
			saveLink(fedoraObject, link + linkTypeRecord.getCode(), itemId);
		}
	}
	
	/**
	 * Add links
	 * 
	 * @param fedoraObject The fedora object to create links for
	 * @param linkType The link type
	 * @param reverseLink The reverse of the link type
	 * @param linkTo The fedora object for opposite relation
	 * @throws FedoraClientException
	 */
	private void addLink(FedoraObject fedoraObject, String linkType, String reverseLink, FedoraObject linkTo) throws FedoraClientException {
		String fedoraPredicate = "info:fedora/";
		saveLink(fedoraObject, linkType, fedoraPredicate + linkTo.getObject_id());
		saveLink(linkTo, reverseLink, fedoraPredicate + fedoraObject.getObject_id());
	}
	
	/**
	 * Save the link
	 * 
	 * @param fedoraObject The object ot save the relation with
	 * @param linkType The type of relationship
	 * @param link The link value
	 * @throws FedoraClientException
	 */
	private void saveLink(FedoraObject fedoraObject, String linkType, String link) throws FedoraClientException {
		FedoraReference reference = new FedoraReference();
		reference.setPredicate_(linkType);
		reference.setObject_(link);
		reference.setIsLiteral_(Boolean.FALSE);
		FedoraBroker.addRelationship(fedoraObject.getObject_id(), reference);
	}
	
	/**
	 * removeLink
	 * 
	 * Remove the specified link with the object
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.21		22/10/2012	Genevieve Turner(GT)	Initial
	 * 0.23		26/11/2012	Genevieve Turner (GT)	Added the removal of reverse links
	 * </pre>
	 * 
	 * @param fedoraObject The fedoraObject to remove an association with
	 * @param linkType The relationship type to remove
	 * @param itemId The item to remove the relationship from
	 * @throws FedoraClientException
	 * @see au.edu.anu.datacommons.security.service.FedoraObjectService#removeLink(au.edu.anu.datacommons.data.db.model.FedoraObject, java.lang.String, java.lang.String)
	 */
	@Override
	public void removeLink(FedoraObject fedoraObject, String linkType, String itemId)
			throws FedoraClientException {
		String link = GlobalProps.getProperty(GlobalProps.PROP_FEDORA_RELATEDURI);
		
		LinkTypeDAO linkTypeDAO = new LinkTypeDAOImpl();
		LinkType linkTypeRecord = linkTypeDAO.getByCode(linkType);
		if (null == linkTypeRecord) {
			throw new WebApplicationException(Response.status(400).entity("Invalid relation type").build());
		}
		
		FedoraReference reference = new FedoraReference();
		String referenceType = linkType;
		String referenceItem = itemId;
		
		reference.setPredicate_(link + referenceType);
		reference.setObject_(referenceItem);
		reference.setIsLiteral_(Boolean.FALSE);
		LOGGER.debug("Item: {}, Predicate: {}, Object: {}", new Object[]{fedoraObject.getObject_id(), reference.getPredicate_(), reference.getObject_()});
		FedoraBroker.removeRelationship(fedoraObject.getObject_id(), reference);
		
		if (referenceItem.startsWith("info:fedora/")) {
			String referenceItemID = referenceItem.substring(12);
			FedoraReference reverseReference = new FedoraReference();
			reverseReference.setPredicate_(link + linkTypeRecord.getReverse());
			reverseReference.setObject_("info:fedora/" + fedoraObject.getObject_id());
			reverseReference.setIsLiteral_(Boolean.FALSE);
			LOGGER.debug("Item: {}, Predicate: {}, Object: {}", new Object[]{referenceItemID, reverseReference.getPredicate_(), reverseReference.getObject_()});
			FedoraBroker.removeRelationship(referenceItemID, reverseReference);
		}
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
	 * 0.8		20/06/2012	Genevieve Turner (GT)	Updated so that page retrieval is now using a map
	 * 0.10		11/07/2012	Genevieve Turner (GT)	Updated to allow or deny access to unpublished pages
	 * 0.11		13/07/2012	Rahul Khanna (RK)		Updated filelist displayed on collection page
	 * 0.15		20/08/2012	Genevieve Turner (GT)	Updated to use permissionService rather than aclService
	 * 0.23		13/11/2012	Genevieve Turner (GT)	Added whether edit mode should be used in retrieving the page (i.e. no published information is retrieved)
	 * </pre>
	 * 
	 * @param layout The layout to use with display (i.e. the xsl stylesheet)
	 * @param tmplt The template that determines the fields on the screen
	 * @param fedoraObject The object of the page to retrieve
	 * @param editMode Indicates whether published information should be returned or not
	 * @return Returns the viewable for the jsp file to pick up.
	 */
	private Map<String, Object> getPage(String layout, String template, FedoraObject fedoraObject, boolean editMode) {
		boolean hasPermission = false;
		if (fedoraObject == null) {
			hasPermission = true;
		}else {
			hasPermission = permissionService.checkViewPermission(fedoraObject);
		}
		Map<String, Object> values = new HashMap<String, Object>();
		values.put("topage", "/page.jsp");
		ViewTransform viewTransform = new ViewTransform();
		try {
			if (fedoraObject != null)
			{
				// Add bag summary to model.
				try {
					RecordDataSummary rdi = storageController.getRecordDataSummary(fedoraObject.getObject_id());
					values.put("rdi", rdi);
				} catch (IOException | StorageException e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
			if (hasPermission) {
				values.putAll(viewTransform.getPage(layout, template, fedoraObject, null, editMode, false));
			}
			else if (fedoraObject.getPublished()) {
				values.putAll(viewTransform.getPage(layout, template, fedoraObject, null, false, true));
			}
			else {
				throw new AccessDeniedException(format("User does not have permission to view page for record {0}", fedoraObject.getObject_id()));
			}
		}
		catch (FedoraClientException e) {
			LOGGER.error("Exception: ", e);
			values.put("topage", "/error.jsp");
		}

		if (!values.containsKey("page")) {
			values.put("topage", "/error.jsp");
		}
		
		if (fedoraObject != null) {
			//TODO This is should probably be modified
			values.put("fedoraObject", fedoraObject);

			String sidepage = "buttons.jsp";
			values.put("sidepage", sidepage);
			
			//SparqlResultSet resultSet = getLinks(fedoraObject);
			List<Result> resultSet = getLinks(fedoraObject);
			values.put("resultSet", resultSet);
		}
		
		return values;
	}
	

	public au.edu.anu.datacommons.data.db.model.Template getTemplateByTemplateId(String templateId) throws FedoraClientException, JAXBException {
//		Template template = 
		//TODO fix this so it is injected
		TemplateDAO templateDAO = new TemplateDAOImpl();
		au.edu.anu.datacommons.data.db.model.Template template = templateDAO.getTemplateByPid(templateId);
		
		return template;
		
//		ViewTransform viewTransform = new ViewTransform();
//		return viewTransform.getTemplate(templateId, null);
	}

	@Override
	public Data getEditData(FedoraObject fedoraObject) throws JAXBException, FedoraClientException {
		boolean hasPermission = permissionService.checkPermission(fedoraObject, CustomACLPermission.WRITE);
		if (!hasPermission) {
			throw new AccessDeniedException("You do not have permission to edit the object " + fedoraObject.getObject_id());
		}
		return getItemData(fedoraObject, Constants.XML_SOURCE);
	}

	@Override
	public Data getPublishData(FedoraObject fedoraObject) throws JAXBException, FedoraClientException {
		return getItemData(fedoraObject, Constants.XML_PUBLISHED);
	}
	
	private Data getItemData(FedoraObject fedoraObject, String source) throws JAXBException, FedoraClientException {
		JAXBTransform jaxbTransform = new JAXBTransform();
		InputStream dataStream = getInputStream(fedoraObject.getObject_id(), source);
		Data data = null;
		data = (Data) jaxbTransform.unmarshalStream(dataStream, Data.class);
		return data;
	}
	
	//Note required after getItemData is removed
	private InputStream getInputStream (String pid, String dsId) throws FedoraClientException {
		InputStream xslStream = null;
		if(Util.isNotEmpty(pid)) {
			xslStream = FedoraBroker.getDatastreamAsStream(pid, dsId);
		}
		return xslStream;
	}
	
	/**
	 * getLinks
	 * 
	 * Retrieves the links for a page.
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.3		26/04/2012	Genevieve Turner (GT)	Initial
	 * 0.7		08/06/2012	Genevieve Turner (GT)	Updated to cater for change to post method in the riSearchService
	 * 0.17		28/08/2012	Genevieve Turner (GT)	Added the display of reverse links
	 * 0.19		28/09/2012	Genevieve Turner (GT)	Updated so reverse links are not displayed
	 * 0.21		22/10/2012	Genevieve Turner (GT)	Made this method public
	 * </pre>
	 * 
	 * @param fedoraObject The object to retrieve the links for
	 * @return The results of the query
	 */
	@Override
	public List<Result> getLinks(FedoraObject fedoraObject) {
		SparqlQuery sparqlQuery = new SparqlQuery();
		
		sparqlQuery.addVar("?item");
		sparqlQuery.addVar("?title");
		sparqlQuery.addVar("?predicate");
		sparqlQuery.addVar("?type");
		
		sparqlQuery.addTriple("<info:fedora/" + fedoraObject.getObject_id() +">", "?predicate", "?item", Boolean.FALSE);
		// GT - 20120928 - Note this code is only commented out as it may be placed back in at a later date.
		/*
		StringBuilder tripleString = new StringBuilder();
		tripleString.append("{ <info:fedora/");
		tripleString.append(fedoraObject.getObject_id());
		tripleString.append("> ?predicate ?item . } ");
		tripleString.append("UNION ");
		tripleString.append("{ ?item ?predicate <info:fedora/");
		tripleString.append(fedoraObject.getObject_id());
		tripleString.append("> } ");
		
		sparqlQuery.addTripleSet(tripleString.toString());
		*/
		//Ensure that the linked to item is active (i.e. it hasn't been deleted)
		sparqlQuery.addTriple("?item", "<dc:title>", "?title", true);
		sparqlQuery.addTriple("?item", "<dc:type>", "?type", true);
		sparqlQuery.addTriple("?item", "<fedora-model:state>", "?state", true);
		String filterString = "regex(str(?predicate), '" + GlobalProps.getProperty(GlobalProps.PROP_FEDORA_RELATEDURI) + "', 'i')";
		sparqlQuery.addFilter(filterString, "");
		sparqlQuery.addFilter("!BOUND(?state) || regex(str(?state), 'Active')", "&&");
		ClientResponse respFromRiSearch = riSearchService.post("query", sparqlQuery.generateQuery());
		
		List<Result> resultList = getSparqlResultList(respFromRiSearch);
		LOGGER.debug("Number of related items found: {}", resultList.size());
		return resultList;
	}
	
	/**
	 * removeReviewReady
	 *
	 * Remove the ready for the review status from the database
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.13		25/07/2012	Genevieve Turner(GT)	Initial
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
	 * 0.13		25/07/2012	Genevieve Turner(GT)	Initial
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
	 * 0.13		25/07/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param fedoraObject The fedora object
	 */
	/*private void removeReviewReject(FedoraObject fedoraObject) {
		if (fedoraObject.getReviewReject() != null) {
			GenericDAO<ReviewReject, Long> rejectDAO = new GenericDAOImpl<ReviewReject, Long>(ReviewReject.class);
			rejectDAO.delete(fedoraObject.getId());
		}
	}*/
	
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
	/*private void setReviewXML(String pid) {
		String location = String.format("%s/objects/%s/datastreams/XML_SOURCE/content", GlobalProps.getProperty(GlobalProps.PROP_FEDORA_URI), pid);
		try {
			FedoraBroker.addDatastreamByReference(pid, Constants.XML_REVIEW, "M", "XML Review", location);
		}
		catch (FedoraClientException e) {
			LOGGER.info("Exception setting review xml: ", e);
		}
	}*/
	
	/**
	 * getListInformation
	 * 
	 * Retrieves some information about the list of fedora objects.
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.14		26/07/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param fedoraObjects A list of fedora objects to get more information for
	 * @return Returns a list of fedora objects
	 * @see au.edu.anu.datacommons.security.service.FedoraObjectService#getListInformation(java.util.List)
	 */
	@Override
	public List<Result> getListInformation(List<FedoraObject> fedoraObjects) {
		if (fedoraObjects.size() == 0) {
			return null;
		}
		// Create the sparql query
		SparqlQuery sparqlQuery = new SparqlQuery();
		sparqlQuery.addVar("?id");
		sparqlQuery.addVar("?name");
		sparqlQuery.addTriple("?item", "<dc:identifier>", "?id", Boolean.FALSE);
		sparqlQuery.addTriple("?item", "<dc:title>", "?name", Boolean.FALSE);
		//Ensure that the linked to item is active (i.e. it hasn't been deleted)
		sparqlQuery.addTriple("?item", "<fedora-model:state>", "<fedora-model:Active>", Boolean.FALSE);
		StringBuffer queryFilter = new StringBuffer();
		for (FedoraObject fedoraObject : fedoraObjects) {
			if (queryFilter.length() > 0) {
				queryFilter.append(" || ");
			}
			queryFilter.append("?id = '");
			queryFilter.append(fedoraObject.getObject_id());
			queryFilter.append("' ");
		}
		sparqlQuery.addFilter(queryFilter.toString(), "");
		
		ClientResponse clientResponse =riSearchService.post("query", sparqlQuery.generateQuery());
		
		List<Result> resultList = getSparqlResultList(clientResponse);
		return resultList;
	}
	
	/**
	 * getSparqlResultList
	 *
	 * Transforms a Sparql response to a result list
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.17		28/08/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param clientResponse
	 * @return
	 */
	private List<Result> getSparqlResultList(ClientResponse clientResponse) {
		List<Result> resultList = null;
		JAXBTransform jaxbTransform = new JAXBTransform();
		try {
			Sparql sparqlResult = (Sparql)jaxbTransform.unmarshalStream(clientResponse.getEntityInputStream(), Sparql.class);
			if (sparqlResult != null && sparqlResult.getResults() != null && sparqlResult.getResults().getResults() != null) {
				resultList = sparqlResult.getResults().getResults();
			}
		}
		catch (JAXBException e) {
			LOGGER.info("Exception doing transform", e);
		}
		return resultList;
	}
	
	/**
	 * hasReportPermission
	 * 
	 * Verifies that the user has permission to review the report
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.20		02/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param fedoraObject The fedora object to verify
	 * @see au.edu.anu.datacommons.security.service.FedoraObjectService#hasReportPermission(au.edu.anu.datacommons.data.db.model.FedoraObject)
	 */
	@Override
	public void hasReportPermission(FedoraObject fedoraObject) {
		// do nothing
	}
	
	@Override
	public void generateDoi(String pid, String tmplt, Long rid) throws FedoraObjectException
	{
		InputStream datastream = null;
		try
		{
			Unmarshaller um = dataContext.createUnmarshaller();
			datastream = FedoraBroker.getDatastreamAsStream(pid, Constants.XML_SOURCE);
			Data itemData = (Data) um.unmarshal(datastream);
			
			// Check if it's a collection.
			String type = itemData.getFirstElementByName("type").getValue();
			if (!type.equalsIgnoreCase("collection"))
				throw new FedoraObjectException("Item is not of type 'collection'. Digital Object Identifiers can only be minted for collections.");

			// Check if a DOI already exists.
			if (itemData.getFirstElementByName("doi") != null)
			{
				throw new FedoraObjectException("Collection already has a Digital Object Identifier.");
			}
			
			// TODO Change the following code as required for checking if the item's published or not.
			//			List<DatastreamType> datastreams = FedoraBroker.getDatastreamList(pid);
			//			for (DatastreamType iDs : datastreams)
			//			{
			//				if (iDs.getDsid().equals(Constants.XML_PUBLISHED))
			//				{
			//					throw new FedoraObjectException("Item's already published.");
			//				}
			//			}
			
			org.datacite.schema.kernel_4.Resource doiResource = new DoiResourceAdapter(itemData).createDoiResource();
			DoiClient doiClient = new DoiClient();
			doiClient.mint(pid, doiResource);
			
			String mintedDoi = doiClient.getDoiResponse().getDoi();
			FedoraObject fedoraObject = getItemByPid(pid);
			Map<String, List<String>> form = new HashMap<String, List<String>>();
			form.put("doi", Arrays.asList(mintedDoi));
			saveEdit(fedoraObject, tmplt, form, rid);
		}
		catch (FedoraClientException e)
		{
			throw new FedoraObjectException(e.getMessage(), e);
		}
		catch (JAXBException e)
		{
			throw new FedoraObjectException(e.getMessage(), e);
		}
		catch (DoiException e)
		{
			throw new FedoraObjectException(e.getMessage(), e);
		}
		finally
		{
			IOUtils.closeQuietly(datastream);
		}
	}
	
	@Override
	public boolean isFilesPublic(String pid) {
		boolean isFilesPublic = false;
		FedoraObjectDAOImpl dao = new FedoraObjectDAOImpl();
		FedoraObject item = dao.getSingleByName(pid);
		Boolean filesPublicObj = item.isFilesPublic();
		if (filesPublicObj != null) {
			isFilesPublic = filesPublicObj.booleanValue();
		}
		return isFilesPublic;
	}
	
	@Override
	public void setFilesPublic(String pid, boolean isFilesPublic) {
		FedoraObjectDAOImpl dao = new FedoraObjectDAOImpl();
		FedoraObject item = dao.getSingleByName(pid);
		item.setFilesPublic(new Boolean(isFilesPublic));
		dao.update(item);
	}

	public FedoraObject getItemByPidReadAccess(String pid) {
		return getItemByPid(pid);
	}
	
	public FedoraObject getItemByPidWriteAccess(String pid) {
		return getItemByPid(pid);
	}
	
	@Override
	public List<FedoraObject> getAllPublishedAndPublic() {
		FedoraObjectDAOImpl dao = new FedoraObjectDAOImpl();
		return dao.getAllPublishedAndPublic();
	}
	
	@Override
	public Data getDataDifferences(au.edu.anu.datacommons.data.db.model.Template template, Data editData, Data publisData) {
		if (editData == null) {
			return null;
		}
		
		Data differenceData = new Data();
		
		List<TemplateAttribute> templateAttributes = template.getTemplateAttributes();
		for (TemplateAttribute attr : templateAttributes) {
			String fieldName = attr.getName();
			List<DataItem> editValues = editData.getElementByName(fieldName);
			List<DataItem> publishValues = publisData.getElementByName(fieldName);
			if (editValues != null && editValues.size() > 0) {
				if (!editValues.equals(publishValues)) {
					differenceData.getItems().addAll(editValues);
				}
			}
			else if (publishValues != null && publishValues.size() > 0) {
				//TODO something when a value has been deleted?
			}
		}
		
		return differenceData;
	}
}
