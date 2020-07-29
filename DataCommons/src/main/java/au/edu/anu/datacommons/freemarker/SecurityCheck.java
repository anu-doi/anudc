package au.edu.anu.datacommons.freemarker;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.context.SecurityContextHolder;

import au.edu.anu.datacommons.data.db.model.FedoraObject;
import au.edu.anu.datacommons.security.CustomUser;
import au.edu.anu.datacommons.security.acl.CustomACLPermission;
import au.edu.anu.datacommons.security.acl.PermissionService;
import au.edu.anu.datacommons.util.AppContext;

public class SecurityCheck {
	static final Logger LOGGER = LoggerFactory.getLogger(SecurityCheck.class);
	
	FedoraObject fedoraObject;
	
	public SecurityCheck() {
		this.fedoraObject = null;
	}
	
	public SecurityCheck(FedoraObject fedoraObject) {
		this.fedoraObject = fedoraObject;
	}
	
	public Boolean checkPermission(Integer access) {
		Boolean isPermitted = Boolean.FALSE;
		if (fedoraObject != null) {
			ApplicationContext appCtx = AppContext.getApplicationContext();
			PermissionService service = (PermissionService) appCtx.getBean("permissionService");
			
			Permission permission = getPermission(access);
			
			isPermitted = service.checkPermission(fedoraObject, permission);
		}
		return isPermitted;
	}
	
	public String getUsername() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		if (principal instanceof CustomUser) {
			CustomUser user = (CustomUser) principal;
			
			String username = user.getDisplayName() + " (" + user.getUsername() + ")";
			return username;
		}
		
		return null;
	}
	
	private Permission getPermission(int access) {
		if (fedoraObject != null) {
			List<Permission> permissions = CustomACLPermission.getPermissionList();
			for (Permission perm : permissions) {
				perm.getMask();
				if (perm.getMask() == access) {
					return perm;
				}
			}
		}
		return null;
	}
	
}
