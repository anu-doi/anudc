package au.edu.anu.datacommons.geoscience;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import au.edu.anu.datacommons.config.Config;
import au.edu.anu.datacommons.config.PropertiesFile;
import au.edu.anu.datacommons.webservice.AbstractResource;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.api.client.filter.LoggingFilter;

@Path("/")
public class GeoScienceResource extends AbstractResource
{
	private static final Logger LOGGER = LoggerFactory.getLogger(GeoScienceResource.class);

	public GeoScienceResource()
	{
		try
		{
			genericWsProps = new PropertiesFile(new File(Config.DIR, "ws-geoscience/genericws.properties"));
			packageLookup = new PropertiesFile(new File(Config.DIR, "ws-geoscience/wslookup.properties"));
		}
		catch (IOException e)
		{
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public Response doGetRequest()
	{
		Response resp = null;
		StringBuilder respStr = new StringBuilder();
		respStr.append("Test - GeoScience");
		resp = Response.ok(respStr.toString()).build();
		return resp;
	}

	@Override
	protected Element processRespElement(Element statusElementFromGenSvc)
	{
		NodeList extIdElements = statusElementFromGenSvc.getElementsByTagName("externalId");
		if (extIdElements.getLength() > 0)
			statusElementFromGenSvc.setAttribute("geoid", extIdElements.item(0).getTextContent());
		return statusElementFromGenSvc;
	}
}
