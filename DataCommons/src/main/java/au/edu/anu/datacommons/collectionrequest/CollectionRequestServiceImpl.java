package au.edu.anu.datacommons.collectionrequest;

import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.core.MultivaluedMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import au.edu.anu.datacommons.collectionrequest.model.CollectionRequestResponse;
import au.edu.anu.datacommons.data.db.dao.CollectionRequestDAO;
import au.edu.anu.datacommons.data.db.dao.CollectionRequestDAOImpl;
import au.edu.anu.datacommons.data.db.dao.DropboxDAO;
import au.edu.anu.datacommons.data.db.dao.DropboxDAOImpl;
import au.edu.anu.datacommons.data.db.dao.QuestionMapDAO;
import au.edu.anu.datacommons.data.db.dao.QuestionMapDAOImpl;
import au.edu.anu.datacommons.data.db.dao.UsersDAO;
import au.edu.anu.datacommons.data.db.dao.UsersDAOImpl;
import au.edu.anu.datacommons.data.db.model.FedoraObject;
import au.edu.anu.datacommons.data.db.model.Groups;
import au.edu.anu.datacommons.data.db.model.Users;
import au.edu.anu.datacommons.exception.ValidateException;
import au.edu.anu.datacommons.security.CustomUser;
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
		QuestionMapDAO questionMapDAO = new QuestionMapDAOImpl();
		FedoraObject fedoraObject = fedoraObjectService.getItemByPid(pid);
		List<QuestionMap> questionMaps = questionMapDAO.getListByItem(fedoraObject);
		return new CollectionRequestResponse(questionMaps);
	}
	
	private CollectionRequestResponse getQuestionsByGroup(Long groupId) {
		QuestionMapDAO questionMapDAO = new QuestionMapDAOImpl();
		List<QuestionMap> questionMaps = questionMapDAO.getListByGroup(groupId);
		return new CollectionRequestResponse(questionMaps);
	}
	
	private CollectionRequestResponse getQuestionsByDomain(Long domainId) {
		QuestionMapDAO questionMapDAO = new QuestionMapDAOImpl();
		List<QuestionMap> questionMaps = questionMapDAO.getListByDomain(domainId);
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
		List<QuestionMap> questions = questionMapDAO.getListByItem(fedoraObject);
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
}
