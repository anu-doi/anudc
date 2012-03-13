/**
 * 
 */
package au.edu.anu.datacommons.ldap;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import au.edu.anu.datacommons.properties.GlobalProps;

/**
 * LdapSearch
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
public class LdapSearch
{
	private static final String LdapUri = GlobalProps.getProperty(GlobalProps.PROP_LDAP_URI);
	private static final String LdapBaseDn = GlobalProps.getProperty(GlobalProps.PROP_LDAP_BASEDN);
	private static final int MAX_SEARCH_RESULTS = 10;
	private static final Hashtable<String, String> env;
	private static final SearchControls ldapSearchControl;
	private static DirContext dirContext = null;

	private final Logger log = Logger.getLogger(this.getClass().getName());
	
	private StringBuilder ldapQuery;
	// TODO: Check if ArrayList is the best type to use for a collection of this kind.
	private ArrayList<LdapPerson> results;

	static
	{
		env = new Hashtable<String, String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, LdapUri);

		ldapSearchControl = new SearchControls();
		ldapSearchControl.setCountLimit(MAX_SEARCH_RESULTS);
		ldapSearchControl.setReturningAttributes(new String[]
		{ "uid", "displayName", "givenName", "sn", "mail", "telephoneNumber" });

		try
		{
			dirContext = new InitialDirContext(env);
		}
		catch (NamingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Constructor
	 */
	public LdapSearch()
	{
		ldapQuery = new StringBuilder();
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
		NamingEnumeration<SearchResult> searchResults = null;
		
		ldapQuery = new StringBuilder();
		ldapQuery.append("(");
		ldapQuery.append(GlobalProps.getProperty(GlobalProps.PROP_LDAPATTR_UNIID, "uid"));
		ldapQuery.append("=");
		ldapQuery.append(uniId);
		ldapQuery.append(")");
		
		log.info("Running LDAP query: " + ldapQuery.toString());
		searchResults = dirContext.search(LdapBaseDn, ldapQuery.toString(), ldapSearchControl);

		SearchResult firstResult = searchResults.next();
		if (firstResult == null || searchResults.hasMore())
			throw new Exception("Multiple results returned for Uni Id. Only one expected.");
		
		return new LdapPerson(firstResult.getAttributes());
	}

	/**
	 * Sets the query passed as the parameter to be run.
	 * @param query
	 * Query to be run
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
		NamingEnumeration<SearchResult> searchResults = null;
		long numResults = 0L;
		
		searchResults = dirContext.search(LdapBaseDn, ldapQuery.toString(), ldapSearchControl);
		
		while (searchResults.hasMore())
		{
			results.add(new LdapPerson(searchResults.next().getAttributes()));
			numResults++;
		}
		
		return numResults;
	}

}
