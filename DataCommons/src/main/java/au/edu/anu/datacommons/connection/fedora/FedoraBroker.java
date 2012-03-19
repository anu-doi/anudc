package au.edu.anu.datacommons.connection.fedora;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.properties.GlobalProps;

import com.yourmediashelf.fedora.client.FedoraClient;
import com.yourmediashelf.fedora.client.FedoraClientException;
import com.yourmediashelf.fedora.client.FedoraCredentials;
import com.yourmediashelf.fedora.client.request.AddDatastream;
import com.yourmediashelf.fedora.client.request.AddRelationship;
import com.yourmediashelf.fedora.client.request.GetDatastreamDissemination;
import com.yourmediashelf.fedora.client.request.Ingest;
import com.yourmediashelf.fedora.client.request.ModifyDatastream;
import com.yourmediashelf.fedora.client.request.PurgeDatastream;
import com.yourmediashelf.fedora.client.response.AddDatastreamResponse;
import com.yourmediashelf.fedora.client.response.FedoraResponse;
import com.yourmediashelf.fedora.client.response.IngestResponse;
import com.yourmediashelf.fedora.client.response.ModifyDatastreamResponse;
import com.yourmediashelf.fedora.client.response.PurgeDatastreamResponse;


/**
 * FedoraBroker
 * 
 * Australian National University Data Commons
 * 
 * Performs actions with the fedora commons repository
 * 
 * Version - Date - Developer
 * 0.1 - 08/03/2012 - Genevieve Turner - Initial build
 * 0.2 - 14/03/2012 - Genevieve Turner - Updated getDatastreamAsStream method to be static
 * 
 */
public class FedoraBroker {
	private static final Logger LOGGER = LoggerFactory.getLogger(FedoraBroker.class);
	
	private static FedoraClient fedoraClient_ = null;
	
	static {
		try {
			FedoraCredentials fedoraCredentials = new FedoraCredentials(GlobalProps.getProperty(GlobalProps.PROP_FEDORA_URI), 
					GlobalProps.getProperty(GlobalProps.PROP_FEDORA_USERNAME),
					GlobalProps.getProperty(GlobalProps.PROP_FEDORA_PASSWORD));
			fedoraClient_ = new FedoraClient(fedoraCredentials);
		}
		catch (MalformedURLException e) {
			LOGGER.error(e.toString());
		}
	}
	
	/**
	 * createNewObject
	 * 
	 * Create a new object in the fedora commons database
	 * 
	 * Version - Date - Dev - Description
	 * 0.1 - 08/03/2012 - Genevieve Turner - Initial Build
	 * 
	 * @param namespace The namespace to create the object with
	 * @return Return the pid of the newly created object
	 * @throws FedoraClientException
	 */
	public static String createNewObject (String namespace) throws FedoraClientException {
		IngestResponse ingestResponse = new Ingest().namespace(namespace).execute(fedoraClient_);
		return ingestResponse.getPid();
	}
	
	/**
	 * addDatasstreamBySource
	 * 
	 * Creates a datastream with the supplied object
	 * 
	 * Version - Date - Dev - Description
	 * 0.1 - 08/03/2012 - Genevieve Turner - Initial Build
	 * 
	 * @param pid The pid of the object
	 * @param streamId The datastream to create the object in
	 * @param label The label for the object
	 * @param content The content to save to the datastream
	 * @return true if the action has completed
	 * @throws FedoraClientException
	 */
	public static boolean addDatasstreamBySource (String pid, String streamId, String label, String content) 
			throws FedoraClientException {
		AddDatastreamResponse sourceResponse = new AddDatastream(pid, streamId).controlGroup("X").dsLabel(label).content(content).mimeType(MediaType.TEXT_XML).execute(fedoraClient_);
		return true;
	}
	
	/**
	 * addDatastreamByReference
	 * 
	 * Creates the datastream with the supplied reference to the source
	 * 
	 * Version - Date - Dev - Description
	 * 0.1 - 08/03/2012 - Genevieve Turner - Initial Build
	 * 
	 * @param pid The pid of the object
	 * @param streamId The datastream to create the object in
	 * @param controlGroup The control group type
	 * @param label The label for the object
	 * @param location The location of the datasource
	 * @return true if the action has completed
	 * @throws FedoraClientException
	 */
	public static boolean addDatastreamByReference (String pid, String streamId, String controlGroup, String label, String location) 
			throws FedoraClientException {
		AddDatastreamResponse sourceResponse = new AddDatastream(pid, streamId).controlGroup(controlGroup).dsLabel(label).dsLocation(location).mimeType(MediaType.TEXT_XML).execute(fedoraClient_);
		return true;
	}
	
	/**
	 * modifyDatastreamBySource
	 * 
	 * Updates the datastream with the supplied object
	 * 
	 * Version - Date - Dev - Description
	 * 0.1 - 08/03/2012 - Genevieve Turner - Initial Build
	 * 
	 * @param pid The pid of the object
	 * @param streamId The datastream of the object to modify
	 * @param label The label for the object
	 * @param content The content to save to the datastream
	 * @return true if the action has completed
	 * @throws FedoraClientException
	 */
	public static boolean modifyDatastreamBySource (String pid, String streamId, String label, String content)
			throws FedoraClientException {
		ModifyDatastreamResponse sourceResponse = new ModifyDatastream(pid, streamId).dsLabel(label).content(content).execute(fedoraClient_);
		return true;
	}
	
	/**
	 * modifyDatastreamByReference
	 * 
	 * Updates the datastream with the supplied reference to the source
	 * 
	 * Version - Date - Dev - Description
	 * 0.1 - 08/03/2012 - Genevieve Turner - Initial Build
	 * 
	 * @param pid The pid of the object
	 * @param streamId The datastream of the object to modify
	 * @param label The label for the object
	 * @param location The location of the datasource
	 * @return true if the action has completed
	 * @throws FedoraClientException
	 */
	public static boolean modifyDatastreamByReference (String pid, String streamId, String label, String location)
			throws FedoraClientException {
		ModifyDatastreamResponse sourceResponse = new ModifyDatastream(pid, streamId).dsLabel(label).dsLocation(location).execute(fedoraClient_);
		return true;
	}
	
	public static boolean addRelationships (String pid, List<FedoraReference> references) throws FedoraClientException {
		PurgeDatastreamResponse purgeDatastreamResponse = new PurgeDatastream(pid, "RELS-EXT").execute(fedoraClient_);
		
		for (int i = 0; i < references.size(); i++) {
			FedoraReference reference = references.get(i);
			FedoraResponse relResponse = new AddRelationship(pid).predicate(reference.getPredicate_()).object(reference.getObject_()).isLiteral(reference.getIsLiteral_()).execute(fedoraClient_);
		}
		
		return true;
	}
	
	/**
	 * getDatastreamAsStream
	 * 
	 * Version - Date - Dev - Description
	 * 0.1 - 08/03/2012 - Genevieve Turner - Initial Build
	 * 0.2 - 14/03/2012 - Genevieve Turner - Updated method to be static
	 * 
	 * Returns the specified datastream as an InputStream
	 * 
	 * @param pid The pid of the object
	 * @param streamId The datastream in the object to retrieve
	 * @return The inputstream of the objects datastream
	 * @throws FedoraClientException
	 */
	public static InputStream getDatastreamAsStream (String pid, String streamId) throws FedoraClientException {
		FedoraResponse sourceResponse = new GetDatastreamDissemination (pid, streamId).execute(fedoraClient_);
		return sourceResponse.getEntityInputStream();
	}
	
	//public static void 
}
