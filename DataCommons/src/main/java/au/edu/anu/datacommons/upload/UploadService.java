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

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import au.edu.anu.datacommons.data.db.model.Users;
import au.edu.anu.datacommons.properties.GlobalProps;
import au.edu.anu.datacommons.storage.AbstractStorageResource;

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

	/**
	 * Displays the Storage Search page from where users can search for data files.
	 * 
	 * @return HTTP response
	 */
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
