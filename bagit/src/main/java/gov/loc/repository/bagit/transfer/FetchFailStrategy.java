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
 * Represents a strategy for failing a fetch, based on some
 * implementation-defined criteria.  Implementors must
 * return a {@link FetchFailureAction} value that determines
 * the action that the caller should take in response to
 * the registered failure.
 * 
 * <p>Implementations of this interface <strong>must be
 * thread-safe</strong>.</p>
 * 
 * <p>Fetch failures are used by the the
 * {@link BagFetcher#setFetchFailStrategy(FetchFailStrategy) setFetchFailStrategy}
 * method of the <c>BagFetcher</c>.  Some common strategies are
 * the {@link StandardFailStrategies#ALWAYS_CONTINUE ALWAYS_CONTINUE},
 * {@link StandardFailStrategies#ALWAYS_RETRY ALWAYS_RETRY},
 * and {@link StandardFailStrategies#FAIL_FAST FAIL_FAST} strategies.
 * A more complex strategy might involve the
 * {@link ThresholdFailStrategy}.</p>
 * 
 * @version $id$
 * @see FetchFailureAction
 * @see StandardFailStrategies
 * @see ThresholdFailStrategy
 * @see BagFetcher
 */
public interface FetchFailStrategy
{
	FetchFailureAction registerFailure(FetchTarget target, Object context);
}
