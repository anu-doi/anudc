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

package au.edu.anu.datacommons.filter;

import java.io.IOException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import au.edu.anu.datacommons.data.db.dao.GenericDAO;
import au.edu.anu.datacommons.data.db.dao.GenericDAOImpl;
import au.edu.anu.datacommons.data.db.model.AuditAccess;
import au.edu.anu.datacommons.properties.GlobalProps;
import au.edu.anu.datacommons.util.Util;

/**
 * AuditFilter
 * 
 * Australian National University Data Commons
 * 
 * Filter class that adds a row to the audit access class.
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		25/09/2012	Genevieve Turner (GT)	Initial
 * 0.2		26/09/2012	Genevieve Turner (GT)	Fixed an issue if the url is encoded
 * 0.3		09/11/2012	Genevieve Turner (GT)	Added request id to filter
 * </pre>
 *
 */
public class AuditFilter implements Filter {
	private static final Logger LOGGER = LoggerFactory.getLogger(AuditFilter.class);
	
	private GenericDAO<AuditAccess, Long> genericDAO;
	private Pattern pidPattern;
	private Pattern ridPattern;

	/**
	 * destroy
	 * 
	 * Currently does nothing
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		25/09/2012	Genevieve Turner(GT)	Initial
	 * 0.2		26/09/2012	Genevieve Turner (GT)	Fixed an issue if the url is encoded
	 * </pre>
	 * 
	 * @see javax.servlet.Filter#destroy()
	 */
	@Override
	public void destroy() {
	}

	/**
	 * doFilter
	 * 
	 * Adds a row to the audit_access table
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		25/09/2012	Genevieve Turner(GT)	Initial
	 * 0.3		09/11/2012	Genevieve Turner (GT)	Added request id to filter
	 * </pre>
	 * 
	 * @param request
	 * @param response
	 * @param chain
	 * @throws IOException
	 * @throws ServletException
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		AuditAccess auditAccess = new AuditAccess();
		auditAccess.setAccessDate(new Date());
		auditAccess.setIpAddress(request.getRemoteAddr());

		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof UserDetails) {
			auditAccess.setUsername(((UserDetails)principal).getUsername());
		} else {
			auditAccess.setUsername(principal.toString());
		}
		
		if (request instanceof HttpServletRequest) {
			HttpServletRequest httpRequest = (HttpServletRequest) request;
			StringBuffer requestURL = httpRequest.getRequestURL();
			if (Util.isNotEmpty(httpRequest.getQueryString())) {
				requestURL.append("?");
				requestURL.append(httpRequest.getQueryString());
			}
			auditAccess.setUrl(requestURL.toString());
			auditAccess.setMethod(httpRequest.getMethod());
			
			String decodedRequestURL = Util.decodeUrlEncoded(requestURL.toString());
			Matcher pidMatch = pidPattern.matcher(decodedRequestURL);
			if (pidMatch.find()) {
				auditAccess.setPid(pidMatch.group(1));
			}
			
			Matcher ridMatch = ridPattern.matcher(decodedRequestURL);
			if (ridMatch.find()) {
				auditAccess.setRid(new Long(ridMatch.group(1)));
			}
		}
		genericDAO.create(auditAccess);
		chain.doFilter(request, response);
	}

	/**
	 * init
	 * 
	 * Creates a pattern for matching the pid so that the pid can be logged.
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		25/09/2012	Genevieve Turner(GT)	Initial
	 * 0.3		09/11/2012	Genevieve Turner (GT)	Added request id to filter
	 * </pre>
	 * 
	 * @param config
	 * @throws ServletException
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	@Override
	public void init(FilterConfig config) throws ServletException {
		genericDAO = new GenericDAOImpl<AuditAccess, Long>(AuditAccess.class);
		
		String namespace = GlobalProps.getProperty(GlobalProps.PROP_FEDORA_SAVENAMESPACE);
		String patternToMatch = "/(" + namespace + ":\\d*)";
		pidPattern = Pattern.compile(patternToMatch);
		
		String ridPatternToMatch = "[?&]{1}rid=(\\d*)";
		ridPattern = Pattern.compile(ridPatternToMatch);
	}

}
