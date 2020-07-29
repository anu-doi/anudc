package au.edu.anu.datacommons.data.db.dao;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import au.edu.anu.datacommons.data.db.PersistenceManager;
import au.edu.anu.datacommons.data.db.model.Template;
import au.edu.anu.datacommons.data.db.model.TemplateAttribute;

public class TemplateDAOImpl extends GenericDAOImpl<Template, Long> implements TemplateDAO {
	
	public TemplateDAOImpl() {
		super(Template.class);
	}

	public TemplateDAOImpl(Class<Template> type) {
		super(type);
	}

	@Override
	public Template getTemplateByPid(String pid) {
		EntityManager entityManager = PersistenceManager.getEntityManagerFactory().createEntityManager();
		try {
			Query query = entityManager.createQuery("from Template where templatePid = :pid");
			query.setParameter("pid", pid);
			Template template = (Template) query.getSingleResult();
//			template.getTemplateAttributes().size();
			for (TemplateAttribute attr : template.getTemplateAttributes()) {
				if ("Table".equals(attr.getFieldType().getName())) {
					attr.getColumns().size();
				}
			}
			template.getTemplateTabs().size();
			return template;
		}
		finally {
			entityManager.close();
		}
	}
	
	
}
