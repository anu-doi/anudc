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

package au.edu.anu.datacommons.report.datasource;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRDataSourceProvider;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JRDesignField;

/**
 * SolrDataSourceProvider
 * 
 * Australian National University Data Commons
 * 
 * Data Source Provider for Solr.  This just give sa few specific fields and is not particularly
 * needed.
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		29/10/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
public class SolrDataSourceProvider implements JRDataSourceProvider {
	/**
	 * create
	 * 
	 * Create the data source
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		29/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param jasperReport
	 * @return
	 * @throws JRException
	 * @see net.sf.jasperreports.engine.JRDataSourceProvider#create(net.sf.jasperreports.engine.JasperReport)
	 */
	public JRDataSource create(JasperReport jasperReport) throws JRException {
		JRDataSource solrDataSource = null;
		
		// Note this value is set up for my system only - change to your own solr instance
		HttpSolrClient solrClient = new HttpSolrClient("http://localhost:8983/solr");
		try {
			try {
				
				SolrQuery solrQuery = new SolrQuery();
				solrQuery.addField("id");
				solrQuery.addField("published.name");
				solrQuery.setQuery("location.published:ANDS");
				
				QueryResponse queryResponse = solrClient.query(solrQuery);
				SolrDocumentList solrDocumentList = queryResponse.getResults();
				
				solrDataSource =  new SolrDataSource(solrDocumentList);
			} 
			catch (SolrServerException | IOException e) {
				e.printStackTrace();
			}
			finally {
				solrClient.close();
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return solrDataSource;
		//return null;
	}

	/**
	 * dispose
	 * 
	 * Disposes the data source
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		29/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param jrDataSource
	 * @throws JRException
	 * @see net.sf.jasperreports.engine.JRDataSourceProvider#dispose(net.sf.jasperreports.engine.JRDataSource)
	 */
	public void dispose(JRDataSource jrDataSource) throws JRException {
	}

	/**
	 * getFields
	 * 
	 * Get the list of fields that are returned
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		29/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param jasperReport
	 * @return
	 * @throws JRException
	 * @throws UnsupportedOperationException
	 * @see net.sf.jasperreports.engine.JRDataSourceProvider#getFields(net.sf.jasperreports.engine.JasperReport)
	 */
	public JRField[] getFields(JasperReport jasperReport) throws JRException,
			UnsupportedOperationException {
		ArrayList<JRDesignField> fields = new ArrayList<JRDesignField>();
		String [] fieldNames = new String [] {"id", "published.name"};
		for (String s : fieldNames) {
			JRDesignField field = new JRDesignField();
			field.setName(s);
			field.setValueClassName("java.lang.String");
			fields.add(field);
		}
		return (JRField[]) fields.toArray(new JRField[fields.size()]);
	}

	/**
	 * supportsGetFieldsOperation
	 * 
	 * Checks whether the get fields operation is possible
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		29/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return
	 * @see net.sf.jasperreports.engine.JRDataSourceProvider#supportsGetFieldsOperation()
	 */
	public boolean supportsGetFieldsOperation() {
		return false;
	}
}
