package au.edu.anu.datacommons.external;

import java.util.List;

import javax.ws.rs.core.MultivaluedMap;

import au.edu.anu.datacommons.webservice.bindings.FedoraItem;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

/**
 * Interface implemented by Metadata Provider classes that retrieve information for a record from an external source
 * and creates a FedoraItem object.
 * 
 * @author Rahul Khanna
 *
 */
public interface ExternalMetadataProvider {

	/**
	 * Gets the friendly name of the Metadata Provider.
	 * 
	 * @return Name of provider as String
	 */
	public String getFriendlyName();
	
	/**
	 * Gets the fully qualified class name of the metadata provider.
	 * 
	 * @return Fully qualified class name as String
	 */
	public String getFqClassName();
	
	/**
	 * Gets the parameters required by this provider.
	 * 
	 * @return List of ParamInfo objects
	 */
	public List<ParamInfo> getRequiredParams();
	
	/**
	 * Retrieves the metadata from the provider.
	 * 
	 * @param params
	 *            MultivaluedMap containing parameter name as keys and its value(s)
	 * @return Fedora Item that's retrieved.
	 * @throws ExternalMetadataException
	 *             when unable to complete request
	 */
	public FedoraItem retrieveMetadata(MultivaluedMap<String, String> params) throws ExternalMetadataException;
}
