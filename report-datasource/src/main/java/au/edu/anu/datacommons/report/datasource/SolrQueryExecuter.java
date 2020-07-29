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
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRValueParameter;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.query.JRAbstractQueryExecuter;

/**
 * SolrQueryExecuter
 * 
 * Australian National University Data Commons
 * 
 * Query Executer for retrieving solr data
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
public class SolrQueryExecuter extends JRAbstractQueryExecuter {
	/**
	 * Constructor
	 * 
	 * Constructor class for the executer
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		29/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param context The jasper reports context
	 * @param jrDataset The jasper reports dataset
	 * @param parameters The parameters sent to the report
	 */
	public SolrQueryExecuter(JasperReportsContext context, JRDataset jrDataset,
			Map<String, ? extends JRValueParameter> parameters) {
		super(context, jrDataset, parameters);
		parseQuery();
	}

	/**
	 * cancelQuery
	 * 
	 * Action to cancel the query
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		29/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return whether the query has been cancelled
	 * @throws JRException
	 * @see net.sf.jasperreports.engine.query.JRQueryExecuter#cancelQuery()
	 */
	public boolean cancelQuery() throws JRException {
		return false;
	}

	/**
	 * close
	 * 
	 * Close the query
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		29/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @see net.sf.jasperreports.engine.query.JRQueryExecuter#close()
	 */
	public void close() {
	}

	/**
	 * createDatasource
	 * 
	 * Create the data source
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		29/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return The jasper reports data source
	 * @throws JRException
	 * @see net.sf.jasperreports.engine.query.JRQueryExecuter#createDatasource()
	 */
	public JRDataSource createDatasource() throws JRException {
		String statement = getQueryString();

		if (statement.length() == 0) {
			SolrDocumentList solrDocumentList = new SolrDocumentList();
			return new SolrDataSource(solrDocumentList);
		}
		
		String solrURL = (String) getParameterValue(SolrQueryExecuterFactory.SOLR_LOCATION);
		HttpSolrClient solrClient = new HttpSolrClient(solrURL);
		
		SolrQuery solrQuery = new SolrQuery();
		String[] statements = statement.split("&");
		for (String param : statements) {
			int equalsIndex = param.indexOf("=");
			String field = param.substring(0, equalsIndex);
			String value = param.substring(equalsIndex + 1);
			solrQuery.setParam(field, value);
		}
		
		SolrDataSource solrDataSource = null;
		try {
			try {
				QueryResponse queryResponse = solrClient.query(solrQuery);
				SolrDocumentList solrDocumentList = queryResponse.getResults();
				solrDataSource = new SolrDataSource(solrDocumentList);
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
	}

	/**
	 * getParameterReplacement
	 * 
	 * Get the parameter replacement value
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		29/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param parameterName The name of the parameter to replace
	 * @return The paraemter value
	 * @see net.sf.jasperreports.engine.query.JRAbstractQueryExecuter#getParameterReplacement(java.lang.String)
	 */
	@Override
	protected String getParameterReplacement(String parameterName) {
		return String.valueOf(getParameterValue(parameterName));
	}
}
