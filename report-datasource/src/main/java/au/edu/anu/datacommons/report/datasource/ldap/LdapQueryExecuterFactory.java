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

import java.util.Arrays;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRValueParameter;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.query.JRQueryExecuter;
import net.sf.jasperreports.engine.query.QueryExecuterFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * LdapQueryExecuterFactory
 *
 * Australian National University Data Commons
 * 
 * Factory class for ldap query executers
 *
 * JUnit coverage:
 * LdapQueryExecuterTest
 * 
 * @author Genevieve Turner
 *
 */
public class LdapQueryExecuterFactory implements QueryExecuterFactory {
	static final Logger LOGGER = LoggerFactory.getLogger(LdapQueryExecuterFactory.class);
	
	public static final String LDAP_LOCATION = "LDAP_LOCATION";
	
	private static final String[] queryParameterClassNames;
	
	static {
		queryParameterClassNames = new String[] {
				java.lang.String.class.getName()
		};
	}
	

	@Override
	public JRQueryExecuter createQueryExecuter(JRDataset dataset,
			Map<String, ? extends JRValueParameter> parameters)
			throws JRException {
		return new LdapQueryExecuter(null, dataset, parameters);
	}

	@Override
	public Object[] getBuiltinParameters() {
		return null;
	}

	@Override
	public JRQueryExecuter createQueryExecuter(
			JasperReportsContext jasperReportsContext, JRDataset dataset,
			Map<String, ? extends JRValueParameter> parameters)
			throws JRException {
		return new LdapQueryExecuter(jasperReportsContext, dataset, parameters);
	}

	@Override
	public boolean supportsQueryParameterType(String className) {
		return Arrays.binarySearch(queryParameterClassNames, className) >= 0;
	}

}
