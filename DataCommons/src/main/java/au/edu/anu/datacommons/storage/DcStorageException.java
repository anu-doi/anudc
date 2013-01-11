package au.edu.anu.datacommons.storage;

public class DcStorageException extends Exception
{
	private static final long serialVersionUID = 1L;

	/**
	 * @see Exception#Exception()
	 */
	public DcStorageException()
	{
		super();
	}

	/**
	 * @see Exception#Exception(String)
	 */
	public DcStorageException(String message)
	{
		super(message);
	}

	/**
	 * @see Exception#Exception(Throwable)
	 */
	public DcStorageException(Throwable cause)
	{
		super(cause);
	}

	/**
	 * @see Exception#Exception(String, Throwable)
	 */
	public DcStorageException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
