package au.edu.anu.datacommons.publish;

import java.util.Date;
import java.util.List;

import au.edu.anu.datacommons.data.db.dao.GenericDAO;
import au.edu.anu.datacommons.data.db.dao.GenericDAOImpl;
import au.edu.anu.datacommons.data.db.model.PublishIris;
import au.edu.anu.datacommons.data.db.model.PublishIrisPK;

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
	 * </pre>
	 * 
	 * @param pid The pid of the object to publish
	 * @param publishCode The code to publish to
	 * @see au.edu.anu.datacommons.publish.GenericPublish#publish(java.lang.String, java.lang.String)
	 */
	@Override
	public void publish(String pid, String publishCode) {
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
