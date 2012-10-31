package au.edu.anu.datacommons.doi;

public class DoiException extends Exception
{
	private static final long serialVersionUID = 1L;

	public DoiException()
	{
		super();
	}

	public DoiException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public DoiException(String message)
	{
		super(message);
	}

	public DoiException(Throwable cause)
	{
		super(cause);
	}
}
