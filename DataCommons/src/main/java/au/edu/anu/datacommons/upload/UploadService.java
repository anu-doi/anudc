package au.edu.anu.datacommons.upload;

import static java.text.MessageFormat.format;
import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFile;
import gov.loc.repository.bagit.Manifest.Algorithm;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.PropertyException;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import au.edu.anu.datacommons.collectionrequest.CollectionDropbox;
import au.edu.anu.datacommons.collectionrequest.CollectionRequestItem;
import au.edu.anu.datacommons.collectionrequest.PageMessages;
import au.edu.anu.datacommons.collectionrequest.PageMessages.MessageType;
import au.edu.anu.datacommons.data.db.dao.AccessLogRecordDAOImpl;
import au.edu.anu.datacommons.data.db.dao.DropboxDAO;
import au.edu.anu.datacommons.data.db.dao.DropboxDAOImpl;
import au.edu.anu.datacommons.data.db.dao.FedoraObjectDAOImpl;
import au.edu.anu.datacommons.data.db.dao.UsersDAOImpl;
import au.edu.anu.datacommons.data.db.model.FedoraObject;
import au.edu.anu.datacommons.data.db.model.Users;
import au.edu.anu.datacommons.properties.GlobalProps;
import au.edu.anu.datacommons.security.AccessLogRecord;
import au.edu.anu.datacommons.storage.DcStorage;
import au.edu.anu.datacommons.storage.DcStorageException;
import au.edu.anu.datacommons.util.Util;
import au.edu.anu.dcbag.BagSummary;

import com.sun.jersey.api.NotFoundException;
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
@Component
@Scope("request")
public class UploadService
{
	private static final Logger LOGGER = LoggerFactory.getLogger(UploadService.class);
	private static final String PART_FILE_SUFFIX = ".part";
	private static final String UPLOAD_JSP = "/upload.jsp";
	private static final String BAGFILES_JSP = "/bagfiles.jsp";
	private static final DcStorage dcStorage = DcStorage.getInstance();

	@Context
	private UriInfo uriInfo;
	@Context
	private HttpServletRequest request;
	@Context
	private HttpHeaders httpHeaders;

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
	@Path("/")
	@Produces(MediaType.TEXT_HTML)
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	public Response doGetAsHtml()
	{
		LOGGER.trace("In doGetAsHtml");
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
	 * Version	Date		Developer				Description
	 * 0.1		14/05/2012	Rahul Khanna (RK)		Initial
	 * 0.2		13/07/2012	Genevieve Turner (GT)	Added pre-authorization
	 * </pre>
	 * 
	 * @param request
	 *            HTTPServletRequest object.
	 * @return A response with status information.
	 */
	@POST
	@Path("/")
	@Produces(MediaType.TEXT_HTML)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	public Response doPostAsHtml()
	{
		Response resp = null;
		Properties uploadProps = new Properties();
		List<FileItem> uploadedItems;
		int filePartNum = 0;
		boolean isLastPart = false;
		boolean isExtRefsOnly = false;

		StringBuilder headersStr = new StringBuilder();
		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements())
		{
			String headerName = headerNames.nextElement();
			headersStr.append(format("{0}: {1}\r\n", headerName, request.getHeader(headerName)));
		}
		LOGGER.debug("HTTP Request Headers:\r\n{}", headersStr.toString());

		// Check if this is a part file. Files greater than threshold specified in JUpload params will be split up and sent as parts.
		if (Util.isNotEmpty(request.getParameter("jupart")))
		{
			filePartNum = Integer.parseInt(request.getParameter("jupart"));
			// Check if this is the final part of a file being send in parts.
			if (Util.isNotEmpty(request.getParameter("jufinal")))
				isLastPart = request.getParameter("jufinal").equals("1");
		}

		FileWriter propFileWriter = null;
		try
		{
			// Get a list of uploaded items. Some may be files, others form fields.
			uploadedItems = parseUploadRequest(request);

			// Map form fields into a properties object.
			mapFormFields(uploadedItems, uploadProps);

			// Iterate through each file item and process if it's a file - form fields already processed.
			StringBuilder formDataStr = new StringBuilder(format("{0} form data items:\r\n", uploadedItems.size()));
			for (FileItem iItem : uploadedItems)
			{
				if (iItem.isFormField())
				{
					formDataStr.append(format("{0}: {1}\r\n", iItem.getFieldName(), iItem.getString()));
					if (iItem.getFieldName().equalsIgnoreCase("extRefsOnly"))
						isExtRefsOnly = true;
				}
				else
				{
					formDataStr.append(format("{0}: {1} ({2})\r\n", iItem.getFieldName(), iItem.getName(), FileUtils.byteCountToDisplaySize(iItem.getSize())));
					saveFileOnServer(iItem, filePartNum, isLastPart, uploadProps.getProperty("pid"), uploadProps.getProperty("md5sum0"));
					uploadProps.setProperty(formatFieldName(iItem.getFieldName()), iItem.getName());
				}
			}
			LOGGER.debug(formDataStr.toString());

			String pid = uploadProps.getProperty("pid").trim().toLowerCase();

			// Check for write access to the fedora object.
			getFedoraObjectWriteAccess(pid);

			// Check if the properties file '[pid].properties' already exists. If yes, merge the new one with the existing one.
			File propFile = new File(GlobalProps.getUploadDirAsFile(), DcStorage.convertToDiskSafe(pid) + ".properties");
			if (propFile.exists())
			{
				Properties existingProps = new Properties();
				FileInputStream propFileInStream = new FileInputStream(propFile);
				existingProps.load(propFileInStream);
				propFileInStream.close();
				mergeProperties(existingProps, uploadProps);
				uploadProps = existingProps;
			}

			// Write the properties to the file.
			propFileWriter = new FileWriter(propFile);
			uploadProps.store(propFileWriter, "");		// Blank comment field.
			propFileWriter.close();

			// Text of response must adhere to param 'stringUploadSuccess' specified in the JUpload applet.
			if (isExtRefsOnly)
				resp = Response.seeOther(
						UriBuilder.fromUri(uriInfo.getBaseUri()).path(UploadService.class).path(UploadService.class, "doGetCreateBag")
								.build(uploadProps.getProperty("pid"))).build();
			else
				resp = Response.ok("SUCCESS", MediaType.TEXT_PLAIN_TYPE).build();
		}
		catch (Exception e)
		{
			LOGGER.error("Unable to process POST request.", e);
			resp = Response.ok(format("ERROR: Unable to process request. [{0}]", e.getMessage()), MediaType.TEXT_PLAIN_TYPE).build();
		}
		finally
		{
			IOUtils.closeQuietly(propFileWriter);
		}

		return resp;
	}

	@GET
	@Produces(MediaType.TEXT_HTML)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Path("bagit/{pid}")
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	public Response doGetCreateBag(@PathParam("pid") String pid)
	{
		Properties uploadProps = new Properties();
		File propFile = null;
		File uploadedFilesDir = null;
		Response resp = null;
		BufferedInputStream propFileInStream = null;
		AccessLogRecord accessRec = null;
		UriBuilder redirUri = UriBuilder.fromUri(uriInfo.getBaseUri()).path(UploadService.class);

		// Check for write access to the fedora object.
		getFedoraObjectWriteAccess(pid);

		try
		{
			// Read properties file [Pid].properties.
			propFile = new File(GlobalProps.getUploadDirAsFile(), DcStorage.convertToDiskSafe(pid) + ".properties");
			propFileInStream = new BufferedInputStream(new FileInputStream(propFile));
			uploadProps.load(propFileInStream);

			// Check the pid in the URL against the one in the properties file.
			if (!pid.equalsIgnoreCase(uploadProps.getProperty("pid")))
				throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);

			// Create access log.
			if (dcStorage.bagExists(pid))
				accessRec = new AccessLogRecord(uriInfo.getPath(), getCurUser(), request.getRemoteAddr(), AccessLogRecord.Operation.UPDATE);
			else
				accessRec = new AccessLogRecord(uriInfo.getPath(), getCurUser(), request.getRemoteAddr(), AccessLogRecord.Operation.CREATE);

			// Add files to bag.
			uploadedFilesDir = new File(GlobalProps.getUploadDirAsFile(), DcStorage.convertToDiskSafe(pid));
			String filename;
			for (int i = 0; (filename = uploadProps.getProperty("file" + i)) != null; i++)
			{
				LOGGER.debug("Adding file {} to Bag {}.", filename, pid);
				dcStorage.addFileToBag(pid, new File(uploadedFilesDir, filename));
			}

			// Add External Reference URLs.
			for (int i = 0; uploadProps.containsKey("url" + i); i++)
			{
				dcStorage.addExtRef(pid, uploadProps.getProperty("url" + i));
				LOGGER.debug("Added URL {} as external reference.", uploadProps.getProperty("url" + i));
			}

			// Save the access log record created earlier for the activity performed.
			new AccessLogRecordDAOImpl(AccessLogRecord.class).create(accessRec);
			resp = Response.temporaryRedirect(
					redirUri.path(UploadService.class, "doGetBagFileListingAsHtml").queryParam("smsg", "Upload Successful.").build(pid)).build();
		}
		catch (Exception e)
		{
			LOGGER.error("Unable to bag file", e);
			resp = Response.temporaryRedirect(
					redirUri.path(UploadService.class, "doGetAsHtml").queryParam("pid", pid).queryParam("emsg", "Upload Unsuccessful. Unable to bag file.")
							.build()).build();
		}
		finally
		{
			// Delete properties file and uploaded files.
			IOUtils.closeQuietly(propFileInStream);
			if (!FileUtils.deleteQuietly(propFile))
				LOGGER.warn("Unable to delete properties file.");
			if (!FileUtils.deleteQuietly(uploadedFilesDir))
				LOGGER.warn("Unable to delete uploaded files.");
		}

		return resp;
	}

	@GET
	@Produces(MediaType.TEXT_HTML)
	@Path("bag/{pid}")
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	public Response doGetBagFileListingAsHtml(@PathParam("pid") String pid)
	{
		Response resp = null;
		Map<String, Object> model = new HashMap<String, Object>();

		// Check if user's got read access to fedora object.
		FedoraObject fo = getFedoraObjectReadAccess(pid);
		model.put("fo", fo);

		if (!dcStorage.bagExists(pid))
			throw new NotFoundException("Bag not found for " + pid);

		BagSummary bagSummary;
		try
		{
			bagSummary = dcStorage.getBagSummary(pid);
			model.put("bagSummary", bagSummary);
			model.put("bagInfoTxt", bagSummary.getBagInfoTxt().entrySet());
			if (bagSummary.getExtRefsTxt() != null)
				model.put("extRefsTxt", bagSummary.getExtRefsTxt().entrySet());
			UriBuilder uriBuilder = UriBuilder.fromUri(uriInfo.getBaseUri()).path(UploadService.class)
					.path(UploadService.class, "doGetFileInBagAsOctetStream2");
			model.put("dlBaseUri", uriBuilder.build(pid, "").toString());
			model.put("downloadAsZipUrl", uriBuilder.build(pid, "zip").toString());
		}
		catch (DcStorageException e)
		{
			LOGGER.error(e.getMessage(), e);
			PageMessages messages = new PageMessages();
			messages.add(MessageType.ERROR, e.getMessage(), model);
		}

		resp = Response.ok(new Viewable(BAGFILES_JSP, model), MediaType.TEXT_HTML_TYPE).build();
		return resp;
	}

	@GET
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@Path("files/{pid}/{fileInBag:.*}")
	public Response doGetFileInBagAsOctetStream(@PathParam("pid") String pid, @PathParam("fileInBag") String filename,
			@QueryParam("dropboxAccessCode") Long dropboxAccessCode, @QueryParam("p") String password)
	{
		Response resp = null;

		LOGGER.trace("pid: {}, filename: {}", pid, filename);

		// Get dropbox requesting file.
		DropboxDAO dropboxDAO = new DropboxDAOImpl(CollectionDropbox.class);
		CollectionDropbox dropbox = dropboxDAO.getSingleByAccessCode(dropboxAccessCode);
		Users requestor = dropbox.getCollectionRequest().getRequestor();
		String username = SecurityContextHolder.getContext().getAuthentication().getName();

		// If dropbox is valid and the requestor of the collection request is the one accessing it, return file as octet stream.
		if (dropbox.isValid(password) && requestor.getUsername().equals(username))
		{
			LOGGER.info("Dropbox details valid. ID: {}, Access Code: {}. Returning file requested.", dropbox.getId().toString(), dropbox.getAccessCode()
					.toString());
			new AccessLogRecordDAOImpl(AccessLogRecord.class).create(new AccessLogRecord(uriInfo.getPath(), getCurUser(), request.getRemoteAddr(),
					AccessLogRecord.Operation.READ));
			if (filename.equalsIgnoreCase("zip"))
			{
				Set<String> fileSet = new HashSet<String>();
				Set<CollectionRequestItem> items = dropbox.getCollectionRequest().getItems();
				for (CollectionRequestItem item : items)
					fileSet.add(item.getItem());
				resp = getBagFilesAsZip(pid, fileSet, "Collection.zip");
			}
			else
				resp = getBagFileOctetStreamResp(pid, filename);
		}
		else
		{
			LOGGER.warn("Unauthorised access to Dropbox ID: {}, Access Code: {}. Returning HTTP 403 Forbidden.", dropbox.getId().toString(), dropbox
					.getAccessCode().toString());
			resp = Response.status(Status.FORBIDDEN).build();
		}

		return resp;
	}

	@GET
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@Path("bag/{pid}/{fileInBag:.*}")
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	public Response doGetFileInBagAsOctetStream2(@PathParam("pid") String pid, @PathParam("fileInBag") String fileInBag)
	{
		LOGGER.trace("pid: {}, filename: {}", pid, fileInBag);
		Response resp = null;
		// Check for read access.
		getFedoraObjectReadAccess(pid);
		// Create a log record.
		new AccessLogRecordDAOImpl(AccessLogRecord.class).create(new AccessLogRecord(uriInfo.getPath(), getCurUser(), request.getRemoteAddr(),
				AccessLogRecord.Operation.READ));
		if (fileInBag.equals("zip"))
		{
			Set<String> fileSet = new HashSet<String>();
			Bag bag = dcStorage.getBag(pid);
			Collection<BagFile> bagFiles = bag.getPayload();
			for (BagFile iFile : bagFiles)
				fileSet.add(iFile.getFilepath());
			resp = getBagFilesAsZip(pid, fileSet, "Collection.zip");
		}
		else
			resp = getBagFileOctetStreamResp(pid, fileInBag);

		return resp;
	}

	@POST
	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	@Path("bag/{pid}")
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	public Response doPostBag(@PathParam("pid") String pid, InputStream is)
	{
		// Check for write access to the fedora object.
		getFedoraObjectWriteAccess(pid);

		Response resp = null;
		AccessLogRecord accessRec = null;
		File uploadedFile = null;
		try
		{
			uploadedFile = File.createTempFile("Rep", null, GlobalProps.getUploadDirAsFile());
			LOGGER.info("Saving uploaded file as {}...", uploadedFile.getAbsolutePath());
			saveInputStreamAsFile(is, uploadedFile);
			LOGGER.info("Uploaded file saved as {}", uploadedFile.getAbsolutePath());

			// Check if a current bag exists. If yes, replace, else saveAs.
			if (dcStorage.bagExists(pid))
			{
				LOGGER.info("A bag exists for {}. Replacing it with the file uploaded...", pid);
				accessRec = new AccessLogRecord(uriInfo.getPath(), getCurUser(), request.getRemoteAddr(), AccessLogRecord.Operation.UPDATE);

			}
			else
			{
				LOGGER.info("No bag exists for {}. Storing Bag...", pid);
				accessRec = new AccessLogRecord(uriInfo.getPath(), getCurUser(), request.getRemoteAddr(), AccessLogRecord.Operation.CREATE);
			}
			dcStorage.storeBag(pid, uploadedFile);
			new AccessLogRecordDAOImpl(AccessLogRecord.class).create(accessRec);
			LOGGER.info("Bag updated for {}", pid);
			resp = Response.ok().build();
		}
		catch (Exception e)
		{
			LOGGER.error("Unable to upload bag.", e);
			resp = Response.serverError().entity(e.toString()).type(MediaType.TEXT_PLAIN).build();
		}
		finally
		{
			// Delete uploaded file.
			if (uploadedFile != null && uploadedFile.exists())
				if (!FileUtils.deleteQuietly(uploadedFile))
					LOGGER.warn("Unable to delete temp file {}.", uploadedFile.getAbsolutePath());
		}

		return resp;
	}
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("userinfo")
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	public Response doGetUserInfo()
	{
		Users curUser = getCurUser();
		Response resp = null;
		StringBuilder respEntity = new StringBuilder();
		respEntity.append(curUser.getUsername());
		respEntity.append(":");
		respEntity.append(curUser.getDisplayName());
		resp = Response.ok(respEntity.toString()).build();
		return resp;
	}

	private void saveInputStreamAsFile(InputStream is, File target) throws IOException
	{
		FileChannel targetChannel = null;
		FileOutputStream fos = null;
		ReadableByteChannel sourceChannel = null;
		try
		{
			fos = new FileOutputStream(target);
			targetChannel = fos.getChannel();
			sourceChannel = Channels.newChannel(is);
			ByteBuffer buffer = ByteBuffer.allocate((int) FileUtils.ONE_MB);
			while (sourceChannel.read(buffer) != -1)
			{
				buffer.flip();
				targetChannel.write(buffer);
				buffer.compact();
			}

			buffer.flip();
			while (buffer.hasRemaining())
				targetChannel.write(buffer);
		}
		finally
		{
			IOUtils.closeQuietly(targetChannel);
			IOUtils.closeQuietly(fos);
			IOUtils.closeQuietly(is);
			IOUtils.closeQuietly(sourceChannel);
		}
	}

	private Users getCurUser()
	{
		return new UsersDAOImpl(Users.class).getUserByName(SecurityContextHolder.getContext().getAuthentication().getName());
	}

	private Response getBagFileOctetStreamResp(String pid, String fileInBag)
	{
		Response resp = null;
		InputStream is = null;

		Bag bag = dcStorage.getBag(pid);
		if (bag == null)
		{
			LOGGER.error(format("No bag found for Pid {0}. Throwing NotFoundException.", pid));
			throw new NotFoundException(format("No bag found for Pid {0}.", pid));
		}

		try
		{
			is = dcStorage.getFileStream(pid, fileInBag);
			ResponseBuilder respBuilder = Response.ok(is, MediaType.APPLICATION_OCTET_STREAM_TYPE);
			// Add filename, MD5 and file size to response header.
			respBuilder = respBuilder.header("Content-Disposition", format("attachment; filename=\"{0}\"", getFilenameFromPath(fileInBag)));
			respBuilder = respBuilder.header("Content-MD5", bag.getChecksums(fileInBag).get(Algorithm.MD5));
			respBuilder = respBuilder.header("Content-Length", bag.getBagFile(fileInBag).getSize());
			resp = respBuilder.build();
		}
		catch (DcStorageException e)
		{
			LOGGER.error(e.getMessage());
			throw new NotFoundException(e.getMessage());
		}

		return resp;
	}

	private Response getBagFilesAsZip(String pid, Set<String> fileSet, String zipFilename)
	{
		Response resp = null;
		InputStream zipStream;
		try
		{
			zipStream = dcStorage.getFilesAsZipStream(pid, fileSet);
			ResponseBuilder respBuilder = Response.ok(zipStream, MediaType.APPLICATION_OCTET_STREAM_TYPE);
			respBuilder.header("Content-Disposition", format("attachment; filename=\"{0}\"", zipFilename));
			resp = respBuilder.build();
		}
		catch (IOException e)
		{
			LOGGER.error(e.getMessage(), e);
			throw new NotFoundException(e.getMessage());
		}

		return resp;
	}

	@PostAuthorize("hasPermission(returnObject, 'READ')")
	private FedoraObject getFedoraObjectReadAccess(String pid)
	{
		return getFedoraObject(pid);
	}

	@PostAuthorize("hasPermission(returnObject, 'WRITE')")
	private FedoraObject getFedoraObjectWriteAccess(String pid)
	{
		return getFedoraObject(pid);
	}

	private FedoraObject getFedoraObject(String pid)
	{
		LOGGER.debug("Retrieving object for: {}", pid);
		String decodedpid = null;
		decodedpid = Util.decodeUrlEncoded(pid);
		if (decodedpid == null)
		{
			return null;
		}
		LOGGER.debug("Decoded pid: {}", decodedpid);
		FedoraObjectDAOImpl object = new FedoraObjectDAOImpl(FedoraObject.class);
		FedoraObject fo = (FedoraObject) object.getSingleByName(decodedpid);
		return fo;
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
	 * @throws IOException
	 * @throws Exception
	 *             Thrown when unable to save the file on the server.
	 */
	private File saveFileOnServer(FileItem fileItem, int partNum, boolean isLastPart, String pid, String expectedMd5) throws IOException
	{
		String clientFullFilename = fileItem.getName();
		String serverFilename = getFilenameFromPath(clientFullFilename);

		LOGGER.debug("filename: {}, filesize: {}.", clientFullFilename, fileItem.getSize());

		// Append .part[n] to the filename if its a part file.
		if (partNum > 0)
			serverFilename += PART_FILE_SUFFIX + partNum;

		BufferedInputStream uploadedFileStream = null;
		BufferedInputStream mergedFileStream = null;
		File pidDir = new File(GlobalProps.getUploadDirAsFile(), DcStorage.convertToDiskSafe(pid));
		if (!pidDir.exists())
			pidDir.mkdirs();
		File fileOnServer = new File(pidDir, serverFilename);
		try
		{
			// Check if file already exists in pid dir. If not, write the file. Otherwise check if the file on server's the same size as the one uploaded. If not, write it.
			if (!fileOnServer.exists() || (fileOnServer.exists() && fileOnServer.length() != fileItem.getSize()))
			{
				LOGGER.debug("Writing file {}", fileOnServer.getAbsolutePath());
				uploadedFileStream = new BufferedInputStream(fileItem.getInputStream(), (int) FileUtils.ONE_MB);
				saveInputStreamAsFile(uploadedFileStream, fileOnServer);
				if (isLastPart)			// If last part then merge part files.
				{
					File mergedFile = processFinalPartFile(fileItem, partNum, pidDir);
					mergedFileStream = new BufferedInputStream(new FileInputStream(mergedFile), (int) FileUtils.ONE_MB);
					String computedMd5Sum = DigestUtils.md5Hex(mergedFileStream);
					if (!computedMd5Sum.equalsIgnoreCase(expectedMd5))
						throw new IOException(format("Computed MD5 {0} does not match expected {1} for uploaded file {2}", computedMd5Sum, expectedMd5,
								mergedFile.getAbsolutePath()));
				}
			}
			else
			{
				LOGGER.warn("File {} exists on server with same size. Not overwriting it.", fileOnServer.getAbsoluteFile());
			}
		}
		finally
		{
			IOUtils.closeQuietly(uploadedFileStream);
			IOUtils.closeQuietly(mergedFileStream);
		}

		return fileOnServer;
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
	 * @throws IOException 
	 * @throws Exception
	 *             Thrown if the files could not be merged.
	 */
	private File processFinalPartFile(FileItem fileItem, int partNum, File pidDir) throws IOException
	{
		BufferedInputStream partInStream = null;
		File[] partFiles = new File[partNum];

		// Open a FileOutputStream in the Target directory where the merged file will be placed.
		File mergedFile = new File(pidDir, fileItem.getName());
		BufferedOutputStream mergedFileStream = new BufferedOutputStream(new FileOutputStream(mergedFile));

		LOGGER.debug("Processing final file part# {}", partNum);

		try
		{
			// Merge individual file parts.
			LOGGER.debug("Merging file parts...");
			for (int i = 1; i <= partNum; i++)
			{
				// Open the FileInputStream to read from the part file.
				partFiles[i - 1] = new File(pidDir, fileItem.getName() + PART_FILE_SUFFIX + i);
				partInStream = new BufferedInputStream(new FileInputStream(partFiles[i - 1]), (int) FileUtils.ONE_MB);

				// Read bytes and add them to the merged file until all files in this file part have been merged.
				try
				{
					byte[] buffer = new byte[(int) FileUtils.ONE_MB];				
					int numBytesRead;
					while ((numBytesRead = partInStream.read(buffer)) != -1)
						mergedFileStream.write(buffer, 0, numBytesRead);
				}
				finally
				{
					IOUtils.closeQuietly(partInStream);
				}
			}

			LOGGER.debug("Merged into {}", pidDir + File.separator + fileItem.getName());
		}
		finally
		{
			IOUtils.closeQuietly(mergedFileStream);

			// Delete the part files now that they've been merged.
			for (int i = 0; i < partFiles.length; i++)
			{
				if (!partFiles[i].delete())
					LOGGER.warn("Unable to delete part file {}.", partFiles[i].getAbsolutePath());
			}
		}
		
		return mergedFile;
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

	private void mapFormFields(List<FileItem> uploadedItems, Properties uploadProps)
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
					if (!uploadProps.containsValue(iFileItem.getString()))
					{
						int i;
						for (i = 0; uploadProps.containsKey("url" + i); i++)
							;

						uploadProps.setProperty(formatFieldName(iFileItem.getFieldName()) + i, iFileItem.getString().trim());
					}
				}
				else
				{
					uploadProps.setProperty(formatFieldName(iFileItem.getFieldName()), iFileItem.getString().trim());
				}
			}
		}
		StringBuilder uploadPropsStr = new StringBuilder();
		for (Entry<Object, Object> entry : uploadProps.entrySet())
			uploadPropsStr.append(format("{0}={1}\r\n", entry.getKey(), entry.getValue()));
		LOGGER.debug("Upload properties:\r\n{}", uploadPropsStr.toString());
	}
}
