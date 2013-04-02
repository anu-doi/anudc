package au.edu.anu.datacommons.xslt;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * SelectExtensionTest
 * 
 * Australian National University Data Commons
 * 
 * Test cases for the SelectExtension class
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		18/02/2013	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
public class SelectExtensionTest {
	/**
	 * testGetSelectValue
	 *
	 * Test the retrieval of the select value
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		18/02/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 *
	 */
	@Test
	public void testGetSelectValue() {
		String selectValue = SelectExtension.getSelectValue("anzforSubject", "010101");
		assertEquals("Values are equal equal", "010101 - Algebra and Number Theory", selectValue);
	}

	/**
	 * testGetRelationValue
	 *
	 * Test the retrieval of the relation value
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		18/02/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 *
	 */
	@Test
	public void testGetRelationValue() {
		String relationValue = SelectExtension.getRelationValue("isEnrichedBy");
		assertEquals("Values are equal equal", "Is Enriched By", relationValue);
	}
}
