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

import java.util.Date;
import java.util.List;

import au.edu.anu.datacommons.data.db.dao.GenericDAO;
import au.edu.anu.datacommons.data.db.dao.GenericDAOImpl;
import au.edu.anu.datacommons.data.db.model.FedoraObject;
import au.edu.anu.datacommons.data.db.model.PublishIris;
import au.edu.anu.datacommons.data.db.model.PublishIrisPK;
import au.edu.anu.datacommons.data.db.model.PublishLocation;
import au.edu.anu.datacommons.exception.ValidateException;

/**
 * IRISPublish
 * 
 * Australian National University Data Commons
 * 
 * Methods for publishing to IRIS
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		20/11/2012	Genevieve Turner (GT)	Initial
 * 0.2		11/12/2012	Genevieve Turner (GT)	Added the thrwoing of the validation exception for publish
 * 0.3		28/03/2013	Genevieve Turner(GT)	Updated input parameters
 * </pre>
 *
 */
public class IRISPublish extends GenericPublish implements Publish {
	/**
	 * 
	 * publish
	 * 
	 * Set in place the parts to publish to iris
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		20/11/2012	Genevieve Turner(GT)	Initial
	 * 0.3		28/03/2013	Genevieve Turner(GT)	Updated input parameters
	 * </pre>
	 * 
	 * @param fedoraObject The fedora object to publish
	 * @param publishLocation The location to publish to
	 * @throws ValidateException
	 * @see au.edu.anu.datacommons.publish.GenericPublish#publish(au.edu.anu.datacommons.data.db.model.FedoraObject, au.edu.anu.datacommons.data.db.model.PublishLocation)
	 */
	@Override
	public void publish(FedoraObject fedoraObject, PublishLocation publishLocation) throws ValidateException{
		super.publish(fedoraObject, publishLocation);
		
		PublishIris publishIris = new PublishIris();
		PublishIrisPK publishIrisPK = new PublishIrisPK();
		publishIrisPK.setPid(fedoraObject.getObject_id());
		publishIrisPK.setPublishDate(new Date());
		
		publishIris.setId(publishIrisPK);
		publishIris.setStatus("INCOMPLETE");
		
		GenericDAO<PublishIris, PublishIrisPK> publishIrisDAO = new GenericDAOImpl<PublishIris, PublishIrisPK>(PublishIris.class);
		publishIrisDAO.create(publishIris);
	}
	
	/**
	 * unpublish
	 * 
	 * Unpublish from IRIS
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		20/11/2012	Genevieve Turner(GT)	Initial
	 * 0.3		28/03/2013	Genevieve Turner(GT)	Updated input parameters
	 * </pre>
	 * 
	 * @param fedoraObject The fedora object to unpublish
	 * @param publishLocation The location to unpublish from
	 * @see au.edu.anu.datacommons.publish.GenericPublish#unpublish(au.edu.anu.datacommons.data.db.model.FedoraObject, au.edu.anu.datacommons.data.db.model.PublishLocation)
	 */
	@Override
	public void unpublish(FedoraObject fedoraObject, PublishLocation publishLocation) {
		//TODO implement
	}
	
	/**
	 * checkValidity
	 * 
	 * Check the validity to before publishing to iris
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		20/11/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param pid The pid of the object to validate
	 * @return Validity errors
	 * @see au.edu.anu.datacommons.publish.GenericPublish#checkValidity(java.lang.String)
	 */
	@Override
	public List<String> checkValidity(String pid) {
		return super.checkValidity(pid);
	}
}
