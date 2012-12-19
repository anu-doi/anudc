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
	public void testGetFileFormatFromInputStream()
	{
		try
		{
			FidoParser fidoParser = new FidoParser(FidoParserTest.class.getResourceAsStream("BagIt Specification.pdf"));
			PronomFormat fileFormat = fidoParser.getFileFormat();
			matchValues(fileFormat);
		}
		catch (IOException e)
		{
			failOnException(e);
		}
	}
	
	@Test
	public void testGetFileFormatFromFile()
	{
		try
		{
			FidoParser fidoParser = new FidoParser(new File(FidoParserTest.class.getResource("BagIt Specification.pdf").toURI()));
			PronomFormat fileFormat = fidoParser.getFileFormat();
			matchValues(fileFormat);
		}
		catch (IOException e)
		{
			failOnException(e);
		}
		catch (URISyntaxException e)
		{
			failOnException(e);
		}
	}
	
	private void matchValues(PronomFormat format)
	{
		assertEquals(MatchStatus.OK, format.getMatchStatus());
		assertEquals("fmt/20", format.getPuid());
		assertEquals("application/pdf", format.getMimeType());
		assertEquals(63647L, format.getFileSize());
		assertEquals("PDF 1.6", format.getSigName());
		assertEquals("Acrobat PDF 1.6 - Portable Document Format", format.getFormatName());
	}
	
	private void failOnException(Throwable e)
	{
		LOGGER.error(e.getMessage(), e);
		fail(e.getMessage());
	}
}
