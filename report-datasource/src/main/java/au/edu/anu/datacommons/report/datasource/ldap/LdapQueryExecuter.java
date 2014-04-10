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
package au.edu.anu.datacommons.report.datasource.ldap;

import java.util.Hashtable;
import java.util.Map;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRValueParameter;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.query.JRAbstractQueryExecuter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * LdapQueryExecuter
 *
 * Australian National University Data Commons
 * 
 * Class to execute ldap queries
 *
 * JUnit coverage:
 * LdapQueryExecuterTest
 * 
 * @author Genevieve Turner
 *
 */
public class LdapQueryExecuter extends JRAbstractQueryExecuter {
	static final Logger LOGGER = LoggerFactory.getLogger(LdapQueryExecuter.class);
	
	private String[] returningAttributes;
	private String filter;
	private DirContext context;
	SearchControls searchControls;

	/**
	 * Constructor
	 * 
	 * @param context The jasper reports context
	 * @param jrDataset The dataset
	 * @param parameters The parameters
	 */
	public LdapQueryExecuter(JasperReportsContext context, JRDataset jrDataset,
			Map<String, ? extends JRValueParameter> parameters) {
		super(context, jrDataset, parameters);
		LOGGER.trace("Create LdapQueryExecuter");
		parseQuery();
	}
	
	@Override
	public JRDataSource createDatasource() throws JRException {
		LOGGER.trace("Create ldap data source");
		
		if (this.returningAttributes == null || this.returningAttributes.length == 0) {
			throw new JRException("No returning attributes defined");
		}
		
		if (this.filter == null) {
			throw new JRException("No Filter Found");
		}
		
		try {
			DirContext dirContext = new InitialDirContext(getConnectionProperties());
			this.context = dirContext;
			SearchControls searchControls = new SearchControls();
			searchControls.setReturningAttributes(getReturningAttributes());
			searchControls.setCountLimit(0);
			searchControls.setTimeLimit(10000);
			searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			this.searchControls = searchControls;
			
			return new LdapDataSource(this);
		}
		catch (NamingException e) {
			e.printStackTrace();
			throw new JRException(e);
		}
	}
	
	@SuppressWarnings({"unchecked","rawtypes"})
	private Hashtable getConnectionProperties() {
		LOGGER.trace("Get report connection properties");
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		String ldapURL = (String) getParameterValue(LdapQueryExecuterFactory.LDAP_LOCATION);
		env.put(Context.PROVIDER_URL, ldapURL);
		return env;
	}
	
	@Override
	protected void parseQuery() {
		LOGGER.trace("parse report query");
		super.parseQuery();
		String queryString = getQueryString();
		int selectIndex = queryString.indexOf("SELECT ");
		if (selectIndex == -1) {
			return;
		}
		int filterIndex = queryString.indexOf("FILTER ");
		if (filterIndex == -1) {
			return;
		}
		
		String returnFieldsString = queryString.substring(selectIndex + 7, filterIndex);
		String[] returnFields = returnFieldsString.split(",");
		for (int i = 0; i < returnFields.length; i++) {
			returnFields[i] = returnFields[i].trim();
		}
		this.returningAttributes = returnFields;
		
		String filterString = queryString.substring(filterIndex + 7);
		this.filter = filterString;
		
		for (String attr : returningAttributes) {
			LOGGER.trace("report ldap attribute: {}", attr);
		}
		LOGGER.trace("Report LDAP Filter value: {}", this.filter);
	}
	
	/**
	 * Get the returning attributes for ldap
	 * 
	 * @return An array of attributes to search for
	 */
	protected String[] getReturningAttributes() {
		return returningAttributes;
	}
	
	/**
	 * Get the ldap search filter
	 * 
	 * @return The search filter
	 */
	protected String getFilter() {
		return filter;
	}
	
	/**
	 * Get the search results
	 * 
	 * @return The search results
	 */
	public NamingEnumeration<SearchResult> getResultList() {
		try {
			//TODO base dn as variable?
			NamingEnumeration<SearchResult> results = context.search("o=anu.edu.au", getFilter(), searchControls);
			context.close();
			return results;
		}
		catch (NamingException e) {
			//TODO handle
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void close() {
		try {
			context.close();
		}
		catch (NamingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean cancelQuery() throws JRException {
		try {
			context.close();
			return true;
		}
		catch (NamingException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	protected String getParameterReplacement(String parameterName) {
		return String.valueOf(getParameterValue(parameterName));
	}

}
