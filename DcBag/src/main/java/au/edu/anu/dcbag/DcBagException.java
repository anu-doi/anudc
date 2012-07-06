package au.edu.anu.dcbag;

public class DcBagException extends Exception
{
	private static final long serialVersionUID = 1L;

	public DcBagException()
	{
		super();
	}

	public DcBagException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public DcBagException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public DcBagException(String message)
	{
		super(message);
	}

	public DcBagException(Throwable cause)
	{
		super(cause);
	}
}
