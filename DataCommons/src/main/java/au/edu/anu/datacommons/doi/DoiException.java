package au.edu.anu.datacommons.doi;

/**
 * This exception is thrown when a DOI request is unable to be processed or is processed with unexpected results.
 */
public class DoiException extends Exception
{
	private static final long serialVersionUID = 1L;

	/**
	 * @see {@link Exception#Exception()}
	 */
	public DoiException()
	{
		super();
	}

	/**
	 * @see {@link Exception#Exception(String, Throwable)}
	 */
	public DoiException(String message, Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * @see {@link Exception#Exception(String)}
	 */
	public DoiException(String message)
	{
		super(message);
	}

	/**
	 * @see {@link Exception#Exception(Throwable)}
	 */
	public DoiException(Throwable cause)
	{
		super(cause);
	}
}
