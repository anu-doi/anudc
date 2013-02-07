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

package gov.loc.repository.bagit.utilities;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ThreadSafeIteratorWrapper<E> implements Iterator<E>, Iterable<E> {
    private E nextItem;
    private Iterator<E> iterator;
    
	public ThreadSafeIteratorWrapper(Iterator<E> iterator)
    {
        this.iterator = iterator;
    }
    
    @Override
    public Iterator<E> iterator()
    {
        return this;
    }
    
    @Override
    public boolean hasNext()
    {
        synchronized (this)
        {
            synchronized (this.iterator)
            {
                boolean hasNext = this.iterator.hasNext();
                
                if (hasNext)
                    this.nextItem = this.iterator.next();
                else
                    this.nextItem = null;
                
                return hasNext;
            }
        }
    }

    @Override
    public E next()
    {
        synchronized (this)
        {
            if (this.nextItem == null)
                throw new NoSuchElementException();
            E tmp = this.nextItem;
            this.nextItem = null;
            return tmp;
        }
    }
    
    @Override
    public void remove()
    {
        throw new UnsupportedOperationException();
    }
    
}
