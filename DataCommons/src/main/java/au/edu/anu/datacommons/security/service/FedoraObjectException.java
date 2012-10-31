package au.edu.anu.datacommons.security.service;

public class FedoraObjectException extends Exception
{
	private static final long serialVersionUID = 1L;

	public FedoraObjectException()
	{
		super();
	}

	public FedoraObjectException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public FedoraObjectException(String message)
	{
		super(message);
	}

	public FedoraObjectException(Throwable cause)
	{
		super(cause);
	}
}
