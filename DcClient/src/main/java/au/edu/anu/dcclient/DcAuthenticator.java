package au.edu.anu.dcclient;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

/**
 * This class extends the Authenticator class to enable credentials management.
 */
public class DcAuthenticator extends Authenticator
{
	private final String username;
	private final String password;
	
	/**
	 * Creates a new instance of DcAuthenticator with specified username and password that will be returned when getPasswordAuthentication is called.
	 * 
	 * @param username
	 *            Username
	 * @param password
	 *            Password
	 */
	public DcAuthenticator(String username, String password)
	{
		super();
		this.username = username;
		this.password = password;
		CustomClient.setAuth(username, password);
	}
	
	@Override
	public PasswordAuthentication getPasswordAuthentication()
	{
		return new PasswordAuthentication(this.username, this.password.toCharArray());
	}
}
