package au.edu.anu.datacommons.collectionrequest;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class EmailTest
{
	private static final Logger LOGGER = LoggerFactory.getLogger(EmailTest.class);
	
	@Autowired
	JavaMailSenderImpl mailSender;

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
	public void test()
	{
		Email email = new Email(mailSender);
		email.addRecipient("abc1@abc.com", "Abc 1");
		email.addRecipient("abc2@abc.com", "Abc 2");
		email.setSubject("Test Subject");
		email.setBody("Test Body");
		email.send();
		LOGGER.info(mailSender.getHost());
	}
}
