package au.edu.anu.dcclient;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

public class DcAuthenticator extends Authenticator
{
	private String username;
	private String password;
	
	public DcAuthenticator(String username, String password)
	{
		super();
		this.username = username;
		this.password = password;
	}
	
	@Override
	public PasswordAuthentication getPasswordAuthentication()
	{
		return new PasswordAuthentication(this.username, this.password.toCharArray());
	}
}
