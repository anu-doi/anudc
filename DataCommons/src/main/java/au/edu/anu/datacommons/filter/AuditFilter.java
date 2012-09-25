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
 * </pre>
 *
 */
public class AuditFilter implements Filter {
	static final Logger LOGGER = LoggerFactory.getLogger(AuditFilter.class);
	private Pattern pattern;

	/**
	 * destroy
	 * 
	 * Currently does nothing
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		25/09/2012	Genevieve Turner(GT)	Initial
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
		if (request instanceof HttpServletRequest) {
			HttpServletRequest httpRequest = (HttpServletRequest) request;
			AuditAccess auditAccess = new AuditAccess();
			auditAccess.setAccessDate(new Date());
			if (Util.isNotEmpty(httpRequest.getRemoteAddr())) {
				auditAccess.setIpAddress(httpRequest.getRemoteAddr());
			}
			StringBuffer requestURL = httpRequest.getRequestURL();
			if (Util.isNotEmpty(httpRequest.getQueryString())) {
				requestURL.append("?");
				requestURL.append(httpRequest.getQueryString());
			}
			auditAccess.setUrl(requestURL.toString());
			auditAccess.setMethod(httpRequest.getMethod());
			
			Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

			if (principal instanceof UserDetails) {
				auditAccess.setUsername(((UserDetails)principal).getUsername());
			}
			else {
				auditAccess.setUsername(principal.toString());
			}
			
			Matcher m = pattern.matcher(requestURL.toString());
			if (m.find()) {
				auditAccess.setPid(m.group(1));
			}
			
			GenericDAO<AuditAccess, Long> genericDAO = new GenericDAOImpl<AuditAccess, Long>(AuditAccess.class);
			genericDAO.create(auditAccess);
			LOGGER.debug("Audit row added to database for {}", auditAccess.getUrl());
		}
		else {
			LOGGER.info("Servlet request is not a http servlet request");
		}
		
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
	 * </pre>
	 * 
	 * @param config
	 * @throws ServletException
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	@Override
	public void init(FilterConfig config) throws ServletException {
		String namespace = GlobalProps.getProperty(GlobalProps.PROP_FEDORA_SAVENAMESPACE);
		String patternToMatch = "/(" + namespace + ":\\d*)";
		pattern = Pattern.compile(patternToMatch);
	}

}
