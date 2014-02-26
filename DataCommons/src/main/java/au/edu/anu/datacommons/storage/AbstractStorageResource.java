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
import gov.loc.repository.bagit.Manifest;
import gov.loc.repository.bagit.Manifest.Algorithm;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.sun.jersey.api.NotFoundException;

import au.edu.anu.datacommons.data.db.dao.AccessLogRecordDAOImpl;
import au.edu.anu.datacommons.data.db.dao.UsersDAOImpl;
import au.edu.anu.datacommons.data.db.model.FedoraObject;
import au.edu.anu.datacommons.data.db.model.Users;
import au.edu.anu.datacommons.properties.GlobalProps;
import au.edu.anu.datacommons.security.AccessLogRecord;
import au.edu.anu.datacommons.security.AccessLogRecord.Operation;
import au.edu.anu.datacommons.security.acl.CustomACLPermission;
import au.edu.anu.datacommons.security.acl.PermissionService;
import au.edu.anu.datacommons.security.service.FedoraObjectService;
import au.edu.anu.datacommons.storage.info.FileInfo;
import au.edu.anu.datacommons.storage.temp.AbstractTempFileTask;
import au.edu.anu.datacommons.storage.temp.PartTempFileTask;
import au.edu.anu.datacommons.storage.temp.TempFileTask;

/**
 * @author Rahul Khanna
 *
 */
public class AbstractStorageResource {
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractStorageResource.class);
	
	@Context
	protected UriInfo uriInfo;
	@Context
	protected HttpServletRequest request;
	@Context
	protected HttpHeaders httpHeaders;
	
	@Resource(name = "fedoraObjectServiceImpl")
	protected FedoraObjectService fedoraObjectService;

	@Resource(name = "permissionService")
	protected PermissionService permissionService;

	@Resource(name = "dcStorage")
	protected DcStorage dcStorage;
	
	protected AccessLogRecordDAOImpl accessLogDao = new AccessLogRecordDAOImpl(AccessLogRecord.class);

	/**
	 * Creates a Response object containing the contents of a single file in a bag of collection as Response object
	 * containing InputStream.
	 * 
	 * @param pid
	 *            Pid of the collection from which a bagfile is to be read.
	 * @param fileInBag
	 *            Name of file in bag whose contents are to be returned as InputStream.
	 * @return Response object including HTTP headers and InputStream containing file contents.
	 * @throws IOException 
	 */
	protected Response getBagFileOctetStreamResp(String pid, String fileInBag) throws IOException {
		Response resp = null;
		InputStream is = null;

		if (!dcStorage.fileExists(pid, fileInBag)) {
			throw new NotFoundException(format("File {0} not found in record {1}", fileInBag, pid));
		}
		is = dcStorage.getFileStream(pid, fileInBag);
		ResponseBuilder respBuilder = Response.ok(is, MediaType.APPLICATION_OCTET_STREAM_TYPE);
		// Add filename, MD5 and file size to response header.
		FileInfo fileInfo = dcStorage.getFileInfo(pid, fileInBag);
		respBuilder = respBuilder.header("Content-Disposition",
				format("attachment; filename=\"{0}\"", fileInfo.getFilename()));
		respBuilder = respBuilder.header("Content-MD5", fileInfo.getMessageDigests().get("MD5"));
		respBuilder = respBuilder.header("Content-Length", Long.toString(fileInfo.getSize(), 10));
		respBuilder = respBuilder.lastModified(fileInfo.getLastModified());
		resp = respBuilder.build();

		return resp;
	}

	protected String extractFilenameFromPath(String fullFilename) {
		// Extract the type of slash being used in the filename.
		char clientSlashType = (fullFilename.lastIndexOf("\\") > 0) ? '\\' : '/';

		// Get the index where the filename starts. -1 if the path isn't specified.
		int clientFilenameStartIndex = fullFilename.lastIndexOf(clientSlashType);

		// Get the part of the string after the last instance of the path separator.
		String filename = fullFilename.substring(clientFilenameStartIndex + 1, fullFilename.length());
		return filename;
	}

	/**
	 * Gets a Users object containing information about the currently logged in user.
	 * 
	 * @return Users object containing information about the currently logged in user.
	 */
	protected Users getCurUser() {
		return new UsersDAOImpl(Users.class).getUserByName(getCurUsername());
	}
	
	protected String getCurUsername() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}
	
	protected String getRemoteIp() {
		return request.getRemoteAddr();
	}

	protected String appendSeparator(String path) {
		if (path.length() > 0 && path.charAt(path.length() - 1) != '/') {
			path += "/";
		}
		return path;
	}

	protected boolean isPublishedAndPublic(FedoraObject fo) {
		return fo.getPublished() && fo.isFilesPublic();
	}

	protected void addAccessLog(AccessLogRecord.Operation op) throws IOException {
		addAccessLog(uriInfo.getPath(), op);
	}

	protected void addAccessLog(String uri, AccessLogRecord.Operation op) throws IOException {
		AccessLogRecord alr = new AccessLogRecord(uri, getCurUser(), request.getRemoteAddr(),
				request.getHeader("User-Agent"), op);
		accessLogDao.create(alr);
	}

	protected URI getUri(String pid, String filepath) {
		return uriInfo.getBaseUriBuilder().path(StorageResource.class)
				.path(StorageResource.class, "getFileOrDirAsHtml").build(pid, filepath);
	}

	protected Response processJUpload(String pid, String path) {
		Response resp = null;
		List<FileItem> uploadedItems = null;
		File savedFile = null;
		int filePart = 0;
		boolean isLastPart = false;
	
		if (request.getParameter("jupart") != null && request.getParameter("jufinal") != null) {
			filePart = Integer.parseInt(request.getParameter("jupart"));
			isLastPart = request.getParameter("jufinal").equals("1");
		}
	
		try {
			uploadedItems = parseUploadRequest(request);
	
			// Retrieve pid and MD5 from request.
			String md5 = null;
	
			for (FileItem fi : uploadedItems) {
				if (fi.isFormField()) {
					if (fi.getFieldName().equals("md5sum0")) {
						md5 = fi.getString();
					}
				}
			}
			if (md5 == null || md5.length() == 0) {
				throw new NullPointerException("MD5 cannot be null.");
			}
			if (pid == null || pid.length() == 0) {
				throw new NullPointerException("Record Identifier cannot be null.");
			}
	
			// Check for write access to the fedora object.
			FedoraObject fo = fedoraObjectService.getItemByPidWriteAccess(pid);
	
			for (FileItem fi : uploadedItems) {
				if (!fi.isFormField()) {
					AbstractTempFileTask task;
					if (filePart > 0) {
						String partFilename = format("{0}-{1}-{2}.part", DcStorage.convertToDiskSafe(path),
								DcStorage.convertToDiskSafe(pid), md5);
						task = new PartTempFileTask(fi.getInputStream(), filePart, isLastPart,
								GlobalProps.getUploadDirAsFile(), partFilename);
					} else {
						task = new TempFileTask(fi.getInputStream(), GlobalProps.getUploadDirAsFile());
					}
					task.setExpectedMessageDigest(Algorithm.MD5, md5);
					savedFile = task.call();
	
					String uri = uriInfo.getPath(true).substring(0, uriInfo.getPath().indexOf(";jsessionid="));
	
					if (savedFile != null) {
						String relPath = path;
						relPath = appendSeparator(relPath);
						relPath += fi.getName();
						if (dcStorage.fileExists(pid, relPath)) {
							addAccessLog(uri, Operation.UPDATE);
						} else {
							addAccessLog(uri, Operation.CREATE);
						}
						dcStorage.addFile(pid, savedFile, relPath);
					}
				}
			}
	
			resp = Response.ok("SUCCESS", MediaType.TEXT_PLAIN_TYPE).build();
		} catch (Exception e) {
			if (savedFile != null && savedFile.exists()) {
				if (!savedFile.delete()) {
					savedFile.deleteOnExit();
				}
			}
			LOGGER.error(e.getMessage(), e);
			resp = Response.serverError().entity("ERROR: " + e.getMessage()).type(MediaType.TEXT_PLAIN_TYPE).build();
		} finally {
			if (uploadedItems != null) {
				for (FileItem fi : uploadedItems) {
					if (!fi.isInMemory()) {
						fi.delete();
					}
				}
			}
		}
	
		return resp;
	}

	protected Response processRestUpload(String pid, String path, InputStream is) {
		Response resp;
		FedoraObject fo = fedoraObjectService.getItemByPidWriteAccess(pid);
		File uploadedFile = null;
	
		try {
			TempFileTask tfTask = new TempFileTask(is, GlobalProps.getUploadDirAsFile());
			if (httpHeaders.getRequestHeader("Content-MD5") != null) {
				String providedMd5 = httpHeaders.getRequestHeader("Content-MD5").get(0);
				if (providedMd5 != null && providedMd5.length() > 0) {
					tfTask.setExpectedMessageDigest(Manifest.Algorithm.MD5, providedMd5);
				}
			}
			
			if (httpHeaders.getRequestHeader("content-length") != null) {
				long expectedSize = Long.parseLong(httpHeaders.getRequestHeader("content-length").get(0), 10);
				tfTask.setExpectedSize(expectedSize);
			}
			
			uploadedFile = tfTask.call();
			if (dcStorage.fileExists(pid, path)) {
				addAccessLog(Operation.UPDATE);
			} else {
				addAccessLog(Operation.CREATE);
			}
			dcStorage.addFile(pid, uploadedFile, path);
			resp = Response.ok(tfTask.getCalculatedMd()).build();
		} catch (NumberFormatException e) {
			throw new WebApplicationException(e, Status.BAD_REQUEST);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			resp = Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		} finally {
			FileUtils.deleteQuietly(uploadedFile);
		}
		return resp;
	}

	protected Response processDeleteFile(String pid, String fileInBag) {
		Response resp = null;
		LOGGER.info("User {} ({}) requested deletion of file {} in {}", getCurUsername(), getRemoteIp(), fileInBag, pid);
		fedoraObjectService.getItemByPidWriteAccess(pid);
		
		try {
			if (dcStorage.fileExists(pid, fileInBag)) {
				addAccessLog(Operation.DELETE);
			}
			dcStorage.deleteItem(pid, fileInBag);
			resp = Response.ok(format("File {0} deleted from {1}", fileInBag, pid)).build();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			resp = Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
		return resp;
	}

	/**
	 * Parses an HttpServletRequest and returns a list of FileItem objects. A fileItem can contain form data or a file
	 * that was uploaded by a user.
	 * 
	 * @param request
	 *            HttpServletRequest object to parse.
	 * @return FileItem objects as List&lt;FileItem&gt;
	 * @throws FileUploadException
	 */
	@SuppressWarnings("unchecked")
	private List<FileItem> parseUploadRequest(HttpServletRequest request) throws FileUploadException {
		// Create a new file upload handler.
		ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory(GlobalProps.getMaxSizeInMem(),
				GlobalProps.getUploadDirAsFile()));
		return (List<FileItem>) upload.parseRequest(request);
	}

	protected Response processSetFilesPublicFlag(String pid, String isFilesPublicStr) {
		Response resp;
		FedoraObject fo = fedoraObjectService.getItemByPid(pid);
		if (!permissionService.checkPermission(fo, CustomACLPermission.PUBLISH)) {
			throw new AccessDeniedException(format("User does not have Publish permissions for record {0}.", pid));
		}
		boolean isFilesPublic = Boolean.parseBoolean(isFilesPublicStr);
		fedoraObjectService.setFilesPublic(pid, isFilesPublic);
	
		try {
			if (!isFilesPublic) {
				dcStorage.deindexFilesInBag(pid);
			} else if (isFilesPublic && fo.getPublished()) {
				dcStorage.indexFilesInBag(pid);
			}
		} catch (IOException e) {
			LOGGER.warn("Error while processing files in record {} for indexing: {}", pid, e.getMessage());
		}
		resp = Response.ok().build();
		return resp;
	}

}
