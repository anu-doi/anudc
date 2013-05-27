package au.edu.anu.datacommons.metadatastores;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Grant
 * 
 * Australian National University Data Commons
 * 
 * Class to resolve grant information imported from Metadata Stores
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		24/05/2013	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Grant {
	private String contractCode;
	private String title;
//	private Person firstInvestigator;
//	private List<Person> associatedPeople = new ArrayList<Person>();
	private String startDate;
	private String endDate;
	private String status;
	private String fundsProvider;
	private String referenceNumber;
	private String description;
	private List<Subject> anzforSubjects = new ArrayList<Subject>();
	
	/**
	 * getContractCode
	 *
	 * Get the grant contract code
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		20/05/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the contractCode
	 */
	@JsonProperty("contract-code")
	public String getContractCode() {
		return contractCode;
	}
	
	/**
	 * setContractCode
	 *
	 * Set the grant contract code
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		20/05/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param contractCode the contractCode to set
	 */
	public void setContractCode(String contractCode) {
		this.contractCode = contractCode;
	}
	
	/**
	 * getTitle
	 *
	 * Get the grant title
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		20/05/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the title
	 */
	@JsonProperty("title")
	public String getTitle() {
		return title;
	}
	
	/**
	 * setTitle
	 *
	 * Set the grant title
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		20/05/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * getStartDate
	 *
	 * Get the grant start date
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		20/05/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the startDate
	 */
	@JsonProperty("start-date")
	public String getStartDate() {
		return startDate;
	}
	
	/**
	 * setStartDate
	 *
	 * Set the grant start date
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		20/05/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param startDate the startDate to set
	 */
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	
	/**
	 * getEndDate
	 *
	 * Get the grant end date
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		20/05/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the endDate
	 */
	@JsonProperty("end-date")
	public String getEndDate() {
		return endDate;
	}
	
	/**
	 * setEndDate
	 *
	 * Set the grant end date
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		20/05/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param endDate the endDate to set
	 */
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	
	/**
	 * getStatus
	 *
	 * Get the grant status
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		20/05/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the status
	 */
	@JsonProperty("status")
	public String getStatus() {
		return status;
	}
	
	/**
	 * setStatus
	 *
	 * Set the grant status
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		20/05/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	
	/**
	 * getFundsProvider
	 *
	 * Get the funds provider
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		20/05/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the fundsProvider
	 */
	@JsonProperty("funds-provider")
	public String getFundsProvider() {
		return fundsProvider;
	}
	
	/**
	 * setFundsProvider
	 *
	 * Set the funds provider
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		20/05/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param fundsProvider the fundsProvider to set
	 */
	public void setFundsProvider(String fundsProvider) {
		this.fundsProvider = fundsProvider;
	}
	
	/**
	 * getReferenceNumber
	 *
	 * Get the grant reference number
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		20/05/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the referenceNumber
	 */
	@JsonProperty("reference-number")
	public String getReferenceNumber() {
		return referenceNumber;
	}
	
	/**
	 * setReferenceNumber
	 *
	 * Set the grant reference number
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		20/05/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param referenceNumber the referenceNumber to set
	 */
	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	/**
	 * getDescription
	 *
	 * Get the description
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		24/05/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the description
	 */
	@JsonProperty("description")
	public String getDescription() {
		return description;
	}

	/**
	 * setDescription
	 *
	 * Set the description
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		24/05/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * getAnzforSubjects
	 *
	 * Get the field of research subjects
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		20/05/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the anzforSubjects
	 */
	@JsonProperty("for-subject")
	public List<Subject> getAnzforSubjects() {
		return anzforSubjects;
	}
	
	/**
	 * setAnzforSubjects
	 *
	 * Set the field of research subjects
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		20/05/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param anzforSubjects the anzforSubjects to set
	 */
	public void setAnzforSubjects(List<Subject> anzforSubjects) {
		this.anzforSubjects = anzforSubjects;
	}
}
