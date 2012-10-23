package au.edu.anu.datacommons.webservice;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.text.MessageFormat;
import java.util.List;

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
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
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.yourmediashelf.fedora.client.FedoraClientException;

import au.edu.anu.datacommons.data.db.dao.FedoraObjectDAOImpl;
import au.edu.anu.datacommons.data.db.model.FedoraObject;
import au.edu.anu.datacommons.data.fedora.FedoraBroker;
import au.edu.anu.datacommons.security.CustomUser;
import au.edu.anu.datacommons.security.cas.ANUUserDetailsService;
import au.edu.anu.datacommons.security.service.FedoraObjectService;
import au.edu.anu.datacommons.storage.DcStorage;
import au.edu.anu.datacommons.storage.DcStorageException;
import au.edu.anu.datacommons.util.Constants;
import au.edu.anu.datacommons.util.Util;
import au.edu.anu.datacommons.webservice.bindings.Activity;
import au.edu.anu.datacommons.webservice.bindings.Collection;
import au.edu.anu.datacommons.webservice.bindings.DcRequest;
import au.edu.anu.datacommons.webservice.bindings.FedoraItem;
import au.edu.anu.datacommons.webservice.bindings.Link;

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
	
	@Context
	private UriInfo uriInfo;
	@Context
	private Request request;
	@Context
	private HttpHeaders httpHeaders;

	@GET
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.TEXT_PLAIN)
	public Response doGetAsXml()
	{
		Response resp = null;

		// TODO Implement method.
		
		resp = Response.ok("Test - Phenomics", MediaType.TEXT_PLAIN_TYPE).build();
		return resp;
	}

	@POST
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	@PreAuthorize("hasAnyRole('ROLE_ANU_USER', 'ROLE_REGISTERED')")
	public Response doPostAsXml(Document xmlDoc)
	{
		Response resp = null;
		Document respDoc = docBuilder.newDocument();
		respDoc.appendChild(respDoc.createElement("response"));

		LOGGER.trace(getStringFromDoc(xmlDoc));
		DcRequest req;
		try
		{
			req = (DcRequest) um.unmarshal(xmlDoc);

			FedoraItem item = req.getFedoraItem();
			
			if (item.getPid() == null)
			{
				// Create
				Element el = createItem(item, respDoc);
				respDoc.getDocumentElement().appendChild(el);
			}
			else if (item.generateDataMap().size() > 0)
			{
				// Update
				Element el = updateItem(item, respDoc);
				respDoc.getDocumentElement().appendChild(el);
			}
			else
			{
				// Query
				Element el = getDatastream(item, respDoc);
				respDoc.getDocumentElement().appendChild(el);
			}
			
			// If collection, add files - download or byRef.
			if (item instanceof Collection && req.getCollection().getFileUrlList() != null)
			{
				List<Link> fileUrlList = req.getCollection().getFileUrlList();
				for (Link iLink : fileUrlList)
				{
					if (iLink.isRefOnly() == null || iLink.isRefOnly() == Boolean.FALSE)
					{
						// Download file hosted at url to bag.
						dcStorage.addFileToBag(item.getPid(), iLink.getFilename(), iLink.getUrl());
					}
					else
					{
						// Refer to the url.
						dcStorage.addExtRef(item.getPid(), iLink.getUrl());
					}
				}
			}

			resp = Response.ok(respDoc).build();
		}
		catch (Exception e)
		{
			respDoc.getDocumentElement().appendChild(createElement(respDoc, "error", e.toString()));
			resp = Response.serverError().entity(e.toString()).build();
		}

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

	@PostAuthorize("hasPermission(returnObject, 'READ')")
	private FedoraObject getFedoraObjectReadAccess(String pid)
	{
		return getFedoraObject(pid);
	}

	@PostAuthorize("hasPermission(returnObject, 'WRITE')")
	private FedoraObject getFedoraObjectWriteAccess(String pid)
	{
		return getFedoraObject(pid);
	}

	private FedoraObject getFedoraObject(String pid)
	{
		LOGGER.debug("Retrieving object for: {}", pid);
		String decodedpid = null;
		decodedpid = Util.decodeUrlEncoded(pid);
		if (decodedpid == null)
		{
			return null;
		}
		LOGGER.debug("Decoded pid: {}", decodedpid);
		FedoraObjectDAOImpl object = new FedoraObjectDAOImpl(FedoraObject.class);
		FedoraObject fo = (FedoraObject) object.getSingleByName(decodedpid);
		return fo;
	}
	
	private Element createItem(FedoraItem item, Document doc) throws FedoraClientException, JAXBException
	{
		FedoraObject fo = fedoraObjectService.saveNew(item);
		String pidCreated = fo.getObject_id();
		item.setPid(pidCreated);
		
		InputStream dataStream = null;
		Element el = createElement(doc, "status", "", new String[] { "action", "created" }, new String[] { "pid", pidCreated }, new String[] {"type", item.getType()});
		try
		{
			dataStream = FedoraBroker.getDatastreamAsStream(pidCreated, Constants.XML_SOURCE);
			el.appendChild(doc.adoptNode(docBuilder.parse(dataStream).getDocumentElement()));
		}
		catch (FedoraClientException e)
		{
			// Catching exception when datastream can't be read instead of throwing it as the item's been created.
			LOGGER.warn(e.getMessage(), e);
		}
		catch (DOMException e)
		{
			LOGGER.warn(e.getMessage(), e);
		}
		catch (SAXException e)
		{
			LOGGER.warn(e.getMessage(), e);
		}
		catch (IOException e)
		{
			LOGGER.warn(e.getMessage(), e);
		}
		finally
		{
			IOUtils.closeQuietly(dataStream);
		}
		
		return el;
	}
	
	private Element updateItem(FedoraItem item, Document doc) throws FedoraClientException, JAXBException
	{
		getFedoraObjectWriteAccess(item.getPid());
		FedoraObject fo = fedoraObjectService.saveEdit(item);
		Element el = createElement(doc, "status", "", new String[] { "action", "updated" }, new String[] { "pid", fo.getObject_id() });
		InputStream dataStream = null;
		try
		{
			dataStream = FedoraBroker.getDatastreamAsStream(fo.getObject_id(), Constants.XML_SOURCE);
			el.appendChild(doc.adoptNode(docBuilder.parse(dataStream).getDocumentElement()));
		}
		catch (FedoraClientException e)
		{
			// Catching exception when datastream can't be read instead of throwing it as the item's been updated.
			LOGGER.warn(e.getMessage(), e);
		}
		catch (DOMException e)
		{
			LOGGER.warn(e.getMessage(), e);
		}
		catch (SAXException e)
		{
			LOGGER.warn(e.getMessage(), e);
		}
		catch (IOException e)
		{
			LOGGER.warn(e.getMessage(), e);
		}
		finally
		{
			IOUtils.closeQuietly(dataStream);
		}
		return el;
	}
	
	private Element getDatastream(FedoraItem item, Document doc) throws FedoraClientException, DOMException, SAXException, IOException
	{
		getFedoraObjectReadAccess(item.getPid());
		InputStream dataStream = null;
		Element el = createElement(doc, "status", "", new String[] {"action", "query"}, new String[] {"pid", item.getPid() });
		try
		{
			dataStream = FedoraBroker.getDatastreamAsStream(item.getPid(), Constants.XML_SOURCE);
			el.appendChild(doc.adoptNode(docBuilder.parse(dataStream).getDocumentElement()));
		}
		finally
		{
			IOUtils.closeQuietly(dataStream);
		}
		return el;
	}
}
