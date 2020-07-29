package au.edu.anu.datacommons.data.db.dao;

import au.edu.anu.datacommons.data.db.model.Template;

public interface TemplateDAO extends GenericDAO<Template, Long> {
	public Template getTemplateByPid(String pid);
}
