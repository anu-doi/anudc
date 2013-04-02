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

import au.edu.anu.datacommons.data.db.model.FedoraObject;
import au.edu.anu.datacommons.data.db.model.PublishLocation;
import au.edu.anu.datacommons.data.fedora.FedoraBroker;
import au.edu.anu.datacommons.data.fedora.FedoraReference;
import au.edu.anu.datacommons.exception.ValidateException;

import com.yourmediashelf.fedora.client.FedoraClientException;

/**
 * ANDSPublish
 * 
 * Australian National University Data Commons
 * 
 * Methods for publishing to ANDS
 * 
 * JUnit coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		15/05/2012	Genevieve Turner (GT)	Initial build
 * 0.2		08/06/2012	Genevieve Turner (GT)	Updated to incorporate some changes to publishing
 * 0.3		17/06/2012	Genevieve Turner (GT)	Added validation prior to publishing
 * 0.4		15/10/2012	Genevieve Turner(GT)	Added checkValidity		
 * 0.5		10/12/2012	Genevieve Turner (GT)	Updated to use the default validation functions and added the isAllowedToPublish field
 * 0.6		02/01/2012	Genevieve Turner (GT)	Updated to allow for changes to error handling
 * 0.7		28/03/2013	Genevieve Turner (GT)	Updated input parameters for publish and unpublish
 * </pre>
 * 
 */
public class ANDSPublish extends ANUEnhancedPublish implements Publish {
	private static final Logger LOGGER = LoggerFactory.getLogger(ANDSPublish.class);
	
	private boolean isAllowedToPublish = false;

	/**
	 * publish
	 * 
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		15/05/2012	Genevieve Turner (GT)	Initial build
	 * 0.2		08/06/2012	Genevieve Turner (GT)	Updated to incorporate some changes to publishing
	 * 0.3		17/06/2012	Genevieve Turner (GT)	Added validation prior to publishing
	 * 0.5		10/12/2012	Genevieve Turner (GT)	Updated to use isAllowedToPublish
	 * 0.6		02/01/2012	Genevieve Turner (GT)	Updated to allow for changes to error handling
	 * 0.7		28/03/2012	Genevieve Turner (GT)	Updated input parameters
	 * </pre>
	 * 
	 * @param fedoraObject The fedora object to unpublish
	 * @param publishLocation The location to unpublish from
	 * @throws ValidateException
	 * @see au.edu.anu.datacommons.publish.ANUEnhancedPublish#publish(au.edu.anu.datacommons.data.db.model.FedoraObject, au.edu.anu.datacommons.data.db.model.PublishLocation)
	 */
	@Override
	public void publish(FedoraObject fedoraObject, PublishLocation publishLocation) throws ValidateException {
		//Validate validate = new ANDSValidate();
		List<String> errorMessages = checkValidity(fedoraObject.getObject_id());
		if (!isAllowedToPublish) {
			List<String> messages = new ArrayList<String>(errorMessages);
			messages.add(0, "Error publishing to " + publishLocation.getCode());
			throw new ValidateException(messages);
		}
		
		super.publish(fedoraObject, publishLocation);
		FedoraReference reference = new FedoraReference();
		reference.setPredicate_("info:fedora/fedora-system:def/model#hasModel");
		reference.setObject_("info:fedora/def:RIFCSContentModel");
		reference.setIsLiteral_(Boolean.FALSE);
		
		try {
			FedoraBroker.addRelationship(fedoraObject.getObject_id(), reference);
		}
		catch (FedoraClientException e) {
			LOGGER.error("Exception adding content model to " + fedoraObject.getObject_id(), e);
		}
	}
	
	/**
	 * 
	 * unpublish
	 * 
	 * Unpublishes from ANDS (Australian National Data Service)
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		15/05/2012	Genevieve Turner(GT)	Initial
	 * 0.7		28/03/2013	Genevieve Turner(GT)	Updated parameters
	 * </pre>
	 * 
	 * @param fedoraObject The fedora object to unpublish
	 * @param publishLocation The location to unpublish from
	 * @see au.edu.anu.datacommons.publish.GenericPublish#unpublish(au.edu.anu.datacommons.data.db.model.FedoraObject, au.edu.anu.datacommons.data.db.model.PublishLocation)
	 */
	@Override
	public void unpublish(FedoraObject fedoraObject, PublishLocation publishLocation) {
		LOGGER.info("unpublishing from ands");
	}

	/**
	 * checkValidity
	 * 
	 * Checks the validity of the object
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.4		15/10/2012	Genevieve Turner(GT)	Initial
	 * 0.5		10/12/2012	Genevieve Turner (GT)	Added the setting of isAllowedToPublish
	 * </pre>
	 * 
	 * @param pid The pid to check validity for
	 * @return A list of validation error messages
	 * @see au.edu.anu.datacommons.publish.GenericPublish#checkValidity(java.lang.String)
	 */
	@Override
	public List<String> checkValidity(String pid) {
		Validate validate = new ANDSValidate();
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
	 * 0.5		10/12/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return
	 * @see au.edu.anu.datacommons.publish.GenericPublish#isAllowedToPublish()
	 */
	public boolean isAllowedToPublish() {
		return isAllowedToPublish;
	}
}
