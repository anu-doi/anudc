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

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AtLeastOneOfValidator
 * 
 * Australian National University Data Commons
 * 
 * Verifies that there is at least one of the given fields
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
public class AtLeastOneOfValidator implements ConstraintValidator<AtLeastOneOf, Object> {
	static final Logger LOGGER = LoggerFactory.getLogger(AtLeastOneOfValidator.class);
	private String[] fieldNames;

	/**
	 * initialize
	 * 
	 * Initialise the field names
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
	public void initialize(final AtLeastOneOf annotation) {
		fieldNames = annotation.fieldNames();
	}

	/**
	 * isValid
	 * 
	 * Check that there is a value in at least one of the specified fields
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		15/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param value The object to valiate
	 * @param ctx The context of the validation
	 * @return Whether at least one of the fields has contents
	 * @see javax.validation.ConstraintValidator#isValid(java.lang.Object, javax.validation.ConstraintValidatorContext)
	 */
	@Override
	public boolean isValid(Object value, ConstraintValidatorContext ctx) {
		boolean hasValue = false;
		
		for (String fieldName : fieldNames) {
			try {
				Field field = value.getClass().getDeclaredField(fieldName);
				field.setAccessible(true);
				Object fieldValue = field.get(value);
				
				if (fieldValue != null) {
					if (fieldValue instanceof Collection) {
						Collection<?> collectionValue = (Collection<?>) fieldValue;
						if(collectionValue.size() > 0) {
							hasValue = true;
						}
					}
					else {
						hasValue = true;
					}
				}
			}
			catch (NoSuchFieldException e) {
				throw new RuntimeException(e);
			}
			catch(IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
		
		return hasValue;
	}

}
