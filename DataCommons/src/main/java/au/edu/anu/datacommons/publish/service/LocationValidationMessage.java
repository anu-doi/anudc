package au.edu.anu.datacommons.publish.service;

import java.util.ArrayList;
import java.util.List;

/**
 * LocationValidationMessage
 * 
 * Australian National University Data Commons
 * 
 * Placeholder
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		11/12/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
public class LocationValidationMessage {
	private String location;
	private List<String> messages;
	
	/**
	 * Constructor
	 * 
	 * Constructor class for LocationValidationMessage
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		11/12/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 *
	 */
	public LocationValidationMessage() {
		messages = new ArrayList<String>();
	}
	
	/**
	 * Constructor
	 * 
	 * Constructor class for LocationValidationMessage
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		11/12/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param location The location for which the message occurs
	 * @param messages The validation messages
	 * 
	 */
	public LocationValidationMessage(String location, List<String> messages) {
		this.location = location;
		this.messages = messages;
	}
	
	/**
	 * getLocation
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		05/12/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}
	
	/**
	 * setLocation
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		05/12/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}
	
	/**
	 * getMessages
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		05/12/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the messages
	 */
	public List<String> getMessages() {
		return messages;
	}
	
	/**
	 * setMessages
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		05/12/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param messages the messages to set
	 */
	public void setMessages(List<String> messages) {
		this.messages = messages;
	}
}
