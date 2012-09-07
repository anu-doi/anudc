package au.edu.anu.datacommons.webservice;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import au.edu.anu.datacommons.storage.DcStorage;
import au.edu.anu.datacommons.storage.DcStorageException;
import au.edu.anu.datacommons.util.Util;

@Path("/ws")
public class WebServiceResource
{
	private static final Logger LOGGER = LoggerFactory.getLogger(WebServiceResource.class);
	private static final DcStorage dcStorage = DcStorage.getInstance();
	
	@GET
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public Response doGetAsXml()
	{
		Response resp = null;
		
		// TODO Implement method.
		
		resp = Response.ok("<?xml version=\"1.0\"?>" + "<SomeXmlTag>Hello World" + "</SomeXmlTag>", MediaType.APPLICATION_XML_TYPE).build();
		return resp;
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public Response doPostAsXml(Document xmlDoc)
	{
		String xmlResp = "<?xml version=\"1.0\"?>\r\n" + "<dcresponse status=\"Item Created\"><pid>test:1</pid></dcresponse>";
		LOGGER.info("\r\n" + Util.getXmlAsString(xmlDoc));
		Document respDoc;
		Response resp = null;
		try
		{
			respDoc = xmlAsDoc(xmlResp);
			String pid = xmlDoc.getElementsByTagName("dcrequest").item(0).getAttributes().getNamedItem("pid").getNodeValue();
			NodeList filesToDownload = xmlDoc.getElementsByTagName("file");
			for (int i = 0; i < filesToDownload.getLength(); i++)
			{
				// Extract the details required from the XML doc.
				String fileUrl = filesToDownload.item(i).getTextContent();
				String filename = filesToDownload.item(i).getAttributes().getNamedItem("name").getNodeValue();
				dcStorage.addFileToBag(pid, filename, fileUrl);
			}
			resp = Response.ok(respDoc, MediaType.APPLICATION_XML_TYPE).build();
		}
		catch (ParserConfigurationException e)
		{
			LOGGER.error(e.getMessage(), e);
			resp = Response.serverError().build();
		}
		catch (SAXException e)
		{
			LOGGER.error(e.getMessage(), e);
			resp = Response.serverError().build();
		}
		catch (IOException e)
		{
			LOGGER.error(e.getMessage(), e);
			resp = Response.serverError().build();
		}
		catch (DcStorageException e)
		{
			LOGGER.error(e.getMessage(), e);
			resp = Response.serverError().build();
		}
		
		return resp;
	}
	
	private Document xmlAsDoc(String xmlStr) throws ParserConfigurationException, SAXException, IOException
	{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		DocumentBuilder db;
		Document doc = null;
		try
		{
			db = dbf.newDocumentBuilder();
			doc = db.parse(new ByteArrayInputStream(xmlStr.getBytes()));
		}
		catch (ParserConfigurationException e)
		{
			LOGGER.error(e.getMessage(), e);
			throw e;
		}
		catch (SAXException e)
		{
			LOGGER.error(e.getMessage(), e);
			throw e;
		}
		catch (IOException e)
		{
			LOGGER.error(e.getMessage(), e);
			throw e;
		}
		return doc;
	}
}
