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

import gov.loc.repository.bagit.FetchTxt;
import gov.loc.repository.bagit.FetchTxt.FilenameSizeUrl;

import java.util.Comparator;

class FetchSizeSorter extends Object implements Comparator<FetchTxt.FilenameSizeUrl>
{
    @Override
    public int compare(FilenameSizeUrl left, FilenameSizeUrl right)
    {
        Long leftSize = left.getSize();
        Long rightSize = right.getSize();
        int result;
        
        if (leftSize == null)
        {
            if (rightSize == null)
                result = 0;
            else
                result = -1;
        }
        else
        {
            if (rightSize == null)
                result = 1;
            else
                result = leftSize.compareTo(rightSize);
        }
        
        return result;
    }
}
