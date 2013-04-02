package au.edu.anu.datacommons.xslt;

import au.edu.anu.datacommons.xslt.db.dao.GenericDAO;
import au.edu.anu.datacommons.xslt.db.dao.GenericDAOImpl;
import au.edu.anu.datacommons.xslt.db.dao.LinkTypeDAO;
import au.edu.anu.datacommons.xslt.db.dao.LinkTypeDAOImpl;
import au.edu.anu.datacommons.xslt.db.model.LinkType;
import au.edu.anu.datacommons.xslt.db.model.SelectCode;
import au.edu.anu.datacommons.xslt.db.model.SelectCodePK;

/**
 * SelectExtension
 * 
 * Australian National University Data Commons
 * 
 * Extension class for saxon
 *
 * JUnit Coverage:
 * SelectExtensionTest
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		18/02/2013	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
public class SelectExtension {
	/**
	 * getSelectValue
	 *
	 * Get the value of the itme with the given code type and value
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		18/02/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param codeType
	 * @param value
	 * @return
	 */
	public static String getSelectValue(String codeType, String value) {
		String selectValue = null;
		
		SelectCodePK selectCodePK = new SelectCodePK();
		selectCodePK.setSelect_name(codeType);
		selectCodePK.setCode(value);
		
		GenericDAO<SelectCode, SelectCodePK> selectDAO = new GenericDAOImpl<SelectCode, SelectCodePK> (SelectCode.class);
		SelectCode selectCode = selectDAO.getSingleById(selectCodePK);
		
		if (selectCode != null) {
			selectValue = selectCode.getDescription();
		}
		
		return selectValue;
	}
	
	/**
	 * getRelationValue
	 *
	 * Get the full text of the relation type
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		18/02/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param relationType
	 * @return
	 */
	public static String getRelationValue(String relationType) {
		LinkTypeDAO linkTypeDAO = new LinkTypeDAOImpl(LinkType.class);
		LinkType linkType = linkTypeDAO.getByCode(relationType);
		
		if (linkType != null) {
			return linkType.getDescription();
		}
		
		return null;
	}
}
