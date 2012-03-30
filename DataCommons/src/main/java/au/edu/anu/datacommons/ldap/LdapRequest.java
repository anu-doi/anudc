package au.edu.anu.datacommons.ldap;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Logger;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import au.edu.anu.datacommons.properties.GlobalProps;

/**
 * LdapRequest
 * 
 * Australian National University Data Commons
 * 
 * This class wraps a request to be submitted to an LDAP server. URL and BaseDN values of LDAP server are retrieved from the properties file.
 * 
 * <pre>
 * Version	Date		Developer			Description
 * 0.1 		09/03/2012	Rahul Khanna (RK)	Initial
 * </pre>
 */
public class LdapRequest
{
	private static final String LdapUri = GlobalProps.getProperty(GlobalProps.PROP_LDAP_URI);
	private static final String LdapBaseDn = GlobalProps.getProperty(GlobalProps.PROP_LDAP_BASEDN);
	private static final String LdapContextFactoryName = "com.sun.jndi.ldap.LdapCtxFactory";
	private static final int DEFAULT_MAX_SEARCH_RESULTS = 10;
	private static final Hashtable<String, String> authCtxEnv = new Hashtable<String, String>();
	private static final Hashtable<String, String> searchCtxEnv = new Hashtable<String, String>();

	private final Logger log = Logger.getLogger(this.getClass().getName());

	private StringBuilder ldapQuery;
	private SearchControls ldapSearchControl;
	// TODO: Check if ArrayList is the best type to use for a collection of this kind.
	private ArrayList<LdapPerson> results;

	// Initialise the Search and Authenticate context environments.
	static
	{
		searchCtxEnv.put(Context.INITIAL_CONTEXT_FACTORY, LdapContextFactoryName);
		searchCtxEnv.put(Context.PROVIDER_URL, LdapUri);

		authCtxEnv.put(Context.INITIAL_CONTEXT_FACTORY, LdapContextFactoryName);
		authCtxEnv.put(Context.PROVIDER_URL, LdapUri);
		authCtxEnv.put(Context.SECURITY_AUTHENTICATION, "simple");
	}

	/**
	 * LdapRequest
	 * 
	 * Autralian National University Data Commons
	 * 
	 * Constructor
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		16/03/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 */
	public LdapRequest()
	{
		ldapQuery = new StringBuilder();
		ldapSearchControl = new SearchControls();
	}

	/**
	 * setMaxResults
	 * 
	 * Autralian National University Data Commons
	 * 
	 * Sets the maximum number of search results that will be returned when the LDAP query is executed. DEFAULT_MAX_SEARCH_RESULTS is used if not specified.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		16/03/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @param maxResults
	 *            Number of maximum search results accepted.
	 */
	public void setMaxResults(long maxResults)
	{
		ldapSearchControl.setCountLimit(maxResults);
	}

	/**
	 * setQuery
	 * 
	 * Autralian National University Data Commons
	 * 
	 * Sets the query passed as the parameter to be run.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		16/03/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @param query
	 *            LDAP Query to be run
	 */
	public void setQuery(String query)
	{
		ldapQuery = new StringBuilder(query);
	}

	/**
	 * search
	 * 
	 * Autralian National University Data Commons
	 * 
	 * Executes the LDAP query. Requires the LDAP query to be assigned.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		16/03/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @throws NamingException
	 *             When the LDAP query is either blank or invalid.
	 */
	public long search() throws NamingException
	{
		DirContext dirContext;
		NamingEnumeration<SearchResult> searchResults = null;
		long numResults = 0L;

		// Perform search.
		dirContext = new InitialDirContext(searchCtxEnv);
		searchResults = dirContext.search(LdapBaseDn, ldapQuery.toString(), ldapSearchControl);

		// Iterate through the results returned, stored each one in results.
		while (searchResults.hasMore())
		{
			results.add(new LdapPerson(searchResults.next().getAttributes()));
			numResults++;
		}

		dirContext.close();

		// Return number of results returned.
		return numResults;
	}

	/**
	 * searchUniId
	 * 
	 * Autralian National University Data Commons
	 * 
	 * A method that searches for the specified Uni Id in the LDAP and returns an LdapPerson object with the entry details. Attributes to be retrieved are
	 * specified in properties file.
	 * 
	 * @param uniId
	 *            A valid ANU university ID.
	 * @return An LdapPerson object containing attributes of the person whose Uni Id was provided as parameter.
	 * @throws Exception
	 */
	public LdapPerson searchUniId(String uniId) throws Exception
	{
		DirContext dirContext;
		NamingEnumeration<SearchResult> searchResults = null;

		ldapSearchControl.setCountLimit(DEFAULT_MAX_SEARCH_RESULTS);
		ldapSearchControl.setReturningAttributes(GlobalProps.getProperty(GlobalProps.PROP_LDAP_ATTRLIST).split(","));
		ldapSearchControl.setTimeLimit(10000);

		// ldapQuery = "(uid=[uniId])". Where uniId is the parameter passed to this function.
		ldapQuery = new StringBuilder();
		ldapQuery.append("(");
		ldapQuery.append(GlobalProps.getProperty(GlobalProps.PROP_LDAPATTR_UNIID));
		ldapQuery.append("=");
		ldapQuery.append(uniId);
		ldapQuery.append(")");

		try
		{
			dirContext = new InitialDirContext(searchCtxEnv);
			log.info("Running LDAP query: " + ldapQuery.toString());
			searchResults = dirContext.search(LdapBaseDn, ldapQuery.toString(), ldapSearchControl);
		}
		catch (NamingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// If no results returned or multiple results returned, throw exception.
		SearchResult firstResult = searchResults.next();
		if (firstResult == null || searchResults.hasMore())
			throw new Exception("No results or multiple results returned for Uni Id. Only one expected.");

		return new LdapPerson(firstResult.getAttributes());
	}

	/**
	 * authenticate
	 * 
	 * Autralian National University Data Commons
	 * 
	 * Queries the LDAP server if a set of username and password are valid.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		16/03/2012	Rahul Khanna (RK)	Initial.
	 * </pre>
	 * 
	 * @param username
	 *            Username
	 * @param password
	 *            Password
	 * @return true if credentials are correct, false otherwise.
	 */
	public boolean authenticate(String username, String password)
	{
		boolean isAuthenticated = false;
		DirContext dirContext = null;

		// Parameter password must never be blank. A user gets authenticated if password is blank.
		if (password.equals(""))
			return isAuthenticated;

		// Include username and password in the directory context environment.
		authCtxEnv.put(Context.SECURITY_PRINCIPAL,
				GlobalProps.getProperty(GlobalProps.PROP_LDAPATTR_UNIID) + "=" + username + ", " + GlobalProps.getProperty(GlobalProps.PROP_LDAP_BASEDN));
		authCtxEnv.put(Context.SECURITY_CREDENTIALS, password);

		try
		{
			dirContext = new InitialDirContext(authCtxEnv);
			// If no exception thrown then credentials are valid.
			isAuthenticated = true;
		}
		catch (AuthenticationException e)		// Invalid credentials
		{
			isAuthenticated = false;
		}
		catch (NamingException e)		// Some other error
		{
			// Can't be sure if credentials are valid when this exception's thrown. Flagged false to be safe.
			isAuthenticated = false;
		}
		finally
		{
			if (dirContext != null)
			{
				try
				{
					dirContext.close();
				}
				catch (NamingException e)
				{
					dirContext = null;
				}
			}
		}

		return isAuthenticated;
	}

}
