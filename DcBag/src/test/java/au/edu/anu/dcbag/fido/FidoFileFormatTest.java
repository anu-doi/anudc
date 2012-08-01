package au.edu.anu.dcbag.fido;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import au.edu.anu.dcbag.fido.PronomFormat.MatchStatus;

public class FidoFileFormatTest
{
	private PronomFormat fmt;
	
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
	public void testFidoFileFormatOK()
	{
		fmt = new PronomFormat(
				"OK,251,fmt/20,\"Acrobat PDF 1.6 - Portable Document Format\",\"PDF 1.6\",63647,\"C:\\Rahul\\eBooks\\B,A.pdf\",\"application/pdf\",\"signature\"");
		assertEquals(fmt.getMatchStatus(), MatchStatus.valueOf("OK"));
		// TODO Add assertion of other values.
	}
	
	@Test
	public void testFidoFileFormatKO()
	{
		fmt = new PronomFormat("KO,47,,,,9540095,\"C:\\Rahul\\eBooks\\PHP Manual.chm\",,\"fail\"");
		assertEquals(fmt.getMatchStatus(), MatchStatus.valueOf("KO"));
		// TODO Add assertions.
	}

}
