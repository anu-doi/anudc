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

package gov.loc.repository.bagit.transfer;

/**
 * A failure strategy that immediately throws a {@link RuntimeException}.
 * This is quite useful for unit testing, and perhaps other things.
 * If the context is a {@link Throwable}, it will be thrown
 * as the {@link Throwable#getCause() cause}.
 * 
 * @version $Id$
 */
public class ThrowExceptionFailStrategy implements FetchFailStrategy 
{
	/**
	 * Throws a {@link RuntimeException}.
	 * 
	 * @param uri Put in the error message.
	 * @param size Ignored.
	 * @param context If a {@link Throwable}, the context will be passed
	 * 		  as the {@link Throwable#getCause() cause}.  Otherwise, ignored.
	 * 
	 * @throws RuntimeException Always.
	 */
	@Override
	public FetchFailureAction registerFailure(FetchTarget target, Object context) 
	{
		if (context instanceof Throwable)
			throw new RuntimeException("Could not fetch: " + target.getFilename(), (Throwable)context);
		else
			throw new RuntimeException("Could not fetch: " + target.getFilename());
	}
}
