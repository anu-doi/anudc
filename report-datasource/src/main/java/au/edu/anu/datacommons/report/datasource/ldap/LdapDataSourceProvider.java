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

import java.util.ArrayList;
import java.util.List;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRDataSourceProvider;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JRDesignField;
import net.sf.jasperreports.engine.query.JRQueryExecuter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * LdapDataSourceProvider
 *
 * Australian National University Data Commons
 * 
 * Data Source provider for ldap queries
 *
 * JUnit coverage:
 * None
 * 
 * @author Genevieve Turner
 *
 */
public class LdapDataSourceProvider implements JRDataSourceProvider {
	static final Logger LOGGER = LoggerFactory.getLogger(LdapDataSourceProvider.class);

	@Override
	public boolean supportsGetFieldsOperation() {
		//return false;
		return true;
	}

	@Override
	public JRField[] getFields(JasperReport report) throws JRException,
			UnsupportedOperationException {
		
		List<JRField> fields = new ArrayList<JRField>();
		JRDesignField field = new JRDesignField();
		field.setName("uid");
		field.setValueClass(String.class);
		fields.add(field);
		
		return fields.toArray(new JRField[fields.size()]);
	}

	@Override
	public JRDataSource create(JasperReport report) throws JRException {
		LdapQueryExecuterFactory factory = new LdapQueryExecuterFactory();
		JRDataset[] datasets = report.getDatasets();
		if (datasets != null) {
			if (datasets.length > 0) {
				JRDataset dataset = datasets[0];
				JRQueryExecuter queryExecuter = factory.createQueryExecuter(dataset, null);
				JRDataSource dataSource = queryExecuter.createDatasource();
				return dataSource;
			}
		}
		return new LdapDataSource();
	}

	@Override
	public void dispose(JRDataSource dataSource) throws JRException {
	}

}
