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

package au.edu.anu.datacommons.upload;

import static java.text.MessageFormat.*;
import gov.loc.repository.bagit.Manifest;
import gov.loc.repository.bagit.Manifest.Algorithm;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
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

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import au.edu.anu.datacommons.collectionrequest.CollectionDropbox;
import au.edu.anu.datacommons.collectionrequest.CollectionRequestItem;
import au.edu.anu.datacommons.collectionrequest.PageMessages;
import au.edu.anu.datacommons.collectionrequest.PageMessages.MessageType;
import au.edu.anu.datacommons.data.db.dao.AccessLogRecordDAOImpl;
import au.edu.anu.datacommons.data.db.dao.DropboxDAO;
import au.edu.anu.datacommons.data.db.dao.DropboxDAOImpl;
import au.edu.anu.datacommons.data.db.dao.UsersDAOImpl;
import au.edu.anu.datacommons.data.db.model.FedoraObject;
import au.edu.anu.datacommons.data.db.model.Users;
import au.edu.anu.datacommons.properties.GlobalProps;
import au.edu.anu.datacommons.security.AccessLogRecord;
import au.edu.anu.datacommons.security.AccessLogRecord.Operation;
import au.edu.anu.datacommons.security.acl.CustomACLPermission;
import au.edu.anu.datacommons.security.acl.PermissionService;
import au.edu.anu.datacommons.security.service.FedoraObjectService;
import au.edu.anu.datacommons.storage.DcStorage;
import au.edu.anu.datacommons.storage.info.BagSummary;
import au.edu.anu.datacommons.storage.info.FileSummaryMap;
import au.edu.anu.datacommons.storage.temp.PartTempFileTask;
import au.edu.anu.datacommons.storage.temp.TempFileTask;
import au.edu.anu.datacommons.storage.verifier.VerificationResults;
import au.edu.anu.datacommons.storage.verifier.VerificationTask;

import com.sun.jersey.api.NotFoundException;
import com.sun.jersey.api.view.Viewable;

/**
 * This class provides REST end points for storing and retrieving data uploaded to collections.
 * 
 * @author Rahul Khanna
 */
@Path("/upload")
@Component
@Scope("request")
public class UploadService {
	private static final Logger LOGGER = LoggerFactory.getLogger(UploadService.class);
	private static final String BAGFILES_JSP = "/bagfiles.jsp";

	@Context
	private UriInfo uriInfo;
	@Context
	private HttpServletRequest request;
	@Context
	private HttpHeaders httpHeaders;

	@Resource(name = "fedoraObjectServiceImpl")
	private FedoraObjectService fedoraObjectService;

	@Resource(name = "permissionService")
	private PermissionService permissionService;
	
	@Resource(name = "dcStorage")
	private DcStorage dcStorage;
	
	private AccessLogRecordDAOImpl accessLogDao = new AccessLogRecordDAOImpl(AccessLogRecord.class);

	/**
	 * Accepts POST requests from a JUpload applet and saves the files on the server for further processing. Creates a
	 * placeholder datastream in the fedora object preventing reuploading to the same datastream.
	 * 
	 * @return A response with status information.
	 */
	@POST
	@Path("/")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	public Response doPostJUploadFilePart() {
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
			String pid = null;

			for (FileItem fi : uploadedItems) {
				if (fi.isFormField()) {
					if (fi.getFieldName().equals("md5sum0")) {
						md5 = fi.getString();
					} else if (fi.getFieldName().equals("pid")) {
						pid = fi.getString();
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
			fedoraObjectService.getItemByPidWriteAccess(pid);

			for (FileItem fi : uploadedItems) {
				if (!fi.isFormField()) {
					if (filePart > 0) {
						String partFilename = DcStorage.convertToDiskSafe(pid) + "-" + md5;
						PartTempFileTask task = new PartTempFileTask(fi.getInputStream(), filePart, isLastPart,
								GlobalProps.getUploadDirAsFile(), partFilename);
						task.setExpectedMessageDigest(Algorithm.MD5, md5);
						savedFile = task.call();
						if (savedFile != null) {
							String uri = uriInfo.getPath().substring(0, uriInfo.getPath().indexOf(";jsessionid="));
							uri = format("{0}/bag/{1}/data/{2}", uri, pid, fi.getName());
							if (dcStorage.fileExists(pid, fi.getName())) {
								addAccessLog(uri, Operation.UPDATE);
							} else {
								addAccessLog(uri, Operation.CREATE);
							}
							dcStorage.addFileToBag(pid, savedFile, fi.getName());
						}
					} else {
						TempFileTask task = new TempFileTask(fi.getInputStream(), GlobalProps.getUploadDirAsFile());
						task.setExpectedMessageDigest(Algorithm.MD5, md5);
						savedFile = task.call();
						String uri = uriInfo.getPath().substring(0, uriInfo.getPath().indexOf(";jsessionid="));
						uri = format("{0}/bag/{1}/data/{2}", uri, pid, fi.getName());
						if (dcStorage.fileExists(pid, fi.getName())) {
							addAccessLog(uri, Operation.UPDATE);
						} else {
							addAccessLog(uri, Operation.CREATE);
						}
						dcStorage.addFileToBag(pid, savedFile, fi.getName());
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
			resp = Response.serverError().entity(e.getMessage()).type(MediaType.TEXT_PLAIN_TYPE).build();
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
	
	/**
	 * Returns the details of contents of a bag.
	 * 
	 * @param pid
	 *            Pid of the collection whose bag details are returned
	 * @return Response as HTML including BagSummary object
	 */
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Path("bag/{pid}")
	public Response doGetBagFileListingAsHtml(@PathParam("pid") String pid) {
		Response resp = null;
		Map<String, Object> model = new HashMap<String, Object>();

		FedoraObject fo = fedoraObjectService.getItemByPid(pid);;
		if (fo == null) {
			throw new NotFoundException(format("Record {0} not found", pid));
		}
		LOGGER.info("User {} requested bag files page of {}", getCurUsername(), pid);
		
		// Check if record is published AND files are public. If not, check permissions.
		if (!(fo.getPublished() && fo.isFilesPublic())) {
			fo = null;
			fo = fedoraObjectService.getItemByPidReadAccess(pid);
		}
		
		model.put("fo", fo);
		
		if (dcStorage.bagExists(pid)) {
			BagSummary bagSummary;
			try {
				addAccessLog(Operation.READ);
				bagSummary = dcStorage.getBagSummary(pid);
				model.put("bagSummary", bagSummary);
				model.put("bagInfoTxt", bagSummary.getBagInfoTxt().entrySet());
				if (bagSummary.getExtRefsTxt() != null) {
					model.put("extRefsTxt", bagSummary.getExtRefsTxt().entrySet());
				}
				UriBuilder uriBuilder = UriBuilder.fromUri(uriInfo.getBaseUri()).path(UploadService.class)
						.path(UploadService.class, "doGetFileInBagAsOctetStream2");
				model.put("dlBaseUri", uriBuilder.build(pid, "").toString());
				model.put("downloadAsZipUrl", uriBuilder.build(pid, "zip").toString());
				model.put("isFilesPublic", fo.isFilesPublic().booleanValue());
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
				PageMessages messages = new PageMessages();
				messages.add(MessageType.ERROR, e.getMessage(), model);
			}
		}
		
		resp = Response.ok(new Viewable(BAGFILES_JSP, model), MediaType.TEXT_HTML_TYPE).build();
		return resp;
	}
	
	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("bag/{pid}/admin")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public Response doAdminTaskJsonXml(@PathParam("pid") String pid, @QueryParam("task") String task) {
		Response resp = null;
		if (!dcStorage.bagExists(pid)) {
			throw new NotFoundException();
		}

		try {
			if (task.equals("verify")) {
				VerificationResults results = dcStorage.verifyBag(pid);
				resp = Response.ok(results).build();
			}
		} catch (WebApplicationException e) {
			throw e;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			resp = Response.serverError().build();
		}

		return resp;
	}
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Path("bag/{pid}/admin")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public Response doAdminTaskHtml(@PathParam("pid") String pid, @QueryParam("task") String task) {
		Response resp = null;
		UriBuilder redirUri = UriBuilder.fromUri(uriInfo.getBaseUri()).path(UploadService.class);
		Map<String, Object> model = new HashMap<String, Object>();
		if (!dcStorage.bagExists(pid)) {
			throw new NotFoundException();
		}

		try {
			if (task.equals("verify")) {
				VerificationResults results = dcStorage.verifyBag(pid);
				model.put("results", results);
				resp = Response.ok(new Viewable("/verificationresults.jsp", model)).build();
			} else if (task.equals("recomplete")) {
				LOGGER.info("User {} requested bag completion of {}", getCurUsername(), pid);
				dcStorage.recompleteBag(pid);
				resp = Response.temporaryRedirect(
						redirUri.path(UploadService.class, "doGetBagFileListingAsHtml")
								.queryParam("smsg", "Files rescanning commenced and will run in the background.")
								.build(pid)).build();
			}
		} catch (WebApplicationException e) {
			throw e;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			resp = Response.serverError().build();
		}

		return resp;
	}
	

	
	/**
	 * Returns a BagSummary in JSON or XML format.
	 * 
	 * @param pid
	 *            Identifier of record whose bag summary is requested
	 * @return Response containing BagSummary in JSON or XML format.
	 */
	@GET
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Path("bag/{pid}")
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	public Response doGetBagSummary(@PathParam("pid") String pid) {
		Response resp = null;
		
		fedoraObjectService.getItemByPidReadAccess(pid);
		if (!dcStorage.bagExists(pid)) {
			throw new NotFoundException(format("Bag not found for {0}", pid));
		}
		
		try {
			addAccessLog(Operation.READ);
			BagSummary bagSummary = dcStorage.getBagSummary(pid);
			resp = Response.ok(bagSummary).build();
		} catch (IOException e) {
			resp = Response.serverError().build();
		}
		
		return resp;
	}

	/**
	 * Returns the contents of a file of a group of files combined into a ZipStream as InputStream in the Response
	 * object. The user gets a request to open or save the requested file. This method checks that the user requesting
	 * the file has a valid collection request.
	 * 
	 * @param pid
	 *            Pid of collection whose files
	 * @param filename
	 *            filename of the file being requested. E.g. "data/file.txt"
	 * @param dropboxAccessCode
	 *            Access Code of the dropbox that the requestor's been given access to
	 * @param password
	 *            Password of dropbox
	 * @return Response containing octet_stream of file or files as zipfile.
	 */
	@GET
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@Path("files/{pid}/{fileInBag:.*}")
	public Response doGetFileInBagAsOctetStream(@PathParam("pid") String pid, @PathParam("fileInBag") String filename,
			@QueryParam("dropboxAccessCode") Long dropboxAccessCode, @QueryParam("p") String password) {
		Response resp = null;

		LOGGER.trace("pid: {}, filename: {}", pid, filename);

		// Get dropbox requesting file.
		DropboxDAO dropboxDAO = new DropboxDAOImpl(CollectionDropbox.class);
		CollectionDropbox dropbox = dropboxDAO.getSingleByAccessCode(dropboxAccessCode);
		Users requestor = dropbox.getCollectionRequest().getRequestor();
		String username = SecurityContextHolder.getContext().getAuthentication().getName();

		// If dropbox is valid and the requestor of the collection request is the one accessing it, return file as octet
		// stream.
		try {
			if (dropbox.isValid(password) && requestor.getUsername().equals(username)) {
				LOGGER.info("Dropbox details valid. ID: {}, Access Code: {}. Returning file requested.", dropbox
						.getId().toString(), dropbox.getAccessCode().toString());
				Set<CollectionRequestItem> items = dropbox.getCollectionRequest().getItems();
				if (filename.equalsIgnoreCase("zip")) {
					Set<String> fileSet = new HashSet<String>();
					for (CollectionRequestItem item : items) {
						fileSet.add(item.getItem());
					}
					resp = getBagFilesAsZip(pid, fileSet, DcStorage.convertToDiskSafe(pid) + ".zip");
				} else {
					boolean isAllowedItem = false;
					for (CollectionRequestItem item : items) {
						if (item.getItem().equals(filename)) {
							isAllowedItem = true;
							break;
						}
					}
					if (isAllowedItem) {
						addAccessLog(Operation.READ);
						resp = getBagFileOctetStreamResp(pid, filename);
					} else {
						resp = Response.status(Status.FORBIDDEN).build();
					}
				}
			} else {
				LOGGER.warn("Unauthorised access to Dropbox ID: {}, Access Code: {}. Returning HTTP 403 Forbidden.",
						dropbox.getId().toString(), dropbox.getAccessCode().toString());
				resp = Response.status(Status.FORBIDDEN).build();
			}

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			resp = Response.serverError().entity(e.getMessage()).build();
		}
		return resp;
	}

	/**
	 * Returns a file in a collection's bag as InputStream in Response.
	 * 
	 * @param pid
	 *            Pid of the collection containing the file
	 * @param fileRequested
	 *            File being requested. E.g. "data/File.txt"
	 * @return File contents as InputStream in Response
	 */
	@GET
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@Path("bag/{pid}/{fileInBag:.*}")
	public Response doGetFileInBagAsOctetStream2(@PathParam("pid") String pid,
			@PathParam("fileInBag") String fileRequested, @QueryParam("file") Set<String> filepaths) {
		LOGGER.trace("pid: {}, filename: {}", pid, fileRequested);
		Response resp = null;

		FedoraObject fo = fedoraObjectService.getItemByPid(pid);;
		if (fo == null) {
			throw new NotFoundException(format("Record {0} not found", pid));
		}
		LOGGER.info("User {} requested bag files page of {}", getCurUsername(), pid);
		
		// Check if record is published AND files are public. If not, check permissions.
		if (!(fo.getPublished() && fo.isFilesPublic())) {
			fo = null;
			fo = fedoraObjectService.getItemByPidReadAccess(pid);
		}

		try {
			if (fileRequested.equals("zip")) {
				FileSummaryMap fsMap = dcStorage.getBagSummary(pid).getFileSummaryMap();
				
				if (filepaths == null || filepaths.size() == 0) {
					filepaths.addAll(fsMap.keySet());
				} else {
					for (Iterator<String> it = filepaths.iterator(); it.hasNext(); ) {
						if (!fsMap.containsKey(it.next())) {
							it.remove();
						}
					}
				}
				
				LOGGER.info("User {} requested {} bag files {} in {} as zip", new Object[]{getCurUsername(), filepaths.size(), filepaths, pid});
				Users curUser = getCurUser();
				if (curUser != null) {
					addAccessLog(Operation.READ);
				}
				resp = getBagFilesAsZip(pid, filepaths, format("{0}.{1}", DcStorage.convertToDiskSafe(pid), "zip"));
			} else {
				LOGGER.info("User {} requested bag file {} in {}", new Object[]{getCurUsername(), fileRequested, pid});
				if (!dcStorage.fileExists(pid, fileRequested)) {
					throw new NotFoundException(format("File {0} not found in {1}", fileRequested, pid));
				}
				resp = getBagFileOctetStreamResp(pid, fileRequested);
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			resp = Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}

		return resp;
	}

	/**
	 * Adds a single file to a bag.
	 * 
	 * @param pid
	 *            Pid of the collection to which a file will be added
	 * @param fileInBag
	 *            Filename to be stored in the bag. For example, "data/File.txt"
	 * @param is
	 *            InputStream to read and save into the file
	 * @return HTTP response as Response
	 */
	@POST
	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("bag/{pid}/{fileInBag:.*}")
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	public Response doAddFileToBag(@PathParam("pid") String pid, @PathParam("fileInBag") String fileInBag,
			InputStream is) {
		Response resp = null;
		LOGGER.info("User {} requested adding file {} in {}", new Object[]{getCurUsername(), fileInBag, pid});
		fedoraObjectService.getItemByPidWriteAccess(pid);
		File uploadedFile = null;

		try {
			TempFileTask tfTask = new TempFileTask(is, GlobalProps.getUploadDirAsFile());
			if (httpHeaders.getRequestHeader("Content-MD5") != null) {
				String providedMd5 = httpHeaders.getRequestHeader("Content-MD5").get(0);
				if (providedMd5 != null && providedMd5.length() > 0) {
					tfTask.setExpectedMessageDigest(Manifest.Algorithm.MD5, providedMd5);
				}
			}
			uploadedFile = tfTask.call();
			if (dcStorage.fileExists(pid, fileInBag)) {
				addAccessLog(Operation.UPDATE);
			} else {
				addAccessLog(Operation.CREATE);
			}
			dcStorage.addFileToBag(pid, uploadedFile, fileInBag);
			resp = Response.ok(tfTask.getCalculatedMd()).build();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			resp = Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		} finally {
			FileUtils.deleteQuietly(uploadedFile);
		}
		return resp;
	}

	/**
	 * Deletes a file in a collection's bag.
	 * 
	 * @param pid
	 *            Collection which contains the file to be deleted.
	 * @param fileInBag
	 *            File to be deleted. E.g. "data/File.txt"
	 * @return Response with HTTP OK if successful, HTTP INTERNAL_SERVER_ERROR otherwise.
	 */
	@DELETE
	@Path("bag/{pid}/{fileInBag:.*}")
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	public Response doDeleteFileInBag(@PathParam("pid") String pid, @PathParam("fileInBag") String fileInBag) {
		Response resp = null;
		LOGGER.info("User {} requested deletion of file {} in {}", new Object[]{getCurUsername(), fileInBag, pid});
		fedoraObjectService.getItemByPidWriteAccess(pid);
		
		try {
			if (dcStorage.fileExists(pid, fileInBag)) {
				addAccessLog(Operation.DELETE);
			}
			dcStorage.deleteFileFromBag(pid, fileInBag);
			resp = Response.ok(format("File {0} deleted from {1}", fileInBag, pid)).build();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			resp = Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
		return resp;
	}

	/**
	 * Adds and/or deletes a set of external reference URLs stored in a collection.
	 * 
	 * <p>
	 * URL format: <code>
	 * http://.../bag/test:123/extrefs?addUrl=http://http://www.add1.com&addUrl=http://www.add2.com&deleteUrl=http://delete1.com&deleteUrl=http://delete2.com
	 * </code>
	 * 
	 * @param pid
	 *            Pid of the collection whose external references are to be added/deleted.
	 * @param addUrlSet
	 *            Set of URLs to add as Set&lt;String&gt;
	 * @param deleteUrlSet
	 *            Set of URLs to delete as Set&lt;String&gt;
	 * @return Response with HTTP OK if successful, HTTP INTERNAL_SERVER_ERROR otherwise.
	 */
	@POST
	@Path("bag/{pid}/extrefs")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	public Response doAddDeleteExtRef(@PathParam("pid") String pid, @FormParam("addUrl") Set<String> addUrlSet,
			@FormParam("deleteUrl") Set<String> deleteUrlSet) {
		Response resp = null;
		LOGGER.info("User {} requested addition/deletion of extRefs in {}", getCurUsername(), pid);
		fedoraObjectService.getItemByPidWriteAccess(pid);
		try {
			dcStorage.addExtRefs(pid, addUrlSet);
			dcStorage.deleteExtRefs(pid, deleteUrlSet);

			resp = Response.ok(format("Added {0} to {1}.", addUrlSet, pid)).build();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			resp = Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}

		return resp;
	}
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("bag/{pid}/ispublic")
	public Response doGetIsFilesPublic(@PathParam("pid") String pid) {
		Response resp = null;
		boolean isFilesPublic = fedoraObjectService.isFilesPublic(pid);
		resp = Response.ok(Boolean.toString(isFilesPublic)).build();
		return resp;
	}

	@PUT
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("bag/{pid}/ispublic")
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	public Response doPutSetFilesPublic(@PathParam("pid") String pid, String isFilesPublic) {
		Response resp = null;
		LOGGER.info("User {} requested change status of files {} to {}", new Object[]{getCurUsername(), pid, isFilesPublic});
		if (isFilesPublic == null || isFilesPublic.length() == 0) {
			resp = Response.status(Status.BAD_REQUEST).build();
		} else {
			FedoraObject fedoraObject = fedoraObjectService.getItemByPid(pid);
			if (!permissionService.checkPermission(fedoraObject, CustomACLPermission.PUBLISH)) {
				throw new AccessDeniedException(format("User does not have Publish permissions for record {0}.", pid));
			}
			fedoraObjectService.setFilesPublic(pid, Boolean.parseBoolean(isFilesPublic));
			resp = Response.ok().build();
		}
		return resp;
	}
	
	/**
	 * Returns information about the current logged on user in the format username:displayName. E.g.
	 * "u1234567:John Smith"
	 * 
	 * @return Response including user information As String.
	 */
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("userinfo")
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	public Response doGetUserInfo() {
		Users curUser = getCurUser();
		Response resp = null;
		StringBuilder respEntity = new StringBuilder();
		//respEntity.append(curUser.getUsername());
		respEntity.append(curUser.getEmail());
		respEntity.append(":");
		respEntity.append(curUser.getDisplayName());
		resp = Response.ok(respEntity.toString()).build();
		return resp;
	}

	/**
	 * Gets a Users object containing information about the currently logged in user.
	 * 
	 * @return Users object containing information about the currently logged in user.
	 */
	private Users getCurUser() {
		return new UsersDAOImpl(Users.class).getUserByName(getCurUsername());
	}
	
	private String getCurUsername() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}


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
	private Response getBagFileOctetStreamResp(String pid, String fileInBag) throws IOException {
		Response resp = null;
		InputStream is = null;

		if (!dcStorage.fileExists(pid, fileInBag)) {
			throw new NotFoundException(format("File {0} not found in record {1}", fileInBag, pid));
		}
		is = dcStorage.getFileStream(pid, fileInBag);
		ResponseBuilder respBuilder = Response.ok(is, MediaType.APPLICATION_OCTET_STREAM_TYPE);
		// Add filename, MD5 and file size to response header.
		respBuilder = respBuilder.header("Content-Disposition",
				format("attachment; filename=\"{0}\"", getFilenameFromPath(fileInBag)));
		respBuilder = respBuilder.header("Content-MD5", dcStorage.getFileMd5(pid, fileInBag));
		respBuilder = respBuilder.header("Content-Length", dcStorage.getFileSize(pid, fileInBag));
		respBuilder = respBuilder.lastModified(dcStorage.getFileLastModified(pid, fileInBag));
		resp = respBuilder.build();

		return resp;
	}

	/**
	 * Creates a Response object containing a Zip file comprised of data from multiple files in a bag of a collection.
	 * 
	 * @param pid
	 *            Pid of the collection whose files are to be included in the Response object.
	 * 
	 * @param fileSet
	 *            Set of file names as Set&lt;String&gt; that are to be included in the Response.
	 * @param zipFilename
	 *            Name of the zip file that will be added to the Content-Disposition HTTP header.
	 * @return Response object
	 */
	private Response getBagFilesAsZip(String pid, Set<String> fileSet, String zipFilename) {
		Response resp = null;
		InputStream zipStream;
		try {
			zipStream = dcStorage.getFilesAsZipStream(pid, fileSet);
			ResponseBuilder respBuilder = Response.ok(zipStream, MediaType.APPLICATION_OCTET_STREAM_TYPE);
			respBuilder.header("Content-Disposition", format("attachment; filename=\"{0}\"", zipFilename));
			resp = respBuilder.build();
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
			throw new NotFoundException(e.getMessage());
		}

		return resp;
	}


	/**
	 * Gets the filename part from a full filename on the client that may contain file separators different from the
	 * ones used on the server.
	 * 
	 * @param fullFilename
	 *            Full path and filename as on the client's computer.
	 * @return Filename only as String.
	 */
	private String getFilenameFromPath(String fullFilename) {
		// Extract the type of slash being used in the filename.
		char clientSlashType = (fullFilename.lastIndexOf("\\") > 0) ? '\\' : '/';

		// Get the index where the filename starts. -1 if the path isn't specified.
		int clientFilenameStartIndex = fullFilename.lastIndexOf(clientSlashType);

		// Get the part of the string after the last instance of the path separator.
		String filename = fullFilename.substring(clientFilenameStartIndex + 1, fullFilename.length());
		return filename;
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

	private boolean hasRole(String[] roles) {
		boolean hasRole = false;
		for (GrantedAuthority authority : SecurityContextHolder.getContext().getAuthentication().getAuthorities()) {
			String userRole = authority.getAuthority();
			for (String role : roles) {
				if (role.equals(userRole)) {
					hasRole = true;
					break;
				}
			}

			if (hasRole) {
				break;
			}
		}

		return hasRole;
	}
	
	private void addAccessLog(AccessLogRecord.Operation op) throws IOException {
		addAccessLog(uriInfo.getPath(), op);
	}
	
	private void addAccessLog(String uri, AccessLogRecord.Operation op) throws IOException {
		AccessLogRecord alr = new AccessLogRecord(uri, getCurUser(), request.getRemoteAddr(),
				request.getHeader("User-Agent"), op);
		accessLogDao.create(alr);
	}

}
