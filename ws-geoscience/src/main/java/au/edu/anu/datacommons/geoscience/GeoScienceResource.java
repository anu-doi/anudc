/*******************************************************************************
 * Australian National University Data Commons
 * Copyright (C) 2013  The Australian National University
 * 
 * This file is part of Australian National University Data Commons.
 * 
 * Australian National University Data Commons is free software: you
 * can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

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
