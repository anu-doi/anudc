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

package au.edu.anu.datacommons.login;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.PortResolver;
import org.springframework.security.web.PortResolverImpl;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import org.springframework.security.web.savedrequest.SavedRequest;

import au.edu.anu.datacommons.properties.GlobalProps;
import au.edu.anu.datacommons.util.Util;

/**
 * Servlet implementation class LoginServlet
 */

/**
 * LoginServlet
 * 
 * Australian National University Data Commons
 * 
 * A Servlet used with the login.
 * 
 * JUnit coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		19/03/2012	Rahul Khanna (RK)		Initial build
 * 0.2		26/04/2012	Genevieve Turner (GT)	Updated for changes to security
 * 0.3		13/09/2012	Genevieve Turner (GT)	Updated to allow redirect to original url when login page is selected
 * 0.4		31/10/2012	Genevieve Turner (GT)	Updates to allow for a single login landing page that then redirects to other pages
 * </pre>
 * 
 */
@WebServlet(name = "LoginServlet", urlPatterns = "/login")
public class LoginServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	static final Logger LOGGER = LoggerFactory.getLogger(LoginServlet.class);
	
	/**
	 * doGet
	 * 
	 * Redirects to the login page and if there is an error it adds an error string
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		19/03/2012	Rahul Khanna (RK)		Initial build
	 * 0.2		26/04/2012	Genevieve Turner (GT)	Updated for changes to security
	 * 0.3		13/09/2012	Genevieve Turner (GT)	Updated to allow redirect to original url when login page is selected
	 * 0.4		31/10/2012	Genevieve Turner (GT)	Updates to allow for a single login landing page that then redirects to other pages
	 * </pre>
	 * 
	 * @param request a HttpServletRequest object that contains the request the client has made of the servlet
	 * @param response a HttpServletResponse object that contains the response the servlet sends to the client 
	 * @throws ServletException
	 * @throws IOException
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String errorParam = request.getParameter("error");
		String methodParam = request.getParameter("method");
		if("true".equals(errorParam)) {
			request.setAttribute("error", "You have entered an invalid username or password");
		}
		else {
			request.removeAttribute("error");
			SavedRequest savedRequest = (SavedRequest) request.getSession().getAttribute(WebAttributes.SAVED_REQUEST);
			if (savedRequest == null) {
				String referer = request.getHeader("Referer");
				if (Util.isNotEmpty(referer)) {
					LOGGER.trace("Referer on login page is: {}", referer);
					PortResolver portResolver = new PortResolverImpl();
					DefaultSavedRequest savedRequestToSet = new DefaultSavedRequest (request, portResolver);
					request.getSession().setAttribute(WebAttributes.SAVED_REQUEST, savedRequestToSet);
				}
			}
		}
		RequestDispatcher requestDispatcher = null;
		
		if ("anu".equals(methodParam)) {
			/*StringBuilder casURL = new StringBuilder();
			casURL.append(GlobalProps.getProperty(GlobalProps.PROP_CAS_SERVER));
			casURL.append("/login?service=");
			casURL.append(GlobalProps.getProperty(GlobalProps.PROP_APP_SERVER));
			casURL.append(request.getServletContext().getContextPath());
			casURL.append("/j_spring_cas_security_check");
			response.sendRedirect(casURL.toString());*/
			StringBuilder shibURL = new StringBuilder();
			shibURL.append(GlobalProps.getProperty(GlobalProps.PROP_APP_SERVER));
			shibURL.append("/Shibboleth.sso/Login?target=");
			shibURL.append(GlobalProps.getProperty(GlobalProps.PROP_APP_SERVER));
			shibURL.append("/DataCommons");
			LOGGER.info("URL: {}", shibURL.toString());
			response.sendRedirect(shibURL.toString());
			return;
		}
		else if ("registered".equals(methodParam)) {
			requestDispatcher = request.getRequestDispatcher("jsp/login.jsp");
		}
		else {
			requestDispatcher = request.getRequestDispatcher("jsp/login_select.jsp");
		}
		requestDispatcher.forward(request, response);
	}
}
