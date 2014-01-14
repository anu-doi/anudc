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

import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.data.db.model.Users;

/*
 * UserDAOTest
 *
 * Australian National University Data Commons
 * 
 * Some tests for the UsersDAO
 *
 * JUnit coverage:
 * None
 * 
 * @author Genevieve Turner
 *
 */
public class UsersDAOTest {
	static final Logger LOGGER = LoggerFactory.getLogger(UsersDAOTest.class);
	
	@Test
	public void test() {
		UsersDAO userDAO = new UsersDAOImpl(Users.class);
		LOGGER.info("Search Given Name");
		List<Users> users = userDAO.findUsers("Gen", null, null);
		printUsers(users);
		LOGGER.info("Search Surname");
		users = userDAO.findUsers(null, "T", null);
		printUsers(users);
		LOGGER.info("Search Email");
		users = userDAO.findUsers(null, null, "anu");
		printUsers(users);
		LOGGER.info("Search Surname");
		users = userDAO.findUsers("G", "T", "AN");
		printUsers(users);
	}
	
	private void printUsers(List<Users> users) {
		if (users.size() > 0) {
			for (Users user : users) {
				LOGGER.info("User: {}, {}, {}", user.getId(), user.getDisplayName(), user.getEmail());
			}
		}
		else {
			LOGGER.info("No matching users found");
		}
	}
}
