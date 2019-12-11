/*******************************************************************************
 * Australian National University Data Commons
 * Copyright (C) 2013  The Australian National University
 * 
 * This file is part of Australian National University Data Commons.
 * 
 * Australian National University Data Commons is free software: you
 * can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package au.edu.anu.datacommons.storage;

import static java.text.MessageFormat.format;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import com.sun.jersey.api.NotFoundException;
import com.sun.jersey.api.view.Viewable;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.yourmediashelf.fedora.client.FedoraClientException;

import au.edu.anu.datacommons.data.db.model.FedoraObject;
import au.edu.anu.datacommons.security.AccessLogRecord.Operation;
import au.edu.anu.datacommons.storage.datafile.StagedDataFile;
import au.edu.anu.datacommons.storage.info.FileInfo;
import au.edu.anu.datacommons.storage.info.RecordDataSummary;
import au.edu.anu.datacommons.storage.jqueryupload.JQueryFileUploadHandler;
import au.edu.anu.datacommons.storage.jqueryupload.JQueryFileUploadResponse;
import au.edu.anu.datacommons.storage.provider.StorageException;
import au.edu.anu.datacommons.storage.temp.UploadedFileInfo;
import au.edu.anu.datacommons.storage.verifier.VerificationResults;
import au.edu.anu.datacommons.util.Util;
import au.edu.anu.datacommons.xml.data.Data;
import au.edu.anu.datacommons.xml.data.DataItem;

/**
 * Provides REST endpoints to which rest requests related to data storage of a collection record are sent. 
 * 
 * @author Rahul Khanna
 * 
 */
@Path("records/{pid:[a-z]*(:|%3[aA])[0-9]*}")
@Component
@Scope("request")
public class StorageResource extends AbstractStorageResource {
	private static final Logger LOGGER = LoggerFactory.getLogger(StorageResource.class);
	
	private static final String FILES_FORM_FIELD = "files";
	
	@Autowired
	private JQueryFileUploadHandler uploadHandler;

	/**
	 * GET request for a file or a folder. For a file, the response is an octet-stream with the contents of the file.
	 * For a folder the response is an HTML page with the list of files in the folder.
	 * 
	 * @param pid
	 *            Identifier of collection record
	 * @param path
	 *            Path to the file/folder. Note that 'data/' is not included in the path.
	 * @return HTTP response
	 */
	@GET
	@Path("data/{path:.*}")
	@Produces({ "text/html; qs=1.1", MediaType.WILDCARD })
	public Response getFileOrDirAsHtml(@PathParam("pid") String pid, @PathParam("path") String path, @QueryParam(value = "upload") String upload) {
		if (upload != null) {
			return createUploadFilesResponse(pid, path, "/storageupload.jsp");
		} else {
			return createFileOrDirResponse(pid, path, "/storage.jsp");
		}
	}

	/**
	 * GET request for information about a file or folder. The response is an XML or JSON representation of the
	 * RecordDataInfo object.
	 * 
	 * @param pid
	 *            Identifier of collection record
	 * @param path
	 *            Filepath of the file/folder whose information is requested.
	 * @return HTTP Response
	 */
	@GET
	@Path("data/{path:.*}")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response getFileOrDirAsJsonXml(@PathParam("pid") String pid, @PathParam("path") String path) {
		return createFileOrDirResponse(pid, path, null);
	}

	/**
	 * HTTP request for accepting a file upload requests. Upload requests from the Python script, JUpload applet and
	 * HTML5 drag and drop uploaders are handled by this endpoint.
	 * 
	 * @param pid
	 *            Identifier of collection record
	 * @param path
	 *            Filepath where the uploaded file will be stored. Note that 'data/' is not part of the path.
	 * @param src
	 *            Optional query parameter specifying the source of the upload. Requests with parameter src=jupload are
	 *            handled differently
	 * @param is
	 *            Contents of the file being uploaded.
	 * @return HTTP Response
	 */
	@POST
	@Path("data/{path:.*}")
	// @Consumes({ MediaType.APPLICATION_OCTET_STREAM, MediaType.MULTIPART_FORM_DATA })
	@Consumes({ MediaType.APPLICATION_OCTET_STREAM })
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	public Response postUploadFile(@PathParam("pid") String pid, @PathParam("path") String path,
			@QueryParam("src") String src, InputStream is) {
		Response resp = null;
		if (src == null || src.length() == 0) {
			List<String> userAgentHeader = httpHeaders.getRequestHeader("User-Agent");
			if (userAgentHeader != null && !userAgentHeader.isEmpty()) {
				src = userAgentHeader.get(0);
			}
		}
		fedoraObjectService.getItemByPidWriteAccess(pid);

		if (src.equals("jupload")) {
			resp = processJUpload(pid, path);
		} else {
			resp = processRestUpload(pid, path, is);
		}
		return resp;
	}
	
	@POST
	@Path("data/{path:.*}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	public Response postFile(@PathParam("pid") String pid, @PathParam("path") String path,
			@HeaderParam("Content-Range") String contentRange,
			@HeaderParam(HttpHeaders.CONTENT_LENGTH) Long contentLength, FormDataMultiPart form) {
		Response resp = null;
		Map<String, List<JQueryFileUploadResponse>> respEntity = new HashMap<>();

		FormDataBodyPart field = form.getField(FILES_FORM_FIELD);

		FormDataContentDisposition contentDisposition = field.getFormDataContentDisposition();
		String fileName = contentDisposition.getFileName();

		JQueryFileUploadResponse fileResp = null;

		String id = request.getSession().getId();
		try (InputStream fileStream = field.getEntityAs(InputStream.class)) {
			boolean isComplete;
			if (contentRange == null) {
				// request contains entire file
				uploadHandler.processFile(fileStream, id, fileName, contentLength);
				fileResp = uploadHandler.generateResponse(id, fileName);
				isComplete = true;
			} else {
				// request contains file part
				uploadHandler.processFilePart(fileStream, id, fileName, contentRange);
				fileResp = uploadHandler.generateResponse(id, fileName);
				isComplete = uploadHandler.isFileComplete(id, fileName, contentRange);
				LOGGER.info("user={};ip={};partfile={}/data/{}{};range={}", getCurUsername(),
						getRemoteIp(), pid, path, fileName, contentRange);
			}

			if (isComplete) {
				if (storageController.fileExists(pid, path)) {
					addAccessLog(Operation.UPDATE);
				} else {
					addAccessLog(Operation.CREATE);
				}

				java.nio.file.Path stagedFile = uploadHandler.getTarget(id, fileName);
				StagedDataFile ufi = new UploadedFileInfo(stagedFile, Files.size(stagedFile), null);
				String filepath;
				if (path == null || path.length() == 0) {
					filepath = String.format("%s", fileName);
				} else {
					filepath = String.format("%s/%s", path, fileName);
				}
				storageController.addFile(pid, filepath, ufi);
				LOGGER.info("User {} ({}) added file {}/data/{}, Size: {}", getCurUsername(), getRemoteIp(), pid, path,
						Util.byteCountToDisplaySize(ufi.getSize()));
			}
			resp = Response.status(Status.OK).entity(respEntity).build();
		} catch (Exception e) {
			fileResp = uploadHandler.generateResponse(id, fileName, e);
			resp = Response.status(Status.INTERNAL_SERVER_ERROR).entity(respEntity).build();
			LOGGER.error(e.getMessage(), e);
		}

		respEntity.put(FILES_FORM_FIELD, Arrays.asList(fileResp));
		return resp;
	}

	
	/**
	 * Accepts POST requests for:
	 * 
	 * <ul>
	 * <li>Downloading a Zip file. This is done with a POST request because a GET request placed a limit on the number
	 * of files that can be included in the ZIP file depending on the allowable query size configured in the web server.
	 * That is, selecting 1000 files would result in a GET request 1000 query parameters.
	 * <li>Add one or more external references
	 * <li>Remove one or more external references
	 * <li>Toggle public flag for files of a collection.
	 * </ul>
	 * 
	 * @param pid
	 *            Identifier of collection record
	 * @param path
	 *            Filepath of file. Currently this parameter is not used, but may be used in the future when data values
	 *            are stored against individual files.
	 * @param action
	 *            Action to perform. The following values are valid:
	 *            <ul>
	 *            <li>zip
	 *            <li>addExtRef
	 *            <li>delExtRef
	 *            <li>filesPublic
	 *            </ul>
	 * @param items
	 *            parameters to be used in performing the 'action'. For zip file creation, list of filepaths, for
	 *            adding/deleting external references, list of URLs.
	 * @return HTTP Response
	 */
	@POST
	@Path("data/{path:.*}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
//	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	public Response postForm(@PathParam("pid") String pid, @PathParam("path") String path,
			@QueryParam("action") String action, @FormParam("i") Set<String> items) {
		Response resp = null;
		if ("zip".equals(action)) {
			resp = createZipFileResponse(pid, path, items);
		}
		else if ("filesPublic".equals(action)) {
			resp = processSetFilesPublicFlag(pid, items.iterator().next());
		}
		else if (action != null) {
			// Check that users have permissions to do these actions
			fedoraObjectService.getItemByPidWriteAccess(pid);
			if (action.equals("addExtRef") && !items.isEmpty()) {
				resp = createAddExtRefResponse(pid, items);
			} else if (action.equals("delExtRef") && !items.isEmpty()) {
				resp = createDelExtRefResponse(pid, items);
			} else if (action.equals("renameFile") && !items.isEmpty()) {
				resp = createRenameResponse(pid, path, items);
			}
		}
		
		return resp;
	}

	/**
	 * Accepts a POST request to create a folder. This endpoint is reached when there is no body in the POST request.
	 * 
	 * @param pid
	 *            Identifier of the collection request
	 * @param path
	 *            Filepath of the folder to be created.
	 * @return HTTP response
	 */
	@POST
	@Path("data/{path:.*}")
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	public Response createDir(@PathParam("pid") String pid, @PathParam("path") String path) {
		Response resp = null;
		LOGGER.info("User {} ({}) requested creation of directory {} in record {}", getCurUsername(),
				getRemoteIp(), uriInfo.getPath(true).toString(), pid);
		fedoraObjectService.getItemByPidWriteAccess(pid);

		path = appendSeparator(path);
		try {
			storageController.addDir(pid, path);
			resp = Response.created(getUri(pid, path)).build();
		} catch (IOException | StorageException e) {
			LOGGER.error(e.getMessage(), e);
			resp = Response.serverError().entity(e.getMessage()).build();
		}

		return resp;
	}

	/**
	 * Deletes a file or folder within a collection record.
	 * 
	 * @param pid
	 *            Identifier of collection record
	 * @param path
	 *            Path to the file or directory to be deleted.
	 * @return HTTP Response
	 */
	@DELETE
	@Path("data/{path:.*}")
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	public Response deleteFile(@PathParam("pid") String pid, @PathParam("path") String path) {
		fedoraObjectService.getItemByPidWriteAccess(pid);
		return processDeleteFile(pid, path);
	}
	
	/**
	 * Provides an endpoint for accepting requests for performing administrative tasks on a collection record's files.
	 * 
	 * @param pid
	 *            Identifier of collection record
	 * @param task
	 *            Task to perform as String. The following values are valid:
	 *            <ul>
	 *            <li>verify
	 *            <li>complete
	 *            </ul>
	 * @return HTTP Response
	 */
	@GET
	@Path("admin")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public Response performAdminTask(@PathParam("pid") String pid, @QueryParam("task") String task) {
		Response resp = null;
		
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			if (!storageController.dirExists(pid, "")) {
				throw new NotFoundException();
			}
		} catch (StorageException e1) {
			throw new NotFoundException();
		}

		try {
			if (task.equals("verify")) {
				VerificationResults results = storageController.verifyIntegrity(pid);
				model.put("results", results);
				resp = Response.ok(new Viewable("/verificationresults.jsp", model)).build();
			} else if (task.equals("complete")) {
				storageController.fixIntegrity(pid);
				resp = Response.ok("Done").build();
			} else if (task.equals("deindex")) {
				resp = createDeindexResponse(pid);
			} else if (task.equals("index")) {
				resp = createIndexResponse(pid);
			}
			
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			resp = Response.serverError().entity(e.getMessage()).build();
		}
		
		return resp;
	}
	
	/**
	 * Creates an HTTP response depending on the item represented by the filepath in the specified record. If file, then
	 * response is an octet stream. If directory, then response is an HTML or XML/JSON response depending on Accepts
	 * header.
	 * 
	 * @param pid
	 *            Identifier of collection record
	 * @param path
	 *            Path to the file or folder being requested.
	 * @param template
	 *            JSP template to use if an HTML response is required. null if XML/JSON response is required.
	 * @return HTTP Response
	 */
	private Response createFileOrDirResponse(String pid, String path, String template) {
		Response resp = null;

		FedoraObject fo = fedoraObjectService.getItemByPid(pid);
		if (fo == null) {
			throw new NotFoundException(uriInfo.getAbsolutePath());
		}

		if (!(isPublishedAndPublic(fo))) {
			fo = null;
			fo = fedoraObjectService.getItemByPidReadAccess(pid);
		}

		Map<String, Object> model = new HashMap<String, Object>();
		
		Data data = null;
		try {
			data = fedoraObjectService.getPublishData(fo);
			if (data == null) {
			}
		}
		catch (FedoraClientException | JAXBException e) {
			
		}
		if (data == null) {
			try {
				data = fedoraObjectService.getEditData(fo);
			}
			catch (FedoraClientException | JAXBException e) {
				LOGGER.error(e.getMessage(), e);
				resp = Response.status(Status.NOT_FOUND).entity("Unable to find item with the id " + pid).build();
//				resp = Response.ok(e.getMessage()).build();
			}
		}
		DataItem dataItem = data.getFirstElementByName("name");
		String title = dataItem.getValue();
		model.put("name", title);
		
		try {
			
			if (path == null || path.length() == 0 || storageController.dirExists(pid, path)) {
				LOGGER.info("User {} ({}) requested list of files in {}/data/{}", getCurUsername(), getRemoteIp(), pid, path);
				RecordDataSummary rdi = storageController.getRecordDataSummary(pid);
				FileInfo fileInfo = null;
				if (storageController.dirExists(pid, "")) {
					fileInfo = storageController.getFileInfo(pid, path);
				}
				
				if (template != null) {
					model.put("fo", fo);
					model.put("rdi", rdi);
					model.put("fileInfo", fileInfo);
					model.put("parents", getParents(fileInfo));
					model.put("path", path);
					model.put("isFilesPublic", fo.isFilesPublic().toString());
					resp = Response.ok(new Viewable(template, model)).build();
				} else {
					resp = Response.ok(rdi).build();
				}
			} else if (storageController.fileExists(pid, path)) {
				LOGGER.info("User {} ({}) requested file {}/data/{}", getCurUsername(), getRemoteIp(), pid, path);
				resp = getBagFileOctetStreamResp(pid, path);
			} else {
				throw new NotFoundException(uriInfo.getAbsolutePath());
			}
		} catch (IOException | StorageException e) {
			LOGGER.error(e.getMessage(), e);
			resp = Response.ok(e.getMessage()).build();
		}
		return resp;
	}

	private List<FileInfo> getParents(FileInfo fileInfo) {
		List<FileInfo> parents = new ArrayList<FileInfo>();
		for (FileInfo parent = fileInfo; parent != null && parent.getParent() != null; parent = parent.getParent()) {
			parents.add(0, parent);
		}
		return parents;
	}

	private Response createUploadFilesResponse(String pid, String path, String template) {
		Response resp;
		
		FedoraObject fo = fedoraObjectService.getItemByPidWriteAccess(pid);
		if (fo == null) {
			throw new NotFoundException(uriInfo.getAbsolutePath());
		}

		try {
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("fo", fo);
			model.put("parents", getParents(storageController.getFileInfo(pid, path)));
			resp = Response.ok(new Viewable(template, model)).build();
		} catch (IOException | StorageException e) {
			LOGGER.error(e.getMessage(), e);
			resp = Response.serverError().entity(e.getMessage()).build();
		}
		
		
		return resp;
	}

	/**
	 * Creates an HTTP response with an octetstream body comprising of a Zip stream of one or more files in a specified
	 * collection record.
	 * 
	 * @param pid
	 *            Identifier of collection record
	 * @param path
	 *            Root path to which all specified filepaths will be appended
	 * @param filepaths
	 *            Collection of relative paths (relative to path param) to be included in the Zip stream
	 * @return HTTP Response
	 */
	private Response createZipFileResponse(String pid, String path, Set<String> filepaths) {
		ResponseBuilder resp;

		FedoraObject fo = fedoraObjectService.getItemByPid(pid);
		if (!(isPublishedAndPublic(fo))) {
			fedoraObjectService.getItemByPidReadAccess(pid);
		}
		Set<String> pathPrependedFilepaths = new HashSet<String>(filepaths.size());
		for (String filepath : filepaths) {
			pathPrependedFilepaths.add(path + filepath);
		}
		try {
			InputStream zipStream = storageController.createZipStream(pid, pathPrependedFilepaths);
			resp = Response.ok(zipStream, MediaType.APPLICATION_OCTET_STREAM_TYPE);
			String clientFilename = format("{0}.{1}", DcStorage.convertToDiskSafe(pid), "zip");
			resp.header("Content-Disposition", format("attachment; filename=\"{0}\"", clientFilename));
		} catch (IOException | StorageException e) {
			LOGGER.error(e.getMessage(), e);
			resp = Response.serverError().entity(e.getMessage());
		}
		
		return resp.build();
	}

	private Response createAddExtRefResponse(String pid, Set<String> items) {
		ResponseBuilder resp = null;
		try {
			storageController.addExtRefs(pid, items);
			resp = Response.ok();
		} catch (IOException | StorageException e) {
			LOGGER.error(e.getMessage(), e);
			resp = Response.serverError().entity(e.getMessage());
		}
		return resp.build();
	}
	
	private Response createDelExtRefResponse(String pid, Set<String> items) {
		ResponseBuilder resp = null;
		try {
			storageController.deleteExtRefs(pid, items);
			resp = Response.ok();
		} catch (IOException | StorageException e) {
			LOGGER.error(e.getMessage(), e);
			resp = Response.serverError().entity(e.getMessage());
		}
		return resp.build();
	}

	private Response createRenameResponse(String pid, String path, Set<String> items) {
		ResponseBuilder resp = null;
		
		try {
			storageController.renameFile(pid, path, items.iterator().next());
			resp = Response.ok();
		} catch (IOException | StorageException e) {
			LOGGER.error(e.getMessage(), e);
			resp = Response.serverError().entity(e.getMessage());
		}
		return resp.build();
	}

	private Response createDeindexResponse(String pid) {
		ResponseBuilder resp = null;
		
		try {
			storageController.deindexFiles(pid);
			resp = Response.ok();
		} catch (IOException | StorageException e) {
			LOGGER.error(e.getMessage(), e);
			resp = Response.serverError().entity(e.getMessage());
		}
		
		return resp.build();
	}

	private Response createIndexResponse(String pid) {
		ResponseBuilder resp = null;
		
		try {
			storageController.indexFiles(pid);
			resp = Response.ok();
		} catch (IOException | StorageException e) {
			LOGGER.error(e.getMessage(), e);
			resp = Response.serverError().entity(e.getMessage());
		}
		
		return resp.build();
	}
	
	private String removeTrailingSlash(String path) {
		if (path.length() > 0 && path.charAt(path.length() - 1) == '/') {
			return path.substring(0, path.length() - 1);
		} else {
			return path;
		}
	}

}
