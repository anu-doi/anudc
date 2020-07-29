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
package au.edu.anu.datacommons.embargo;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import au.edu.anu.datacommons.collectionrequest.Email;
import au.edu.anu.datacommons.data.solr.dao.SolrSearchDAO;
import au.edu.anu.datacommons.data.solr.dao.SolrSearchDAOImpl;
import au.edu.anu.datacommons.data.solr.model.SolrSearchResult;
import au.edu.anu.datacommons.properties.GlobalProps;

import com.ibm.icu.util.Calendar;

/**
 * EmbargoEmailer
 *
 * Australian National University Data Commons
 * 
 * Emails contacts for records when the embargo is soon to expire.
 *
 * JUnit coverage:
 * EmbargoEmailerTest
 * 
 * @author Genevieve Turner
 *
 */
public class EmbargoEmailer implements Runnable {
	static final Logger LOGGER = LoggerFactory.getLogger(EmbargoEmailer.class);
	
	private int days;
	JavaMailSenderImpl mailSender;
	
	/**
	 * Constructor
	 */
	public EmbargoEmailer(JavaMailSenderImpl mailSender, int reminderDays) {
		this.days = reminderDays;
		this.mailSender = mailSender;
	}

	@Override
	public void run() {
		// We only want to send the emails if the embargo dates are today or later.
		if (days < 0) {
			return;
		}
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, days);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String date = sdf.format(cal.getTime());
		String fromDate = date + "T00:00:00Z";
		String toDate = date + "T23:23:59.999Z";
		LOGGER.debug("Finding embargoed items where the lift date is between {} and {}", fromDate, toDate);
		String queryString = "published.calcEmbargoDate:[" + fromDate + " TO " + toDate + "]";
		
		SimpleDateFormat sdf2 = new SimpleDateFormat("dd MMMMMM yyyy");
		String expiryDateStr = sdf2.format(cal.getTime());
		
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery(queryString);
		solrQuery.addField("id");
		solrQuery.addField("published.name");
		solrQuery.addField("published.email");
		
		SolrSearchDAO solrSearchDAO = new SolrSearchDAOImpl();
		try {
			SolrSearchResult solrSearchResult = solrSearchDAO.executeSearch(solrQuery);
			SolrDocumentList docList = solrSearchResult.getDocumentList();
			Iterator<SolrDocument> it = docList.iterator();
			while (it.hasNext()) {
				SolrDocument doc = it.next();
				
				LOGGER.info("Record to email embargo expiry for Id: {}, Name: {}, Emails: {}", doc.getFieldValue("id"), doc.getFieldValue("published.name"), doc.getFieldValues("published.email"));
				
				Map<String, String> emailProperties = new HashMap<String, String>();
				emailProperties.put("embargoExpiryDate", expiryDateStr);
				emailProperties.put("itemName", (String)doc.getFieldValue("published.name"));
				String itemURL = GlobalProps.getProperty("app.server") + "/DataCommons/item/" + doc.getFieldValue("id");
				emailProperties.put("itemURL", itemURL);
				
				Collection<Object> emails = doc.getFieldValues("published.email");
				
				Email email = new Email(mailSender);
				for (Object emailAddress : emails) {
					email.addRecipient((String)emailAddress);
				}
				email.setSubject("The embargo for " + doc.getFieldValue("published.name") + " is about to expire");
				try {
					email.setBody("mailtmpl/embargoreminder.txt", emailProperties);
					email.send();
				}
				catch (IOException e) {
					LOGGER.error("Error setting email body", e);
				}
			}
		}
		catch (SolrServerException | IOException e) {
			LOGGER.error("Exception querying solr for EmbargoEmailer", e);
		}
	}
	
}
