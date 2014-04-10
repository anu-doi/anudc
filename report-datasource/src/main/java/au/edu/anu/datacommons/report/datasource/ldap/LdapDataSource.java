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

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * LdapDataSource
 *
 * Australian National University Data Commons
 * 
 * Data Source for Ldap Queries
 *
 * JUnit coverage:
 * LdapQueryExecuterTest
 * 
 * @author Genevieve Turner
 *
 */
public class LdapDataSource implements JRDataSource {
	static final Logger LOGGER = LoggerFactory.getLogger(LdapDataSource.class);
	
	NamingEnumeration<SearchResult> results;
	SearchResult currentResult;
	LdapQueryExecuter queryExecuter;
	boolean hasRetrievedFields;
	
	/**
	 * Constructor
	 * 
	 * @param queryExecuter The query executer
	 */
	public LdapDataSource(LdapQueryExecuter queryExecuter) {
		this.queryExecuter = queryExecuter;
		this.hasRetrievedFields = false;
	}
	
	public LdapDataSource() {
		this.hasRetrievedFields = true;
	}

	@Override
	public boolean next() throws JRException {
		LOGGER.trace("Get next ldap data source row");

		if (!hasRetrievedFields) {
			moveFirst();
		}
		boolean hasMore = false;
		try {
			if (results != null && results.hasMore()) {
				hasMore = true;
				currentResult = results.next();
			}
		} catch (NamingException e) {
			e.printStackTrace();
			throw new JRException(e);
		}
		return hasMore;
	}

	@Override
	public Object getFieldValue(JRField jrField) throws JRException {
		LOGGER.trace("Get value for field '{}'", jrField.getName());
		Attributes attributes = currentResult.getAttributes();
		Attribute attr = attributes.get(jrField.getName());
		try {
			if (attr != null) {
				return attr.get();
			}
		}
		catch (NamingException e) {
			throw new JRException(e);
		}
		return null;
	}

	/**
	 * Get the result list
	 * 
	 * @throws JRException
	 */
	public void moveFirst() throws JRException {
		LOGGER.trace("Move to the first row of the data source");
		hasRetrievedFields = true;
		this.results = queryExecuter.getResultList();
	}	
}
