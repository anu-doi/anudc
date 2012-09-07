package au.edu.anu.datacommons.xml.transform;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
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
import au.edu.anu.datacommons.security.service.GroupService;
import au.edu.anu.datacommons.security.service.GroupServiceImpl;

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
 * </pre>
 *
 */
public class SelectExtension {
	static final Logger LOGGER = LoggerFactory.getLogger(SelectExtension.class);
/*	
	@Resource(name="groupService")
	static GroupService groupServiceTest;
	*/
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
		String options = null;
		if ("ownerGroup".equals(codeType)) {
			options = getGroups(value);
		}
		else {
			options = getSelectOptions(codeType, value);
		}
		if (options.length() == 0) {
			StringBuilder defaultOptions = new StringBuilder();
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element elementNode = (Element) node;
					defaultOptions.append(addOption(elementNode.getAttribute("value"), elementNode.getAttribute("label"), value));
				}
			}
			options = defaultOptions.toString();
		}
		
		return options;
	}
	
	/**
	 * getGroups
	 *
	 * Gets the group options.
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		30/08/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param value	The selected value if it exists
	 * @return A list of options of groups for the user
	 */
	private static String getGroups(String value) {
		ApplicationContext ctx = new AnnotationConfigApplicationContext(GroupServiceImpl.class);
		GroupService groupService = ctx.getBean(GroupServiceImpl.class);
		
		//GroupService groupService = new GroupServiceImpl();
		List<Groups> groups = groupService.getCreateGroups();
		StringBuilder groupsStr = new StringBuilder();
		for (Groups group : groups) {
			groupsStr.append(addOption(group.getId().toString(), group.getGroup_name(), value));
		}
		return groupsStr.toString();
	}
	
	/**
	 * getSelectOptions
	 *
	 * Gets the select code options
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		30/08/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param codeType The field to retrieve options from
	 * @param value	The selected value if it exists
	 * @return A list of options from the given code
	 */
	private static String getSelectOptions(String codeType, String value) {
		SelectCodeDAO selectCodeDAO = new SelectCodeDAOImpl(SelectCode.class);
		List<String> fieldNames = new ArrayList<String>();
		fieldNames.add(codeType);
		List<SelectCode> selectCodes = selectCodeDAO.getOptionsByNames(fieldNames);
		
		StringBuilder selectStr = new StringBuilder();
		for (SelectCode selectCode : selectCodes) {
			selectStr.append(addOption(selectCode.getId().getCode(), selectCode.getDescription(), value));
		}
		
		return selectStr.toString();
	}
	
	/**
	 * addOption
	 *
	 * Create an option
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		31/08/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param value The value part of the option
	 * @param description The description part of the value
	 * @param selectedValue The value that has been selected for this list
	 * @return A option for a select list
	 */
	private static String addOption(String value, String description, String selectedValue) {
		StringBuilder optionStr = new StringBuilder();
		optionStr.append("<option value='");
		optionStr.append(value);
		optionStr.append("'");
		if (value.equals(selectedValue)) {
			optionStr.append(" selected='selected'");
		}
		optionStr.append(">");
		optionStr.append(description);
		optionStr.append("</option>");
		
		return optionStr.toString();
	}
}
