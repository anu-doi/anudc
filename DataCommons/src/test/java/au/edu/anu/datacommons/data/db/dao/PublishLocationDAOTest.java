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
package au.edu.anu.datacommons.data.db.dao;


import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.data.db.model.FedoraObject;
import au.edu.anu.datacommons.data.db.model.PublishLocation;
import au.edu.anu.datacommons.data.db.model.Template;

/**
 * PublishLocationDAOTest
 *
 * Australian National University Data Commons
 * 
 * Test class for the classes PublishLocationDAO and PublishLocationDAOImpl
 *
 * JUnit coverage:
 * None
 * 
 * @author Genevieve Turner
 *
 */
public class PublishLocationDAOTest {
	static final Logger LOGGER = LoggerFactory.getLogger(FedoraObject.class);
	
	/**
	 * Test if the templates are retrieved with the publish locations
	 */
	@Test
	public void testTemplateRetrieval() {
		PublishLocationDAO publishLocationDAO = new PublishLocationDAOImpl();
		List<PublishLocation> locations = publishLocationDAO.getAllWithTemplates();
		assertNotNull("Publish Locations are null", locations);
		assertNotEquals("No publish locations found", locations.size(), 0);
		for (PublishLocation location : locations) {
			List<Template> templates = location.getTemplates();
			assertNotNull("The templates value for the location " + location.getCode() + " is null", templates);
			assertNotEquals("No templates associated with the publish location " + location.getCode(), templates.size(), 0);
		}
	}
}
