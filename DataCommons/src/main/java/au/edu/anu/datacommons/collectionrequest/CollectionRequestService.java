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

package au.edu.anu.datacommons.collectionrequest;

import static java.text.MessageFormat.format;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.sun.jersey.api.NotFoundException;
import com.sun.jersey.api.view.Viewable;

import au.edu.anu.datacommons.collectionrequest.CollectionRequestStatus.ReqStatus;
import au.edu.anu.datacommons.collectionrequest.PageMessages.MessageType;
import au.edu.anu.datacommons.data.db.dao.CollectionRequestDAO;
import au.edu.anu.datacommons.data.db.dao.CollectionRequestDAOImpl;
import au.edu.anu.datacommons.data.db.dao.DropboxDAO;
import au.edu.anu.datacommons.data.db.dao.DropboxDAOImpl;
import au.edu.anu.datacommons.data.db.dao.GenericDAO;
import au.edu.anu.datacommons.data.db.dao.GenericDAOImpl;
import au.edu.anu.datacommons.data.db.dao.QuestionDAO;
import au.edu.anu.datacommons.data.db.dao.QuestionDAOImpl;
import au.edu.anu.datacommons.data.db.dao.QuestionMapDAO;
import au.edu.anu.datacommons.data.db.dao.QuestionMapDAOImpl;
import au.edu.anu.datacommons.data.db.dao.UsersDAO;
import au.edu.anu.datacommons.data.db.dao.UsersDAOImpl;
import au.edu.anu.datacommons.data.db.model.Domains;
import au.edu.anu.datacommons.data.db.model.FedoraObject;
import au.edu.anu.datacommons.data.db.model.Groups;
import au.edu.anu.datacommons.data.db.model.Users;
import au.edu.anu.datacommons.data.solr.SolrManager;
import au.edu.anu.datacommons.data.solr.SolrUtils;
import au.edu.anu.datacommons.security.CustomUser;
import au.edu.anu.datacommons.security.acl.CustomACLPermission;
import au.edu.anu.datacommons.security.acl.PermissionService;
import au.edu.anu.datacommons.security.service.FedoraObjectService;
import au.edu.anu.datacommons.security.service.GroupService;
import au.edu.anu.datacommons.storage.DcStorage;
import au.edu.anu.datacommons.storage.controller.StorageController;
import au.edu.anu.datacommons.storage.info.FileInfo;
import au.edu.anu.datacommons.storage.provider.StorageException;
import au.edu.anu.datacommons.util.Util;

/**
 * CollectionRequestService
 * 
 * Australian National University Data Commons
 * 
 * This method serves REST requests related to Collection Requests. The following broad workflows are supported:
 * 
 * <ol>
 * <li>Submitting a Collection Request that includes the specific items (datastreams) requested and the answers to
 * questions assigned to a collection (pid).</li>
 * <li>Changing the status of a Collection Request providing a reason for the change.</li>
 * <li>When a Collection Request is approved, providing access to the created dropbox.</li>
 * <li>Allow for questions to be added to the Question Bank.</li>
 * <li>Allow questions to be assigned to Fedora Objects so when a request is submitted, those questions must be
 * answered.
 * </ol>
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		1/05/2012	Rahul Khanna (RK)		Initial
 * 0.2		25/06/2012	Genevieve Turner (GT)	Updated to filter out requests to either the logged in user or those they have review access to
 * 0.3		29/06/2012	Genevieve Turner (GT)	Updated to use DAO and for filtering out records with permissions
 * 0.4		08/04/2012	Genevieve Turner (GT)	Updated to allow for required and optional questions
 * </pre>
 * 
 */
@Component
@Scope("request")
@Path("/collreq")
public class CollectionRequestService {
	private static final Logger LOGGER = LoggerFactory.getLogger(CollectionRequestService.class);

	private static final String COLL_REQ_JSP = "/collreq.jsp";
	private static final String DROPBOX_JSP = "/dropbox.jsp";
	private static final String QUESTION_JSP = "/question.jsp";
	private static final String DROPBOX_ACCESS_JSP = "/dropboxaccess.jsp";

	@Resource(name = "dcStorage")
	private DcStorage dcStorage;

	@Resource(name = "mailSender")
	JavaMailSenderImpl mailSender;

	@Resource(name = "groupServiceImpl")
	GroupService groupService;

	@Resource(name = "fedoraObjectServiceImpl")
	FedoraObjectService fedoraObjectService;

	@Resource(name = "permissionService")
	private PermissionService permissionService;
	
	@Autowired
	protected StorageController storageController;

	/**
	 * doGetAsHtml
	 * 
	 * Australian National University Data Commons
	 * 
	 * Gets the Collection Request page.
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		1/05/2012	Rahul Khanna (RK)		Initial
	 * 0.2		25/06/2012	Genevieve Turner (GT)	Updated to filter out requests to either the logged in user or those they have review access to
	 * 0.3		27/06/2012	Genevieve Turner (GT)	Updated to use DAO pattern and limit those authorised
	 * </pre>
	 * 
	 * @return Collection Request page as HTML.
	 */
	@GET
	@PreAuthorize("hasRole('ROLE_REGISTERED')")
	@Produces(MediaType.TEXT_HTML)
	public Response doGetAsHtml() {
		Response resp = null;
		Map<String, Object> model = new HashMap<String, Object>();

		CustomUser customUser = (CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Long id = customUser.getId();

		List<Groups> reviewGroups = groupService.getReviewGroups();

		CollectionRequestDAO collectionRequestDAO = new CollectionRequestDAOImpl();

		List<CollectionRequest> collReqs = collectionRequestDAO.getPermittedRequests(id, reviewGroups);
		LOGGER.info("Number of collection requests: {}", collReqs.size());
		model.put("collReqs", collReqs);
		resp = Response.ok(new Viewable(COLL_REQ_JSP, model)).build();
		return resp;
	}

	/**
	 * doGetReqItemAsHtml
	 * 
	 * Australian National University Data Commons
	 * 
	 * Gets the specific Collection Request with the provided ID.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		1/05/2012	Rahul Khanna (RK)	Initial
	 * 0.2		28/06/2012	Genevieve Turner (GT)	Updated to use DAO pattern and limit those authorised
	 * </pre>
	 * 
	 * @param collReqId
	 *            ID of the Collection request as Long.
	 * @return Collection Request page as HTML.
	 */
	@GET
	@Produces(MediaType.TEXT_HTML)
	@PreAuthorize("hasRole('ROLE_REGISTERED')")
	@Path("{collReqId}")
	public Response doGetReqItemAsHtml(@PathParam("collReqId") Long collReqId) {
		PageMessages messages = new PageMessages();
		Map<String, Object> model = new HashMap<String, Object>();
		Response resp = null;

		LOGGER.trace("In method doGetReqItemAsHtml. Param collReqId={}.", collReqId);
		LOGGER.debug("Retrieving Collection Request with ID: {}...", collReqId);

		try {
			LOGGER.debug("Retrieving Collection Request with ID: {}...", collReqId);

			// Find the Collection Request with the specified ID.
			CollectionRequestDAO collectionRequestDAO = new CollectionRequestDAOImpl();
			CollectionRequest collReq = collectionRequestDAO.getSingleByIdEager(collReqId);

			// Check if the Collection Request actually exists. If not, throw Exception.
			if (collReq == null)
				throw new Exception("Invalid Collection Request ID or no Collection Request with that ID exists.");

			LOGGER.debug("Found Collection Request ID {}, for Pid {}.", collReq.getId(), collReq.getPid());
			model.put("collReq", collReq);

			// Add files in payload to model.
			FedoraObject fo = fedoraObjectService.getItemByPid(collReq.getPid());
			if (permissionService.checkPermission(fo, CustomACLPermission.PUBLISH)
					|| permissionService.checkPermission(fo, CustomACLPermission.REVIEW)) {
				FileInfo downloadables = storageController.getFileInfo(collReq.getPid(), "");
				model.put("downloadables", downloadables);
			}
		} catch (Exception e) {
			LOGGER.error("Unable to find or retrieve Collection Request " + collReqId, e);
			messages.clear();
			messages.add(MessageType.ERROR, e.getMessage(), model);
		} finally {
			resp = Response.ok(new Viewable(COLL_REQ_JSP, model)).build();
		}

		return resp;
	}

	/**
	 * doPostCollReqAsHtml
	 * 
	 * Australian National University Data Commons
	 * 
	 * Receives a POST request with the details of a new Collection Request to be created and creates a new Collection
	 * Request.
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		1/05/2012	Rahul Khanna (RK)		Initial
	 * 0.2		27/06/2012	Genevieve Turner (GT)	Updated to associated fedoraObject
	 * 0.3		27/06/2012	Genevieve Turner (GT)	Updated to use DAO pattern and limit those authorised
	 * 0.4		08/04/2012	Genevieve Turner (GT)	Updated to allow for required and optional questions
	 * </pre>
	 * 
	 * @param pid
	 *            Pid for which the request belongs to.
	 * @param requestedFileSet
	 *            Datastream IDs as a set being requested.
	 * @param allFormParams
	 *            Map of all form parameters from which questions that have been answered are extracted.
	 * @return Response as HTML
	 */
	@POST
	@Produces(MediaType.TEXT_HTML)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@PreAuthorize("hasRole('ROLE_REGISTERED')")
	public Response doPostCollReqAsHtml(@Context HttpServletRequest request, @Context UriInfo uriInfo,
			@FormParam("pid") String pid, @FormParam("file") Set<String> requestedFileSet,
			MultivaluedMap<String, String> allFormParams) {
		PageMessages messages = new PageMessages();
		Map<String, Object> model = new HashMap<String, Object>();
		Response resp = null;
		UriBuilder uriBuilder = null;

		LOGGER.trace("In method doPostCollReqAsHtml. Param pid={}, dsIdSet={}, allFormParams={}.", new Object[] { pid,
				requestedFileSet, allFormParams });

		// Save the Collection Request for further processing.
		try {
			Users user = new UsersDAOImpl().getUserByName(SecurityContextHolder.getContext().getAuthentication()
					.getName());
			FedoraObject fedoraObject = fedoraObjectService.getItemByPid(pid);
			CollectionRequest newCollReq = new CollectionRequest(pid, user, request.getRemoteAddr(), fedoraObject);

			// Get a list of questions assigned to the Pid.
			QuestionDAO questionDAO = new QuestionDAOImpl();
			List<Question> reqQuestionList = questionDAO.getQuestionsByPid(pid, true);

			// Iterate through the questions that need to be answered for the pid, get the answers for those questions
			// and add to CR.
			// If an answer for a question doesn't exist throw exception.
			for (Question iQuestion : reqQuestionList) {
				// Check if the answer to the current question is provided. If yes, save the answer, else throw
				// exception.
				if (allFormParams.containsKey("q" + iQuestion.getId())
						&& Util.isNotEmpty(allFormParams.getFirst("q" + iQuestion.getId()))) {
					CollectionRequestAnswer ans = new CollectionRequestAnswer(iQuestion, allFormParams.getFirst("q"
							+ iQuestion.getId()));
					newCollReq.addAnswer(ans);
				} else {
					throw new Exception("All questions must be answered. The question '" + iQuestion.getQuestionText()
							+ "' has been left blank.");
				}
			}

			// Iterate through the optional questions for the pid, and add the answers for those questions to the
			// Collection Request
			List<Question> optQuestionList = questionDAO.getQuestionsByPid(pid, false);
			for (Question iQuestion : optQuestionList) {
				if (allFormParams.containsKey("q" + iQuestion.getId())
						&& Util.isNotEmpty(allFormParams.getFirst("q" + iQuestion.getId()))) {
					CollectionRequestAnswer ans = new CollectionRequestAnswer(iQuestion, allFormParams.getFirst("q"
							+ iQuestion.getId()));
					newCollReq.addAnswer(ans);
				}
			}

			LOGGER.debug("All mandatory questions answered for this Pid.");

			// Save the newly created CR and add success message to message set.
			CollectionRequestDAO requestDAO = new CollectionRequestDAOImpl();
			requestDAO.create(newCollReq);

			uriBuilder = UriBuilder.fromPath("/collreq/").path(newCollReq.getId().toString());

			// Send email to contacts of the collection.
			Map<String, String> varMap = new HashMap<String, String>();
			varMap.put("pid", pid);
			varMap.put("collReqUrl",
					uriInfo.getBaseUriBuilder().path(this.getClass()).path(this.getClass(), "doGetReqItemAsHtml")
							.build(newCollReq.getId()).toString());

			List<String> contactEmailList = getEmails(pid);
			Email email = new Email(mailSender);
			for (String recipientEmail : contactEmailList)
				email.addRecipient(recipientEmail);
			email.setSubject("Collection data requested");
			email.setBody("mailtmpl/collreqsubmitted.txt", varMap);
			email.send();

			messages.add(MessageType.SUCCESS, "Collection Request successfully saved. ID# " + newCollReq.getId(), model);
			model.put("collReq", newCollReq);
			uriBuilder = uriBuilder
					.queryParam("smsg", "Collection Request saved. ID# " + newCollReq.getId().toString());
		} catch (Exception e) {
			LOGGER.error("Unable to create new Collection Request.", e);
			uriBuilder = UriBuilder.fromPath("/collreq/").queryParam("pid", pid).queryParam("emsg", e.getMessage());
		} finally {
			resp = Response.seeOther(uriBuilder.build()).build();
		}

		return resp;
	}

	/**
	 * doPostUpdateCollReqAsHtml
	 * 
	 * Australian National University Data Commons
	 * 
	 * This method accepts changes to be made to a Collection Request.
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		2/05/2012	Rahul Khanna (RK)		Initial
	 * 0.3		27/06/2012	Genevieve Turner (GT)	Updated to use DAO pattern and limit those authorised
	 * </pre>
	 * 
	 * @param collReqId
	 *            The collection ID to be updated.
	 * @param status
	 *            The status of the collection request. E.g. active, rejected etc.
	 * @param reason
	 *            The reason for the status change.
	 * @return Response as HTML.
	 */
	@POST
	@Path("{collReqId}")
	@Produces(MediaType.TEXT_HTML)
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	public Response doPostUpdateCollReqAsHtml(@PathParam("collReqId") Long collReqId, @Context UriInfo uriInfo,
			@FormParam("status") ReqStatus status, @FormParam("reason") String reason,
			@FormParam("file") Set<String> fileSet) {
		Response resp = null;
		CollectionRequest collReq = null;
		UriBuilder redirUri = UriBuilder.fromPath("/collreq/").path(collReqId.toString());

		LOGGER.trace("In doPostUpdateCollReqAsHtml. Params collReqId={}, status={}, reason={}", new Object[] {
				collReqId, status, reason });

		try {
			LOGGER.debug("Saving Collection Request ID {} with updated details...", collReqId);

			// Get the CR with the provided ID.
			CollectionRequestDAO collectionRequestDAO = new CollectionRequestDAOImpl();
			collReq = collectionRequestDAO.getSingleByIdEager(collReqId);

			// Check if the CR exists.
			if (collReq == null)
				throw new Exception("Invalid Collection Request ID or Collection Request could not be retrieved.");

			// Check if a reason is provided. If not, add error to message set.
			if (!Util.isNotEmpty(reason))
				throw new Exception("A reason must be provided for a status change.");

			// If the CR's current status is approved or rejected, the status cannot change anymore.
			ReqStatus curStatus = collReq.getLastStatus().getStatus();
			if (curStatus == ReqStatus.ACCEPTED || curStatus == ReqStatus.REJECTED)
				throw new Exception(
						"Cannot change status of a CR with an Approved or Rejected status. A new CR must be submitted by the requestor for processing.");

			// Add a status row to the status history for that CR.
			Users user = new UsersDAOImpl().getUserByName(SecurityContextHolder.getContext().getAuthentication()
					.getName());
			CollectionRequestStatus newStatus = new CollectionRequestStatus(collReq, status, reason, user);
			collReq.addStatus(newStatus);
			collReq = collectionRequestDAO.update(collReq);

			// Update the items requested, if required.
			LOGGER.debug("{} items checked.", fileSet.size());
			// Add each of the items requested (datastreams) to the CR.
			Set<CollectionRequestItem> curItems = collReq.getItems();

			// Sync the list of items in the POST with the ones already stored.
			for (String iFile : fileSet) {
				boolean isPresent = false;
				for (CollectionRequestItem iCurItem : curItems) {
					if (iCurItem.getItem().equals(iFile)) {
						isPresent = true;
						break;
					}
				}

				if (!isPresent) {
					CollectionRequestItem newItem = new CollectionRequestItem(iFile);
					collReq.addItem(newItem);
				}
			}

			GenericDAO<CollectionRequestItem, Long> collReqDao = new GenericDAOImpl<CollectionRequestItem, Long>(
					CollectionRequestItem.class);
			Iterator<CollectionRequestItem> iter = curItems.iterator();
			while (iter.hasNext()) {
				CollectionRequestItem iItem = iter.next();
				if (!fileSet.contains(iItem.getItem())) {
					iter.remove();
					collReqDao.delete(iItem.getId());
				}
			}

			// Save the updated collection request in the DB.
			collReq = collectionRequestDAO.update(collReq);

			LOGGER.debug("Updated details of CR ID# {}.", collReq.getId());
			redirUri.queryParam("smsg", "Successfully added status to Status History");

			// Generate the dropbox access URI.
			URI dropboxUri = null;
			if (status == ReqStatus.ACCEPTED && collReq.getItems().size() > 0) {
				CollectionDropbox dBox = new CollectionDropbox(collReq, user, true);
				collReq.setDropbox(dBox);
				collReq = collectionRequestDAO.update(collReq);

				dropboxUri = UriBuilder.fromUri(uriInfo.getBaseUri()).path(CollectionRequestService.class)
						.path(CollectionRequestService.class, "doGetDropboxAccessAsHtml")
						.queryParam("p", collReq.getDropbox().getAccessPassword())
						.build(collReq.getDropbox().getAccessCode());

				redirUri.queryParam(
						"imsg",
						format("Dropbox created<br /><strong>Code: </strong>{0}<br /><strong>Password: </strong>{1}",
								collReq.getDropbox().getAccessCode().toString(), collReq.getDropbox()
										.getAccessPassword()));
				redirUri.queryParam("imsg",
						format("Dropbox Access Link: <a href=''{0}''>Dropbox Access</a>", dropboxUri.toString()));
			}

			// Send out an email to the requestor. If failed, add status message advising that the requestor should be
			// contacted directly.
			try {
				HashMap<String, String> varMap = new HashMap<String, String>();
				varMap.put("requestorGivenName", collReq.getRequestor().getGivenName());
				varMap.put("collReqId", collReq.getId().toString());
				varMap.put("changedByDispName", collReq.getLastStatus().getUser().getDisplayName());
				varMap.put("dateChanged", collReq.getLastStatus().getTimestamp().toString());
				varMap.put("status", collReq.getLastStatus().getStatus().toString());
				varMap.put("reason", collReq.getLastStatus().getReason());

				Email email = new Email(mailSender);
				email.addRecipient(collReq.getRequestor().getEmail(), collReq.getRequestor().getDisplayName());
				email.setSubject("Collection Request# " + collReq.getId() + " Status Changed");

				// Add a message about the dropbox being created if the status is now Accepted.
				if (status == ReqStatus.ACCEPTED && collReq.getItems().size() > 0) {
					varMap.put("dropboxLink", dropboxUri.toString());
					email.setBody("mailtmpl/collreqaccepted.txt", varMap);
				} else {
					email.setBody("mailtmpl/collreqchanged.txt", varMap);
				}
				email.send();
			} catch (Exception e) {
				redirUri.queryParam("emsg",
						"Could not email the requestor. Please contact him/her with the dropbox access link.");
			}
		} catch (Exception e) {
			LOGGER.error("Unable to add request row.", e);
			redirUri.queryParam("emsg", e.getMessage());
		}

		resp = Response.seeOther(redirUri.build()).build();
		return resp;
	}

	/**
	 * doGetDropboxesAsHtml
	 * 
	 * Australian National University Data Commons
	 * 
	 * This method returns a list of dropboxes and their details. This method enables administration of dropboxes only.
	 * Access to the files within the dropbox is from doGetDropboxAccessAsHtml method.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		2/05/2012	Rahul Khanna (RK)	Initial
	 * 0.3		27/06/2012	Genevieve Turner (GT)	Updated to use DAO pattern and limit those authorised
	 * </pre>
	 * 
	 * @return Response as HTML.
	 */
	@GET
	@Path("dropbox")
	@Produces(MediaType.TEXT_HTML)
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	public Response doGetDropboxesAsHtml() {
		PageMessages messages = new PageMessages();
		Map<String, Object> model = new HashMap<String, Object>();
		Response resp = null;

		LOGGER.trace("In doGetDropboxesAsHtml.");

		try {
			List<CollectionDropbox> dropboxes = null;
			String username = SecurityContextHolder.getContext().getAuthentication().getName();
			UsersDAO userDAO = new UsersDAOImpl();
			Users user = userDAO.getUserByName(username);

			DropboxDAO dropboxDAO = new DropboxDAOImpl();
			dropboxes = dropboxDAO.getUserDropboxes(user);
			model.put("dropboxes", dropboxes);

			if (dropboxes.size() == 0)
				messages.add(MessageType.WARNING, "No dropboxes found.", model);
			model.put("dropboxes", dropboxes);
		} catch (Exception e) {
			LOGGER.error("Unable to get list of Collection Dropboxes.", e);
			messages.clear();
			messages.add(MessageType.ERROR, e.getMessage(), model);
		} finally {
			resp = Response.ok(new Viewable(DROPBOX_JSP, model)).build();
		}

		return resp;
	}

	/**
	 * doGetDropboxAsHtml
	 * 
	 * Australian National University Data Commons
	 * 
	 * This method Displays the details of a dropbox. It does not allow access to the files that have been requested in
	 * the dropbox.
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		2/05/2012	Rahul Khanna (RK)		Initial
	 * 0.3		27/06/2012	Genevieve Turner (GT)	Updated to use DAO pattern and limit those authorised
	 * </pre>
	 * 
	 * @param dropboxId
	 *            The ID of the dropbox.
	 * 
	 * @return Response as HTML.
	 */
	@GET
	@Path("dropbox/{dropboxId}")
	@Produces(MediaType.TEXT_HTML)
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	public Response doGetDropboxAsHtml(@PathParam("dropboxId") long dropboxId) {
		PageMessages messages = new PageMessages();
		Map<String, Object> model = new HashMap<String, Object>();
		Response resp = null;

		LOGGER.trace("In doGetDropboxAsHtml. Params dropboxId: {}", dropboxId);

		try {
			// Find the dropbox with the specified ID.
			DropboxDAO dropboxDAO = new DropboxDAOImpl();
			CollectionDropbox dropbox = dropboxDAO.getSingleById(dropboxId);

			// Check if a valid dropbox exists and was retrieved.
			if (dropbox == null)
				throw new Exception("Invalid Dropbox ID or a dropbox with ID " + dropboxId + "doesn't exist.");

			model.put("dropbox", dropbox);
		} catch (Exception e) {
			LOGGER.error("Unable to get list of Collection Dropboxes.", e);
			messages.clear();
			messages.add(MessageType.ERROR, e.getMessage(), model);
		} finally {
			resp = Response.ok(new Viewable(DROPBOX_JSP, model)).build();
		}

		return resp;
	}

	/**
	 * doPostUpdateDropboxAsHtml
	 * 
	 * Australian National University Data Commons
	 * 
	 * This method accepts POST requests to update the details of a dropbox.
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		15/05/2012	Rahul Khanna (RK)		Initial
	 * 0.3		29/06/2012	Genevieve Turner (GT)	Limit those authorised
	 * </pre>
	 * 
	 * @param dropboxId
	 *            ID of the dropbox to be updated.
	 * @return Response as HTML.
	 */
	@POST
	@Path("dropbox/{dropboxId}")
	@Produces(MediaType.TEXT_HTML)
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	public Response doPostUpdateDropboxAsHtml(@PathParam("dropboxId") long dropboxId) {
		PageMessages messages = new PageMessages();
		Map<String, Object> model = new HashMap<String, Object>();
		Response resp = null;

		// TODO Process updates to dropbox. Change notifyOnPickup and/or active status.

		return resp;
	}

	/**
	 * doGetDropboxAccessAsHtml
	 * 
	 * Australian National University Data Commons
	 * 
	 * This method allows access to the files in a dropbox.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		2/05/2012	Rahul Khanna (RK)	Initial
	 * 0.3		27/06/2012	Genevieve Turner (GT)	Updated to use DAO pattern and limit those authorised
	 * </pre>
	 * 
	 * @param dropboxAccessCode
	 *            Access code of a dropbox.
	 * @param password
	 *            Password to access the dropbox.
	 * @return Response as HTML.
	 */
	@GET
	@Path("dropbox/access/{dropboxAccessCode}")
	@Produces(MediaType.TEXT_HTML)
	@PreAuthorize("hasRole('ROLE_REGISTERED')")
	public Response doGetDropboxAccessAsHtml(@Context HttpServletRequest request, @Context UriInfo uriInfo,
			@PathParam("dropboxAccessCode") Long dropboxAccessCode, @QueryParam("p") String password) {
		PageMessages messages = new PageMessages();
		Map<String, Object> model = new HashMap<String, Object>();
		Response resp = null;

		LOGGER.trace("In doGetDropboxAccessAsHtml. Params dropboxAccessCode={}, password={}.", dropboxAccessCode,
				password);

		try {
			LOGGER.debug("Finding dropbox with access code {}...", dropboxAccessCode);
			DropboxDAO dropboxDAO = new DropboxDAOImpl();
			CollectionDropbox dropbox = dropboxDAO.getSingleByAccessCode(dropboxAccessCode);

			if (dropbox == null)
				throw new NotFoundException(format("Dropbox with Access Code {0} doesn't exist.",
						dropboxAccessCode.toString()));

			LOGGER.debug("Dropbox found.");
			model.put("dropbox", dropbox);

			Users requestor = dropbox.getCollectionRequest().getRequestor();
			String username = SecurityContextHolder.getContext().getAuthentication().getName();
			if (!requestor.getUsername().equals(username))
				throw new Exception("You are not authorised to view this dropbox.");

			if (new Date().after(dropbox.getExpiry())) // Check if today's date and time is after dropbox expiry.
				throw new Exception("This Dropbox has expired. Please submit a new Collection Request.");

			if (!dropbox.isActive()) // Check if dropbox active.
				throw new Exception("This Dropbox has been marked as inactive.");

			if (!Util.isNotEmpty(password)) // Check if password provided.
				throw new IllegalArgumentException("Please enter password for this Dropbox.");

			if (!password.equals(dropbox.getAccessPassword())) // Check if password correct.
				throw new Exception("Incorrect password entered.");

			// Create HashMap downloadables with a link for each item to be downloaded.
			HashMap<String, String> downloadables = new HashMap<String, String>();
			UriBuilder uriBuilder = uriInfo.getBaseUriBuilder().path(CollectionRequestService.class)
					.path(CollectionRequestService.class, "doGetDropboxFileAsOctetStream")
					.queryParam("dropboxAccessCode", dropboxAccessCode).queryParam("p", password);
			for (CollectionRequestItem reqItem : dropbox.getCollectionRequest().getItems()) {
				URI fileDlUri = uriBuilder.build(dropbox.getCollectionRequest().getPid(), reqItem.getItem());
				LOGGER.debug("Adding URI {} to downloadables", fileDlUri.toString());
				downloadables.put(reqItem.getItem(), fileDlUri.toString());
			}
			model.put("downloadables", downloadables);
			model.put("downloadAsZipUrl", uriBuilder.build(dropbox.getCollectionRequest().getPid(), "zip"));

			// External references list.

			Collection<String> extRefs = storageController.getRecordDataSummary(dropbox.getCollectionRequest().getPid())
					.getExtRefs();
			if (extRefs != null && extRefs.size() > 0) {
				model.put("fetchables", extRefs);
			}

			// Make a log of access
			CollectionDropboxAccessLog log = new CollectionDropboxAccessLog(dropbox, request.getRemoteAddr());
			dropbox.addAccessLogEntry(log);
			dropboxDAO.update(dropbox);
		} catch (Exception e) {
			LOGGER.error("Unable to get Dropbox for access.", e);
			messages.clear();
			if (e.getClass() == IllegalArgumentException.class)
				messages.add(MessageType.INFO, e.getMessage(), model);
			else
				messages.add(MessageType.ERROR, e.getMessage(), model);
		} finally {
			resp = Response.ok(new Viewable(DROPBOX_ACCESS_JSP, model)).build();
		}

		return resp;
	}
	
	/**
	 * Returns the contents of a file of a group of files combined into a ZipStream as InputStream in the Response
	 * object. The user gets a request to open or save the requested file. This method checks that the user requesting
	 * the file has a valid collection request.
	 * 
	 * @param pid
	 *            Pid of collection whose files
	 * @param filename
	 *            filename of the file being requested. E.g. "data/file.txt"
	 * @param dropboxAccessCode
	 *            Access Code of the dropbox that the requestor's been given access to
	 * @param password
	 *            Password of dropbox
	 * @return Response containing octet_stream of file or files as zipfile.
	 */
	@GET
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@Path("dropbox/access/{dropboxAccessCode}/{fileInBag:.*}")
	public Response doGetDropboxFileAsOctetStream(@PathParam("fileInBag") String filename,
			@QueryParam("dropboxAccessCode") Long dropboxAccessCode, @QueryParam("p") String password) {
		Response resp = null;

		// Get dropbox requesting file.
		DropboxDAO dropboxDAO = new DropboxDAOImpl();
		CollectionDropbox dropbox = dropboxDAO.getSingleByAccessCode(dropboxAccessCode);
		Users requestor = dropbox.getCollectionRequest().getRequestor();
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		String pid = dropbox.getCollectionRequest().getPid();
		LOGGER.trace("User {} requested dropbox for pid: {}, filename: {}", username, pid, filename);

		// If dropbox is valid and the requestor of the collection request is the one accessing it, return file as octet
		// stream.
		try {
			if (dropbox.isValid(password) && requestor.getUsername().equals(username)) {
				LOGGER.info("Dropbox details valid. ID: {}, Access Code: {}. Returning file requested.", dropbox
						.getId().toString(), dropbox.getAccessCode().toString());
				Set<CollectionRequestItem> items = dropbox.getCollectionRequest().getItems();
				if (filename.equalsIgnoreCase("zip")) {
					Set<String> fileSet = new HashSet<String>();
					for (CollectionRequestItem item : items) {
						fileSet.add(item.getItem());
					}
					resp = getBagFilesAsZip(pid, fileSet, DcStorage.convertToDiskSafe(pid) + ".zip");
				} else {
					boolean isAllowedItem = false;
					for (CollectionRequestItem item : items) {
						if (item.getItem().equals(filename)) {
							isAllowedItem = true;
							break;
						}
					}
					if (isAllowedItem) {
						// addAccessLog(Operation.READ);
						resp = getBagFileOctetStreamResp(pid, filename);
					} else {
						resp = Response.status(Status.FORBIDDEN).build();
					}
				}
			} else {
				LOGGER.warn("Unauthorised access to Dropbox ID: {}, Access Code: {}. Returning HTTP 403 Forbidden.",
						dropbox.getId().toString(), dropbox.getAccessCode().toString());
				resp = Response.status(Status.FORBIDDEN).build();
			}

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			resp = Response.serverError().entity(e.getMessage()).build();
		}
		return resp;
	}
	
	/**
	 * doGetQuestionsAsHtml
	 * 
	 * Australian National University Data Commons
	 * 
	 * Returns a Viewable with all questions in the question bank in its model.
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		2/05/2012	Rahul Khanna (RK)		Initial
	 * 0.3		29/06/2012	Genevieve Turner (GT)	 Limit those authorised
	 * </pre>
	 * 
	 * @return Response as HTML.
	 */
	@GET
	@Path("question")
	@Produces(MediaType.TEXT_HTML)
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	public Response doGetQuestionsAsHtml() {
		PageMessages messages = new PageMessages();
		Map<String, Object> model = new HashMap<String, Object>();
		Response resp = null;

		LOGGER.info("User {} requested the questions page.", getCurUsername());

		try {
			// Get all questions from the Question Bank.
			List<Question> questions = getAllQuestions();
			LOGGER.debug("Retrieved {} questions from question bank.", questions.size());

			// Add a warning to the message set to let the user know that there aren't any questions in the question
			// bank.
			if (questions.size() == 0) {
				messages.add(MessageType.WARNING, "No questions found in the question bank.", model);
			}

			model.put("questions", questions);

			List<Groups> groups = groupService.getReviewGroups();
			model.put("groups", groups);
		} catch (Exception e) {
			LOGGER.error("Unable to retrieve questions from question bank", e);
			messages.clear();
			messages.add(MessageType.ERROR, e.getMessage(), model);
		} finally {
			resp = Response.ok(new Viewable(QUESTION_JSP, model)).build();
		}

		return resp;
	}

	/**
	 * doPostQuestionAsHtml
	 * 
	 * Australian National University Data Commons
	 * 
	 * Accepts POST requests to add new questions in the question bank as well as requests to assign a question to a
	 * Pid.
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		2/05/2012	Rahul Khanna (RK)		Initial
	 * 0.2		29/06/2012	Genevieve Turner (GT)	Updated to use the DAO pattern and limit those authorised
	 * 0.4		04/04/2012	Genevieve Turner (GT)	Updated to allow for questions against groups and domains
	 * </pre>
	 * 
	 * @param submit
	 *            The task to be performed. Value comes from the submit button in a form. "Add Question" to add a
	 *            question to the question bank, or "Save" to update the list of questions against a Pid.
	 * @param questionText
	 *            Question as String
	 * @param pid
	 *            Pid as String
	 * @param qIdSet
	 *            Questions to be assigned to Pid as Set.
	 * @return Response as HTML.
	 */
	@POST
	@Path("question")
	@Produces(MediaType.TEXT_HTML)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	public Response doPostQuestionAsHtml(@Context UriInfo uriInfo, @FormParam("submit") String submit,
			@FormParam("q") String questionText, @FormParam("pid") String pid, @FormParam("qid") Set<Long> qIdSet,
			@FormParam("group") Long groupId, @FormParam("domain") Long domainId,
			@FormParam("idPidQ") Set<Long> requiredQuestions, @FormParam("optQid") Set<Long> optQidSet) {
		LOGGER.debug("Number of req q's: {}, Number of opt q's: {}", qIdSet.size(), optQidSet.size());
		Response resp = null;
		UriBuilder uriBuilder = uriInfo.getBaseUriBuilder().path(CollectionRequestService.class)
				.path(CollectionRequestService.class, "doGetQuestionsAsHtml");

		LOGGER.trace("In doPostQuestionAsHtml. Params submit={}, questionStr={}, pid={}, qid={}.", new Object[] {
				submit, questionText, pid, qIdSet });

		if (Util.isNotEmpty(pid))
			uriBuilder = uriBuilder.queryParam("pid", pid);

		if (Util.isNotEmpty(submit)) {
			// Adding a question to the question bank.
			if (submit.equals("Add Question")) {
				try {
					// Validate question text.
					questionText = questionText.trim();
					if (questionText.equals(""))
						throw new Exception("Question text not provided. A Question cannot be blank.");

					LOGGER.debug("Saving question in question bank...", pid);
					// Create Question object and persist it.
					Question question = new Question(questionText);
					QuestionDAO questionDAO = new QuestionDAOImpl();
					questionDAO.create(question);
					uriBuilder = uriBuilder.queryParam("smsg", "The question <em>" + question.getQuestionText()
							+ "</em> saved in the Question Bank.");
					LOGGER.info("Saved question in question bank: {}", question.getQuestionText());
				} catch (Exception e) {
					LOGGER.error("Unable to save question in the question bank.", e);
					uriBuilder.queryParam("emsg", "Unable to save question in the question bank.");
				}

				resp = Response.seeOther(uriBuilder.build()).build();
			}
			// Assigning questions to a pid.
			else if (submit.equals("Save")) {
				try {
					QuestionDAO questionDAO = new QuestionDAOImpl();
					QuestionMapDAO questionMapDAO = new QuestionMapDAOImpl();
					updateQuestions(questionDAO, questionMapDAO, qIdSet, pid, groupId, domainId, Boolean.TRUE);
					updateQuestions(questionDAO, questionMapDAO, optQidSet, pid, groupId, domainId, Boolean.FALSE);

					uriBuilder = uriBuilder.queryParam("smsg", "Question List updated for this Item.");
				} catch (Exception e) {
					LOGGER.error("Unable to update questions for Pid " + pid, e);
					uriBuilder = uriBuilder.queryParam("emsg", "Unable to update questions for this Item.");
				}

				resp = Response.seeOther(uriBuilder.build()).build();
			}
		} else {
			LOGGER.error("Attempt to POST question without param 'submit'.");
			resp = Response.serverError().build();
		}

		return resp;
	}
	
	/**
	 * doGetDsListAsJson
	 * 
	 * Australian National University Data Commons
	 * 
	 * This method returns JSON responses for requests for the following:
	 * 
	 * <ol>
	 * <li>List of Datastreams of a Fedora Object.</li>
	 * <li>List of Collection Requests and their details</li>
	 * <ol>
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		8/05/2012	Rahul Khanna (RK)		Initial
	 * 0.3		27/06/2012	Genevieve Turner (GT)	Updated to use DAO pattern and limit those authorised
	 * 0.4		04/04/2012	Genevieve Turner (GT)	Updated to allow for questions against groups and domains and to allow for optional and required questions
	 * </pre>
	 * 
	 * @return JSON object
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("json")
	@PreAuthorize("hasRole('ROLE_REGISTERED')")
	public Response doGetCollReqInfoAsJson(@QueryParam("task") String task, @QueryParam("pid") String pid,
			@QueryParam("group") Long groupId, @QueryParam("domain") Long domainId) {
		Response resp = null;

		LOGGER.trace("In doGetDsListAsJson. Params task={}, pid={}.", task, pid);

		// Gets a list of Questions assigned to a Pid.
		if (task.equals("listPidQuestions")) {
			try {
				QuestionDAO questionDAO = new QuestionDAOImpl();
				List<Question> reqQuestions = questionDAO.getQuestionsByPid(pid, true);
				LOGGER.info("Number of req questions: {}", reqQuestions.size());
				List<Question> optQuestions = questionDAO.getQuestionsByPid(pid, false);
				LOGGER.info("Number of opt questions: {}", optQuestions.size());
				resp = processQuestionsJsonResponse(reqQuestions, optQuestions);
			} catch (Exception e) {
				LOGGER.error("Unable to get list of questions for Pid " + pid, e);
				resp = Response.serverError().build();
			}
		} else if ("listGroupQuestions".equals(task)) {
			try {
				QuestionDAO questionDAO = new QuestionDAOImpl();
				List<Question> reqQuestions = questionDAO.getQuestionsByGroup(groupId, true);
				List<Question> optQuestions = questionDAO.getQuestionsByGroup(groupId, false);
				resp = processQuestionsJsonResponse(reqQuestions, optQuestions);
			} catch (Exception e) {
				LOGGER.error("Unable to get list of questions for group id ", groupId, e);
				resp = Response.serverError().build();
			}
		} else if ("listDomainQuestions".equals(task)) {
			try {
				QuestionDAO questionDAO = new QuestionDAOImpl();
				List<Question> reqQuestions = questionDAO.getQuestionsByDomain(domainId, true);
				List<Question> optQuestions = questionDAO.getQuestionsByDomain(domainId, false);
				resp = processQuestionsJsonResponse(reqQuestions, optQuestions);
			} catch (Exception e) {
				LOGGER.error("Unable to get list of questions for group id ", groupId, e);
				resp = Response.serverError().build();
			}
		}
		// Get a list of Collection Requests and their details.
		else if (task.equals("listCollReq")) {
			JSONArray reqStatusListJsonArray = new JSONArray();

			try {
				LOGGER.debug("Requested Collection Requests as JSON.");

				// Retrieve Collection Requests.

				CustomUser customUser = (CustomUser) SecurityContextHolder.getContext().getAuthentication()
						.getPrincipal();
				Long id = customUser.getId();

				List<Groups> reviewGroups = groupService.getReviewGroups();
				CollectionRequestDAO requestDAO = new CollectionRequestDAOImpl();
				List<CollectionRequest> reqStatusList = requestDAO.getPermittedRequests(id, reviewGroups);

				// Add the details of each CR into a JSONObject. Then add that JSONObject to a JSONArray.
				for (CollectionRequest iCr : reqStatusList) {
					JSONObject reqStatusJsonObj = new JSONObject();
					reqStatusJsonObj.put("id", iCr.getId());
					reqStatusJsonObj.put("pid", iCr.getPid());
					reqStatusJsonObj.put("requestor", iCr.getRequestor().getUsername());
					reqStatusJsonObj.put("timestamp", iCr.getTimestamp().toString());
					reqStatusJsonObj.put("lastStatus", iCr.getLastStatus().getStatus().toString());
					reqStatusJsonObj.put("lastStatusTimestamp", iCr.getLastStatus().getTimestamp().toString());
					reqStatusListJsonArray.put(reqStatusJsonObj);
				}

				// Convert the JSONArray into a JSON String and include in Response object.
				resp = Response.ok(reqStatusListJsonArray.toString(), MediaType.APPLICATION_JSON_TYPE).build();
			} catch (Exception e) {
				LOGGER.error("Unable to get list of Collection Requests.");
				resp = Response.serverError().build();
			}
		}

		return resp;
	}
	
	/**
	 * Creates a Response object containing the contents of a single file in a bag of collection as Response object
	 * containing InputStream.
	 * 
	 * @param pid
	 *            Pid of the collection from which a bagfile is to be read.
	 * @param fileInBag
	 *            Name of file in bag whose contents are to be returned as InputStream.
	 * @return Response object including HTTP headers and InputStream containing file contents.
	 * @throws IOException
	 */
	protected Response getBagFileOctetStreamResp(String pid, String fileInBag) throws IOException,
			StorageException {
		Response resp = null;
		InputStream is = null;

		if (!storageController.fileExists(pid, fileInBag)) {
			throw new NotFoundException(format("File {0} not found in record {1}", fileInBag, pid));
		}
		is = storageController.getFileStream(pid, fileInBag);
		ResponseBuilder respBuilder = Response.ok(is, MediaType.APPLICATION_OCTET_STREAM_TYPE);
		// Add filename, MD5 and file size to response header.
		FileInfo fileInfo = storageController.getFileInfo(pid, fileInBag);
		respBuilder = respBuilder.header("Content-Disposition",
				format("attachment; filename=\"{0}\"", fileInfo.getFilename()));
		String md5 = fileInfo.getMessageDigests().get("MD5");
		if (md5 != null && md5.length() > 0) {
			respBuilder = respBuilder.header("Content-MD5", md5);
		}
		respBuilder = respBuilder.header("Content-Length", Long.toString(fileInfo.getSize(), 10));
		respBuilder = respBuilder.lastModified(new Date(fileInfo.getLastModified().toMillis()));
		resp = respBuilder.build();

		return resp;
	}


	/**
	 * Creates a Response object containing a Zip file comprised of data from multiple files in a bag of a collection.
	 * 
	 * @param pid
	 *            Pid of the collection whose files are to be included in the Response object.
	 * 
	 * @param fileSet
	 *            Set of file names as Set&lt;String&gt; that are to be included in the Response.
	 * @param zipFilename
	 *            Name of the zip file that will be added to the Content-Disposition HTTP header.
	 * @return Response object
	 */
	private Response getBagFilesAsZip(String pid, Set<String> fileSet, String zipFilename) {
		Response resp = null;
		InputStream zipStream;
		try {
			zipStream = storageController.createZipStream(pid, fileSet);
			ResponseBuilder respBuilder = Response.ok(zipStream, MediaType.APPLICATION_OCTET_STREAM_TYPE);
			respBuilder.header("Content-Disposition", format("attachment; filename=\"{0}\"", zipFilename));
			resp = respBuilder.build();
		} catch (IOException | StorageException e) {
			LOGGER.error(e.getMessage(), e);
			throw new NotFoundException(e.getMessage());
		}
	
		return resp;
	}

	/**
	 * Save the questions to the database
	 * 
	 * @param questionDAO
	 *            The question DAO
	 * @param questionMapDAO
	 *            The question map DAO
	 * @param questions
	 *            The list of questions to check
	 * @param pid
	 *            The pid to assign the values to
	 * @param groupId
	 *            The group to assign the questions to
	 * @param domainId
	 *            The domain to assign the questions to
	 * @param required
	 *            Whether set of questions are required questions or not
	 */
	private void updateQuestions(QuestionDAO questionDAO, QuestionMapDAO questionMapDAO, Set<Long> questions,
			String pid, Long groupId, Long domainId, Boolean required) {
	
		List<Question> curQuestions = questionDAO.getQuestionsForObject(pid, groupId, domainId, required);
		for (Long iUpdatedId : questions) {
			boolean isAlreadyMapped = false;
			for (Question iCurQuestion : curQuestions) {
				if (iCurQuestion.getId() == iUpdatedId.longValue()) {
					isAlreadyMapped = true;
					break;
				}
			}
	
			if (!isAlreadyMapped) {
				Question question = questionDAO.getSingleById(iUpdatedId);
				LOGGER.debug("Adding Question '{}' against Pid {}", question.getQuestionText(), pid);
				QuestionMap qm = null;
				// Create the question map for the pid, group or domain
				if (pid != null && pid.trim().length() > 0) {
					qm = new QuestionMap(pid, question, required);
				} else if (groupId != null) {
					GenericDAO<Groups, Long> genericDAO = new GenericDAOImpl<Groups, Long>(Groups.class);
					Groups group = (Groups) genericDAO.getSingleById(groupId);
					qm = new QuestionMap(group, question, required);
				} else if (domainId != null) {
					GenericDAO<Domains, Long> genericDAO = new GenericDAOImpl<Domains, Long>(Domains.class);
					Domains domain = (Domains) genericDAO.getSingleById(domainId);
					qm = new QuestionMap(domain, question, required);
				}
				if (qm != null) {
					questionMapDAO.create(qm);
				}
			}
		}
	
		// Check if each question for a pid, group, or domain is provided in the updated list. If not, delete it.
		for (Question iCurQuestion : curQuestions) {
			if (!questions.contains(iCurQuestion.getId())) {
				LOGGER.debug("Mapping of Question ID" + iCurQuestion.getId() + "to be deleted...");
				QuestionMap questionMap = questionMapDAO.getSingleByObjectAndQuestion(iCurQuestion, pid, groupId,
						domainId);
				questionMapDAO.delete(questionMap.getId());
			}
		}
	}

	/**
	 * getAllQuestions
	 * 
	 * Australian National University Data Commons
	 * 
	 * Gets a list of all questions from the Question Bank.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		16/05/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @return Questions as List<Questions>
	 */
	private List<Question> getAllQuestions() {
		List<Question> questions = new QuestionDAOImpl().getAll();
		return questions;
	}

	private Response processQuestionsJsonResponse(List<Question> reqQuestions, List<Question> optQuestions)
			throws JSONException {
		JSONObject questionsJson = new JSONObject();

		JSONObject reqQuestionsJson = new JSONObject();
		// Add the Id and question (String) for each Question (Object) into a JSONObject.
		for (Question iQuestion : reqQuestions) {
			reqQuestionsJson.put(iQuestion.getId().toString(), iQuestion.getQuestionText());
		}
		questionsJson.put("required", reqQuestionsJson);

		JSONObject optQuestionsJson = new JSONObject();
		for (Question iQuestion : optQuestions) {
			optQuestionsJson.put(iQuestion.getId().toString(), iQuestion.getQuestionText());
		}
		questionsJson.put("optional", optQuestionsJson);

		// Convert the JSONObject into a JSON String and include it in the Response object.
		return Response.ok(questionsJson.toString(), MediaType.APPLICATION_JSON_TYPE).build();
	}

	private List<String> getEmails(String pid) {
		List<String> emailList = new ArrayList<String>();
		SolrClient solrClient = SolrManager.getInstance().getSolrClient();
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery(format("id:\"{0}\"", SolrUtils.escapeSpecialCharacters(pid)));
		solrQuery.addField("id");
		solrQuery.addField("unpublished.email");

		QueryResponse queryResponse;
		try {
			queryResponse = solrClient.query(solrQuery);
			SolrDocumentList resultList = queryResponse.getResults();
			if (resultList.getNumFound() == 0)
				throw new IllegalArgumentException(format("A collection doesn't exist with the Pid {0}", pid));

			if (resultList.getNumFound() > 1)
				throw new IllegalArgumentException(format("Multiple collections found with Pid {0}", pid));

			if (resultList.get(0).getFieldValues("unpublished.email") != null)
				for (Object emailAsObj : resultList.get(0).getFieldValues("unpublished.email"))
					emailList.add((String) emailAsObj);
		} catch (SolrServerException | IOException e) {
			LOGGER.warn(format("Unable to execute Solr Query to retrieve emails for pid {0}.", pid), e);
		}

		return emailList;
	}

	private String getCurUsername() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}
}
