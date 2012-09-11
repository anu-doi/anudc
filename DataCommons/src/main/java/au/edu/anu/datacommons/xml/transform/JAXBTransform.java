package au.edu.anu.datacommons.xml.transform;

import java.io.InputStream;
import java.io.Writer;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.xml.data.Data;
import au.edu.anu.datacommons.xml.dc.DublinCore;
import au.edu.anu.datacommons.xml.other.OptionList;
import au.edu.anu.datacommons.xml.sparql.Sparql;
import au.edu.anu.datacommons.xml.template.Template;

/**
 * JAXBTransform
 * 
 * Australian National University Data Commons
 * 
 * Helper class that marshals and unmarshals JAXB objects
 * 
 * JUnit coverage:
 * JAXBTransformTest
 * 
 * <pre>
 * Version	Date		Developer			Description
 * 0.1		19/03/2012	Genevieve Turner	Initial build
 * 0.2		23/03/2012	Genevieve Turner	Updated to allow for properties in the marshaller
 * 0.3		11/09/2012	Genevieve Turner	Updated to only initialise the jaxb context once
 * </pre>
 */
public class JAXBTransform {
	static final Logger LOGGER = LoggerFactory.getLogger(JAXBTransform.class);
	private JAXBContext jaxbContext_;
	
	/**
	 * getJAXBContext
	 *
	 * Initialise the JAXBContext
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * X.X		11/09/2012	Genevieve Turner(GT)	Initial
	 * 0.3		11/09/2012	Genevieve Turner	Updated to only initialise the jaxb context once
	 * </pre>
	 * 
	 * @return
	 * @throws JAXBException
	 */
	private JAXBContext getJAXBContext() throws JAXBException {
		if (jaxbContext_ == null) {
			jaxbContext_ = JAXBContext.newInstance(Data.class, Template.class, DublinCore.class, Sparql.class, OptionList.class);
		}
		return jaxbContext_;
	}
	
	/**
	 * unmarshalStream
	 * 
	 * Unmarshal the given InputStream into the type of the given class
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		19/03/2012	Genevieve Turner	Initial build
	 * 0.3		11/09/2012	Genevieve Turner	Updated to only initialise the jaxb context once
	 * </pre>
	 * 
	 * @param transformStream The stream to  transform
	 * @param classToBeBound The class type to transform
	 * @return The unmarshalled object
	 * @throws JAXBException
	 */
	public Object unmarshalStream(InputStream transformStream, Class classToBeBound)
			throws JAXBException {
		JAXBContext jaxbContext = getJAXBContext();
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		return unmarshaller.unmarshal(transformStream);
	}
	
	/**
	 * marshalStream
	 * 
	 * Marshal the given object into the given writer.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		19/03/2012	Genevieve Turner	Initial build
	 * 0.3		11/09/2012	Genevieve Turner	Updated to only initialise the jaxb context once
	 * </pre>
	 * 
	 * @param out The stream to marshal the object in to
	 * @param object The object to marshal
	 * @param classToBeBound The class type of the object
	 * @throws JAXBException
	 */
	public void marshalStream(Writer out, Object object, Class classToBeBound)
			throws JAXBException {
		marshalStream(out, object, classToBeBound, null);
	}
	
	/**
	 * marshalStream
	 * 
	 * Marshal the given object into the given writer.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		19/03/2012	Genevieve Turner	Initial build
	 * 0.2		23/03/2012	Genevieve Turner	Updated to allow for properties in the marshaller
	 * 0.3		11/09/2012	Genevieve Turner	Updated to only initialise the jaxb context once
	 * </pre>
	 * 
	 * @param out The stream to marshal the object in to
	 * @param object The object to marshal
	 * @param classToBeBound The class type of the object
	 * @param properties Additional marshal properties to be used when marshalling the object
	 * @throws JAXBException
	 */
	public void marshalStream(Writer out, Object object, Class classToBeBound, Map<String, Object> properties)
			throws JAXBException {
		JAXBContext jaxbContext = getJAXBContext();
		Marshaller marshaller = jaxbContext.createMarshaller();
		if (properties != null) {
			for (Entry<String, Object> property : properties.entrySet()) {
				marshaller.setProperty(property.getKey(), property.getValue());
			}
		}
		marshaller.marshal(object, out);
	}
}
