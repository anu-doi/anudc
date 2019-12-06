package au.edu.anu.datacommons.freemarker;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.GenericTypeResolver;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.web.FilterInvocation;

import au.edu.anu.datacommons.data.db.model.FedoraObject;
import au.edu.anu.datacommons.security.acl.CustomACLPermission;
import au.edu.anu.datacommons.security.acl.PermissionService;
import au.edu.anu.datacommons.util.AppContext;

public class SecurityCheck {
	static final Logger LOGGER = LoggerFactory.getLogger(SecurityCheck.class);
	
	FedoraObject fedoraObject;
	
	public SecurityCheck(FedoraObject fedoraObject) {
		this.fedoraObject = fedoraObject;
	}
	
	public Boolean checkPermission(Integer access) {
		LOGGER.info("Access: {}", access);
		Boolean isPermitted = Boolean.FALSE;
		ApplicationContext appCtx = AppContext.getApplicationContext();
		PermissionService service = (PermissionService) appCtx.getBean("permissionService");
		
		Permission permission = getPermission(access);
		
		isPermitted = service.checkPermission(fedoraObject, permission);
		
		return isPermitted;
	}
	
	private Permission getPermission(int access) {
		List<Permission> permissions = CustomACLPermission.getPermissionList();
		Permission checkPermission = null;
		for (Permission perm : permissions) {
			perm.getMask();
			if (perm.getMask() == access) {
				LOGGER.info("Found permission: {}", perm.toString());
				return perm;
			}
		}
		return null;
	}
	
}
