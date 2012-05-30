package au.edu.anu.datacommons.upload;

import gov.loc.repository.bagit.Bag.Format;
import gov.loc.repository.bagit.BagFactory.LoadOption;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.PropertyException;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
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
	private static final String PART_FILE_SUFFIX = ".part";
	private static final String UPLOAD_JSP = "/upload.jsp";
	private static final String FILE_DS_PREFIX = "FILE";

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
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	public Response doGetAsHtml()
	{
		LOGGER.info("In doGetAsHtml");
		LOGGER.info("Username: " + SecurityContextHolder.getContext().getAuthentication().getName());

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
		Properties uploadProps = new Properties();
		List<FileItem> uploadedItems;
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

		try
		{
			// Get a list of uploaded items. Some may be files, others form fields.
			uploadedItems = parseUploadRequest(request);
			LOGGER.debug("{} items uploaded. Processing each one now...", uploadedItems.size());

			// Map form fields into a properties object.
			mapFormFields(uploadedItems, uploadProps);

			// Iterate through each file item and process if it's a file - form fields already processed.
			for (FileItem iItem : uploadedItems)
			{
				LOGGER.debug("Processing file item with details, contentType={}, fieldName={}, name={}.",
						new Object[] { iItem.getContentType(), iItem.getFieldName(), iItem.getName() });
				if (!iItem.isFormField())					// File Item.
				{
					LOGGER.trace("Processing File Item");
					saveFileOnServer(iItem, partNum, isLastPart, uploadProps.getProperty("pid"));
					uploadProps.setProperty(formatFieldName(iItem.getFieldName()), iItem.getName());
				}
			}

			// Check if the properties file '[pid].properties' already exists. If yes, merge the new one with the existing one.
			File dsPropFile = new File(GlobalProps.getUploadDirAsFile(), Util.convertToDiskSafe(uploadProps.getProperty("pid")) + ".properties");
			if (dsPropFile.exists())
			{
				Properties existingProps = new Properties();
				existingProps.load(new FileInputStream(dsPropFile));
				mergeProperties(existingProps, uploadProps);
				uploadProps = existingProps;
			}

			// Write the properties to the file.
			FileWriter dsPropFileWriter = new FileWriter(dsPropFile);
			uploadProps.store(dsPropFileWriter, "");		// Blank comment field.
			dsPropFileWriter.close();

			// Create a placeholder datastream.
			FedoraBroker.addDatastreamBySource(uploadProps.getProperty("pid"), FILE_DS_PREFIX + "0", uploadProps.getProperty("Label"),
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

	@GET
	@Produces(MediaType.TEXT_HTML)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Path("bagit/{pid}")
	public Response doGetCreateBag(@PathParam("pid") String pid)
	{
		Properties uploadProps = new Properties();
		Response resp = null;
		try
		{
			// Read properties file [Pid].properties.
			uploadProps.load(new BufferedInputStream(new FileInputStream(
					new File(GlobalProps.getUploadDirAsFile(), Util.convertToDiskSafe(pid) + ".properties"))));

			// Create a new Bag.
			pid = uploadProps.getProperty("pid");
			if (!Util.isNotEmpty(pid))
				throw new Exception("Missing Pid value.");
			DcBag dcBag = new DcBag(Util.convertToDiskSafe(pid));

			// Add the files to the bag.
			String filename;
			for (int i = 0; (filename = uploadProps.getProperty("file" + i)) != null; i++)
			{
				LOGGER.debug("Adding file {} to Bag {}.", filename, pid);
				dcBag.addFileToPayload(new File(new File(GlobalProps.getUploadDirAsFile(), Util.convertToDiskSafe(pid)), filename));
			}

			// Add URLs to Fetch file.
			for (int i = 0; uploadProps.containsKey("url" + i); i++)
			{
				dcBag.addFetchEntry(uploadProps.getProperty("url" + i), 0L, uploadProps.getProperty("url" + i));
				LOGGER.debug("Added URL {} to bag's fetch file.", uploadProps.getProperty("url" + i));
			}

			// Save the bag.
			dcBag.save(Format.FILESYSTEM);
			resp = Response.temporaryRedirect(UriBuilder.fromPath("/upload").queryParam("smsg", "Upload Successful.").build()).build();
		}
		catch (Exception e)
		{
			resp = Response.temporaryRedirect(UriBuilder.fromPath("/upload").queryParam("emsg", "Upload Unsuccessful. Unable to bag file.").build()).build();
		}

		return resp;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("files/{pid}")
	public Response doGetFilesInBagAsJson(@PathParam("pid") String pid)
	{
		DcBag dcBag = null;
		Response resp = null;

		dcBag = new DcBag(new File(GlobalProps.getBagsDirAsFile(), Util.convertToDiskSafe(pid)), LoadOption.BY_MANIFESTS);
		JSONArray filenames = new JSONArray();

		for (Entry<String, String> iEntry : dcBag.getPayloadFileList())
		{
			try
			{
				JSONObject obj = new JSONObject();
				obj.put("name", iEntry.getKey());				// Key is filename, value is Hash value.
				filenames.put(obj);
			}
			catch (JSONException e)
			{
				LOGGER.debug("Unable to add file to JSON Object: " + iEntry.getKey());
			}
		}
		resp = Response.ok(filenames.toString(), MediaType.APPLICATION_JSON_TYPE).build();
		return resp;
	}

	@GET
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@Path("files/{pid}/{fileInBag}")
	public Response doGetFileInBagAsOctetStream(@PathParam("pid") String pid, @PathParam("fileInBag") String filename)
	{
		Response resp = null;
		DcBag dcBag = null;

		LOGGER.debug("pid: {}, filename: {}", pid, filename);

		// TODO Do custom user checking here - if the user has a valid dropbox etc.

		dcBag = new DcBag(new File(GlobalProps.getBagsDirAsFile(), Util.convertToDiskSafe(pid)), LoadOption.BY_FILES);
		ResponseBuilder respBuilder = Response.ok(dcBag.getBagFileStream("data/" + filename), MediaType.APPLICATION_OCTET_STREAM_TYPE);
		respBuilder = respBuilder.header("Content-Disposition", "attachment;filename=" + filename);			// Filename on client's computer.
		respBuilder = respBuilder.header("Content-MD5", dcBag.getBagFileHash("data/" + filename));			// Hash of file. Header not used by most web browsers.
		respBuilder = respBuilder.header("Content-Length", dcBag.getBagFileSize("data/" + filename));		// File size.
		resp = respBuilder.build();
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
	 * @param pid
	 * @throws Exception
	 *             Thrown when unable to save the file on the server.
	 */
	private void saveFileOnServer(FileItem fileItem, int partNum, boolean isLastPart, String pid) throws Exception
	{
		String clientFullFilename = fileItem.getName();
		String serverFilename = getFilenameFromPath(clientFullFilename);

		LOGGER.debug("filename: {}, filesize: {}.", clientFullFilename, fileItem.getSize());

		// Append .part[n] to the filename if its a part file.
		if (partNum > 0)
			serverFilename += PART_FILE_SUFFIX + partNum;

		try
		{
			File pidDir = new File(GlobalProps.getUploadDirAsFile(), Util.convertToDiskSafe(pid));
			if (!pidDir.exists())
				pidDir.mkdir();
			File fileOnServer = new File(pidDir, serverFilename);

			// Check if file already exists in pid dir. If not, write the file. Otherwise check if the file on server's the same size as the one uploaded. If not, write it.
			// TODO Perform hash check instead of checking for same file size.
			if (!fileOnServer.exists() || (fileOnServer.exists() && fileOnServer.length() != fileItem.getSize()))
			{
				LOGGER.debug("Writing file {}", fileOnServer.getAbsolutePath());
				fileItem.write(fileOnServer);
				if (isLastPart)			// If last part then merge part files.
					processFinalPartFile(fileItem, partNum, pidDir);
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
	 * @param pidDir
	 * @throws Exception
	 *             Thrown if the files could not be merged.
	 */
	private void processFinalPartFile(FileItem fileItem, int partNum, File pidDir) throws Exception
	{
		BufferedInputStream partInStream = null;
		byte[] buffer = new byte[8192];				// 8 KB.
		int numBytesRead;
		File[] partFiles = new File[partNum];

		// Open a FileOutputStream in the Target directory where the merged file will be placed.
		BufferedOutputStream mergedFile = new BufferedOutputStream(new FileOutputStream(new File(pidDir, fileItem.getName())));

		LOGGER.debug("Processing final file part# {}", partNum);

		try
		{
			// Merge individual file parts.
			LOGGER.debug("Merging file parts...");
			for (int i = 1; i <= partNum; i++)
			{
				// Open the FileInputStream to read from the part file.
				partFiles[i - 1] = new File(pidDir, fileItem.getName() + PART_FILE_SUFFIX + i);
				partInStream = new BufferedInputStream(new FileInputStream(partFiles[i - 1]));

				// Read bytes and add them to the merged file until all files in this file part have been merged.
				try
				{
					while ((numBytesRead = partInStream.read(buffer)) != -1)
						mergedFile.write(buffer, 0, numBytesRead);
				}
				finally
				{
					partInStream.close();		// Close part file filestream.
				}

			}

			mergedFile.close();		// Close merged filestream.

			LOGGER.debug("Merged into {}", pidDir + File.separator + fileItem.getName());
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
		for (int i = 0; i < partFiles.length; i++)
		{
			if (!partFiles[i].delete())
				LOGGER.warn("Unable to delete part file {}.", partFiles[i].getAbsolutePath());
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
		for (int iSource = 0; source.containsKey("file" + iSource); iSource++)
		{
			if (!target.containsValue(source.get("file" + iSource)))
			{
				// Find the next available file number.
				int iNextAvail;
				for (iNextAvail = 0; target.containsKey("file" + iNextAvail); iNextAvail++)
					;

				// Pull the properties from the source, change the number suffix and add them to target so they're not overwriting existing properties in the target.
				target.put("file" + iNextAvail, source.getProperty("file" + iSource));
				target.put("mimeType" + iNextAvail, source.getProperty("mimetype" + iSource));
				target.put("pathinfo" + iNextAvail, source.getProperty("pathinfo" + iSource));
				target.put("md5sum" + iNextAvail, source.getProperty("md5sum" + iSource));
				target.put("filemodificationdate" + iNextAvail, source.getProperty("filemodificationdate" + iSource));
			}
		}
	}

	@SuppressWarnings("unchecked")
	private List<FileItem> parseUploadRequest(HttpServletRequest request) throws FileUploadException, NumberFormatException, PropertyException
	{
		// Create a new file upload handler.
		ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory(GlobalProps.getMaxSizeInMem(), GlobalProps.getTempDirAsFile()));
		return (List<FileItem>) upload.parseRequest(request);
	}

	private String formatFieldName(String fieldName)
	{
		if (Util.isNotEmpty(fieldName))
			return fieldName.toLowerCase().trim();
		else
			return "";
	}

	private void mapFormFields(List<FileItem> uploadedItems, Properties dsUploadProps)
	{
		// Iterate each item in the request, and extract the form fields first.
		for (FileItem iFileItem : uploadedItems)
		{
			LOGGER.debug("Processing all form fields in this request.");
			if (iFileItem.isFormField() && Util.isNotEmpty(iFileItem.getString()))
			{
				// TODO Only include valid properties. Skip over props not required. Determine which ones are not required. 
				if (formatFieldName(iFileItem.getFieldName()).equals("url"))
				{
					// If the url doesn't exist in the properties file, find the next available index and add it.
					if (!dsUploadProps.containsValue(iFileItem.getString()))
					{
						int i;
						for (i = 0; dsUploadProps.containsKey("url" + i); i++)
							;

						dsUploadProps.setProperty(formatFieldName(iFileItem.getFieldName()) + i, iFileItem.getString().trim());
					}
				}
				else
				{
					dsUploadProps.setProperty(formatFieldName(iFileItem.getFieldName()), iFileItem.getString().trim());
					LOGGER.debug("Added {}={} to properties file.", formatFieldName(iFileItem.getFieldName()), iFileItem.getString().trim());
				}
			}
		}
	}
}
