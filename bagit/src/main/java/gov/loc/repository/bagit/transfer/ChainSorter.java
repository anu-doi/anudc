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

import java.util.Comparator;

/**
 * Compares to objects by delegating to a chain of sub-comparisons.
 * This is useful for implementing secondary or tertiary sorting
 * of objects.  If no sub-comparisons are specified, the
 * {@link #compare(Object, Object) compare} method will default to
 * always returning 0.
 * 
 * @author Brian Vargas
 * @param <T> The type to be compared by the sub-comparisons.
 */
class ChainSorter<T> extends Object implements Comparator<T>
{
	private Comparator<T>[] comparators;
		
	public ChainSorter(Comparator<T>... sorters)
	{
		this.comparators = sorters;
	}
	
    @Override
    public int compare(T left, T right)
    {
    	int result = 0;
    	
    	for (int i = 0; i < this.comparators.length; i++)
    	{
    		result = this.comparators[i].compare(left, right);
    		
    		if (result != 0)
    			break;
    	}
    	
    	return result;
    }
}
