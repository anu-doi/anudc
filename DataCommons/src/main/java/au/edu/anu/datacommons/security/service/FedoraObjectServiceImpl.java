package au.edu.anu.datacommons.security.service;

import gov.loc.repository.bagit.BagFactory.LoadOption;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.WebApplicationException;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

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
import au.edu.anu.datacommons.exception.ValidationException;
import au.edu.anu.datacommons.properties.GlobalProps;
import au.edu.anu.datacommons.publish.FieldValidate;
import au.edu.anu.datacommons.publish.Publish;
import au.edu.anu.datacommons.publish.Validate;
import au.edu.anu.datacommons.search.ExternalPoster;
import au.edu.anu.datacommons.search.SparqlQuery;
import au.edu.anu.datacommons.search.SparqlResultSet;
import au.edu.anu.datacommons.security.CustomUser;
import au.edu.anu.datacommons.security.acl.CustomACLPermission;
import au.edu.anu.datacommons.util.Constants;
import au.edu.anu.datacommons.util.Util;
import au.edu.anu.datacommons.xml.sparql.Result;
import au.edu.anu.datacommons.xml.sparql.Sparql;
import au.edu.anu.datacommons.xml.template.Template;
import au.edu.anu.datacommons.xml.transform.JAXBTransform;
import au.edu.anu.datacommons.xml.transform.ViewTransform;
import au.edu.anu.dcbag.DcBag;

import com.sun.jersey.api.client.ClientResponse;
import com.yourmediashelf.fedora.client.FedoraClientException;
import com.yourmediashelf.fedora.generated.access.DatastreamType;

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
 * </pre>
 * 
 */
@Service("fedoraObjectServiceImpl")
public class FedoraObjectServiceImpl implements FedoraObjectService {
	static final Logger LOGGER = LoggerFactory.getLogger(FedoraObjectServiceImpl.class);

	@Resource(name="riSearchService")
	ExternalPoster riSearchService;
	
	@Resource(name="aclService")
	MutableAclService aclService;
	
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
	public FedoraObject getItemByName(String pid) {
		LOGGER.debug("Retrieving object for: {}", pid);
		String decodedpid = null;
		decodedpid = Util.decodeUrlEncoded(pid);
		if (decodedpid == null) {
			return null;
		}
		LOGGER.debug("Decoded pid: {}", decodedpid);
		FedoraObjectDAOImpl object = new FedoraObjectDAOImpl(FedoraObject.class);
		FedoraObject item = (FedoraObject) object.getSingleByName(decodedpid);
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
	public Map<String, Object> getViewPage(FedoraObject fedoraObject, String layout, String tmplt) {
		Map<String, Object> values = getPage(layout, tmplt, fedoraObject);
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
	 * </pre>
	 * 
	 * @param layout The layout that defines the flow of the items on the page
	 * @param tmplt The template that determines the fields on the screen
	 * @return Returns the viewable for the jsp file to pick up.
	 */
	public Map<String, Object> getNewPage(String layout, String tmplt) {
		Map<String, Object> values = getPage(layout, tmplt, null);
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
	 * </pre>
	 * 
	 * @param layout The layout to display the page
	 * @param tmplt The template that determines the fields on the screen
	 * @param form Contains the parameters from the request
	 * @return Returns the viewable for the jsp file to pick up.
	 */
	public FedoraObject saveNew(String layout, String tmplt, Map<String, List<String>> form) {
		FedoraObject fedoraObject = null;
		ViewTransform viewTransform = new ViewTransform();
		try {
			fedoraObject = viewTransform.saveData(tmplt, null, form);
			saveObjectPermissions(fedoraObject);
		}
		catch (JAXBException e) {
			LOGGER.error("Exception transforming jaxb", e);
		}
		catch (FedoraClientException e) {
			LOGGER.error("Exception creating/retrieving objects", e);
		}
		
		return fedoraObject;
	}
	
	/**
	 * saveObjectPermissions
	 *
	 * Updates/sets the permissions for the permissions for an object
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.9		20/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param fedoraObject
	 */
	public void saveObjectPermissions(FedoraObject fedoraObject) {
		LOGGER.info("Id: {}", fedoraObject.getId());
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		Sid sid = new PrincipalSid(authentication.getName());
		
		ObjectIdentity group_oi = new ObjectIdentityImpl(Groups.class, fedoraObject.getGroup_id());
		MutableAcl groupAcl = null;
		try {
			groupAcl = (MutableAcl) aclService.readAclById(group_oi);
		}
		catch (NotFoundException nfe) {
			groupAcl = aclService.createAcl(group_oi);
		}
		
		ObjectIdentity fedora_oi = new ObjectIdentityImpl(FedoraObject.class, fedoraObject.getId());
		MutableAcl fedoraAcl = null;
		try {
			fedoraAcl = (MutableAcl) aclService.readAclById(fedora_oi);
		}
		catch (NotFoundException nfe) {
			fedoraAcl = aclService.createAcl(fedora_oi);
		}
		
		fedoraAcl.setParent(groupAcl);
		fedoraAcl.setEntriesInheriting(Boolean.TRUE);
		fedoraAcl.setOwner(sid);
		
		aclService.updateAcl(fedoraAcl);
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
	public String getEditItem(FedoraObject fedoraObject, String layout, String tmplt, String fieldName) {
		LOGGER.info("In get edit item");
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
	 * </pre>
	 * 
	 * @param fedoraObject The  fedora object to get the page for
	 * @param layout The layout to use with display (i.e. the xsl stylesheet)
	 * @param tmplt The template that determines the fields on the screen
	 * @return Returns the viewable for the jsp file to pick up.
	 */
	public Map<String, Object> getEditPage(FedoraObject fedoraObject, String layout, String tmplt) {
		Map<String, Object> values = getPage(layout, tmplt, fedoraObject);
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
	 * </pre>
	 * 
	 * @param fedoraObject The  fedora object to get the page for
	 * @param tmplt The template that determines the fields on the screen
	 * @param form The form fields of the screen
	 * @return Returns the viewable for the jsp file to pick up.
	 */
	public Map<String, Object> saveEdit(FedoraObject fedoraObject, String layout, String tmplt, Map<String, List<String>> form) {
		Map<String, Object> values = new HashMap<String, Object>();
		ViewTransform viewTransform = new ViewTransform();
		try {
			fedoraObject = viewTransform.saveData(tmplt, fedoraObject, form);
			removeReviewReady(fedoraObject);
			removePublishReady(fedoraObject);
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
		String link = GlobalProps.getProperty(GlobalProps.PROP_FEDORA_RELATEDURI);
		FedoraReference reference = new FedoraReference();
		String referenceType = form.get("linkType").get(0);
		String referenceItem = form.get("itemId").get(0);
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
	 * 0.2		03/05/2012	Genevieve Turner (GT)	Updated to add related links to the page
	 * 0.8		20/06/2012	Genevieve Turner (GT)	Updated so that page retrieval is now using a map
	 * 0.10		11/07/2012	Genevieve Turner (GT)	Updated to allow or deny access to unpublished pages
	 * 0.11		13/07/2012	Rahul Khanna (RK)		Updated filelist displayed on collection page
	 * </pre>
	 * 
	 * @param layout The layout to use with display (i.e. the xsl stylesheet)
	 * @param tmplt The template that determines the fields on the screen
	 * @param fedoraObject The object of the page to retrieve
	 * @return Returns the viewable for the jsp file to pick up.
	 */
	private Map<String, Object> getPage(String layout, String template, FedoraObject fedoraObject) {
		boolean hasPermission = false;
		if (fedoraObject == null) {
			hasPermission = true;
		}else {
			hasPermission = checkViewPermission(fedoraObject);
		}
		Map<String, Object> values = new HashMap<String, Object>();
		values.put("topage", "/page.jsp");
		ViewTransform viewTransform = new ViewTransform();
		try {
			if (fedoraObject != null) {
				// Update this section if we want to have a full list of files
				List<DatastreamType> datastreamList = FedoraBroker.getDatastreamList(fedoraObject.getObject_id());
				for (DatastreamType dsType : datastreamList) {
					if (dsType.getDsid().contains("FILE0") )
					{
						DcBag dcBag = new DcBag(GlobalProps.getBagsDirAsFile(), fedoraObject.getObject_id(), LoadOption.BY_MANIFESTS);
						values.put("filelist", dcBag.getPayloadFileList());
						break;
					}
				}
			}
			if (hasPermission) {
				values.putAll(viewTransform.getPage(layout, template, fedoraObject, null, false, false));
			}
			else if (fedoraObject.getPublished()) {
				values.putAll(viewTransform.getPage(layout, template, fedoraObject, null, false, true));
			}
			else {
				throw new AccessDeniedException("User does not have permission to view page");
			}
		}
		catch (FedoraClientException e) {
			LOGGER.error("Exception: ", e);
			values.put("topage", "/error.jsp");
		}

		if (!values.containsKey("page")) {
			LOGGER.error("Page is empty");
			values.put("topage", "/error.jsp");
		}
		
		if (fedoraObject != null) {
			//TODO This is should probably be modified
			values.put("fedoraObject", fedoraObject);

			String sidepage = "buttons.jsp";
			values.put("sidepage", sidepage);
			
			SparqlResultSet resultSet = getLinks(fedoraObject);
			values.put("resultSet", resultSet);
		}
		
		return values;
	}
	
	/**
	 * checkViewPermission
	 *
	 * Checks if the user has a higher level of permissions (i.e. has an entry in the
	 * ACL tables for the given fedora object).
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.10		11/07/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param fedoraObject The object of the page to retrieve
	 * @return True if the user has some form of permissions, false otherwise
	 */
	private boolean checkViewPermission(FedoraObject fedoraObject) {
		boolean hasPermission = false;
		ObjectIdentity objectIdentity = new ObjectIdentityImpl(FedoraObject.class, fedoraObject.getId());
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		// Put the user into a sid list
		Sid sid = new PrincipalSid(authentication.getName());
		List<Sid> sidList = new ArrayList<Sid>();
		Iterator<GrantedAuthority> it = authentication.getAuthorities().iterator();
		while (it.hasNext()) {
			GrantedAuthority auth = it.next();
			Sid authSid = new GrantedAuthoritySid(auth.getAuthority());
			sidList.add(authSid);
		}
		
		// Get a list of implemented permissions
		List<Permission> permissions = new ArrayList<Permission>();
		permissions.add(CustomACLPermission.READ);
		permissions.add(CustomACLPermission.CREATE);
		permissions.add(CustomACLPermission.WRITE);
		permissions.add(CustomACLPermission.DELETE);
		permissions.add(CustomACLPermission.ADMINISTRATION);
		permissions.add(CustomACLPermission.REVIEW);
		permissions.add(CustomACLPermission.PUBLISH);
		
		sidList.add(sid);
		
		Acl acl = null;
		try {
			acl = aclService.readAclById(objectIdentity, sidList);
			
			hasPermission = acl.isGranted(permissions, sidList, false);
			
			LOGGER.info("After is granted");
		}
		catch (NotFoundException e) {
			LOGGER.info("User doesn't have permissions");
		}
		return hasPermission;
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
	 * </pre>
	 * 
	 * @param fedoraObject The object to retrieve the links for
	 * @return The results of the query
	 */
	private SparqlResultSet getLinks(FedoraObject fedoraObject) {
		SparqlResultSet resultSet = null;
		SparqlQuery sparqlQuery = new SparqlQuery();
		
		sparqlQuery.addVar("?item");
		sparqlQuery.addVar("?title");
		sparqlQuery.addVar("?predicate");
		sparqlQuery.addTriple("<info:fedora/" + fedoraObject.getObject_id() + ">", "?predicate", "?item", false);
		sparqlQuery.addTriple("?item", "<dc:title>", "?title", false);
		String filterString = "regex(str(?predicate), '" + GlobalProps.getProperty(GlobalProps.PROP_FEDORA_RELATEDURI) + "', 'i')";
		sparqlQuery.addFilter(filterString, "");
		
		ClientResponse respFromRiSearch = riSearchService.post("query", sparqlQuery.generateQuery());
		try {
			// For some reason XPath doesn't work properly if you directly get the document from the stream
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			Document resultsXmlDoc = factory.newDocumentBuilder().parse(new InputSource(new StringReader(respFromRiSearch.getEntity(String.class))));
			resultSet = new SparqlResultSet(resultsXmlDoc);
		}
		catch (SAXException e)
		{
			LOGGER.error("Error creating document", e);
		}
		catch (ParserConfigurationException e)
		{
			LOGGER.error("Error creating document", e);
		}
		catch (IOException e)
		{
			LOGGER.error("Error creating document", e);
		}
		return resultSet;
	}

	/**
	 * getPublishers
	 * 
	 * Returns a list of publishers
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		05/05/2012	Genevieve Turner (GT)	Initial
	 * 0.5		16/05/2012	Genevivee Turner (GT)	Updated to allow differing configurations for publishing
	 * </pre>
	 * 
	 * @return Returns a viewable of publishers
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
	 * 0.4		15/05/2012	Genevieve Turner (GT)	Initial
	 * 0.5		16/05/2012	Genevivee Turner (GT)	Updated to allow differing configurations for publishing
	 * 0.6		21/05/2012	Genevieve Turner (GT)	Added saving publication locations to database
	 * 0.7		08/06/2012	Genevieve Turner (GT)	Updated to use generic publishing options
	 * 0.9		20/06/2012	Genevieve Turner (GT)	Updated to add an audit row when an object is published
	 * 0.13		25/07/2012	Genevieve Turner (GT)	Added removing of ready for review/publish, and rejections
	 * </pre>
	 * 
	 * @param fedoraObject The item to publish
	 * @param publishers The list of publishers to publish to
	 * @return message to display on the screen
	 */
	public String publish(FedoraObject fedoraObject, List<String> publishers) {
		GenericDAOImpl<PublishLocation, Long> publishLocationDAO = new GenericDAOImpl<PublishLocation, Long>(PublishLocation.class);
		StringBuffer message = new StringBuffer();
		
		// Set the base publishing information
		generalPublish(fedoraObject.getObject_id());
		
		for (String publisher : publishers) {
			Long id = Long.parseLong(publisher);
			PublishLocation publishLocation = publishLocationDAO.getSingleById(id);
			LOGGER.debug("Publish class: {}", publishLocation.getExecute_class());
			try {
				Publish genericPublish = (Publish) Class.forName(publishLocation.getExecute_class()).newInstance();
				genericPublish.publish(fedoraObject.getObject_id(), publishLocation.getCode());
				if (!fedoraObject.getPublished()) {
					fedoraObject.setPublished(Boolean.TRUE);
				}
				message.append(publishLocation.getName());
				message.append("<br />");
				
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
		FedoraObjectDAOImpl object = new FedoraObjectDAOImpl(FedoraObject.class);
		object.update(fedoraObject);
		
		CustomUser customUser = (CustomUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		LOGGER.info("User id: {}",customUser.getId());
		
		AuditObject auditObject = new AuditObject();
		auditObject.setLog_date(new java.util.Date());
		auditObject.setLog_type("PUBLISH");
		auditObject.setObject_id(fedoraObject.getId());
		auditObject.setUser_id(customUser.getId());
		auditObject.setAfter(message.toString());
		GenericDAO<AuditObject,Long> auditObjectDAO = new GenericDAOImpl<AuditObject,Long>(AuditObject.class);
		auditObjectDAO.create(auditObject);
		
		removePublishReady(fedoraObject);
		removeReviewReady(fedoraObject);
		removeReviewReject(fedoraObject);
		
		return message.toString();
	}
	
	/**
	 * generalPublish
	 * 
	 * Sets information used by various publishing options.
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.7		08/06/2012	Genevieve Turner(GT)	Initial
	 * 0.11		17/07/2012	Genevieve Turner (GT)	Added validation prior to publishing
	 * 0.13		24/07/2012	Genevieve Turner (GT)	Moved the generating of a list of messages to a util function
	 * </pre>
	 * @param pid The pid to set the publishing information for
	 */
	private void generalPublish(String pid) {
		Validate validate = new FieldValidate();
		if (!validate.isValid(pid)) {
			StringBuffer errorMessages = new StringBuffer();
			errorMessages.append("Not all required fields have been filled out correctly\n");
			errorMessages.append(Util.listToStringWithNewline(validate.getErrorMessages()));
			throw new ValidationException(errorMessages.toString());
		}
		FedoraReference fedoraReference = new FedoraReference();
		fedoraReference.setPredicate_(GlobalProps.getProperty(GlobalProps.PROP_FEDORA_OAIPROVIDER_URL));
		fedoraReference.setObject_("oai:" + pid);
		fedoraReference.setIsLiteral_(Boolean.FALSE);
		
		String location = String.format("%s/objects/%s/datastreams/XML_SOURCE/content", GlobalProps.getProperty(GlobalProps.PROP_FEDORA_URI), pid);
		try {
			FedoraBroker.addDatastreamByReference(pid, Constants.XML_PUBLISHED, "M", "XML Published", location);
			FedoraBroker.addRelationship(pid, fedoraReference);
		}
		catch (FedoraClientException e) {
			LOGGER.info("Exception publishing to ANU: ", e);
		}
	}
	
	/**
	 * addPublishLocation
	 *
	 * Adds a publish location to the database
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.7		08/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
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
	 * getReadyForReview
	 * 
	 * Returns a list of items that are ready for a review
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.13		25/07/2012	Genevieve Turner(GT)	Initial
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
	 * 0.13		25/07/2012	Genevieve Turner(GT)	Initial
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
	 * 0.13		25/07/2012	Genevieve Turner(GT)	Initial
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
	 * 0.13		25/07/2012	Genevieve Turner(GT)	Initial
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
		
	}
	
	/**
	 * setReadyForPublish
	 * 
	 * Sets the item as ready for publish
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.13		25/07/2012	Genevieve Turner(GT)	Initial
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
		
	}
	
	/**
	 * setRejected
	 * 
	 * Rejects the item for publishing
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.13		25/07/2012	Genevieve Turner(GT)	Initial
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
	private void removeReviewReject(FedoraObject fedoraObject) {
		if (fedoraObject.getReviewReject() != null) {
			GenericDAO<ReviewReject, Long> rejectDAO = new GenericDAOImpl<ReviewReject, Long>(ReviewReject.class);
			rejectDAO.delete(fedoraObject.getId());
		}
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
}
