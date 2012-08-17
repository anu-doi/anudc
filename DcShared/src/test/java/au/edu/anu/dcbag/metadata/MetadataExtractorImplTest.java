package au.edu.anu.dcbag.metadata;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.tika.exception.TikaException;
import org.apache.tika.xmp.XMPMetadata;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.adobe.xmp.XMPMeta;
import com.adobe.xmp.impl.XMPMetaImpl;

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

	@Test
	public void testGetXmpMetadata()
	{
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		Document doc = null;
		org.apache.jempbox.xmp.XMPMetadata xmp = null;
		try
		{
			// Get the metadata
			XMPMetadata xmpMd = mdExtractor.getXmpMetadata();
			String xmpXml = xmpMd.toString(); 
			LOGGER.info("\r\n" + xmpXml);

			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(new InputSource(new StringReader(xmpXml)));
			xmp = new org.apache.jempbox.xmp.XMPMetadata(doc);
			LOGGER.info("Title: {}", xmp.getDublinCoreSchema().getTitle());
			assertEquals("Title does not match", "BagIt File Packaging Format v 0.97", xmp.getDublinCoreSchema().getTitle());
		}
		catch (TikaException e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
		catch (ParserConfigurationException e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
		catch (SAXException e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
		catch (IOException e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
		
	}
}
