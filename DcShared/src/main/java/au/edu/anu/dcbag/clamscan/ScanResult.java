// Source: https://github.com/philvarner/clamavj

package au.edu.anu.dcbag.clamscan;

/**
 * This class contains the results of a virus scan performed with ClamScan.
 * 
 * @see <a href="https://github.com/philvarner/clamavj/">https://github.com/philvarner/clamavj/</a>
 */
public class ScanResult {

    private String result = "";
    private Status status = Status.FAILED;
    private String signature = "";
    private Exception exception = null;

    public enum Status {PASSED, FAILED, ERROR}

    public static final String STREAM_PREFIX = "stream: ";
    public static final String RESPONSE_OK = "stream: OK";
    public static final String FOUND_SUFFIX = "FOUND";

    public static final String RESPONSE_SIZE_EXCEEDED = "INSTREAM size limit exceeded. ERROR";
    public static final String RESPONSE_ERROR_WRITING_FILE = "Error writing to temporary file. ERROR";

    /**
	 * Instantiates a new scan result with a specified String returned by the ClamAV service.
	 * 
	 * @param result
	 *            the result
	 */
    public ScanResult(String result) {
        setResult(result);
    }

	/**
	 * Instantiates a new scan result with a specified exception when the ClamAV service didn't return a result as a result of an exception being thrown.
	 * 
	 * @param ex
	 *            the exception thrown
	 */
    public ScanResult(Exception ex) {
        setException(ex);
        setStatus(Status.ERROR);
    }

    /**
	 * Gets the exception.
	 * 
	 * @return the exception
	 */
    public Exception getException() {
        return exception;
    }

    /**
	 * Sets the exception.
	 * 
	 * @param exception
	 *            the new exception
	 */
    public void setException(Exception exception) {
        this.exception = exception;
    }

    /**
	 * Gets the result.
	 * 
	 * @return the result
	 */
    public String getResult() {
        return result;
    }

    /**
	 * Sets the result.
	 * 
	 * @param result
	 *            the new result
	 */
    public void setResult(String result) {
        this.result = result;

        if (result == null) {
            setStatus(Status.ERROR);
        } else if (RESPONSE_OK.equals(result)) {
            setStatus(Status.PASSED);
        } else if (result.endsWith(FOUND_SUFFIX)) {
            setSignature(result.substring(STREAM_PREFIX.length(), result.lastIndexOf(FOUND_SUFFIX) - 1));
        } else if (RESPONSE_SIZE_EXCEEDED.equals(result)) {
            setStatus(Status.ERROR);
        } else if (RESPONSE_ERROR_WRITING_FILE.equals(result)) {
            setStatus(Status.ERROR);
        }

    }

    /**
	 * Gets the virus signature.
	 * 
	 * @return the virus signature
	 */
    public String getSignature() {
        return signature;
    }

    /**
	 * Sets the virus signature.
	 * 
	 * @param signature
	 *            the new signature
	 */
    private void setSignature(String signature) {
        this.signature = signature;
    }

    /**
	 * Gets the status.
	 * 
	 * @return the status
	 */
    public Status getStatus() {
        return status;
    }

    /**
	 * Sets the status.
	 * 
	 * @param status
	 *            the new status
	 */
    public void setStatus(Status status) {
        this.status = status;
    }
}
