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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import au.edu.anu.datacommons.data.db.model.FedoraObject;
import au.edu.anu.datacommons.storage.info.RecordDataInfo;
import au.edu.anu.datacommons.storage.verifier.VerificationResults;

import com.sun.jersey.api.NotFoundException;
import com.sun.jersey.api.view.Viewable;

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

	@GET
	public Response get() {
		return Response.ok("Test").build();
	}

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
	public Response getFileOrDirAsHtml(@PathParam("pid") String pid, @PathParam("path") String path) {
		return createFileOrDirResponse(pid, path, "/storage.jsp");
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
	@Consumes({ MediaType.APPLICATION_OCTET_STREAM, MediaType.MULTIPART_FORM_DATA })
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
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	public Response postForm(@PathParam("pid") String pid, @PathParam("path") String path,
			@QueryParam("action") String action, @FormParam("i") Set<String> items) {
		Response resp = null;
		if (action != null) {
			if (action.equals("zip") && !items.isEmpty()) {
				resp = createZipFileResponse(pid, path, items);
			} else if (action.equals("addExtRef") && !items.isEmpty()) {
				resp = createAddExtRefResponse(pid, items);
			} else if (action.equals("delExtRef") && !items.isEmpty()) {
				resp = createDelExtRefResponse(pid, items);
			} else if (action.equals("filesPublic") && !items.isEmpty()) {
				resp = processSetFilesPublicFlag(pid, items.iterator().next());
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
		LOGGER.info("User {} ({})requested creation of directory {} in record {} file upload to {}", getCurUsername(),
				getRemoteIp(), uriInfo.getPath(true).toString(), pid);
		fedoraObjectService.getItemByPidWriteAccess(pid);

		path = appendSeparator(path);
		try {
			dcStorage.createPayloadDir(pid, path);
			resp = Response.created(getUri(pid, path)).build();
		} catch (IOException e) {
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
		if (!dcStorage.bagExists(pid)) {
			throw new NotFoundException();
		}

		try {
			if (task.equals("verify")) {
				VerificationResults results = dcStorage.verifyBag(pid);
				model.put("results", results);
				resp = Response.ok(new Viewable("/verificationresults.jsp", model)).build();
			} else if (task.equals("complete")) {
				dcStorage.recompleteBag(pid);
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
		try {
			if (path == null || path.length() == 0 || dcStorage.dirExists(pid, path)) {
				LOGGER.info("User {} ({}) requested list of files in {}/data/{}", getCurUsername(), getRemoteIp(), pid, path);
				RecordDataInfo rdi = dcStorage.getDirLimitedRecordDataInfo(pid, path);
				if (template != null) {
					model.put("fo", fo);
					model.put("rdi", rdi);
					model.put("path", path);
					model.put("isFilesPublic", fo.isFilesPublic().toString());
					resp = Response.ok(new Viewable(template, model)).build();
				} else {
					resp = Response.ok(rdi).build();
				}
			} else if (dcStorage.fileExists(pid, path)) {
				LOGGER.info("User {} ({}) requested file {}/data/{}", getCurUsername(), getRemoteIp(), pid, path);
				resp = getBagFileOctetStreamResp(pid, path);
			} else {
				throw new NotFoundException(uriInfo.getAbsolutePath());
			}
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
			resp = Response.ok(e.getMessage()).build();
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
		
		fedoraObjectService.getItemByPidReadAccess(pid);
		Set<String> pathPrependedFilepaths = new HashSet<String>(filepaths.size());
		for (String filepath : filepaths) {
			pathPrependedFilepaths.add(path + filepath);
		}
		try {
			InputStream zipStream = dcStorage.createZipStream(pid, pathPrependedFilepaths);
			resp = Response.ok(zipStream, MediaType.APPLICATION_OCTET_STREAM_TYPE);
			String clientFilename = format("{0}.{1}", DcStorage.convertToDiskSafe(pid), "zip");
			resp.header("Content-Disposition", format("attachment; filename=\"{0}\"", clientFilename));
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
			resp = Response.serverError().entity(e.getMessage());
		}
		
		return resp.build();
	}

	private Response createAddExtRefResponse(String pid, Set<String> items) {
		ResponseBuilder resp = null;
		try {
			dcStorage.addExtRefs(pid, items);
			resp = Response.ok();
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
			resp = Response.serverError().entity(e.getMessage());
		}
		return resp.build();
	}
	
	private Response createDelExtRefResponse(String pid, Set<String> items) {
		ResponseBuilder resp = null;
		try {
			dcStorage.deleteExtRefs(pid, items);
			resp = Response.ok();
		} catch (IOException e) {
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
