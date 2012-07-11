package au.edu.anu.datacommons.ldap;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class LdapRequestTest
{

	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
	}

	@Before
	public void setUp() throws Exception
	{
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public void testSearchUniId() throws Exception
	{
		LdapRequest req = new LdapRequest();
		LdapPerson person = req.searchUniId("U4465201");
		assertTrue("Family not as expected.", person.getFamilyName().equalsIgnoreCase("khanna"));
		assertTrue("Given name not as expected", person.getGivenName().equalsIgnoreCase("rahul"));
	}

	@Test
	public void testAuth()
	{
		LdapRequest req = new LdapRequest();
		boolean authResult = req.authenticate("U4465201", "abc123");
		assertFalse("", authResult);
	}
}
