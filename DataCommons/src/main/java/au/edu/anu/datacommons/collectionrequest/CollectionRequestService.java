package au.edu.anu.datacommons.collectionrequest;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.collectionrequest.CollectionRequestStatus.ReqStatus;
import au.edu.anu.datacommons.connection.fedora.FedoraBroker;
import au.edu.anu.datacommons.persistence.HibernateUtil;
import au.edu.anu.datacommons.properties.GlobalProps;

import com.sun.jersey.api.view.Viewable;
import com.yourmediashelf.fedora.client.FedoraClientException;
import com.yourmediashelf.fedora.client.request.ListDatastreams;
import com.yourmediashelf.fedora.client.response.ListDatastreamsResponse;
import com.yourmediashelf.fedora.generated.access.DatastreamType;

/**
 * CollectionRequestService
 * 
 * Australian National University Data Commons
 * 
 * This method serves REST requests related to Collection Requests. The following broad workflows are supported:
 * 
 * <ol>
 * <li>Submitting a Collection Request that includes the specific items (datastreams) requested and the answers to questions assigned to a collection (pid).</li>
 * <li>Changing the status of a Collection Request providing a reason for the change.</li>
 * <li>When a Collection Request is approved, providing access to the created dropbox.</li>
 * <li>Allow for questions to be added to the Question Bank.</li>
 * <li>Allow questions to be assigned to Fedora Objects so when a request is submitted, those questions must be answered.
 * </ol>
 * 
 * <pre>
 * Version	Date		Developer			Description
 * 0.1		1/05/2012	Rahul Khanna (RK)	Initial
 * </pre>
 * 
 */
@Path("/collreq")
public class CollectionRequestService
{
	private static final Logger LOGGER = LoggerFactory.getLogger(CollectionRequestService.class);
	// TODO Check if the entitymanager is to be object specific or not - static or non-static?
	private static final EntityManager entityManager = HibernateUtil.getSessionFactory().createEntityManager();

	private static final String COLL_REQ_JSP = "/collreq.jsp";
	private static final String DROPBOX_JSP = "/dropbox.jsp";
	private static final String QUESTION_JSP = "/question.jsp";
	private static final String DROPBOX_ACCESS_JSP = "/dropboxaccess.jsp";

	private enum MessageType
	{
		ERROR, WARNING, INFO, SUCCESS
	};

	private HashMap<String, Object> model = new HashMap<String, Object>();
	private Response resp = null;

	private Set<String> errors = null;
	private Set<String> warnings = null;
	private Set<String> infos = null;
	private Set<String> successes = null;

	@Context
	private HttpServletRequest request;

	/**
	 * doGetAsHtml
	 * 
	 * Australian National University Data Commons
	 * 
	 * Gets the Collection Request page.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		1/05/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @return Collection Request page as HTML.
	 */
	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response doGetAsHtml()
	{
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
	 * </pre>
	 * 
	 * @param collReqId
	 *            ID of the Collection request as Long.
	 * @return Collection Request page as HTML.
	 */
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Path("{collReqId}")
	public Response doGetReqItemAsHtml(@PathParam("collReqId") Long collReqId)
	{
		CollectionRequest collReq;

		LOGGER.trace("In method doGetReqItemAsHtml. Param collReqId={}.", collReqId);

		try
		{
			LOGGER.debug("Retrieving Collection Request with ID: {}...", collReqId);
			entityManager.getTransaction().begin();
			// Find the Collection Request with the specified ID.
			collReq = entityManager.find(CollectionRequest.class, collReqId);
			entityManager.getTransaction().commit();

			// Check if the Collection Request actually exists. If not, add error to message set.
			if (collReq != null)
			{
				LOGGER.debug("Found Collection Request ID {}, for Pid {}.", collReq.getId(), collReq.getPid());
				model.put("collReq", collReq);
			}
			else
			{
				LOGGER.error("Invalid Collection Request ID {}", collReqId);
				addMessage(MessageType.ERROR, "Invalid Collection Request ID.");
			}

			resp = Response.ok(new Viewable(COLL_REQ_JSP, model)).build();
		}
		catch (Exception e)
		{
			LOGGER.error("Unable to find Collection Request {}.", collReqId);
			if (entityManager.getTransaction().isActive())
				entityManager.getTransaction().rollback();
			resp = Response.serverError().build();
		}

		return resp;
	}

	/**
	 * doPostCollReqAsHtml
	 * 
	 * Australian National University Data Commons
	 * 
	 * Receives a POST request with the details of a new Collection Request to be created and creates a new Collection Request.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		1/05/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @param pid
	 *            Pid for which the request belongs to.
	 * @param dsIdSet
	 *            Datastream IDs as a set being requested.
	 * @param allFormParams
	 *            Map of all form parameters from which questions that have been answered are extracted.
	 * @return Response as HTML
	 */
	@POST
	@Produces(MediaType.TEXT_HTML)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response doPostCollReqAsHtml(@FormParam("pid") String pid, @FormParam("dsid") Set<String> dsIdSet, MultivaluedMap<String, String> allFormParams)
	{
		CollectionRequest newCollReq;

		LOGGER.trace("In method doPostCollReqAsHtml. Param pid={}, dsIdSet={}, allFormParams={}.", new Object[]
		{ pid, dsIdSet, allFormParams });

		// Save the Collection Request for further processing.
		try
		{
			entityManager.getTransaction().begin();
			newCollReq = new CollectionRequest(pid, 1234L, request.getRemoteAddr());

			// Check if at least one item has been requested. If not, add error in message set.
			if (dsIdSet.size() > 0)
			{

				// Add each of the items requested (datastreams) to the CR.
				for (String iDsId : dsIdSet)
				{
					CollectionRequestItem collReqItem = new CollectionRequestItem(iDsId);
					newCollReq.addItem(collReqItem);
				}

				// Get a list of questions assigned to the Pid.
				List<Question> questionList = entityManager
						.createQuery("SELECT q FROM Question q, QuestionMap qm WHERE qm.pid=:pid AND q=qm.question", Question.class).setParameter("pid", pid)
						.getResultList();

				// Iterate through the questions that need to be answered, get the answers for those questions and add to CR.
				// If an answer for a question doesn't exist break out of loop.
				boolean isAllAnswered = true;
				for (Question iQuestion : questionList)
				{
					// Check if the answer to the current question is provided. If yes, save the answer, else invalidate the whole request.
					if (allFormParams.containsKey("q" + iQuestion.getId()) && !allFormParams.getFirst("q" + iQuestion.getId()).trim().equals(""))
					{
						CollectionRequestAnswer ans = new CollectionRequestAnswer(iQuestion, allFormParams.getFirst("q" + iQuestion.getId()));
						newCollReq.addAnswer(ans);
					}
					else
					{
						isAllAnswered = false;
						break;
					}
				}

				// Check if all questions answered. If yes, save newly created CR, else don't save.
				if (isAllAnswered == true)
				{
					// Save the newly created CR and add success message to message set.
					entityManager.persist(newCollReq);
					entityManager.getTransaction().commit();
					addMessage(MessageType.SUCCESS, "Request successfully saved.");
					model.put("collReq", newCollReq);
					resp = Response.ok(new Viewable(COLL_REQ_JSP, model)).build();
				}
				else
				{
					if (entityManager.getTransaction().isActive())
						entityManager.getTransaction().rollback();
					addMessage(MessageType.ERROR, "Please answer all questions.");
					resp = Response.ok(new Viewable(COLL_REQ_JSP, model)).build();
				}
			}
			else
			{
				if (entityManager.getTransaction().isActive())
					entityManager.getTransaction().rollback();
				addMessage(MessageType.ERROR, "Please select at least one item.");
				resp = Response.ok(new Viewable(COLL_REQ_JSP, model)).build();
			}
		}
		catch (Exception e)
		{
			LOGGER.error("Unable to create new Collection Request.", e);
			entityManager.getTransaction().rollback();
			resp = Response.serverError().build();
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
	 * Version	Date		Developer			Description
	 * 0.1		2/05/2012	Rahul Khanna (RK)	Initial
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
	public Response doPostUpdateCollReqAsHtml(@PathParam("collReqId") long collReqId, @FormParam("status") ReqStatus status, @FormParam("reason") String reason)
	{
		CollectionRequest collReq;

		LOGGER.trace("In doPostUpdateCollReqAsHtml. Params collReqId={}, status={}, reason={}", new Object[]
		{ collReqId, status, reason });

		try
		{
			LOGGER.debug("Saving Collection Request ID {} with updated details...", collReqId);

			// Check if a reason is provided. If not, add error to message set.
			if (reason != null && !reason.trim().equals(""))
			{
				// Get the CR with the provided ID.
				entityManager.getTransaction().begin();
				collReq = entityManager.find(CollectionRequest.class, collReqId);

				// Add a status row to the status history for that CR.
				// TODO Replace userId with the actual userId.
				CollectionRequestStatus newStatus = new CollectionRequestStatus(status, reason, 1234L);
				collReq.addStatus(newStatus);
				entityManager.getTransaction().commit();

				// Add the CR object in the model and include a viewable in response.
				model.put("collReq", collReq);
				addMessage(MessageType.SUCCESS, "Successfully added status to Status History");
				if (status == ReqStatus.ACCEPTED)
					addMessage(MessageType.INFO, "Created Dropbox with code " + collReq.getDropbox().getAccessCode() + " and password "
							+ collReq.getDropbox().getAccessPassword());
			}
			else
			{
				addMessage(MessageType.ERROR, "No reason provided. Please provide a reason for the status change.");
			}

			resp = Response.ok(new Viewable(COLL_REQ_JSP, model)).build();
		}
		catch (Exception e)
		{
			LOGGER.error("Unable to add request row.", e);
			if (entityManager.getTransaction().isActive())
				entityManager.getTransaction().rollback();
			resp = Response.serverError().build();
		}

		return resp;
	}

	/**
	 * doGetDropboxesAsHtml
	 * 
	 * Australian National University Data Commons
	 * 
	 * This method returns a list of dropboxes and their details. This method enables administration of dropboxes only. Access to the files within the dropbox
	 * is from doGetDropboxAccessAsHtml method.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		2/05/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @return Response as HTML.
	 */
	@GET
	@Path("dropbox")
	@Produces(MediaType.TEXT_HTML)
	public Response doGetDropboxesAsHtml()
	{
		LOGGER.trace("In doGetDropboxesAsHtml.");

		try
		{
			entityManager.getTransaction().begin();
			// Get all relevant dropboxes.
			// TODO Exclude any dropboxes that the user doesn't have any admin rights to.
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<CollectionDropbox> criteria = builder.createQuery(CollectionDropbox.class);
			Root<CollectionDropbox> root = criteria.from(CollectionDropbox.class);
			criteria.select(root);
			List<CollectionDropbox> dropboxes = entityManager.createQuery(criteria).getResultList();
			entityManager.getTransaction().commit();
			model.put("dropboxes", dropboxes);
			resp = Response.ok(new Viewable(DROPBOX_JSP, model)).build();
		}
		catch (Exception e)
		{
			LOGGER.error("Unable to get list of Collection Dropboxes.", e);
			if (entityManager.getTransaction().isActive())
				entityManager.getTransaction().rollback();
			resp = Response.serverError().build();
		}

		return resp;
	}

	/**
	 * doGetDropboxAsHtml
	 * 
	 * Australian National University Data Commons
	 * 
	 * This method Displays the details of a dropbox. It does not allow access to the files that have been requested in the dropbox.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		2/05/2012	Rahul Khanna (RK)	Initial
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
	public Response doGetDropboxAsHtml(@PathParam("dropboxId") long dropboxId)
	{
		LOGGER.trace("In doGetDropboxAsHtml. Params dropboxId: {}", dropboxId);

		try
		{
			// Find the dropbox with the specified ID.
			entityManager.getTransaction().begin();
			CollectionDropbox dropbox = entityManager.find(CollectionDropbox.class, dropboxId);
			entityManager.getTransaction().commit();

			// Check if a valid dropbox exists and was retrieved.
			if (dropbox != null)
			{
				model.put("dropbox", dropbox);
			}
			else
			{
				addMessage(MessageType.ERROR, "Invalid Dropbox ID or a dropbox with that ID doesn't exist.");
			}

			resp = Response.ok(new Viewable(DROPBOX_JSP, model)).build();
		}
		catch (Exception e)
		{
			LOGGER.error("Unable to get list of Collection Dropboxes.", e);
			if (entityManager.getTransaction().isActive())
				entityManager.getTransaction().rollback();
			resp = Response.serverError().build();
		}

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
	public Response doGetDropboxAccessAsHtml(@PathParam("dropboxAccessCode") long dropboxAccessCode, @QueryParam("p") String password)
	{
		HashMap<String, String> downloadables;

		LOGGER.trace("In doGetDropboxAccessAsHtml. Params dropboxAccessCode={}, password={}.", dropboxAccessCode, password);

		try
		{
			// TODO Use CriteriaBuilder.
			/*
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<CollectionDropbox> criteria = builder.createQuery(CollectionDropbox.class);
			Root<CollectionDropbox> root = criteria.from(CollectionDropbox.class);
			criteria.select(root);
			criteria.where(builder.equal()
			*/

			try
			{
				LOGGER.debug("Finding dropbox with access code {}...", dropboxAccessCode);
				entityManager.getTransaction().begin();
				CollectionDropbox dropbox = (CollectionDropbox) entityManager.createQuery("FROM CollectionDropbox cd WHERE cd.accessCode=:dropboxAccessCode")
						.setParameter("dropboxAccessCode", dropboxAccessCode).getSingleResult();
				entityManager.getTransaction().commit();
				LOGGER.debug("Dropbox retrieved successfully.");
				model.put("dropbox", dropbox);

				if (password != null)										// Check if password provided.
				{
					if (password.equals(dropbox.getAccessPassword()))		// Check if password correct.
					{
						if (dropbox.getExpiry().after(new Date()))			// Check if dropbox expired.
						{
							if (dropbox.isActive())							// Check if dropbox active.
							{
								// Create HashMap downloadables with a link for each item to be downloaded.
								downloadables = new HashMap<String, String>();
								for (CollectionRequestItem reqItem : dropbox.getCollectionRequest().getItems())
								{
									StringBuilder url = new StringBuilder();
									url.append(GlobalProps.getProperty(GlobalProps.PROP_FEDORA_URI));
									url.append("/objects/");
									url.append(dropbox.getCollectionRequest().getPid());
									url.append("/datastreams/");
									url.append(reqItem.getItem());
									url.append("/content");
									downloadables.put(reqItem.getItem(), url.toString());
								}

								model.put("downloadables", downloadables);
							}
							else
							{
								addMessage(MessageType.ERROR, "Dropbox inactive.");
							}
						}
						else
						{
							addMessage(MessageType.ERROR, "Dropbox expired.");
						}
					}
					else
					{
						addMessage(MessageType.ERROR, "Incorrect Password");
					}
				}
				else
				{
					addMessage(MessageType.INFO, "Please provide password for this Dropbox.");
				}
			}
			catch (NoResultException e)
			{
				addMessage(MessageType.ERROR, "Invalid Dropbox Access Code");
				LOGGER.error("Invalid Dropbox Access Code", e);
			}
			catch (NonUniqueResultException e)
			{
				addMessage(MessageType.ERROR, "Non-unique Dropbox Access Code");
				LOGGER.error("Non-unique Dropbox Access Code", e);
			}
			finally
			{
				if (entityManager.getTransaction().isActive())
					entityManager.getTransaction().rollback();
			}

			resp = Response.ok(new Viewable(DROPBOX_ACCESS_JSP, model)).build();
		}
		catch (Exception e)
		{
			LOGGER.error("Unable to get Dropbox for access.", e);
			entityManager.getTransaction().rollback();
			resp = Response.serverError().build();
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
	 * Version	Date		Developer			Description
	 * 0.1		2/05/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @return Response as HTML.
	 */
	@GET
	@Path("question")
	@Produces(MediaType.TEXT_HTML)
	public Response doGetQuestionsAsHtml()
	{
		LOGGER.trace("In doGetQuestionsAsHtml");

		try
		{
			// Get all questions from the Question Bank.
			LOGGER.debug("Retrieving questions from question bank...");
			entityManager.getTransaction().begin();
			// TODO Use CriteriaBuilder
			List<Question> questions = entityManager.createQuery("FROM Question qb", Question.class).getResultList();
			entityManager.getTransaction().commit();

			// Add a warning to the message set to let the user know that there aren't any questions in the question bank.
			if (questions.size() == 0)
				addMessage(MessageType.WARNING, "No question found in the question bank.");

			model.put("questions", questions);
			resp = Response.ok(new Viewable(QUESTION_JSP, model)).build();
		}
		catch (Exception e)
		{
			LOGGER.error("Unable to retrieve questions from question bank", e);
			resp = Response.serverError().build();
		}

		return resp;
	}

	/**
	 * doPostQuestionAsHtml
	 * 
	 * Australian National University Data Commons
	 * 
	 * Accepts POST requests to add new questions in the question bank as well as requests to assign a question to a Pid.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		2/05/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @param submit
	 *            The task to be performed. Value comes from the submit button in a form. "Add Question" to add a question to the question bank, or "Save" to
	 *            update the list of questions against a Pid.
	 * @param questionStr
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
	public Response doPostQuestionAsHtml(@FormParam("submit") String submit, @FormParam("q") String questionStr, @FormParam("pid") String pid,
			@FormParam("qid") Set<Long> qIdSet)
	{
		LOGGER.trace("In doPostQuestionAsHtml. Params submit={}, questionStr={}, pid={}, qid={}.", new Object[]
		{ submit, questionStr, pid, qIdSet });

		if (submit != null)
		{
			// Adding a question to the question bank.
			if (submit.equals("Add Question"))
			{
				// TODO Check if the question already exists (requires text search). Possibly check for weird chars.

				try
				{
					LOGGER.debug("Saving question in question bank...", pid);
					// Create Question object and persist it.
					entityManager.getTransaction().begin();
					Question question = new Question(questionStr);
					entityManager.persist(question);
					entityManager.getTransaction().commit();
					addMessage(MessageType.SUCCESS, "Question successfully added.");
					resp = Response.ok(new Viewable(QUESTION_JSP, model)).build();
					LOGGER.debug("Saved question in question bank.");
				}
				catch (Exception e)
				{
					LOGGER.error("Unable to save question in the question bank.", e);
					if (entityManager.getTransaction().isActive())
						entityManager.getTransaction().rollback();
					resp = Response.serverError().build();
				}
			}
			// Assigning questions to a pid.
			else if (submit.equals("Save"))
			{
				try
				{
					entityManager.getTransaction().begin();
					// TODO Use CriteriaBuilder.
					List<Question> curQuestionsPid = entityManager
							.createQuery("SELECT q FROM Question q, QuestionMap qm WHERE qm.pid=:pid AND q=qm.question", Question.class)
							.setParameter("pid", pid).getResultList();

					// Check if each question Id provided as query parameters already exist. If not, add them.
					for (Long iUpdatedId : qIdSet)
					{
						boolean isAlreadyMapped = false;
						for (Question iCurQuestion : curQuestionsPid)
						{
							if (iCurQuestion.getId() == iUpdatedId.longValue())
							{
								isAlreadyMapped = true;
								break;
							}
						}

						if (!isAlreadyMapped)
						{
							Question question = entityManager.find(Question.class, iUpdatedId);
							LOGGER.debug("Adding Question '{}' against Pid {}", question.getQuestion(), pid);
							QuestionMap qm = new QuestionMap(pid, question);
							entityManager.persist(qm);
						}
					}

					// Check if each question for a pid is provided in the updated list. If not, delete it.
					for (Question iCurQuestion : curQuestionsPid)
					{
						if (!qIdSet.contains(iCurQuestion.getId()))
						{
							// TODO Delete question against pid.
							LOGGER.debug("Mapping of Question ID" + iCurQuestion.getId() + "to be deleted...");
							QuestionMap qMap = entityManager
									.createQuery("SELECT qm FROM QuestionMap qm, Question q WHERE qm.pid=:pid AND qm.question=:q", QuestionMap.class)
									.setParameter("pid", pid).setParameter("q", iCurQuestion).getSingleResult();
							entityManager.remove(qMap);
						}
					}

					entityManager.getTransaction().commit();
					addMessage(MessageType.SUCCESS, "Questions updated.");
					resp = Response.ok(new Viewable(QUESTION_JSP, model)).build();
				}
				catch (Exception e)
				{
					LOGGER.error("Unable to update questions for Pid " + pid, e);
					if (entityManager.getTransaction().isActive())
						entityManager.getTransaction().rollback();
					resp = Response.serverError().build();
				}
			}
		}
		else
		{
			LOGGER.error("Attempt to POST question without param 'submit'.");
			resp = Response.serverError().build();
		}

		return resp;
	}

	/**
	 * addMessage
	 * 
	 * Australian National University Data Commons
	 * 
	 * Adds a message to the set of its type - errors, warnings, infos and successes. Sets that contain messages are included in the model when creating a
	 * viewable for a response. The JSP displays messages on top of the page with the relevant formatting in addition to any other data that may be included in
	 * the model. See statusmessages.jsp .
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		3/05/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @param msgType
	 *            Type of the message as enum MessageType.
	 * @param message
	 *            Message to be displayed as string.
	 */
	private void addMessage(MessageType msgType, String message)
	{
		Set<String> msgSet = null;

		LOGGER.trace("In addMessage. Params msgType={}, message={}.", msgType, message);

		// Check if a HashSet exists for the message type provided. If not, create it and add it to model. Then add the message to the hashset for the message type.
		switch (msgType)
		{
		case ERROR:
			if (errors == null)
				errors = new HashSet<String>();
			msgSet = errors;
			if (!model.containsValue(msgSet))
				model.put("errors", msgSet);

			break;

		case WARNING:
			if (warnings == null)
				warnings = new HashSet<String>();
			msgSet = warnings;
			if (!model.containsValue(msgSet))
				model.put("warnings", msgSet);
			break;

		case INFO:
			if (infos == null)
				infos = new HashSet<String>();
			msgSet = infos;
			if (!model.containsValue(msgSet))
				model.put("infos", msgSet);
			break;

		case SUCCESS:
			if (successes == null)
				successes = new HashSet<String>();
			msgSet = successes;
			if (!model.containsValue(msgSet))
				model.put("successes", msgSet);
			break;
		}

		if (msgSet != null)
			msgSet.add(message);
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
	 * @return JSON object
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("json")
	@SuppressWarnings("unchecked")
	// To eliminate warnings thrown by JSONObject and JSONArray.
	public Response doGetDsListAsJson(@QueryParam("task") String task, @QueryParam("pid") String pid)
	{
		LOGGER.trace("In doGetDsListAsJson. Params task={}, pid={}.", task, pid);

		// Gets a list of datastreams in a Fedora Object.
		if (task.equals("listDs"))
		{
			JSONArray dsListJson = new JSONArray();

			try
			{
				LOGGER.debug("Requested Datastream List for pid {} as JSON.", pid);

				// Get a list of datastreams for the pid from the Fedora Repository.
				ListDatastreams listDsCmd = new ListDatastreams(pid);
				ListDatastreamsResponse listDsResp = listDsCmd.execute(FedoraBroker.getClient());

				// Get the IDs and Labels of datasets, create a JSONObject, add it to JSONArray
				// TODO Exclude DC, XML_SOURCE, XML_TEMPLATE, RELS-EXT (?)
				for (DatastreamType iDs : listDsResp.getDatastreams())
				{
					JSONObject dsJsonObj = new JSONObject();
					dsJsonObj.put("dsId", iDs.getDsid());
					dsJsonObj.put("dsLabel", iDs.getLabel());
					dsListJson.add(dsJsonObj);
				}

				// Convert the JSONArray containing datastream details into a JSON string and create a Response object for return.
				LOGGER.debug("Returning JSON Object: {}", dsListJson.toJSONString());
				resp = Response.ok(dsListJson.toJSONString(), MediaType.APPLICATION_JSON_TYPE).build();
			}
			catch (FedoraClientException e)
			{
				LOGGER.error("Unable to retrieve list of datastreams.", e);
				resp = Response.serverError().build();
			}
		}
		// Gets a list of Questions assigned to a Pid.
		else if (task.equals("listPidQuestions"))
		{
			JSONObject questionsJson = new JSONObject();
			try
			{
				// Get all Questions assigned to the specified Pid.
				entityManager.getTransaction().begin();
				List<Question> curQuestionsPid = entityManager
						.createQuery("SELECT q FROM Question q, QuestionMap qm WHERE qm.pid=:pid AND q=qm.question", Question.class).setParameter("pid", pid)
						.getResultList();
				entityManager.getTransaction().commit();

				// Add the Id and question (String) for each Question (Object) into a JSONObject. 
				for (Question iQuestion : curQuestionsPid)
					questionsJson.put(iQuestion.getId(), iQuestion.getQuestion());

				// Convert the JSONObject into a JSON String and include it in the Response object.
				resp = Response.ok(questionsJson.toJSONString(), MediaType.APPLICATION_JSON_TYPE).build();
			}
			catch (Exception e)
			{
				LOGGER.error("Unable to get list of questions for Pid " + pid, e);
				resp = Response.serverError().build();
			}
		}
		// Get a list of Collection Requests and their details.
		else if (task.equals("listCollReq"))
		{
			JSONArray reqStatusListJsonArray = new JSONArray();

			try
			{
				LOGGER.debug("Requested Collection Requests as JSON.");

				// Retrieve Collection Requests. 
				entityManager.getTransaction().begin();
				List<CollectionRequest> reqStatusList = entityManager.createQuery("FROM CollectionRequest cr ORDER BY cr.timestamp DESC",
						CollectionRequest.class).getResultList();
				entityManager.getTransaction().commit();
				
				// Add the details of each CR into a JSONObject. Then add that JSONObject to a JSONArray.
				for (CollectionRequest i : reqStatusList)
				{
					JSONObject reqStatusJsonObj = new JSONObject();
					reqStatusJsonObj.put("id", i.getId());
					reqStatusJsonObj.put("pid", i.getPid());
					reqStatusJsonObj.put("requestorId", i.getRequestorId());
					reqStatusJsonObj.put("timestamp", i.getTimestamp().toString());
					reqStatusJsonObj.put("lastStatus", i.getLastStatus().getStatus().toString());
					reqStatusJsonObj.put("lastStatusTimestamp", i.getLastStatus().getTimestamp().toString());
					reqStatusListJsonArray.add(reqStatusJsonObj);
				}
				
				// Convert the JSONArray into a JSON String and include in Response object.
				resp = Response.ok(reqStatusListJsonArray.toJSONString(), MediaType.APPLICATION_JSON_TYPE).build();
			}
			catch (Exception e)
			{
				LOGGER.error("Unable to get list of Collection Requests.");
				if (entityManager.getTransaction().isActive())
					entityManager.getTransaction().rollback();
				resp = Response.serverError().build();
			}
		}

		return resp;
	}
}
