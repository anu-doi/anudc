package au.edu.anu.datacommons.search;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;

import au.edu.anu.datacommons.properties.GlobalProps;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class RiSearchRequest
{
	private WebResource riSearchService;
	private SparqlQuery sparqlQuery;
	private Language paramLang = null;
	private Format paramFormat = null;
	private Type paramType = null;
	private int paramLimit = -1;
	private boolean paramDistinct = false;	// Force Distinct
	private boolean paramDt = false;		// Fake Media-Types
	private boolean paramStream = false;	// Stream immediately.

	public enum Language
	{
		ITQL
		{
			public String toString()
			{
				return "itql";
			}
		},

		SPARQL
		{
			public String toString()
			{
				return "sparql";
			}
		}
	}

	public enum Format
	{
		CSV
		{
			public String toString()
			{
				return "CSV";
			}
		},

		SIMPLE
		{
			public String toString()
			{
				return "Simple";
			}
		},

		SPARQL
		{
			public String toString()
			{
				return "Sparql";
			}
		},

		TSV
		{
			public String toString()
			{
				return "TSV";
			}
		},

		JSON
		{
			public String toString()
			{
				return "json";
			}
		},

		COUNT
		{
			public String toString()
			{
				return "count";
			}
		},

		COUNTJSON
		{
			public String toString()
			{
				return "count/json";
			}
		},

		COUNTSPARQL
		{
			public String toString()
			{
				return "count/Sparql";
			}
		}
	}

	public enum Type
	{
		TUPLES
		{
			public String toString()
			{
				return "tuples";
			}
		},

		TRIPLES
		{
			public String toString()
			{
				return "triples";
			}
		}
	}

	public RiSearchRequest()
	{
		// Set default params
		paramType = Type.TUPLES;
		paramLang = Language.SPARQL;
		paramFormat = Format.SPARQL;
	}

	private MultivaluedMap<String, String> generateParamMap()
	{
		MultivaluedMap<String, String> paramMap = new MultivaluedMapImpl();

		if (paramLang != null)
			paramMap.add("lang", paramLang.toString());

		if (paramFormat != null)
			paramMap.add("format", paramFormat.toString());

		if (paramType != null)
			paramMap.add("type", paramType.toString());

		if (paramLimit > 0)
			paramMap.add("limit", String.valueOf(paramLimit));

		if (paramDistinct == true)
			paramMap.add("distinct", "on");

		if (paramDt == true)
			paramMap.add("dt", "on");

		if (paramStream == true)
			paramMap.add("stream", "on");

		paramMap.add("query", sparqlQuery.generateQuery());

		return paramMap;
	}

	public ClientResponse execute(String sparqlQueryStr)
	{
		Client client = Client.create(new DefaultClientConfig());
		client.addFilter(new HTTPBasicAuthFilter(GlobalProps.getProperty(GlobalProps.PROP_FEDORA_USERNAME), GlobalProps
				.getProperty(GlobalProps.PROP_FEDORA_PASSWORD)));

		riSearchService = client.resource(UriBuilder.fromUri(GlobalProps.getProperty(GlobalProps.PROP_FEDORA_URI))
				.path(GlobalProps.getProperty(GlobalProps.PROP_FEDORA_RISEARCHURL)).build());

		riSearchService = riSearchService.queryParams(generateParamMap());
		ClientResponse respFromRiSearch = riSearchService.type(MediaType.APPLICATION_FORM_URLENCODED).accept(MediaType.TEXT_XML)
				.post(ClientResponse.class);

		return respFromRiSearch;
	}
}
