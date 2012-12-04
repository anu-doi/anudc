package au.edu.anu.datacommons.ands.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.ands.check.CollectionCheck;
import au.edu.anu.datacommons.xml.transform.JAXBTransform;

/**
 * RegistryObjectsTest
 * 
 * Australian National University Data Commons
 * 
 * Test cases for the registry objects
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		30/10/2012	Genevieve Turner (GT)	Initial
 * 0.2		04/12/2012	Genevieve Turner (GT)	Updated to comply with rif-cs version 1.4
 * </pre>
 *
 */
public class RegistryObjectsTest {
	static final Logger LOGGER = LoggerFactory.getLogger(RegistryObjectsTest.class);
	private static Validator validator;
	
	/**
	 * setUp
	 *
	 * Set up for the registry objects test cases
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		30/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	/**
	 * tearDown
	 *
	 * Tear down for the registry objects test cases
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		30/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @throws Exception
	 */
	@After
	public void tearDown() throws Exception {
		
	}

	/**
	 * test
	 *
	 * Test a registry objects xml
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		30/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @throws JAXBException
	 */
	@Test
	public void test() throws JAXBException {
		InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("rifcs-example.xml");
		
		JAXBTransform jaxbTransform = new JAXBTransform();
		RegistryObjects registryObjects = (RegistryObjects) jaxbTransform.unmarshalStream(inputStream, RegistryObjects.class);
		
		assertNotNull("There are no registryObjects", registryObjects);
		assertNotNull("There are no registryObject records", registryObjects.getRegistryObjects());
		assertEquals(1, registryObjects.getRegistryObjects().size());
		assertEquals("http://anu.edu.au/test:96", registryObjects.getRegistryObjects().get(0).getKey());
		assertEquals("http://anu.edu.au", registryObjects.getRegistryObjects().get(0).getOriginatingSource());
		assertNotNull("Object type is null", registryObjects.getRegistryObjects().get(0).getObjectType());
		
		ObjectType objectType = registryObjects.getRegistryObjects().get(0).getObjectType();
		if (objectType instanceof Collection) {
			
		}
		else {
			fail("Entity is not a collection");
		}
		
		assertEquals("dataset", registryObjects.getRegistryObjects().get(0).getObjectType().getType());
		
		assertNotNull("object does not have any names", objectType.getNames());
		assertEquals(2, objectType.getNames().size());
		assertEquals("primary",objectType.getNames().get(0).getType());
		assertNotNull("name does not have name parts", objectType.getNames().get(0).getNameParts());
		assertEquals(1,objectType.getNames().get(0).getNameParts().size());
		assertNull(objectType.getNames().get(0).getNameParts().get(0).getType());
		assertEquals("Some Dataset 7",objectType.getNames().get(0).getNameParts().get(0).getValue());
		assertEquals("alternative", objectType.getNames().get(1).getType());
		assertNotNull("name does not have name parts", objectType.getNames().get(1).getNameParts());
		assertEquals(1,objectType.getNames().get(1).getNameParts().size());
		assertNull(objectType.getNames().get(1).getNameParts().get(0).getType());
		assertEquals("Dataset 7",objectType.getNames().get(1).getNameParts().get(0).getValue());
		
		assertNotNull("Object does not have a location", objectType.getLocations());
		assertEquals(1, objectType.getLocations().size());
		assertNotNull("Location address is null", objectType.getLocations().get(0).getAddresses());
		assertEquals(1, objectType.getLocations().get(0).getAddresses().size());
		//TODO update address info
		assertNotNull("No location address type", objectType.getLocations().get(0).getAddresses().get(0).getElectronicAddresses());
		//assertNotNull("No location address type", objectType.getLocations().get(0).getAddresses().get(0).getAddresses());
		assertEquals(1, objectType.getLocations().get(0).getAddresses().get(0).getElectronicAddresses().size());
		
		
		assertEquals("email", objectType.getLocations().get(0).getAddresses().get(0).getElectronicAddresses().get(0).getType());
		assertEquals("test@gmail.com", objectType.getLocations().get(0).getAddresses().get(0).getElectronicAddresses().get(0).getValue());
		
		//Set<ConstraintViolation<RegistryObjects>> constraintViolations = validator.validate(registryObjects, CollectionCheck.class);

		//printViolations(constraintViolations);
		//TODO update the xml
		//assertEquals(2, constraintViolations.size());
	}
	
	/**
	 * testCollection
	 *
	 * Test a Collection
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		30/10/2012	Genevieve Turner(GT)	Initial
	 * 0.2		04/12/2012	Genevieve Turner (GT)	Updated to comply with rif-cs version 1.4
	 * </pre>
	 * 
	 * @throws DatatypeConfigurationException
	 */
	@Test
	public void testCollection() throws DatatypeConfigurationException {
		RegistryObjects registryObjects = new RegistryObjects();
		Set<ConstraintViolation<RegistryObjects>> constraintViolations = validator.validate(registryObjects, CollectionCheck.class);
		assertEquals("Error validating registryObjects", 1, constraintViolations.size());
		
		RegistryObject registryObject = new RegistryObject();
		registryObjects.getRegistryObjects().add(registryObject);
		
		constraintViolations = validator.validate(registryObjects, CollectionCheck.class);
		//printViolations(constraintViolations);
		
		assertEquals("Error validating registryObject", 4, constraintViolations.size());
		
		registryObject.setKey("http://anu.edu.au/test:96");
		registryObject.setGroup("The Australian National University");
		registryObject.setOriginatingSource("http://anu.edu.au/");
		Collection collection = new Collection();
		registryObject.setObjectType(collection);
		
		constraintViolations = validator.validate(registryObjects, CollectionCheck.class);
		//printViolations(constraintViolations);
		
		assertEquals("Error validating collection", 10, constraintViolations.size());
		
		collection.setType("dataset");
		
		constraintViolations = validator.validate(registryObjects, CollectionCheck.class);
		//printViolations(constraintViolations);
		
		assertEquals("Error validating collection type", 9, constraintViolations.size());
		
		Identifier identifier = new Identifier();
		collection.getIdentifiers().add(identifier);
		
		constraintViolations = validator.validate(registryObjects, CollectionCheck.class);
		//printViolations(constraintViolations);
		
		assertEquals("Error validating Identifier", 10, constraintViolations.size());
		
		identifier.setType("local");
		identifier.setValue("test:96");
		
		constraintViolations = validator.validate(registryObjects, CollectionCheck.class);
		//printViolations(constraintViolations);
		
		assertEquals("Error validating identifier contents", 8, constraintViolations.size());
		
		Name name = new Name();
		
		collection.getNames().add(name);
		
		constraintViolations = validator.validate(registryObjects, CollectionCheck.class);
		//printViolations(constraintViolations);
		
		assertEquals("Error validating name", 10, constraintViolations.size());
		
		name.setType("test");
		
		NamePart namePart = new NamePart();
		name.getNameParts().add(namePart);
		
		constraintViolations = validator.validate(registryObjects, CollectionCheck.class);
		//printViolations(constraintViolations);
		
		assertEquals("Error validating namePart", 10, constraintViolations.size());
		
		name.setType("alternative");
		namePart.setType("test");
		namePart.setValue("Alternative Testing");
		
		constraintViolations = validator.validate(registryObjects, CollectionCheck.class);
		//printViolations(constraintViolations);
		
		assertEquals("Error validating namePart contents", 9, constraintViolations.size());
		
		name.setType("primary");
		
		constraintViolations = validator.validate(registryObjects, CollectionCheck.class);
		//printViolations(constraintViolations);
		
		assertEquals("Error validating name type", 8, constraintViolations.size());
		
		namePart.setType("title");
		
		constraintViolations = validator.validate(registryObjects, CollectionCheck.class);
		//printViolations(constraintViolations);
		
		assertEquals("Error validating namePart type", 7, constraintViolations.size());
		
		Location location = new Location();
		collection.getLocations().add(location);
		
		constraintViolations = validator.validate(registryObjects, CollectionCheck.class);
		//printViolations(constraintViolations);
		
		assertEquals("Error validating location", 7, constraintViolations.size());
		
		Address address = new Address();
		location.getAddresses().add(address);
		
		constraintViolations = validator.validate(registryObjects, CollectionCheck.class);
		//printViolations(constraintViolations);
		
		assertEquals("Error validating address", 7, constraintViolations.size());
		
		PhysicalAddress physicalAddress = new PhysicalAddress();
		address.getPhysicalAddresses().add(physicalAddress);
		
		constraintViolations = validator.validate(registryObjects, CollectionCheck.class);
		//printViolations(constraintViolations);
		
		assertEquals("Error validating physical address", 7, constraintViolations.size());
		
		AddressPart addressPart = new AddressPart();
		physicalAddress.getAddressParts().add(addressPart);
		
		constraintViolations = validator.validate(registryObjects, CollectionCheck.class);
		//printViolations(constraintViolations);
		
		assertEquals("Error validating address part", 8, constraintViolations.size());
		
		addressPart.setType("test");
		addressPart.setValue("Hello");
		
		constraintViolations = validator.validate(registryObjects, CollectionCheck.class);
		//printViolations(constraintViolations);
		
		assertEquals("Error validating addressPart contents", 7, constraintViolations.size());
		
		addressPart.setType("addressLine");
		
		constraintViolations = validator.validate(registryObjects, CollectionCheck.class);
		//printViolations(constraintViolations);
		
		assertEquals("Error validating addressPart type", 6, constraintViolations.size());
		
		ElectronicAddress electronicAddress = new ElectronicAddress();
		address.getElectronicAddresses().add(electronicAddress);
		
		constraintViolations = validator.validate(registryObjects, CollectionCheck.class);
		//printViolations(constraintViolations);
		
		assertEquals("Error validating electronic address", 7, constraintViolations.size());
		
		electronicAddress.setValue("http://google.com.au");
		
		constraintViolations = validator.validate(registryObjects, CollectionCheck.class);
		//printViolations(constraintViolations);
		
		assertEquals("Error validating electronic address value", 6, constraintViolations.size());
		
		ElectronicAddressArgument argument = new ElectronicAddressArgument();
		
		electronicAddress.getArgs().add(argument);
		
		constraintViolations = validator.validate(registryObjects, CollectionCheck.class);
		//printViolations(constraintViolations);
		
		assertEquals("Error validating electronic address argument", 9, constraintViolations.size());
		
		argument.setRequired("test");
		argument.setType("test");
		argument.setUse("test");
		
		constraintViolations = validator.validate(registryObjects, CollectionCheck.class);
		//printViolations(constraintViolations);
		
		assertEquals("Error validating argument value", 10, constraintViolations.size());
		
		argument.setRequired("true");
		argument.setType("string");
		argument.setUse("inline");
		
		constraintViolations = validator.validate(registryObjects, CollectionCheck.class);
		//printViolations(constraintViolations);
		
		assertEquals("Error validating argument proper values", 7, constraintViolations.size());
		
		electronicAddress.getArgs().clear();
		
		constraintViolations = validator.validate(registryObjects, CollectionCheck.class);
		//printViolations(constraintViolations);
		
		assertEquals("Error validating no electronic address arguments", 6, constraintViolations.size());
		
		RelatedObject relatedObject = new RelatedObject();
		collection.getRelatedObjects().add(relatedObject);
		
		constraintViolations = validator.validate(registryObjects, CollectionCheck.class);
		//printViolations(constraintViolations);
		
		assertEquals("Error validating related objects", 8, constraintViolations.size());
		
		relatedObject.setKey("http://anu.edu.au/test:1");
		Relation relation = new Relation();
		relatedObject.getRelations().add(relation);
		
		constraintViolations = validator.validate(registryObjects, CollectionCheck.class);
		//printViolations(constraintViolations);
		
		assertEquals("Error validating relation", 7, constraintViolations.size());
		
		relation.setType("isPartOf");
		
		constraintViolations = validator.validate(registryObjects, CollectionCheck.class);
		//printViolations(constraintViolations);
		
		assertEquals("Error validating relation type", 6, constraintViolations.size());
		
		Subject subject = new Subject();
		collection.getSubjects().add(subject);
		
		constraintViolations = validator.validate(registryObjects, CollectionCheck.class);
		//printViolations(constraintViolations);
		
		assertEquals("Error validating subjects", 7, constraintViolations.size());
		
		subject.setType("anzsrc-for");
		subject.setValue("10");
		
		constraintViolations = validator.validate(registryObjects, CollectionCheck.class);
		//printViolations(constraintViolations);
		
		assertEquals("Error validating subject values", 5, constraintViolations.size());
		
		Description description = new Description();
		collection.getDescriptions().add(description);
		
		constraintViolations = validator.validate(registryObjects, CollectionCheck.class);
		//printViolations(constraintViolations);
		
		assertEquals("Error validating description", 7, constraintViolations.size());
		
		description.setType("test");
		description.setValue("Testing Description");
		
		constraintViolations = validator.validate(registryObjects, CollectionCheck.class);
		//printViolations(constraintViolations);
		
		assertEquals("Error validating description values", 6, constraintViolations.size());
		
		description.setType("significanceStatement");
		
		constraintViolations = validator.validate(registryObjects, CollectionCheck.class);
		//printViolations(constraintViolations);
		
		assertEquals("Error validating description type", 5, constraintViolations.size());
		
		description.setType("brief");
		
		constraintViolations = validator.validate(registryObjects, CollectionCheck.class);
		//printViolations(constraintViolations);
		
		assertEquals("Error validating description brief type", 4, constraintViolations.size());
		
		Coverage coverage = new Coverage();
		collection.getCoverage().add(coverage);
		
		constraintViolations = validator.validate(registryObjects, CollectionCheck.class);
		//printViolations(constraintViolations);
		
		assertEquals("Error validating coverage", 4, constraintViolations.size());
		
		Temporal temporal = new Temporal();
		coverage.getTemporalDates().add(temporal);
		
		constraintViolations = validator.validate(registryObjects, CollectionCheck.class);
		//printViolations(constraintViolations);
		
		assertEquals("Error validating temporal coverage", 4, constraintViolations.size());
		
		ANDSDate andsDate = new ANDSDate();
		temporal.setDate(andsDate);
		
		constraintViolations = validator.validate(registryObjects, CollectionCheck.class);
		//printViolations(constraintViolations);
		
		assertEquals("Error validating temporal coverage date", 5, constraintViolations.size());
		
		andsDate.setType("dateFrom");
		andsDate.setDateFormat("");
		XMLGregorianCalendar date = DatatypeFactory.newInstance().newXMLGregorianCalendar();
		andsDate.setValue(date);
		
		constraintViolations = validator.validate(registryObjects, CollectionCheck.class);
		//printViolations(constraintViolations);
		
		assertEquals("Error validating temporal coverage date value", 3, constraintViolations.size());
		
		Rights rights = new Rights();
		collection.getRights().add(rights);
		
		constraintViolations = validator.validate(registryObjects, CollectionCheck.class);
		//printViolations(constraintViolations);
		
		assertEquals("Error validating rights", 3, constraintViolations.size());
		
		RightsSection rightsSection = new RightsSection();
		rights.setAccessRights(rightsSection);
		
		constraintViolations = validator.validate(registryObjects, CollectionCheck.class);
		//printViolations(constraintViolations);
		
		assertEquals("Error validating rights section", 3, constraintViolations.size());
		
		rightsSection.setType("test");
		rightsSection.setValue("Testing Rights Value");
		
		constraintViolations = validator.validate(registryObjects, CollectionCheck.class);
		//printViolations(constraintViolations);
		
		assertEquals("Error validating rights section content", 3, constraintViolations.size());
		
		rightsSection.setType("CC-BY-SA");
		
		constraintViolations = validator.validate(registryObjects, CollectionCheck.class);
		//printViolations(constraintViolations);
		
		assertEquals("Error validating rights section type", 2, constraintViolations.size());
		
		CitationInfo citationInfo = new CitationInfo();
		collection.getCitationInfo().add(citationInfo);
		
		constraintViolations = validator.validate(registryObjects, CollectionCheck.class);
		//printViolations(constraintViolations);
		
		assertEquals("Error validating citation info", 2, constraintViolations.size());
		
		FullCitation fullCitation = new FullCitation();
		citationInfo.setFullCitation(fullCitation);
		
		constraintViolations = validator.validate(registryObjects, CollectionCheck.class);
		//printViolations(constraintViolations);
		
		assertEquals("Error validating full citation", 2, constraintViolations.size());
		
		fullCitation.setValue("Full Citation Value");
		
		constraintViolations = validator.validate(registryObjects, CollectionCheck.class);
		//printViolations(constraintViolations);
		
		assertEquals("Error validating full citation value", 1, constraintViolations.size());
		
		CitationMetadata citationMetadata = new CitationMetadata();
		citationInfo.setCitationMetadata(citationMetadata);
		
		constraintViolations = validator.validate(registryObjects, CollectionCheck.class);
		//printViolations(constraintViolations);
		
		assertEquals("Error validating citation metadata", 6, constraintViolations.size());
		
		Identifier citationIdentifier = new Identifier();
		citationMetadata.setIdentifier(citationIdentifier);
		Contributor contributor = new Contributor();
		citationMetadata.getContributors().add(contributor);
		citationMetadata.setTitle("Citation Title");
		citationMetadata.setPublisher("Australian National University");
		ANDSDate citationDate = new ANDSDate();
		List<ANDSDate> citationDates = Arrays.asList(citationDate);
		citationMetadata.setDates(citationDates);
		
		constraintViolations = validator.validate(registryObjects, CollectionCheck.class);
		//printViolations(constraintViolations);
		
		assertEquals("Error validating citation metadata content", 6, constraintViolations.size());
		
		citationIdentifier.setType("doi");
		citationIdentifier.setValue("test");
		
		NamePart contributorNamePart = new NamePart();
		contributorNamePart.setType("family");
		contributorNamePart.setValue("Tester");
		contributor.getNameParts().add(contributorNamePart);
		
		constraintViolations = validator.validate(registryObjects, CollectionCheck.class);
		//printViolations(constraintViolations);
		
		assertEquals("Error validating citation metadata contributor", 3, constraintViolations.size());
		contributor.getNameParts().add(contributorNamePart);
		
		citationDate.setDateFormat("W3CDTF");
		XMLGregorianCalendar citationDateValue = DatatypeFactory.newInstance().newXMLGregorianCalendar();
		citationDate.setValue(citationDateValue);
		
		//andsDate.setType("dateFrom");
		//andsDate.setDateFormat("");
		//XMLGregorianCalendar date = DatatypeFactory.newInstance().newXMLGregorianCalendar();
		//andsDate.setValue(date);
		
		constraintViolations = validator.validate(registryObjects, CollectionCheck.class);
		//printViolations(constraintViolations);
		
		assertEquals("Error validating citation metadata date", 1, constraintViolations.size());
		contributor.getNameParts().add(contributorNamePart);
		
		Dates dates = new Dates();
		List<Dates> dateList = Arrays.asList(dates);
		
		collection.setDates(dateList);
		
		constraintViolations = validator.validate(registryObjects, CollectionCheck.class);
		//printViolations(constraintViolations);
		
		assertEquals("Error validating citation metadata date", 2, constraintViolations.size());
		
		dates.setType("test");
		
		ANDSDate datesDate = new ANDSDate();
		List<ANDSDate> andsDatesList = Arrays.asList(datesDate);
		
		dates.setDate(andsDatesList);
		
		constraintViolations = validator.validate(registryObjects, CollectionCheck.class);
		//printViolations(constraintViolations);
		
		assertEquals("Error validating citation metadata date", 3, constraintViolations.size());
		
		dates.setType("created");
		
		datesDate.setDateFormat("dateFrom");
		XMLGregorianCalendar datesXMLDate = DatatypeFactory.newInstance().newXMLGregorianCalendar();
		datesDate.setValue(datesXMLDate);
		
		constraintViolations = validator.validate(registryObjects, CollectionCheck.class);
		//printViolations(constraintViolations);
		
		assertEquals("Error validating citation metadata date", 0, constraintViolations.size());
	}
	
	private void printViolations(Set<ConstraintViolation<RegistryObjects>> constraintViolations) {
		LOGGER.info("Violation Set, size: {}", constraintViolations.size());
		Iterator<ConstraintViolation<RegistryObjects>> it = constraintViolations.iterator();
		while (it.hasNext()) {
			ConstraintViolation<RegistryObjects> violation = it.next();
			LOGGER.info("Violation: {}", violation.getMessage());
		}
	}
}
