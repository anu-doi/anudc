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

package au.edu.anu.datacommons.ldap;

import static java.text.MessageFormat.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LdapRequestTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(LdapRequestTest.class);
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSearchUniId() throws Exception {
		LdapRequest req = new LdapRequest();
		LdapPerson person = req.searchUniId("U4465201");
		assertTrue("Family not as expected.", person.getFamilyName().equalsIgnoreCase("khanna"));
		assertTrue("Given name not as expected", person.getGivenName().equalsIgnoreCase("rahul"));
	}

	@Test
	public void testSearchFirstLastname() throws Exception {
		LdapRequest req = new LdapRequest();
		req.setQuery("(&(sn=smith)(givenName=janet*))");
		List<LdapPerson> results = req.search();
		for (LdapPerson p : results) {
			LOGGER.trace("{} {} {}", new Object[]{p.getUniId(), p.getGivenName(), p.getFamilyName()});
		}
		assertThat(results, hasSize(greaterThanOrEqualTo(1)));
	}
	
	@Test
	public void testAuth() {
		LdapRequest req = new LdapRequest();
		boolean authResult = req.authenticate("U4465201", "abc123");
		assertFalse("", authResult);
	}
	
	@Test
	public void testQueryCreation() {
		String lastname = "abc";
		String firstname = "xyz";
		String uid = "u1234567";
		
		LdapRequest req = new LdapRequest();
		String lnPart = req.createQueryPart("sn", lastname);
		assertEquals(format("(sn={0})", lastname), lnPart);
		
		String fnPart = req.createQueryPart("givenName", firstname);
		String queryGrp = req.createQueryGroup("&", lnPart, fnPart);
		String queryGrp2 = req.createQueryPart("uid", uid);
		String complexQueryGrp = req.createQueryGroup("|", queryGrp2, queryGrp);
		
		assertEquals(complexQueryGrp, format("(|(uid={0})(&(sn={1})(givenName={2})))", uid, lastname, firstname));
	}
}
