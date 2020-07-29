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

package au.edu.anu.datacommons.validator;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Iterator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RequiredPropertyValidator
 * 
 * Australian National University Data Commons
 * 
 * Verifies that the given sub field has a value of one of the expected values
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		15/10/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
public class RequiredPropertyValidator implements ConstraintValidator<RequiredProperty, Object> {
	static final Logger LOGGER = LoggerFactory.getLogger(RequiredPropertyValidator.class);
	
	private String subFieldName;
	private String[] expectedValues;
	
	/**
	 * initialize
	 * 
	 * Initialise the sub field names, and the expected values
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		15/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param annotation
	 * @see javax.validation.ConstraintValidator#initialize(java.lang.annotation.Annotation)
	 */
	@Override
	public void initialize(final RequiredProperty annotation) {
		subFieldName = annotation.subFieldName();
		expectedValues = annotation.expectedValues();
	}

	/**
	 * isValid
	 * 
	 * Check that the sub field has at least one instance where the property equals the
	 * expected value
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		15/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param value The object to valiate
	 * @param ctx The context of the validation
	 * @return Whether the field has a required property
	 * @see javax.validation.ConstraintValidator#isValid(java.lang.Object, javax.validation.ConstraintValidatorContext)
	 */
	@Override
	public boolean isValid(Object value, final ConstraintValidatorContext ctx) {
		if (value == null) {
			return true;
		}
		
		boolean hasValue = false;
		
		if (value instanceof Collection) {
			Collection<?> collection = (Collection<?>) value;
			Iterator<?> it = collection.iterator();
			while (!hasValue && it.hasNext()) {
				Object object = it.next();
				try {
					Field field = object.getClass().getDeclaredField(subFieldName);
					field.setAccessible(true);
					Object subObject = field.get(object);
					for (int i = 0; i < expectedValues.length; i++) {
						if (expectedValues[i].equals(subObject)) {
							hasValue = true;
						}
					}
				}
				catch (NoSuchFieldException e) {
					LOGGER.error("Field does not exist", e);
				}
				catch (IllegalAccessException e) {
					LOGGER.error("Sub field does not exist", e);
				}
			}
		}
		else {
			LOGGER.info("It is not a collection type");
		}
		
		return hasValue;
	}
	
}
