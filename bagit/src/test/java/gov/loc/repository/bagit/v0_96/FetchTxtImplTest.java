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

package gov.loc.repository.bagit.v0_96;

import java.io.ByteArrayInputStream;

import gov.loc.repository.bagit.BagFactory;
import gov.loc.repository.bagit.FetchTxt;
import gov.loc.repository.bagit.FetchTxtReader;
import gov.loc.repository.bagit.Bag.BagPartFactory;
import gov.loc.repository.bagit.BagFactory.Version;
import gov.loc.repository.bagit.impl.FetchTxtReaderImpl;

import org.junit.Test;
import static org.junit.Assert.*;

public class FetchTxtImplTest
{
	BagFactory bagFactory = new BagFactory();
	
    @Test
    public void testCorrectlyReadsFetch() throws Exception
    {
        BagPartFactory factory = this.bagFactory.getBagPartFactory(Version.V0_96);
        FetchTxtReader reader = factory.createFetchTxtReader(new ByteArrayInputStream("http://localhost:8989/bags/v0_96/holey-bag/data/dir1/test3.txt - data/dir1/test3.txt".getBytes("UTF-8")), "UTF-8");
        
        assertTrue(reader.hasNext());
        FetchTxt.FilenameSizeUrl line = reader.next();
        assertFalse(reader.hasNext());
        
        assertEquals("http://localhost:8989/bags/v0_96/holey-bag/data/dir1/test3.txt", line.getUrl());
        assertNull(line.getSize());
        assertEquals("data/dir1/test3.txt", line.getFilename());
    }
    
    @Test
    public void testLeavesWhitespaceInFilename()
    {
        ByteArrayInputStream in = new ByteArrayInputStream("http://example.org - data/this is a filename with spaces".getBytes());
        FetchTxtReaderImpl reader = new FetchTxtReaderImpl(in, "UTF-8");
        
        assertTrue(reader.hasNext());
        FetchTxt.FilenameSizeUrl line = reader.next();
        assertFalse(reader.hasNext());
        
        assertEquals("http://example.org", line.getUrl());
        assertNull(line.getSize());
        assertEquals("data/this is a filename with spaces", line.getFilename());
    }
}
