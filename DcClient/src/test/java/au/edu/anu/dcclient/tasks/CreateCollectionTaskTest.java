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

package au.edu.anu.dcclient.tasks;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.core.UriBuilder;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.dcclient.Global;
import au.edu.anu.dcclient.collection.CollectionInfo;

public class CreateCollectionTaskTest extends AbstractDcBagTaskTest
{
	private static final Logger LOGGER = LoggerFactory.getLogger(CreateCollectionTaskTest.class);
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		AbstractDcBagTaskTest.setUpBeforeClass();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
		AbstractDcBagTaskTest.tearDownAfterClass();
	}

	@Before
	public void setUp() throws Exception
	{
		super.setUp();
	}

	@After
	public void tearDown() throws Exception
	{
		super.tearDown();
	}

	@Ignore
	public void testCreateCollectionTask() throws Exception
	{
		CollectionInfo ci = new CollectionInfo(new File(this.getClass().getResource("collinfotest.properties").toURI()));
		CreateCollectionTask createCollTask = new CreateCollectionTask(ci, Global.getCreateUri());
		String createdPid = createCollTask.call();
		LOGGER.info("Created Pid {}", createdPid);
		assertNotNull("Created object has a null pid", createdPid);
		assertNotSame(0, createdPid.length());
	}
	
}
