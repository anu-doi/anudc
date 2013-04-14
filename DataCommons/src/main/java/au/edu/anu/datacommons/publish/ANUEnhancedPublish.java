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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.data.db.dao.PublishLocationDAO;
import au.edu.anu.datacommons.data.db.dao.PublishLocationDAOImpl;
import au.edu.anu.datacommons.data.db.model.FedoraObject;
import au.edu.anu.datacommons.data.db.model.PublishLocation;
import au.edu.anu.datacommons.exception.ValidateException;

/**
 * ANUSuperPublish
 * 
 * Australian National University Data Commons
 * 
 * Publishes to the given location and to ANU
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		28/03/2013	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
public class ANUEnhancedPublish extends GenericPublish implements Publish {
	private static final Logger LOGGER = LoggerFactory.getLogger(ANUEnhancedPublish.class);
	
	/**
	 * publish
	 * 
	 * Perform additional publish actions to ANU
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		28/03/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param fedoraObject The fedora object to publish to
	 * @param publishLocation The location to publish to
	 * @throws ValidateException
	 * @see au.edu.anu.datacommons.publish.GenericPublish#publish(au.edu.anu.datacommons.data.db.model.FedoraObject, au.edu.anu.datacommons.data.db.model.PublishLocation)
	 */
	@Override
	public void publish(FedoraObject fedoraObject, PublishLocation publishLocation) throws ValidateException {
		super.publish(fedoraObject, publishLocation);
		PublishLocation location = getPublishLocation("ANU");
		if (location != null) {
			super.publish(fedoraObject, location);
		}
		else {
			LOGGER.info("Unable to find ANU publish location");
		}
	}
	
	/**
	 * getPublishLocation
	 *
	 * Retrieve the publish location with the given code
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		28/03/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param code The code to retrieve the publish location for
	 * @return The publish location
	 */
	public PublishLocation getPublishLocation(String code) {
		PublishLocationDAO publishLocationDAO = new PublishLocationDAOImpl(PublishLocation.class);
		PublishLocation location = publishLocationDAO.getByCode(code);
		return location;
	}
}
