package au.edu.anu.datacommons.collectionrequest;

import java.util.List;

import javax.ws.rs.core.MultivaluedMap;

import au.edu.anu.datacommons.collectionrequest.model.CollectionRequestResponse;
import au.edu.anu.datacommons.data.db.model.FedoraObject;

public interface CollectionRequestService {
	public List<CollectionRequest> getUserRequests();
	
	public List<CollectionRequest> getReviewRequests();
	
	public List<CollectionDropbox> getUserApprovedRequests();

	public CollectionRequestResponse getQuestions(String task, String pid, Long groupId, Long domainId);
	
	public CollectionRequest saveAnswers(FedoraObject fedoraObject, MultivaluedMap<String, String> formParameters, String ipAddress);
}
