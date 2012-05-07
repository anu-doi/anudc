package au.edu.anu.datacommons.upload;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import au.edu.anu.datacommons.properties.GlobalProps;

/**
 *	FileUploadServlet
 * 
 *  Autralian National University Data Commons
 *  
 *  This servlet performs the following functions:
 *  <ol>
 *  <li>Accept and process a POST request that submits a file for uploading.
 *  <li>Acts as an AJAX provider by allowing a client to request progress updates while a file is being uploaded. The response is in XML format
 *  </ol>
 *  
 *  <pre>
 *  Version	Date		Developer			Description
 *  0.1		20/03/2012	Rahul Khanna (RK)	Initial
 *  </pre>
 *
 */
@WebServlet(name = "FileUploadServlet", urlPatterns = "/upload/upload.do")
public class FileUploadServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	private final Logger log = Logger.getLogger(this.getClass().getName());
	private FileUploadListener listener;

	/**
	 * doGet
	 * 
	 * Autralian National University Data Commons
	 * 
	 * Called when a Get request is called to this servlet.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		20/03/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		PrintWriter respWriter = response.getWriter();
		HttpSession curSession = request.getSession();
		FileUploadListener listener = null;
		StringBuffer xmlResp = new StringBuffer();
		long bytesRead = 0L;
		long contentLength = 0L;

		if (curSession == null)
		{
			return;
		}
		else
		{
			log.info("Session ID in Get: " + curSession.getId());
			listener = (FileUploadListener) curSession.getAttribute("LISTENER");

			if (listener == null)
			{
				return;
			}
			else
			{
				bytesRead = listener.getBytesRead();
				contentLength = listener.getContentLength();
			}
		}

		response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
		response.setContentType("text/xml");

		xmlResp.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
		xmlResp.append("<response>\n");
		xmlResp.append("\t<bytes_read>" + bytesRead + "</bytes_read>\n");
		xmlResp.append("\t<content_length>" + contentLength + "</content_length>\n");

		if (bytesRead == contentLength)
		{
			xmlResp.append("\t<finished />\n");
			curSession.setAttribute("LISTENER", null);
		}
		else
		{
			long percentComplete = ((100 * bytesRead) / contentLength);
			xmlResp.append("\t<percent_complete>" + String.valueOf(percentComplete) + "</percent_complete>\n");
		}

		xmlResp.append("</response>\n");

		respWriter.println(xmlResp.toString());
		respWriter.flush();
		respWriter.close();
	}

	/**
	 * doPost
	 * 
	 * Autralian National University Data Commons
	 * 
	 * Called when a file is uploaded using form post method.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		20/03/2012	Rahul Khanna		Initial.
	 * </pre>
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		// Check if this is a file upload request. If not, redirect to the referer page.
		// TODO Display an error before redirecting.
		if (!ServletFileUpload.isMultipartContent(request))
		{
			log.warning("Unexpected POST request format. Redirecting to " + request.getHeader("referer"));
			response.sendRedirect(request.getHeader("referer"));
		}

		// Create a new file upload handler using properties from FileUpload.properties. Use defaults when property not specified.
		ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory(Integer.parseInt(GlobalProps.getProperty(GlobalProps.PROP_UPLOAD_MAXSIZEINMEM,
				String.valueOf(DiskFileItemFactory.DEFAULT_SIZE_THRESHOLD))), new File(GlobalProps.getProperty(GlobalProps.PROP_UPLOAD_TEMPDIR))));
		Properties dsFileProps = new Properties();
		FileWriter filePropsWriter = null;

		// Setup the listener that's going to provide the progress information to AJAX requests.
		listener = new FileUploadListener();
		HttpSession session = request.getSession();
		session.setAttribute("LISTENER", listener);
		upload.setProgressListener(listener);

		// List<FileItem> uploadedItems = null;
		String serverFilePath = GlobalProps.getProperty(GlobalProps.PROP_UPLOAD_DIR);

		try
		{
			@SuppressWarnings("unchecked")
			List<FileItem> uploadedItems = (List<FileItem>) upload.parseRequest(request);
			Iterator<FileItem> iItem = uploadedItems.iterator();

			log.info("Session ID in Post: " + session.getId());

			// Iterate through each param that's been sent in the POST request.
			while (iItem.hasNext())
			{
				FileItem fileItem = (FileItem) iItem.next();
				// If this is the actual file then upload it, else, send it off to the associated properties file of the file upload.
				if (fileItem.isFormField() == false)
				{
					if (fileItem.getSize() > 0)
					{
						File uploadedFileOnServer = null;
						String clientFullFilename = fileItem.getName();
						String clientSlashType = (clientFullFilename.lastIndexOf("\\") > 0) ? "\\" : "/";
						int clientFilenameStartIndex = clientFullFilename.lastIndexOf(clientSlashType);

						String serverFilename = clientFullFilename.substring(clientFilenameStartIndex + 1, clientFullFilename.length());
						uploadedFileOnServer = new File(serverFilePath, serverFilename);
						log.info("File received by server. Writing file on server's local drive...");
						fileItem.write(uploadedFileOnServer);
						log.info("Done.");

						filePropsWriter = new FileWriter(serverFilePath + "\\" + serverFilename + ".properties");
					}
				}
				else
				{
					// Not a file item, i.e. normal form field. Check if there's a value against the parameter. If not, don't include in the properties file.
					if (!fileItem.getString().equals(""))
						dsFileProps.setProperty(fileItem.getFieldName(), fileItem.getString());
				}
			}
		}
		catch (FileUploadException e)
		{
			e.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
		}

		// Destroy the listener and remove it from the session attributes map.
		listener = null;
		session.removeAttribute("LISTENER");
		
		// Write the properties to the file.
		// TODO: Update comments to something useful.
		dsFileProps.store(filePropsWriter, "Comments");

		// Close the properties file writer object.
		if (filePropsWriter != null)
			filePropsWriter.close();
	}
}
