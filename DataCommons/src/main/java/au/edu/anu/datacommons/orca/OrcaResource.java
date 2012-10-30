package au.edu.anu.datacommons.orca;

import java.io.InputStream;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.xml.transform.TransformerException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.data.fedora.FedoraBroker;
import au.edu.anu.datacommons.properties.GlobalProps;
import au.edu.anu.datacommons.util.Constants;
import au.edu.anu.datacommons.xml.transform.ViewTransform;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.yourmediashelf.fedora.client.FedoraClientException;

/**
 * OrcaResource
 * 
 * Australian National University Data Commons
 * 
 * Performs a transform on the OAI-PMH providers rif-cs xml to strip out the OAI-PMH nodes
 * so that ORCA can use this RIF-CS.
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		30/10/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
@Path("orca")
public class OrcaResource {
	static final Logger LOGGER = LoggerFactory.getLogger(OrcaResource.class);
	
	/**
	 * getOrcaXML
	 *
	 * Retrieve the transformed xml for orca
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		30/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return
	 */
	@GET
	@Produces(MediaType.TEXT_XML)
	public Response getOrcaXML() {
		try {
			InputStream xslStream = FedoraBroker.getDatastreamAsStream(GlobalProps.getProperty(GlobalProps.PROP_ORCA_XSL), Constants.XSL_SOURCE);
			if (xslStream == null) {
				LOGGER.error("XSL stream is null");
				return Response.status(500).build();
			}
			ClientConfig config = new DefaultClientConfig();
			Client client = Client.create(config);
			
			WebResource webService = client.resource(UriBuilder.fromUri(GlobalProps.getProperty(GlobalProps.PROP_ORCA_RIFCS)).build());
			
			ClientResponse clientResponse = webService.type(MediaType.APPLICATION_FORM_URLENCODED).accept(MediaType.TEXT_XML).get(ClientResponse.class);
			
			InputStream xmlStream = clientResponse.getEntityInputStream();
			
			ViewTransform viewTransform = new ViewTransform();
			String transformedPage = viewTransform.transform(xmlStream, xslStream);
			return Response.ok(transformedPage).build();
		}
		catch(FedoraClientException e) {
			LOGGER.error("Error retrieving orca xsl");
		}
		catch(TransformerException e) {
			LOGGER.error("Error transforming xsl");
		}
		
		return Response.status(500).build();
	}
}
