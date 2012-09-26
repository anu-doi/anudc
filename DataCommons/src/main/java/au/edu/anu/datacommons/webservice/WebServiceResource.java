package au.edu.anu.datacommons.webservice;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.text.MessageFormat;

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.yourmediashelf.fedora.client.FedoraClientException;

import au.edu.anu.datacommons.data.db.model.FedoraObject;
import au.edu.anu.datacommons.data.fedora.FedoraBroker;
import au.edu.anu.datacommons.security.service.FedoraObjectService;
import au.edu.anu.datacommons.storage.DcStorage;
import au.edu.anu.datacommons.storage.DcStorageException;
import au.edu.anu.datacommons.util.Constants;
import au.edu.anu.datacommons.util.Util;
import au.edu.anu.datacommons.webservice.bindings.Activity;
import au.edu.anu.datacommons.webservice.bindings.Request;

@Component
@Scope("request")
@Path("/ws")
public class WebServiceResource
{
	private static final Logger LOGGER = LoggerFactory.getLogger(WebServiceResource.class);
	private static final DcStorage dcStorage = DcStorage.getInstance();

	private static final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	private static DocumentBuilder docBuilder;

	private static JAXBContext context;
	private static Unmarshaller um;

	@Resource(name = "fedoraObjectServiceImpl")
	private FedoraObjectService fedoraObjectService;

	static
	{
		docBuilderFactory.setNamespaceAware(true);
		try
		{
			docBuilder = docBuilderFactory.newDocumentBuilder();
		}
		catch (ParserConfigurationException e)
		{
			// This should never happen as we're using default Document Builder Factory. If it does, then NPE is to be expected.
			LOGGER.error(e.getMessage(), e);
			docBuilder = null;
		}

		try
		{
			ClassLoader cl = WebServiceResource.class.getClassLoader();
			context = JAXBContext.newInstance("au.edu.anu.datacommons.webservice.bindings", cl);
			um = context.createUnmarshaller();
		}
		catch (JAXBException e)
		{
			LOGGER.error(e.getMessage(), e);
			context = null;
			um = null;
		}

	}

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

	/*
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
	*/

	@POST
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	@PreAuthorize("hasAnyRole('ROLE_ANU_USER', 'ROLE_REGISTERED')")
	public Response doPostAsXml(Document xmlDoc)
	{
		Response resp = null;
		Document respDoc = docBuilder.newDocument();
		Element respRootElement = respDoc.createElement("response");
		respDoc.appendChild(respRootElement);

		LOGGER.trace(getStringFromDoc(xmlDoc));
		Request req;
		try
		{
			req = (Request) um.unmarshal(xmlDoc);

			if (req.getActivity() != null)
			{
				Activity activity = req.getActivity();
				if (activity.getPid() == null)
				{
					// If Pid doesn't exist, create the object.
					FedoraObject fo = fedoraObjectService.saveNew(req.getActivity());
					String pidCreated = fo.getObject_id();
					
					// Create response element.
					respRootElement.appendChild(createElement(respDoc, "status", pidCreated, new String[] {"action", "created"}));
				}
				else
				{
					if (activity.generateDataMap().size() > 0)
					{
						// Pid exists, update the object.
						FedoraObject fo = fedoraObjectService.saveEdit(activity);
						respRootElement.appendChild(createElement(respDoc, "status", fo.getObject_id(), new String[] {"action", "updated"}));
					}
					else
					{
						// No fields specified, return object details.
						InputStream dataStream = null;
						try
						{
							dataStream = FedoraBroker.getDatastreamAsStream(activity.getPid(), Constants.XML_SOURCE);
							respDoc = docBuilder.parse(dataStream);
						}
						finally
						{
							IOUtils.closeQuietly(dataStream);
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			respRootElement.appendChild(createElement(respDoc, "error", e.getMessage()));
		}

		resp = Response.ok(respDoc).build();

		return resp;
	}
	
	/**
	 * Creates an element with a tagName, textContent and pairs of attribute key and value pairs. For example,
	 * 
	 * <p>
	 * <code>
	 * createElement(doc, "abc", "xyz", new String[] {"key1", "val1"}, new String[] {"key2", "val2"});
	 * </code>
	 * 
	 * <p>
	 * creates
	 * 
	 * <p>
	 * &lt;abc "key1"="val1" "key2"="val2"&gt;xyz&lt;/abc&gt;
	 * 
	 * @param doc
	 *            Document the element belongs to.
	 * @param tagName
	 *            Tagname of the element.
	 * @param textContent
	 *            Text contents of the element
	 * @param attrs
	 *            Key-value pairs of attributes as String[] where index 0 is key, 1 is value.
	 * @return The created Element object.
	 */
	private Element createElement(Document doc, String tagName, String textContent, String[]... attrs)
	{
		Element el = doc.createElement(tagName);
		el.setTextContent(textContent);
		for (String[] attr : attrs)
			el.setAttribute(attr[0], attr[1]);
		
		return el;
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

	private String getStringFromDoc(Document doc)
	{
		StringWriter writer = new StringWriter();
		try
		{
			DOMSource domSource = new DOMSource(doc);
			StreamResult result = new StreamResult(writer);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.transform(domSource, result);
			writer.flush();
		}
		catch (TransformerException ex)
		{
			ex.printStackTrace();
			return null;
		}

		return writer.toString();
	}

}
