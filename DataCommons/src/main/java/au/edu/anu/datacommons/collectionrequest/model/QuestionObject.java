package au.edu.anu.datacommons.collectionrequest.model;

import java.util.ArrayList;
import java.util.List;

import au.edu.anu.datacommons.collectionrequest.Question;
import au.edu.anu.datacommons.collectionrequest.QuestionMap;
import au.edu.anu.datacommons.collectionrequest.QuestionOption;

public class QuestionObject {
	private Long id;
	private String question;
	private Integer order;
	private Boolean required;
	private List<String> options;
	
	public QuestionObject() {
		
	}
	
	public QuestionObject(QuestionMap map) {
		Question questionItem = map.getQuestion();
		this.id = questionItem.getId();
		this.question = questionItem.getQuestionText();
		this.order = map.getSeqNum();
		this.required = map.getRequired();
		List<String> options = new ArrayList<String>();
		if (questionItem.getQuestionOptions() != null) {
			for (QuestionOption option : questionItem.getQuestionOptions()) {
				options.add(option.getValue());
			}
		}
		this.options = options;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getQuestion() {
		return question;
	}
	
	public void setQuestion(String question) {
		this.question = question;
	}
	
	public Integer getOrder() {
		return order;
	}
	
	public void setOrder(Integer order) {
		this.order = order;
	}
	
	public Boolean getRequired() {
		return required;
	}
	
	public void setRequired(Boolean required) {
		this.required = required;
	}

	public List<String> getOptions() {
		return options;
	}

	public void setOptions(List<String> options) {
		this.options = options;
	}
}
