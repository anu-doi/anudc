package au.edu.anu.datacommons.search;

import java.net.URI;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;

import au.edu.anu.datacommons.properties.GlobalProps;
import au.edu.anu.datacommons.util.Util;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * RiSearchRequest
 * 
 * Australian National University Data Commons
 * 
 * Creates a request to be submitted to the Resource Index Search web service of Fedora Repository.
 * 
 * <pre>
 * Version	Date		Developer			Description
 * 0.1		08/05/2012	Rahul Khanna (RK)	Initial
 * </pre>
 */
public class RiSearchRequest
{
	private static final URI riSearchUri = UriBuilder.fromUri(GlobalProps.getProperty(GlobalProps.PROP_FEDORA_URI))
			.path(GlobalProps.getProperty(GlobalProps.PROP_FEDORA_RISEARCHURL)).build();
	private static final Client client = Client.create(new DefaultClientConfig());

	static
	{
		client.addFilter(new HTTPBasicAuthFilter(GlobalProps.getProperty(GlobalProps.PROP_FEDORA_USERNAME), GlobalProps
				.getProperty(GlobalProps.PROP_FEDORA_PASSWORD)));
	}

	private String paramQuery;				// The query to be executed.
	private Language paramLang = null;		// Language of the input query.
	private Format paramFormat = null;		// Format of the resultset.
	private Type paramType = null;			// The type of resultset required - tuples or triples.
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

	/**
	 * RiSearchRequest
	 * 
	 * Australian National University Data Commons
	 * 
	 * Constructor for RiSearchRequest
	 * 
	 * @param type
	 *            Type of results.
	 * @param lang
	 *            Language of input query.
	 * @param format
	 *            Format of query output.
	 * @param query
	 *            Query to be executed.
	 */
	public RiSearchRequest(Type type, Language lang, Format format, String query)
	{
		// Set default params
		this.paramType = type;
		this.paramLang = lang;
		this.paramFormat = format;
		this.paramQuery = query;
	}

	/**
	 * 
	 * getParamQuery
	 * 
	 * Australian National University Data Commons
	 * 
	 * Getter for paramQuery.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		08/05/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @return paramQuery as String.
	 */
	public String getParamQuery()
	{
		return paramQuery;
	}

	/**
	 * setParamQuery
	 * 
	 * Australian National University Data Commons
	 * 
	 * Setter for paramQuery.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		08/05/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @param paramQuery
	 *            paramQuery as String.
	 */
	public void setParamQuery(String paramQuery)
	{
		this.paramQuery = paramQuery;
	}

	/**
	 * getParamLang
	 * 
	 * Australian National University Data Commons
	 * 
	 * Getter for paramLang.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		08/05/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @return paramLang as Language.
	 */
	public Language getParamLang()
	{
		return paramLang;
	}

	/**
	 * setParamLang
	 * 
	 * Australian National University Data Commons
	 * 
	 * Setter for paramLang.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		08/05/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @param paramLang
	 *            paramLang as Language.
	 */
	public void setParamLang(Language paramLang)
	{
		this.paramLang = paramLang;
	}

	/**
	 * getParamFormat
	 * 
	 * Australian National University Data Commons
	 * 
	 * Getter for paramFormat.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		08/05/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @return paramFormat as Format.
	 */
	public Format getParamFormat()
	{
		return paramFormat;
	}

	/**
	 * setParamFormat
	 * 
	 * Australian National University Data Commons
	 * 
	 * Setter for paramFormat.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		08/05/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @param paramFormat
	 *            paramFormat as Format.
	 */
	public void setParamFormat(Format paramFormat)
	{
		this.paramFormat = paramFormat;
	}

	/**
	 * getParamType
	 * 
	 * Australian National University Data Commons
	 * 
	 * Getter for paramType.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		08/05/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @return paramType as Type.
	 */
	public Type getParamType()
	{
		return paramType;
	}

	/**
	 * setParamType
	 * 
	 * Australian National University Data Commons
	 * 
	 * Setter for paramType.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		08/05/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @param paramType
	 *            paramType as Type.
	 */
	public void setParamType(Type paramType)
	{
		this.paramType = paramType;
	}

	/**
	 * isParamDistinct
	 * 
	 * Australian National University Data Commons
	 * 
	 * Gets if distinct flag is set.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		08/05/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @return paramDistinct as boolean.
	 */
	public boolean isParamDistinct()
	{
		return paramDistinct;
	}

	/**
	 * setParamDistinct
	 * 
	 * Australian National University Data Commons
	 * 
	 * Sets distinct flag.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		08/05/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @param paramDistinct
	 *            true if distinct flag is set.
	 */
	public void setParamDistinct(boolean paramDistinct)
	{
		this.paramDistinct = paramDistinct;
	}

	/**
	 * isParamDt
	 * 
	 * Australian National University Data Commons
	 * 
	 * Gets Fake Media Types flag.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		08/05/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @return Fake Media Types flag as boolean.
	 */
	public boolean isParamDt()
	{
		return paramDt;
	}

	/**
	 * setParamDt
	 * 
	 * Australian National University Data Commons
	 * 
	 * Sets Fake Media Types flag.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		08/05/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @param paramDt
	 *            true if Fake Media Types flag is to be set, false otherwise.
	 */
	public void setParamDt(boolean paramDt)
	{
		this.paramDt = paramDt;
	}

	/**
	 * isParamStream
	 * 
	 * Australian National University Data Commons
	 * 
	 * Gets Stream Immediately flag.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		08/05/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @return Stream Immediately flag as boolean.
	 */
	public boolean isParamStream()
	{
		return paramStream;
	}

	/**
	 * setParamStream
	 * 
	 * Australian National University Data Commons
	 * 
	 * Sets Stream Immediately flag.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		08/05/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @param paramStream
	 *            Stream Immediately flag as boolean.
	 */
	public void setParamStream(boolean paramStream)
	{
		this.paramStream = paramStream;
	}

	/**
	 * generateParamMap
	 * 
	 * Australian National University Data Commons
	 * 
	 * Generates a map of parameters and values to be included in the request.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		08/05/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @return Parameter map as MultiValuedMap<String, String>.
	 */
	private MultivaluedMap<String, String> generateParamMap()
	{
		MultivaluedMap<String, String> paramMap = new MultivaluedMapImpl();

		if (paramLang != null)
			paramMap.add("lang", paramLang.toString());
		else
			throw new IllegalArgumentException("Parameter 'lang' not provided.");

		if (paramFormat != null)
			paramMap.add("format", paramFormat.toString());
		else
			throw new IllegalArgumentException("Parameter 'format' not provided.");

		if (paramType != null)
			paramMap.add("type", paramType.toString());
		else
			throw new IllegalArgumentException("Parameter 'type' not provided.");

		if (Util.isNotEmpty(paramQuery))
			paramMap.add("query", paramQuery);
		else
			throw new IllegalArgumentException("Parameter 'query' not provided.");

		// Optional parameters.
		if (paramDistinct == true)
			paramMap.add("distinct", "on");

		if (paramDt == true)
			paramMap.add("dt", "on");

		if (paramStream == true)
			paramMap.add("stream", "on");

		return paramMap;
	}

	/**
	 * execute
	 * 
	 * Australian National University Data Commons
	 * 
	 * Submits a POST request to the RI Search service along with the generated parameter map.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		08/05/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @return Response from RI Search service as ClientResponse.
	 */
	public ClientResponse execute()
	{
		WebResource riSearchReq = client.resource(riSearchUri);
		riSearchReq = riSearchReq.queryParams(generateParamMap());
		ClientResponse respFromRiSearch = riSearchReq.type(MediaType.APPLICATION_FORM_URLENCODED).post(ClientResponse.class);

		return respFromRiSearch;
	}
}
