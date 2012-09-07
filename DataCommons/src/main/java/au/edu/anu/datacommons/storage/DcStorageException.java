package au.edu.anu.datacommons.storage;

public class DcStorageException extends Exception
{
	private static final long serialVersionUID = 1L;

	public DcStorageException()
	{
		super();
	}

	public DcStorageException(String message)
	{
		super(message);
	}

	public DcStorageException(Throwable cause)
	{
		super(cause);
	}

	public DcStorageException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
}
