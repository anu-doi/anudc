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
 * <p>Broadcasts progress to interested listeners.
 * This interface is implemented by any components that provide progress
 * updates during a potentially long-running operation.</p>
 * 
 * <p>To receive progress
 * updates, pass an implementation of the the {@link ProgressListener}
 * interface to the {@link #addProgressListener(ProgressListener)} method.
 * If you no longer wish to receive updates, object may be passed to the
 * {@link #removeProgressListener(ProgressListener)} method.</p>
 * 
 * <p>This interface makes no guarantees about thread safety.  It is the
 * responsibility of the listener implementations to ensure that concurrent
 * invocations of the {@link ProgressListener#reportProgress(String, Object, Long, Long)}
 * method do not conflict.</p> 
 * 
 * @see ProgressListener
 * @see gov.loc.repository.bagit.utilities.LongRunningOperationBase
 */
public interface ProgressListenable
{
	/**
	 * Adds a progress listener.
	 * 
	 * @param progressListener The listener to add.  Must not be null.
	 */
	void addProgressListener(ProgressListener progressListener);
	
	/**
	 * Removes a progress listener.  If the listener was not previously
	 * registered with {@link #addProgressListener(ProgressListener)},
	 * or has already been removed, then nothing happens.
	 * 
	 * @param progressListener The listener to remove.  Must not be null.
	 */
	void removeProgressListener(ProgressListener progressListener);
}
