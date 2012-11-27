package au.edu.anu.dcbag.fido;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.dcbag.fido.PronomFormat.MatchStatus;

public class FidoParserTest
{
	private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getClass());
	
	private static File sampleFile;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		sampleFile = new File(FidoParserTest.class.getResource("BagIt Specification.pdf").toURI());
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
	public void testGetFileFormatFromInputStream()
	{
		try
		{
			FidoParser fidoParser = new FidoParser(FidoParserTest.class.getResourceAsStream("BagIt Specification.pdf"));
			PronomFormat fileFormat = fidoParser.getFileFormat();
			assertTrue(fileFormat.getMatchStatus().equals(MatchStatus.OK));
			assertTrue(fileFormat.getPuid().equals("fmt/20"));
		}
		catch (IOException e)
		{
			failOnException(e);
		}
	}
	
	private void failOnException(Throwable e)
	{
		LOGGER.error(e.getMessage(), e);
		fail(e.getMessage());
	}
}
