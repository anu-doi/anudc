package au.edu.anu.datacommons.webservice;

public class UnauthorisedException extends Exception
{
	private static final long serialVersionUID = 1L;

	public UnauthorisedException()
	{
		super();
	}

	public UnauthorisedException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public UnauthorisedException(String message)
	{
		super(message);
	}

	public UnauthorisedException(Throwable cause)
	{
		super(cause);
	}
}
