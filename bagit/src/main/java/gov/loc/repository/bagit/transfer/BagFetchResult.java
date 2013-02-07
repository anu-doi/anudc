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

import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.utilities.SimpleResult;

public class BagFetchResult extends SimpleResult
{
    private Bag resultingBag;
    
    public Bag getResultingBag()
    {
        return this.resultingBag;
    }
    
    public void setResultingBag(Bag bag)
    {
        this.resultingBag = bag;
    }
        
    public BagFetchResult(boolean isSuccess)
    {
        super(isSuccess);
    }

    public BagFetchResult(boolean isSuccess, String message)
    {
        super(isSuccess, message);
    }
}
