package au.edu.anu.datacommons.freemarker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.data.db.dao.SelectCodeDAO;
import au.edu.anu.datacommons.data.db.dao.SelectCodeDAOImpl;
import au.edu.anu.datacommons.data.db.model.SelectCode;
import au.edu.anu.datacommons.data.db.model.SelectCodePK;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

public class SelectOptions implements TemplateMethodModelEx {
	static final Logger LOGGER = LoggerFactory.getLogger(SelectOptions.class);
	
	@Override
	public Object exec(List arguments) throws TemplateModelException {
		if (arguments.size() == 1) {
			Object arg1 = arguments.get(0);
			if (arg1 != null) {
				String code = arg1.toString();
				if (code != null) {
					SelectCodeDAO selectDAO = new SelectCodeDAOImpl();
					List<SelectCode> selectCodes = selectDAO.getOptionsByName(code);
					
					return selectCodes;
				}
			}
		}
		else if (arguments.size() == 2) {
			Object arg1 = arguments.get(0);
			Object arg2 = arguments.get(1);
			LOGGER.debug("Argument 1: {}, Argument 2: {}", arg1, arg2);
			if (arg1 != null && arg2 != null) {
				String selectName = arg1.toString();
				String code = arg2.toString();
				
				SelectCodeDAO selectDAO = new SelectCodeDAOImpl();
				SelectCodePK pk = new SelectCodePK();
				pk.setSelect_name(selectName);
				pk.setCode(code);
				SelectCode selectCode = selectDAO.getSingleById(pk);
				return selectCode;
			}
		}
		return new ArrayList<SelectCode>();
	}
}
