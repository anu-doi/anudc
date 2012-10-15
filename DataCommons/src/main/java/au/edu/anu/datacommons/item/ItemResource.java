package au.edu.anu.datacommons.item;

import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import au.edu.anu.datacommons.data.db.model.FedoraObject;
import au.edu.anu.datacommons.security.service.FedoraObjectService;

import com.sun.jersey.api.view.Viewable;

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
		FedoraObject fedoraObject = fedoraObjectService.getItemByPid(item);
		Map<String, Object> values = fedoraObjectService.getViewPage(fedoraObject, "def:display", null);

		Viewable viewable = new Viewable((String) values.remove("topage"), values);
		return Response.ok(viewable).build();
	}
}
