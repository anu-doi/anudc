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

package au.edu.anu.datacommons.publish;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.data.db.dao.FedoraObjectDAO;
import au.edu.anu.datacommons.data.db.dao.FedoraObjectDAOImpl;
import au.edu.anu.datacommons.data.db.dao.PublishLocationDAO;
import au.edu.anu.datacommons.data.db.dao.PublishLocationDAOImpl;
import au.edu.anu.datacommons.data.db.model.FedoraObject;
import au.edu.anu.datacommons.data.db.model.PublishLocation;
import au.edu.anu.datacommons.data.fedora.FedoraBroker;
import au.edu.anu.datacommons.data.fedora.FedoraReference;
import au.edu.anu.datacommons.exception.ValidateException;

import com.yourmediashelf.fedora.client.FedoraClientException;

/**
 * GenericPublish
 * 
 * Australian National University Data Commons
 * 
 * Placeholder
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		08/06/2012	Genevieve Turner (GT)	Initial
 * 0.2		15/10/2012	Genevieve Turner (GT)	Added checkValidity function
 * 0.3		11/12/2012	Genevieve Turner (GT)	Added a check for if the record is publishable
 * </pre>
 *
 */
public class GenericPublish implements Publish {
	private static final Logger LOGGER = LoggerFactory.getLogger(GenericPublish.class);
	
	private boolean isAllowedToPublish = false;
	
	/**
	 * publish
	 * 
	 * Publishes to the specified location
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		08/06/2012	Genevieve Turner(GT)	Initial
	 * 0.4		28/03/2013	Genevieve Turner(GT)	Updated the input parameters
	 * </pre>
	 * 
	 * @param fedoraObject The fedora object to publish
	 * @param publishLocation The location to publish to
	 * @throws ValidateException
	 * @see au.edu.anu.datacommons.publish.Publish#publish(au.edu.anu.datacommons.data.db.model.FedoraObject, au.edu.anu.datacommons.data.db.model.PublishLocation)
	 */
	@Override
	public void publish(FedoraObject fedoraObject, PublishLocation publishLocation) throws ValidateException {
		LOGGER.debug("Publishing to {}", publishLocation.getCode());
		List<FedoraReference> references = new ArrayList<FedoraReference>();
		FedoraReference reference = new FedoraReference();
		reference.setPredicate_("http://anu.edu.au/publish");
		reference.setObject_(publishLocation.getCode());
		reference.setIsLiteral_(Boolean.TRUE);
		references.add(reference);
		
		FedoraReference reference2 = new FedoraReference();
		reference2.setPredicate_("info:fedora/fedora-system:def/model#hasModel");
		reference2.setObject_("info:fedora/def:DCContentModel");
		reference2.setIsLiteral_(Boolean.FALSE);
		references.add(reference2);
		try {
			FedoraBroker.addRelationship(fedoraObject.getObject_id(), reference);
			//addPublishLocation(fedoraObject.getObject_id(), publishLocation.getCode());
			addPublishLocation(fedoraObject, publishLocation);
			FedoraBroker.addRelationship(fedoraObject.getObject_id(), reference2);
			//FedoraBroker.addRelationships(pid, references);
		}
		catch (FedoraClientException e) {
			LOGGER.error("Exception publishing to " + publishLocation.getCode() + " for " + fedoraObject.getObject_id(), e);
		}
	}
	
	/**
	 * 
	 * addPublishLocation
	 *
	 * The add the publish location to the fedora object
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.4		28/03/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param fedoraObject The fedora object to add the published location to
	 * @param publishLocation The location to add
	 */
	private void addPublishLocation(FedoraObject fedoraObject, PublishLocation publishLocation) {
		boolean addPublisher = true;
		for (int i = 0; addPublisher && i < fedoraObject.getPublishedLocations().size(); i++) {
			PublishLocation loc = fedoraObject.getPublishedLocations().get(i);
			if (loc.equals(publishLocation) || loc.getId().equals(publishLocation.getId())) {
				addPublisher = false;
			}
		}
		if (addPublisher) {
			fedoraObject.getPublishedLocations().add(publishLocation);
		}
	}
	
	/**
	 * unpublish
	 * 
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		08/06/2012	Genevieve Turner(GT)	Initial
	 * 0.4		28/03/2013	Genevieve Turner(GT)	Updated the input parameters
	 * </pre>
	 * 
	 * @param fedoraObject The fedora object to unpublish
	 * @param publishLocation The location to unpublish
	 * @see au.edu.anu.datacommons.publish.Publish#unpublish(au.edu.anu.datacommons.data.db.model.FedoraObject, au.edu.anu.datacommons.data.db.model.PublishLocation)
	 */
	@Override
	public void unpublish(FedoraObject fedoraObject, PublishLocation publishLocation) {
		// TODO Auto-generated method stub
		LOGGER.info("Unpublishing from {}", publishLocation.getCode());
		
	}

	/**
	 * checkValidity
	 * 
	 * Checks the validity of the object
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		15/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param pid The pid to check validity for
	 * @return A list of validation error messages
	 * @see au.edu.anu.datacommons.publish.Publish#checkValidity(java.lang.String)
	 */
	public List<String> checkValidity(String pid) {
		Validate validate = new FieldValidate();
		isAllowedToPublish = validate.isValid(pid);
		return validate.getErrorMessages();
	}
	
	/**
	 * isAllowedToPublish
	 * 
	 * Indicates whether record is valid enough to allow for publishing
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		11/12/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return
	 * @see au.edu.anu.datacommons.publish.Publish#isAllowedToPublish()
	 */
	public boolean isAllowedToPublish() {
		return isAllowedToPublish;
	}
}
