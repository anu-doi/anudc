package au.edu.anu.dcclient.explorer;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class FileCopierTest
{

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

	@Test
	public void testCall()
	{
		File source = new File("C:\\Rahul\\Temp\\TestColl");
		File target = new File("C:\\Dir");
		try
		{
			FileCopier fc = new FileCopier(source, target, false);
			long begin = System.currentTimeMillis();
			Runnable indicateActivity = new Runnable() {
				private boolean isCancelled = false;
				
				@Override
				public void run()
				{
					while (!isCancelled)
					{
						System.out.print('.');
						try
						{
							Thread.sleep(3000);
						}
						catch (InterruptedException e)
						{
						}
					}
				}
			};
			new Thread(indicateActivity).start();
			fc.call();
			long timeElapsed = System.currentTimeMillis() - begin;
			System.out.println();
//			System.out.println(MessageFormat.format("Time elapsed: {0,number,integer} sec. Avg {1,number,integer} MB per sec.", timeElapsed / 1000, source.length()
//					/ 1024L / 1024L / (timeElapsed / 1000)));
		}
		catch (IOException e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}
