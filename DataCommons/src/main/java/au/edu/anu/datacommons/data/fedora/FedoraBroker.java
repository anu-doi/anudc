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

package au.edu.anu.datacommons.data.fedora;

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
import com.yourmediashelf.fedora.client.request.GetObjectProfile;
import com.yourmediashelf.fedora.client.request.Ingest;
import com.yourmediashelf.fedora.client.request.ListDatastreams;
import com.yourmediashelf.fedora.client.request.ModifyDatastream;
import com.yourmediashelf.fedora.client.request.ModifyObject;
import com.yourmediashelf.fedora.client.request.PurgeDatastream;
import com.yourmediashelf.fedora.client.request.PurgeRelationship;
import com.yourmediashelf.fedora.client.response.AddDatastreamResponse;
import com.yourmediashelf.fedora.client.response.FedoraResponse;
import com.yourmediashelf.fedora.client.response.GetObjectProfileResponse;
import com.yourmediashelf.fedora.client.response.IngestResponse;
import com.yourmediashelf.fedora.client.response.ListDatastreamsResponse;
import com.yourmediashelf.fedora.client.response.ModifyDatastreamResponse;
import com.yourmediashelf.fedora.client.response.PurgeDatastreamResponse;
import com.yourmediashelf.fedora.generated.access.DatastreamType;


/**
 * FedoraBroker
 * 
 * Australian National University Data Commons
 * 
 * Performs actions with the fedora commons repository
 * 
 * Version	Date		Developer				Description
 * 0.1		08/03/2012	Genevieve Turner (GT)	Initial build
 * 0.2		14/03/2012	Genevieve Turner (GT)	Updated getDatastreamAsStream method to be static
 * 0.3		21/03/2012	Rahul Khanna (RK)		Added getClient method
 * 0.4		29/03/2012	Genevieve Turner (GT)	Added addRelationship method
 * 0.5		26/04/2012	Genevieve Turner (GT)	Added getDatastreamList method
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
	 * Version	Date		Deveveloper				Description
	 * 0.1		08/03/2012	Genevieve Turner (GT)	Initial Build
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
	 * addDatastreamBySource
	 * 
	 * Creates a datastream with the supplied object
	 * 
	 * Version	Date		Deveveloper				Description
	 * 0.1		08/03/2012	Genevieve Turner (GT)	Initial Build
	 * 
	 * @param pid The pid of the object
	 * @param streamId The datastream to create the object in
	 * @param label The label for the object
	 * @param content The content to save to the datastream
	 * @return true if the action has completed
	 * @throws FedoraClientException
	 */
	public static boolean addDatastreamBySource (String pid, String streamId, String label, String content) 
			throws FedoraClientException {
		@SuppressWarnings("unused")
		AddDatastreamResponse sourceResponse = new AddDatastream(pid, streamId).controlGroup("X").dsLabel(label).content(content).mimeType(MediaType.TEXT_XML).execute(fedoraClient_);
		return true;
	}
	
	/**
	 * addDatastreamByReference
	 * 
	 * Creates the datastream with the supplied reference to the source
	 * 
	 * Version	Date		Deveveloper				Description
	 * 0.1		08/03/2012	Genevieve Turner (GT)	Initial Build
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
		@SuppressWarnings("unused")
		AddDatastreamResponse sourceResponse = new AddDatastream(pid, streamId).controlGroup(controlGroup).dsLabel(label).dsLocation(location).mimeType(MediaType.TEXT_XML).execute(fedoraClient_);
		return true;
	}
	
	/**
	 * modifyDatastreamBySource
	 * 
	 * Updates the datastream with the supplied object
	 * 
	 * Version	Date		Deveveloper				Description
	 * 0.1		08/03/2012	Genevieve Turner (GT)	Initial Build
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
		@SuppressWarnings("unused")
		ModifyDatastreamResponse sourceResponse = new ModifyDatastream(pid, streamId).dsLabel(label).content(content).execute(fedoraClient_);
		return true;
	}
	
	/**
	 * modifyDatastreamByReference
	 * 
	 * Updates the datastream with the supplied reference to the source
	 * 
	 * Version	Date		Deveveloper				Description
	 * 0.1		08/03/2012	Genevieve Turner (GT)	Initial Build
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
		@SuppressWarnings("unused")
		ModifyDatastreamResponse sourceResponse = new ModifyDatastream(pid, streamId).dsLabel(label).dsLocation(location).execute(fedoraClient_);
		return true;
	}
	
	/**
	 * modifyDatastreamByReference
	 * 
	 * Updates the datastream with the supplied reference to the source
	 * 
	 * Version	Date		Deveveloper				Description
	 * 0.1		08/03/2012	Genevieve Turner (GT)	Initial Build
	 * 
	 * @param pid The pid of the object
	 * @param references A list of references to place in the system
	 * @return true if the action has completed
	 * @throws FedoraClientException
	 */
	public static boolean addRelationships (String pid, List<FedoraReference> references) throws FedoraClientException {
		@SuppressWarnings("unused")
		PurgeDatastreamResponse purgeDatastreamResponse = new PurgeDatastream(pid, "RELS-EXT").execute(fedoraClient_);
		
		for (int i = 0; i < references.size(); i++) {
			FedoraReference reference = references.get(i);
			@SuppressWarnings("unused")
			FedoraResponse relResponse = new AddRelationship(pid).predicate(reference.getPredicate_()).object(reference.getObject_()).isLiteral(reference.getIsLiteral_()).execute(fedoraClient_);
		}
		
		return true;
	}
	
	/**
	 * addRelationship
	 * 
	 * Adds the given relationship to the specified pid.
	 * 
	 * Version	Date		Deveveloper				Description
	 * 0.4		29/03/2012	Genevieve Turner (GT)	Added to create relationships
	 * 
	 * @param pid The pid of the object
	 * @param references A list of references to place in the system
	 * @return true if the action has completed
	 * @throws FedoraClientException
	 */
	public static boolean addRelationship (String pid, FedoraReference reference)
			throws FedoraClientException {
		@SuppressWarnings("unused")
		FedoraResponse relResponse = new AddRelationship(pid).predicate(reference.getPredicate_()).object(reference.getObject_()).isLiteral(reference.getIsLiteral_()).execute(fedoraClient_);
		
		return true;
	}
	
	public static boolean removeRelationship (String pid, FedoraReference reference)
			throws FedoraClientException {
		@SuppressWarnings("unused")
		FedoraResponse relResponse = new PurgeRelationship(pid).predicate(reference.getPredicate_()).object(reference.getObject_()).isLiteral(reference.getIsLiteral_()).execute(fedoraClient_);
		
		return true;
	}
	
	/**
	 * getDatastreamAsStream
	 * 
	 * Version	Date		Deveveloper				Description
	 * 0.1		08/03/2012	Genevieve Turner (GT)	Initial Build
	 * 0.2		14/03/2012	Genevieve Turner (GT)	Updated method to be static
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

	/**
	 * getDatastreamList
	 * 
	 * Get a list of datastreams for the given object
	 * 
	 * Version	Date		Deveveloper				Description
	 * 0.5		04/04/2012	Genevieve Turner (GT)	Added method
	 * 
	 * @param pid The pid of the object
	 * @return A list of datastreams for objects
	 * @throws FedoraClientException
	 */
	public static List<DatastreamType> getDatastreamList(String pid) throws FedoraClientException {
		ListDatastreamsResponse response = new ListDatastreams(pid).format("xml").execute(fedoraClient_);
		return response.getDatastreams();
	}
	
	/**
	 * getObjectState
	 * 
	 * Get the state of the object
	 * 
	 * @param pid The pid of the object
	 * @return The object state
	 * @throws FedoraClientException
	 */
	public static String getObjectState(String pid) throws FedoraClientException {
		GetObjectProfileResponse response = new GetObjectProfile(pid).format("xml").execute(fedoraClient_);
		return response.getState();
	}
	
	/**
	 * updateObjectState
	 * 
	 * Update the state of the object
	 * 
	 * @param pid THe pid of the object
	 * @param state The state to change the object to
	 * @throws FedoraClientException
	 */
	public static void updateObjectState(String pid, String state) throws FedoraClientException {
		@SuppressWarnings("unused")
		FedoraResponse response = new ModifyObject(pid).state(state).execute(fedoraClient_);
	}

	/**
	 * getClient
	 * 
	 * Gets the fedoraClient instance.
	 * 
	 * Australian National University Data Commons
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		1/05/2012	Rahul Khanna (RK)	Initial
	 * 0.2		8/05/2012	Rahul Khanna (RK)	Deprecated method.
	 * </pre>
	 * @return
	 */
	@Deprecated
	public static FedoraClient getClient()
	{
		return fedoraClient_;
	}
}
