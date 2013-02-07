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
import au.edu.anu.datacommons.data.db.model.PublishIris;
import au.edu.anu.datacommons.data.db.model.PublishIrisPK;
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
 * </pre>
 *
 */
public class IRISPublish extends GenericPublish implements Publish {
	/**
	 * publish
	 * 
	 * Set in place the parts to publish to iris
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		20/11/2012	Genevieve Turner(GT)	Initial
	 * 0.2		11/12/2012	Genevieve Turner (GT)	Added the thrwoing of the validation exception
	 * </pre>
	 * 
	 * @param pid The pid of the object to publish
	 * @param publishCode The code to publish to
	 * @throws ValidateException
	 * @see au.edu.anu.datacommons.publish.GenericPublish#publish(java.lang.String, java.lang.String)
	 */
	@Override
	public void publish(String pid, String publishCode) throws ValidateException{
		super.publish(pid, publishCode);
		
		PublishIris publishIris = new PublishIris();
		PublishIrisPK publishIrisPK = new PublishIrisPK();
		publishIrisPK.setPid(pid);
		publishIrisPK.setPublishDate(new Date());
		
		publishIris.setId(publishIrisPK);
		publishIris.setStatus("INCOMPLETE");
		
		GenericDAO<PublishIris, PublishIrisPK> publishIrisDAO = new GenericDAOImpl<PublishIris, PublishIrisPK>(PublishIris.class);
		publishIrisDAO.create(publishIris);
	}
	
	/**
	 * unpublish
	 * 
	 * Unpublish from iris
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		20/11/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param pid The object to unpublish
	 * @param publishCode The object code to unpublish
	 * @see au.edu.anu.datacommons.publish.GenericPublish#unpublish(java.lang.String, java.lang.String)
	 */
	@Override
	public void unpublish(String pid, String publishCode) {
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
