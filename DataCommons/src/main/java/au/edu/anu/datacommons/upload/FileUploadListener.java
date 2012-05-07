package au.edu.anu.datacommons.upload;

import java.util.logging.Logger;

import javax.servlet.annotation.WebListener;

import org.apache.commons.fileupload.ProgressListener;

/**
 * Application Lifecycle Listener implementation class FileUploadListener
 * 
 */
@WebListener
public class FileUploadListener implements ProgressListener
{
	@SuppressWarnings("unused")
	private final Logger log = Logger.getLogger(this.getClass().getName());

	private volatile long bytesRead = 0L;
	private volatile long contentLength = 0L;
	private volatile long item = 0L;

	public FileUploadListener()
	{
		super();
	}

	public void update(long aBytesRead, long aContentLength, int anItem)
	{
		bytesRead = aBytesRead;
		contentLength = aContentLength;
		item = anItem;
		int i = 0;
		log.info("Update called." + bytesRead + "/" + contentLength);
	}

	public long getBytesRead()
	{
		return bytesRead;
	}

	public long getContentLength()
	{
		return contentLength;
	}

	public long getItem()
	{
		return item;
	}
}
