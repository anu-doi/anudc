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

package au.edu.anu.datacommons.phenomics;


import java.io.File;
import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import au.edu.anu.datacommons.config.Config;
import au.edu.anu.datacommons.config.PropertiesFile;
import au.edu.anu.datacommons.webservice.AbstractResource;

@Path("/")
public class PhenomicsResource extends AbstractResource
{
	public static final Logger LOGGER = LoggerFactory.getLogger(PhenomicsResource.class);
	
	public PhenomicsResource()
	{
		try
		{
			genericWsProps = new PropertiesFile(new File(Config.DIR, "ws-phenomics/genericws.properties"));
			packageLookup = new PropertiesFile(new File(Config.DIR, "ws-phenomics/wslookup.properties"));
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
		resp = Response.ok("Test", MediaType.TEXT_PLAIN_TYPE).build();
		return resp;
	}

	@Override
	protected Element processRespElement(Element statusElementFromGenSvc)
	{
		NodeList extIdElements = statusElementFromGenSvc.getElementsByTagName("externalId");
		if (extIdElements.getLength() > 0)
			statusElementFromGenSvc.setAttribute("phid", extIdElements.item(0).getTextContent());
		return statusElementFromGenSvc;
	}
}
