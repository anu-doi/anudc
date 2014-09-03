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

package au.edu.anu.datacommons.services;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import au.edu.anu.datacommons.admin.AdminService;
import au.edu.anu.datacommons.data.db.model.Domains;
import au.edu.anu.datacommons.data.db.model.Groups;
import au.edu.anu.datacommons.data.solr.SolrManager;
import au.edu.anu.datacommons.data.solr.model.SolrSearchResult;
import au.edu.anu.datacommons.exception.DataCommonsException;

import com.sun.jersey.api.view.Viewable;

/**
 * AdminResource
 * 
 * Australian National University Data Commons
 * 
 * Administrative functions
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		14/08/2012	Genevieve Turner (GT)	Initial
 * 0.2		02/01/2012	Genevieve Turner (GT)	Updated to reflect changes in error handling
 * </pre>
 *
 */
@Component
@Scope("request")
@Path("/admin")
public class AdminResource {
	private static final Logger LOGGER = LoggerFactory.getLogger(AdminResource.class);
	
	@Resource(name = "adminServiceImpl")
	private AdminService adminService;
	
	/**
	 * listAllANUPublished
	 *
	 * Lists all the data commons records published to the ANU
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		14/08/2012	Genevieve Turner(GT)	Initial
	 * 0.2		02/01/2012	Genevieve Turner (GT)	Updated to reflect changes in error handling
	 * </pre>
	 * 
	 * @return
	 */
	@GET
	@Path("/anupublished")
	@Produces(MediaType.TEXT_HTML)
	public Response listAllANUPublished() {
		int numResults = 1000;
		
		Response response = null;
		SolrServer solrServer = SolrManager.getInstance().getSolrServer();
		
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery("published.all:*");
		solrQuery.addField("id");
		solrQuery.addField("published.name");
		solrQuery.addFilterQuery("location.published:ANU");
		solrQuery.setRows(numResults);
		
		try {
			SolrDocumentList documentList = new SolrDocumentList();
			QueryResponse queryResponse = solrServer.query(solrQuery);
			SolrDocumentList resultList = queryResponse.getResults();
			long numFound = resultList.getNumFound();
			documentList.addAll(resultList);
			for (int i = numResults; i < numFound; i = i + numResults) {
				solrQuery.setStart(i);
				queryResponse = solrServer.query(solrQuery);
				resultList = queryResponse.getResults();
				documentList.addAll(resultList);
			}
			
			SolrSearchResult solrSearchResult = new SolrSearchResult(documentList);
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("resultSet", solrSearchResult);
			response = Response.ok(new Viewable("/sitemap.jsp", model)).build();
		}
		catch (SolrServerException e) {
			LOGGER.error("Error retrieving results for page", e);
			throw new DataCommonsException(502, "Error retrieving results for page");
		}
		
		return response;
	}
	
	/**
	 * Get a page to create and display domains
	 * 
	 * @return The page
	 */
	@GET
	@Path("/domains")
	@Produces(MediaType.TEXT_HTML)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public Response getDomainModification() {
		Map<String, Object> model = new HashMap<String, Object>();
		List<Domains> domains = adminService.getDomains();
		Collections.sort(domains, new Comparator<Domains>() {
			@Override
			public int compare(Domains domain1, Domains domain2) {
				return domain1.getDomain_name().compareTo(domain2.getDomain_name());
			}
		});
		model.put("domains", domains);
		return Response.ok(new Viewable("/domains.jsp", model)).build();
	}
	
	/**
	 * Create a domain
	 * 
	 * @param domainName The name of the  domain to create
	 * @return The response
	 */
	@POST
	@Path("/domains")
	@Produces(MediaType.TEXT_HTML)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public Response createDomain(@FormParam("domainName") String domainName) {
		adminService.createDomain(domainName);
		UriBuilder builder = UriBuilder.fromResource(this.getClass()).path("domains");
		return Response.seeOther(builder.build()).build();
	}

	/**
	 * Get a page to create and display groups
	 * 
	 * @return The page
	 */
	@GET
	@Path("/groups")
	@Produces(MediaType.TEXT_HTML)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public Response getGroupsModification() {
		Map<String, Object> model = new HashMap<String, Object>();
		List<Domains> domains = adminService.getDomains();
		Collections.sort(domains, new Comparator<Domains>() {
			@Override
			public int compare(Domains domain1, Domains domain2) {
				return domain1.getDomain_name().compareTo(domain2.getDomain_name());
			}
		});
		model.put("domains", domains);
		List<Groups> groups = adminService.getGroups();
		Collections.sort(groups, new Comparator<Groups>() {
			@Override
			public int compare(Groups group1, Groups group2) {
				return group1.getGroup_name().compareTo(group2.getGroup_name());
			}
		});
		model.put("groups", groups);
		return Response.ok(new Viewable("/groups.jsp", model)).build();
	}
	
	/**
	 * Create a group
	 * 
	 * @param groupName The name of the group to create
	 * @param domainId The id of the domain to assign as the groups parent
	 * @return The response
	 */
	@POST
	@Path("/groups")
	@Produces(MediaType.TEXT_HTML)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public Response createDomain(@FormParam("groupName") String groupName, @FormParam("domain") Long domainId) {
		LOGGER.info("In group post. Group Name: {}, Domain Id: {}", groupName, domainId);
		adminService.createGroup(groupName, domainId);
		UriBuilder builder = UriBuilder.fromResource(this.getClass()).path("groups");
		return Response.seeOther(builder.build()).build();
	}
}
