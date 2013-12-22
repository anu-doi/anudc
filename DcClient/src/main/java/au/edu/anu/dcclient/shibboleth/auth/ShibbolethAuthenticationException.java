package au.edu.anu.dcclient.shibboleth.auth;

public class ShibbolethAuthenticationException extends Exception
{
	private static final long serialVersionUID = 1L;
	
	public ShibbolethAuthenticationException(String message)
	{
		super(message);
	}
	
	public ShibbolethAuthenticationException(String message, Throwable throwable)
	{
		super(message, throwable);
	}
}
