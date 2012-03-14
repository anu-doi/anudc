/**
 * 
 */
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
 * Class that performs a search on the LDAP directory.
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

	static
	{
		searchCtxEnv.put(Context.INITIAL_CONTEXT_FACTORY, LdapContextFactoryName);
		searchCtxEnv.put(Context.PROVIDER_URL, LdapUri);
		
		authCtxEnv.put(Context.INITIAL_CONTEXT_FACTORY, LdapContextFactoryName);
		authCtxEnv.put(Context.PROVIDER_URL, LdapUri);
		authCtxEnv.put(Context.SECURITY_AUTHENTICATION, "simple");
	}


	/**
	 * Constructor
	 */
	public LdapRequest()
	{
		ldapQuery = new StringBuilder();
		ldapSearchControl = new SearchControls();
	}

	/**
	 * Sets the maximum number of search results that will be returned.
	 * 
	 * @param maxResults
	 *            Number of maximum search results accepted.
	 */
	public void setMaxResults(long maxResults)
	{
		ldapSearchControl.setCountLimit(maxResults);
	}

	/**
	 * A convenience method that searches for the specified Uni Id in the LDAP and returns an LdapPerson object with the entry details.
	 * 
	 * @param uniId
	 *            A valid ANU university ID.
	 * @return An LdapPerson object containing attributes of the person whose Uni Id was provided as parameter.
	 * @throws Exception
	 */
	public LdapPerson uniId(String uniId) throws Exception
	{
		DirContext dirContext;
		NamingEnumeration<SearchResult> searchResults = null;

		ldapSearchControl.setCountLimit(DEFAULT_MAX_SEARCH_RESULTS);
		ldapSearchControl.setReturningAttributes(GlobalProps.getProperty(GlobalProps.PROP_LDAP_ATTRLIST).split(","));

		ldapQuery = new StringBuilder();
		ldapQuery.append("(");
		ldapQuery.append(GlobalProps.getProperty(GlobalProps.PROP_LDAPATTR_UNIID, "uid"));
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

		SearchResult firstResult = searchResults.next();
		if (firstResult == null || searchResults.hasMore())
			throw new Exception("No results or multiple results returned for Uni Id. Only one expected.");

		return new LdapPerson(firstResult.getAttributes());
	}

	/**
	 * Sets the query passed as the parameter to be run.
	 * 
	 * @param query
	 *            Query to be run
	 */
	public void setQuery(String query)
	{
		ldapQuery = new StringBuilder(query);
	}

	/**
	 * Executes the LDAP query.
	 * 
	 * @throws NamingException
	 */
	public long search() throws NamingException
	{
		DirContext dirContext;
		NamingEnumeration<SearchResult> searchResults = null;
		long numResults = 0L;

		dirContext = new InitialDirContext(searchCtxEnv);
		searchResults = dirContext.search(LdapBaseDn, ldapQuery.toString(), ldapSearchControl);

		while (searchResults.hasMore())
		{
			results.add(new LdapPerson(searchResults.next().getAttributes()));
			numResults++;
		}

		return numResults;
	}

	public boolean authenticate(String username, String password)
	{
		boolean isAuthenticated = false;
		DirContext dirContext = null;
		
		// Add addition environment key values in the environment Hashtable.
		authCtxEnv.put(Context.SECURITY_PRINCIPAL, "uid=" + username + ", " + GlobalProps.getProperty(GlobalProps.PROP_LDAP_BASEDN));
		authCtxEnv.put(Context.SECURITY_CREDENTIALS, password);

		try
		{
			dirContext = new InitialDirContext(authCtxEnv);

			// If no exception thrown then credentials are valid.
			isAuthenticated = true;
		}
		catch (AuthenticationException e)
		{
			// When credentials are not valid.
			isAuthenticated = false;
		}
		catch (NamingException e)
		{
			// TODO Change catch code.
			e.printStackTrace();
			
			// Can't be sure if credentials are valid when this exception's thrown. Status set to false.
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
