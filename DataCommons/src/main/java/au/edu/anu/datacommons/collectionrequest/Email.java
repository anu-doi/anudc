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

package au.edu.anu.datacommons.collectionrequest;

import static java.text.MessageFormat.format;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import au.edu.anu.datacommons.config.Config;
import au.edu.anu.datacommons.properties.GlobalProps;
import au.edu.anu.datacommons.util.Util;

/**
 * Represents an email object comprising of from email, to email(s), subject and body. 
 * 
 * @author Rahul Khanna
 * 
 */
public class Email {
	private static final Logger LOGGER = LoggerFactory.getLogger(Email.class);
	// Regular expression to check for a valid email address.
	// Source: http://www.regular-expressions.info/email.html
	private static final Pattern pattern = Pattern
			.compile("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");
	private JavaMailSenderImpl mailSender;

	private String fromName;
	private String fromEmail;
	private Map<String, String> recipients;
	private String subject;
	private String body;

	public Email(JavaMailSenderImpl mailSender) {
		this.mailSender = mailSender;
		this.setFromName("ANU Data Commons");
		this.setFromEmail("no-reply@anu.edu.au");
		this.recipients = new HashMap<String, String>();
	}

	public String getFromName() {
		return fromName;
	}

	public void setFromName(String fromName) {
		this.fromName = fromName;
	}

	public String getFromEmail() {
		return fromEmail;
	}

	public void setFromEmail(String fromEmail) {
		if (!isValidEmail(fromEmail)) {
			LOGGER.warn("Potentially invalid email address - {}", fromEmail);
		}
		this.fromEmail = fromEmail;
	}

	public Map<String, String> getRecipients() {
		return recipients;
	}

	public void addRecipient(String toEmail) {
		addRecipient(toEmail, null);
	}

	public void addRecipient(String toEmail, String name) {
		if (!isValidEmail(toEmail)) {
			LOGGER.warn("Potentially invalid email address - {}", toEmail);
		}
		recipients.put(toEmail, name);
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public void setBody(String templateFilename, Map<String, String> vars) throws IOException {
		// Read the template file with variables as ${varname} which will be replaced with the value against the key in
		// the Map.
		this.body = convertStreamToString(Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(templateFilename));
		for (String iVar : vars.keySet()) {
			this.body = this.body.replaceAll("\\$\\{" + iVar + "\\}", vars.get(iVar));
		}
	}

	public void send() {
		if (Boolean.parseBoolean(GlobalProps.getProperty(GlobalProps.PROP_EMAIL_DEBUG_SEND, "false"))) {
			if (mailSender == null) {
				throw new NullPointerException("mailSender is null");
			}

			if (!Util.isNotEmpty(this.subject)) {
				throw new NullPointerException("Subject is null. A subject must be provided.");
			}

			if (!Util.isNotEmpty(this.body)) {
				throw new NullPointerException("Message Body is Null. Message text must be provided.");
			}

			SimpleMailMessage message = new SimpleMailMessage();
			message.setFrom(format("{0} <{1}>", fromName, fromEmail));
			message.setTo(getRecipientsAsStringArray());
			message.setSubject(subject);
			message.setText(body);
			LOGGER.info("Sending email...\r\nTO: {}\r\nSUBJECT: {}\r\nBODY: {}", new Object[] { recipientsAsString(),
					subject, body });
			mailSender.send(message);
		} else {
			LOGGER.info(
					"email.debug.sendmail set to 'false' or not present in Global Properties. Following email not sent out.\r\nTO: {}\r\nSUBJECT: {}\r\nBODY: {}",
					new Object[] { recipientsAsString(), subject, body });
		}
	}

	private String recipientsAsString() {
		StringBuilder toList = new StringBuilder();
		String[] toListArray = getRecipientsAsStringArray();
		for (int i = 0; i < toListArray.length; i++) {
			toList.append(toListArray[i]);
			if (i < (toListArray.length - 1)) {
				toList.append(", ");
			}
		}
		return toList.toString();
	}

	private String[] getRecipientsAsStringArray() {
		Set<String> emailSet = new HashSet<String>(recipients.size());
		for (Entry<String, String> recipient : recipients.entrySet()) {
			if (recipient.getValue() != null) {
				emailSet.add(format("{0} <{1}>", recipient.getValue(), recipient.getKey()));
			} else {
				emailSet.add(recipient.getKey());
			}
		}
		return emailSet.toArray(new String[0]);
	}

	private String convertStreamToString(InputStream is) throws IOException {
		if (is == null) {
			throw new NullPointerException("InputStream is null.");
		}

		Writer writer = new StringWriter();
		char[] buffer = new char[8192]; // 8K.
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, Config.CHARSET));
			int n;
			while ((n = reader.read(buffer)) != -1) {
				writer.write(buffer, 0, n);
			}
		} finally {
			IOUtils.closeQuietly(is);
		}
		return writer.toString();
	}

	/**
	 * Checks if an email address is valid.
	 * 
	 * @param email
	 *            Email address to check as String
	 * 
	 * @return true if valid, false otherwise
	 */
	public static boolean isValidEmail(String email) {
		return pattern.matcher(email).matches();
	}
}
