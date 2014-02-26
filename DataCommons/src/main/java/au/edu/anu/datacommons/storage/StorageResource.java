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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
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
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import au.edu.anu.datacommons.data.db.model.FedoraObject;
import au.edu.anu.datacommons.security.acl.CustomACLPermission;
import au.edu.anu.datacommons.storage.info.FileInfo;
import au.edu.anu.datacommons.storage.info.FileInfo.Type;
import au.edu.anu.datacommons.storage.info.RecordDataInfo;
import au.edu.anu.datacommons.storage.verifier.VerificationResults;

import com.sun.jersey.api.NotFoundException;
import com.sun.jersey.api.view.Viewable;

/**
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

	@GET
	@Path("data/{path:.*}")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response getFileOrDirAsJsonXml(@PathParam("pid") String pid, @PathParam("path") String path) {
		return createFileOrDirResponse(pid, path, null);
	}

	@GET
	@Path("data/{path:.*}")
	@Produces({ MediaType.TEXT_HTML, MediaType.WILDCARD })
	public Response getFileOrDirAsHtml(@PathParam("pid") String pid, @PathParam("path") String path,
			@QueryParam("action") String action, @QueryParam("f") Set<String> filepaths) {
		if (action == null) {
			return createFileOrDirResponse(pid, path, "/storage.jsp");
		} else {
			if (action.equals("zip")) {
				return createZipFileResponse(pid, path, filepaths);
			} else {
				return Response.status(Status.BAD_REQUEST).entity(format("Invalid action {0}", action)).build();
			}
		}
	}

	@POST
	@Path("data/{path:.*}")
	@Consumes({ MediaType.APPLICATION_OCTET_STREAM, MediaType.MULTIPART_FORM_DATA })
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	public Response postUploadFile(@PathParam("pid") String pid, @PathParam("path") String path,
			@QueryParam("src") String src, InputStream is) {
		Response resp = null;
		LOGGER.info("User {} ({}) requested file upload to {} in record {} [SOURCE:{}]", getCurUsername(),
				getRemoteIp(), uriInfo.getPath(true).toString(), pid, src);
		fedoraObjectService.getItemByPidWriteAccess(pid);

		if (src == null || src.length() == 0) {
			resp = processRestUpload(pid, path, is);
		} else if (src.equals("jupload")) {
			resp = processJUpload(pid, path);
		}
		return resp;
	}
	
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

	@DELETE
	@Path("data/{path:.*}")
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	public Response deleteFile(@PathParam("pid") String pid, @PathParam("path") String path) {
		fedoraObjectService.getItemByPidWriteAccess(pid);
		return processDeleteFile(pid, path);
	}
	
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
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			resp = Response.serverError().entity(e.getMessage()).build();
		}
		
		return resp;
	}
	
	@GET
	@Path("attr")
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	public Response getRecordAttr(@PathParam("pid") String pid, @QueryParam("attr") String attr) {
		Response resp = null;
		FedoraObject fo = fedoraObjectService.getItemByPidReadAccess(pid);
		if (attr != null) {
			if (attr.equals("filesPublic")) {
				boolean isFilesPublic = fo.isFilesPublic();
				resp = Response.ok(String.valueOf(isFilesPublic)).build();
			}
		}
		return resp;
	}

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
				RecordDataInfo rdi = dcStorage.getRecordDataInfo(pid);
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
