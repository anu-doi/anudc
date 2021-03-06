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

package au.edu.anu.datacommons.ldap;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.xml.bind.v2.runtime.unmarshaller.Loader;

import au.edu.anu.datacommons.properties.GlobalProps;
import au.edu.anu.datacommons.services.UserResource;

/**
 * LdapRequest
 * 
 * Australian National University Data Commons
 * 
 * This class wraps a request to be submitted to an LDAP server. URL and BaseDN values of LDAP server are retrieved from
 * the properties file.
 * 
 * <pre>
 * Version	Date		Developer			Description
 * 0.1 		09/03/2012	Rahul Khanna (RK)	Initial
 * </pre>
 */
public class LdapRequest {
	private static final Logger LOGGER = LoggerFactory.getLogger(LdapRequest.class);

	private static final String LdapUri = GlobalProps.getProperty(GlobalProps.PROP_LDAP_URI);
	private static final String LdapBaseDn = GlobalProps.getProperty(GlobalProps.PROP_LDAP_BASEDN);
	private static final String LdapContextFactoryName = "com.sun.jndi.ldap.LdapCtxFactory";
	private static final int DEFAULT_MAX_SEARCH_RESULTS = 10;
	private static final Hashtable<String, String> authCtxEnv = new Hashtable<String, String>();
	private static final Hashtable<String, String> searchCtxEnv = new Hashtable<String, String>();
	private static final MessageFormat queryPartTemplate = new MessageFormat("({0}={1})");

	private StringBuilder ldapQuery;
	private SearchControls ldapSearchControl;

	static {
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
	public LdapRequest() {
		ldapQuery = new StringBuilder();
		ldapSearchControl = new SearchControls();
	}

	/**
	 * setMaxResults
	 * 
	 * Autralian National University Data Commons
	 * 
	 * Sets the maximum number of search results that will be returned when the LDAP query is executed.
	 * DEFAULT_MAX_SEARCH_RESULTS is used if not specified.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		16/03/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @param maxResults
	 *            Number of maximum search results accepted.
	 */
	public void setMaxResults(long maxResults) {
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
	public void setQuery(String query) {
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
	public List<LdapPerson> search() throws NamingException {
		DirContext dirContext;
		NamingEnumeration<SearchResult> searchResults = null;
		List<LdapPerson> people = new ArrayList<LdapPerson>();

		// Perform search.
		dirContext = new InitialDirContext(searchCtxEnv);
		searchResults = dirContext.search(LdapBaseDn, ldapQuery.toString(), ldapSearchControl);

		// Iterate through the results returned, stored each one in results.
		while (searchResults.hasMore()) {
			LdapPerson ldapPerson = new LdapPerson(searchResults.next().getAttributes());
			people.add(ldapPerson);
		}

		try {
			dirContext.close();
		} catch (NamingException e) {
			LOGGER.warn("Unable to close directory context after LDAP query {}", ldapQuery.toString());
		}
		return people;
	}

	/**
	 * searchUniId
	 * 
	 * Autralian National University Data Commons
	 * 
	 * A method that searches for the specified Uni Id in the LDAP and returns an LdapPerson object with the entry
	 * details. Attributes to be retrieved are specified in properties file.
	 * 
	 * @param uniId
	 *            A valid ANU university ID.
	 * @return An LdapPerson object containing attributes of the person whose Uni Id was provided as parameter.
	 * @throws Exception
	 */
	public LdapPerson searchUniId(String uniId) throws Exception {
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

		try {
			dirContext = new InitialDirContext(searchCtxEnv);
			searchResults = dirContext.search(LdapBaseDn, ldapQuery.toString(), ldapSearchControl);
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// If no results returned or multiple results returned, throw exception.
		SearchResult firstResult = searchResults.next();
		if (firstResult == null || searchResults.hasMore()) {
			throw new Exception("No results or multiple results returned for Uni Id. Only one expected.");
		}

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
	public boolean authenticate(String username, String password) {
		boolean isAuthenticated = false;
		DirContext dirContext = null;

		// Parameter password must never be blank. A user gets authenticated if password is blank.
		if (password.equals("")) {
			return isAuthenticated;
		}

		// Include username and password in the directory context environment.
		authCtxEnv.put(Context.SECURITY_PRINCIPAL, GlobalProps.getProperty(GlobalProps.PROP_LDAPATTR_UNIID) + "="
				+ username + ", " + GlobalProps.getProperty(GlobalProps.PROP_LDAP_BASEDN));
		authCtxEnv.put(Context.SECURITY_CREDENTIALS, password);

		try {
			dirContext = new InitialDirContext(authCtxEnv);
			// If no exception thrown then credentials are valid.
			isAuthenticated = true;
		} catch (AuthenticationException e) {
			// Invalid credentials
			isAuthenticated = false;
		} catch (NamingException e) {
			// Can't be sure if credentials are valid when this exception's thrown. Flagged false to be safe.
			isAuthenticated = false;
		} finally {
			if (dirContext != null) {
				try {
					dirContext.close();
				} catch (NamingException e) {
					dirContext = null;
				}
			}
		}

		return isAuthenticated;
	}

	String createQueryPart(String attributeName, String attributeValue) {
		return queryPartTemplate.format(new Object[] {attributeName, attributeValue});
	}
	
	String createQueryGroup(String operator, String... queryParts) {
		StringBuilder queryGroup = new StringBuilder("(");
		queryGroup.append(operator);
		for (int i = 0; i < queryParts.length; i++) {
			queryGroup.append(queryParts[i]);
		}
		queryGroup.append(")");
		return queryGroup.toString();
	}
}
