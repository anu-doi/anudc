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
package au.edu.anu.datacommons.report.schedule;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletContext;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import au.edu.anu.datacommons.config.Config;
import au.edu.anu.datacommons.data.db.model.ReportAuto;
import au.edu.anu.datacommons.data.db.model.ReportAutoParam;
import au.edu.anu.datacommons.properties.GlobalProps;
import au.edu.anu.datacommons.report.ReportGenerator;
import au.edu.anu.datacommons.util.AppContext;

/**
 * ReportRunnable
 *
 * Australian National University Data Commons
 * 
 * Runnable class to automate the generation and email of reports
 *
 * JUnit coverage:
 * None
 * 
 * @author Genevieve Turner
 *
 */
public class ReportRunnable implements Runnable {
	static final Logger LOGGER = LoggerFactory.getLogger(ReportRunnable.class);
	ReportAuto reportAuto;
	
	JavaMailSender mailSender;
	
	private String emailSubject = GlobalProps.getProperty(GlobalProps.PROP_REPORT_EMAIL_SUBJECT);
	
	ServletContext context;
	
	/**
	 * Constructor
	 * 
	 * @param reportAuto The automated report object
	 * @param context The servlet context
	 */
	public ReportRunnable(ReportAuto reportAuto, ServletContext context) {
		this.reportAuto = reportAuto;
		this.context = context;
		
		ApplicationContext appContext = AppContext.getApplicationContext();
		this.mailSender = (JavaMailSender) appContext.getBean("mailSender");
	}

	@Override
	public void run() {
		Date date = new Date();
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
		
		LOGGER.info("Report {} sends to {} with parameters:", reportAuto.getReportId(), reportAuto.getEmail());
		if (reportAuto.getReportAutoParam() != null) {
			for (ReportAutoParam param : reportAuto.getReportAutoParam()) {
				LOGGER.info("Parameter {} with value {}", param.getParam(), param.getParamVal());
			}
		}
		String path = context.getRealPath("/");
		ReportGenerator reportGenerator = new ReportGenerator(reportAuto, path);
		try {
			byte[] bytes = reportGenerator.generateReportPDF();
			ByteArrayResource byteArrayResource = new ByteArrayResource(bytes);
			MimeMessage message = mailSender.createMimeMessage();
			if (Boolean.parseBoolean(GlobalProps.getProperty(GlobalProps.PROP_EMAIL_DEBUG_SEND, "false"))) {
				try {
					MimeMessageHelper helper = new MimeMessageHelper(message, true);
					helper.setFrom("no-reply@anu.edu.au", "ANU Data Commons");
					helper.setTo(reportAuto.getEmail());
					helper.setSubject(emailSubject);
					String body = getBody();
					helper.setText(getBody());
					
					String filename = "report-" + sdf2.format(date) + ".pdf";
					
					helper.addAttachment(filename, byteArrayResource);

					LOGGER.info("Sending email...\r\nTO: {}\r\nSUBJECT: {}\r\nBODY: {}\r\nATTACHMENT: {}", new Object[] { reportAuto.getEmail(),
							emailSubject, body, filename });
					mailSender.send(message);
				}
				catch (MessagingException | IOException e) {
					LOGGER.error("Exception generating or sending email", e);
				}
			}
			else {
				LOGGER.info("Subject: {}", emailSubject);
				LOGGER.debug("Report has been generated, not sending due to emails being set to false");
			}
		}
		catch (JRException | SQLException | IOException | ClassNotFoundException e) {
			LOGGER.error("Error generating pdf", e);
		}
	}

	/**
	 * Get the email body
	 * 
	 * @return The email body
	 * @throws IOException
	 */
	private String getBody() throws IOException {
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("mailtmpl/reportmailer.txt");
		
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
}
