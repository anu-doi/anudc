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

import au.edu.anu.datacommons.ands.xml.RegistryObjects;
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
	 * 0.2		11/09/2012	Genevieve Turner(GT)	Initial
	 * 0.3		11/09/2012	Genevieve Turner (GT)	Updated to only initialise the jaxb context once
	 * 0.4		15/10/2012	Genevieve Turner (GT)	Added RegistryObjects.class
	 * </pre>
	 * 
	 * @return
	 * @throws JAXBException
	 */
	private JAXBContext getJAXBContext() throws JAXBException {
		if (jaxbContext_ == null) {
			jaxbContext_ = JAXBContext.newInstance(Data.class, Template.class, DublinCore.class, Sparql.class, OptionList.class, RegistryObjects.class);
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
