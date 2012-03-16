package au.edu.anu.datacommons.login;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import au.edu.anu.datacommons.ldap.LdapPerson;
import au.edu.anu.datacommons.ldap.LdapRequest;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet(name = "LoginServlet", urlPatterns = "/login/login.do")
public class LoginServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	private final Logger log = Logger.getLogger(this.getClass().getName());

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public LoginServlet()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		if (request.getParameter("cmd").equalsIgnoreCase("logout"))
		{
			log.info("Invalidating Session ID: " + request.getSession().getId());
			request.getSession().invalidate();
		}
		response.sendRedirect(request.getContextPath() + "/");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		
		if (username.equals("") || password.equals(""))
			response.sendRedirect(request.getContextPath() + "/jsp/login.jsp?err=blank");

		try
		{
			LdapRequest authReq = new LdapRequest();
			if (authReq.authenticate(username, password))
			{
				LdapPerson user = new LdapRequest().searchUniId(request.getParameter("username"));
				request.getSession().setAttribute("user", user);
				log.info("User : " + user.getDisplayName());
				response.sendRedirect(request.getContextPath() + "/");
			}
			else
			{
				log.info("Invalid credentials.");
				response.sendRedirect(request.getContextPath() + "/jsp/login.jsp?err=authfail");
			}
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
