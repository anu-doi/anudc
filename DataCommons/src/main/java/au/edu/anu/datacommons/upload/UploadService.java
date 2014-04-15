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

import static java.text.MessageFormat.format;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import au.edu.anu.datacommons.collectionrequest.CollectionDropbox;
import au.edu.anu.datacommons.collectionrequest.CollectionRequestItem;
import au.edu.anu.datacommons.data.db.dao.DropboxDAO;
import au.edu.anu.datacommons.data.db.dao.DropboxDAOImpl;
import au.edu.anu.datacommons.data.db.model.Users;
import au.edu.anu.datacommons.properties.GlobalProps;
import au.edu.anu.datacommons.security.AccessLogRecord.Operation;
import au.edu.anu.datacommons.storage.AbstractStorageResource;
import au.edu.anu.datacommons.storage.DcStorage;

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
public class UploadService extends AbstractStorageResource {
	private static final Logger LOGGER = LoggerFactory.getLogger(UploadService.class);
	private static final String BAGFILES_JSP = "/bagfiles.jsp";

	/**
	 * Accepts POST requests from a JUpload applet and saves the files on the server for further processing. Creates a
	 * placeholder datastream in the fedora object preventing reuploading to the same datastream.
	 * 
	 * @return A response with status information.
	 */
	@POST
	@Path("/{pid}")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	public Response doPostJUploadFilePart(@PathParam("pid") String pid) {
		return processJUpload(pid, "/");
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
		DropboxDAO dropboxDAO = new DropboxDAOImpl();
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
						resp = getBagFileOctetStreamResp(pid, removeDataPrefix(filename));
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
		LOGGER.info("User {} requested adding file {} in {}", getCurUsername(), fileInBag, pid);
		return processRestUpload(pid, removeDataPrefix(fileInBag), is);
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
		return processDeleteFile(pid, removeDataPrefix(fileInBag));
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
	public Response doPutSetFilesPublic(@PathParam("pid") String pid, String isFilesPublicStr) {
		Response resp = null;
		LOGGER.info("User {} requested change status of files {} to {}", getCurUsername(), pid, isFilesPublicStr);
		if (isFilesPublicStr == null || isFilesPublicStr.length() == 0) {
			resp = Response.status(Status.BAD_REQUEST).build();
		} else {
			resp = processSetFilesPublicFlag(pid, isFilesPublicStr);
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
		respEntity.append(curUser.getUsername());
		respEntity.append(":");
		respEntity.append(curUser.getDisplayName());
		resp = Response.ok(respEntity.toString()).build();
		return resp;
	}

	@GET
	@Produces(MediaType.TEXT_HTML)
	@Path("search")
	public Response doGetStorageSearchPage() {
		Response resp = null;

		Map<String, Object> model = new HashMap<String, Object>();
		model.put("solrUrl", GlobalProps.getStorageSolrUrl());
		resp = Response.ok(new Viewable("/storagesearch.jsp", model)).build();

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
			zipStream = dcStorage.createZipStream(pid, fileSet);
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
	 * Removes the payload directory prefix from a filepath. For example, "data/somedir/abc.txt" returns
	 * "somedir/abc.txt"
	 * 
	 * @param filepath
	 *            filepath from which to remove the payload directory prefix.
	 * 
	 * @return filepath as String with "data/" prefix removed.
	 */
	private String removeDataPrefix(String filepath) {
		if (filepath.startsWith("data/")) {
			filepath = filepath.substring(filepath.indexOf("data/") + 5);
		}
		return filepath;
	}
}
