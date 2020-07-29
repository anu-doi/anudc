package au.edu.anu.datacommons.freemarker;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import au.edu.anu.datacommons.data.db.dao.GenericDAO;
import au.edu.anu.datacommons.data.db.dao.GenericDAOImpl;
import au.edu.anu.datacommons.data.db.model.Groups;
import au.edu.anu.datacommons.security.acl.PermissionService;
import au.edu.anu.datacommons.util.AppContext;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

public class GroupOptions implements TemplateMethodModelEx {
	static final Logger LOGGER = LoggerFactory.getLogger(GroupOptions.class);
	@Override
	public Object exec(List arguments) throws TemplateModelException {
		// TODO Auto-generated method stub
		if (arguments.size() == 1) {
			Object arg1 = arguments.get(0);
//			String groupStr = arg1.
			String groupStr = arg1.toString();
			Long groupId = Long.parseLong(groupStr);
//			Long groupId = (Long) arg1;
			
			GenericDAO<Groups, Long> groupsDAO = new GenericDAOImpl<Groups, Long>(Groups.class);
			Groups group = groupsDAO.getSingleById(groupId);
			
			return group;
//			Groups 
		}
		else {
			ApplicationContext appCtx = AppContext.getApplicationContext();
			PermissionService service = (PermissionService) appCtx.getBean("permissionService");
			
			// It would have been preferable to get the groups from the group service however
			// there are issues with the post filter and this class not being used via a Servlet call
			//TODO see if injection can work
			List<Groups> groups = service.getCreatePermissions();
			
			for (Groups group : groups) {
				LOGGER.debug("Group: {}, Name: {}", group.getId(), group.getGroup_name());
			}
			return groups;
		}
		
	}
	
}
