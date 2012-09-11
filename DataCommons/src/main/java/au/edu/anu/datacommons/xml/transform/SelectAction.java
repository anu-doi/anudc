package au.edu.anu.datacommons.xml.transform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import au.edu.anu.datacommons.data.db.dao.SelectCodeDAO;
import au.edu.anu.datacommons.data.db.dao.SelectCodeDAOImpl;
import au.edu.anu.datacommons.data.db.model.Groups;
import au.edu.anu.datacommons.data.db.model.SelectCode;
import au.edu.anu.datacommons.security.service.GroupService;
import au.edu.anu.datacommons.security.service.GroupServiceImpl;

/**
 * SelectAction
 * 
 * Australian National University Data Commons
 * 
 * Creates and formats code value lists
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		11/09/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
public class SelectAction {
	static final Logger LOGGER = LoggerFactory.getLogger(SelectAction.class);
	
	private Map<String, String> codes_;
	
	/**
	 * Constructor
	 * 
	 * Constructor for 
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		11/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param codeType The field name to get codes for
	 * @param nodeList The list of default code/value pairs
	 */
	public SelectAction(String codeType, NodeList nodeList) {
		codes_ = new HashMap<String, String>();
		if ("ownerGroup".equals(codeType)) {
			getGroups();
		}
		else {
			getSelectCodes(codeType);
		}
		if (codes_.size() == 0) {
			getNodeCodes(nodeList);
		}
	}
	
	/**
	 * getGroups
	 *
	 * Get a list of groups
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		11/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 *
	 */
	private void getGroups() {
		ApplicationContext ctx = new AnnotationConfigApplicationContext(GroupServiceImpl.class);
		GroupService groupService = ctx.getBean(GroupServiceImpl.class);
		
		List<Groups> groups = groupService.getCreateGroups();
		for (Groups group : groups) {
			codes_.put(group.getId().toString(), group.getGroup_name());
		}
	}
	
	/**
	 * getSelectCodes
	 *
	 * Get a list of select codes
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		11/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param codeType
	 */
	private void getSelectCodes(String codeType) {
		SelectCodeDAO selectCodeDAO = new SelectCodeDAOImpl(SelectCode.class);
		List<String> fieldNames = new ArrayList<String>();
		fieldNames.add(codeType);
		List<SelectCode> selectCodes = selectCodeDAO.getOptionsByNames(fieldNames);
		
		for (SelectCode selectCode : selectCodes) {
			codes_.put(selectCode.getId().getCode(), selectCode.getDescription());
		}
	}
	
	/**
	 * getNodeCodes
	 *
	 * Get a list of codes based on the default value/description pairs
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		11/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param nodeList The node list to get codes for
	 */
	private void getNodeCodes(NodeList nodeList) {
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element elementNode = (Element) node;
				codes_.put(elementNode.getAttribute("value"), elementNode.getAttribute("label"));
			}
		}
	}
	
	/**
	 * formatOptions
	 *
	 * Format the value/description pairs into an option list and make the selected value selected
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		11/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param selectedValue The selected value.
	 * @return The list of value/description as options
	 */
	public String formatOptions(String selectedValue) {
		StringBuilder options = new StringBuilder();
		for (Entry<String, String> entry : codes_.entrySet()) {
			options.append("<option value='");
			options.append(entry.getKey());
			options.append("'");
			if (entry.getKey().equals(selectedValue)) {
				options.append(" selected='selected'");
			}
			options.append(">");
			options.append(entry.getValue());
			options.append("</options>");
		}
		
		return options.toString();
	}
}
