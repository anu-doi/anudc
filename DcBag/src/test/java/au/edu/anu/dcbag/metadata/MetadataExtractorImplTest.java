package au.edu.anu.dcbag.metadata;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.dcbag.fido.FidoParserTest;

public class MetadataExtractorImplTest
{
	private static final Logger LOGGER = LoggerFactory.getLogger(MetadataExtractorImplTest.class);
	
	private MetadataExtractor mdExtractor;
	
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
		mdExtractor = new MetadataExtractorImpl(new File(FidoParserTest.class.getResource("BagIt Specification.pdf").toURI())); 
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public void testGetMetadataMap()
	{
		Map<String, String[]> mdMap = mdExtractor.getMetadataMap();
		LOGGER.info("Attributes count: {}", mdMap.size());
		assertEquals(mdMap.size(), 26);
		assertTrue(mdMap.get("dc:title")[0].equals("BagIt File Packaging Format v 0.97"));
	}

}
