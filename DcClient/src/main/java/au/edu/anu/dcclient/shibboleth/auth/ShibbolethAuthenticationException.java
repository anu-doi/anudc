package au.edu.anu.dcclient.shibboleth.auth;

/*
 * ShibbolethAuthenticationException
 *
 * Australian National University Data Commons
 * 
 * Authentication Exception 
 *
 * JUnit coverage:
 * None
 * 
 * @author Genevieve Turner
 *
 */
public class ShibbolethAuthenticationException extends Exception
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Constructor
	 * 
	 * @param message The message to add with the exception
	 */
	public ShibbolethAuthenticationException(String message)
	{
		super(message);
	}
	
	/**
	 * Constructor
	 * 
	 * @param message The message to add with the exception
	 * @param throwable The throwable object to go with the exception
	 */
	public ShibbolethAuthenticationException(String message, Throwable throwable)
	{
		super(message, throwable);
	}
}
