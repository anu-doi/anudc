/*******************************************************************************
 * Australian National University Data Commons
 * Copyright (C) 2013  The Australian National University
 * 
 * This file is part of Australian National University Data Commons.
 * 
 * Australian National University Data Commons is free software: you
 * can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

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
