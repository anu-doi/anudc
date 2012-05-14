package au.edu.anu.datacommons.upload;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;

import au.edu.anu.datacommons.data.fedora.FedoraBroker;
import au.edu.anu.datacommons.properties.GlobalProps;
import au.edu.anu.datacommons.util.Util;

import com.sun.jersey.api.view.Viewable;

/**
 * UploadService
 * 
 * Australian National University Data Commons
 * 
 * This class accepts POST requests for uploading files to datastreams in a Collection. The files are uploaded using JUpload applet.
 * 
 * <pre>
 * Version	Date		Developer			Description
 * 0.1		14/05/2012	Rahul Khanna (RK)	Initial
 * </pre>
 */
@Path("/upload")
public class UploadService
{
	private static final Logger LOGGER = LoggerFactory.getLogger(UploadService.class);
	private static final String TEMP_DIR = GlobalProps.getProperty(GlobalProps.PROP_UPLOAD_TEMPDIR);
	private static final String UPLOAD_DIR = GlobalProps.getProperty(GlobalProps.PROP_UPLOAD_DIR);
	private static final String PART_FILE_SUFFIX = ".part";
	private static final String UPLOAD_JSP = "/upload.jsp";

	/**
	 * doGetAsHtml
	 * 
	 * Australian National University Data Commons
	 * 
	 * Accepts Get requests to display the file upload page.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		14/05/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @return HTML page as response.
	 */
	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response doGetAsHtml()
	{
		LOGGER.info("In doGetAsHtml");
		LOGGER.info(SecurityContextHolder.getContext().getAuthentication().getName());

		return Response.ok(new Viewable(UPLOAD_JSP)).build();
	}

	/**
	 * doPostAsHtml
	 * 
	 * Australian National University Data Commons
	 * 
	 * Accepts POST requests from a JUpload applet and saves the files on the server for further processing. Creates a placeholder datastream in the fedora
	 * object preventing reuploading to the same datastream.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		14/05/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @param request
	 *            HTTPServletRequest object.
	 * @return A response with status information.
	 */
	@POST
	@Produces(MediaType.TEXT_HTML)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response doPostAsHtml(@Context HttpServletRequest request)
	{
		Response resp = null;
		Properties dsUploadProps = new Properties();
		List<FileItem> uploadedItems;
		ServletFileUpload upload;
		String jupart = request.getParameter("jupart");
		String jufinal = request.getParameter("jufinal");
		int partNum = 0;
		boolean isLastPart = false;

		// Check if this is a part file. Files greater than threshold specified in JUpload params will be split up and sent as parts.
		if (Util.isNotEmpty(jupart))
		{
			partNum = Integer.parseInt(jupart);

			// Check if this is the final part of a file being send in parts.
			if (Util.isNotEmpty(jufinal))
				isLastPart = jufinal.equals("1");
		}

		// Create a new file upload handler.
		upload = new ServletFileUpload(new DiskFileItemFactory(Integer.parseInt(GlobalProps.getProperty(GlobalProps.PROP_UPLOAD_MAXSIZEINMEM,
				String.valueOf(DiskFileItemFactory.DEFAULT_SIZE_THRESHOLD))), new File(TEMP_DIR)));

		try
		{
			// Get a list of uploaded items. Some may be files, others form fields.
			uploadedItems = (List<FileItem>) upload.parseRequest(request);
			LOGGER.debug("{} items uploaded. Processing each one now...", uploadedItems.size());

			// Iterate through each file item and process it.
			for (FileItem iFileItem : uploadedItems)
			{
				LOGGER.debug("Processing file item with details, contentType={}, fieldName={}, name={}.", new Object[]
				{ iFileItem.getContentType(), iFileItem.getFieldName(), iFileItem.getName() });

				// Process this item based on its type - file or form field.
				if (!iFileItem.isFormField())
				{
					LOGGER.trace("Processing File Item");
					saveFileOnServer(iFileItem, partNum);

					// Check if this is the final part file. If yes, merge all parts.
					if (isLastPart)
						processFinalPartFile(iFileItem, partNum);

					dsUploadProps.setProperty(iFileItem.getFieldName(), iFileItem.getName());
				}
				else
				{
					LOGGER.trace("Processing Form Field");

					// Check if the property actually has a value.
					if (Util.isNotEmpty(iFileItem.getString()))
					{
						// TODO Only include valid properties. Skip over props not required. Determine which ones are not required. 
						if (iFileItem.getFieldName().trim().equals("url"))
						{
							if (!dsUploadProps.containsValue(iFileItem.getString()))
							{
								int i;
								for (i = 0; dsUploadProps.containsKey("url" + i); i++)
									;

								dsUploadProps.setProperty(iFileItem.getFieldName() + i, iFileItem.getString());
							}
						}
						else
						{
							dsUploadProps.setProperty(iFileItem.getFieldName(), iFileItem.getString());
							LOGGER.debug("Added {}={} to properties file.", iFileItem.getFieldName(), iFileItem.getString());
						}
					}
				}
			}

			// Generate the properties filename: "[pid]-[dsid].properties" .
			String dsPropFullFilename = UPLOAD_DIR + File.separator + Util.convertToDiskSafe(dsUploadProps.getProperty("pid")) + "-"
					+ Util.convertToDiskSafe(dsUploadProps.getProperty("dsid")) + ".properties";

			// Check if the properties file already exists. If yes, merge the new one with the existing one.
			File dsPropFile = new File(dsPropFullFilename);
			if (dsPropFile.exists())
			{
				Properties existingProps = new Properties();
				existingProps.load(new FileInputStream(dsPropFile));
				mergeProperties(existingProps, dsUploadProps);
				dsUploadProps = existingProps;
			}

			// Write the properties to the file.
			FileWriter dsPropFileWriter = new FileWriter(dsPropFile);
			// TODO: Update comments to something useful.
			dsUploadProps.store(dsPropFileWriter, "Comments");
			dsPropFileWriter.close();

			// Create a placeholder datastream.
			FedoraBroker.addDatastreamBySource(dsUploadProps.getProperty("pid"), dsUploadProps.getProperty("dsid"), dsUploadProps.getProperty("Label"),
					"<text>Pending processing of uploaded files.</text>");

			// Text of response must adhere to param 'stringUploadSuccess' specified in the JUpload applet.
			resp = Response.ok("SUCCESS", MediaType.TEXT_PLAIN_TYPE).build();
		}
		catch (FileUploadException e)
		{
			LOGGER.error("Unable to process POST request.", e);
			resp = Response.ok("ERROR: Unable to process request.", MediaType.TEXT_PLAIN_TYPE).build();
		}
		catch (IOException e)
		{
			LOGGER.error("Unable to process POST request.", e);
			resp = Response.ok("ERROR: Unable to process request.", MediaType.TEXT_PLAIN_TYPE).build();
		}
		catch (Exception e)
		{
			LOGGER.error("Unable to process POST request.", e);
			resp = Response.ok("ERROR: Unable to process request.", MediaType.TEXT_PLAIN_TYPE).build();
		}

		return resp;
	}

	/**
	 * saveFileOnServer
	 * 
	 * Australian National University Data Commons
	 * 
	 * Saves a file in a FileItem object on the server.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		14/05/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @param fileItem
	 *            FileItem object
	 * @param partNum
	 *            -1 if the file is a complete file, else a number from 0-(n-1) where n is the number of parts a file is split up into.
	 * @throws Exception
	 *             Thrown when unable to save the file on the server.
	 */
	private void saveFileOnServer(FileItem fileItem, int partNum) throws Exception
	{
		String fieldName = fileItem.getFieldName();
		String clientFullFilename = fileItem.getName();
		String serverFilename = getFilenameFromPath(clientFullFilename);
		long sizeInBytes = fileItem.getSize();

		LOGGER.debug("fieldname: {}, filename: {}.", fieldName, clientFullFilename);
		LOGGER.debug("Filesize: {}.", sizeInBytes);

		// Append .part[n] to the filename if its a part file.
		if (partNum > 0)
			serverFilename += PART_FILE_SUFFIX + partNum;

		try
		{
			File fileOnServer = new File(partNum == 0 ? UPLOAD_DIR : TEMP_DIR, serverFilename);

			// Check if the file already exists on server. If not, write the file. Otherwise check if the file on server's the same size as the one uploaded. If not, write it. 
			if (!fileOnServer.exists() || (fileOnServer.exists() && fileOnServer.length() != sizeInBytes))
			{
				LOGGER.debug("Writing file {}", fileOnServer.getAbsoluteFile());
				// Write the file into the Temp directory.
				fileItem.write(fileOnServer);
			}
			else
			{
				LOGGER.warn("File {} exists on server with same size. Not overwriting it.", fileOnServer.getAbsoluteFile());
			}
		}
		catch (Exception e)
		{
			LOGGER.error("Exception writing to file: " + e.toString());
			throw e;
		}
	}

	/**
	 * processFinalPartFile
	 * 
	 * Australian National University Data Commons
	 * 
	 * This method is called after the final part of a file has been saved on the server. This method merges the parts into a single file.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		14/05/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @param fileItem
	 *            FileItem object.
	 * @param partNum
	 *            Part number has int
	 * @throws Exception
	 *             Thrown if the files could not be merged.
	 */
	private void processFinalPartFile(FileItem fileItem, int partNum) throws Exception
	{
		BufferedInputStream partInStream = null;
		byte[] buffer = new byte[8192];				// 8 KB.
		int numBytesRead;
		String partFilename;

		// Open a FileOutputStream in the Target directory where the merged file will be placed.
		BufferedOutputStream mergedFile = new BufferedOutputStream(new FileOutputStream(UPLOAD_DIR + File.separator + fileItem.getName()));

		LOGGER.debug("Processing final file part# {}", partNum);

		try
		{
			// Merge individual file parts.
			LOGGER.debug("Merging file parts...");
			for (int i = 1; i <= partNum; i++)
			{
				// Open the FileInputStream to read from the part file.
				partFilename = fileItem.getName() + PART_FILE_SUFFIX + i;
				partInStream = new BufferedInputStream(new FileInputStream(TEMP_DIR + File.separator + partFilename));

				// Read bytes and add them to the merged file until all files in this file part have been merged.
				while ((numBytesRead = partInStream.read(buffer)) != -1)
				{
					mergedFile.write(buffer, 0, numBytesRead);
				}

				// Close FileInputStream of part file.
				partInStream.close();
			}

			// Close the merged file.
			mergedFile.close();

			LOGGER.debug("Merged into {}", UPLOAD_DIR + File.separator + fileItem.getName());
		}
		catch (Exception e)
		{
			if (partInStream != null)
				partInStream.close();

			if (mergedFile != null)
				mergedFile.close();

			throw e;
		}

		// Delete the part files now that they've been merged.
		for (int i = 1; i <= partNum; i++)
		{
			partFilename = fileItem.getName() + PART_FILE_SUFFIX + i;
			File file = new File(TEMP_DIR + File.separator + partFilename);
			if (!file.delete())
			{
				LOGGER.warn("Unable to delete part file {}.", partFilename);
			}
		}
	}

	/**
	 * getFilenameFromPath
	 * 
	 * Australian National University Data Commons
	 * 
	 * Gets the filename part from a full filename on the client that may contain file separators different from the ones used on the server.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		14/05/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @param fullFilename
	 *            Full path and filename as on the client's computer.
	 * @return Filename only as String.
	 */
	private String getFilenameFromPath(String fullFilename)
	{
		// Extract the type of slash being used in the filename.
		char clientSlashType = (fullFilename.lastIndexOf("\\") > 0) ? '\\' : '/';

		// Get the index where the filename starts. -1 if the path isn't specified.
		int clientFilenameStartIndex = fullFilename.lastIndexOf(clientSlashType);

		// Get the part of the string after the last instance of the path separator.
		String filename = fullFilename.substring(clientFilenameStartIndex + 1, fullFilename.length());
		return filename;
	}

	/**
	 * mergeProperties
	 * 
	 * Australian National University Data Commons
	 * 
	 * Merges properties in source into properties in target. If there are conflicting keys, increment the key counter until no keys in the target get
	 * overridden. For example, if File0 and File1 exists in the target, File0 will be renamed to File2 and merged into the target.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		14/05/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @param target
	 *            Properties object the properties are to be merged into.
	 * @param source
	 *            Properties object the properties are to be merged from.
	 */
	private void mergeProperties(Properties target, Properties source)
	{
		// Check for file keys.
		for (int iSource = 0; source.containsKey("File" + iSource); iSource++)
		{
			if (!target.containsValue(source.get("File" + iSource)))
			{
				// Find the next available file number.
				int iNextAvail;
				for (iNextAvail = 0; target.containsKey("File" + iNextAvail); iNextAvail++)
					;

				// Pull the properties from the source, change the number suffix and add them to target so they're not overwriting existing properties in the target.
				target.put("File" + iNextAvail, source.getProperty("File" + iSource));
				target.put("mimeType" + iNextAvail, source.getProperty("mimetype" + iSource));
				target.put("pathinfo" + iNextAvail, source.getProperty("pathinfo" + iSource));
				target.put("md5sum" + iNextAvail, source.getProperty("md5sum" + iSource));
				target.put("filemodificationdate" + iNextAvail, source.getProperty("filemodificationdate" + iSource));
			}
		}
	}
}
