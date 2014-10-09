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
package au.edu.anu.datacommons.security.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Service;

import au.edu.anu.datacommons.data.db.dao.GenericDAO;
import au.edu.anu.datacommons.data.db.dao.GenericDAOImpl;
import au.edu.anu.datacommons.data.db.model.Template;
import au.edu.anu.datacommons.security.acl.CustomACLPermission;
import au.edu.anu.datacommons.security.acl.PermissionService;

@Service("templateServiceImpl")
public class TemplateServiceImpl implements TemplateService {
	static final Logger LOGGER = LoggerFactory.getLogger(TemplateServiceImpl.class);
	
	@Resource(name="permissionService")
	PermissionService permissionService;

	@Override
	public List<Template> getTemplates() {
		GenericDAO<Template, Long> templateDAO = new GenericDAOImpl<Template, Long>(Template.class);
		List<Template> templates = templateDAO.getAll();
		
		// Sort by id
		Collections.sort(templates, new Comparator<Template>(){
			@Override
			public int compare(Template template1, Template template2) {
				return template1.getId().compareTo(template2.getId());
			}
		});
		
		return templates;
	}

	@Override
	public List<Template> getTemplatesForUser(String username) {
		List<Template> templates = getTemplates();
		List<Template> templatesWithPermission = new ArrayList<Template>();
		for (Template template : templates) {
			List<Permission> permission = permissionService.getListOfPermission(Template.class, template.getId(), username);
			if (permission.contains(CustomACLPermission.WRITE)) {
				templatesWithPermission.add(template);
			}
		}
		return templatesWithPermission;
	}

}
