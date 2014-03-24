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

package au.edu.anu.datacommons.xml.transform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import au.edu.anu.datacommons.data.db.dao.SelectCodeDAO;
import au.edu.anu.datacommons.data.db.dao.SelectCodeDAOImpl;
import au.edu.anu.datacommons.data.db.model.Groups;
import au.edu.anu.datacommons.data.db.model.SelectCode;
import au.edu.anu.datacommons.security.acl.PermissionService;
import au.edu.anu.datacommons.util.AppContext;

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
 * 0.2		17/09/2012	Genevieve Turner (GT)	Updates to how the ownerGroups are retrieved
 * 0.3		05/12/2012	Genevieve Turner (GT)	Updated so each option has a title, so that Internet Explorer users are able to view the values
 * 0.4		11/12/2012	Genevieve Turner (GT)	Added a title to the option list so that Internet Explorer users can see the whole name
 * 0.5		02/01/2012	Genevieve Turner (GT)	Updated to fix typo of 'options' rather than 'option' on closing tag in formatOptions
 * </pre>
 *
 */
@Configurable(autowire=Autowire.BY_NAME)
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
	 * 0.2		17/09/2012	Genevieve Turner (GT)	Updates to how the ownerGroups are retrieved
	 * </pre>
	 *
	 */
	private void getGroups() {
		ApplicationContext testCtx = AppContext.getApplicationContext();
		PermissionService service = (PermissionService) testCtx.getBean("permissionService");
		
		// It would have been preferable to get the groups from the group service however
		// there are issues with the post filter and this class not being used via a Servlet call
		List<Groups> groups = service.getCreatePermissions();
		
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
		SelectCodeDAO selectCodeDAO = new SelectCodeDAOImpl();
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
	 * 0.3		05/12/2012	Genevieve Turner (GT)	Updated so each option has a title, so that Internet Explorer users are able to view the values
	 * 0.4		11/12/2012	Genevieve Turner (GT)	Added a title to the option list so that Internet Explorer users can see the whole name
	 * 0.5		02/01/2012	Genevieve Turner (GT)	Updated to fix typo of 'options' rather than 'option' on closing tag
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
			options.append("' title='");
			options.append(entry.getValue());
			options.append("'");
			if (entry.getKey().equals(selectedValue)) {
				options.append(" selected='selected'");
			}
			options.append(">");
			options.append(entry.getValue());
			options.append("</option>");
		}
		
		return options.toString();
	}
}
