package au.edu.anu.dcbag;

import java.lang.Exception;

public class DcBagException extends Exception
{
	private static final long serialVersionUID = 1L;

	public DcBagException()
	{
		super();
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
