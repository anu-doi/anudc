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
import javax.ws.rs.core.UriInfo;

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

	private HashMap<String, Object> model = new HashMap<String, Object>();
	private Response resp = null;

	private enum MessageType
	{
		ERROR, WARNING, INFO, SUCCESS
	};

	private Set<String> errors = null;
	private Set<String> warnings = null;
	private Set<String> infos = null;
	private Set<String> successes = null;

	@Context
	private HttpServletRequest request;

	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response doGetAsHtml()
	{
		resp = Response.ok(new Viewable(COLL_REQ_JSP, model)).build();
		return resp;
	}

	@GET
	@Produces(MediaType.TEXT_HTML)
	@Path("{collReqId}")
	public Response doGetReqItemAsHtml(@PathParam("collReqId") Long collReqId)
	{
		CollectionRequest collReq;

		LOGGER.trace("In method doGetReqItemAsHtml");

		try
		{
			LOGGER.debug("Retrieving Collection Request with ID: {}...", collReqId);
			entityManager.getTransaction().begin();
			collReq = entityManager.find(CollectionRequest.class, collReqId);
			entityManager.getTransaction().commit();

			if (collReq != null)
			{
				LOGGER.debug("Found Collection Request ID {}, for Pid: {}. Returning as HTML.", collReq.getId(), collReq.getPid());
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

	@POST
	@Produces(MediaType.TEXT_HTML)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response doPostCollReqAsHtml(@FormParam("pid") String pid, @FormParam("dsid") Set<String> dsIdSet, MultivaluedMap<String, String> allFormParams)
	{
		CollectionRequest newCollReq;

		LOGGER.trace("In method doPostCollReqAsHtml");

		// Save the Collection Request for further processing.
		try
		{
			entityManager.getTransaction().begin();
			newCollReq = new CollectionRequest(pid, 1234L, request.getRemoteAddr());

			if (dsIdSet.size() > 0)
			{

				// Add each of the items requested (datastreams)
				for (String iDsId : dsIdSet)
				{
					CollectionRequestItem collReqItem = new CollectionRequestItem(iDsId);
					newCollReq.addItem(collReqItem);
				}

				// Add the answers to the questions required.
				List<Question> questionList = entityManager
						.createQuery("SELECT q FROM Question q, QuestionMap qm WHERE qm.pid=:pid AND q=qm.question", Question.class).setParameter("pid", pid)
						.getResultList();

				boolean isAllAnswered = true;
				for (Question iQuestion : questionList)
				{
					// Check if the answer to the current question is provided. If yes, save the answer, else invalidate the whole request.
					if (allFormParams.containsKey("q" + iQuestion.getId()) && !allFormParams.getFirst("q" + iQuestion.getId()).equals(""))
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

				if (isAllAnswered == true)
				{
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

	@POST
	@Path("{collReqId}")
	@Produces(MediaType.TEXT_HTML)
	public Response doPutCollReqAsHtml(@PathParam("collReqId") long collReqId, @FormParam("id") long id, @FormParam("status") ReqStatus status,
			@FormParam("reason") String reason)
	{
		CollectionRequest collReq;

		LOGGER.debug("Attempting POST for Request ID {}.", collReqId);

		try
		{
			entityManager.getTransaction().begin();
			collReq = entityManager.find(CollectionRequest.class, collReqId);

			// TODO Replace userId with the actual userId.
			CollectionRequestStatus newStatus = new CollectionRequestStatus(status, reason, 1234L);
			collReq.addStatus(newStatus);

			entityManager.getTransaction().commit();

			model.put("collReq", collReq);
			addMessage(MessageType.SUCCESS, "Successfully added status to Status History");
			if (status == ReqStatus.ACCEPTED)
				addMessage(MessageType.INFO, "Created Dropbox with code " + collReq.getDropbox().getAccessCode() + " and password " + collReq.getDropbox().getAccessPassword());
			
			resp = Response.ok(new Viewable(COLL_REQ_JSP, model)).build();
		}
		catch (Exception e)
		{
			LOGGER.error("Unable to add request row.", e);
			entityManager.getTransaction().rollback();
			resp = Response.serverError().build();
		}

		return resp;
	}

	@GET
	@Path("dropbox")
	@Produces(MediaType.TEXT_HTML)
	public Response doGetDropboxesAsHtml()
	{
		LOGGER.debug("Attempting GET for Dropboxes");

		try
		{
			entityManager.getTransaction().begin();

			// Select all dropboxes.
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
			entityManager.getTransaction().rollback();
			resp = Response.serverError().build();
		}

		return resp;
	}

	@GET
	@Path("dropbox/{dropboxId}")
	@Produces(MediaType.TEXT_HTML)
	public Response doGetDropboxAsHtml(@PathParam("dropboxId") long dropboxId)
	{
		LOGGER.debug("Attempting GET for Dropbox ID: {}", dropboxId);

		try
		{
			entityManager.getTransaction().begin();
			CollectionDropbox dropbox = entityManager.find(CollectionDropbox.class, dropboxId);
			entityManager.getTransaction().commit();

			model.put("dropbox", dropbox);

			resp = Response.ok(new Viewable(DROPBOX_JSP, model)).build();
		}
		catch (Exception e)
		{
			LOGGER.error("Unable to get list of Collection Dropboxes.", e);
			entityManager.getTransaction().rollback();
			resp = Response.serverError().build();
		}

		return resp;
	}

	@GET
	@Path("dropbox/access/{dropboxAccessCode}")
	@Produces(MediaType.TEXT_HTML)
	public Response doGetDropboxAccessAsHtml(@PathParam("dropboxAccessCode") long dropboxAccessCode, @QueryParam("p") String password)
	{
		HashMap<String, String> downloadables;

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
				LOGGER.info("Finding dropbox with access code {}...", dropboxAccessCode);
				entityManager.getTransaction().begin();
				CollectionDropbox dropbox = (CollectionDropbox) entityManager.createQuery("FROM CollectionDropbox cd WHERE cd.accessCode=:dropboxAccessCode")
						.setParameter("dropboxAccessCode", dropboxAccessCode).getSingleResult();
				entityManager.getTransaction().commit();
				LOGGER.info("Dropbox retrieved successfully.");
				model.put("dropbox", dropbox);

				if (password != null)										// Check if password provided.
				{
					if (password.equals(dropbox.getAccessPassword()))		// Check if password correct.
					{
						if (dropbox.getExpiry().after(new Date()))			// Check if dropbox expired.
						{
							if (dropbox.isActive())							// Check if dropbox active.
							{
								// Put in the model that the access code and password have been authenticated.
								model.put("auth", "y");

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

	@GET
	@Path("question")
	@Produces(MediaType.TEXT_HTML)
	public Response doGetQuestionsAsHtml()
	{
		try
		{
			entityManager.getTransaction().begin();
			// TODO Use CriteriaBuilder
			List<Question> questions = entityManager.createQuery("FROM Question qb", Question.class).getResultList();
			entityManager.getTransaction().commit();

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

	@GET
	@Path("question/{pid}")
	@Produces(MediaType.TEXT_HTML)
	public Response doGetQuestionPidAsHtml(@PathParam("pid") String pid, @Context UriInfo uriInfo)
	{
		try
		{
			entityManager.getTransaction().begin();
			List<Question> questionsPid = entityManager
					.createQuery("SELECT q FROM Question q, QuestionMap qm WHERE qm.pid=:pid AND q=qm.question", Question.class).setParameter("pid", pid)
					.getResultList();
			entityManager.getTransaction().commit();

			model.put("questionsPid", questionsPid);
			resp = Response.ok(new Viewable(QUESTION_JSP, model)).build();
		}
		catch (Exception e)
		{
			LOGGER.error("Unable to get question for pid " + pid, e);
			if (entityManager.getTransaction().isActive())
				entityManager.getTransaction().rollback();
			resp = Response.serverError().build();
		}

		return resp;
	}

	@POST
	@Path("question")
	@Produces(MediaType.TEXT_HTML)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response doPostQuestionAsHtml(@FormParam("submit") String submit, @FormParam("q") String questionStr, @FormParam("pid") String pid,
			@FormParam("qid") Set<Long> qIdSet)
	{
		if (submit != null)
		{
			// Adding a question to the question bank.
			if (submit.equals("Add Question"))
			{
				// TODO Check if the question already exists. Possibly check for weird chars.

				try
				{
					LOGGER.debug("Saving question in question bank...", pid);
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
			// Saving a Pid with the list of assigned questions.
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

	private void logClientInfo()
	{
		LOGGER.info("Query String: {}", request.getQueryString());
		LOGGER.info("Remote Host: {} [{}]", request.getRemoteAddr(), request.getRemoteHost());
		LOGGER.info("Remote User: {}", request.getRemoteUser());
	}

	/**
	 * Adds a message to the set of its type.
	 * 
	 * @param msgType
	 * @param message
	 */
	private void addMessage(MessageType msgType, String message)
	{
		Set<String> msgSet = null;

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

		msgSet.add(message);
	}

	/**
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
	public Response doGetDsListAsJson(@QueryParam("task") String task, @QueryParam("pid") String pid)
	{
		if (task.equals("listDs"))
		{
			JSONArray dsListJson = new JSONArray();

			try
			{
				LOGGER.debug("Requested Datastream List for pid {} as JSON.", pid);

				// Get a list of datastreams for the pid.
				ListDatastreams listDsCmd = new ListDatastreams(pid);
				ListDatastreamsResponse listDsResp = listDsCmd.execute(FedoraBroker.getClient());

				// Get the IDs and Labels of datasets and put them into a HashMap
				// TODO Exclude DC, XML_SOURCE, XML_TEMPLATE, RELS-EXT (?)
				for (DatastreamType iDs : listDsResp.getDatastreams())
				{
					JSONObject dsJsonObj = new JSONObject();
					dsJsonObj.put("dsId", iDs.getDsid());
					dsJsonObj.put("dsLabel", iDs.getLabel());
					dsListJson.add(dsJsonObj);
				}

				LOGGER.debug("Returning JSON Object: {}", dsListJson.toJSONString());
				resp = Response.ok(dsListJson.toJSONString(), MediaType.APPLICATION_JSON_TYPE).build();
			}
			catch (FedoraClientException e)
			{
				LOGGER.error("Unable to retrieve list of datastreams.", e);
				resp = Response.serverError().build();
			}
		}
		else if (task.equals("listPidQuestions"))
		{
			JSONObject questionsJson = new JSONObject();
			try
			{
				entityManager.getTransaction().begin();
				List<Question> curQuestionsPid = entityManager
						.createQuery("SELECT q FROM Question q, QuestionMap qm WHERE qm.pid=:pid AND q=qm.question", Question.class).setParameter("pid", pid)
						.getResultList();
				for (Question iQuestion : curQuestionsPid)
				{
					questionsJson.put(iQuestion.getId(), iQuestion.getQuestion());
				}
				entityManager.getTransaction().commit();
				resp = Response.ok(questionsJson.toJSONString(), MediaType.APPLICATION_JSON_TYPE).build();
			}
			catch (Exception e)
			{
				LOGGER.error("Unable to get list of questions for Pid " + pid, e);
				resp = Response.serverError().build();
			}
		}
		else if (task.equals("listCollReq"))
		{
			List<CollectionRequest> reqStatusList;
			JSONArray reqStatusListJsonArray = new JSONArray();

			try
			{
				LOGGER.debug("Requested Collection Requests as JSON.");
				entityManager.getTransaction().begin();
				reqStatusList = entityManager.createQuery("FROM CollectionRequest cr ORDER BY cr.timestamp DESC", CollectionRequest.class).getResultList();
				entityManager.getTransaction().commit();
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
