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

package au.edu.anu.datacommons.exception;

import javax.ws.rs.core.Response.Status;

/**
 * DataCommonsException
 * 
 * Australian National University Data Commons
 * 
 * General Exception class for the Data Commons.  It contains the http status reponse code and a message for an indication of what the error is
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		02/01/2013	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
public class DataCommonsException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	int status;
	String message;
	
	/**
	 * Constructor
	 * 
	 * Construct a new instance with the given HTTP status code and message
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		02/01/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param status The status of the error reponse
	 * @param message The error message text
	 */
	public DataCommonsException(int status, String message) {
		super(message);
		this.status = status;
		this.message = message;
	}
	
	/**
	 * Constructor
	 * 
	 * Construct a new instance with the given HTTP status code and message
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		02/01/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param status The status of the error reponse
	 * @param message The error message text
	 */
	public DataCommonsException(Status status, String message) {
		this(status.getStatusCode(), message);
	}
	
	/**
	 * getStatus
	 *
	 * Get the status code of the response
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		02/01/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}
	
	/**
	 * getErrorMessage
	 *
	 * Get the error message
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		02/01/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the message
	 */
	public String getErrorMessage() {
		return message;
	}
}
