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

import java.util.Arrays;
import java.util.List;

/**
 * ValidateException
 * 
 * Australian National University Data Commons
 * 
 * Exception that occurs on validation
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		12/12/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
public class ValidateException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	List<String> messages;
	
	/**
	 * Constructor
	 * 
	 * Exception for validation
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		12/12/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param message The message behind the error
	 */
	public ValidateException(String message) {
		super(message);
		messages = Arrays.asList(message);
	}
	
	/**
	 * Constructor
	 * 
	 * Constructor for a validation exception
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * X.X		21/12/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param messages A list of validation error messages
	 */
	public ValidateException(List<String> messages) {
		super(messages.toString());
		this.messages = messages;
	}
	
	/**
	 * getMessages
	 *
	 * The messages that have been set for the validation exception
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * X.X		21/12/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return The validation error messages
	 */
	public List<String> getMessages() {
		return messages;
	}
}
