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

package au.edu.anu.datacommons.doi;

import java.util.List;
import java.util.Map.Entry;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * Runs a tracing server for testing. Accepts GET and POST requests and returns the request details in the response as a string. For use it in a JUnit test case
 * that extends JerseyTest:
 * 
 * <p>
 * <code>super("[PACKAGE NAME WHERE THIS CLASS IS LOCATED]");</code>
 * 
 * <p>Sample response:
 * 
 * <blockquote> Method:POST<br />
 * Path:test/index.html<br />
 * Header:content-length=7<br />
 * Header:content-type=application/x-www-form-urlencoded; charset=UTF-8<br />
 * Header:host=localhost:9998<br />
 * Header:connection=Keep-Alive<br />
 * Header:user-agent=Apache-HttpClient/4.2.1 (java 1.5)<br />
 * QueryParam:abc=xyz<br />
 * QueryParam:a1=b2<br />
 * FormData:abc=xyz<br />
 * <br />
 * </blockquote>
 * 
 */
@Path("{path:.*}")
public class TraceServer
{
	public static final String NEWLINE = System.getProperty("line.separator");

	@GET
	public Response doGet(@Context Request req, @Context UriInfo uri, @Context HttpHeaders headers)
	{
		Response resp;
		StringBuilder respStr = new StringBuilder();

		respStr.append(getMethod(req));
		respStr.append(getPath(uri));
		respStr.append(getHeaders(headers));
		respStr.append(getQueryParams(uri));

		resp = Response.ok(respStr.toString()).build();
		return resp;
	}

	@POST
	public Response doPost(@Context Request req, @Context UriInfo uri, @Context HttpHeaders headers, MultivaluedMap<String, String> formParams)
	{
		Response resp;
		StringBuilder respStr = new StringBuilder();

		respStr.append(getMethod(req));
		respStr.append(getPath(uri));
		respStr.append(getHeaders(headers));
		respStr.append(getQueryParams(uri));
		respStr.append(getParameters(formParams));

		resp = Response.ok(respStr.toString()).build();
		return resp;
	}

	private String getMethod(Request req)
	{
		StringBuilder strB = new StringBuilder();
		strB.append("Method:");
		strB.append(req.getMethod());
		strB.append(NEWLINE);
		return strB.toString();
	}

	private String getPath(UriInfo uri)
	{
		StringBuilder strB = new StringBuilder();
		strB.append("Path:");
		strB.append(uri.getPath());
		strB.append(NEWLINE);
		strB.append("AbsolutePath:");
		strB.append(uri.getAbsolutePath().toString());
		strB.append(NEWLINE);
		return strB.toString();
	}

	private String getHeaders(HttpHeaders headers)
	{
		StringBuilder strB = new StringBuilder();
		for (Entry<String, List<String>> entry : headers.getRequestHeaders().entrySet())
		{
			for (String val : entry.getValue())
			{
				strB.append("Header:");
				strB.append(entry.getKey());
				strB.append("=");
				strB.append(val);
				strB.append(NEWLINE);
			}
		}
		return strB.toString();
	}

	private String getQueryParams(UriInfo uriInfo)
	{
		StringBuilder strB = new StringBuilder();
		for (Entry<String, List<String>> entry : uriInfo.getQueryParameters().entrySet())
		{
			for (String val : entry.getValue())
			{
				strB.append("QueryParam:");
				strB.append(entry.getKey());
				strB.append("=");
				strB.append(val);
				strB.append(NEWLINE);
			}
		}
		return strB.toString();
	}

	private String getParameters(MultivaluedMap<String, String> formData)
	{
		StringBuilder strB = new StringBuilder();
		for (Entry<String, List<String>> entry : formData.entrySet())
		{
			for (String val : entry.getValue())
			{
				strB.append("FormData:");
				strB.append(entry.getKey());
				strB.append("=");
				strB.append(val);
				strB.append(NEWLINE);
			}
		}
		return strB.toString();
	}
}
