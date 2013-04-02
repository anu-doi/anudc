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

import java.util.List;

import au.edu.anu.datacommons.data.db.model.FedoraObject;
import au.edu.anu.datacommons.data.db.model.PublishLocation;
import au.edu.anu.datacommons.exception.ValidateException;

/**
 * GenericPublish
 * 
 * Australian National University Data Commons
 * 
 * Interface containing the methods for publishing
 * 
 * JUnit coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		15/05/2012	Genevieve Turner (GT)	Initial build
 * 0.2		08/06/2012	Genevieve Turner (GT)	Renamed to Publish from GenericPublish
 * 0.3		15/10/2012	Genevieve Turner (GT)	Added checkValidity function
 * 0.4		11/12/2012	Genevieve Turner (GT)	Added isAllowedToPublish method
 * 0.5		28/03/2013	Genevieve Turner(GT)	Updated the paremeters
 * </pre>
 * 
 */
public interface Publish {
	/**
	 * publish
	 * 
	 * Publishes data to the appropriate service
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		15/05/2012	Genevieve Turner (GT)	Initial build
	 * </pre>
	 * 
	 * @param pid The pid of the object to publish
	 */
	//public void publish(String pid, String publishCode) throws ValidateException;
	/**
	 * publish
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		15/05/2012	Genevieve Turner (GT)	Initial build
	 * 0.5		28/03/2013	Genevieve Turner(GT)	Updated the paremeters
	 * </pre>
	 * 
	 * @param fedoraObject The fedora object to publish
	 * @param publishLocation The location to publish to
	 * @throws ValidateException
	 */
	public void publish(FedoraObject fedoraObject, PublishLocation publishLocation) throws ValidateException;
	
	/**
	 * unpublish
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		15/05/2012	Genevieve Turner (GT)	Initial build
	 * 0.5		28/03/2013	Genevieve Turner(GT)	Updated the paremeters
	 * </pre>
	 * 
	 * @param fedoraObject The fedora object to unpublish
	 * @param publishLocation The location to unpublish from
	 */
	public void unpublish(FedoraObject fedoraObject, PublishLocation publishLocation);
	
	/**
	 * checkValidity
	 *
	 * Checks the validity of the object
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.3		15/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param pid The pid to check validity for
	 * @return A list of validation error messages
	 */
	public List<String> checkValidity(String pid);
	
	/**
	 * isAllowedToPublish
	 *
	 * Indicates that the record is valid enough for publishing
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.4		11/12/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return True if the records is valid enough for publish
	 */
	public boolean isAllowedToPublish();
}
