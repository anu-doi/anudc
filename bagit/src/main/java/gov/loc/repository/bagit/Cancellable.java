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

package gov.loc.repository.bagit;

/**
 * Allows long-running operations to be canceled.
 */
public interface Cancellable
{
	/**
	 * <p>Cancels a long-running operation.  The operation may not be
	 * terminated immediately, but may end at the earliest possible
	 * convenience of the implementor.</p>
	 * 
	 * <p>Consecutive calls to this method must not have any additional
	 * effect.</p>
	 * 
	 * <p>The {@link #isCancelled()} property must be set to return
	 * <c>true</c> by the end of the cancel method.</p>
	 * 
	 * <p>The state of the operation after a cancel is undefined by
	 * this method, but gurantees may be made by particular implementors.</p>
	 */
	void cancel();
	
	/**
	 * Whether or not the operation has been canceled.  That this method
	 * returns <c>true</c> only indicates that a request to cancel
	 * has been registered - the operation may still be performing work
	 * until some convenient time to exit is reached. 
	 * 
	 * @return Returns <c>true</c> if the {@link #cancel()} method
	 * has been called; <c>false</c> otherwise.
	 */
	boolean isCancelled();
}
