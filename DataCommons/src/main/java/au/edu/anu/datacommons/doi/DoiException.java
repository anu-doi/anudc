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

package au.edu.anu.datacommons.doi;

/**
 * This exception is thrown when a DOI request is unable to be processed or is processed with unexpected results.
 */
public class DoiException extends Exception
{
	private static final long serialVersionUID = 1L;

	/**
	 * @see {@link Exception#Exception()}
	 */
	public DoiException()
	{
		super();
	}

	/**
	 * @see {@link Exception#Exception(String, Throwable)}
	 */
	public DoiException(String message, Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * @see {@link Exception#Exception(String)}
	 */
	public DoiException(String message)
	{
		super(message);
	}

	/**
	 * @see {@link Exception#Exception(Throwable)}
	 */
	public DoiException(Throwable cause)
	{
		super(cause);
	}
}
