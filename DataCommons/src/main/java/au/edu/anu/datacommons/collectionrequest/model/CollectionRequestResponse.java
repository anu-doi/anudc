package au.edu.anu.datacommons.collectionrequest.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonProperty;

import au.edu.anu.datacommons.collectionrequest.QuestionMap;

@XmlRootElement(name = "questions")
public class CollectionRequestResponse {
//	private List<Question> requiredQuestions;
//	private List<Question> optionalQuestions;
	private List<QuestionObject> questions;
	
	public CollectionRequestResponse() {
		
	}
	
//	public CollectionRequestResponse(List<Question> required, List<Question> optional) {
//		this.requiredQuestions = required;
//		this.optionalQuestions = optional;
//	}
	
	public CollectionRequestResponse(List<QuestionMap> questionMaps) {
		if (questionMaps != null) {
			List<QuestionObject> questions = new ArrayList<QuestionObject>();
			for (QuestionMap map : questionMaps) {
				questions.add(new QuestionObject(map));
			}
			this.questions = questions;
		}
	}

	@JsonProperty("question")
	public List<QuestionObject> getQuestions() {
		return questions;
	}

	public void setQuestions(List<QuestionObject> questions) {
		this.questions = questions;
	}
	
	public void sortQuestionsByOrder() {
		questions.sort(new Comparator<QuestionObject>() {
			@Override
			public int compare(QuestionObject o1, QuestionObject o2) {
				return o1.getOrder().compareTo(o2.getOrder());
			}
		});
	}
	
//	@JsonProperty("required")
//	public List<Question> getRequiredQuestions() {
//		return requiredQuestions;
//	}
//	
//	public void setRequiredQuestions(List<Question> requiredQuestions) {
//		this.requiredQuestions = requiredQuestions;
//	}
//	
//	@JsonProperty("optional")
//	public List<Question> getOptionalQuestions() {
//		return optionalQuestions;
//	}
//	
//	public void setOptionalQuestions(List<Question> optionalQuestions) {
//		this.optionalQuestions = optionalQuestions;
//	}
}
