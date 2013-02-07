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

import java.util.Collection;
import java.util.Iterator;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

/**
 * SolrDataSource
 * 
 * Australian National University Data Commons
 * 
 * Jasper Reports Data Source for solr.  
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
public class SolrDataSource implements JRDataSource {
	SolrDocumentList solrDocumentList;
	int index = -1;
	
	/**
	 * Constructor
	 * 
	 * Constructor with a given solrDocumentList to move through
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		29/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param solrDocumentList
	 */
	public SolrDataSource(SolrDocumentList solrDocumentList) {
		this.solrDocumentList = solrDocumentList;
	}
	
	/**
	 * getFieldValue
	 * 
	 * Retrieve the field with the given name
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		29/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param jrField
	 * @return
	 * @throws JRException
	 * @see net.sf.jasperreports.engine.JRDataSource#getFieldValue(net.sf.jasperreports.engine.JRField)
	 */
	public Object getFieldValue(JRField jrField) throws JRException {
		SolrDocument solrDocument = solrDocumentList.get(index);
		Object object = solrDocument.get(jrField.getName());
		if (object instanceof Collection) {
			Collection collection = (Collection) object;
			Iterator it = collection.iterator();
			StringBuilder sb = new StringBuilder();
			for (int i = 0; it.hasNext(); i++) {
				if (i > 0) {
					sb.append(", ");
				}
				sb.append(it.next());
			}
			return sb.toString();
		}
		else {
			return object;
		}
	}

	/**
	 * next
	 * 
	 * Move the record to the next position
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		29/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return
	 * @throws JRException
	 * @see net.sf.jasperreports.engine.JRDataSource#next()
	 */
	public boolean next() throws JRException {
		index++;
		if (index < solrDocumentList.size()) {
			return true;
		}
		return false;
	}
}
