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

package au.edu.anu.datacommons.item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import au.edu.anu.datacommons.comparator.TemplateAttributeSortByDisplay;
import au.edu.anu.datacommons.data.db.model.FedoraObject;
import au.edu.anu.datacommons.data.db.model.Template;
import au.edu.anu.datacommons.freemarker.GroupOptions;
import au.edu.anu.datacommons.freemarker.SecurityCheck;
import au.edu.anu.datacommons.freemarker.SelectOptions;
import au.edu.anu.datacommons.security.service.FedoraObjectService;
import au.edu.anu.datacommons.storage.info.RecordDataSummary;
import au.edu.anu.datacommons.xml.data.Data;
import au.edu.anu.datacommons.xml.sparql.Result;

import com.sun.jersey.api.view.Viewable;
import com.yourmediashelf.fedora.client.FedoraClientException;

/**
 * ItemResource
 * 
 * Australian National University Data Commons
 * 
 * Class that displays a page for an object.  This class was created to make a url
 * for identification and external linking.
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		10/10/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
@Path("/")
@Component
@Scope("request")
public class ItemResource {
	static final Logger LOGGER = LoggerFactory.getLogger(ItemResource.class);
	
	@Resource(name = "fedoraObjectServiceImpl")
	private FedoraObjectService fedoraObjectService;
	
	/**
	 * getItem
	 *
	 * Retrieves a web page for the specified item.
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		10/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param item The pid of the item to retrieve
	 * @return The page with the items information
	 */
	@GET
	@Path("{item}")
	public Response getItem(@PathParam("item") String item) {
		LOGGER.info("In /item getItem", item);
		FedoraObject fedoraObject = fedoraObjectService.getItemByPid(item);
		if (fedoraObject == null) {
			return Response.status(Status.NOT_FOUND).entity("Item not found").build();
		}
		try {
			fedoraObjectService.verifyActive(fedoraObject);
			Template template = fedoraObjectService.getTemplateByTemplateId(fedoraObject.getTmplt_id());
			
			template.getTemplateAttributes().sort(new TemplateAttributeSortByDisplay());
			
			Data editData = null;
			Data publishData = null;
			try {
				editData = fedoraObjectService.getEditData(fedoraObject);
			}
			catch (Exception e) {
				LOGGER.error("Exception retrieving edit data", e);
			}
			try {
				publishData = fedoraObjectService.getPublishData(fedoraObject);
			}
			catch (Exception e) {
				LOGGER.error("Exception retrieving publish data: {}", e.getMessage());
			}
			if (editData == null && publishData == null) {
				throw new AccessDeniedException("You do not have access to this resource");
			}
			Map<String, Object> values = new HashMap<String, Object>();
			if (publishData == null) {
				values.put("data", editData);
				
			}
			else {
				values.put("data", publishData);
				Data differences = fedoraObjectService.getDataDifferences(template, editData, publishData);
				values.put("differenceData", differences);
				
			}
			RecordDataSummary rdi = fedoraObjectService.getRecordDataSummary(fedoraObject);
			List<Result> links = fedoraObjectService.getLinks(fedoraObject);
			
			values.put("tmplt", template);
			values.put("item", fedoraObject);
			values.put("rdi", rdi);
			values.put("links", links);
			values.put("options", new SelectOptions());
			values.put("groups", new GroupOptions());
			values.put("security",new SecurityCheck(fedoraObject));
//			values.put("errormessage", errorMessage);
			
			Viewable viewable = new Viewable("/display/display.ftl", values);
			return Response.ok(viewable).build();
		}
		catch (JAXBException | FedoraClientException e) {
			LOGGER.error("Exception retrieing template", e);
		}
		return Response.serverError().build();
	}
}
