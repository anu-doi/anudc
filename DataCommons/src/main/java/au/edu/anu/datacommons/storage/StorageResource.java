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

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import au.edu.anu.datacommons.data.db.model.FedoraObject;
import au.edu.anu.datacommons.storage.DcStorage.FileInfo;
import au.edu.anu.datacommons.storage.DcStorage.FileInfo.Type;
import au.edu.anu.datacommons.storage.DcStorage.RecordDataInfo;

import com.sun.jersey.api.NotFoundException;
import com.sun.jersey.api.view.Viewable;

/**
 * @author Rahul Khanna
 * 
 */
@Path("record/{pid:[a-z]*(:|%3[aA])[0-9]*}")
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
	public Response getFileOrDirAsHtml(@PathParam("pid") String pid, @PathParam("path") String path) {
		return createFileOrDirResponse(pid, path, "/storage.jsp");
	}

	@POST
	@Path("data/{path:.*}")
	@Consumes({ MediaType.APPLICATION_OCTET_STREAM, MediaType.MULTIPART_FORM_DATA })
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	public Response uploadFile(@PathParam("pid") String pid, @PathParam("path") String path,
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
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	public Response createDir(@PathParam("pid") String pid, @PathParam("path") String path) {
		Response resp = null;
		LOGGER.info("User {} ({})requested creation of directory {} in record {} file upload to {}", getCurUsername(),
				getRemoteIp(), uriInfo.getPath(true).toString(), pid);
		fedoraObjectService.getItemByPidWriteAccess(pid);

		path = appendSeparator(path);
		try {
			dcStorage.addDirectory(pid, path);
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
		return processDeleteFile(pid, path);
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
		path = removeTrailingSlash(path);
		try {
			if (dcStorage.dirExists(pid, path)) {
				LOGGER.info("User {} ({}) requested list of files in {}/data/{}", getCurUsername(), getRemoteIp(), pid, path);
				RecordDataInfo fl = new RecordDataInfo();
				fl.setFiles(dcStorage.getFilesInDir(pid, path));
				fl.setParents(dcStorage.getParentDirs(pid, path));
				addUris(pid, path, fl.getFiles());
				addUris(pid, path, fl.getParents());
				fl.setUri(uriInfo.getAbsolutePath().toString());

				if (template != null) {
					model.put("fo", fo);
					model.put("fileList", fl);
					resp = Response.ok(new Viewable(template, model)).build();
				} else {
					resp = Response.ok(fl).build();
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

	private void addUris(String pid, String path, Collection<FileInfo> files) {
		for (FileInfo iFileInfo : files) {
			String relPath = iFileInfo.getRelFilepath();
			if (iFileInfo.getType() == Type.DIR) {
				relPath = appendSeparator(relPath);
			}
			String uri = getUri(pid, relPath).toString();
			iFileInfo.setUri(uri);
		}
	}

	private String removeTrailingSlash(String path) {
		if (path.length() > 0 && path.charAt(path.length() - 1) == '/') {
			return path.substring(0, path.length() - 1);
		} else {
			return path;
		}
	}

}
