package au.edu.anu.datacommons.collectionrequest;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.core.MultivaluedMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import au.edu.anu.datacommons.collectionrequest.model.CollectionRequestResponse;
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
import au.edu.anu.datacommons.exception.ValidateException;
import au.edu.anu.datacommons.security.CustomUser;
import au.edu.anu.datacommons.security.acl.CustomACLPermission;
import au.edu.anu.datacommons.security.acl.PermissionService;
import au.edu.anu.datacommons.security.service.FedoraObjectService;
import au.edu.anu.datacommons.security.service.GroupService;
import au.edu.anu.datacommons.util.Util;

@Service("collectionRequestServiceImpl")
public class CollectionRequestServiceImpl implements CollectionRequestService {
	private static final Logger LOGGER = LoggerFactory.getLogger(CollectionRequestServiceImpl.class);
	
	@Resource(name = "groupServiceImpl")
	GroupService groupService;
	
	@Resource(name = "fedoraObjectServiceImpl")
	FedoraObjectService fedoraObjectService;
	
	@Resource(name="permissionService")
	PermissionService permissionService;

	@Override
	public List<CollectionRequest> getUserRequests() {
		CustomUser customUser = (CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Long id = customUser.getId();

		List<Groups> reviewGroups = groupService.getReviewGroups();

		CollectionRequestDAO collectionRequestDAO = new CollectionRequestDAOImpl();

		List<CollectionRequest> collReqs = collectionRequestDAO.getPermittedRequests(id, reviewGroups);

		
		return collReqs;
	}

	@Override
	public List<CollectionRequest> getReviewRequests() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CollectionDropbox> getUserApprovedRequests() {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		UsersDAO userDAO = new UsersDAOImpl();
		Users user = userDAO.getUserByName(username);
		
		DropboxDAO dropboxDAO = new DropboxDAOImpl();
		List<CollectionDropbox> dropboxes = dropboxDAO.getUserDropboxes(user);
		
		return dropboxes;
	}

	@Override
	public CollectionRequestResponse getQuestions(String task, String pid, Long groupId, Long domainId) {
		LOGGER.info("Get Questions: {}, {}, {}, {}", task, pid, groupId, domainId);
		CollectionRequestResponse response = null;
		switch (task) {
		case "listPidQuestions":
			response = getQuestionsByPid(pid);
			break;
		case "listGroupQuestions":
			response = getQuestionsByGroup(groupId);
			break;
		case "listDomainQuestions":
			response = getQuestionsByDomain(domainId);
			break;
		}
		
		return response;
	}
	
	private CollectionRequestResponse getQuestionsByPid(String pid) {
		LOGGER.debug("Get questions by pid: {}", pid);
		QuestionMapDAO questionMapDAO = new QuestionMapDAOImpl();
		FedoraObject fedoraObject = fedoraObjectService.getItemByPid(pid);
		List<QuestionMap> questionMaps = questionMapDAO.getListByItem(fedoraObject, true);
		return new CollectionRequestResponse(questionMaps);
	}
	
	private CollectionRequestResponse getQuestionsByGroup(Long groupId) {
		LOGGER.debug("Get questions by group: {}", groupId);
		QuestionMapDAO questionMapDAO = new QuestionMapDAOImpl();
		List<QuestionMap> questionMaps = questionMapDAO.getListByGroup(groupId, true);
		return new CollectionRequestResponse(questionMaps);
	}
	
	private CollectionRequestResponse getQuestionsByDomain(Long domainId) {
		LOGGER.debug("Get questions by domain: {}", domainId);
		QuestionMapDAO questionMapDAO = new QuestionMapDAOImpl();
		List<QuestionMap> questionMaps = questionMapDAO.getListByDomain(domainId, true);
		return new CollectionRequestResponse(questionMaps);
	}
	
	@Override
	public CollectionRequest saveAnswers(FedoraObject fedoraObject, MultivaluedMap<String, String> formParameters, String ipAddress) {
		CollectionRequest collectionRequest = getCollectionRequest(fedoraObject, formParameters, ipAddress);
		CollectionRequestDAO requestDAO = new CollectionRequestDAOImpl();
		collectionRequest = requestDAO.create(collectionRequest);
		return collectionRequest;
	}
	
	private CollectionRequest getCollectionRequest(FedoraObject fedoraObject, MultivaluedMap<String, String> formParameters, String ipAddress) {
		Users user = new UsersDAOImpl().getUserByName(SecurityContextHolder.getContext().getAuthentication()
				.getName());
		CollectionRequest collectionRequest = new CollectionRequest(fedoraObject.getObject_id(), user, ipAddress, fedoraObject);
		
		QuestionMapDAO questionMapDAO = new QuestionMapDAOImpl();
		List<QuestionMap> questions = questionMapDAO.getListByItem(fedoraObject, true);
		for (QuestionMap questionMap : questions) {
			Question question = questionMap.getQuestion();
			String qId = "q" + question.getId();
			if (formParameters.containsKey(qId) && Util.isNotEmpty(formParameters.getFirst(qId))) {
				CollectionRequestAnswer answer = new CollectionRequestAnswer(question, formParameters.getFirst(qId), questionMap.getSeqNum());
				collectionRequest.addAnswer(answer);
			}
			else if (questionMap.getRequired()) {
				throw new ValidateException("All required questions must be answered. The question '" + question.getQuestionText() + "' has been left empty.");
			}
		}
		
		return collectionRequest;
	}

	@Override
	public void saveQuestions(String pid, Long groupId, Long domainId, List<Long> qid, List<Long> requiredQuestions) {
		LOGGER.debug("PID: {}, Group ID: {}, Domain ID: {}, Number of Questions: {}, Number of Required Questions: {}", pid, groupId, domainId, qid.size(), requiredQuestions.size());
		if (pid != null) {
			saveObjectQuestions(pid, qid, requiredQuestions);
		}
		else if (groupId != null) {
			saveGroupQuestions(groupId, qid, requiredQuestions);
		}
		else if (domainId != null) {
			saveDomainQuestions(domainId, qid, requiredQuestions);
		}
	}
	
	public void saveDomainQuestions(Long domainId, List<Long> qid, List<Long> requiredQuestions) {
		verifyRequestQuestionAccess(Domains.class, domainId);
		
		GenericDAO<Domains, Long> domainDAO = new GenericDAOImpl<Domains, Long>(Domains.class);
		Domains domain = domainDAO.getSingleById(domainId);
		
		QuestionDAO questionDAO = new QuestionDAOImpl();
		List<QuestionMap> questions = new ArrayList<QuestionMap>();

		//TODO add sequence numbers
		for (int i = 0; i < qid.size(); i++) {
			Long questionId = qid.get(i);
			Question question = questionDAO.getSingleById(questionId);
			Boolean isRequired = requiredQuestions.contains(questionId);
			LOGGER.debug("Question ID: {}, Required: {}", question.getId(), isRequired);
			QuestionMap questionMap = new QuestionMap(domain, question, i, isRequired);
			questions.add(questionMap);
		}

		QuestionMapDAO questionMapDAO = new QuestionMapDAOImpl();
		List<QuestionMap> existingQuestions = questionMapDAO.getListByDomain(domain.getId(), false);
		
		updateQuestions(questionMapDAO, existingQuestions, questions);
		
	}
	
	public void saveGroupQuestions(Long groupId, List<Long> qid, List<Long> requiredQuestions) {
		verifyRequestQuestionAccess(Groups.class, groupId);

		GenericDAO<Groups, Long> groupDAO = new GenericDAOImpl<Groups, Long>(Groups.class);
		Groups group = groupDAO.getSingleById(groupId);

		QuestionDAO questionDAO = new QuestionDAOImpl();
		List<QuestionMap> questions = new ArrayList<QuestionMap>();

		for (int i = 0; i < qid.size(); i++) {
			Long questionId = qid.get(i);
			Question question = questionDAO.getSingleById(questionId);
			Boolean isRequired = requiredQuestions.contains(questionId);
			LOGGER.debug("Question ID: {}, Required: {}", question.getId(), isRequired);
			QuestionMap questionMap = new QuestionMap(group, question, i, isRequired);
			questions.add(questionMap);
		}

		QuestionMapDAO questionMapDAO = new QuestionMapDAOImpl();
		List<QuestionMap> existingQuestions = questionMapDAO.getListByGroup(group.getId(), false);
		
		updateQuestions(questionMapDAO, existingQuestions, questions);
	}
	
	public void saveObjectQuestions(String pid, List<Long> qid, List<Long> requiredQuestions) {
		LOGGER.debug("Save object questions for {}", pid);
		FedoraObject fedoraObject = fedoraObjectService.getItemByPid(pid);

		verifyRequestQuestionAccess(FedoraObject.class, fedoraObject.getId());
		
		QuestionDAO questionDAO = new QuestionDAOImpl();
		
		List<QuestionMap> questions = new ArrayList<QuestionMap>();

		//TODO add sequence numbers
		for (int i = 0; i < qid.size(); i++) {
			Long questionId = qid.get(i);
			Question question = questionDAO.getSingleById(questionId);
			Boolean isRequired = requiredQuestions.contains(questionId);
			LOGGER.debug("Question ID: {}, Required: {}", question.getId(), isRequired);
			QuestionMap questionMap = new QuestionMap(pid, question, i, isRequired);
			questions.add(questionMap);
		}

		QuestionMapDAO questionMapDAO = new QuestionMapDAOImpl();
		List<QuestionMap> existingQuestions = questionMapDAO.getListByItem(fedoraObject, false);
		
		updateQuestions(questionMapDAO, existingQuestions, questions);
	}
	
	private void verifyRequestQuestionAccess(Class<?> clazz, Long id) {
		boolean hasPermission = permissionService.checkPermission(clazz, id, CustomACLPermission.REVIEW);
		if (!hasPermission) {
			hasPermission = permissionService.checkPermission(clazz, id, CustomACLPermission.PUBLISH);
		}
		if (!hasPermission) {
			hasPermission = permissionService.checkPermission(clazz, id, CustomACLPermission.ADMINISTRATION);
		}
		if (!hasPermission) {
			throw new AccessDeniedException("You do not have permission to update these questions");
		}
	}
	
	private void updateQuestions(QuestionMapDAO questionMapDAO, List<QuestionMap> existingQuestions, List<QuestionMap> newQuestions) {
		LOGGER.debug("In updateQuestions");
		for (QuestionMap questionMap : newQuestions) {
			QuestionMap existingQuestion = getExistingQuestion(questionMap, existingQuestions);
			if (existingQuestion != null) {
				boolean updateQuestion = false;
				if (!questionMap.getSeqNum().equals(existingQuestion.getSeqNum())) {
					existingQuestion.setSeqNum(questionMap.getSeqNum());
					updateQuestion = true;
				}
				if (!questionMap.getRequired().equals(existingQuestion.getRequired())) {
					existingQuestion.setRequired(questionMap.getRequired());
					updateQuestion = true;
				}
				if (updateQuestion) {
					LOGGER.debug("Updating existing question, {}", questionMap.getQuestion().getId());
					questionMapDAO.update(existingQuestion);
				}
			}
			else {
				LOGGER.debug("Add question map for question quetionMap {}", questionMap.getQuestion().getId());
				questionMapDAO.create(questionMap);
			}
		}
		for (QuestionMap existingQuestion : existingQuestions) {
			boolean foundQuestion = false;
			for (int i = 0; !foundQuestion && i < newQuestions.size(); i++) {
				QuestionMap newQuestion = newQuestions.get(i);
				if (existingQuestion.getQuestion().getId().equals(newQuestion.getQuestion().getId())) {
					foundQuestion = true;
				}
			}
			if (!foundQuestion) {
				LOGGER.debug("Remove question map: {}", existingQuestion.getId());
				questionMapDAO.delete(existingQuestion.getId());
			}
		}
	}
	
	private QuestionMap getExistingQuestion(QuestionMap currentQuestion, List<QuestionMap> existingQuestions) {
		for (QuestionMap comparisonQuestion : existingQuestions) {
			LOGGER.debug("Comparing question {} to {}", comparisonQuestion.getQuestion().getId(), currentQuestion.getQuestion().getId());
			if (comparisonQuestion.getQuestion().getId().equals(currentQuestion.getQuestion().getId())) {
				LOGGER.info("Found match, returning comparison question: {}", comparisonQuestion.getId());
				return comparisonQuestion;
			}
		}
		return null;
	}
}
