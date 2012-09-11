package au.edu.anu.datacommons.xml.transform;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import au.edu.anu.datacommons.data.db.dao.GenericDAO;
import au.edu.anu.datacommons.data.db.dao.GenericDAOImpl;
import au.edu.anu.datacommons.data.db.dao.SelectCodeDAO;
import au.edu.anu.datacommons.data.db.dao.SelectCodeDAOImpl;
import au.edu.anu.datacommons.data.db.model.Groups;
import au.edu.anu.datacommons.data.db.model.SelectCode;
import au.edu.anu.datacommons.data.db.model.SelectCodePK;

/**
 * SelectExtension
 * 
 * Australian National University Data Commons
 * 
 * This class provides extra functions for select boxes, retrieving information from the database
 * for display purposes.
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		30/08/2012	Genevieve Turner (GT)	Initial
 * 0.2		11/09/2012	Genevieve Turner (GT)	Moved some functions to the SelectAction class.  Added 
 * </pre>
 *
 */
public class SelectExtension {
	static final Logger LOGGER = LoggerFactory.getLogger(SelectExtension.class);
	
	/**
	 * getOptionValue
	 *
	 * Gets the description for the given value, whether it be from the database
	 * or from the node list
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		30/08/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param codeType The name of the select list
	 * @param nodeList A list of options from the xml document
	 * @param value The selected value for the list
	 * @return
	 */
	public static String getOptionValue(String codeType, NodeList nodeList, String value) {
		String optionValue = null;
		// If its a group we should be checking the groups table
		if ("ownerGroup".equals(codeType)) {
			GenericDAO<Groups, Long> groupDAO = new GenericDAOImpl<Groups, Long>(Groups.class);
			Groups group = groupDAO.getSingleById(new Long(value));
			optionValue = group.getGroup_name();
		}
		else {
			// Otherwise we should be searching the select code table
			SelectCodePK selectCodePK = new SelectCodePK();
			selectCodePK.setSelect_name(codeType);
			selectCodePK.setCode(value);

			SelectCodeDAO selectCodeDAO = new SelectCodeDAOImpl(SelectCode.class);
			SelectCode selectCode = selectCodeDAO.getSingleById(selectCodePK);
			if (selectCode != null) {
				optionValue = selectCode.getDescription();
			}
		}
		// If there is no option from the database we check through the list provided with the field
		for (int i = 0;  optionValue == null && i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element elementNode = (Element) node;
				if(elementNode.getAttribute("value").equals(value)) {
					optionValue = elementNode.getAttribute("label");
				}
				
			}
		}
		// If the value is not in the database or a list somewhere then just display the code
		if (optionValue == null) {
			optionValue = value;
		}
		return optionValue;
	}
	
	/**
	 * getOptions
	 *
	 * Returns a list of options, either from the database or from the node list.
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		31/08/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param codeType The name of the select list
	 * @param nodeList A list of options from the xml document
	 * @return A list of options
	 */
	public static String getOptions(String codeType, NodeList nodeList) {
		return getOptions(codeType, nodeList, "");
	}
	
	/**
	 * getOptions
	 *
	 * Returns a list of options, either from the database or from the node list.
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		30/08/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param codeType The name of the select list
	 * @param nodeList A list of options from the xml document
	 * @param value The selected value for the drop down list
	 * @return A list of options
	 */
	public static String getOptions(String codeType, NodeList nodeList, String value) {
		SelectAction selectAction = new SelectAction(codeType, nodeList);
		String options = selectAction.formatOptions(value);
		
		return options;
	}
	
	/**
	 * addNewlines
	 *
	 * This function is to replace the new line character with a br tag.  This is due to 
	 * Xalan/XSL/Fedora Commons doing something funky with the new line characters and not
	 * using the standard character for new lines and thus it not being possible
	 * to place br replacement using the xsl syntax.
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		11/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param value
	 * @return
	 */
	public static String replaceNewlineWithBr(String value) {
		return value.replace("\n", "<br/>");
	}
}
