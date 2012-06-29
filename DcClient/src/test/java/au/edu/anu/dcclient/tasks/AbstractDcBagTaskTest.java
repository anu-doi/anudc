package au.edu.anu.dcclient.tasks;

import static org.junit.Assert.*;
import gov.loc.repository.bagit.ProgressListener;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractDcBagTaskTest
{
	private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getClass());
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
	}

	@Before
	public void setUp() throws Exception
	{
	}

	@After
	public void tearDown() throws Exception
	{
	}

	protected ProgressListener getProgressListener()
	{
		return new ProgressListener()
		{
			@Override
			public void reportProgress(String activity, Object item, Long count, Long total)
			{
				LOGGER.info("Progress: Activity '{}', item '{}', count {}, total {}.", new Object[] { activity, item, count, total });
			}
		};
	}
}
