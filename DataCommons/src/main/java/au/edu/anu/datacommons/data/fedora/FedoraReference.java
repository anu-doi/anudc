package au.edu.anu.datacommons.data.fedora;


/**
 * FedoraReference
 * 
 * Australian National University Data Commons
 * 
 * Class for references or relationships to be added to Fedora Commons
 * 
 * Version	Date		Developer				Description
 * 0.1		08/03/2012	Genevieve Turner (GT)	Initial
 */
public class FedoraReference {
	private String predicate_;
	private String object_;
	private Boolean isLiteral_;
	
	/**
	 * getPredicate_
	 * 
	 * Returns the reference predicate
	 * 
	 * Version	Date		Dev						Description
	 * 0.1		08/03/2012	Genevieve Turner (GT)	Initial
	 * 
	 * @return The predicate of the reference
	 */
	public String getPredicate_() {
		return predicate_;
	}
	
	/**
	 * setPredicate_
	 * 
	 * Sets the reference predicate
	 * 
	 * Version	Date		Dev						Description
	 * 0.1		08/03/2012	Genevieve Turner (GT)	Initial
	 * 
	 * @param predicate_ The predicate of the reference
	 */
	public void setPredicate_(String predicate_) {
		this.predicate_ = predicate_;
	}
	
	/**
	 * getObject_
	 * 
	 * Gets the reference object
	 * 
	 * Version	Date		Dev						Description
	 * 0.1		08/03/2012	Genevieve Turner (GT)	Initial
	 * 
	 * @return The object of the reference
	 */
	public String getObject_() {
		return object_;
	}
	
	/**
	 * setObject_
	 * 
	 * Gets the reference object
	 * 
	 * Version	Date		Dev						Description
	 * 0.1		08/03/2012	Genevieve Turner (GT)	Initial
	 * 
	 * @param object_ The object of the reference
	 */
	public void setObject_(String object_) {
		this.object_ = object_;
	}
	
	/**
	 * getIsLiteral_
	 * 
	 * Gets whether the reference is a literal or a uri
	 * 
	 * Version	Date		Dev						Description
	 * 0.1		08/03/2012	Genevieve Turner (GT)	Initial
	 * 
	 * @return Whether the value is a literal or not
	 */
	public Boolean getIsLiteral_() {
		return isLiteral_;
	}
	
	/**
	 * getIsLiteral_
	 * 
	 * Sets whether the reference is a literal or a uri
	 * 
	 * Version	Date		Dev						Description
	 * 0.1		08/03/2012	Genevieve Turner (GT)	Initial
	 * 
	 * @param isLiteral_ Whether the value is a literal or not
	 */
	public void setIsLiteral_(Boolean isLiteral_) {
		this.isLiteral_ = isLiteral_;
	}
}
