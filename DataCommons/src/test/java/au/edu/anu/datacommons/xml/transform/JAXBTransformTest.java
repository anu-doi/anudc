package au.edu.anu.datacommons.xml.transform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import au.edu.anu.datacommons.xml.template.Template;
import au.edu.anu.datacommons.xml.template.TemplateColumn;
import au.edu.anu.datacommons.xml.template.TemplateItem;
import au.edu.anu.datacommons.xml.template.TemplateOption;

/**
 * JAXBTransformTest
 * 
 * Australian National University Data Commons
 * 
 * Test cases for the JAXBTransform class. This class also uses the Template, TemplateColumn, TemplateItem
 * and TemplateOption classes to perform the tests.
 * 
 * Version	Date		Developer			Description
 * 0.1		19/03/2012	Genevieve Turner	Initial build
 * 
 */
public class JAXBTransformTest {
	
	/**
	 * unmarshalTest
	 * 
	 * Tests the unmarshalStream method.
	 * 
	 * Version	Date		Developer			Description
	 * 0.1		13/03/2012	Genevieve Turner	Initial build
	 */
	@Test
	public void unmarshalTest() {
		InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("template.xml");
		assertNotNull("Input Stream is null", inputStream);
		JAXBTransform jaxbTransform = new JAXBTransform();
		Template template = null;
		try {
			template = (Template) jaxbTransform.unmarshalStream(inputStream, Template.class);
		}
		catch (JAXBException e) {
			fail("Failed to unmarshal input stream");
		}
		assertNotNull("Template object is empty", template);
		List<TemplateItem> items = template.getItems();
		assertEquals(items.size(), 3);
		
		TemplateItem templateItem = items.get(0);
		assertEquals("test1", templateItem.getName());
		assertEquals("Test Label 1", templateItem.getLabel());
		assertEquals("TestField", templateItem.getFieldType());
		assertEquals("Test Tooltip", templateItem.getTooltip());
		assertEquals("single", templateItem.getSaveType());
		assertEquals("100", templateItem.getMaxLength());
		assertEquals("disabled", templateItem.getDisabled());
		assertEquals("readonly", templateItem.getReadOnly());
		assertEquals("testPerm", templateItem.getEditPerm());
		assertEquals("required", templateItem.getClassValue());
		assertEquals("testing", templateItem.getDefaultValue());
		
		templateItem = items.get(1);
		
		assertEquals("test2", templateItem.getName());
		assertEquals("Test Label 2", templateItem.getLabel());
		
		List<TemplateOption> templateOptions = templateItem.getTemplateOptions();
		//templateOptions
		
		assertEquals("testopt1", templateOptions.get(0).getValue());
		assertEquals("Test 1", templateOptions.get(0).getLabel());
		assertEquals("testopt2", templateOptions.get(1).getValue());
		assertEquals("Test 2", templateOptions.get(1).getLabel());
		
		templateItem = items.get(2);
		
		List<TemplateColumn> templateColumns = templateItem.getTemplateColumns();
		
		assertEquals("testcol1", templateColumns.get(0).getName());
		assertEquals("Test Col 1", templateColumns.get(0).getLabel());
		assertEquals("TestField", templateColumns.get(0).getFieldType());
		assertEquals("150", templateColumns.get(0).getMaxLength());
		assertEquals("email", templateColumns.get(0).getClassValue());
		assertEquals("testcol2", templateColumns.get(1).getName());
		assertEquals("Test Col 2", templateColumns.get(1).getLabel());
		assertEquals("testopt3", templateColumns.get(1).getTemplateOptions().get(0).getValue());
		assertEquals("Test 3", templateColumns.get(1).getTemplateOptions().get(0).getLabel());
		assertEquals("testopt4", templateColumns.get(1).getTemplateOptions().get(1).getValue());
		assertEquals("Test 4", templateColumns.get(1).getTemplateOptions().get(1).getLabel());
		
//		fail("Not yet implemented");
	}

	/**
	 * marshalTest
	 * 
	 * Tests the marshalStream method.
	 * 
	 * Version	Date		Developer			Description
	 * 0.1		13/03/2012	Genevieve Turner	Initial build
	 */
	@Test
	public void marshalTest() {
		/*Template template = new Template();
		
		TemplateItem templateItem1 = new TemplateItem();
		templateItem1.setName("test1");
		templateItem1.setLabel("Test Label 1");
		templateItem1.setFieldType("TestField");
		templateItem1.setTooltip("Test Tooltip");
		templateItem1.setSaveType("single");
		templateItem1.setMaxLength("100");
		templateItem1.setDisabled("disabled");
		templateItem1.setReadOnly("readonly");
		templateItem1.setEditPerm("testPerm");
		templateItem1.setClassValue("required");
		templateItem1.setDefaultValue("testing");
		
		template.getItems().add(templateItem1);
		
		TemplateItem templateItem2 = new TemplateItem();*/
		//FileUtils
		
		
		//TODO Implement this test
		//fail("Not yet implemented");
	}

	/**
	 * marshalWithPropertiesTest
	 * 
	 * Tests the marshalStream method with some properties values.
	 * 
	 * Version	Date		Developer			Description
	 * 0.1		13/03/2012	Genevieve Turner	Initial build
	 */
	@Test
	public void marshalWithPropertiesTest() {
		//TODO Implement this test
		//fail("Not yet implemented");
	}
}
